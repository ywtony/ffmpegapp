package com.yw.ffmpeg.harddecoding;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.harddecoding
 * @ClassName: BaseDecoder
 * @Description: 解码器基类
 * @Author: wei.yang
 * @CreateDate: 2021/3/17 9:43
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/3/17 9:43
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public abstract class BaseDecoder implements IDecoder {
    private String filePath;

    public BaseDecoder(String filePath) {
        this.filePath = filePath;
    }

    private static final String TAG = "BaseDecoder";
    //-------------线程相关------------------------
    /**
     * 解码器是否在运行
     */
    private boolean mIsRunning = true;
    /**
     * 线程等待锁
     */
    private Object mLock = new Object();
    /**
     * 是否可以进入解码
     */
    private boolean mReadyForDecode = false;

    //---------------状态相关-----------------------
    /**
     * 音视频解码器
     */
    private MediaCodec mCodec = null;
    /**
     * 音视频数据读取器
     */
    private IExtractor mExtractor = null;
    /**
     * 解码输入缓存区
     */
    private ByteBuffer[] mInputBuffers = null;
    /**
     * 解码输出缓存区
     */
    private ByteBuffer[] mOutputBuffers = null;
    /**
     * 解码数据信息
     */
    private MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();
    /**
     * 状态
     */
    private DecodeState mState = DecodeState.STOP;
    protected IDecoderStateListener mStateListener = null;
    /**
     * 流数据是否结束
     */
    private boolean mIsEOS = false;

    protected int mVideoWidth = 0;

    protected int mVideoHeight = 0;

    private long mDuration = 0;

    private long mStartPos = 0;

    private long mEndPos = 0;

    /**
     * 开始解码时间，用于音视频同步
     */
    private long mStartTimeForSync = -1L;
    // 是否需要音视频渲染同步
    private boolean mSyncRender = true;

    @Override
    public void run() {
        if (mState == DecodeState.STOP) {
            mState = DecodeState.START;
        }
        //准备解码器
        if(mStateListener!=null){
            mStateListener.decoderPrepare(this);
        }

        //初始化并启动解码器
        if (!init()) {
            return;
        }
        Log.i(TAG, "开始解码");
        try {
            while (mIsRunning) {
                if (mState != DecodeState.START &&
                        mState != DecodeState.DECODING &&
                        mState != DecodeState.SEEKING) {
                    Log.i(TAG, "进入等待");
                    waitDecode();
                    //同步时间矫正
                    // ---------【同步时间矫正】-------------
                    //恢复同步的起始时间，即去除等待流失的时间
                    mStartTimeForSync = System.currentTimeMillis() - getCurTimeStamp();

                }

                if (!mIsRunning || mState == DecodeState.STOP) {
                    mIsRunning = false;
                    break;
                }
                if (mStartTimeForSync == -1L) {
                    mStartTimeForSync = System.currentTimeMillis();
                }
                //如果数据没有解码完毕，将数据推入解码器进行解码
                if (!mIsEOS) {
                    //【解码步骤：2. 将数据压入解码器输入缓冲】
                    mIsEOS = pushBufferToDecoder();
                }
                //【解码步骤：3. 将解码好的数据从缓冲区拉取出来】
                int index = pullBufferFromDecoder();
                Log.e(TAG,"Index:"+index);
                if (index >= 0) {
                    // ---------【音视频同步】-------------
                    if (mSyncRender && mState == DecodeState.DECODING) {
                        sleepRender();
                    }
                    //【解码步骤：4. 渲染】
                    if (mSyncRender) {// 如果只是用于编码合成新视频，无需渲染
                        render(mOutputBuffers[index], mBufferInfo);
                    }
                    //将解码数据传递出去
                    Frame frame = new Frame();
                    frame.buffer = mOutputBuffers[index];
                    frame.setBufferInfo(mBufferInfo);
                    if(mStateListener!=null){
                        mStateListener.decodeOneFrame(this, frame);
                    }


                    //【解码步骤：5. 释放输出缓冲】
                    mCodec.releaseOutputBuffer(index, true);
                    if (mState == DecodeState.START) {
                        mState = DecodeState.PAUSE;
                    }
                }
                //【解码步骤：6. 判断解码是否完成】
                if (mBufferInfo.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                    Log.i(TAG, "解码结束");
                    mState = DecodeState.FINISH;
                    if(mStateListener!=null){
                        mStateListener.decoderFinish(this);
                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            doneDecode();
            release();
        }
    }

    private boolean init() {
        if (filePath == null || "".equals(filePath)) {
            Log.e(TAG, "文件路径为空");
            if(mStateListener!=null){
                mStateListener.decoderError(this, "文件路径为空");
            }

            return false;
        }
        if (!check()) {
            return false;
        }
        //初始化数据提取器
        mExtractor = initExtractor(filePath);
        if (mExtractor == null || mExtractor.getFormat() == null) {
            Log.e(TAG, "无法解析文件");
            return false;
        }
        //初始化参数
        if (!initParams()) return false;
        //初始化渲染器
        if (!initRender()) return false;
        //初始化解码器
        if (!initCodec()) {
            return false;
        }
        return true;
    }

    private boolean initParams() {
        try {
            MediaFormat format = mExtractor.getFormat();
            mDuration = format.getLong(MediaFormat.KEY_DURATION) / 1000;
            if (mEndPos == 0L) mEndPos = mDuration;
            initSpecParams(mExtractor.getFormat());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean initCodec() {
        try {
            String type = mExtractor.getFormat().getString(MediaFormat.KEY_MIME);
            mCodec = MediaCodec.createDecoderByType(type);
            if (!configCodec(mCodec, mExtractor.getFormat())) {
                waitDecode();
            }
            mCodec.start();//开始解码
            mInputBuffers = mCodec.getInputBuffers();
            mOutputBuffers = mCodec.getOutputBuffers();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean pushBufferToDecoder() {
        int inputBufferIndex = mCodec.dequeueInputBuffer(1000);
        boolean isEndOfStream = false;
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = mInputBuffers[inputBufferIndex];
            int sampleSize = mExtractor.readBuffer(inputBuffer);
            if (sampleSize < 0) {
                //如果数据已经取完，压入数据结束标志：MediaCodec.BUFFER_FLAG_END_OF_STREAM
                mCodec.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                isEndOfStream = true;
            } else {
                mCodec.queueInputBuffer(inputBufferIndex, 0, sampleSize, mExtractor.getCurrentTimestamp(), 0);
            }
        }
        return isEndOfStream;
    }

    private int pullBufferFromDecoder() {
        // 查询是否有解码完成的数据，index >=0 时，表示数据有效，并且index为缓冲区索引
        int index = mCodec.dequeueOutputBuffer(mBufferInfo, 1000);
        switch (index) {
            case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
            case MediaCodec.INFO_TRY_AGAIN_LATER:
                break;
            case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                mOutputBuffers = mCodec.getOutputBuffers();
                break;
            default:
                return index;
        }
        return -1;
    }

    /**
     * 解码线程进入等待
     */
    private void waitDecode() {
        try {
            if (mState == DecodeState.PAUSE) {
                if(mStateListener!=null){
                    mStateListener.decoderPause(this);
                }

            }
            synchronized (mLock) {
                mLock.wait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sleepRender() {
        long passTime = System.currentTimeMillis() - mStartTimeForSync;
        long curTime = getCurTimeStamp();
        if (curTime > passTime) {
            try {
                Thread.sleep(curTime - passTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void release() {
        try {
            Log.i(TAG, "解码停止，释放解码器");
            mState = DecodeState.STOP;
            mIsEOS = false;
            mExtractor.stop();
            mCodec.stop();
            mCodec.release();
            if(mStateListener!=null){
                mStateListener.decoderDestroy(this);
            }

        } catch (Exception e) {
        }
    }

    /**
     * 当前帧时间
     *
     * @return
     */
    @Override
    public long getCurTimeStamp() {
        return mBufferInfo.presentationTimeUs / 1000;
    }

    /**
     * 通知解码线程继续运行
     */
    protected void notifyDecode() {
        synchronized (mLock) {
            mLock.notifyAll();
        }
        if (mState == DecodeState.DECODING) {
            if(mStateListener!=null){
                mStateListener.decoderRunning(this);
            }

        }
    }

    @Override
    public void pause() {
        mState = DecodeState.DECODING;
    }

    @Override
    public void goOn() {
        mState = DecodeState.DECODING;
        notifyDecode();
    }

    @Override
    public long seekTo(long pos) {
        return 0;
    }

    @Override
    public long seekAndPlay(long pos) {
        return 0;
    }

    @Override
    public void stop() {
        mState = DecodeState.STOP;
        mIsRunning = false;
        notifyDecode();
    }

    @Override
    public boolean isDecoding() {
        return mState == DecodeState.DECODING;
    }

    @Override
    public boolean isSeeking() {
        return mState == DecodeState.SEEKING;
    }

    @Override
    public boolean isStop() {
        return mState == DecodeState.STOP;
    }

    @Override
    public void setSizeListener(IDecoderProgress sizeListener) {

    }

    public void setmStateListener(IDecoderStateListener mStateListener) {
        this.mStateListener = mStateListener;
    }

    @Override
    public int getWidth() {
        return mVideoWidth;
    }

    @Override
    public int getHeight() {
        return mVideoHeight;
    }

    @Override
    public long getDuration() {
        return mDuration;
    }

    @Override
    public int getRotationAngle() {
        return 0;
    }

    @Override
    public MediaFormat getMediaFormat() {
        return mExtractor.getFormat();
    }

    @Override
    public int getTrack() {
        return 0;
    }

    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    public IDecoder withoutSync() {
        mSyncRender = false;
        return this;
    }

    /**
     * 检查子类参数
     *
     * @return
     */
    public abstract boolean check();

    /**
     * 初始化数据提取器
     *
     * @param path 文件路径
     * @return 返回提取器
     */
    public abstract IExtractor initExtractor(String path);

    /**
     * 初始化子类自己特有的参数
     *
     * @param format
     */
    public abstract void initSpecParams(MediaFormat format);

    /**
     * 配置解码器
     *
     * @param mCodec
     * @param format
     * @return
     */
    public abstract boolean configCodec(MediaCodec mCodec, MediaFormat format);

    /**
     * 初始化渲染器
     *
     * @return
     */
    public abstract boolean initRender();

    /**
     * 渲染
     *
     * @param outputBuffer
     * @param info
     */
    public abstract void render(ByteBuffer outputBuffer, MediaCodec.BufferInfo info);

    /**
     * 结束解码
     */
    public abstract void doneDecode();
}

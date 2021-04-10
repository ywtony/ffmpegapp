package com.yw.ffmpeg.harddecoding.encoder;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.provider.Telephony;

import com.yw.ffmpeg.harddecoding.Frame;
import com.yw.ffmpeg.muxer.MMuxer;
import com.yw.ffmpeg.utils.LogUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.harddecoding.encoder
 * @ClassName: BaseEncoder
 * @Description: 基础编码器
 * @Author: wei.yang
 * @CreateDate: 2021/4/10 15:44
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/4/10 15:44
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public abstract class BaseEncoder implements Runnable {
    /**
     * @param mMuxer 音视频封装器
     * @param width  视频的宽度
     * @param height 视频的高度
     * @method 构造函数，对参数进行初始化
     * @description
     * @date: 2021/4/10 15:45
     * @author: wei.yang
     */
    public BaseEncoder(MMuxer mMuxer, int width, int height) {
        this.mMuxer = mMuxer;
        this.width = width;
        this.height = height;
        initCodec();
    }

    private static final String TAG = "BaseEncoder";
    //Mp4合成器
    private MMuxer mMuxer = null;
    //目标视频的宽高，只有在视频编码的时候才有效
    protected int width = -1;
    protected int height = -1;
    //控制线程运行
    private boolean mRunning = true;
    //编码序列帧
    private List<Frame> mFrames = new ArrayList<>();
    //编码器
    private MediaCodec mCodec = null;
    //当前编码帧信息
    private MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();
    //编码输出缓冲区
    private ByteBuffer[] mOutputBuffers = null;
    //编码输入缓冲区
    private ByteBuffer[] mInputBuffers = null;
    //锁
    private Object mLock = new Object();
    //是否编码结束
    private boolean mIsEos = false;
    //编码状态监听器
    private IEncodeStateListener mStateListener;

    /**
     * 初始化编码器
     */
    private void initCodec() {
        try {
            //初始化编码器
            mCodec = MediaCodec.createEncoderByType(encodeType());
            //配置编码器
            configEncoder(mCodec);
            //开始编码
            mCodec.start();
            //初始化输出缓冲区
            mOutputBuffers = mCodec.getOutputBuffers();
            //初始化输入缓冲区
            mInputBuffers = mCodec.getInputBuffers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        loopEncode();
        done();
    }

    /**
     * 循环编码
     */
    private void loopEncode() {
        LogUtil.log("开始编码");
        //控制线程运行&&编码是否结束
        while (mRunning && !mIsEos) {
            boolean empty = mFrames.isEmpty();
            if (empty) {
                justWait();
            }
            if (!mFrames.isEmpty()) {
                Frame frame = mFrames.remove(0);
                if (encodeManually()) {
                    encode(frame);

                } else if (frame.buffer == null) {///    如果是自动编码（比如视频），遇到结束帧时，直接结束掉
                    LogUtil.log("发送编码结束标志");
                    // This may only be used with encoders receiving input from a Surface;
                    mCodec.signalEndOfInputStream();
                    mIsEos = true;
                }
            }
            drain();
        }
    }

    /**
     * 编码
     */
    private void encode(Frame frame) {
        int index = mCodec.dequeueInputBuffer(-1);
        //向编码器输入数据
        if (index > 0) {
            ByteBuffer inputBuffer = mInputBuffers[index];
            inputBuffer.clear();
            if (frame.buffer != null) {
                inputBuffer.put(frame.buffer);
            }
            if (frame.buffer == null || frame.bufferInfo.size <= 0) {//教育等于0时为音频结束标记
                mCodec.queueInputBuffer(index, 0, 0, frame.bufferInfo.presentationTimeUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
            } else {
                frame.buffer.flip();
                frame.buffer.mark();
                mCodec.queueInputBuffer(index, 0, frame.bufferInfo.size, frame.bufferInfo.presentationTimeUs, 0);
            }
            frame.buffer.clear();
        }
    }

    /**
     * 榨干编码输出数据
     */
    private void drain() {
        //如果编码一结束
        while (!mIsEos) {
            int index = mCodec.dequeueOutputBuffer(mBufferInfo, 1000);
            switch (index) {
                case MediaCodec.INFO_TRY_AGAIN_LATER:
                    break;
                case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                    addTrack(mMuxer, mCodec.getOutputFormat());
                    break;
                case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                    mOutputBuffers = mCodec.getOutputBuffers();
                    break;
                default:
                    if (mBufferInfo.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                        mIsEos = true;//编码结束
                        mBufferInfo.set(0, 0, 0, mBufferInfo.flags);
                        LogUtil.log("编码结束");
                    }
                    if (mBufferInfo.flags == MediaCodec.BUFFER_FLAG_CODEC_CONFIG) {
                        // SPS or PPS, which should be passed by MediaFormat.
                        mCodec.releaseOutputBuffer(index, false);

                    }
                    if (!mIsEos) {
                        writeData(mMuxer, mOutputBuffers[index], mBufferInfo);
                    }
                    mCodec.releaseOutputBuffer(index, false);
                    break;
            }
        }
    }

    /**
     * 结束编码释放资源
     */
    private void done() {
        try {
            LogUtil.log("release");
            release(mMuxer);
            mCodec.stop();
            mCodec.release();
            mRunning = false;
            if (mStateListener != null) {
                mStateListener.encodeFinish(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 编码进入等待，等待时间是1s
     */
    private void justWait() {
        try {
            synchronized (mLock) {
                mLock.wait(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通知继续编码
     */
    private void notifyGo() {
        try {
            synchronized (mLock) {
                mLock.notify();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将一帧数据压入队列，等待编码
     */
    public void encodeOneFrame(Frame frame) {
        synchronized (mFrames) {
            mFrames.add(frame);
        }
        notifyGo();
        try {
            //延时一点时间避免掉帧
            Thread.sleep(frameWaitTimeMs());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通知结束编码
     */
    public void endOfStream() {
        synchronized (mFrames) {
            Frame frame = new Frame();
            frame.buffer = null;
            mFrames.add(frame);
            notifyGo();
        }
    }

    /**
     * 设置状态监听器
     *
     * @param iEncodeStateListener
     */
    public void setStateListener(IEncodeStateListener iEncodeStateListener) {
        this.mStateListener = iEncodeStateListener;
    }

    /**
     * 子类配置编码类型
     */
    abstract String encodeType();

    /**
     * @param mCodec 编解码器
     * @description 子类配置编码器
     * @date: 2021/4/10 15:59
     * @author: wei.yang
     */
    abstract void configEncoder(MediaCodec mCodec);

    /**
     * @param mMuxer      mp4和车能起
     * @param mediaFormat 媒体格式
     * @description 配置Mp4音视频轨
     * @date: 2021/4/10 16:00
     * @author: wei.yang
     */
    abstract void addTrack(MMuxer mMuxer, MediaFormat mediaFormat);

    /**
     * @param
     * @description 往Mp4中写入音视频数据
     * @date: 2021/4/10 16:02
     * @author: wei.yang
     */
    abstract void writeData(MMuxer mMuxer, ByteBuffer byteBuffer, MediaCodec.BufferInfo bufferInfo);

    /**
     * 释放子类资源
     *
     * @param mMuxer
     */
    abstract void release(MMuxer mMuxer);

    /**
     * 每一帧排队等待时间
     *
     * @return
     */
    public long frameWaitTimeMs() {
        return 20L;
    }

    /**
     * 是否手动编码
     * 视频：false，true音频
     * ps：视频编码通过Surface，MediaCodec自动完成编码；音频数据需要用户自己压入编码缓冲区完成编码
     */
    public boolean encodeManually() {
        return true;
    }

}

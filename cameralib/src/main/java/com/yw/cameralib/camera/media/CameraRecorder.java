package com.yw.cameralib.camera.media;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.cameralib.camera.media
 * @ClassName: CameraRecoder
 * @Description: 相机录制
 * @Author: wei.yang
 * @CreateDate: 2021/4/14 9:55
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/4/14 9:55
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CameraRecorder implements Runnable {
    private static final String TAG = "CameraRecorder";
    private static final int TIMEOUT_S = 10000;//超时时间
    //视频帧率
    private int mFrameRate = 30;
    //视频码率
    private int mBitRate = 500000;
    //关键帧
    private int mIFrameInterval = 1;
    private long generateIndex = 0;
    private Queue<byte[]> dataQueue;
    private Thread mRecordThread;
    private boolean isRecording;
    private MediaCodec mMediaCodec;
    private MediaCodec.BufferInfo mBufferInfo;
    private byte[] mMediaConfigBytes;

    public CameraRecorder(int width, int height) {
        int formatWidth = width;
        int formatHeight = height;
        if ((formatWidth & 1) == 1) {
            formatWidth--;
        }
        if ((formatHeight & 1) == 1) {
            formatHeight--;
        }
        Log.e("宽高参数：",+formatWidth+"|"+formatHeight);
        dataQueue = new LinkedBlockingDeque<>();
        mRecordThread = new Thread(this);
        isRecording = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            initMediaCodec(width, height);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initMediaCodec(int width, int height) {
        try {
            mBufferInfo = new MediaCodec.BufferInfo();
            mMediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
            MediaFormat mMediaFormat = MediaFormat.createVideoFormat("video/avc", width, height);
            mMediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            //设置色彩格式
//            mMediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, getSupportColorFormat());
            //设置码率
            mMediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 500000);
            //设置帧率
            mMediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, mFrameRate);
            //关键帧
            mMediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, mIFrameInterval);

//            try {
//                configEncoderWidthCQ(mMediaCodec, mMediaFormat);
//            } catch (Exception e) {
//                e.printStackTrace();
//                try {
//                    //捕获异常，设置系统默认编码格式
//                    configEncoderWidthVBR(mMediaCodec, mMediaFormat);
//                } catch (Exception e1) {
//                    e1.printStackTrace();
//                }
//            }
            mMediaCodec.configure(mMediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configEncoderWidthCQ(MediaCodec codec, MediaFormat outputFormat) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //本部手机不支持BITRATE_MODE_CQ模式，有可能会有异常
            outputFormat.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CQ);
        }
    }

    private void configEncoderWidthVBR(MediaCodec codec, MediaFormat outputFormat) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            outputFormat.setInteger(
                    MediaFormat.KEY_BITRATE_MODE,
                    MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_VBR
            );
        }
    }

    /**
     * 开始编码
     */
    public synchronized void start() {
        isRecording = true;
        dataQueue.clear();
        mRecordThread.start();
        startEncode();
    }

    /**
     * 结束编码
     */
    public synchronized void end() {
        isRecording = false;
        notifyAll();
        endEncode();
    }

    /**
     * 向队列中写入数据
     *
     * @param data
     */
    public synchronized void push(byte[] data) {
        dataQueue.offer(data);
        notifyAll();
    }


    @Override
    public synchronized void run() {
        while (true) {
            if (dataQueue.isEmpty()) {
                try {
                    wait();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!isRecording) {
                break;
            }
            byte[] data = dataQueue.poll();
            if (data != null) {
                encode(data);
            }
        }
    }

    private void startEncode() {
        mMediaCodec.start();
        generateIndex = 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void encode(byte[] bytes) {
        int inputIndex = mMediaCodec.dequeueInputBuffer(TIMEOUT_S);
        if (inputIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
            Log.e(TAG, "encode: INFO_OUTPUT_FORMAT_CHANGED");
        } else if (inputIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
            Log.e(TAG, "encode: INFO_TRY_AGAIN_LATER");
        } else {
            long pts = computePresentationTime(generateIndex++);
            ByteBuffer inputBuffer = mMediaCodec.getInputBuffer(inputIndex);
            inputBuffer.clear();
            inputBuffer.put(bytes);
            mMediaCodec.queueInputBuffer(inputIndex, 0, bytes.length, pts, 0);
        }

        int outputIndex = mMediaCodec.dequeueOutputBuffer(mBufferInfo, TIMEOUT_S);
        while (outputIndex > 0) {
            ByteBuffer outputBuffer = mMediaCodec.getOutputBuffer(outputIndex);
            byte[] outputData = new byte[mBufferInfo.size];
            outputBuffer.get(outputData);
            if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {//配置
                Log.e(TAG, "encode: BUFFER_FLAG_CODEC_CONFIG");
                mMediaConfigBytes = new byte[outputData.length];
                mMediaConfigBytes = outputData;
            } else if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_KEY_FRAME) != 0) {//关键帧
                Log.e(TAG, "encode: BUFFER_FLAG_KEY_FRAME");
                if (mMediaConfigBytes != null) {
                    byte[] keyframe = new byte[mBufferInfo.size + mMediaConfigBytes.length];
                    System.arraycopy(mMediaConfigBytes, 0, keyframe, 0, mMediaConfigBytes.length);
                    System.arraycopy(outputData, 0, keyframe, mMediaConfigBytes.length, outputData.length);
                }
            } else if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_PARTIAL_FRAME) != 0) {
                Log.e(TAG, "encode: BUFFER_FLAG_PARTIAL_FRAME");
            } else if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                Log.e(TAG, "encode: BUFFER_FLAG_END_OF_STREAM");
            } else {
                Log.e(TAG, "encode: other " + mBufferInfo.flags);
            }
            mMediaCodec.releaseOutputBuffer(outputIndex, false);
            outputIndex = mMediaCodec.dequeueOutputBuffer(mBufferInfo, TIMEOUT_S);
        }
    }

    private void endEncode() {
        mMediaCodec.stop();
    }

    //计算pts
    private long computePresentationTime(long frameIndex) {
        return 132 + frameIndex * 1000000 / mFrameRate;
    }

    /**
     * 获取手机支持的颜色格式
     * @return 当前手机支持的颜色格式
     */
    private int getSupportColorFormat() {
        int numCodecs = MediaCodecList.getCodecCount();
        MediaCodecInfo codecInfo = null;
        for (int i = 0; i < numCodecs && codecInfo == null; i++) {
            MediaCodecInfo info = MediaCodecList.getCodecInfoAt(i);
            if (!info.isEncoder()) {
                continue;
            }
            String[] types = info.getSupportedTypes();
            boolean found = false;
            for (int j = 0; j < types.length && !found; j++) {
                if (types[j].equals("video/avc")) {
                    Log.d(TAG, "found");
                    found = true;
                }
            }
            if (!found)
                continue;
            codecInfo = info;
        }
        Log.e("AvcEncoder", "Found " + codecInfo.getName() + " supporting " + "video/avc");
        // Find a color profile that the codec supports
        MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType("video/avc");
        Log.e("AvcEncoder",
                "length-" + capabilities.colorFormats.length + "==" + Arrays.toString(capabilities.colorFormats));
        for (int i = 0; i < capabilities.colorFormats.length; i++) {
            Log.d(TAG, "MediaCodecInfo COLOR FORMAT :" + capabilities.colorFormats[i]);
            if ((capabilities.colorFormats[i] == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar) || (capabilities.colorFormats[i] == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar)) {
                Log.e("正确格式",capabilities.colorFormats[i]+"");
                return capabilities.colorFormats[i];
            }
        }
        return MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible;
    }
}

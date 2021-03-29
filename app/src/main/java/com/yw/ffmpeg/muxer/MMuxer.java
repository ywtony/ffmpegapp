package com.yw.ffmpeg.muxer;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Environment;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.muxer
 * @ClassName: MMuxer
 * @Description: 音视频封装器
 * @Author: wei.yang
 * @CreateDate: 2021/3/23 9:50
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/3/23 9:50
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class MMuxer {
    private static final String TAG = "MMUxer";
    /**
     * 多媒体文件路径
     */
    private String mPath = null;
    /**
     * 音视频封装器工具
     */
    private MediaMuxer mMediaMuxer;
    /**
     * 音视频轨的索引
     */
    private int mVideoTrackIndex = -1;
    private int mAudioTrackIndex = -1;
    /**
     * 是否添加音视频轨
     */
    private boolean mIsAudioTrackAdd = false;
    private boolean mIsVideoTrackAdd = false;
    private boolean mIsStart = false;
    private boolean misAudioEnd = false;
    private boolean mIsVideoEnd = false;
    private IMuxerStateListener mStateListener;

    public MMuxer() {
        String fileName = "YW_TONY_YANG" + /*SimpleDateFormat("yyyyMM_dd-HHmmss").format(Date()) +*/ ".mp4";
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        mPath = filePath + fileName;
        try {
            mMediaMuxer = new MediaMuxer(mPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param
     * @return
     * @method 添加视频轨
     * @description
     * @date: 2021/3/23 10:00
     * @author: wei.yang
     */
    public void addVideoTrack(MediaFormat mediaFormat) {
        if (mIsVideoTrackAdd) return;
        if (mMediaMuxer != null) {
            mVideoTrackIndex = mMediaMuxer.addTrack(mediaFormat);
            Log.i(TAG, "添加视频轨道");
            mIsVideoTrackAdd = true;
            startMuxer();
        }
    }

    /**
     * @param
     * @return
     * @method 添加音频轨
     * @description
     * @date: 2021/3/23 10:03
     * @author: wei.yang
     */
    public void addAudioTrack(MediaFormat mediaFormat) {
        if (mIsAudioTrackAdd) return;
        if (mMediaMuxer != null) {
            mAudioTrackIndex = mMediaMuxer.addTrack(mediaFormat);
            Log.i(TAG, "添加音频轨道");
            mIsAudioTrackAdd = true;
            startMuxer();
        }
    }

    public void setNoAudio() {
        if (mIsAudioTrackAdd) return;
        mIsAudioTrackAdd = true;
        misAudioEnd = true;
        startMuxer();
    }

    public void setNoVideo() {
        if (mIsVideoTrackAdd) return;
        mIsAudioTrackAdd = true;
        mIsVideoEnd = true;
        startMuxer();
    }

    /**
     * 写入数据
     */
    public void writeVideoData(ByteBuffer byteBuffer, MediaCodec.BufferInfo bufferInfo) {
        if (mIsStart) {
            mMediaMuxer.writeSampleData(mVideoTrackIndex, byteBuffer, bufferInfo);
        }
    }

    public void writeAudioData(ByteBuffer byteBuffer, MediaCodec.BufferInfo bufferInfo) {
        if (mIsStart) {
            mMediaMuxer.writeSampleData(mAudioTrackIndex, byteBuffer, bufferInfo);
        }
    }

    private void startMuxer() {
        if (mIsVideoTrackAdd && mIsAudioTrackAdd) {
            mMediaMuxer.start();
            mIsStart = true;
            if (mStateListener != null) {
                mStateListener.onMuxerStart();
            }
            Log.i(TAG, "启动封装器");
        }
    }

    public void releaseVideoTrack() {
        mIsVideoEnd = true;
        release();
    }

    public void releaseAudioTrack() {
        misAudioEnd = true;
        release();
    }

    private void release() {
        if (misAudioEnd && mIsVideoEnd) {
            mIsAudioTrackAdd = false;
            mIsVideoTrackAdd = false;
            try {
                mMediaMuxer.stop();
                mMediaMuxer.release();
                Log.i(TAG, "退出封装器");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (mStateListener != null) {
                    mStateListener.onMuxerFinish();
                }
            }
        }
    }

    public void setIMuxerStateListener(IMuxerStateListener listener) {
        this.mStateListener = listener;
    }

    public interface IMuxerStateListener {
        /**
         * 开始封装
         */
        void onMuxerStart();

        /**
         * 封装结束
         */
        void onMuxerFinish();
    }
}

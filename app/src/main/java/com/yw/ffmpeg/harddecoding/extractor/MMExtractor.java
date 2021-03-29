package com.yw.ffmpeg.harddecoding.extractor;

import android.media.MediaExtractor;
import android.media.MediaFormat;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.harddecoding.extractor
 * @ClassName: MMExtractor
 * @Description: 音视频分离器
 * @Author: wei.yang
 * @CreateDate: 2021/3/17 11:13
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/3/17 11:13
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class MMExtractor {
    private String path;
    /**
     * 音视频分离器
     */
    private MediaExtractor mExtractor;
    /**
     * 音频通道索引
     */
    private int mAudioTrack = -1;
    /**
     * 视频通道索引
     */
    private int mVideoTrack = -1;
    /**
     * 当前帧时间戳
     */
    private long mCurSampleTime = 0;
    /**
     * 当前帧标志
     */
    private int mCurSampleFlag = 0;
    /**
     * 开始解码时间点
     */
    private long mStartPos = 0;

    public MMExtractor(String path) {
        this.path = path;
        mExtractor = new MediaExtractor();
        try {
            mExtractor.setDataSource(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取视频格式参数
     */
    public MediaFormat getVideoFormat() {
        for (int i = 0; i < mExtractor.getTrackCount(); i++) {
            MediaFormat mediaFormat = mExtractor.getTrackFormat(i);
            String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("video/")) {
                mVideoTrack = i;
                break;
            }

        }
        return mVideoTrack > 0 ? mExtractor.getTrackFormat(mVideoTrack) : null;
    }

    /**
     * 获取音频格式参数
     */

    public MediaFormat getAudioFormat() {
        for (int i = 0; i < mExtractor.getTrackCount(); i++) {
            MediaFormat mediaFormat = mExtractor.getTrackFormat(i);
            String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("audio/")) {
                mAudioTrack = i;
                break;
            }
        }
        return mAudioTrack >= 0 ? mExtractor.getTrackFormat(mAudioTrack) : null;
    }

    /**
     * 读取视频数据
     */
    public int readBuffer(ByteBuffer byteBuffer) {
        byteBuffer.clear();
        selectSourceTrack();
        int readSampleCount = mExtractor.readSampleData(byteBuffer, 0);
        if (readSampleCount < 0) {
            return -1;
        }
        //记录当前帧的时间戳
        mCurSampleTime = mExtractor.getSampleTime();
        mCurSampleFlag = mExtractor.getSampleFlags();
        //进入下一帧
        mExtractor.advance();
        return readSampleCount;

    }

    /**
     * Seek到指定位置，并返回实际帧的时间戳
     */
    public long seek(long pos) {
        mExtractor.seekTo(pos, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);
        return mExtractor.getSampleTime();
    }

    /**
     * 选择通道
     */
    private void selectSourceTrack() {
        if (mVideoTrack >= 0) {
            mExtractor.selectTrack(mVideoTrack);
        } else if (mAudioTrack >= 0) {
            mExtractor.selectTrack(mAudioTrack);
        }
    }

    /**
     * 停止读取数据
     */
    public void stop() {
        mExtractor.release();
        mExtractor = null;
    }

    public int getVideoTrack() {
        return mVideoTrack;
    }

    public int getAudioTrack() {
        return mAudioTrack;
    }

    public void setStartPos(long pos) {
        mStartPos = pos;
    }

    /**
     * 获取当前帧时间
     */
    public long getCurrentTimestamp() {
        return mCurSampleTime;
    }

    public int getSampleFlag() {
        return mCurSampleFlag;
    }

}

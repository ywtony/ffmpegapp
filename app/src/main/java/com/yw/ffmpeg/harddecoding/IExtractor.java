package com.yw.ffmpeg.harddecoding;

import android.media.MediaFormat;

import java.nio.ByteBuffer;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.harddecoding
 * @ClassName: IExtractor
 * @Description: 音视频分离器定义
 * @Author: wei.yang
 * @CreateDate: 2021/3/17 9:46
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/3/17 9:46
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public interface IExtractor {
    MediaFormat getFormat();
    /**
     * 读取音视频数据
     */
    int readBuffer(ByteBuffer byteBuffer);

    /**
     * 获取当前时间
     * @return
     */
    long getCurrentTimestamp();
    int getSampleFlag();
    /**
     * Seek到指定位置，并返回实际帧的时间戳
     */
    long seek(long pos);

    void setStartPos(long pos);
    /**
     * 停止读取数据
     */
    void stop();
}

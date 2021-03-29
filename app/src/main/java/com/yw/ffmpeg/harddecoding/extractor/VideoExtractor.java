package com.yw.ffmpeg.harddecoding.extractor;

import android.media.MediaFormat;

import com.yw.ffmpeg.harddecoding.IExtractor;

import java.nio.ByteBuffer;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.harddecoding.extractor
 * @ClassName: VideoExtractor
 * @Description: 视频数据提取器
 * @Author: wei.yang
 * @CreateDate: 2021/3/17 11:31
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/3/17 11:31
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class VideoExtractor implements IExtractor {
    private String path;
    private MMExtractor mmExtractor;

    public VideoExtractor(String path) {
        this.path = path;
        mmExtractor = new MMExtractor(path);
    }

    @Override
    public MediaFormat getFormat() {
        return mmExtractor.getVideoFormat();
    }

    @Override
    public int readBuffer(ByteBuffer byteBuffer) {
        return mmExtractor.readBuffer(byteBuffer);
    }

    @Override
    public long getCurrentTimestamp() {
        return mmExtractor.getCurrentTimestamp();
    }

    @Override
    public int getSampleFlag() {
        return mmExtractor.getSampleFlag();
    }

    @Override
    public long seek(long pos) {
        return mmExtractor.seek(pos);
    }

    @Override
    public void setStartPos(long pos) {
        mmExtractor.setStartPos(pos);
    }

    @Override
    public void stop() {
        mmExtractor.stop();
    }
}

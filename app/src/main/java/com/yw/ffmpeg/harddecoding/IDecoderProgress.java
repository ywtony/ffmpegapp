package com.yw.ffmpeg.harddecoding;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.harddecoding
 * @ClassName: IDecoderProgress
 * @Description: 尺寸监听器
 * @Author: wei.yang
 * @CreateDate: 2021/3/12 11:44
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/3/12 11:44
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public interface IDecoderProgress {
    /**
     * 视频宽高回调
     * @param width 视频宽度
     * @param height 视频高度
     * @param rotationAngle 旋转角度
     */
    void videoSizeChange(int width,int height,int rotationAngle);

    /**
     * 视频播放进度回调
     * @param pos 视频进度
     */
    void videoProgressChange(long pos);
}

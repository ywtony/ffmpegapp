package com.yw.ffmpeg.harddecoding;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.harddecoding
 * @ClassName: IDecoder
 * @Description: 解码器接口定义
 * @Author: wei.yang
 * @CreateDate: 2021/3/12 9:42
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/3/12 9:42
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public interface IDecoder extends Runnable {
    /**
     * 暂停解码
     */
    void pause();

    /**
     * 继续解码
     */
    void goOn();

    /**
     * 跳转到指定位置，并返回实际的帧时间
     *
     * @param pos 毫秒
     * @return 实际的时间戳，单位：毫秒
     */
    long seekTo(long pos);

    /**
     * 跳转到指定位置并播放。
     * 并返回实际的时间戳
     * @param pos 毫秒
     * @return 实际时间戳，单位：毫秒
     */
    long seekAndPlay(long pos);

    /**
     * 停止解码
     */
    void stop();

    /**
     * 是否正在解码
     * @return true 是，false否
     */
    boolean isDecoding();

    /**
     * 是否正在快进
     * @return true是，false否
     */
    boolean isSeeking();

    /**
     * 是否停止解码
     * @return true 是，false否
     */
    boolean isStop();

    /**
     * 视频尺寸监听回调
     * @param sizeListener 视频尺寸变化监听器
     */
    void setSizeListener(IDecoderProgress sizeListener);

    /**
     *设置解码状态回调接口
     * @param decodeStateListener 解码状态回调接口
     */
    void setDecodeStateListener(IDecoderStateListener decodeStateListener);
}

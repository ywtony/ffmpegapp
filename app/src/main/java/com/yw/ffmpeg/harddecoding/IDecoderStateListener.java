package com.yw.ffmpeg.harddecoding;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.harddecoding
 * @ClassName: IDecoderStateListener
 * @Description: 解码状态回调接口
 * @Author: wei.yang
 * @CreateDate: 2021/3/12 11:48
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/3/12 11:48
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public interface IDecoderStateListener {
    /**
     * 准备解码
     * @param decoder
     */
    void decoderPrepare(BaseDecoder decoder);

    /**
     * 准备好了解码
     * @param  decoder 解码器
     */
    void decoderReady(BaseDecoder decoder);

    /**
     * 持续解码
     * @param decoder 解码器
     */
    void decoderRunning(BaseDecoder decoder);

    /**
     * 暂停解码
     * @param decoder 解码器
     */
    void decoderPause(BaseDecoder decoder);

    /**
     * 解码一帧
     * @param decoder 解码器
     * @param frame 帧数据
     */
    void decodeOneFrame(BaseDecoder decoder, Frame frame);

    /**
     * 解码结束
     * @param decoder 解码器
     */
    void decoderFinish(BaseDecoder decoder);

    /**
     * 销毁解码器
     * @param decoder 解码器
     */
    void decoderDestroy(BaseDecoder decoder);

    /**
     * 解码错误
     * @param decoder 解码器
     * @param errMsg 错误信息
     */
    void decoderError(BaseDecoder decoder, String errMsg);
}

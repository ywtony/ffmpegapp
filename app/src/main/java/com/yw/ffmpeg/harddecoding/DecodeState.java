package com.yw.ffmpeg.harddecoding;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.harddecoding
 * @ClassName: DecodeState
 * @Description: 解码状态
 * @Author: wei.yang
 * @CreateDate: 2021/3/17 9:52
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/3/17 9:52
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public enum DecodeState {
    /**开始状态*/
    START,
    /**解码中*/
    DECODING,
    /**解码暂停*/
    PAUSE,
    /**正在快进*/
    SEEKING,
    /**解码完成*/
    FINISH,
    /**解码器释放*/
    STOP
}

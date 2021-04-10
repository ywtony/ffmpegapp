package com.yw.ffmpeg.harddecoding.encoder;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.harddecoding.encoder
 * @ClassName: IEncodeStateListener
 * @Description: 编码状态回调接口
 * @Author: wei.yang
 * @CreateDate: 2021/4/10 15:53
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/4/10 15:53
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public interface IEncodeStateListener {
     /**
      * @description 开始编码
      * @date: 2021/4/10 15:55
      * @author: wei.yang
      * @param encoder 编码器
      */
    void encodeStart(BaseEncoder encoder);
    /**
     * @description 编码进度
     * @date: 2021/4/10 15:55
     * @author: wei.yang
     * @param encoder 编码器
     */
    void encodeProgress(BaseEncoder encoder);
    /**
     * @description 结束编码
     * @date: 2021/4/10 15:55
     * @author: wei.yang
     * @param encoder 编码器
     */
    void encodeFinish(BaseEncoder encoder);
}

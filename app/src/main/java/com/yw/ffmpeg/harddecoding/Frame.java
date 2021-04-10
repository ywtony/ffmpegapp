package com.yw.ffmpeg.harddecoding;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;

import java.nio.ByteBuffer;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.harddecoding
 * @ClassName: Frame
 * @Description: 一帧数据
 * @Author: wei.yang
 * @CreateDate: 2021/3/17 9:59
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/3/17 9:59
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class Frame {
    public ByteBuffer buffer=null;
    public MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
    public void setBufferInfo(MediaCodec.BufferInfo bufferInfo){
        this.bufferInfo.set(bufferInfo.offset,bufferInfo.size,bufferInfo.presentationTimeUs,bufferInfo.flags);
    }

}

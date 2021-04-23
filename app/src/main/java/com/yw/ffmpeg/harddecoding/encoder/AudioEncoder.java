package com.yw.ffmpeg.harddecoding.encoder;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;

import com.yw.ffmpeg.muxer.MMuxer;
import com.yw.ffmpeg.utils.LogUtil;

import java.nio.ByteBuffer;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.harddecoding.encoder
 * @ClassName: AudioEncoder
 * @Description: 视频编码器
 * @Author: wei.yang
 * @CreateDate: 2021/4/12 9:52
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/4/12 9:52
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class AudioEncoder extends BaseEncoder {
    //定义音频的采样率
    private static final int DEST_SAMPLE_RATE = 44100;
    //定义默认编码码率
    private static final int DEST_BIT_RATE = 128000;

    /**
     * @param mMuxer 音视频封装器
     * @method 构造函数，对参数进行初始化
     * @description
     * @date: 2021/4/10 15:45
     * @author: wei.yang
     */
    public AudioEncoder(MMuxer mMuxer) {
        super(mMuxer);
    }

    @Override
    String encodeType() {
        return "audio/mp4a-latm";
    }

    @Override
    void configEncoder(MediaCodec mCodec) {
        //设置编码格式为audio/mp4a-latm 采样率：44100，声道数：单声道
        MediaFormat mediaFormat = MediaFormat.createAudioFormat(encodeType(), DEST_SAMPLE_RATE, 1);
        //设置默认码率
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, DEST_BIT_RATE);
        //设置输入缓冲区大小
        mediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 100 * 1024);
        //设置码率模式
        try {
            configEncodeWithCQ(mCodec, mediaFormat);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                configEncodeWidthVBR(mCodec, mediaFormat);
            } catch (Exception e1) {
                e1.printStackTrace();
                LogUtil.log("配置音频编码器失败");
            }
        }
    }

    private void configEncodeWithCQ(MediaCodec codec, MediaFormat outputFormat) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 本部分手机不支持 BITRATE_MODE_CQ 模式，有可能会异常
            outputFormat.setInteger(
                    MediaFormat.KEY_BITRATE_MODE,
                    MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CQ
            );
        }
        codec.configure(outputFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
    }

    private void configEncodeWidthVBR(MediaCodec codec, MediaFormat outputFormat) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            outputFormat.setInteger(
                    MediaFormat.KEY_BITRATE_MODE,
                    MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_VBR
            );
        }
        codec.configure(outputFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
    }

    @Override
    void addTrack(MMuxer mMuxer, MediaFormat mediaFormat) {
        mMuxer.addAudioTrack(mediaFormat);
    }

    @Override
    void writeData(MMuxer mMuxer, ByteBuffer byteBuffer, MediaCodec.BufferInfo bufferInfo) {
        mMuxer.writeAudioData(byteBuffer, bufferInfo);
    }

    @Override
    void release(MMuxer mMuxer) {
        mMuxer.releaseAudioTrack();
    }
}

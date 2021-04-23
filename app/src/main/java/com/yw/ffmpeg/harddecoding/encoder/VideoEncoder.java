package com.yw.ffmpeg.harddecoding.encoder;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.provider.MediaStore;
import android.view.Surface;

import com.yw.ffmpeg.muxer.MMuxer;

import java.nio.ByteBuffer;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.harddecoding.encoder
 * @ClassName: VideoEncoder
 * @Description: 视频编码器
 * @Author: wei.yang
 * @CreateDate: 2021/4/12 9:23
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/4/12 9:23
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class VideoEncoder extends BaseEncoder {
    //默认编码帧率
    private static final int DEFAULT_ENCODE_FRAME_RATE = 30;
    //媒体数据缓冲容器
    private Surface surface;

    /**
     * @param mMuxer 音视频封装器
     * @param width  视频的宽度
     * @param height 视频的高度
     * @method 构造函数，对参数进行初始化
     * @description
     * @date: 2021/4/10 15:45
     * @author: wei.yang
     */
    public VideoEncoder(MMuxer mMuxer, int width, int height) {
        super(mMuxer, width, height);
    }

    /**
     * 定义视频编码类型
     *
     * @return
     */
    @Override
    String encodeType() {
        return "video/avc";
    }

    /**
     * 配置视频编码器
     *
     * @param mCodec 编解码器
     */
    @Override
    void configEncoder(MediaCodec mCodec) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Encode with or height is invalid,width:" + width + ",height:" + height);
        }
        //https://segmentfault.com/a/1190000021223837?utm_source=tag-newest
        //Biterate = Width * Height * FrameRate * Factor
        //
        //Factor: 0.1~0.2
        int bitrate = 3 * width * height;//视频码率
        //创建视频媒体格式
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(encodeType(), width, height);
        //设置视频码率
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitrate);
        //设置视频帧率
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, DEFAULT_ENCODE_FRAME_RATE);
        //配置关键帧出现频率
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
        //配置码率模式
        //- BITRATE_MODE_CQ 忽略用户设置的码率，由编码器自己控制码率，并尽可能保证画面清晰度和码率的均衡
        //      - BITRATE_MODE_CBR 无论视频的画面内容如果，尽可能遵守用户设置的码率
        //    - BITRATE_MODE_VBR 尽可能遵守用户设置的码率，但是会根据帧画面之间运动矢量
        //（通俗理解就是帧与帧之间的画面变化程度）来动态调整码率，如果运动矢量较大，则在该时间段将码率调高，如果画面变换很小，则码率降低。
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        try {
            configEncoderWidthCQ(mCodec, mediaFormat);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                //捕获异常，设置系统默认编码格式
                configEncoderWidthVBR(mCodec, mediaFormat);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        //从MediaCodec中创建一个Surface
        surface = mCodec.createInputSurface();

    }

    private void configEncoderWidthCQ(MediaCodec codec, MediaFormat outputFormat) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //本部手机不支持BITRATE_MODE_CQ模式，有可能会有异常
            outputFormat.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CQ);
        }
        codec.configure(outputFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
    }

    private void configEncoderWidthVBR(MediaCodec codec, MediaFormat outputFormat) {
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
        mMuxer.addVideoTrack(mediaFormat);
    }

    @Override
    void writeData(MMuxer mMuxer, ByteBuffer byteBuffer, MediaCodec.BufferInfo bufferInfo) {
        mMuxer.writeVideoData(byteBuffer, bufferInfo);
    }

    @Override
    void release(MMuxer mMuxer) {
        mMuxer.releaseVideoTrack();
    }

    @Override
    public boolean encodeManually() {
        return false;
    }

    /**
     * 获取Surface用于展示画面
     *
     * @return
     */
    public Surface getEncodeSurface() {
        return surface;
    }

}

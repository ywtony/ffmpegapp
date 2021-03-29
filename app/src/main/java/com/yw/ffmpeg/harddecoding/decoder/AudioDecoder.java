package com.yw.ffmpeg.harddecoding.decoder;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaFormat;

import com.yw.ffmpeg.harddecoding.BaseDecoder;
import com.yw.ffmpeg.harddecoding.IDecoderStateListener;
import com.yw.ffmpeg.harddecoding.IExtractor;
import com.yw.ffmpeg.harddecoding.extractor.AudioExtractor;

import java.nio.ByteBuffer;

/**
 *
 *
 *
 *
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.harddecoding.decoder
 * @ClassName: AudioDecoder
 * @Description: 音频解码器
 * @Author: wei.yang
 * @CreateDate: 2021/3/17 13:09
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/3/17 13:09
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class AudioDecoder extends BaseDecoder {
    /**
     * 采样率
     */
    private int mSampleRate = -1;

    /**
     * 声音通道数量
     */
    private int mChannels = 1;
    /**
     * PCM采样位数
     */
    private int mPCMEncodeBit = AudioFormat.ENCODING_PCM_16BIT;
    /**
     * 音频播放器
     */
    private AudioTrack mAudioTrack;
    /**
     * 音频数据缓存
     */
    private short[] mAudioOutTempBuf = null;

    public AudioDecoder(String filePath) {
        super(filePath);
    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    public IExtractor initExtractor(String path) {
        return new AudioExtractor(path);
    }

    @Override
    public void initSpecParams(MediaFormat format) {
        try {
            mChannels = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
            mSampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
            //如果没有这个参数，默认为16位采样
            mPCMEncodeBit = format.containsKey(MediaFormat.KEY_PCM_ENCODING) ? format.getInteger(MediaFormat.KEY_PCM_ENCODING) : AudioFormat.ENCODING_PCM_16BIT;

        } catch (Exception e) {
        }
    }

    @Override
    public boolean configCodec(MediaCodec mCodec, MediaFormat format) {
        mCodec.configure(format, null, null, 0);
        return true;
    }

    @Override
    public boolean initRender() {
        //1单声道，else双声道
        int channel = mChannels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO;
        //获取最小缓冲区
        int minBufferSize = AudioTrack.getMinBufferSize(mSampleRate, channel, mPCMEncodeBit);

        mAudioOutTempBuf = new short[minBufferSize / 2];

        mAudioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,//播放类型：音乐
                mSampleRate, //采样率
                channel, //通道
                mPCMEncodeBit, //采样位数
                minBufferSize, //缓冲区大小
                AudioTrack.MODE_STREAM); //播放模式：数据流动态写入，另一种是一次性写入
        //播放音频
        mAudioTrack.play();
        return true;
    }

    @Override
    public void render(ByteBuffer outputBuffer, MediaCodec.BufferInfo info) {
        if (mAudioOutTempBuf.length < info.size / 2) {
            mAudioOutTempBuf = new short[info.size / 2];
        }
        outputBuffer.position(0);
        outputBuffer.asShortBuffer().get(mAudioOutTempBuf, 0, info.size / 2);
        mAudioTrack.write(mAudioOutTempBuf, 0, info.size / 2);
    }

    @Override
    public void doneDecode() {
        mAudioTrack.stop();
        mAudioTrack.release();
    }

    @Override
    public void setDecodeStateListener(IDecoderStateListener decodeStateListener) {

    }
}

package com.yw.ffmpeg.muxer;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;

import com.yw.ffmpeg.harddecoding.extractor.AudioExtractor;
import com.yw.ffmpeg.harddecoding.extractor.VideoExtractor;

import java.nio.ByteBuffer;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.muxer
 * @ClassName: Mp4Repack
 * @Description: Mp4重打包工具
 * @Author: wei.yang
 * @CreateDate: 2021/3/23 10:13
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/3/23 10:13
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class Mp4Repack {
    private static final String TAG ="Mp4Repack";
    private String path;
    private AudioExtractor mAudioExtractor;
    private VideoExtractor mVideoExtractor;
    private MMuxer mMuxer;
    public Mp4Repack(String path){
        this.path  = path;
        mAudioExtractor = new AudioExtractor(path);
        mVideoExtractor = new VideoExtractor(path);
        mMuxer = new MMuxer();
    }

    public void start(){
        MediaFormat audioFormat = mAudioExtractor.getFormat();
        MediaFormat videoFormat = mVideoExtractor.getFormat();
        if(audioFormat!=null){
            mMuxer.addAudioTrack(audioFormat);
        }else{
            mMuxer.setNoAudio();
        }
        if(videoFormat!=null){
            mMuxer.addVideoTrack(videoFormat);
        }else{
            mMuxer.setNoVideo();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                ByteBuffer byteBuffer = ByteBuffer.allocate(500*1024);
                MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                if(audioFormat!=null){
                    int size = mAudioExtractor.readBuffer(byteBuffer);
                    while(size>0){
                        bufferInfo.set(0,size,mAudioExtractor.getCurrentTimestamp(),mAudioExtractor.getSampleFlag());
                        mMuxer.writeAudioData(byteBuffer,bufferInfo);
                        size = mAudioExtractor.readBuffer(byteBuffer);
                    }
                }
                if(videoFormat!=null){
                    int size = mVideoExtractor.readBuffer(byteBuffer);
                    while (size>0){
                        bufferInfo.set(0,size,mVideoExtractor.getCurrentTimestamp(),mVideoExtractor.getSampleFlag());
                        mMuxer.writeVideoData(byteBuffer,bufferInfo);
                        size = mVideoExtractor.readBuffer(byteBuffer);
                    }
                }
                mAudioExtractor.stop();
                mVideoExtractor.stop();
                mMuxer.releaseAudioTrack();
                mMuxer.releaseVideoTrack();
                Log.i(TAG, "MP4 重打包完成");
            }
        }){}.start();
    }



}

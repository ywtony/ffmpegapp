package com.yw.ffmpeg.harddecoding.decoder;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.yw.ffmpeg.harddecoding.BaseDecoder;
import com.yw.ffmpeg.harddecoding.IDecoderStateListener;
import com.yw.ffmpeg.harddecoding.IExtractor;
import com.yw.ffmpeg.harddecoding.extractor.VideoExtractor;

import java.nio.ByteBuffer;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.harddecoding.decoder
 * @ClassName: VideoDecoder
 * @Description: 视频解码器
 * @Author: wei.yang
 * @CreateDate: 2021/3/17 11:39
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/3/17 11:39
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class VideoDecoder extends BaseDecoder {
    private static String TAG = "VideoDecoder";
    private SurfaceView surfaceView;
    private Surface surface;

    public VideoDecoder(String filePath, SurfaceView surfaceView, Surface surface) {
        super(filePath);
        this.surfaceView = surfaceView;
        this.surface = surface;
    }

    @Override
    public boolean check() {
        if (surfaceView == null && surface == null) {
            Log.w(TAG, "SurfaceView和Surface都为空，至少需要一个不为空");
            mStateListener.decoderError(this, "显示器为空");
            return false;
        }
        return true;
    }

    @Override
    public IExtractor initExtractor(String path) {
        return new VideoExtractor(path);
    }

    @Override
    public void initSpecParams(MediaFormat format) {

    }

    @Override
    public boolean configCodec(final MediaCodec mCodec, final MediaFormat format) {
        if (surface != null) {
            mCodec.configure(format, surface, null, 0);
            notifyDecode();
        } else if (surfaceView.getHolder().getSurface() != null) {
            surface = surfaceView.getHolder().getSurface();
            configCodec(mCodec, format);
        } else {
            surfaceView.getHolder().addCallback(new SurfaceHolder.Callback2() {
                @Override
                public void surfaceRedrawNeeded(@NonNull SurfaceHolder holder) {

                }

                @Override
                public void surfaceCreated(@NonNull SurfaceHolder holder) {
                    surface = holder.getSurface();
                    configCodec(mCodec, format);
                }

                @Override
                public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

                }
            });

            return false;
        }
        return true;
    }

    @Override
    public boolean initRender() {
        return true;
    }

    @Override
    public void render(ByteBuffer outputBuffer, MediaCodec.BufferInfo info) {

    }

    @Override
    public void doneDecode() {

    }

    @Override
    public void setDecodeStateListener(IDecoderStateListener decodeStateListener) {

    }
}

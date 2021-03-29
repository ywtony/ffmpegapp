package com.yw.ffmpeg;

import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.yw.ffmpeg.harddecoding.BaseDecoder;
import com.yw.ffmpeg.harddecoding.Frame;
import com.yw.ffmpeg.harddecoding.IDecoderStateListener;
import com.yw.ffmpeg.harddecoding.decoder.AudioDecoder;
import com.yw.ffmpeg.harddecoding.decoder.VideoDecoder;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 硬件编码播放器：ps：音频播放不出来
 *
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg
 * @ClassName: MediaCodecActivity
 * @Description: 音视频硬件解码步骤：
 * 1.初始化并启动解码器
 * 2.将数据压入解码器输入缓冲
 * 3.将解码好的数据从缓冲区中拉取出来
 * 4.渲染
 * 5.释放输出缓冲
 * 6.判断解码是否完成
 * 7.释放解码器
 * @Author: wei.yang
 * @CreateDate: 2021/3/17 9:38
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/3/17 9:38
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class MediaCodecActivity extends BaseActivity {
    private String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/VID_20210306_17293827.mp4";
    private SurfaceView surfaceView;
    private Button btnChoiceVideo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_codec);
        surfaceView = findViewById(R.id.surfaceView);
        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;
            }
        });
        btnChoiceVideo = findViewById(R.id.btnChoiceVideo);
        btnChoiceVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choiceVideo();
            }
        });
    }

    @Override
    public void videoPathCallback(String vidoPath) {
        filePath = vidoPath;
        initPlayer();
    }

    private void initPlayer() {
        //创建线程池
        Executor executor = Executors.newFixedThreadPool(2);
        //创建视频解码器
        VideoDecoder videoDecoder = new VideoDecoder(filePath, surfaceView, null);
        executor.execute(videoDecoder);

        //创建音频解码器
        AudioDecoder audioDecoder = new AudioDecoder(filePath);
        executor.execute(audioDecoder);
        //开启播放
        videoDecoder.goOn();
        audioDecoder.goOn();
    }
}

package com.yw.ffmpeg;

import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.yw.ffmpeg.harddecoding.decoder.AudioDecoder;
import com.yw.ffmpeg.harddecoding.decoder.VideoDecoder;
import com.yw.ffmpeg.opengles.BitmapDrawer;
import com.yw.ffmpeg.opengles.IDrawer;
import com.yw.ffmpeg.opengles.SimpleRender;
import com.yw.ffmpeg.opengles.TriangleDrawer;
import com.yw.ffmpeg.opengles.VideoDrawer;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg
 * @ClassName: OpenGlEsPlayerActivity
 * @Description: 使用opengles渲染播放器
 * @Author: wei.yang
 * @CreateDate: 2021/3/23 10:43
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/3/23 10:43
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class OpenGlEsPlayerActivity extends BaseActivity {
    private Button btnChoice;
    private GLSurfaceView glSurfaceView;
    private VideoDrawer videoDrawer;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opengl_es_player);
        btnChoice = findViewById(R.id.btnChoice);
        glSurfaceView = findViewById(R.id.glSurfaceView);
        btnChoice.setOnClickListener(v -> {
            choiceVideo();
        });
        videoDrawer = new VideoDrawer();
        initRender(videoDrawer);
//        initRender(new BitmapDrawer(BitmapFactory.decodeResource(getResources(), R.drawable.girl)));
    }

    private void initRender(IDrawer drawer) {
        glSurfaceView.setEGLContextClientVersion(2);
        SimpleRender render = new SimpleRender();
        render.addDrawer(drawer);
        glSurfaceView.setRenderer(render);
    }

    @Override
    public void videoPathCallback(String vidoPath) {
        initPlayer(vidoPath);
    }

    private void initPlayer(String filePath) {
        //创建线程池
        Executor executor = Executors.newFixedThreadPool(10);
        //创建视频解码器
        if(videoDrawer.getSurfaceTexture()!=null){
            Log.e("TAG","不为空");
        }else{
            Log.e("为空","不为空");
        }
        VideoDecoder videoDecoder = new VideoDecoder(filePath, null, new Surface(videoDrawer.getSurfaceTexture()));
        executor.execute(videoDecoder);

        //创建音频解码器
        AudioDecoder audioDecoder = new AudioDecoder(filePath);
        executor.execute(audioDecoder);
        //开启播放
        videoDecoder.goOn();
        audioDecoder.goOn();
    }
}

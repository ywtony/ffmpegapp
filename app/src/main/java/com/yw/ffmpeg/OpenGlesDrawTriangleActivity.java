package com.yw.ffmpeg;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.yw.ffmpeg.opengles.IDrawer;
import com.yw.ffmpeg.opengles.SimpleRender;
import com.yw.ffmpeg.opengles.TriangleDrawer;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg
 * @ClassName: OpenGlesDrawTriangleActivity
 * @Description: 使用opengles绘制三角形
 * @Author: wei.yang
 * @CreateDate: 2021/3/23 16:54
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/3/23 16:54
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class OpenGlesDrawTriangleActivity extends BaseActivity {
    private Button btnChoice;
    private GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opengl_es_player);
        btnChoice = findViewById(R.id.btnChoice);
        glSurfaceView = findViewById(R.id.glSurfaceView);
        btnChoice.setOnClickListener(v -> {
            choiceVideo();
        });
        initRender(new TriangleDrawer());
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

    }
}

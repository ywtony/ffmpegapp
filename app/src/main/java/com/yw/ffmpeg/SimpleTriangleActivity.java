package com.yw.ffmpeg;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.SurfaceView;

import androidx.annotation.Nullable;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg
 * @ClassName: SimpleTriangleActivity
 * @Description: 简单三角形测试案例
 * @Author: wei.yang
 * @CreateDate: 2021/4/2 14:01
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/4/2 14:01
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class SimpleTriangleActivity extends BaseActivity{
//    private GLSurfaceView surfaceView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_triangle);
//        surfaceView = findViewById(R.id.surfaceView);
//        initSurfaceView();
    }
    private void initSurfaceView(){
        //设置版本
        //设置渲染器
    }

    @Override
    public void videoPathCallback(String vidoPath) {

    }
}

package com.yw.ffmpeg;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.yw.ffmpeg.adapter.BaseListAdapter;
import com.yw.ffmpeg.adapter.MainAdapter;
import com.yw.ffmpeg.bean.ClassBean;
import com.yw.ffmpeg.utils.ActivityUtils;

import java.util.ArrayList;
import java.util.List;

import camera.SurfaceCameraMp4Activity;
import camera.SurfaceViewCamera2PreviewActivity;
import camera.TextureViewCamera2PreviewActivity;

public class MainActivity extends FragmentActivity {
    private ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }


    private void initViews() {
        listView = findViewById(R.id.listView);
        List list = new ArrayList<ClassBean>();
        list.add(new ClassBean("简单的FFMPEG播放器", FFmpegSimpleActivity.class.getName()));
        list.add(new ClassBean("C++调用java代码", CJVMActivity.class.getName()));
        list.add(new ClassBean("硬解码实现简单播放器", MediaCodecActivity.class.getName()));
        list.add(new ClassBean("生成一个Mp4文件", GenerateMp4Activity.class.getName()));
        list.add(new ClassBean("绘制一个三角形", OpenGlesDrawTriangleActivity.class.getName()));
        list.add(new ClassBean("绘制一个图片", OpenGlesDrawBitmapActivity.class.getName()));
        list.add(new ClassBean("opengles播放器", OpenGlEsPlayerActivity.class.getName()));
        list.add(new ClassBean("使用opengles渲染一个简单的三角形", SimpleTriangleActivity.class.getName()));
        list.add(new ClassBean("EGL小案例", EGLSimpleActivity.class.getName()));
        list.add(new ClassBean("SurfaceView摄像头预览", SurfaceViewCamera2PreviewActivity.class.getName()));
        list.add(new ClassBean("TextureView摄像头预览", TextureViewCamera2PreviewActivity.class.getName()));
        list.add(new ClassBean("Camera相机拍照并预览编码保存", SurfaceCameraMp4Activity.class.getName()));

        MainAdapter adapter = new MainAdapter(this, list, R.layout.item_main, new BaseListAdapter.OnListItemClickListener<ClassBean>() {

            @Override
            public void onItemClick(ClassBean data, int position) {
                ActivityUtils.getInstance().startActivity(MainActivity.this, data.getClassName());
            }
        });
        listView.setAdapter(adapter);
    }


}
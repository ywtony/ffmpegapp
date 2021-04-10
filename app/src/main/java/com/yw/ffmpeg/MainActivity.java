package com.yw.ffmpeg;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.yw.ffmpeg.adapter.BaseListAdapter;
import com.yw.ffmpeg.adapter.MainAdapter;
import com.yw.ffmpeg.bean.ClassBean;
import com.yw.ffmpeg.utils.ActivityUtils;

import java.util.ArrayList;
import java.util.List;

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


        MainAdapter adapter = new MainAdapter(this, list, R.layout.item_main, new BaseListAdapter.OnListItemClickListener<ClassBean>() {

            @Override
            public void onItemClick(ClassBean data, int position) {
                ActivityUtils.getInstance().startActivity(MainActivity.this, data.getClassName());
            }
        });
        listView.setAdapter(adapter);
    }


}
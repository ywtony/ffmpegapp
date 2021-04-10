package com.yw.ffmpeg;

import android.os.Bundle;

import androidx.annotation.Nullable;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg
 * @ClassName: SimpleImageActivity
 * @Description: 渲染一张图片
 * @Author: wei.yang
 * @CreateDate: 2021/4/2 14:52
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/4/2 14:52
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class SimpleImageActivity extends BaseActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_image);
    }

    @Override
    public void videoPathCallback(String vidoPath) {

    }
}

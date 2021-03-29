package com.yw.ffmpeg;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.yw.ffmpeg.muxer.Mp4Repack;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg
 * @ClassName: GenerateMp4Activity
 * @Description: 生成Mp4文件
 * @Author: wei.yang
 * @CreateDate: 2021/3/23 9:44
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/3/23 9:44
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class GenerateMp4Activity extends BaseActivity{
    private Button btnChoice;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_mp4);
        btnChoice = findViewById(R.id.btnChoice);
        btnChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choiceVideo();
            }
        });
    }

    /**
     * 选择视频成功后的回调函数
     * @param vidoPath
     */
    @Override
    public void videoPathCallback(String vidoPath) {
        //将视频重新打包成mp4
        new Mp4Repack(vidoPath).start();
    }
}

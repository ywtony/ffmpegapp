package com.yw.ffmpeg;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.yw.ffmpeg.media.MySimpleMediaPlayer;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg
 * @ClassName: FFmpegSimpleActivity
 * @Description: java类作用描述
 * @Author: wei.yang
 * @CreateDate: 2021/3/23 9:22
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/3/23 9:22
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class FFmpegSimpleActivity  extends BaseActivity {
    // Used to load the 'native-lib' library on application startup.
    ///storage/emulated/0/Pictures/QQ/1612157802690xq.mp4
    ///storage/emulated/0/Android/data/com.uns.uu/files/Movies/VID_C27BFE83D694FF0ABFFDC241C4F24F.mp4
//    private String filePath = "/storage/emulated/0/Pictures/QQ/1612157802690xq.mp4";
    private String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/VID_20210306_17293827.mp4";
    private MySimpleMediaPlayer player = null;
    private Button btn;
    private SurfaceView surfaceView;
    private int playerId = -1;
    private String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ffmpeg_simple);
        surfaceView = findViewById(R.id.surfaceView);
        btn  = findViewById(R.id.btnChoice);
        player = new MySimpleMediaPlayer();

        requestMyPermissions();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choiceVideo();
            }
        });
    }


    @Override
    public void videoPathCallback(String vidoPath) {
        filePath = vidoPath;
        openVideo();
    }

    private void openVideo(){
        if (filePath != null) {
            surfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
            surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(@NonNull final SurfaceHolder holder) {
                    Log.e("MainActivity_Player","surfaceCreate");
//                    if (playerId != -1) {
                    Log.e("MainActivity_Player","surfaceCreate !=-1");
//                        playerId = player.createPlayer(filePath, holder.getSurface());
                    Log.e("MainActivity_Player","surfaceCreate 开始播放");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //此处是巨耗时任务，一定要在子线程中执行
                            player.play(filePath,holder.getSurface());
                        }
                    }).start();

//                    }
                }

                @Override
                public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

                }
            });
        }
    }
}
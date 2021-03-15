package com.yw.ffmpeg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import android.widget.TextView;

import com.yw.ffmpeg.media.MediaInfo;
import com.yw.ffmpeg.media.MySimpleMediaPlayer;

public class MainActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_main);
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
    private void requestMyPermissions() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //没有授权，编写申请权限代码
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        } else {
            Log.d(TAG, "requestMyPermissions: 有写SD权限");
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //没有授权，编写申请权限代码
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        } else {
            Log.d(TAG, "requestMyPermissions: 有读SD权限");
        }
    }

    /**
     * 从相册中选择视频
     */
    private void choiceVideo() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 66);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 66 && resultCode == RESULT_OK && null != data) {
            Uri selectedVideo = data.getData();
            String[] filePathColumn = {MediaStore.Video.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedVideo,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String  path  = cursor.getString(columnIndex);
            cursor.close();
            Log.e("path:",path);
            filePath = path;
            openVideo();
        }
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
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

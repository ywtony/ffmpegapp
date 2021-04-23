package camera;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yanzhenjie.permission.runtime.Permission;
import com.yw.cameralib.camera2.Camera2Manager;
import com.yw.ffmpeg.BaseActivity;
import com.yw.ffmpeg.R;
import com.yw.ffmpeg.utils.PermissionUtils;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.cameralib
 * @ClassName: SurfaceCameraPreviewActivity
 * @description 摄像头预览测试
 * @Author: wei.yang
 * @CreateDate: 2021/4/13 9:47
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/4/13 9:47
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class SurfaceViewCamera2PreviewActivity extends BaseActivity {
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        Camera2Manager.getInstance();
        PermissionUtils.getInstance().requestPermission(this, new PermissionUtils.PermissionSuccessListener() {
            @Override
            public void onSuccess() {
            }
        }, Permission.CAMERA);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface_camera_preview);
        surfaceView = findViewById(R.id.surfaceView);
        Camera2Manager.getInstance().setSurface(surfaceView.getHolder().getSurface());
        surfaceView.setZOrderMediaOverlay(true);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                Camera2Manager.getInstance().setUpCamera(SurfaceViewCamera2PreviewActivity.this,holder.getSurfaceFrame().width(),holder.getSurfaceFrame().height());
                Camera2Manager.getInstance().openCamera(SurfaceViewCamera2PreviewActivity.this);
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

            }
        });

    }

    @Override
    public void videoPathCallback(String videoPath) {

    }
}

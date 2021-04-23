package camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yw.cameralib.camera2.Camera2Manager;
import com.yw.ffmpeg.BaseActivity;
import com.yw.ffmpeg.R;
import com.yw.ffmpeg.utils.LogUtil;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: camera
 * @ClassName: TextureViewCameraPreviewActivity
 * @Description: TextureView照相机预览
 * @Author: wei.yang
 * @CreateDate: 2021/4/13 14:04
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/4/13 14:04
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class TextureViewCamera2PreviewActivity extends BaseActivity {
    private TextureView textureView;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        Camera2Manager.getInstance();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texture_view_camera_preview);
        textureView = findViewById(R.id.textureView);
        if (textureView == null) {
            LogUtil.log("不为空");
        }
        textureView.getSurfaceTexture();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!textureView.isAvailable()) {
            textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                    SurfaceTexture mSurfaceTexture = textureView.getSurfaceTexture();
//                    mSurfaceTexture.setDefaultBufferSize(Camera2Manager.getInstance().getPreviewSize().getWidth(), Camera2Manager.getInstance().getPreviewSize().getHeight());
                    Camera2Manager.getInstance().setSurface(new Surface(mSurfaceTexture));
                    Camera2Manager.getInstance().setUpCamera(TextureViewCamera2PreviewActivity.this, width, height);
                    Camera2Manager.getInstance().openCamera(TextureViewCamera2PreviewActivity.this);
                }

                @Override
                public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

                }

                @Override
                public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                    return false;
                }

                @Override
                public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

                }
            });
        }
    }

    @Override
    public void videoPathCallback(String vidoPath) {

    }
}

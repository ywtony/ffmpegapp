package camera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yw.cameralib.camera.media.CameraRecorder;
import com.yw.cameralib.camera.utils.CameraUtil;
import com.yw.cameralib.camera.utils.ImageUtil;
import com.yw.cameralib.camera.utils.PermisstionUtil;
import com.yw.cameralib.camera.utils.StorageUtil;
import com.yw.cameralib.camera.widget.CameraFocusView;
import com.yw.cameralib.camera.widget.CameraProgressButton;
import com.yw.cameralib.camera.widget.CameraSwitchView;
import com.yw.ffmpeg.BaseActivity;
import com.yw.ffmpeg.R;
import com.yw.ffmpeg.utils.LogUtil;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: camera
 * @ClassName: SurfaceCameraMp4Activity
 * @Description: 预览并生成Mp4
 * @Author: wei.yang
 * @CreateDate: 2021/4/16 9:38
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/4/16 9:38
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class SurfaceCameraMp4Activity extends BaseActivity implements CameraProgressButton.Listener, CameraSensor.CameraSensorListener, Camera.PreviewCallback {
    private final static String TAG = SurfaceCameraMp4Activity.class.getSimpleName();
    private final static int CAMERA_REQUEST_CODE = 1;
    private final static int STORE_REQUEST_CODE = 2;

    private SurfaceView mCameraView;
    private CameraSensor mCameraSensor;
    private CameraProgressButton mProgressBtn;
    private CameraFocusView mFocusView;
    private CameraSwitchView mSwitchView;
    // 是否正在对焦
    private boolean isFocusing;
    private Size mPreviewSize = null;
    private boolean isTakePhoto;
    private boolean isRecording;
    private CameraRecorder mCameraRecorder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface_camera_mp4);
        initView();
        PermisstionUtil.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                100
        );
    }

    private void initView() {
        isFocusing = false;
        isTakePhoto = false;
        isRecording = false;

        mCameraView = findViewById(R.id.camera_view);
        mProgressBtn = findViewById(R.id.progress_btn);
        mFocusView = findViewById(R.id.focus_view);
        mSwitchView = findViewById(R.id.switch_view);

        mCameraSensor = new CameraSensor(this);
        mCameraSensor.setCameraSensorListener(this);
        mCameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                LogUtil.log("宽高参数：" + holder.getSurfaceFrame().width() + "|" + holder.getSurfaceFrame().height());
                mPreviewSize = new Size(holder.getSurfaceFrame().width(), holder.getSurfaceFrame().height());
                startPreview();
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
                focus(width / 2, height / 2, true);
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                releasePreview();
            }
        });
        mProgressBtn.setProgressListener(this);

        mCameraView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    focus((int) event.getX(), (int) event.getY(), false);
                    return true;
                }
                return false;
            }
        });
        mSwitchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFocusView.cancelFocus();
                if (mPreviewSize != null) {
                    CameraUtil.switchCamera(SurfaceCameraMp4Activity.this,
                            !CameraUtil.isBackCamera(),
                            mCameraView.getHolder(),
                            mPreviewSize.getWidth(),
                            mPreviewSize.getHeight());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CameraUtil.releaseCamera();
    }


    public void startPreview() {
        if (requestCameraPermission()) {
            if (CameraUtil.getCamera() == null) {
                CameraUtil.openCamera();
            }
            if (mPreviewSize != null) {
                CameraUtil.startPreview(this, mCameraView.getHolder(), mPreviewSize.getWidth(), mPreviewSize.getHeight());
                CameraUtil.setPreviewCallback(this);
                mCameraSensor.start();
                mSwitchView.setOrientation(mCameraSensor.getX(), mCameraSensor.getY(), mCameraSensor.getZ());
            }
        }
    }

    public void releasePreview() {
        CameraUtil.releaseCamera();
        mCameraSensor.stop();
        mFocusView.cancelFocus();
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePreview();
    }

    @Override
    public void onResume() {
        super.onResume();
//        startPreview();
    }

    @Override
    public void onShortPress() {
        if (requestStoragePermission()) {
            takePicture();
        }
    }

    @Override
    public void onStartLongPress() {
        if (requestStoragePermission()) {
            beginRecord();
        }
    }

    @Override
    public void onEndLongPress() {
        endRecord();
    }

    @Override
    public void onEndMaxProgress() {
        endRecord();
    }

    private boolean requestCameraPermission() {
        return PermisstionUtil.checkPermissionsAndRequest(this, PermisstionUtil.CAMERA, CAMERA_REQUEST_CODE, "请求相机权限被拒绝");
    }

    private boolean requestStoragePermission() {
        return PermisstionUtil.checkPermissionsAndRequest(this, PermisstionUtil.STORAGE, STORE_REQUEST_CODE, "请求访问SD卡权限被拒绝");
    }

    private void focus(final int x, final int y, final boolean isAutoFocus) {
        if (!CameraUtil.isBackCamera()) {
            return;
        }
        if (isFocusing && isAutoFocus) {
            return;
        }
        isFocusing = true;
        Point focusPoint = new Point(x, y);
        Size screenSize = new Size(mCameraView.getWidth(), mCameraView.getHeight());
        if (!isAutoFocus) {
            mFocusView.beginFocus(x, y);
        }
        CameraUtil.newCameraFocus(focusPoint, screenSize, new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                isFocusing = false;
                if (!isAutoFocus) {
                    mFocusView.endFocus(success);
                }
            }
        });
    }

    @Override
    public void onRock() {
        if (CameraUtil.isBackCamera() && CameraUtil.getCamera() != null) {
            focus(mCameraView.getWidth() / 2, mCameraView.getHeight() / 2, true);
        }
        mSwitchView.setOrientation(mCameraSensor.getX(), mCameraSensor.getY(), mCameraSensor.getZ());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startPreview();
        }
    }

    public void takePicture() {
        isTakePhoto = true;
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        if (isTakePhoto) {
            String dirPath = StorageUtil.getImagePath();
            StorageUtil.checkDirExist(dirPath);
            LogUtil.log("图片宽高：" + mPreviewSize.getWidth() + "|" + mPreviewSize.getHeight());
//            boolean result = ImageUtil.saveNV21(bytes, mPreviewSize.getWidth(), mPreviewSize.getHeight(), dirPath + "image.jpg");
            boolean result = ImageUtil.saveImage(bytes, dirPath + "" + System.nanoTime() + ".jpg");
            isTakePhoto = false;
            if (result) {
                Intent intent = new Intent(this, ImageActivity.class);
                intent.putExtra("path", dirPath + "image.jpg");
                startActivity(intent);
            }
        } else if (isRecording) {
            mCameraRecorder.push(bytes);
        }
    }

    private void beginRecord() {
        if (mPreviewSize != null) {
            mCameraRecorder = new CameraRecorder(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            isRecording = true;
            mCameraRecorder.start();
        }
    }

    private void endRecord() {
        isRecording = false;
        mCameraRecorder.end();
    }

    @Override
    public void videoPathCallback(String vidoPath) {

    }
}

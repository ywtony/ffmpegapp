package camera;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;

import com.google.common.util.concurrent.ListenableFuture;
import com.yw.ffmpeg.BaseActivity;
import com.yw.ffmpeg.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: camera
 * @ClassName: CameraXActivity
 * @Description: CameraX实现预览，拍照和录制
 * @Author: wei.yang
 * @CreateDate: 2021/4/30 9:28
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/4/30 9:28
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class CameraXActivity extends BaseActivity {
    private static final String TAG = "CameraXActivity";
    private Button btnTakePicture;
    private PreviewView viewFinder;
    private Preview preview;
    private ExecutorService executors;
    private Camera camera;
    private ImageCapture imageCapture;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camerax_layout);
        initViews();
    }

    private void initViews() {
        executors = Executors.newSingleThreadExecutor();
        btnTakePicture = findViewById(R.id.btnCameraCapture);
        viewFinder = findViewById(R.id.viewFinder);
        btnTakePicture.setOnClickListener(v -> {
            takePhoto();
        });
        startCameraPreview();
    }

    @Override
    public void videoPathCallback(String vidoPath) {

    }

    /**
     * 预览
     */
    private void startCameraPreview() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                imageCapture = new ImageCapture.Builder().build();
                ProcessCameraProvider provider = cameraProviderFuture.get();
                preview = new Preview.Builder().build();
                CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
                provider.unbindAll();
                camera = provider.bindToLifecycle(this, cameraSelector, preview,imageCapture);
                preview.setSurfaceProvider(viewFinder.createSurfaceProvider(camera.getCameraInfo()));

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }, getMainExecutor());

    }

    /**
     * 拍照
     */
    private void takePhoto() {
        //拍照后的保存路径
        File photoFile = new File(
                Environment.getExternalStorageDirectory() + "/Download",
                System.nanoTime() + ".jpg");
        ImageCapture.OutputFileOptions options = new ImageCapture.OutputFileOptions.Builder(photoFile).build();
        imageCapture.takePicture(options, getMainExecutor(), new ImageCapture.OnImageSavedCallback() {
            /**
             * 保存照片成功
             * @param outputFileResults
             */
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Uri savedUri = Uri.fromFile(photoFile);
                String msg = "Photo capture succeeded: $savedUri";
                Toast.makeText(CameraXActivity.this, msg, Toast.LENGTH_SHORT).show();
                Log.d(TAG, msg);
            }

            /**
             * 保存照片出错
             * @param exception
             */
            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e(TAG, "Photo capture failed: ${exc.message}", exception);
            }
        });
    }
}

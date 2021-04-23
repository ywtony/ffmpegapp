package com.yw.cameralib.camera2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.cameralib
 * @ClassName: Camera2Manager
 * @Description: 相机管理类
 * @Author: wei.yang
 * @CreateDate: 2021/4/12 11:11
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/4/12 11:11
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 * <p>
 * 相关回调的介绍：
 * 1.openCamera中的CameraManager.StateCallback是Camera创建过程中状态回调
 * 2.createCaptureSession中的CameraCaptureSession.StateCallback是session创建过程中的状态回调
 * 3.capture或者setRepeatingRequest的CameraCaptureSession.CaptureCallback是在预览或拍照request请求之后的回调
 * ps：只支持Android5.0及以上版本
 */
public class Camera2Manager {
    /**
     * 摄像头工作线程
     */
    private HandlerThread cameraThread;
    /**
     * 代表代开摄像头的工作在具体哪个Handler的Looper中，也就是在哪个线程中执行，若为null则在当前线程中执行
     */
    private Handler cameraHandler;
    /**
     * 在Camera2中负责管理、查询摄像头信息、打开可用摄像头
     */
    private CameraManager cameraManager;
    /**
     * 具体的摄像头，提供一组属性信息，描述硬件设备以及设备可用设置及参数
     */
    private CameraDevice cameraDevice;

    /**
     * 负责创建个中捕获图像的请求CaptureRequest
     */
    private CaptureRequest.Builder captureRequestBuilder;
    /**
     * 各种捕获图像的请求
     */
    private CaptureRequest captureRequest;
    /**
     * 负责创建各种捕获图像的会话（预览、拍照都由其控制）
     */
    private CameraCaptureSession cameraCaptureSession;
    /**
     * 图像数据，可用SurfaceView中获取
     */
    private Surface surface;
    /**
     * 视频预览大小
     */
    private Size previewSize;
    /**
     * 通过getCameraIDList()枚举得到，代表选择使用哪个摄像头
     */
    private String cameraId;

    private Camera2Manager() {
        initCameraThread();
    }

    private static Camera2Manager instance;

    public static synchronized Camera2Manager getInstance() {
        if (instance == null) {
            instance = new Camera2Manager();
        }
        return instance;
    }
    public Size getPreviewSize(){
        return previewSize;
    }
    /**
     * 初始化相机预览线程
     */
    private void initCameraThread() {
        cameraThread = new HandlerThread("CameraThread");
        cameraThread.start();
        cameraHandler = new Handler(cameraThread.getLooper());
    }

    /**
     * 打开相机
     *
     * @return true打开成功，false打开失败
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void openCamera(Context context) {
        //创建摄像机管理类
        cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (cameraManager != null) {
                //检测应用是否开启了相机权限
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                    @Override
                    public void onOpened(@NonNull CameraDevice camera) {//当相机打开时调用
                        cameraDevice = camera;
                        //开始预览
                        startPreView();
                    }

                    @Override
                    public void onDisconnected(@NonNull CameraDevice camera) {//当相机关闭时调用
                        releaseCamera(camera);
                    }

                    @Override
                    public void onError(@NonNull CameraDevice camera, int error) {//当出现错误时调用
                        releaseCamera(camera);
                    }
                }, cameraHandler);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void releaseCamera(CameraDevice camera) {
        if (cameraDevice != null) {
            cameraDevice.close();
            camera.close();
            cameraDevice = null;
        }
    }

    /**
     * 开始预览
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startPreView() {
        try {
            //设置预览
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            //
            if (surface != null) {
                captureRequestBuilder.addTarget(surface);
            }
            //配置CaptureSession
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    //配置成功的情况
                    captureRequest = captureRequestBuilder.build();
                    cameraCaptureSession = session;
                    try {
                        cameraCaptureSession.setRepeatingRequest(captureRequest, null, cameraHandler);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    //配置失败

                }
            }, cameraHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setSurface(Surface surface) {
        this.surface = surface;
    }

    public void setSurface(SurfaceView surfaceView) {
        this.surface = surfaceView.getHolder().getSurface();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setUpCamera(Context context, int width, int height) {
        if (cameraManager == null) {
            cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            try {
                //遍历所有摄像头
                for (String cameraId : cameraManager.getCameraIdList()) {
                    //查询摄像头属性
                    CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                    Integer facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
                    //此处默认打开后置摄像头
                    if (facing == CameraCharacteristics.LENS_FACING_BACK) {
                        this.cameraId = cameraId;
                        StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                        //将最合适的预览尺寸设置给SurfaceView或者TextureView
                        previewSize = getOptimalSize(map.getOutputSizes(SurfaceTexture.class), width, height);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取预览最适合的长宽比例
     *
     * @param sizeMap
     * @param width
     * @param height
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private Size getOptimalSize(Size[] sizeMap, int width, int height) {
        List<Size> sizeList = new ArrayList<>();
        for (Size option : sizeMap) {
            if (width > height) {
                if (option.getWidth() > width && option.getHeight() > height) {
                    sizeList.add(option);
                }
            } else {
                if (option.getWidth() > height && option.getHeight() > width) {
                    sizeList.add(option);
                }
            }
        }
        if (sizeList.size() > 0) {
            return Collections.min(sizeList, new Comparator<Size>() {
                @Override
                public int compare(Size lhs, Size rhs) {
                    return Long.signum(lhs.getWidth() * lhs.getHeight() - rhs.getWidth() * rhs.getHeight());
                }
            });
        }
        return sizeMap[0];
    }

}


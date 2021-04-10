package com.yw.ffmpeg;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yw.ffmpeg.egl.EGLRender;
import com.yw.ffmpeg.utils.LogUtil;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg
 * @ClassName: EGLSimpleActivity
 * @Description: 测试EGL的渲染功能
 * @Author: wei.yang
 * @CreateDate: 2021/4/7 9:56
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/4/7 9:56
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class EGLSimpleActivity extends BaseActivity {
    private SurfaceView surfaceView;
    private EGLRender eglRender;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egl_simple);
        surfaceView = findViewById(R.id.surfaceView);
        eglRender = new EGLRender("EGLRender");
        eglRender.start();
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
                eglRender.render(holder.getSurface(), width, height);
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        eglRender.release();
        eglRender = null;
        super.onDestroy();

    }

    @Override
    public void videoPathCallback(String vidoPath) {

    }

    /**
     * EGL的使用步骤
     * 1.获取Display
     * 2.初始化EGL
     * 3.选择Config
     * 4.构造Surface
     * 5.创建Context
     * 6.切换上下文
     * 7.绘制
     * 8.使用完成后释放资源
     */

//    private void egltest() {
//        //获取display
//        EGLDisplay display = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
//        if (display == EGL14.EGL_NO_DISPLAY) {
//            Log.e("TAG", "获取EGLDisplay失败");
//        }
//        //初始化EGL
//        // 定义一个 2 维数组，用于存放获取到的版本号，主版本号放在 version[0]，次版本号放在 version[1]
//        int[] version = new int[2];
//        boolean isSuccess= EGL14.eglInitialize(display,version,0,version,1);
//        if(!isSuccess){
//            getEGLError();
//        }
//        //配置
//        int[] configAttribs = new int[] {
//                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_WINDOW_BIT,
//                EGL14.EGL_RED_SIZE, 8,
//                EGL14.EGL_GREEN_SIZE, 8,
//                EGL14.EGL_BLUE_SIZE, 8,
//                EGL14.EGL_DEPTH_SIZE, 24,
//                EGL14.EGL_NONE
//        };
//        EGLConfig[] configs = new EGLConfig[1];
//        int[] numConfigs = new int[1];
//        if(!EGL14.eglChooseConfig(display, configAttribs, 0, configs, 0,
//                configs.length, numConfigs, 0)) {
//            Log.e("initWindow", EGL14.eglGetError() + "");
//        }
//        if(configs[0] == null) {
//            Log.e("config", EGL14.eglGetError() + "");
//        }
//        // 4. 创建渲染表面，此处是创建窗口
//        EGLSurface window = EGL14.eglCreateWindowSurface(display,
//                configs[0], glSurfaceView.getHolder(), null, 0);
//        if(window == EGL14.EGL_NO_SURFACE) {
//            Log.e("initWindow", EGL14.eglGetError() + "");
//        }
//        // 5. 创建上下文
//        int[] contextAttribs = new int[] {
//                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
//                EGL14.EGL_NONE
//        };
//        EGLContext eglContext = EGL14.eglCreateContext(display, configs[0],
//                EGL14.EGL_NO_CONTEXT, contextAttribs, 0);
//        if(eglContext == EGL14.EGL_NO_CONTEXT) {
//            Log.e("initWindow", EGL14.eglGetError() + "");
//        }
//        // 6. 绑定上下文与表面
//        if(!EGL14.eglMakeCurrent(display, window, window, eglContext)) {
//            Log.e("initWindow", EGL14.eglGetError() + "");
//        }
//        Log.d("initWindow", "初始化成功");
//
//
//    }
//
//    private void getEGLError() {
//        int errCode = EGL14.eglGetError();
//        switch (errCode) {
//            case EGL14.EGL_SUCCESS:
//                LogUtil.log("函数执行成功，无错误---没有错误");
//                break;
//            case EGL14.EGL_NOT_INITIALIZED:
//                LogUtil.log("对于特定的 Display, EGL 未初始化，或者不能初始化---没有初始化");
//                break;
//            case EGL14.EGL_BAD_ACCESS:
//                LogUtil.log("EGL 无法访问资源(如 Context 绑定在了其他线程)---访问失败");
//                break;
//            case EGL14.EGL_BAD_ALLOC:
//                LogUtil.log("对于请求的操作，EGL 分配资源失败---分配失败");
//                break;
//            case EGL14.EGL_BAD_ATTRIBUTE:
//                LogUtil.log("未知的属性，或者属性已失效---错误的属性");
//                break;
//            case EGL14.EGL_BAD_CONTEXT:
//                LogUtil.log("EGLContext(上下文) 错误或无效---错误的上下文");
//                break;
//            case EGL14.EGL_BAD_CONFIG:
//                LogUtil.log("EGLConfig(配置) 错误或无效---错误的配置");
//                break;
//            case EGL14.EGL_BAD_DISPLAY:
//                LogUtil.log("EGLDisplay(显示) 错误或无效---错误的显示设备对象");
//                break;
//            case EGL14.EGL_BAD_SURFACE:
//                LogUtil.log("未知的属性，或者属性已失效---错误的Surface对象");
//                break;
//            case EGL14.EGL_BAD_CURRENT_SURFACE:
//                LogUtil.log("窗口，缓冲和像素图(三种 Surface)的调用线程的 Surface 错误或无效---当前Surface对象错误");
//                break;
//            case EGL14.EGL_BAD_MATCH:
//                LogUtil.log("参数不符(如有效的 Context 申请缓冲，但缓冲不是有效的 Surface 提供)---无法匹配");
//                break;
//            case EGL14.EGL_BAD_PARAMETER:
//                LogUtil.log("错误的参数");
//                break;
//            case EGL14.EGL_BAD_NATIVE_PIXMAP:
//                LogUtil.log("NativePixmapType 对象未指向有效的本地像素图对象---错误的像素图");
//                break;
//            case EGL14.EGL_BAD_NATIVE_WINDOW:
//                LogUtil.log("NativeWindowType 对象未指向有效的本地窗口对象---错误的本地窗口对象");
//                break;
//            case EGL14.EGL_CONTEXT_LOST:
//                LogUtil.log("电源错误事件发生，Open GL重新初始化，上下文等状态重置---上下文丢失");
//                break;
//            default:
//                break;
//        }
//    }
}

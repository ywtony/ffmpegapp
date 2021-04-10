package com.yw.ffmpeg.egl;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.util.Log;
import android.view.Surface;

import com.yw.ffmpeg.utils.LogUtil;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.egl
 * @ClassName: EGLCore
 * @Description: 对EGL进行封装
 * <p>
 * <p>
 * 封装步骤：
 * 1.EGLDisplay：GL定义的一个抽象的系统显示类，用于操作设备窗口。
 * 2.EGLConfig：EGL配置，如rgba位数
 * 3.EGLSurface：渲染缓存，一块内存空间，所有要渲染到屏幕上的图像数据，都要先缓存在EGLSurface上。
 * 4.EGLContext：OpenGL上下文，用于存储OpenGL的绘制状态信息、数据。
 * <p>
 * <p>
 * @Author: wei.yang
 * @CreateDate: 2021/4/8 10:01
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/4/8 10:01
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class EGLCore {
    public static int FLAG_RECORDABLE = 0x01;

    public static int EGL_RECORDABLE_ANDROID = 0x3142;
    private static final String TAG = "EGLCORE";
    //初始化EGL相关变量
    private EGLDisplay eglDisplay = EGL14.EGL_NO_DISPLAY;
    private EGLContext eglContext = EGL14.EGL_NO_CONTEXT;
    private EGLConfig eglConfig = null;

    /**
     * @param eglContext 共享上下文
     * @param flags      初始化标记
     * @return
     * @description 初始化EGLDisplay
     * @date: 2021/4/8 10:05
     * @author: wei.yang
     */
    public void init(EGLContext eglContext, int flags) {
        if (eglDisplay != EGL14.EGL_NO_DISPLAY) {
            LogUtil.log("EGL already init");
            return;
        }
        EGLContext sharedContext = eglContext == null ? EGL14.EGL_NO_CONTEXT : eglContext;
        //创建EGLDisplay
        eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        if (eglDisplay == EGL14.EGL_NO_DISPLAY) {
            LogUtil.log("Unable to get EGL14 display");
        }
        //初始化EGLDisplay
        int[] version = new int[2];
        if (!EGL14.eglInitialize(eglDisplay, version, 0, version, 1)) {
            eglDisplay = EGL14.EGL_NO_DISPLAY;
            LogUtil.log("unable to initialize EGL14");
        }
        //初始化EGLConfig，EGLContext上下文
        if (eglContext == EGL14.EGL_NO_CONTEXT) {
            EGLConfig eglConfig1 = getConfig(flags, 2);
            int[] attr2List = new int[]{
                    EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE
            };
            EGLContext eglContext1 = EGL14.eglCreateContext(eglDisplay, eglConfig1, sharedContext, attr2List, 0);
            eglConfig = eglConfig1;
            eglContext = eglContext1;
        }

    }

    /**
     * @param flags   初始化标记
     * @param version EGL版本
     * @description 初始化EGL配置信息
     * @date: 2021/4/8 10:27
     * @author: wei.yang
     */
    private EGLConfig getConfig(int flags, int version) {
        int renderAbleType = EGL14.EGL_OPENGL_ES2_BIT;
        if (version >= 3) {
            //配置EGL3
            renderAbleType = EGLExt.EGL_OPENGL_ES3_BIT_KHR;
        }
        //配置数组，主要是配置RGBA位数和深度位数
        //两个一对，前面是key，后面是value
        //数组必须是以EGL14.EGL_NONE结尾
        int[] attrList = new int[]{
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_ALPHA_SIZE, 8,
                EGL14.EGL_RENDERABLE_TYPE, renderAbleType,
                EGL14.EGL_NONE, 0,
                EGL14.EGL_NONE
        };
        //配置Android指定的标记
        if (flags != 0 && FLAG_RECORDABLE != 0) {
            attrList[attrList.length - 3] = EGL_RECORDABLE_ANDROID;
            attrList[attrList.length - 2] = 1;

        }

        EGLConfig[] eglConfigs = new EGLConfig[1];
        int[] numConfigs = new int[1];
        //获取可用的EGL配置列表
        if (!EGL14.eglChooseConfig(eglDisplay, attrList, 0, eglConfigs, 0, eglConfigs.length, numConfigs, 0)) {
            LogUtil.log("Unable to find RGB8888 / $version EGLConfig");
        }
        //使用系统推荐的第一个配置
        return eglConfigs[0];

    }

    /**
     * @param surface 窗口Surface，如SurfaceView.getHolder().getSurface()
     * @return EGLSurface
     * @method 创建EGLSurface
     * @description
     * @date: 2021/4/8 10:44
     * @author: wei.yang
     */
    public EGLSurface createWindowSurface(Surface surface) {
        int[] surfaceAttr = new int[]{
                EGL14.EGL_NONE
        };
        EGLSurface eglSurface = EGL14.eglCreateWindowSurface(eglDisplay, eglConfig, surface, surfaceAttr, 0);
        if (eglSurface == null) {
            LogUtil.log("eglsurface is null");
        }
        return eglSurface;
    }

    /**
     * @param width  缓存窗口宽度
     * @param height 缓存窗口高度
     * @return 返回EGLSurface
     * @method 创建离屏渲染缓存
     * @description
     * @date: 2021/4/8 10:48
     * @author: wei.yang
     */
    public EGLSurface createOffScreenSurface(int width, int height) {
        int[] surfaceAttr = new int[]{
                EGL14.EGL_WIDTH, width,
                EGL14.EGL_HEIGHT, height,
                EGL14.EGL_NONE
        };
        EGLSurface eglSurface = EGL14.eglCreatePbufferSurface(eglDisplay, eglConfig, surfaceAttr, 0);
        if (eglSurface == null) {
            LogUtil.log("eglsurface is null");
        }
        return eglSurface;
    }

    /**
     * 将当前线程与上下文绑定
     *
     * @param eglSurface 渲染数据缓存
     * @method
     * @description
     * @date: 2021/4/8 10:50
     * @author: wei.yang
     */
    public void makeCurrent(EGLSurface eglSurface) {
        if (eglDisplay == EGL14.EGL_NO_DISPLAY) {
            LogUtil.log("EGLDisplay is null, call init first");
        }
        if (!EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
            LogUtil.log("makeCurrent(eglSurface) failed");
        }
    }

    /**
     * 将当前线程与上下文绑定
     *
     * @param readSurface 渲染数据缓存
     * @param drawSurface 渲染数据缓存
     * @method
     * @description
     * @date: 2021/4/8 10:50
     * @author: wei.yang
     */
    public void makeCurrent(EGLSurface drawSurface, EGLSurface readSurface) {
        if (eglDisplay == EGL14.EGL_NO_DISPLAY) {
            LogUtil.log("EGLDisplay is null, call init first");
        }
        if (!EGL14.eglMakeCurrent(eglDisplay, drawSurface, readSurface, eglContext)) {
            LogUtil.log("makeCurrent(eglSurface) failed");
        }
    }

    /**
     * eglSwapBuffers是EGL提供的用来将EGLSurface数据显示到设备屏幕上的方法。在OpenGL绘制完图像化，调用该方法，才能真正显示出来
     * @param eglSurface 图像缓存数据
     * @return 是否发送并显示成功
     * @method 将缓存图像数据发送到设备进行显示
     * @description
     * @date: 2021/4/8 10:54
     * @author: wei.yang
     */
    public boolean swapBuffers(EGLSurface eglSurface) {
        return EGL14.eglSwapBuffers(eglDisplay, eglSurface);
    }

    /**
     * 设置当前帧的时间，单位：纳秒
     */
    public void setPresentationTime(EGLSurface eglSurface, long nsecs) {
        EGLExt.eglPresentationTimeANDROID(eglDisplay, eglSurface, nsecs);
    }

    /**
     * 销毁EGLSurface，并解除上下文绑定
     */
    public void destroySurface(EGLSurface elg_surface) {
        EGL14.eglMakeCurrent(
                eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
                EGL14.EGL_NO_CONTEXT
        );
        EGL14.eglDestroySurface(eglDisplay, elg_surface);
    }

    /**
     * 释放资源
     */
    public void release() {
        if (eglDisplay != EGL14.EGL_NO_DISPLAY) {
            // Android is unusual in that it uses a reference-counted EGLDisplay.  So for
            // every eglInitialize() we need an eglTerminate().
            EGL14.eglMakeCurrent(
                    eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
                    EGL14.EGL_NO_CONTEXT
            );
            EGL14.eglDestroyContext(eglDisplay, eglContext);
            EGL14.eglReleaseThread();
            EGL14.eglTerminate(eglDisplay);
        }

        eglDisplay = EGL14.EGL_NO_DISPLAY;
        eglContext = EGL14.EGL_NO_CONTEXT;
        eglConfig = null;
    }
}

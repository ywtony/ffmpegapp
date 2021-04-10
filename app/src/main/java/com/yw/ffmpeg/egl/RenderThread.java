package com.yw.ffmpeg.egl;

import android.opengl.GLES20;
import android.view.Surface;

import com.yw.ffmpeg.opengles.IDrawer;
import com.yw.ffmpeg.opengles.OpenGlTools;

import java.util.ArrayList;
import java.util.List;

import static com.yw.ffmpeg.egl.EGLCore.EGL_RECORDABLE_ANDROID;
import static com.yw.ffmpeg.egl.RenderState.*;
import static com.yw.ffmpeg.egl.RenderState.FRESH_SURFACE;
import static com.yw.ffmpeg.egl.RenderState.RENDERING;
import static com.yw.ffmpeg.egl.RenderState.SURFACE_CHANGE;
import static com.yw.ffmpeg.egl.RenderState.SURFACE_DESTROY;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.egl
 * @ClassName: RenderThread
 * @Description: 渲染线程
 * @Author: wei.yang
 * @CreateDate: 2021/4/8 11:15
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/4/8 11:15
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class RenderThread extends Thread {
    private Surface surface;
    private List<IDrawer> drawers = new ArrayList<>();
    public void setSurface(Surface surface) {
        this.surface = surface;
    }

    public void setDrawers(List<IDrawer> drawers){
        this.drawers = drawers;
    }

    // 渲染状态
    private RenderState mState = NO_SURFACE;
    //初始化自定义的SurfaceHolder
    private EGLSurfaceHolder eglSurfaceHolder = null;
    //是否绑定了EGLContext
    private boolean isBindEGLContext = false;
    //是否已经新建过EGL上下文，用于判断是否需要生产新的纹理ID
    private boolean isCreateEGLContext = true;

    private int mWidth = 0;
    private int mHeight = 0;
    //定义一个线程锁
    private Object mWaitLock = new Object();

    private void holdOn() {
        synchronized (mWaitLock) {
            try {
                mWaitLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void notifyGo() {
        synchronized (mWaitLock) {
            mWaitLock.notify();
        }
    }

    public void onSufaceCreate() {
        mState = FRESH_SURFACE;
        notifyGo();
    }

    public void onSurfaceChange(int width, int height) {
        mWidth = width;
        mHeight = height;
        mState = SURFACE_CHANGE;
        notifyGo();
    }

    public void onSurfaceDestroy() {
        mState = SURFACE_DESTROY;
        notifyGo();
    }

    public void onSufaceStop() {
        mState = STOP;
        notifyGo();
    }

    @Override
    public void run() {
        // 【1】初始化EGL
        initEGL();
        while (true) {
            switch (mState) {
                case FRESH_SURFACE: {
                    //【2】使用surface初始化EGLSurface，并绑定上下文
                    createEGLSurfaceFirst();
                    holdOn();
                }
                break;
                case SURFACE_CHANGE: {
                    createEGLSurfaceFirst();
                    //【3】初始化OpenGL世界坐标系宽高
                    GLES20.glViewport(0, 0, mWidth, mHeight);
                    configWordSize();
                    mState = RENDERING;
                }
                break;
                case RENDERING: {
                    //【4】进入循环渲染
                    render();
                }
                break;
                case SURFACE_DESTROY: {
                    //【5】销毁EGLSurface，并解绑上下文
                    destroyEGLSurface();
                    mState = NO_SURFACE;
                }
                break;
                case STOP: {
                    //【6】释放所有资源
                    releaseEGL();
                    return;
                }
                default: {
                    holdOn();
                }
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void initEGL() {
        eglSurfaceHolder = new EGLSurfaceHolder();
        eglSurfaceHolder.init(null, EGL_RECORDABLE_ANDROID);
    }

    private void createEGLSurfaceFirst() {
        if (!isBindEGLContext) {
            isBindEGLContext = true;
            createEGLSurface();
            if (isCreateEGLContext) {
                isCreateEGLContext = false;
                generateTextureID();
            }
        }
    }

    private void createEGLSurface() {
        eglSurfaceHolder.createEGLSurface(surface);
        eglSurfaceHolder.makeCurrent();
    }

    private void destroyEGLSurface() {
        eglSurfaceHolder.destroyEGLSurface();
        isBindEGLContext = false;
    }

    private void releaseEGL() {
        eglSurfaceHolder.release();
    }

    /**
     * @description 生成纹理ID
     * @date: 2021/4/8 11:32
     * @author: wei.yang
     */
    private void generateTextureID() {
        int[] textureIds = OpenGlTools.getInstance().createTextureIds(drawers.size());
        for ((idx, drawer) in mDrawers.withIndex()){
            drawer.setTextureID(textureIds[idx])
        }
    }

    /**
     * @description 配置世界坐标系的坐标
     * @date: 2021/4/8 11:33
     * @author: wei.yang
     */
    private void configWordSize() {
        mDrawers.forEach {
            it.setWorldSize(mWidth, mHeight)
        }
    }

    /**
     * @description 开始渲染
     * @date: 2021/4/8 11:33
     * @author: wei.yang
     */
    private void render() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        mDrawers.forEach {
            it.draw()
        }
        eglSurfaceHolder.swapBuffers();
    }
}

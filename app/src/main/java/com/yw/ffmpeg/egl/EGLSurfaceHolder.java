package com.yw.ffmpeg.egl;

import android.opengl.EGLContext;
import android.opengl.EGLSurface;
import android.view.Surface;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.egl
 * @ClassName: EGLSurfaceHolder
 * @Description: 定义一个SurfaceHolder用于操作EGLCore
 * @Author: wei.yang
 * @CreateDate: 2021/4/8 11:06
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/4/8 11:06
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class EGLSurfaceHolder {
    private EGLCore eglCore;
    private EGLSurface eglSurface;

    public void init(EGLContext shareEGLContext, int flags) {
        eglCore = new EGLCore();
        eglCore.init(shareEGLContext, flags);
    }

    public void createEGLSurface(Surface surface) {
        eglSurface = eglCore.createWindowSurface(surface);
    }

    public void createEGLSurface(int width, int height) {
        eglSurface = eglCore.createOffScreenSurface(width, height);
    }

    public void makeCurrent(){
        if(eglSurface!=null){
            eglCore.makeCurrent(eglSurface);
        }
    }

    public void swapBuffers(){
        if(eglSurface!=null){
            eglCore.swapBuffers(eglSurface);
        }
    }

    public void destroyEGLSurface(){
        if(eglSurface!=null){
            eglCore.destroySurface(eglSurface);
            eglSurface = null;
        }
    }

    public void release(){
        eglCore.release();
    }
}

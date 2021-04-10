package com.yw.ffmpeg.egl;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;

import com.yw.ffmpeg.opengles.IDrawer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.egl
 * @ClassName: CustomerGLRender
 * @Description: 自定义渲染器
 * @Author: wei.yang
 * @CreateDate: 2021/4/8 11:13
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/4/8 11:13
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class CustomerGLRender implements SurfaceHolder.Callback {

    //OpenGL渲染线程
    private RenderThread mThread = new RenderThread();

    //页面上的SurfaceView弱引用
    private WeakReference<SurfaceView> mSurfaceView = null;

    //所有的绘制器
    private List<IDrawer> mDrawers = new ArrayList<IDrawer>();

    public CustomerGLRender(SurfaceView surfaceView) {
        setSurface(surfaceView);
        //启动渲染线程
        mThread.start();
    }

    /**
     * 设置SurfaceView
     */
    public void setSurface(SurfaceView surfaceView) {
        mSurfaceView = new WeakReference(surfaceView);
        surfaceView.getHolder().addCallback(this);
        surfaceView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {

            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                mThread.onSufaceStop();
            }
        });
        mThread.setSurface(surfaceView.getHolder().getSurface());
    }

    /**
     * 添加绘制器
     */
    public void addDrawer(IDrawer drawer) {
        mDrawers.add(drawer);
    }


    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        mThread.onSufaceCreate();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        mThread.onSurfaceChange(width, height);
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        mThread.onSurfaceDestroy();
    }
}

package com.yw.ffmpeg.opengles;

import android.annotation.SuppressLint;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.opengles
 * @ClassName: SimpleRender
 * @Description: java类作用描述
 * @Author: wei.yang
 * @CreateDate: 2021/3/23 10:59
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/3/23 10:59
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class SimpleRender implements GLSurfaceView.Renderer {
    private static final String TAG = "SimpleRender";
    private List<IDrawer> drawers = new ArrayList<>();

    public SimpleRender() {
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //清屏
        GLES20.glClearColor(0f, 0f, 0f, 0f);
        //开启混合，即半透明
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        //设置纹理ID
        int[] textureIds = OpenGlTools.getInstance().createTextureIds(drawers.size());
        for (int i = 0; i < drawers.size(); i++) {
            drawers.get(i).setTextureId(textureIds[i]);
        }
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //设置opengles绘制区域的宽高
        GLES20.glViewport(0, 0, width, height);
        Log.e(TAG + "onSurfaceChanged", width + "|" + height);
        for (IDrawer iDrawer : drawers) {
            iDrawer.setWorldSize(width, height);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        //此接口会不停的执行回调从而实现绘制
        for (IDrawer iDrawer : drawers) {
            iDrawer.draw();
        }
        Log.e(TAG + "onDrawFrame", "onDrawFrame");
    }

    public void addDrawer(IDrawer iDrawer) {
        drawers.add(iDrawer);
    }
}

package com.yw.ffmpeg.opengles.simple;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.opengles.simple
 * @ClassName: MyGLSurfaceView
 * @Description: 自定义GLSurface用于渲染三角形
 * @Author: wei.yang
 * @CreateDate: 2021/4/2 14:07
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/4/2 14:07
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class MyGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {
    /**
     * 图形顶点坐标
     */
    private float triangleCoords[] = {//坐标x,y,z
            0.5f, 0.5f, 0f,//top
            -0.5f, -0.5f, 0f,//bottom left
            0.5f, -0.5f, 0f//bottom right
    };
    /**
     * 三角形的颜色
     */
    private float color[] = {1.0f, 1.0f, 1.0f, 1.0f}; //白色
    /**
     * 片元着色器的vColor成员句柄
     */
    private int mColorHandle;
    /**
     * 顶点着色器的vPosition成员句柄
     */
    private int mPositionHandle;
    /**
     * 渲染程序
     */
    private int mProgram;
    /**
     * 定义顶点坐标缓冲区
     */
    private FloatBuffer vertexBuffer = null;

    public MyGLSurfaceView(Context context) {
        super(context);
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //设置版本号
        setEGLContextClientVersion(2);
        //设置渲染器
        setRenderer(this);
        //设置刷新模式-》手动刷新
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    /**
     * 渲染器回调方法开始
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置背景为灰色
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1f);
        //申请底层空间,像素数量*4(rgba)
        ByteBuffer bb = ByteBuffer.allocateDirect(triangleCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        //将坐标转换为floatbuffer，用以传入给opengles程序
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(triangleCoords);
        vertexBuffer.position(0);
        //获取顶点着色器
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, ELShaderUtil.vertexShaderCode);
        //获取片元着色器
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, ELShaderUtil.fragmentShadeCode);
        //创建一个空的opengles程序
        mProgram = GLES20.glCreateProgram();
        //将顶点着色器加入程序
        GLES20.glAttachShader(mProgram, vertexShader);
        //将片元着色器加入程序
        GLES20.glAttachShader(mProgram, fragmentShader);
        //链接到着色器程序
        GLES20.glLinkProgram(mProgram);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //上面写好的程序加入到Opengles2.0环境
        GLES20.glUseProgram(mProgram);
        //获取顶点着色器的vPosition成员句柄
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        //启用三角形顶点句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
        //获取片元着色器的vColor成员句柄
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        //设置绘制三角形颜色
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        //绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        Log.e("TAG", "onDrawFrame");
    }

    /**
     * 渲染器回调方法结束
     */


    private int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (0 != shader) {
            GLES20.glShaderSource(shader, source);//设置源代码
            //将源代码给编译器
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e("TAG", "Could not compile shader:" + shaderType);
                Log.e("TAG", "GLES20 Error:" + GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }

        }
        return shader;
    }
}

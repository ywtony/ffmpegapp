package com.yw.ffmpeg.opengles;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.opengles
 * @ClassName: TriangleDrawer
 * @Description: 绘制一个三角形
 * @Author: wei.yang
 * @CreateDate: 2021/3/23 11:18
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/3/23 11:18
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class TriangleDrawer implements IDrawer {
    /**
     * 纹理id
     */
    private int textureId = -1;

    /**
     * 初始化顶点坐标
     */
    private float[] mVertexCoors = new float[]{
            -1f, -1f,
            1f, -1f,
            0f, 1f
    };

    /**
     * 初始化纹理坐标
     */
    private float[] mTextureCoors = new float[]{
            0f, 1f,
            1f, 1f,
            0.5f, 0f
    };
    /**
     * opengles 小程序id
     */
    private int mProgram = -1;
    /**
     * 顶点坐标接受者
     */
    private int mVertexPosHandler = -1;
    /**
     * 纹理坐标接收者
     */
    private int mTexturePosHandler = -1;
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTextureBuffer;

    public TriangleDrawer() {
        //【步骤1: 初始化顶点坐标】
        initPos();
    }

    private void initPos() {
        ByteBuffer bb = ByteBuffer.allocateDirect(mVertexCoors.length * 4);
        bb.order(ByteOrder.nativeOrder());
        //将坐标数据转换为FloatBuffer，用以传入给OpenGL ES程序
        mVertexBuffer = bb.asFloatBuffer();
        mVertexBuffer.put(mVertexCoors);
        mVertexBuffer.position(0);

        ByteBuffer cc = ByteBuffer.allocateDirect(mTextureCoors.length * 4);
        cc.order(ByteOrder.nativeOrder());
        mTextureBuffer = cc.asFloatBuffer();
        mTextureBuffer.put(mTextureCoors);
        mTextureBuffer.position(0);
    }

    @Override
    public void draw() {
        if (textureId != -1) {
            //【步骤2: 创建、编译并启动OpenGL着色器】
            createGLProgram();
            //【步骤3: 开始渲染绘制】
            doDraw();
        }
    }

    @Override
    public void setTextureId(int textureId) {
        this.textureId = textureId;
    }

    @Override
    public void release() {
        GLES20.glDisableVertexAttribArray(mVertexPosHandler);
        GLES20.glDisableVertexAttribArray(mTexturePosHandler);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glDeleteTextures(1, new int[textureId], 0);
        GLES20.glDeleteProgram(mProgram);
    }

    @Override
    public void setWorldSize(int worldW, int worldH) {

    }

    private void createGLProgram() {
        if (mProgram == -1) {

            int  vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, getVertexShader());
            int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, getFragmentShader());

            //创建OpenGL ES程序，注意：需要在OpenGL渲染线程中创建，否则无法渲染
            mProgram = GLES20.glCreateProgram();
            //将顶点着色器加入到程序
            GLES20.glAttachShader(mProgram, vertexShader);
            //将片元着色器加入到程序中
            GLES20.glAttachShader(mProgram, fragmentShader);
            //连接到着色器程序
            GLES20.glLinkProgram(mProgram);

            mVertexPosHandler = GLES20.glGetAttribLocation(mProgram, "aPosition");
            mTexturePosHandler = GLES20.glGetAttribLocation(mProgram, "aCoordinate");
        }
        //使用OpenGL程序
        GLES20.glUseProgram(mProgram);
    }

    private void doDraw() {
        //启用顶点句柄
        GLES20.glEnableVertexAttribArray(mVertexPosHandler);
        GLES20.glEnableVertexAttribArray(mTexturePosHandler);
        //设置着色器参数
        GLES20.glVertexAttribPointer(mVertexPosHandler, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glVertexAttribPointer(mTexturePosHandler, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);
        //开始绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 3);

    }

    private String getVertexShader() {
        return "attribute vec4 aPosition;" +
                "void main() {" +
                "  gl_Position = aPosition;" +
                "}";
    }

    private String getFragmentShader() {
        return "precision mediump float;" +
                "void main() {" +
                "  gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);" +
                "}";
    }

    /**
     * 加载shared
     *
     * @param type      顶点着色器或片元着色器
     * @param shadeCode 着色器代码
     * @return
     */
    private int loadShader(int type, String shadeCode) {
        //根据type创建顶点着色器或者片元着色器
        int shader = GLES20.glCreateShader(type);
        //将资源加入到着色器，并编译
        GLES20.glShaderSource(shader, shadeCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
}

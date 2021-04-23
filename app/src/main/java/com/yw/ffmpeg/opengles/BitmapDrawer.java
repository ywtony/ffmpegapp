package com.yw.ffmpeg.opengles;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.opengles
 * @ClassName: BitmapDrawer
 * @Description: java类作用描述
 * @Author: wei.yang
 * @CreateDate: 2021/3/23 14:03
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/3/23 14:03
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class BitmapDrawer implements IDrawer {
    private Bitmap bitmap;
    public BitmapDrawer(Bitmap bitmap){
        this.bitmap = bitmap;
        //【步骤1: 初始化顶点坐标】
        initPos();
    }
    // 顶点坐标
    private float[] mVertexCoors = new float[]{
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f
    };

    // 纹理坐标
    private float[] mTextureCoors = new float[]{
            0f, 1f,
            1f, 1f,
            0f, 0f,
            1f, 0f
    };

    private int mTextureId = -1;

    //OpenGL程序ID
    private int mProgram = -1;

    // 顶点坐标接收者
    private int mVertexPosHandler = -1;
    // 纹理坐标接收者
    private int mTexturePosHandler = -1;
    // 纹理接收者
    private int mTextureHandler = -1;

    private FloatBuffer mVertexBuffer = null;
    private FloatBuffer mTextureBuffer = null;
    public BitmapDrawer(){
        //【步骤1: 初始化顶点坐标】
        initPos();
    }
    private void initPos() {
        //分配堆外内存
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
        if (mTextureId != -1) {
            //【步骤2: 创建、编译并启动OpenGL着色器】
            createGLPrg();
            //【步骤3: 激活并绑定纹理单元】
            activateTexture();
            //【步骤4: 绑定图片到纹理单元】
            bindBitmapToTexture();
            //【步骤5: 开始渲染绘制】
            doDraw();
        }
    }

    @Override
    public void setTextureId(int textureId) {
        mTextureId = textureId;
    }

    @Override
    public void release() {
        //禁用顶点位置变量
        GLES20.glDisableVertexAttribArray(mVertexPosHandler);
        //禁用纹理位置变量
        GLES20.glDisableVertexAttribArray(mTexturePosHandler);
        //解绑纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        //删除纹理
        GLES20.glDeleteTextures(1, new int[mTextureId], 0);
        //删除程序
        GLES20.glDeleteProgram(mProgram);
    }

    @Override
    public void setWorldSize(int worldW, int worldH) {

    }

    @Override
    public void setVideoSize(int videoW, int videoH) {

    }

    private void createGLPrg() {
        if (mProgram == -1) {
            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, getVertexShader());
            int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, getFragmentShader());

            //创建OpenGL ES程序，注意：需要在OpenGL渲染线程中创建，否则无法渲染
            mProgram = GLES20.glCreateProgram();
            //将顶点着色器加入到程序
            GLES20.glAttachShader(mProgram, vertexShader);
            //将片元着色器加入到程序中
            GLES20.glAttachShader(mProgram, fragmentShader);
            //连接到着色器程序
            GLES20.glLinkProgram(mProgram);

            /**
             * 一下三个都是用于获取着色器程序内，成员变量的ID，也可以理解为句柄或指针
             * 当我们需要改变如：颜色、位置、矩阵变换，都需要先获取这个变量的ID，然后对这个变量进行操作，就可以改变我们的绘制内容
             */
            //顶点位置变量ID
            mVertexPosHandler = GLES20.glGetAttribLocation(mProgram, "aPosition");
            //颜色变量ID
            mTexturePosHandler = GLES20.glGetAttribLocation(mProgram, "aCoordinate");
            //纹理ID
            mTextureHandler = GLES20.glGetUniformLocation(mProgram, "uTexture");

        }
        //使用OpenGL程序
        GLES20.glUseProgram(mProgram);
    }
//    TEXTURE_EXTERNAL_OES，主要用于摄像头数据的采集
    private void activateTexture() {
        //默认会生成第0号纹理，所以此处激活的是第0个纹理单元
        //激活指定纹理单元
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //绑定纹理ID到纹理单元
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
        //将激活的纹理单元传递到着色器里面
        GLES20.glUniform1i(mTextureHandler, 0);
        //配置边缘过渡参数
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, (float) GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,  (float) GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    }

    private void bindBitmapToTexture() {
        if (!bitmap.isRecycled()) {
            //绑定图片到被激活的纹理单元
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        }
    }

    private void doDraw() {
        //启用顶点变量ID，只有这里启用了我们才可以操作顶点或者纹理句柄
        GLES20.glEnableVertexAttribArray(mVertexPosHandler);
        //启用纹理变量ID
        GLES20.glEnableVertexAttribArray(mTexturePosHandler);
        /**
         * 传入顶点/纹理的位置
         * glVertexAttribPointer
         * (
         * 1.顶点对应的句柄ID，
         * 2.每个顶点属性的数组数量，（x,y）传2，（x,y,z）传3，（x,y,z,w）传4，
         * 3.指定数组中每个数组的数据类型
         * 4.固定点数据值是否归一化（GL_TRUE）或者直接转换为固定值（GL_FALSE）
         * 5.指定连续顶点属性之间的偏移量。如果为0，则顶点属性会被理解为：他们是被紧密排列再一起的，反之，这些指会被直接转换为浮点值而不是归一化处理
         * 6.顶点的缓冲数据
         * )
         */
        GLES20.glVertexAttribPointer(mVertexPosHandler, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glVertexAttribPointer(mTexturePosHandler, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);
        //开始绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    /**
     * gl_Position:设置顶点转换到屏幕坐标的位置
     * gl_pointSize:在粒子效果场景下，需要为粒子设置大小，改变内置变量的值就是为了设置每一个粒子矩形的大小
     * gl_FragColor:指定当前纹理坐标锁代表的像素点的最终颜色值
     *
     *
     *
     * @return
     */
    private String getVertexShader() {
        return "attribute vec4 aPosition;" +
                "attribute vec2 aCoordinate;" +
                "varying vec2 vCoordinate;" +
                "void main() {" +
                "  gl_Position = aPosition;" +
                "  vCoordinate = aCoordinate;" +
                "}";
    }

    private String getFragmentShader() {
        return "precision mediump float;" +
                "uniform sampler2D uTexture;" +
                "varying vec2 vCoordinate;" +
                "void main() {" +
                "  vec4 color = texture2D(uTexture, vCoordinate);" +
                "  gl_FragColor = color;" +
                "}";
    }

    private int loadShader(int type, String shaderCode) {
        //根据type创建顶点着色器或者片元着色器
        int shader = GLES20.glCreateShader(type);
        //将资源加入到着色器中，并编译
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
}

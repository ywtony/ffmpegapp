//package com.yw.ffmpeg.opengles.simple;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.opengl.GLES20;
//import android.opengl.GLSurfaceView;
//import android.opengl.GLUtils;
//import android.opengl.Matrix;
//import android.util.AttributeSet;
//import android.util.Log;
//
//import com.yw.ffmpeg.R;
//
//import java.nio.ByteBuffer;
//import java.nio.ByteOrder;
//import java.nio.FloatBuffer;
//import java.nio.ShortBuffer;
//
//import javax.microedition.khronos.egl.EGLConfig;
//import javax.microedition.khronos.opengles.GL10;
//
///**
// * @ProjectName: AndroidFFMpeg
// * @Package: com.yw.ffmpeg.opengles.simple
// * @ClassName: MyGLTextureView
// * @Description: 绘制一个简单的图片
// * @Author: wei.yang
// * @CreateDate: 2021/4/2 15:02
// * @UpdateUser: 更新者：wei.yang
// * @UpdateDate: 2021/4/2 15:02
// * @UpdateRemark: 更新说明：
// * @Version: 1.0
// */
//public class MyGLTextureView extends GLSurfaceView implements GLSurfaceView.Renderer {
//    /**
//     * 顶点着色器代码
//     */
//   private String vertexShader = "layout (location=0) in vec4 vPosition;" +
//            "layout (location=1) in vec2 aTextureCoord;" +
//            "uniform mat4 u_Matrix;" +
//            "out vec2 vTexCoord;" +
//            "void main(){" +
//            "gl_Position = u_Matrix*vPosition;" +
//            "gl_PointSize = 10.0;" +
//            "vTexCoord = aTextureCoord;}";
//    /**
//     * 片元着色器代码
//     */
//   private String fragmentShader = "" +
//            "precision mediump float;" +
//            "uniform smpler2D uTextureUnit;" +
//            "int vec2 vTexCoord;" +
//            "out vec4 vFragColor;" +
//            "void main(){vFragColor=texture(uTextureUnit,vTexCoord);}";
//    private  FloatBuffer vertexBuffer,mTexVertexBuffer;
//    private ShortBuffer mVertexIndexBuffer;
//    //glsl程序id
//    private int mProgram;
//    //纹理id
//    private int textureId;
//    //相机矩阵
//    private float[] mViewMatrix = new float[16];
//    //投影矩阵
//    private float[] mProjectMatrix = new float[16];
//    //最终变换矩阵
//    private float[] mMVPMatrix = new float[16];
//    //返回属性变量位置
//    //变换矩阵
//    private int uMatrixLocation;
//    //顶点
//    private int  aPositionLocation;
//    //纹理
//    private int aTextureLocation;
//    //顶点坐标
//    private float[] POSITION_VERTEX = new float[]{
//            0f, 0f, 0f,     //顶点坐标V0
//            1f, 1f, 0f,     //顶点坐标V1
//            -1f, 1f, 0f,    //顶点坐标V2
//            -1f, -1f, 0f,   //顶点坐标V3
//            1f, -1f, 0f     //顶点坐标V4
//    };
//    /**
//     * 纹理坐标
//     * (s,t)
//     */
//    private  final float[] TEX_VERTEX = {
//            0.5f, 0.5f, //纹理坐标V0
//            1f, 0f,     //纹理坐标V1
//            0f, 0f,     //纹理坐标V2
//            0f, 1.0f,   //纹理坐标V3
//            1f, 1.0f    //纹理坐标V4
//    };
//
//    /**
//     * 绘制顺序索引
//     */
//    private  final short[] VERTEX_INDEX = {
//            0, 1, 2,  //V0,V1,V2 三个顶点组成一个三角形
//            0, 2, 3,  //V0,V2,V3 三个顶点组成一个三角形
//            0, 3, 4,  //V0,V3,V4 三个顶点组成一个三角形
//            0, 4, 1   //V0,V4,V1 三个顶点组成一个三角形
//    };
//
//    //图片生成的位图
//    private Bitmap mBitmap;
//    private static final String TAG = "MyGLTextureView";
//    public MyGLTextureView() {
//        vertexBuffer = ByteBuffer.allocateDirect(POSITION_VERTEX.length * 4)
//                .order(ByteOrder.nativeOrder())
//                .asFloatBuffer();
//        vertexBuffer.put(POSITION_VERTEX);
//        vertexBuffer.position(0);
//
//        mTexVertexBuffer = ByteBuffer.allocateDirect(TEX_VERTEX.length * 4)
//                .order(ByteOrder.nativeOrder())
//                .asFloatBuffer()
//                .put(TEX_VERTEX);
//        mTexVertexBuffer.position(0);
//
//        mVertexIndexBuffer = ByteBuffer.allocateDirect(VERTEX_INDEX.length * 2)
//                .order(ByteOrder.nativeOrder())
//                .asShortBuffer()
//                .put(VERTEX_INDEX);
//        mVertexIndexBuffer.position(0);
//    }
//
//    public MyGLTextureView(Context context) {
//        super(context);
//    }
//
//    public MyGLTextureView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        //设置版本号
//        setEGLContextClientVersion(2);
//        //设置渲染器
//        setRenderer(this);
//        //设置渲染模式
//        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
//    }
//
//    @Override
//    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//        //将背景设置为黑色
//        GLES20.glClearColor(0.0f,0.0f,0.0f,1.0f);
//
//        //编译
//        final int vertexShaderId = ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.vertex_texture2d_shader));
//        final int fragmentShaderId = ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.fragment_texture2d_shader));
//        //链接程序片段
//        mProgram = ShaderUtils.linkProgram(vertexShaderId, fragmentShaderId);
//        //在OpenGLES环境中使用程序
//        GLES20.glUseProgram(mProgram);
//
//        //获取属性位置
//        uMatrixLocation = GLES20.glGetUniformLocation(mProgram, "u_Matrix");
//        aPositionLocation = GLES20.glGetAttribLocation(mProgram, "vPosition");
//        aTextureLocation = GLES20.glGetAttribLocation(mProgram,"aTextureCoord");
//        //加载纹理
//        textureId = loadTexture(getContext(), R.drawable.girl);
//    }
//
//    @Override
//    public void onSurfaceChanged(GL10 gl, int width, int height) {
////将背景设置为黑色
//        GLES20.glClearColor(0.0f,0.0f,0.0f,1.0f);
//
//        //编译
//        final int vertexShaderId = ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.vertex_texture2d_shader));
//        final int fragmentShaderId = ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.fragment_texture2d_shader));
//        //链接程序片段
//        mProgram = ShaderUtils.linkProgram(vertexShaderId, fragmentShaderId);
//        //在OpenGLES环境中使用程序
//        GLES20.glUseProgram(mProgram);
//
//        //获取属性位置
//        uMatrixLocation = GLES20.glGetUniformLocation(mProgram, "u_Matrix");
//        aPositionLocation = GLES20.glGetAttribLocation(mProgram, "vPosition");
//        aTextureLocation = GLES20.glGetAttribLocation(mProgram,"aTextureCoord");
//        //加载纹理
//        textureId = loadTexture(getContext(), R.drawable.texture2dshow);
//    }
//    private int loadTexture(Context context, int resourceId) {
//        final int[] textureIds = new int[1];
//        //创建一个纹理对象
//        GLES20.glGenTextures(1, textureIds, 0);
//        if (textureIds[0] == 0) {
//            Log.e(TAG, "Could not generate a new OpenGL textureId object.");
//            return 0;
//        }
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        //这里需要加载原图未经缩放的数据
//        options.inScaled = false;
//        mBitmap= BitmapFactory.decodeResource(context.getResources(), resourceId, options);
//        if (mBitmap == null) {
//            Log.e(TAG, "Resource ID " + resourceId + " could not be decoded.");
//            GLES20.glDeleteTextures(1, textureIds, 0);
//            return 0;
//        }
//        //绑定纹理到OpenGL
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0]);
//
//        //设置默认的纹理过滤参数
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
//
//        //加载bitmap到纹理中
//        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
//
//        //生成MIP贴图
//        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
//
//        //数据如果已经被加载进OpenGL,则可以回收该bitmap
//        mBitmap.recycle();
//
//        //取消绑定纹理
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
//
//        return textureIds[0];
//    }
//
//    @Override
//    protected void onSizeChanged(int x, int y, int width, int height) {
//        //设置绘制窗口
//        GLES20.glViewport(0, 0, width, height);
//
//        int w=mBitmap.getWidth();
//        int h=mBitmap.getHeight();
//        float sWH=w/(float)h;
//        float sWidthHeight=width/(float)height;
//        if(width>height){
//            if(sWH>sWidthHeight){
//                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight*sWH,sWidthHeight*sWH, -1,1, 3, 7);
//            }else{
//                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight/sWH,sWidthHeight/sWH, -1,1, 3, 7);
//            }
//        }else{
//            if(sWH>sWidthHeight){
//                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -1/sWidthHeight*sWH, 1/sWidthHeight*sWH,3, 7);
//            }else{
//                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -sWH/sWidthHeight, sWH/sWidthHeight,3, 7);
//            }
//        }
//        //设置相机位置
//        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
//        //计算变换矩阵
//        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);
//    }
//
//    @Override
//    public void onDrawFrame(GL10 gl) {
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
//
//        //将变换矩阵传入顶点渲染器
//        GLES20.glUniformMatrix4fv(uMatrixLocation,1,false,mMVPMatrix,0);
//        //启用顶点坐标属性
//        GLES20.glEnableVertexAttribArray(aPositionLocation);
//        GLES20.glVertexAttribPointer(aPositionLocation, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
//        //启用纹理坐标属性
//        GLES20.glEnableVertexAttribArray(aTextureLocation);
//        GLES20.glVertexAttribPointer(aTextureLocation, 2, GLES20.GL_FLOAT, false, 0, mTexVertexBuffer);
//        //激活纹理
//        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//        //绑定纹理
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
//        // 绘制
//        GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_INDEX.length, GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);
//
//        //禁止顶点数组的句柄
//        GLES20.glDisableVertexAttribArray(aPositionLocation);
//        GLES20.glDisableVertexAttribArray(aTextureLocation);
//    }
//}

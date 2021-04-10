package com.yw.ffmpeg.egl;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Surface;

import com.yw.ffmpeg.utils.LogUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.egl
 * @ClassName: EGLRender
 * @Description: EGL渲染器
 * @Author: wei.yang
 * @CreateDate: 2021/4/7 10:25
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/4/7 10:25
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class EGLRender extends HandlerThread {

    private static final String verticesShader = "attribute vec2 vPosition;" +
            "void main(){gl_Position=vec4(vPosition,0,1);}";
    private static final String fragmentShader = "precision mediump float;" +
            "uniform vec4 uColor;" +
            "void main(){gl_FragColor=uColor;}";

    public EGLRender(String name) {
        super(name);
    }

    private EGLDisplay eglDisplay = EGL14.EGL_NO_DISPLAY;
    private EGLConfig eglConfig = null;
    private EGLContext eglContext = EGL14.EGL_NO_CONTEXT;
    private int program;
    private int vPosition;
    private int uColor;

    /**
     * 创建OpenGLes环境
     */
    private void createGLES() {
        //获取默认显示设备
        eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        //初始化
        int[] version = new int[2];
        if (!EGL14.eglInitialize(eglDisplay, version, 0, version, 1)) {
            LogUtil.log("初始化失败");
            return;
        }
        //获取FrameBuffer格式和能力
        int[] configAttribs = {
                EGL14.EGL_BUFFER_SIZE, 32,
                EGL14.EGL_ALPHA_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL14.EGL_SURFACE_TYPE, EGL14.EGL_WINDOW_BIT,
                EGL14.EGL_NONE
        };
        int[] numConfigs = new int[1];
        EGLConfig[] configs = new EGLConfig[1];
        if (!EGL14.eglChooseConfig(eglDisplay, configAttribs, 0, configs, 0, configs.length, numConfigs, 0)) {
            LogUtil.log("config初始化失败");
            return;
        }
        eglConfig = configs[0];
        //创建EGL上下文
        int[] contextAttribs = {
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL14.EGL_NONE
        };
        eglContext = EGL14.eglCreateContext(eglDisplay, eglConfig, EGL14.EGL_NO_CONTEXT, contextAttribs, 0);
        if (eglContext == EGL14.EGL_NO_CONTEXT) {
            LogUtil.log("eglContext初始化失败");
            return;
        }


    }

    private void destoryEGL() {
        EGL14.eglDestroyContext(eglDisplay, eglContext);
        eglContext = EGL14.EGL_NO_CONTEXT;
        eglDisplay = EGL14.EGL_NO_DISPLAY;

    }

    @Override
    public synchronized void start() {
        super.start();
        new Handler(getLooper()).post(new Runnable() {
            @Override
            public void run() {
                createGLES();
            }
        });
    }

    public void release() {
        new Handler(getLooper()).post(new Runnable() {
            @Override
            public void run() {
                destoryEGL();
                quit();
            }
        });
    }

    /**
     * 加载shader
     *
     * @return 返回shaderid
     */
    private int loadShader(int shaderType, String sourceCode) {
        //创建一个shader
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, sourceCode);
            GLES20.glCompileShader(shader);
            //存放编译成功shader数量的数组
            int[] compiled = new int[1];
            //获取shader的编译情况
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                //编译失败
                LogUtil.log(GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }

        }
        return shader;
    }

    /**
     * 创建shader程序的方法
     */
    private int createProgram(String vertexSource, String fragmentSource) {
        //加载顶点做色漆
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }
        //加载片元着色器
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (fragmentShader == 0) {
            return 0;
        }
        //创建程序
        int program = GLES20.glCreateProgram();
        //如果程序创建成功则向程序中加入顶点着色器和片元着色器
        if (program != 0) {
            //向程序中加入顶点着色器和片元着色器
            GLES20.glAttachShader(program, vertexShader);
            GLES20.glAttachShader(program, fragmentShader);
            //链接程序
            GLES20.glLinkProgram(program);
            //存放链接成功programe数量的数组
            int[] linkStatus = new int[1];
            //获取program的链接情况
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            //如果连接报错，则删除程序
            if (linkStatus[0] != GLES20.GL_TRUE) {
                LogUtil.log(GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    private FloatBuffer getVertices() {
        float[] vertices = {
                0.0f, 0.5f,
                -0.5f, -0.5f,
                0.5f, -0.5f
        };
        //创建顶点坐标缓冲
        //因为一个float占四个字节
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());//设置字节顺序
        //转换为float类型缓冲
        FloatBuffer vBuffer = bb.asFloatBuffer();
        vBuffer.put(vertices);//向缓冲区中放入顶点数据
        //设置缓冲区的起始位置
        vBuffer.position(0);
        return vBuffer;

    }


    public void render(Surface surface, int width, int height) {
        final int[] surfaceAttribs = {EGL14.EGL_NONE};
        //创建EGLSurfface
        EGLSurface eglSurface = EGL14.eglCreateWindowSurface(eglDisplay, eglConfig, surface, surfaceAttribs, 0);
        EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext);
        //初始化着色器，
        //基于顶点着色器及片元着色器创建成宿
        program = createProgram(verticesShader, fragmentShader);
        //获取着色器中的属性引用id（传入的字符串就是着色器脚本中的属性）
        vPosition = GLES20.glGetAttribLocation(program, "vPosition");
        uColor = GLES20.glGetUniformLocation(program, "uColor");
        //设置清屏颜色值，而不执行清屏
        GLES20.glClearColor(1.0f, 0f, 0f, 1.0f);
        //设置绘图窗口
        GLES20.glViewport(0, 0, width, height);
        //获取图形顶点坐标
        FloatBuffer vBuffer = getVertices();
        //清屏
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        //使用某shader程序
        GLES20.glUseProgram(program);
        //为画笔指定顶点坐标数据
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, vBuffer);
        //允许顶点位置数据数组
        GLES20.glEnableVertexAttribArray(vPosition);
        //设置属性uColor
        GLES20.glUniform4f(uColor, 0.0f, 1.0f, 0.0f, 1.0f);
        //绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 3);
        //交换显存，将surface显存和显示器的显存交换
        EGL14.eglSwapBuffers(eglDisplay, eglSurface);
        EGL14.eglDestroySurface(eglDisplay, eglSurface);


    }
}

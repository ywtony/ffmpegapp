package com.yw.ffmpeg.opengles.simple;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.opengles.simple
 * @ClassName: ELShaderUtil
 * @Description: 着色器工具
 * @Author: wei.yang
 * @CreateDate: 2021/4/2 13:51
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/4/2 13:51
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public interface ELShaderUtil {
    /**
     * 创建三角形顶点着色器
     */
    String vertexShaderCode = "attribute vec4 vPosition;void main(){gl_Position=vPosition;}";
    /**
     * 创建三角形一个片元着色器
     */
    String fragmentShadeCode ="precision mediump float;uniform vec4 vColor;void main(){gl_FragColor=vColor;}";
}

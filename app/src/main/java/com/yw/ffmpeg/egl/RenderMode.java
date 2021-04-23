package com.yw.ffmpeg.egl;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.egl
 * @ClassName: EnumMode
 * @Description: 渲染模式
 * @Author: wei.yang
 * @CreateDate: 2021/4/12 10:11
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/4/12 10:11
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public enum RenderMode {
    //自动循环渲染
    RENDER_CONTINUOUSLY,
    //由外部通过notifySwap通知渲染
    RENDER_WHEN_DIRTY
}

package com.yw.ffmpeg.egl;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.egl
 * @ClassName: RenderState
 * @Description: 渲染状态枚举类
 * @Author: wei.yang
 * @CreateDate: 2021/4/8 11:14
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/4/8 11:14
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public enum RenderState {
    NO_SURFACE, //无效surface
    FRESH_SURFACE, //持有一个未初始化的新的surface
    SURFACE_CHANGE, // surface尺寸变化
    RENDERING, //初始化完毕，可以开始渲染
    SURFACE_DESTROY, //surface销毁
    STOP //停止绘制
}

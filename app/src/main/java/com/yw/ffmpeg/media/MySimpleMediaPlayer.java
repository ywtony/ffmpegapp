package com.yw.ffmpeg.media;

import android.view.Surface;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.media
 * @ClassName: MySimpleMediaPlayer
 * @Description: 最简单的视频播放器（ps：视频原生播放）
 * @Author: wei.yang
 * @CreateDate: 2021/3/3 13:39
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/3/3 13:39
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class MySimpleMediaPlayer {
    /**
     * 初始化视频播放器
     * 主要是初始化ffmpeg相关的东西
     *
     * @return
     */
    public native String init();

    /**
     * 创建一个播放器
     *
     * @param path
     * @param surface
     * @return
     */
    public native int createPlayer(String path, Surface surface);

    /**
     * 开始播放
     *
     * @param player
     */
    public native void play(int player);

    /**
     * 暂停播放
     *
     * @param player
     */
    public native void pause(int player);

    /**
     * 销毁播放器
     *
     * @param player
     */
    public native void destroy(int player);
}

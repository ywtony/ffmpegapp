package com.yw.ffmpeg.media;

import android.view.Surface;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.media
 * @ClassName: MediaInfo
 * @Description: 媒体文件基本信息
 * @Author: wei.yang
 * @CreateDate: 2021/3/3 10:00
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/3/3 10:00
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class MediaInfo {
    /**
     * @description
     * @date: 2021/3/3 13:22
     * @author: wei.yang
     */
    public native String ffMpegInfo();

    /**
     * 创建播放器
     * @param path
     * @param surface
     * @return
     */
    public native int createPlayer(String path, Surface surface);

    /**
     * 开始播放
     * @param player
     */
    public native void play(int player);

    /**
     * 暂停播放
     * @param player
     */
    public native void pause(int player);

    static {
        System.loadLibrary("native-lib");
    }

}

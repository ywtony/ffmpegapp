package com.yw.ffmpeg.utils;

import android.util.Log;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.utils
 * @ClassName: LogUtil
 * @Description: 日志工具
 * @Author: wei.yang
 * @CreateDate: 2021/4/7 10:09
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/4/7 10:09
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class LogUtil {
    private static final String TAG = "LogUtil";
    public static void log(String log){
        Log.e(TAG,log);
    }
}

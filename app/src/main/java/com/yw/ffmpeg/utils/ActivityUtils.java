package com.yw.ffmpeg.utils;

import android.content.Context;
import android.content.Intent;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.utils
 * @ClassName: ActivityUtils
 * @Description: java类作用描述
 * @Author: wei.yang
 * @CreateDate: 2021/3/23 9:35
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/3/23 9:35
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class ActivityUtils {
    private ActivityUtils(){}
    private static ActivityUtils instance = null;
    public static ActivityUtils getInstance(){
        if(instance==null){
            instance  = new ActivityUtils();
        }
        return instance;
    }


    public void  startActivity(Context context,String className){
        Intent intent = new Intent();
        intent.setClassName(context,className);
        context.startActivity(intent);

    }
}

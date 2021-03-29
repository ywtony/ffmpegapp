package com.yw.ffmpeg.opengles;

import android.opengl.GLES20;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.opengles
 * @ClassName: OpenGlTools
 * @Description: java类作用描述
 * @Author: wei.yang
 * @CreateDate: 2021/3/23 11:13
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/3/23 11:13
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class OpenGlTools {
    private OpenGlTools(){}
    private static OpenGlTools instance;
    public static OpenGlTools getInstance(){
        if(instance==null){
            instance = new OpenGlTools();
        }
        return instance;
    }
     /**
      * @method  创建一个纹理id
      * @description 
      * @date: 2021/3/23 13:06
      * @author: wei.yang
      * @param 
      * @return 
      */
    public int[] createTextureIds(int count){
        int[] texture = new int[count];
        //生成纹理
        GLES20.glGenTextures(count,texture,0);
        return texture;
    }
}

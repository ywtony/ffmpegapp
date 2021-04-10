package com.yw.ffmpeg.opengles;

import android.graphics.SurfaceTexture;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.opengles
 * @ClassName: IDrawer
 * @Description: java类作用描述
 * @Author: wei.yang
 * @CreateDate: 2021/3/23 11:09
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/3/23 11:09
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public interface IDrawer {
    /**
     * 绘制
     */
    void draw();

    /**
     * 设置纹理id
     * @param textureId
     */
    void setTextureId(int textureId);

    /**
     * 释放
     */
    void release();
    void setWorldSize(int worldW ,int worldH);

    /**
     * 设置视频的原始宽高
     * @param videoW 宽
     * @param videoH 高
     */
    void  setVideoSize(int videoW,int videoH);
    default  SurfaceTexture getSurfaceTexture(){
        return null;
    };
}

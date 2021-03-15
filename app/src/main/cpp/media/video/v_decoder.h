//视频解码器
/// // Created by wei.yang on 2021/3/10.
//

#ifndef ANDROIDFFMPEG_V_DECODER_H
#define ANDROIDFFMPEG_V_DECODER_H

#include "../base_decoder.h"
#include <jni.h>
#include "../../render/video/video_render.h"

extern "C" {
#include <libavutil/imgutils.h>
#include <libswscale/swscale.h>
};

class VideoDecoder : public BaseDecoder {
private:
    const char *TAG = "VideoDecoder";
    //视频数据目标格式
    const AVPixelFormat DST_FORMAT = AV_PIX_FMT_RGBA;
    //存放yuv转换为RGB后的数据
    AVFrame *m_rgb_frame = NULL;
    uint8_t *m_buf_for_rgb_frame = NULL;
    //视频格式转换器
    SwsContext *m_sws_ctx = NULL;
    //视频渲染器
    VideoRender *m_video_render = NULL;
    //显示目标的宽
    int m_dst_w;
    //显示目标的高
    int m_dst_h;

    /**
     * 初始化渲染器
     */
    void initRender(JNIEnv *env);

    /**
     * 初始化显示器
     */
    void initBuffer();

    /**
     * 初始化视频数据转换器
     */
    void initSws();

public:
    VideoDecoder(JNIEnv *env, jstring path, bool for_synthesizer = false);

    ~VideoDecoder();

    void setRender(VideoRender *render);

protected:
    AVMediaType getMediaType() override {
        return AVMEDIA_TYPE_VIDEO;
    }

    /**
     * 是否需要循环解码
     */
    bool needLoopDecode() override;

    /**
     * 准备解码环境
     * ps:在解码线程中回调
     */
    void prepare(JNIEnv *env) override;

    /**
     * 渲染
     * ps：在解码线程中回调
     */
    void render(AVFrame *avFrame) override;

    /**
     * 释放回调
     */
    void release() override;

    const char *const LogSpec() override {
        return "VIDEO";
    };

};

#endif //ANDROIDFFMPEG_V_DECODER_H

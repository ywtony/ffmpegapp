//
// Created by wei.yang on 2021/3/10.
//

#ifndef ANDROIDFFMPEG_NATIVE_RENDER_H
#define ANDROIDFFMPEG_NATIVE_RENDER_H

#include <android/native_window.h>
#include <android/native_window_jni.h>
#include <jni.h>
#include "video_render.h"
#include "../../utils/logger.h"
#include "../../media/one_frame.h"

extern "C" {
#include <libavutil/mem.h>
};


class NativeRender : public VideoRender {
private:
    const char *TAG = "Native Render";
    //surface引用，必须使用引用，否则无法再线程中操作
    jobject m_surface_ref = NULL;
    //存放输出到屏幕上的缓存数据
    ANativeWindow_Buffer m_out_buffer;
    //本地窗口
    ANativeWindow *m_native_window = NULL;
    //显示目标的宽
    int m_dst_w;
    //显示目标的高
    int m_dst_height;
public:
    NativeRender(JNIEnv *env, jobject surface);

    ~NativeRender();

    void initRender(JNIEnv *env, int video_width, int video_height, int *dst_size) override;

    void render(OneFrame *oneFrame) ;

    void releaseRender() override;

};

#endif //ANDROIDFFMPEG_NATIVE_RENDER_H

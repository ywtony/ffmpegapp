//
// Created by wei.yang on 2021/3/10.
//
#include <string.h>
#include "native_render.h"
#include "../../media/one_frame.h"
#include "../../utils/logger.h"
#include <android/native_window.h>
#include <android/native_window_jni.h>
NativeRender::NativeRender(JNIEnv *env,jobject surface){
    m_surface_ref = surface;
}
NativeRender::~NativeRender(){

}

//初始化渲染器
void NativeRender::initRender(JNIEnv *env, int video_width, int video_height, int *dst_size) {
    //初始化窗口
    m_native_window = ANativeWindow_fromSurface(env,m_surface_ref);
    //绘制区域的宽高
    int windowWidth = ANativeWindow_getWidth(m_native_window);
    int windowHeight = ANativeWindow_getHeight(m_native_window);
    //计算目标视频的宽高
    m_dst_w = windowWidth;
    m_dst_height = m_dst_w*video_height/video_width;
    if(m_dst_height>windowHeight){
        m_dst_height = windowHeight;
        m_dst_w = windowHeight*video_width/video_height;
    }
    LOGE(TAG, "windowW: %d, windowH: %d, dstVideoW: %d, dstVideoH: %d",
         windowWidth, windowHeight, m_dst_w, m_dst_height);
    //设置宽高限制缓冲区中的像素数量
    ANativeWindow_setBuffersGeometry(m_native_window,windowWidth,windowHeight,WINDOW_FORMAT_RGBA_8888);
    dst_size[0] = m_dst_w;
    dst_size[1] = m_dst_height;
}
/**
 * 渲染一帧数据
 * @param oneFrame
 */
void NativeRender::render(OneFrame *oneFrame) {
    //锁定窗口
    ANativeWindow_lock(m_native_window,&m_out_buffer,NULL);
    uint8_t *dst = (uint8_t*)m_out_buffer.bits;
    //获取stride：一行可以保存的内存像素数量*4（即RGBA的位数）
    int dstStride = m_out_buffer.stride*4;
    int srcStride = oneFrame->line_size;
    //由于window的stride和帧的stride不同，因此需要进行逐行赋值
    for(int h=0;h<m_dst_height;h++){
        memcpy(dst+h*dstStride,oneFrame->data+h*srcStride,srcStride);
    }
    //释放窗口
    ANativeWindow_unlockAndPost(m_native_window);
}
/**
 * 手动释放内存空间
 */
void NativeRender::releaseRender() {
    if(m_native_window!=NULL){
        ANativeWindow_release(m_native_window);
    }
    av_free(&m_out_buffer);
}



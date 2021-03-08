//音视频解码基类
// Created by wei.yang on 2021/3/3.
//

#include "base_decoder.h"
#include "../utils/timer.c"

using namespace std;

BaseDecoder::BaseDecoder(JNIEnv *env, jstring path, bool for_synthesizer) {
    init(env, path);//初始化
    //创建解码线程
    createDecodeThread();

}

/**
 * 析构函数，类执行完成释放内存
 */
BaseDecoder::~BaseDecoder() {
    if (ctx != NULL) delete ctx;
    if (mCodecCtx != NULL) delete mCodecCtx;
    if (mFrame != NULL) delete mFrame;
    if (mPacket != NULL) delete mPacket;
}

/**
 * 初始化
 * @param env
 * @param path
 */
void BaseDecoder::init(JNIEnv *env, jstring path) {
    m_path_ref = env->NewGlobalRef(path);
    m_path = env->GetStringUTFChars(path, NULL);
    //获取JVM虚拟机，为创建线程做准备
    env->GetJavaVM(&m_jvm_for_thread);
}

/**
 * 创建一个解码线程
 */
void BaseDecoder::createDecodeThread() {
    //使用智能指针，线程结束时自动删除本类指针
    shared_ptr<BaseDecoder> that(this);
    thread t(Decode, that);
    t.detach();
}
/**
 * 解码
 * @param that
 */
void BaseDecoder::Decode(shared_ptr<BaseDecoder> that) {
    JNIEnv *env;
    //将线程附加到虚拟机，并获取env
    if (that->m_jvm_for_thread->AttachCurrentThread(&env, NULL) != JNI_OK) {
        LOG_ERROR(that->TAG, that->LogSpec(), "Fail to Init decode thread");
        return;
    }
    //状态切换为准备阶段
    that->callbackState(PREPARE);
    //初始化FFMpeg
    that->initFFMpegDecoder(env);

}
/**
 * 初始化FFmpeg
 * @param env
 */
void BaseDecoder::initFFMpegDecoder(JNIEnv *env) {
    //初始化文件格式上下文
    ctx = avformat_alloc_context();
    //打开文件
    if(avformat_open_input(&ctx,m_path,NULL,NULL)!=0);
}


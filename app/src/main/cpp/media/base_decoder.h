//基础解码器头文件,基类
//
// Created by wei.yang on 2021/3/3.
//

#ifndef ANDROIDFFMPEG_BASE_DECODER_H
#define ANDROIDFFMPEG_BASE_DECODER_H

#include <jni.h>
#include <string>
#include <thread>
#include "i_decoder.h"
#include "decode_state.h"
#include "../utils/logger.h"

extern "C" {
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libavutil/frame.h>
#include <libavutil/time.h>
}
using namespace std;

/**
 * 编解码器的基类
 */
class BaseDecoder : public IDecoder {
private:
    const char *TAG = "BaseDecoder";
    //定义解码相关的上下文以及变量
    //解码信息的上下文
    AVFormatContext *ctx = NULL;
    //解码器
    AVCodec *mCodec = NULL;
    //解码器上下文
    AVCodecContext *mCodecCtx = NULL;
    //待解码包
    AVPacket *mPacket = NULL;
    //最终解码数据
    AVFrame *mFrame = NULL;
    //当前播放时间
    int64_t m_cur_t_s = 0;
    //总时长
    long mDuration = 0;
    //开始播放时间
    int64_t m_started_t = -1;
    //解码状态
    DecodeState mState = STOP;
    //数据索引流
    int m_stream_index = -1;
    // -------------------定义线程相关-----------------------------
    //线程依附的JVM环境
    JavaVM *m_jvm_for_thread = NULL;
    //原始路径jstring的引用
    jobject m_path_ref = NULL;
    //经过转换的路径
    const char *m_path = NULL;
    //线程等待锁变量
    pthread_mutex_t m_mutex = PTHREAD_MUTEX_INITIALIZER;
    //条件
    pthread_cond_t m_cond = PTHREAD_COND_INITIALIZER;
    //为合成器提供解码
    bool m_for_synthesizer = false;
    //-----------------私有方法------------------------------------
    /**
     * 初始化解码器
     * @param env  jvm环境变量
     * @param path 本地文件路径
     */
    void init(JNIEnv *env, jstring path);

    /**
     * 初始化FFmpeg相关的参数
     * @param env jvm环境变量
     */
    void initFFMpegDecoder(JNIEnv *env);

    /**
     * 分配解码过程中需要的缓存
     */
    void allocFrameBuffer();

    /**
     * 新建解码线程
     */
    void createDecodeThread();

    /**
     * 循环解码
     */
    void loopDecode();

    /**
     * 获取当前时间戳
     */
    void obtainTimeStamp();

    /**
     * 解码完成
     * @param env jvm 环境
     */
    void doneDecode(JNIEnv *env);

    /**
     * 静态解码方法，用于解码线程回调
     * @param that  当前解码器
     */
    static void Decode(shared_ptr<BaseDecoder> that);

    /**
     * 时间同步
     */
    void syncRender();

public:
    BaseDecoder(JNIEnv *env, jstring path, bool for_synthesizer);

    virtual ~BaseDecoder();

    /**
     * 视频宽度
     * @return
     */
    int width() {
        return mCodecCtx->width;
    }

    /**
     * 视频高度
     * @return
     */
    int height() {
        return mCodecCtx->height;
    }

    /**
     * 视频的总时长
     * @return
     */
    long duration() {
        return mDuration;
    }

    void goOn() override;

    void pause() override;

    void stop() override;

    bool isRunning() override;

    long getDuration() override;

    long getCurPos() override;

    void setStateReceiver(IDecoderStateCb *cb) override {
        m_state_cb = cb;
    };

    char *getStateStr() {
        switch (mState) {
            case STOP:
                return (char *) "STOP";
            case START:
                return (char *) "START";
            case DECODING:
                return (char *) "DECODING";
            case PAUSE:
                return (char *) "PAUSE";
            case FINISH:
                return (char *) "FINISH";
            default:
                return (char *) "UNKNOW";

        }
    }

protected:
    IDecoderStateCb *m_state_cb = NULL;

    /**
     * 是否为合成器提供解码
     * @return true 为合成器提供解码，false解码播放
     */
    bool forSynthesizer() {
        return m_for_synthesizer;
    }

    const char *path() {
        return m_path;
    }

    /**
     * 解码器上下文
     * @return
     */
    AVCodecContext *codecContext() {
        return mCodecCtx;
    }

    /**
     * 视频数据编解码格式
     */
    AVPixelFormat video_pixel_format() {
        return mCodecCtx->pix_fmt;
    }

    /**
     * 获取解码时间基
     */
    AVRational time_base() {
        return ctx->streams[m_stream_index]->time_base;
    }

    /**
     * 解码一帧数据
     */
    AVFrame *decodeOneFrame();

    /**
     * 返回多媒体类型
     * @return
     */
    virtual AVMediaType getMediaType() = 0;

    /**
     * 是否需要循环解码
     * @return
     */
    virtual bool needLoopDecode() = 0;

    /**
     * 子类准备回调方法
     * 在解码线程中回调，并在解码线程中绑定JVM环境
     * @param env
     */
    virtual void prepare(JNIEnv *env) = 0;

    /**
     * 子类渲染回调方法
     * @param frame
     */
    virtual void render(AVFrame *frame) = 0;

    /**
     * 子类释放资源回调方法
     */
    virtual void release() = 0;

    /**
     * log前缀
     */
    virtual const char *const LogSpec() = 0;

    /**
     * 进入等待
     * @param second
     * @param ms
     */
    void wait(long second = 0, long ms = 0);

    /**
     * 恢复解码
     */
    void sendSignal();

    void callbackState(DecodeState status);
};

#endif //ANDROIDFFMPEG_BASE_DECODER_H

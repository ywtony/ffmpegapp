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
    if (avformat_open_input(&ctx, m_path, NULL, NULL) != 0) {
        LOG_ERROR(TAG, LogSpec(), "Fail to open file [%s]", m_path);
        doneDecode(env);
        return;
    }
    //获取音视频流信息
    if (avformat_find_stream_info(ctx, NULL) < 0) {
        LOG_ERROR(TAG, LogSpec(), "Fail to find stream info");
        doneDecode(env);
        return;
    }
    //查找编解码器
    //获取视频流的索引
    int vIndex = -1;//用于存放视频流的索引
    for (int i = 0; i < ctx->nb_streams; i++) {
        if (ctx->streams[i]->codecpar->codec_type == getMediaType()) {
            vIndex = i;
            break;
        }
    }
    if (vIndex == -1) {
        LOG_ERROR(TAG, LogSpec(), "Fail to find stream index")
        doneDecode(env);
        return;
    }
    m_stream_index = vIndex;
    //获取解码器参数
    AVCodecParameters *codecPar = ctx->streams[vIndex]->codecpar;
    //获取解码器
    mCodec = avcodec_find_decoder(codecPar->codec_id);
    //获取解码器上下文
    mCodecCtx = avcodec_alloc_context3(mCodec);
    if (avcodec_parameters_to_context(mCodecCtx, codecPar) != 0) {
        LOG_ERROR(TAG, LogSpec(), "Fail to obtain av codec context");
        doneDecode(env);
        return;
    }
    //打开解码器
    if (avcodec_open2(mCodecCtx, mCodec, NULL) < 0) {
        LOG_ERROR(TAG, LogSpec(), "Fail to open av codec");
        doneDecode(env);
        return;
    }
    mDuration = (long) ((float) ctx->duration / AV_TIME_BASE * 1000);
    LOG_INFO(TAG, LogSpec(), "Decoder init success")
}

void BaseDecoder::allocFrameBuffer() {
    //初始化待解码和解码数据结构
    //初始化AVPAcket存放解码前的数据
    mPacket = av_packet_alloc();
    //初始化AVFrame存放解码后的数据
    mFrame = av_frame_alloc();

}

/**
 * 循环解码
 */
void BaseDecoder::loopDecode() {
    if (STOP == mState) {//如果已经被外部改变状态，则维持外部配置
        mState = START;
    }
    callbackState(START);
    LOG_INFO(TAG, LogSpec(), "Start loop decode");
    while (1) {
        if (mState != DECODING &&
            mState != START &&
            mState != STOP) {
            callbackState(mState);
            wait();
            callbackState(mState);
            // 恢复同步起始时间，去除等待流失的时间
            m_started_t = GetCurMsTime() - m_cur_t_s;
        }
        if (mState == STOP) {
            break;
        }
        if (-1 == m_started_t) {
            m_started_t = GetCurMsTime();
        }
        if (decodeOneFrame() != NULL) {
            syncRender();
            render(mFrame);
            if (mState == START) {
                mState = PAUSE;
            }
        } else {
            LOG_INFO(TAG, LogSpec(), "m_state = %d", mState)
            if (forSynthesizer()) {
                mState = STOP;
            } else {
                mState = FINISH;
            }
            callbackState(FINISH);
        }

    }
}

AVFrame *BaseDecoder::decodeOneFrame() {
    int ret = av_read_frame(ctx, mPacket);
    while (ret == 0) {
        if (mPacket->stream_index == m_stream_index) {
            switch (avcodec_send_packet(mCodecCtx, mPacket)) {
                case AVERROR_EOF: {
                    av_packet_unref(mPacket);
                    LOG_ERROR(TAG, LogSpec(), "Decode error: %s", av_err2str(AVERROR_EOF));
                    return NULL; //解码结束
                }
                case AVERROR(EAGAIN):
                    LOG_ERROR(TAG, LogSpec(), "Decode error: %s", av_err2str(AVERROR(EAGAIN)));
                    break;
                case AVERROR(EINVAL):
                    LOG_ERROR(TAG, LogSpec(), "Decode error: %s", av_err2str(AVERROR(EINVAL)));
                    break;
                case AVERROR(ENOMEM):
                    LOG_ERROR(TAG, LogSpec(), "Decode error: %s", av_err2str(AVERROR(ENOMEM)));
                    break;
                default:
                    break;
            }
            //这里需要考虑一个packet有可能包含多个frame的情况
            int result = avcodec_receive_frame(mCodecCtx, mFrame);
            if (result == 0) {
                obtainTimeStamp();
                av_packet_unref(mPacket);
                return mFrame;
            } else {
                LOG_INFO(TAG, LogSpec(), "Receive frame error result: %s",
                         av_err2str(AVERROR(result)))
            }
        }
        //释放packet
        av_packet_unref(mPacket);
        ret = av_read_frame(ctx, mPacket);

    }
    av_packet_unref(mPacket);
    LOGI(TAG, "ret = %s", av_err2str(AVERROR(ret)))
    return NULL;
}

void BaseDecoder::callbackState(DecodeState status) {
    if (mState != NULL) {
        switch (status) {
            case PREPARE:
                m_state_cb->decodePrepare(this);
                break;
            case START:
                m_state_cb->decoderReady(this);
                break;
            case DECODING:
                m_state_cb->decoderRunning(this);
                break;
            case PAUSE:
                m_state_cb->decoderPause(this);
                break;
            case FINISH:
                m_state_cb->decoderFinish(this);
                break;
            case STOP:
                m_state_cb->decoderStop(this);
                break;
        }
    }
}

void BaseDecoder::obtainTimeStamp() {
    if (mFrame->pkt_dts != AV_NOPTS_VALUE) {
        m_cur_t_s = mPacket->dts;
    } else if (mFrame->pts != AV_NOPTS_VALUE) {
        m_cur_t_s = mFrame->pts;
    } else {
        m_cur_t_s = 0;
    }
    m_cur_t_s = (int64_t) ((m_cur_t_s * av_q2d(ctx->streams[m_stream_index]->time_base)) *
                           1000);
}

void BaseDecoder::syncRender() {
    if (forSynthesizer()) {
//        av_usleep(15000);
        return;
    }
    int64_t ct = GetCurMsTime();
    int64_t passTime = ct - m_started_t;
    if (m_cur_t_s > passTime) {
        av_usleep((unsigned int) ((m_cur_t_s - passTime) * 1000));
    }
}

void BaseDecoder::wait(long second, long ms) {
//    LOG_INFO(TAG, LogSpec(), "Decoder run into wait, state：%s", GetStateStr())
    pthread_mutex_lock(&m_mutex);
    if (second > 0 || ms > 0) {
        timeval now;
        timespec outtime;
        gettimeofday(&now, NULL);
        int64_t destNSec = now.tv_usec * 1000 + ms * 1000000;
        outtime.tv_sec = static_cast<__kernel_time_t>(now.tv_sec + second + destNSec / 1000000000);
        outtime.tv_nsec = static_cast<long>(destNSec % 1000000000);
        pthread_cond_timedwait(&m_cond, &m_mutex, &outtime);
    } else {
        pthread_cond_wait(&m_cond, &m_mutex);
    }
    pthread_mutex_unlock(&m_mutex);
}

void BaseDecoder::doneDecode(JNIEnv *env) {
    LOG_INFO(TAG, LogSpec(), "Decode done and decoder release");
    //释放内存
    if (mPacket != NULL) {
        av_packet_free(&mPacket);
    }
    if (mFrame != NULL) {
        av_frame_free(&mFrame);
    }
    //关闭解码器
    if (mCodecCtx != NULL) {
        avcodec_close(mCodecCtx);
        avcodec_free_context(&mCodecCtx);
    }
    //关闭输入流
    if (ctx != NULL) {
        avformat_close_input(&ctx);
        avformat_free_context(ctx);
    }
    //释放转换参数
    if (m_path_ref != NULL && m_path != NULL) {
        env->ReleaseStringUTFChars((jstring) m_path_ref, m_path);
        env->DeleteGlobalRef(m_path_ref);
    }
    //通知子类释放资源
    release();
}

void BaseDecoder::sendSignal() {
//    LOG_INFO(TAG, LogSpec(), "Decoder wake up, state: %s", GetStateStr())
    pthread_mutex_lock(&m_mutex);
    pthread_cond_signal(&m_cond);
    pthread_mutex_unlock(&m_mutex);
}

void BaseDecoder::goOn() {
    mState = DECODING;
    sendSignal();
}

void BaseDecoder::pause() {
    mState = PAUSE;
}

void BaseDecoder::stop() {
    mState = STOP;
    sendSignal();
}

bool BaseDecoder::isRunning() {
    return DECODING == mState;
}

long BaseDecoder::getDuration() {
    return mDuration;
}

long BaseDecoder::getCurPos() {
    return (long) m_cur_t_s;
}


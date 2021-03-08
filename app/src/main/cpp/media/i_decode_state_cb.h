//编码回调接口定义// Created by wei.yang on 2021/3/8.
//

#ifndef ANDROIDFFMPEG_I_DECODE_STATE_CB_H
#define ANDROIDFFMPEG_I_DECODE_STATE_CB_H

#include "one_frame.h"

//生命IDecoder
class IDecoder;

class IDecoderStateCb {
public:
    IDecoderStateCb();

    /**
     * 解码准备
     * @param decoder
     */
    virtual void decodePrepare(IDecoder *decoder) = 0;

    /**
     * 解码器已准备好
     * @param decoder
     */
    virtual void decoderReady(IDecoder *decoder);

    /**
     * 开始解码
     * @param decoder
     */
    virtual void decoderRunning(IDecoder *decoder);

    /**
     * 暂停解码
     * @param decoder
     */
    virtual void decoderPause(IDecoder *decoder);

    /**
     * 解码一帧数据
     * @param decoder
     * @param oneFrame
     * @return
     */
    virtual bool decoderOneFrame(IDecoder *decoder, OneFrame *oneFrame);

    /*
     * 解码结束
     */
    virtual void decoderFinish(IDecoder *decoder);

    /**
     * 停止解码
     * @param decoder
     */
    virtual void decoderStop(IDecoder *decoder);
};

#endif //ANDROIDFFMPEG_I_DECODE_STATE_CB_H

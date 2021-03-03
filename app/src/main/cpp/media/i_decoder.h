//定义解码器
// Created by wei.yang on 2021/3/3.
//

#ifndef ANDROIDFFMPEG_I_DECODER_H
#define ANDROIDFFMPEG_I_DECODER_H

class IDecoder {
public:
    virtual void goOn() = 0;

    virtual void pause() = 0;

    virtual void stop() = 0;

    virtual bool isRunning() = 0;

    virtual long getDuration() = 0;

    virtual long getCurPos() = 0;

};

#endif //ANDROIDFFMPEG_I_DECODER_H

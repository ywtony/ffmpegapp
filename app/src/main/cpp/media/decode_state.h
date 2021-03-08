//定义解码状态// Created by wei.yang on 2021/3/3.
//
#ifndef ANDROIDFFMPEG_DECODE_STATE_H
#define ANDROIDFFMPEG_DECODE_STATE_H
//定义一个枚举，来表示解码的状态
enum DecodeState {
    STOP,//停止
    PREPARE,//准备
    START,//开始
    DECODING,//解码中
    PAUSE,//暂停
    FINISH//结束
};
#endif //ANDROIDFFMPEG_DECODE_STATE_H

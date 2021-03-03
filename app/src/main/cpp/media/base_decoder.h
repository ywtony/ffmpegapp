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

extern "C"{
#include <libavcodec/av>
};

#endif //ANDROIDFFMPEG_BASE_DECODER_H

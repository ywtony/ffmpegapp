// 自定义编解码常量
// Created by wei.yang on 2021/3/4.
//

#ifndef ANDROIDFFMPEG_CONST_H
#define ANDROIDFFMPEG_CONST_H
extern "C" {
#include <libavutil/samplefmt.h>
};

//音频编码格式：浮点型数据（32位）
static const AVSampleFormat ENCODE_AUDIO_DEST_FORMAT = AV_SAMPLE_FMT_FLTP;
//音频编码采样率
static const int ENCODE_AUDIO_DEST_SAMPLE_RATE = 44100;
//音频编码声道数
static const int ENCODE_AUDIO_DEST_CHANNELS_COUNTS = 2;
//以您编码声道格式
static const int ENCODE_AUDIO_DEST_CHANGLE_LAYOUT = AV_CH_LAYOUT_STEREO;
//音频编码比特率
static const int ENCODE_AUDIO_DEST_BIT_RATE = 64000;
//aac音频一帧采样数
static const int AAC_NB_SAMPLES = 1024;
//视频编码帧数
static const int ENCODE_VIDEO_FPS = 25;
#endif //ANDROIDFFMPEG_CONST_H

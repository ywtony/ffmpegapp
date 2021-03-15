//
// Created by wei.yang on 2021/3/10.
//

#ifndef ANDROIDFFMPEG_PLAYER_H
#define ANDROIDFFMPEG_PLAYER_H

#include <jni.h>
#include "../media/video/v_decoder.h"
#include "../render/video/native_render.h"
#include "../render/video/video_render.h"
class Player {
private:
    VideoDecoder *m_v_decoder;
    VideoRender *m_v_render;


public:
    Player(JNIEnv *jniEnv, jstring path, jobject surface);
    ~Player();

    void play();
    void pause();
};

#endif //ANDROIDFFMPEG_PLAYER_H

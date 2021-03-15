//
// Created by wei.yang on 2021/3/10.
//

#ifndef ANDROIDFFMPEG_VIDEO_RENDER_H
#define ANDROIDFFMPEG_VIDEO_RENDER_H

#include <jni.h>
#include <stdint.h>
#include "../../media/one_frame.h"

class VideoRender {
public:
    virtual void initRender(JNIEnv *env, int video_width, int video_height, int *dst_size) = 0;

    virtual void render(OneFrame *oneFrame)=0;

    virtual void releaseRender()=0;
};

#endif //ANDROIDFFMPEG_VIDEO_RENDER_H

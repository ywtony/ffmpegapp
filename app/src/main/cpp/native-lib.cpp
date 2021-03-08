#include <jni.h>
#include <string>
#include <stdio.h>
#include <iostream>
#include "utils/logger.h"
#include "media/base_decoder.h"
#include "media/const.h"
#include "media/one_frame.h"
#include "utils/timer.c"
extern "C" {
#include "libavformat/avformat.h"
}
using namespace std;
extern "C" JNIEXPORT jstring JNICALL
Java_com_yw_ffmpeg_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    string hello = avcodec_configuration();
    int code = avcodec_version();
    //c++ int转字符串
    string str = to_string(code);
    return env->NewStringUTF(str.c_str());
}

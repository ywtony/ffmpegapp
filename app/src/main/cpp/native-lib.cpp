#include <jni.h>
#include <string>
#include "libavformat/avformat.h"

extern "C" JNIEXPORT jstring JNICALL
Java_com_yw_ffmpeg_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    avcodec_version();
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

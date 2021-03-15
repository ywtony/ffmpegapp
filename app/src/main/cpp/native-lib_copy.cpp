//#include <jni.h>
//#include <string>
//#include <stdio.h>
//#include <iostream>
//
//#include "play/player.h"
//
//extern "C" {
//#include "libavformat/avformat.h"
//}
//using namespace std;
//extern "C" JNIEXPORT jstring JNICALL
//Java_com_yw_ffmpeg_MainActivity_stringFromJNI(
//        JNIEnv *env,
//        jobject /* this */) {
//    string hello = avcodec_configuration();
//    int code = avcodec_version();
//    //c++ int转字符串
//    string str = to_string(code);
//    return env->NewStringUTF(str.c_str());
//}
//extern "C" {
//JNIEXPORT jint  JNICALL
//Java_com_yw_ffmpeg_media_MySimpleMediaPlayer_createPlayer(JNIEnv *env, jobject, jstring path,
//                                                          jobject surface) {
//    Player *player = new Player(env, path, surface);
//    return (jint) player;
//}
//JNIEXPORT void JNICALL
//Java_com_yw_ffmpeg_media_MySimpleMediaPlayer_play(JNIEnv *env,
//                                                  jobject  /* this */,
//                                                  jint player) {
//    Player *p = (Player *) player;
//    p->play();
//}
//
//JNIEXPORT void JNICALL
//Java_com_yw_ffmpeg_media_MySimpleMediaPlayer_pause(JNIEnv *env,
//                                                   jobject  /* this */,
//                                                   jint player) {
//    Player *p = (Player *) player;
//    p->pause();
//}
//}

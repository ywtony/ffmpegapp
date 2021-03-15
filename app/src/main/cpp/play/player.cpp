//
// Created by wei.yang on 2021/3/10.
//

#include "player.h"

Player::Player(JNIEnv *jniEnv, jstring path, jobject surface) {
    m_v_decoder = new VideoDecoder(jniEnv, path);

    // 本地窗口播放
    m_v_render = new NativeRender(jniEnv, surface);
    m_v_decoder->setRender(m_v_render);
}

Player::~Player() {
    // 此处不需要 delete 成员指针
    // 在BaseDecoder中的线程已经使用智能指针，会自动释放
}

void Player::play() {
    if (m_v_decoder != NULL) {
        m_v_decoder->goOn();
    }
}

void Player::pause() {
    if (m_v_decoder != NULL) {
        m_v_decoder->pause();
    }
}
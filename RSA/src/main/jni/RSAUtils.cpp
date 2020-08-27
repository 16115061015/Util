//
// Created by hzy on 2020/8/25.
//
#include "Java_com_hzy_rsa_RSA_RSAUtils_rsaLock.h"

extern "C"
JNIEXPORT jstring JNICALL
Java_com_hzy_rsa_RSA_RSAUtils_rsaLock(JNIEnv *env, jclass clazz) {
    return env->NewStringUTF("123456");
}
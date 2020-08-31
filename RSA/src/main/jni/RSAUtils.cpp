//
// Created by hzy on 2020/8/25.
//
#include "RSAUtils.h"
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif
JNIEXPORT jstring JNICALL
Java_com_hzy_rsa_RSA_RSAUtils_rsaLock(JNIEnv *env, jclass clazz) {
    return env->NewStringUTF("123456");
}

#ifdef __cplusplus
}
#endif
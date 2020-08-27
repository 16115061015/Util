LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_LDLIBS += -llog
LOCAL_MODULE := RSALib
LOCAL_SRC_FILES =: Java_com_hzy_rsa_RSA_RSAUtils_rsaLock.cpp
include $(BUILD_SHARED_LIBRARY)
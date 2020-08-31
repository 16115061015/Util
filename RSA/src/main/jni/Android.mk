LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_LDLIBS += -llog
LOCAL_MODULE := RSALib
LOCAL_SRC_FILES =: RSAUtils.cpp
include $(BUILD_SHARED_LIBRARY)
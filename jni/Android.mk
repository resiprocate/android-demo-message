LOCAL_PATH := $(call my-dir)

RESIP_HOME := $(HOME)/ws/resiprocate/resip-droid
OPENSSL_HOME := $(HOME)/ws/openssl/openssl-android/include

include $(CLEAR_VARS)
LOCAL_MODULE := BasicPhone
LOCAL_MODULE_FILENAME := libBasicPhone
LOCAL_SRC_FILES := BasicPhone.cpp
LOCAL_C_INCLUDES := $(RESIP_HOME)
LOCAL_C_INCLUDES += $(OPENSSL_HOME)/include
LOCAL_CFLAGS := 
LOCAL_CPP_FEATURES += exceptions
LOCAL_CPP_FEATURES += rtti
LOCAL_SHARED_LIBRARIES += crypto
LOCAL_SHARED_LIBRARIES += ssl
LOCAL_SHARED_LIBRARIES += resipares
LOCAL_SHARED_LIBRARIES += rutil
LOCAL_SHARED_LIBRARIES += resip
LOCAL_SHARED_LIBRARIES += dum
include $(BUILD_SHARED_LIBRARY)

$(call import-module,openssl)
$(call import-module,resip)



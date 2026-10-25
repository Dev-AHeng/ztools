LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := $(call all-subdir-java-files) 	
LOCAL_PACKAGE_NAME := SystemSwitchLogo
LOCAL_CERTIFICATE := platform
LOCAL_PRIVILEGED_MODULE := true
LOCAL_MODULE_TAGS := optional

LOCAL_PRIVATE_PLATFORM_APIS := true
LOCAL_CERTIFICATE := platform

LOCAL_STATIC_JAVA_LIBRARIES += \
            vendor.mediatek.hardware.nvram-V1.0-java

#LOCAL_STATIC_JAVA_LIBRARIES += vendor.mediatek.hardware.netdagent-V1.0-java-static

#LOCAL_JAVA_LIBRARIES += telephony-common \
#                        mediatek-telephony-base
            
include $(BUILD_PACKAGE)
include $(call all-makefiles-under,$(LOCAL_PATH))

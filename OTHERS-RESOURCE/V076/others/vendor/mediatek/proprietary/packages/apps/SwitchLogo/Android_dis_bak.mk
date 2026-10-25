LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := $(call all-subdir-java-files) 	
LOCAL_PACKAGE_NAME := SystemSwitchLogo
LOCAL_PRIVATE_PLATFORM_APIS := true
#LOCAL_SDK_VERSION := current
#LOCAL_SDK_VERSION := system_current

#LOCAL_PRODUCT_MODULE := true
LOCAL_SYSTEM_EXT_MODULE := true
LOCAL_PRIVILEGED_MODULE := true
LOCAL_REQUIRED_MODULES := privapp_whitelist_com.mediatek.changeanimation

#LOCAL_STATIC_JAVA_LIBRARIES += vendor.mediatek.hardware.nvram-V1.0-java-static

LOCAL_CERTIFICATE := platform
LOCAL_PRIVILEGED_MODULE := true
LOCAL_MODULE_TAGS := optional
            
include $(BUILD_PACKAGE)
include $(call all-makefiles-under,$(LOCAL_PATH))

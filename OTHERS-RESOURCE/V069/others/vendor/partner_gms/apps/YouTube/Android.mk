ifeq ($(MSSI_MTK_TC1_COMMON_SERVICEZL), yes)
ifeq ($(strip $(BUILD_GMS)), yes)

LOCAL_PATH :=$(call my-dir)

my_archs := arm arm64
my_src_arch := $(call get-prebuilt-src-arch, $(my_archs))

$(shell mkdir -p $(PRODUCT_OUT)/system/app/YouTube/)

ifeq ($(my_src_arch),arm64)
$(warning === YouTube call all-makefiles-under)
include $(call all-makefiles-under,$(LOCAL_PATH))
#LOCAL_POST_PROCESS_COMMAND := $(shell cp -r $(LOCAL_PATH)/YouTube/*  $(PRODUCT_OUT)/system/app/YouTube/)
else
$(warning === cp 32 bit apk)
LOCAL_POST_PROCESS_COMMAND := $(shell cp -r $(LOCAL_PATH)/YouTube_32/*  $(PRODUCT_OUT)/system/app/YouTube/)
endif

endif
endif

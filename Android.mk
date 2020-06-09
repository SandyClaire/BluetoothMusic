LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)
#mk include jar
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES  :=   \
		libsupportbtmusicv4:libs/android-support-v4.jar
		
		
include $(BUILD_MULTI_PREBUILT)

include $(CLEAR_VARS)

LOCAL_STATIC_JAVA_LIBRARIES := libsupportbtmusicv4 autosdk

LOCAL_AIDL_INCLUDES := $(LOCAL_PATH)/src

LOCAL_SRC_FILES := $(call all-java-files-under, src) 

LOCAL_PACKAGE_NAME := BluetoothMusic

LOCAL_PROGUARD_ENABLED := disabled

LOCAL_DEX_PREOPT := false

LOCAL_PROGUARD_FLAG_FILES := proguard-project.txt

LOCAL_PRIVATE_PLATFORM_APIS := true
#LOCAL_PRIVILEGED_MODULE := true

LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)

include $(call all-makefiles-under,$(LOCAL_PATH))



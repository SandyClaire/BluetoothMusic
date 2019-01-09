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

LOCAL_SDK_VERSION := current

LOCAL_SRC_FILES += \
	src/com/anwsdk/service/IAnwAudioGetMediaInfoStatusCallBack.aidl\
	src/com/anwsdk/service/IAnwBLEConnectionCallBack.aidl\
	src/com/anwsdk/service/IAnwBLEDiscoveryCallBack.aidl\
	src/com/anwsdk/service/IAnwBrowsingCallBack.aidl\
	src/com/anwsdk/service/IAnwBrowsingChangePathCallBack.aidl\
	src/com/anwsdk/service/IAnwEmailCallBack.aidl\
	src/com/anwsdk/service/IAnwHFAGEventCallBack.aidl\
	src/com/anwsdk/service/IAnwInquiryCallBack.aidl\
	src/com/anwsdk/service/IAnwInquiryCallBackEx.aidl\
	src/com/anwsdk/service/IAnwObexTxfCallBack.aidl\
	src/com/anwsdk/service/IAnwPBAPBrowsingDataCallBack.aidl\
	src/com/anwsdk/service/IAnwPhonebookCallBack.aidl\
	src/com/anwsdk/service/IAnwPhoneLink.aidl\
	src/com/anwsdk/service/IAnwSMSCallBack.aidl\
	src/com/anwsdk/service/IAnwSPPDataCallBack.aidl\
	
LOCAL_PACKAGE_NAME := BluetoothMusic

LOCAL_PROGUARD_ENABLED := disabled

LOCAL_DEX_PREOPT := false

LOCAL_PROGUARD_FLAG_FILES := proguard-project.txt

LOCAL_SDK_VERSION := current

LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)

include $(call all-makefiles-under,$(LOCAL_PATH))



package com.anwsdk.service;

import com.anwsdk.service.IAnwInquiryCallBack;
import com.anwsdk.service.IAnwPhonebookCallBack;
import com.anwsdk.service.IAnwSMSCallBack;
import com.anwsdk.service.IAnwEmailCallBack;
import com.anwsdk.service.SupportMobileFunction;
import com.anwsdk.service.IAnwSPPDataCallBack;
import com.anwsdk.service.IAnwBrowsingCallBack;
import com.anwsdk.service.IAnwHFAGEventCallBack;
import com.anwsdk.service.IAnwAudioGetMediaInfoStatusCallBack;
import android.graphics.Bitmap;
import com.anwsdk.service.IAnwInquiryCallBackEx;
import com.anwsdk.service.IAnwPBAPBrowsingDataCallBack;
import com.anwsdk.service.DeviceIDInfoData;
import com.anwsdk.service.ProfileSupportInfo;
import com.anwsdk.service.CallInfoData;
import com.anwsdk.service.SmsData;
import com.anwsdk.service.EmailData;
import com.anwsdk.service.IAnwObexTxfCallBack;
import com.anwsdk.service.IAnwBrowsingChangePathCallBack;
import com.anwsdk.service.IAnwBLEDiscoveryCallBack;
import com.anwsdk.service.IAnwBLEConnectionCallBack;
import com.anwsdk.service.BT_ADV_DATA;
import com.anwsdk.service.MAPFolderInfo;

interface IAnwPhoneLink {
	int 		ANWBT_BTPowerOn();
	int 		ANWBT_BTPowerOff();
	int	 		ANWBT_GetBTPowerStatus();
	
	int 		ANWBT_DeleteDefaultPhone(String address);
	int 		ANWBT_SetDiscoveryMode(int mask);
	int 		ANWBT_SetDeviceName(String Name);
	String		ANWBT_ReadLocalAddr();
	String		ANWBT_GetRemoteName(String address);
	boolean		ANWBT_SetDeviceVol(boolean bSpeaker,int nVal);
	boolean		ANWBT_GetDeviceVol(boolean bSpeaker,out int[] nVal);
	int			ANWBT_SetMicLevel(int nVal);
	int			ANWBT_GetMicLevel(out int[] nVal);
	int			ANWBT_SetSpkLevel(int nVal);
	int			ANWBT_GetSpkLevel(out int[] nVal);
	SupportMobileFunction	ANWBT_GetPhoneSupportFun();
	
	int   		ANWBT_GetPairedList(out int[] nCount,out String[] Name,out String[] Address,out int[] cod);
	int   		ANWBT_DeviceInquiry(IAnwInquiryCallBack cb);
	int   		ANWBT_DeviceInquiryStop();	
	int   		ANWBT_DevicePair(String address,String pin_code,int cod);
	int   		ANWBT_DeviceUnPair(String address); 	
	int   		ANWBT_AcceptPair(String address,String pin_code, boolean bSSP);
	int   		ANWBT_RejectPair(String address,boolean bSSP);
	
	int   		ANWBT_GetConnectStatus(int nProfileType,int nIndex);
	int   		ANWBT_GetConnectedDeviceInfo(int nProfileType,out String[] strAddress,out String[] strName,int nIndex);
	int   		ANWBT_AcceptConnectRequest(int nProfileType,int nIndex);
	int   		ANWBT_RejectConnectRequest(int nProfileType,int nIndex);	
	int   		ANWBT_ConnectMobile(String address);
	int   		ANWBT_DisconnectMobile();
	
	int   		ANWBT_AnswerCall();
	int   		ANWBT_HangupCall();
	int   		ANWBT_Dial(String number);
	int   		ANWBT_DialDTMF(String number);
	int   		ANWBT_VoiceDial();
	int   		ANWBT_HangupVoiceDial();
	
	int   		ANWBT_CallWaiting(int nType);
	int   		ANWBT_DisableNREC();		
	int   		ANWBT_QueryCurrentCall(out String[] strNumber1,out String[] strName1,out int[] nCall1Status1,out String[] strNumber2,out String[] strName2,out int[] nCall1Status2);
	int   		ANWBT_MemoryDial(String number);
	int   		ANWBT_ReDial();
	int   		ANWBT_TransferAudio(boolean bToPND);
	boolean 	ANWBT_IsSupportAudioTransfer();
	int   		ANWBT_GetCallStatus();
	int   		ANWBT_GetMissCallCount();
	
	int   		ANWBT_GetContacts(boolean bRefresh,int nMemtype,int nMax_ME,int nMax_SM,int nMax_RC,int nMax_MC,int nMax_DC,IAnwPhonebookCallBack cb);
	void		ANWBT_UnRegistryContactsCallback(IAnwPhonebookCallBack cb);
	int   		ANWBT_GetSMSs(boolean bRefresh,int nMaxGet,IAnwSMSCallBack cb);
	void		ANWBT_UnRegistrySMSCallback(IAnwSMSCallBack cb);
	int   		ANWBT_SendSMS(String strNumber,String Text);
	
	int   		ANWBT_A2DPConnect(String address);
	int   		ANWBT_A2DPDisconnect();
	int   		ANWBT_AVRCPControl(int op_code);
	boolean		ANWBT_IsA2DPSupportMetadata();
	String		ANWBT_A2DPGetCurrentAttributes(int Attributes_id);
	int 		ANWBT_AudioGetPlayStatus();
	
	int 		ANWBT_AudioSetStreamMode(int mode);
	int 		ANWBT_AudioGetStreamMode();

	int 		ANWBT_AudioSetStreamVolume(float volume);
	
	boolean		ANWBT_IsCurrentPairing();
	boolean		ANWBT_IsCurrentInquiring();
	boolean		ANWBT_IsCurrentDownloading();
	boolean		ANWBT_IsCurrentA2DPPlaying();


	int   		ANWBT_SPPInit(in byte[] b);
	int   		ANWBT_SPPDenit();
	int   		ANWBT_SPPConnect(String address,in byte[] b,out int[] nIndex);
	int   		ANWBT_SPPDisconnect(int nindex);
	int   		ANWBT_SPPWrite(int nindex,in byte[] b,int datasize,out int[] nWritten);
	boolean   	ANWBT_GetSPPInitStatus();
	void		ANWBT_RegistrySPPDataCallback(IAnwSPPDataCallBack cb);
	void		ANWBT_UnRegistrySPPDataCallback(IAnwSPPDataCallBack cb);

	int   		ANWBT_SendHCIrawpdu(in byte[] b,int datasize);

	boolean   	ANWBT_GetHFAGInitStatus();
	int 		ANWBT_HFAGInit();
	int 		ANWBT_HFAGDenit();
	int 		ANWBT_HFAGConnect(String address);
	int 		ANWBT_HFAGDisconnect();
	int 		ANWBT_HFAGCreateSCO(boolean bCreate);
	void		ANWBT_RegistryHFAGEventCallback(IAnwHFAGEventCallBack cb);
	void		ANWBT_UnRegistryHFAGEventCallback(IAnwHFAGEventCallBack cb);
	int 		ANWBT_HFAGVoiceRecognitionRsp(boolean bEnable);
	int 		ANWBT_HFAGDeviceVolumeRsp(boolean bSpeaker,int nVol);
	int 		ANWBT_HFAGDeviceStatusRsp(int bService, int bRoam, int signal, int batt_chg);
	int 		ANWBT_HFAGCOPSRsp(String strOperator);
	int 		ANWBT_HFAGCINDRsp(int svc, int call_active, int call_held, int call_setup, int signal, int roam, int batt_chg);
	int 		ANWBT_HFAGCLCCRsp(int index, int dir, int status, int mode, int mpty, String number, int type);
	int 		ANWBT_HFAGFormatATRsp( String szRsp);
	int 		ANWBT_HFAGATRsp(boolean bOK, int error_code);
	int 		ANWBT_HFAGPhoneStateRsp(int call_active, int call_held, int call_setup, String number, int type);
	
	
	boolean   	ANWBT_GetHIDInitStatus();	
	int 		ANWBT_HidInit();
	int 		ANWBT_HidDeinit();
	int 		ANWBT_HidConnect(String address);
	int 		ANWBT_HidDisconnect();
	int 		ANWBT_HidWriteCoordinate(int nButtonType, int nXCor, int nYCor, int nWheel);
	int 		ANWBT_HidHandleButton(int nModifier, int nReserved, int nKeyLength, in byte[] bNormalKey);
	
	int   		ANWBT_AudioBrowsingGetElement(String uuid,int Attr_mask,int nMaxGetCount,IAnwBrowsingCallBack cb);
	int   		ANWBT_AudioBrowsingSearch(String Folder_UID ,String keyword,int Attr_mask,int nMaxGetCount,IAnwBrowsingCallBack cb);
	int   		ANWBT_AudioBrowsingGetNowPlayingList(int Attr_mask,int nMaxGetCount,IAnwBrowsingCallBack cb);
	int   		ANWBT_AudioBrowsingPlayItem(int scop,String uuid);
	int   		ANWBT_AudioBrowsingAddtoNowPlaying(int scop,String uuid);
	int   		ANWBT_AudioBrowsingGetItemAttributes(int scop,String uuid,int Attr_mask,IAnwBrowsingCallBack cb);
	boolean		ANWBT_IsCurrentAudioBrowsingBusy();	
	int 		ANWBT_GetAVRCPBrowsingSupportFeature();	

	String		ANWBT_GetOperatorInfo();
	int 		ANWBT_GetSignalQuality();
	int 		ANWBT_GetBatteryCharge();
	
	String		ANWBT_GetSDKVersion();	
	int   		ANWBT_GetConnectedMobileInfo(out String[] strManufacturer,out String[] strModelName,out String[] strIMEI);
	int 		ANWBT_SetDownloadPBKAttr(int nAttribute);
	Bitmap 		ANWBT_ReadBitmap(String img_file,int W,int H);
	boolean		ANWBT_DeletePhotoFile(String img_file);
	
	int 		ANWBT_AudioGetCurrentPlayerAPSetting();
	int 		ANWBT_AudioSetCurrentPlayerAPSetting(int nAttrID,int nAttrValue);
	int 		ANWBT_AudioGetMediaInfoEx(int nAttr_Mask,IAnwAudioGetMediaInfoStatusCallBack cb);
	int 		ANWBT_AVRCPControlEx(int op_code, int nActFlag);
	int 		ANWBT_AudioRetrieveCurrentPlayerAPSetting(int nAttrID);
	int 		ANWBT_AudioRetrieveCurrentPlayerAPSupported(int nAttrID,out int[] nAllowArray,int nArraySize);
	
	int 		ANWBT_ReadRSSI(String address);
	int   		ANWBT_DeviceInquiryEx(IAnwInquiryCallBackEx cb);
	
	int   		ANWBT_PBAPBrowsingGetList(int nMemtype,int nMax_ME,int nMax_SM,int nMax_RC,int nMax_MC,int nMax_DC,IAnwPBAPBrowsingDataCallBack cb);
	void		ANWBT_UnRegistryPBAPBrowsingCallback(IAnwPBAPBrowsingDataCallBack cb);
	int   		ANWBT_PBAPBrowsingGetEntry(int gsmMemtype,String sHandle,IAnwPhonebookCallBack cb);

	int   		ANWBT_GetDeviceIDInfo(String address,int nMaxRecordCount,out DeviceIDInfoData[] DeviceInfo,out int[] nGetRecordCount);
	int   		ANWBT_GetSupportServiceRecord(String address,in byte[] GUID,out int[] supportresult,int nCount);
	void		ANWBT_CancelGetSupportServiceRecord();
	int 		ANWBT_HidConsumer(int nAttribute);
	
	void		ANWBT_EnableSDKLog(int bEnable);
	int   		ANWBT_GetSupportServiceRecordEx(String address,in byte[] GUID,out ProfileSupportInfo[] supportresult,int nCount);
	int   		ANWBT_SetSMSProperty(in SmsData sms,int nPropertyValue);
	int   		ANWBT_QueryCurrentCallEx(out CallInfoData[] callinfo,int nCallInfoDataCount,out int[] nGetCount);
	int   		ANWBT_AudioBrowsingGetElementEx(String uuid,int Attr_mask,int nStartPos,int nMaxGetCount,IAnwBrowsingCallBack cb);
	int   		ANWBT_AudioBrowsingSearchEx(String Folder_UID ,String keyword,int Attr_mask,int nStartPos,int nMaxGetCount,IAnwBrowsingCallBack cb);
	int   		ANWBT_AudioBrowsingGetNowPlayingListEx(int Attr_mask,int nStartPos,int nMaxGetCount,IAnwBrowsingCallBack cb);
	int			ANWBT_GenerateActivateFile(String address);
	
	int			ANWBT_OPPInit();
	int			ANWBT_OPPDeInit();
	int			ANWBT_OPPServer_Start(int mtu,IAnwObexTxfCallBack cb);
	int			ANWBT_OPPServer_Stop();
	int			ANWBT_OPPServer_ReqRsp(int idx,int req_type, String localfilename,int  nrsp_code);
	int			ANWBT_OPPClient_Connect(String address,int mtu,out  int[] nIndex);
	int			ANWBT_OPPClient_Disconnect(int idx);
	int			ANWBT_OPPClient_PushFile(int idx, String localfilename,String filetype,IAnwObexTxfCallBack cb);
	int			ANWBT_OPPClient_PullFile(int idx, String localfilename,String filetype,IAnwObexTxfCallBack cb);
	int			ANWBT_OPPClient_ExchangeFile(int idx, String pushfilename, String pullfilenamee,String filetype,IAnwObexTxfCallBack cb);
	int			ANWBT_OPPGetDeviceInfobyIndex(int idx,out String[] strAddress,out String[] strName,out int[] nConnectStatus,out boolean[] bServer);
	boolean   	ANWBT_GetOPPInitStatus();	
	String		ANWBT_GetDeviceName();
	
	int			ANWBT_SPPAddService(in byte[] b);
	int			ANWBT_AVRCPSetPositionChangedInterval(int Interval);
	void		ANWBT_EnableSWAEC(boolean bEnable);
	int			ANWBT_ActiveBQB(boolean bEnable,String address);
	int			ANWBT_AudioBrowsingChangePath(String uuid,int absflag,int direction,IAnwBrowsingChangePathCallBack cb);
	int			ANWBT_GetRemotePhoneNumber(out String[] strNumber);
	int			ANWBT_SetEIRData(in BT_ADV_DATA EIRData);	
	
	
	int			ANWBT_BLE_SetMode(boolean bDiscoverable,boolean bConnectable,int direct_mode);	
	int			ANWBT_BLE_SetRandomAddress(String Addr);	
	int			ANWBT_BLE_GetRandomAddress(out String[] strAddress);	
	int			ANWBT_BLE_Discovery(int scan_mode ,int ndiscovery_time,int filter,IAnwBLEDiscoveryCallBack cb);	
	int			ANWBT_BLE_StopDiscovery();	
	int			ANWBT_BLE_StartAdvertising(int minInterval,int maxInterval, String direct_addr, int direct_addr_type,int ch_map,int filter,in BT_ADV_DATA adv);	
	int			ANWBT_BLE_StopAdvertising();	
	int			ANWBT_BLE_SetScanRsp(in BT_ADV_DATA adv);	
	int			ANWBT_BLE_GetWhitelistSize(out  int[] nSize);	
	int			ANWBT_BLE_AddtoWhitelist(String Addr, int addr_type);	
	int			ANWBT_BLE_RemovefromWhitelist(String Addr, int addr_type);	
	int			ANWBT_BLE_ClearWhitelist();	
	int			ANWBT_BLE_SMP_SetSecurity(int capability, int oob_data,int authentication);	
	int			ANWBT_BLE_SMP_PasskeyReply(int accept, int passkey);	
	int			ANWBT_BLE_SMP_Pair(String Addr, int addr_type);	
	int			ANWBT_BLE_SMP_UnPair(String Addr, int addr_type);	
	int			ANWBT_BLE_SMP_CheckPair(String Addr, int addr_type,out String[] strPairAddress,out  int[] nPairAddrType);	
	boolean		ANWBT_BLE_IsCurrentPairing();
	boolean		ANWBT_BLE_IsCurrentDiscovering();
	boolean		ANWBT_BLE_IsCurrentAdvertising();
	
	int			ANWBT_BLE_GATT_Init();	
	int			ANWBT_BLE_GATT_Deinit();
	void		ANWBT_RegistryBLEConnectionCallback(int nIndex,IAnwBLEConnectionCallBack cb);
	void		ANWBT_UnRegistryBLEConnectionCallback(int nIndex,IAnwBLEConnectionCallBack cb);
	
	int			ANWBT_BLE_GATT_Connect(String Addr, int addr_type,out int[] nIndex);	
	int			ANWBT_BLE_GATT_Disconnect(int nIndex);	
	int			ANWBT_BLE_GATT_GetAllService(int nIndex);	
	int			ANWBT_BLE_GATT_RegisterNotification(int nIndex);	
	int			ANWBT_BLE_GATT_ExchangeMtu(int nIndex,int mtu);	
	int			ANWBT_BLE_GATT_WriteCharValue(int nIndex,int handle,in byte[] b,int datasize);
	int			ANWBT_BLE_GATT_WriteCharWithoutRes(int nIndex,int handle,in byte[] b,int datasize);
	int			ANWBT_BLE_GATT_ReadChar(int nIndex,int handle);
	int			ANWBT_BLE_GATT_GetDeviceInfo(int nIndex,out String[] strAddress,out int[] nAddrType,out String[] strName,out int[] Mtu,out boolean[] bServer);
	
	boolean   	ANWBT_GetGATTInitStatus();	
	boolean		ANWBT_BLE_IsCurrentGetAllService(int nIndex);
	
	int   		ANWBT_GetMessages(int nGetTypeMask,String foldername,boolean bRefresh,int nMaxGet_SMS,IAnwSMSCallBack cb,int nMaxGet_EMAIL,IAnwEmailCallBack emailcb);
	void		ANWBT_UnRegistryEmailCallback(IAnwEmailCallBack cb);
	int   		ANWBT_SendEmail(in EmailData email);
	int   		ANWBT_SetEmailProperty(in EmailData email,int nPropertyValue);
	int   		ANWBT_GetMessageFolderListing(out MAPFolderInfo[] FolderInfo);
	
}

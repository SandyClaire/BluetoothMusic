package com.anwsdk.service;

public class MangerConstant
{
	public static final int Anw_SUCCESS = 1;
	//connect status callback channel type
	public static final int PROFILE_HF_CHANNEL = 0;
	public static final int PROFILE_PIMDATA_CHANNEL = 1;
	public static final int PROFILE_AUDIO_STREAM_CHANNEL = 2;
	public static final int PROFILE_AUDIO_CONTROL_CHANNEL = 3;
	public static final int PROFILE_SPP_CHANNEL = 4;
	public static final int PROFILE_HFAG_CHANNEL = 5;
	public static final int PROFILE_HID_CONTROL_CHANNEL = 6;
	public static final int PROFILE_HID_INTERRUPT_CHANNEL = 7;
	public static final int PROFILE_SCO_CHANNEL = 8;
	public static final int PROFILE_AVRCP_BROWSING_CHANNEL = 9;
	public static final int PROFILE_PAN_CHANNEL = 10;
	public static final int PROFILE_GATT_CHANNEL = 11;
	public static final int PROFILE_PHONEBOOK_CHANNEL = 12; // PBAP
	public static final int PROFILE_SMS_CHANNEL = 13;//MAP
	public static final int PROFILE_OPP_CHANNEL = 14;
	public static final int PROFILE_EMAIL_CHANNEL = 15;

	
	public static final int BTPOWER_STATUS_OFF =0;
	public static final int BTPOWER_STATUS_ON =1;
	public static final int BTPOWER_STATUS_ONING =2;
	public static final int BTPOWER_STATUS_OFFING =3;
	
	//HFP Indicator Key Type
	public static final int HF_IND_SERVICE = 0;
	public static final int HF_IND_ROAM = 1;
	public static final int HF_IND_BATTERY_LEVEL = 2;
	public static final int HF_IND_SIGNAL_LEVEL = 3;
	public static final int HF_IND_SUPPORT_FEATURE= 4;
	public static final int HF_IND_SPEAKER_VOL= 5;
	public static final int HF_IND_MIC_VOL= 6;
	//Call status
	public static final int CALLSTATUS_ENDCALL = 0;
	public static final int CALLSTATUS_INCOMINGCALL = 1;
	public static final int CALLSTATUS_INCALL = 2;
	public static final int CALLSTATUS_OUTGOINGCALL = 3;
	public static final int CALLSTATUS_THREEWAYCALL = 4;
	public static final int CALLSTATUS_VOICEDIALACTIVE = 5;
	public static final int CALLSTATUS_VOICEDIALTIMEOUT = 6;
	public static final int CALLSTATUS_VOICEDIALTERMINATE = 7;
	public static final int CALLSTATUS_HELDSTATUSCHANGE = 9;

	//Getcontacts callback - DataType
	public static final int DATATYPE_ERROR = 0;
	public static final int DATATYPE_ME = 1;
	public static final int DATATYPE_SM = 2;
	public static final int DATATYPE_RC = 3;
	public static final int DATATYPE_MC = 4;
	public static final int DATATYPE_DC = 5;
	public static final int DATATYPE_FINISH = 6;	
	public static final int DATATYPE_CC = 7;	
	public static final int DATATYPE_SPD = 8;	
	public static final int DATATYPE_FAV = 9;	

	
	//Use in ANWBT_GetContacts - Memetype
	public static final int PBK_MEMTYPE_ME = 0x0001;
	public static final int PBK_MEMTYPE_SM = 0x0002;
	public static final int PBK_MEMTYPE_RC = 0x0004;
	public static final int PBK_MEMTYPE_MC = 0x0008;
	public static final int PBK_MEMTYPE_DC = 0x0010;
	
	//Use in ANWBT_SetDownloadPBKAttr - Attribute
	public static final int PBK_ATTR_PHOTO = 0x00000001;
	public static final int PBK_ATTR_EMAIL = 0x00000002;
	
	//SPP
	public static final int SPP_MAX_CONNECTION = 4;

	//HFAG
	public static final int HFAG_EV_VOICE_RECOGNITION_REQ 	= 0;
	public static final int HFAG_EV_MIC_VOL_REQ 			= 1;
	public static final int HFAG_EV_SPK_VOL_REQ 			= 2;
	public static final int HFAG_EV_COPS_REQ 				= 3;
	public static final int HFAG_EV_CIND_REQ 				= 4;
	public static final int HFAG_EV_CLCC_REQ 				= 5;
	public static final int HFAG_EV_DIAL_REQ 				= 6;
	public static final int HFAG_EV_REDIAL_REQ 				= 7;
	public static final int HFAG_EV_MEMDIAL_REQ 			= 8;
	public static final int HFAG_EV_ANWSER_CALL_REQ 		= 9;
	public static final int HFAG_EV_CANCEL_CALL_REQ 		= 10;
	public static final int HFAG_EV_DTMF_REQ 				= 11;
	public static final int HFAG_EV_NREC_REQ 				= 12;
	public static final int HFAG_EV_MANUFACTURE_REQ 		= 13;
	public static final int HFAG_EV_MODEL_REQ 				= 14;
	public static final int HFAG_EV_OTHER_ATCMD_REQ 		= 15;

	public static final String ServiceActionName = "com.anwsdk.service.AnwPhoneLink";

	public static final String MSG_ACTION_POWER_STATUS = "android.bluetooth.anw.action.POWER_STATUS";
	public static final String MSG_ACTION_CONNECT_STATUS = "android.bluetooth.anw.action.CONNECT_STATUS";
	public static final String MSG_ACTION_HFP_INDICATOR = "android.bluetooth.anw.action.HFP_INDICATOR";
	public static final String MSG_ACTION_CALL_STATUS = "android.bluetooth.anw.action.CALL_STATUS";
	public static final String MSG_ACTION_MISS_CALL = "android.bluetooth.anw.action.MISS_CALL";
	public static final String MSG_ACTION_INCOMINGSMS = "android.bluetooth.anw.action.INCOMINGSMS";
	public static final String MSG_ACTION_PAIR_REQUEST = "android.bluetooth.anw.action.PAIR_REQUEST";
	public static final String MSG_ACTION_PAIR_STATUS = "android.bluetooth.anw.action.PAIR_STATUS";
	public static final String MSG_ACTION_CONNECT_REQUEST = "android.bluetooth.anw.action.CONNECT_REQUEST";
	public static final String MSG_ACTION_A2DP_FEATURE_SUPPORT = "android.bluetooth.anw.action.A2DP_FEATURE_SUPPORT";
	public static final String MSG_ACTION_A2DP_METADATA = "android.bluetooth.anw.action.A2DP_METADATA";
	public static final String MSG_ACTION_A2DP_PLAYSTATUS = "android.bluetooth.anw.action.A2DP_PLAYSTATUS";
	public static final String MSG_ACTION_A2DP_PLAYBACKPOS = "android.bluetooth.anw.action.A2DP_PLAYBACKPOS";
	public static final String MSG_ACTION_A2DP_STREAMSTATUS = "android.bluetooth.anw.action.A2DP_STREAMSTATUS";
	public static final String MSG_ACTION_AVRCP_BROWSINGEVENT= "android.bluetooth.anw.action.AVRCP_BROWSINGEVENT";
	public static final String MSG_ACTION_AVRCP_BROWSING_FEATURE_SUPPORT= "android.bluetooth.anw.action.AVRCP_BROWSING_FEATURE_SUPPORT";
	public static final String MSG_ACTION_AVRCP_PLAYERSETTING_CHANGED_EVENT= "android.bluetooth.anw.action.AVRCP_PLAYERSETTING_CHANGED_EVENT";
	public static final String MSG_ACTION_AVRCP_PLAYERSETTING_SUPPORTED_EVENT= "android.bluetooth.anw.action.AVRCP_PLAYERSETTING_SUPPORTED_EVENT";
	public static final String MSG_ACTION_RSSI_VALUE = "android.bluetooth.anw.action.RSSI_VALUE";
	public static final String MSG_ACTION_OPP_PUSH_REQ = "android.bluetooth.anw.action.OPP_PUSH_REQ";
	public static final String MSG_ACTION_OPP_PULL_REQ = "android.bluetooth.anw.action.OPP_PULL_REQ";
	public static final String MSG_ACTION_BLE_SMP_EVENT = "android.bluetooth.anw.action.BLE_SMP_EVENT";
	public static final String MSG_ACTION_INCOMINGEMAIL = "android.bluetooth.anw.action.INCOMINGEMAIL";
	
	//A2DP
	public static final int AUDIO_STREAM_MODE_UNMUTE 	= 0;
	public static final int AUDIO_STREAM_MODE_MUTE		= 1;
//	public static final int AUDIO_STREAM_MODE_ENABLE 	= 2;
//	public static final int AUDIO_STREAM_MODE_DISABLE	= 3;
	
	public static final int SMS_PROPERTY_DELETE = 0;
	public static final int SMS_PROPERTY_READ = 1;
	
	//OPP
	public static final int OPP_MAX_CONNECTION = 10;
	public static final int OPP_EVENT_PUSH_FILE = 3;
	public static final int OPP_EVENT_PULL_FILE = 4;
	public static final int OBEX_TXF_TYPE_PUSH = 0;
	public static final int OBEX_TXF_TYPE_PULL = 1;
	public static final int OBEX_TXF_TYPE_EXCHANGE = 2;
	
	public static final int OBEX_CONNECT_REQ  = 0;
	public static final int OBEX_CLIENT_PUSHFILE_REQ =1;
	public static final int OBEX_CLIENT_PULLFILE_REQ=2;
	public static final int OBEX_CLIENT_DELFILE_REQ =3;

	
	//BLE
	public static final int BLE_CONNECT_UNDIRECT =0;		/* Connectable undirected advertising */
	public static final int BLE_CONNECT_DIRECT = 1;			/* Connectable directed advertising */
	public static final int BLE_CONNECT_LOW_DUTY_DIRECT=2;	 /* Connectable low duty cycle directed advertising  */
	
	public static final int BLE_SMPEVENT_SMP_NONE =0;
	public static final int BLE_SMPEVENT_SMP_PASSKEY_NOTIFY =1;
	public static final int BLE_SMPEVENT_SMP_PASSKEY_ENTER =2;
	public static final int BLE_SMPEVENT_SMP_PASSKEY_CFM =3;
	public static final int BLE_SMPEVENT_SMP_RESPONSE =4;
	public static final int BLE_SMPEVENT_SMP_SECURITY_REQUEST =5;
	public static final int BLE_SMPEVENT_SMP_PAIR_COMPLETE =6;
	//BLE
	public static final int GATT_MAX_CONNECTION = 10;
	
	//BLE Scan Mode
	public static final int BLE_SCAN_PASSIVE =0; /* Passive Scanning. No SCAN_REQ packets shall be sent*/
	public static final int BLE_SCAN_ACTIVE =1; /*Active scanning. SCAN_REQ packets may be sent*/
	
	//BLE Scanning_Filter_Policy
	
	public static final int BLE_SCAN_FILTER_ALL =0; /* Accept all advertisement packets*/
	public static final int BLE_SCAN_FILTER_WL =1; /*Ignore advertisement packets from devices not in the White List Only*/
	
	//BLE Address Type
	public static final int BLE_ADDR_TYPE_PUBLIC =0; /* PUBLIC Address*/
	public static final int BLE_ADDR_TYPE_RANDOM =1; /* RANDOM Address*/
	
	//BLE SMP
	//IO capability
	public static final int BLE_SMP_IO_DISPLAY_ONLY 	=	0x00;
	public static final int BLE_SMP_IO_DISPLAY_YESNO 	=	0x01;
	public static final int BLE_SMP_IO_KEYBOARD_ONLY    =	0x02;
	public static final int BLE_SMP_IO_NO_INPUT_OUTPUT  =	0x03;
	public static final int BLE_SMP_IO_KEYBOARD_DISPLAY =	0x04;

	//OOB FLAG
	public static final int BLE_SMP_OOB_NOT_PRESENT		=	0x00;
	public static final int BLE_SMP_OOB_PRESENT			=	0x01;

	//AuthReq
	public static final int BLE_SMP_AUTH_NONE 			=	0x00;
	public static final int BLE_SMP_AUTH_BONDING		=	0x01;
	public static final int BLE_SMP_AUTH_MITM			=	0x04;
//  Advertising_Channel_Map used for ANWBT_BLE_StartAdvertising ch_map
	public static final int BLE_ADV_CH_37     			=	0x01;    // Enable channel 37 use
	public static final int BLE_ADV_CH_38     			=	0x02;    // Enable channel 38 use
	public static final int BLE_ADV_CH_39     			=	0x04;    // Enable channel 39 use
	public static final int BLE_ADV_CH_ALL    			=	0x07;    // Default (all channels enabled)	
	
//  Advertising_filter used for ANWBT_BLE_StartAdvertising filter
	public static final int BLE_AP_SCAN_CONN_ALL        =	0x00;         // Allow Scan Request from Any, Allow Connect Request from Any (default).
	public static final int BLE_AP_SCAN_WL_CONN_ALL     =	0x01;			// Allow Scan Request from White List Only, Allow Connect Request from Any
	public static final int BLE_AP_SCAN_ALL_CONN_WL     =	0x02;			// Allow Scan Request from Any, Allow Connect Request from White List Only
	public static final int BLE_AP_SCAN_CONN_WL         =	0x03;         // Allow Scan Request from White List Only, Allow Connect Request from White List Only
	/* ADV ad_mask flag bit definition used for BT_ADV_DATA*/	
	public static final int BLE_ADV_BIT_DEV_NAME        =	(0x0001 << 0);
	public static final int BLE_ADV_BIT_FLAGS           =	(0x0001 << 1);
	public static final int BLE_ADV_BIT_SERVICE         =	(0x0001 << 2);
	public static final int BLE_ADV_BIT_TX_PWR          =	(0x0001 << 3);
	public static final int BLE_ADV_BIT_APPEARANCE      =	(0x0001 << 4);
	public static final int BLE_ADV_BIT_SERVICE_DATA    =	(0x0001 << 5);
	public static final int BLE_ADV_BIT_INT_RAMGE	    =	(0x0001 << 6);
	public static final int BLE_ADV_BIT_OTHER_ELEM      =	(0x0001 << 7);
	//GATT ERROR code
	public static final int BLE_GATT_errNone							 =0;
	public static final int BLE_GATT_errInvalidHandle					 =1;
	public static final int BLE_GATT_errReadNotPermitted				 =2;
	public static final int BLE_GATT_errWriteNotPermitted				 =3;
	public static final int BLE_GATT_errInvalidPDU						 =4;
	public static final int BLE_GATT_errInsufficientAuthentication		 =5;
	public static final int BLE_GATT_errRequestNotSupported				 =6;
	public static final int BLE_GATT_errInvalidOffset					 =7;
	public static final int BLE_GATT_errInsufficientAuthorization		 =8;
	public static final int BLE_GATT_errPrepareQueueFull				 =9;
	public static final int BLE_GATT_errAttributeNotFound				 =10;
	public static final int BLE_GATT_errAttributeNotLong				 =11;
	public static final int BLE_GATT_errInsufficientEncryptionKeySize	 =12;
	public static final int BLE_GATT_errInvalidAttributeValueLength		 =13;
	public static final int BLE_GATT_errUnlikelyError					 =14;
	public static final int BLE_GATT_errInsufficientEncryption			 =15;
	public static final int BLE_GATT_errUnsupportedGroupType			 =16;
	public static final int BLE_GATT_errInsufficientResources			 =17;
	
	
	public static final int MESSAGE_GET_TYPE_SMS				=1;
	public static final int MESSAGE_GET_TYPE_EMAIL				=2;
}
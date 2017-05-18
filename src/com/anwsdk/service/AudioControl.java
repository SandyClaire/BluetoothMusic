package com.anwsdk.service;

public class AudioControl {
	// Media Attributes ID
	public static final int MEDIA_ATTR_ILLEGAL				= 0x00;
	public static final int MEDIA_ATTR_MEDIA_TITLE			= 0x01;
	public static final int MEDIA_ATTR_ARTIST_NAME			= 0x02;
	public static final int MEDIA_ATTR_ALBUM_NAME			= 0x03;
	public static final int MEDIA_ATTR_TRACK_NUM_IN_ALBUM	= 0x04;
	public static final int MEDIA_ATTR_TOTAL_NUM_IN_ALBUM	= 0x05;
	public static final int MEDIA_ATTR_GENRE				= 0x06;
	public static final int MEDIA_ATTR_PLAYING_TIME_IN_MS	= 0x07;


	// AVRCP Operands in Pass_Through Commands
	public static final int CONTROL_SELECT			= 0x00;
	public static final int CONTROL_UP			= 0x01;
	public static final int CONTROL_DOWN			= 0x02;
	public static final int CONTROL_LEFT			= 0x03;
	public static final int CONTROL_RIGHT			= 0x04;
	public static final int CONTROL_RIGHT_UP		= 0x05;
	public static final int CONTROL_RIGHT_DOWN		= 0x06;
	public static final int CONTROL_LEFT_UP			= 0x07;
	public static final int CONTROL_LEFT_DOWN		= 0x08;
	public static final int CONTROL_ROOT_MENU		= 0x09;
	public static final int CONTROL_SETUP_MENU		= 0x0A;
	public static final int CONTROL_CONTENTS_MENU		= 0x0B;
	public static final int CONTROL_FAVORITE_MENU 		= 0x0C;
	public static final int CONTROL_EXIT			= 0x0D;

	public static final int CONTROL_0			= 0x20;
	public static final int CONTROL_1			= 0x21;
	public static final int CONTROL_2			= 0x22;
	public static final int CONTROL_3			= 0x23;
	public static final int CONTROL_4			= 0x24;
	public static final int CONTROL_5			= 0x25;
	public static final int CONTROL_6			= 0x26;
	public static final int CONTROL_7			= 0x27;
	public static final int CONTROL_8			= 0x28;
	public static final int CONTROL_9			= 0x29;
	public static final int CONTROL_DOT			= 0x2a;
	public static final int CONTROL_ENTER			= 0x2b;
	public static final int CONTROL_CLEAR			= 0x2c;	

	public static final int CONTROL_CHANNEL_UP		= 0x30;
	public static final int CONTROL_CHANNEL_DOWN		= 0x31;
	public static final int CONTROL_PREV_CHANNEL		= 0x32;
	public static final int CONTROL_SOUND_SELECT		= 0x33;
	public static final int CONTROL_INPUT_SELECT		= 0x34;
	public static final int CONTROL_DISPLAY_INFO		= 0x35;
	public static final int CONTROL_HELP			= 0x36;
	public static final int CONTROL_PAGE_UP			= 0x37;
	public static final int CONTROL_PAGE_DOWN		= 0x38;

	public static final int CONTROL_POWER			= 0x40;
	public static final int CONTROL_VOLUME_UP		= 0x41;
	public static final int CONTROL_VOLUME_DOWN		= 0x42;
	public static final int CONTROL_MUTE			= 0x43;
	public static final int CONTROL_PLAY			= 0x44;
	public static final int CONTROL_STOP			= 0x45;
	public static final int CONTROL_PAUSE			= 0x46;
	public static final int CONTROL_RECORD			= 0x47;
	public static final int CONTROL_REWIND			= 0x48;
	public static final int CONTROL_FASTFORWARD		= 0x49;
	public static final int CONTROL_EJECT			= 0x4A;
	public static final int CONTROL_FORWARD			= 0x4B;
	public static final int CONTROL_BACKWARD		= 0x4C;
	public static final int CONTROL_LIST			= 0x4d;
	public static final int CONTROL_F1			= 0x71;
	public static final int CONTROL_F2			= 0x72;
	public static final int CONTROL_F3			= 0x73;
	public static final int CONTROL_F4			= 0x74;
	public static final int CONTROL_F5			= 0x75;
	public static final int CONTROL_F6			= 0x76;
	public static final int CONTROL_F7			= 0x77;
	public static final int CONTROL_F8			= 0x78;
	public static final int CONTROL_F9			= 0x79;

	public static final int CONTROL_VENDOR_UNIQUE		= 0x7e;
	
	// Control Error & Status Codes
	public static final int CONTROL_ERROR_INVALID_COMMAND			= 0x00;
	public static final int CONTROL_ERROR_INVALID_PARAMETER		= 0x01;
	public static final int CONTROL_ERROR_PARAMETER_NOT_FOUND		= 0x02;
	public static final int CONTROL_ERROR_INTERNAL_ERROR			= 0x03;
	public static final int CONTROL_ERROR_OPERATION_COMPLETED		= 0x04;
	public static final int CONTROL_ERROR_UID_CHANGED				= 0x05;

	public static final int CONTROL_ERROR_INVALID_DIRECTION		= 0x07;
	public static final int CONTROL_ERROR_NOT_A_DIRECTORY			= 0x08;
	public static final int CONTROL_ERROR_NOT_EXIST				= 0x09;
	public static final int CONTROL_ERROR_INVALID_SCOPE			= 0x0a;
	public static final int CONTROL_ERROR_RANGE_OUT_OF_BOUNDS		= 0x0b;
	public static final int CONTROL_ERROR_UID_IS_A_DIRECTORY		= 0x0c;
	public static final int CONTROL_ERROR_MEDIA_IN_USE				= 0x0d;
	public static final int CONTROL_ERROR_PLAYING_LIST_FULL		= 0x0e;
	public static final int CONTROL_ERROR_SEARCH_NOT_SUPPORTED		= 0x0f;
	public static final int CONTROL_ERROR_SEARCH_IN_PROGRESS		= 0x10;
	public static final int CONTROL_ERROR_INVALID_PLAYED_ID		= 0x11;
	public static final int CONTROL_ERROR_PLAYER_NOT_BROWSABLE		= 0x12;
	public static final int CONTROL_ERROR_PLAYER_NOT_ADDRESSED		= 0x13;
	public static final int CONTROL_ERROR_NO_VALID_SEARCH_RESULT 	= 0x14;
	public static final int CONTROLERROR_NO_AVAILABLE_PLAYER		= 0x15;
	public static final int CONTROL_ERROR_ADDRESSED_PLAYER_CHANGED	= 0x16;	
	
	//CONTROL_PLAYSTATUS
	public static final int PLAYSTATUS_STOPPED			= 0x00;
	public static final int PLAYSTATUS_PLAYING			= 0x01;
	public static final int PLAYSTATUS_PAUSED			= 0x02;
	public static final int PLAYSTATUS_FWD_SEEK			= 0x03;
	public static final int PLAYSTATUS_REV_SEEK			= 0x04;
	public static final int PLAYSTATUS_ERROR			= 0xFF;

	//CONTROL_NOTIFICATION_EVENT
	public static final int EVENT_PLAYBACK_STATUS_CHANGED				= 0x01;
	public static final int EVENT_TRACK_CHANGED							= 0x02;
	public static final int EVENT_TRACK_REACHED_END						= 0x03;
	public static final int EVENT_TRACK_REACHED_START					= 0x04;
	public static final int EVENT_PLAYBACK_POS_CHANGED					= 0x05;
	public static final int EVENT_BATT_STATUS_CHANGED					= 0x06;
	public static final int EVENT_SYSTEM_STATUS_CHANGED					= 0x07;
	public static final int EVENT_PLAYER_APPLICATION_SETTING_CHANGED	= 0x08;
	public static final int EVENT_NOW_PLAYING_CONTENT_CHANGED			= 0x09;
	public static final int EVENT_AVAILABLE_PLAYERS_CHANGED				= 0x0a;
	public static final int EVENT_ADDRESSED_PLAYER_CHANGED				= 0x0b;
	public static final int EVENT_UIDS_CHANGED							= 0x0c;
	public static final int EVENT_VOLUME_CHANGED						= 0x0d;

	public static final int EVENT_PLAYER_APPLICATION_SETTING_SUPPORTED	= 0xf0;
	public static final int EVENT_STREAM_STATUS_CHANGED					= 0xf1;
	public static final int EVENT_MEDIA_METADATA						= 0xff;
	
	//BROWSING_SUPPORT_TYPE	
	public static final int BROWSING_SUP_PASSTHROUGH_CMD				= 0x01;
	public static final int BROWSING_SUP_BASIC_GROUP_NAVI				= 0x02;
	public static final int BROWSING_SUP_ADVANCED_CTPLAYER				= 0x03;
	public static final int BROWSING_SUP_BROWSING						= 0x04;
	public static final int BROWSING_SUP_SEARCHING						= 0x05;
	public static final int BROWSING_SUP_ADDTONOWPLAYING				= 0x06;
	public static final int BROWSING_SUP_UIDS_UNIQUE					= 0x07;
	public static final int BROWSING_SUP_ONLYBROWSABLE					= 0x08;
	public static final int BROWSING_SUP_ONLYSEARCHABLE					= 0x09;
	public static final int BROWSING_SUP_NOWPLAYING						= 0x0A;
	public static final int BROWSING_SUP_UIDPERSISTENCY					= 0x0B;		

	//CONTROL_OPERATION_EVENT
	public static final int EVENT_OPERATION_RECEIVED			= 0x00;
	public static final int EVENT_OPERATION_NOT_IMPLEMENTED			= 0x08;
	public static final int EVENT_OPERATION_ACCEPTED			= 0x09;
	public static final int EVNET_OPERATION_REJECTED			= 0x0A;
	
	//CONTROL_EVENT_TYPE
	public static final int TYPE_CONNECT_STATE			= 0x01;
	public static final int TYPE_OPERATION_EVENT			= 0x02;
	public static final int TYPE_EVENTS_SUPPORTED			= 0x03;
	public static final int TYPE_EVENT_NOTIFICATION			= 0x04;
	public static final int TYPE_EVENTS_BROWSING_SUPPORTED			= 0x05;
	
	//CONTROL_STREAM_STATUS
	public static final int STREAM_STATUS_SUSPEND			= 0x00;
	public static final int STREAM_STATUS_STREAMING			= 0x01;
	
	//<<AVRCP Browsing 
	//AVRCP_BROWSING_Scope
	public static final int SCOPE_Media_Player_List = 0;
	public static final int SCOPE_Media_Player_Virtual_Filesystem = 0x01;
	public static final int SCOPE_Search = 0x02;
	public static final int SCOPE_Now_Playing = 0x03;
	
	//AVRCP_BROWSING_Attr
	public static final int ELEMENT_ATTR_TITLE				= 0x01;
	public static final int ELEMENT_ATTR_ARTIST				= 0x02;
	public static final int ELEMENT_ATTR_ALBUM				= 0x04;
	public static final int ELEMENT_ATTR_NUMOFMEDIA			= 0x08;
	public static final int ELEMENT_ATTR_TOTALNUMOFMEDIA	= 0x10;
	public static final int ELEMENT_ATTR_GENRE				= 0x20;
	public static final int ELEMENT_ATTR_PLAYTIME			= 0x40;
	
	//CONTROL_PLAYER_SETTING: AVRCP Player Attribute ID
	public static final int PLAYER_ATTRIBUTE_EQUALIZER		= 0x01;
	public static final int PLAYER_ATTRIBUTE_REPEAT			= 0x02;
	public static final int PLAYER_ATTRIBUTE_SHUFFLE		= 0x03;
	public static final int PLAYER_ATTRIBUTE_SCAN			= 0x04;
	
	
	//CONTROL_EQUALIZER_VALUES: AVRCP Player Equalizer Setting Values
	public static final int PLAYER_EQUALIZER_OFF			= 0x01;
	public static final int	PLAYER_EQUALIZER_ON				= 0x02;
	
	//CONTROL_REPEAT_MODE_VALUES:AVRCP Player Repeat Mode Setting Values
	public static final int PLAYER_REPEAT_MODE_OFF			= 0x01;
	public static final int PLAYER_REPEAT_MODE_SINGLE_TRACK	= 0x02;
	public static final int PLAYER_REPEAT_MODE_ALL_TRACK	= 0x03;
	public static final int PLAYER_REPEAT_MODE_GROUP		= 0x04;
	
	//CONTROL_SHUFFLE_VALUES:AVRCP Player Shuffle Setting Values
	public static final int PLAYER_SHUFFLE_OFF				= 0x01;
	public static final int PLAYER_SHUFFLE_ALL_TRACK		= 0x02;
	public static final int PLAYER_SHUFFLE_GROUP			= 0x03;	
	
	//CONTROL_SCAN_VALUES :AVRCP Player Scan Setting Values
	public static final int PLAYER_SCAN_OFF					= 0x01;
	public static final int PLAYER_SCAN_ALL_TRACK			= 0x02;
	public static final int PLAYER_SCAN_GROUP				= 0x03;
	
	//CONTROL_ACTION_FLAG: AVRCP ControlRequest action flag
	public static final int AVRCP_CONTROL_ACT_PRESS			= 0x00;
	public static final int	AVRCP_CONTROL_ACT_RELEASE		= 0x01;
//>>	

}

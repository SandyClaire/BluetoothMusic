/**
 * 
 */
package com.hsae.d531mc.bluetooth.music.util;


/**
 * @author wangda
 *
 */
public class MusicActionDefine {
    static public final int ACTION_APP_LAUNCHED = 1;
    static public final int ACTION_APP_ONINTENT = 2;
    static public final int ACTION_APP_RESUME = 3;
    
    static public final int ACTION_APP_EXIT = 99;
    static public final int ACTION_ISBTSEARCHING = 0X10010;
    public static final int ACTION_SEACH_CALLBACK = 0X10011;
    public static final int ACTION_SETTING_UPDATE_LOCALNAME = 0X1012;
    
    static public final int ACTION_BLUETOOTH_ENABLE_STATUS_CHANGE = 100;
    
    // CONNECT status
    static public final int ACTION_A2DP_CONNECT_STATUS_CHANGE = 10;
    static public final int ACTION_A2DP_PLAY_PAUSE_STATUS_CHANGE = 11;
    static public final int ACTION_A2DP_SUPPORT_MATE_DATA_STATUS_CHANGE = 12;
    static public final int ACTION_A2DP_CURRENT_MUSIC_POSITION_CHANGE = 13;
    
    //CONTROL event
    static public final int ACTION_A2DP_PLAY = 20;
    static public final int ACTION_A2DP_PAUSE = 21;
    static public final int ACTION_A2DP_PREV = 22;
    static public final int ACTION_A2DP_NEXT = 23;
    static public final int ACTION_A2DP_FASTFORWORD = 24;
    static public final int ACTION_A2DP_REWIND = 25;
    static public final int ACTION_A2DP_FASTFORWORD_CANCEL = 26;
    static public final int ACTION_A2DP_REWIND_CANCEL = 27;
    
    //MODEL 
    static public final int ACTION_A2DP_REPEAT_MODEL = 30;
    static public final int ACTION_A2DP_SHUFFLE_MODEL = 31;
        
    static public final int ACTION_A2DP_REQUEST_AUDIO_FOCUSE = 40;
    
    static public final int ACTION_A2DP_SHUFFLE_ATTRIBUTE = 50;
    static public final int ACTION_A2DP_REPEAT_ATTRIBUTE = 51;
    static public final int ACTION_A2DP_PLAYERSETTING_CHANGED_EVENT = 52;
    
    static public final int ACTION_A2DP_ACTIVITY_PAUSE = 60;
    static public final int ACTION_A2DP_ACTIVITY_FINISH = 61;
    
    // SETTING page
    static public final int ACTION_SETTING_INQUIRY = 70;
    static public final int ACTION_SETTING_STOP_INQUIRY = 71;
    static public final int ACTION_SETTING_PAIR = 72;
    static public final int ACTION_SETTING_UNPAIR = 73;
    static public final int ACTION_SETTING_INQUIRY_DEVICES = 74;
    static public final int ACTION_SETTING_INQUIRY_FINISH = 75;
    static public final int ACTION_SETTING_CONNECT_STATUS_CHANGE = 76;
    static public final int ACTION_SETTING_PAIR_STATUS_CHANGE = 77;
    static public final int ACTION_SETTING_DISCONNECT_MOBILE = 78;
    static public final int ACTION_SETTING_CONNECT_MOBILE = 79;
    
    static public final int ACTION_SETTING_GET_PAIRED_DEVICES = 80;
    static public final int ACTION_SETTING_UPDATE_BG = 81;
    static public final int ACTION_SETTING_GET_CARPLAY_STATUS = 82;
    static public final int ACTION_SETTING_GET_CARLIFE_STATUS = 83;
    static public final int ACTION_USB_DISCONNECT = 84;
    
    
    static public final String ACTION_A2DP_FINISH_ACTIVITY = "action_finish_music_activity";
    
//    static public final String ACTION_A2DP_AUTO_CONNECT = "action_A2DP_AUTO_CONNECT";
    
}

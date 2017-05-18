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
    static public final int ACTION_APP_EXIT = 99;

    
    // connect status
    static public final int ACTION_A2DP_CONNECT_STATUS_CHANGE = 10;
    static public final int ACTION_A2DP_PLAY_PAUSE_STATUS_CHANGE = 11;
    static public final int ACTION_A2DP_SUPPORT_MATE_DATA_STATUS_CHANGE = 12;
    static public final int ACTION_A2DP_CURRENT_MUSIC_POSITION_CHANGE = 13;
    
    //play event
    static public final int ACTION_A2DP_PLAY = 20;
    static public final int ACTION_A2DP_PAUSE = 21;
    static public final int ACTION_A2DP_PREV = 22;
    static public final int ACTION_A2DP_NEXT = 23;
    static public final int ACTION_A2DP_FASTFORWORD = 24;
    static public final int ACTION_A2DP_REWIND = 25;
    
    static public final int ACTION_A2DP_REPEAT_ALL = 30;
    static public final int ACTION_A2DP_REPEAT_SINGLE = 31;
    static public final int ACTION_A2DP_REPEAT_ORDER = 32;
    static public final int ACTION_A2DP_SHUFFLE_OPEN = 33;
    static public final int ACTION_A2DP_SHUFFLE_CLOSE = 34;
    
    static public final int ACTION_A2DP_REQUEST_AUDIO_FOCUSE = 40;
    
    
    static public final String ACTION_A2DP_FINISH_ACTIVITY = "action_finish_music_activity";
    
}

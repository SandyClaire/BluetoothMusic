package com.hsae.autosdk.bt.music;
import com.hsae.autosdk.bt.music.BTMusicInfo;

/**
* 蓝牙音乐管理器
*/
interface IBTMusicListener {

	/**
    * 模式变化回调函数
    * @param shufflemode, repeatmode
    */
	void onModeChanged(int shufflemode, int repeatmode);

	/**
    * 播放变化回调函数
    * @param state
    */
	void onPlaybackStateChanged(int state);
	
	/**
    * 音轨进度变化回调函数
    * @param position, duration
    */
	void onTrackProgress(long position, long duration);
	
	
	/**
	 * 歌曲切换时回调，同步歌曲信息
	 */
	void syncBtMusicInfo(in BTMusicInfo btm);
}
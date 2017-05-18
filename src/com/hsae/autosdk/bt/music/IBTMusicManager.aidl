package com.hsae.autosdk.bt.music;

import com.hsae.autosdk.bt.music.IBTMusicListener;

/**
 * 蓝牙音乐管理器
 */
interface IBTMusicManager {

	/**
	 * 判断蓝牙音乐是否连接
	 */
	boolean isConnected();

	/**
	 * 暂停当前蓝牙音乐
	 */
	oneway void pause();

	/**
	 * 播放当前蓝牙音乐
	 */
	oneway void play();

	/**
	 * 切换蓝牙音乐至上一曲
	 */
	void prev();

	/**
	 * 切换蓝牙音乐至下一曲
	 */
	void next();

	/**
	 * 快进
	 */
	int forward();

	/**
	 * 快退
	 */
	int backward();

	/**
	 * 设置随机模式
	 * 
	 * @param shufflemode
	 */
	void setShuffleMode(int shufflemode);

	/**
	 * 获取随机模式
	 * 
	 * @return int
	 */
	int getShuffleMode();

	/**
	 * 设置循环模式
	 * 
	 * @param repeatmode
	 *            - 0: sequence(as default), 1: single repeat, 2: all repeat, 3:
	 *            shuffle.
	 */
	void setRepeatMode(int repeatmode);

	/**
	 * 获取循环模式
	 * 
	 * @return int
	 */
	int getRepeatMode();

	/**
	 * 获取专辑图片
	 * 
	 * @return Bitmap
	 */
	Bitmap getArtwork();

	/**
	 * 获取当前播放音轨的歌曲名称
	 * 
	 * @return String
	 */
	String getTrackName();

	/**
	 * 获取当前播放音轨的专辑名称
	 * 
	 * @return String
	 */
	String getAlbumName();

	/**
	 * 获取当前播放音轨的音乐家名称
	 * 
	 * @return String
	 */
	String getArtistName();

	/**
	 * 播放指定歌曲名的音乐
	 */
	void playByName(String songName); // VR的

	oneway void show(); //起界面
	oneway void hide(); //隐藏界面
	
	void popUpCurrentMode();

	/**
	 * Hmi触发回调
	 */
	void onHmiChanged(int hmiIndex, boolean down); // key prev、next，down false/true

	/**
	 * 注册状态监听
	 * 
	 * @param listener
	 */
	void registerBTMusicListener(IBTMusicListener listener);

	/**
	 * 反注册状态监听
	 * 
	 * @param listener
	 */
	void unregisterBTMusicListener(IBTMusicListener listener);

}
package com.hsae.d531mc.bluetooth.music.view;

import com.hsae.d531mc.bluetooth.music.entry.MusicBean;

public interface IMusicView {

	/**
	 * update view by connect status
	 * @param status
	 */
	public void updateViewByConnectStatus(int status);
	
	/**
	 * update play button by status
	 * @param status
	 */
	public void updatePlayBtnByStatus(boolean flag);

	/**
	 * update data info
	 * @param title
	 * @param atrlist
	 * @param album
	 */
	public void updateMusicDataInfo(MusicBean bean , boolean isSupport);
	
	/**
	 * update music play current time
	 * @param currentTime
	 */
	public void updateMusicPlayCurrentTime(String currentTime , boolean isPlaying);

}

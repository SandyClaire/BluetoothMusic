package com.hsae.d531mc.bluetooth.music.view;

import java.util.ArrayList;

import android.graphics.Bitmap;

import com.hsae.d531mc.bluetooth.music.entry.MusicBean;

/**
 * 
 * @author wangda
 *
 */
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
	
	/**
	 * finish Music Activity
	 */
	public void finishMusicActivity();
	
	/**
	 * 更新背景图片
	 * @return
	 */
	public void updateBgBitmap(Bitmap bg);
	
	/**
	 * 更新断开提示语
	 * @param conn
	 */
	public void updateTextTipShow(boolean conn);

	public void onUsbDesconnet();

}

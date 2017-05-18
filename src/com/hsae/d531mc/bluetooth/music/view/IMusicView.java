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
	 * 更新顺序播放模式
	 * @param allowList
	 */
	public void updateRepeatAllowList(ArrayList<Integer> allowList);
	
	/**
	 * 更新随机播放模式
	 * @param allowList
	 */
	public void updateShuffleAllowList(ArrayList<Integer> allowList);
	
	/**
	 * 跟新播放模式状态
	 * @param nAttrID
	 * @param nAttrValue
	 */
	public void UpdatePlayerModeSetting(int nAttrID,int nAttrValue); 
	
	/**
	 * 主动获取随机播放模式
	 * @param allowList
	 */
	public void updateShuffleAllowArray(int[] AllowArray , int num);
	
	/**
	 * 主动获取顺序播放模式
	 * @param allowList
	 */
	public void updateRepeatAllowArray(int[] AllowArray , int num);
	
	/**
	 * finish Music Activity
	 */
	public void finishMusicActivity();
	
	/**
	 * 更新背景图片
	 * @return
	 */
	public void updateBgBitmap(Bitmap bg);

}

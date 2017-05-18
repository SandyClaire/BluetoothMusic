package com.hsae.d531mc.bluetooth.music.model;

import com.hsae.d531mc.bluetooth.music.entry.MusicBean;

/**
 * 
 * @author wangda
 *
 */
public interface IMusicModel {

	/**
	 * release model
	 */
	public void releaseModel();

	/**
	 * get a2dp connect status
	 */
	public int getA2DPConnectStatus();

	/**
	 * update connect status msg
	 * @param status
	 */
	public void updateConnectStatusMsg(int status);

	/**
	 * set avrcp control command
	 */
	public void setAVRCPControl(int command);

	/**
	 * update play or pause status
	 * @param status
	 */
	public void updatePlayOrPauseStatus(boolean flag);
	
	/**
	 * update play or pause status
	 * @param status
	 */
	public int playStatus();
	
	/**
	 * support metadata or not
	 * @return
	 */
	public boolean A2DPSupportMetadata();
	
	/**
	 * get current attributes
	 * @param type
	 * @return
	 */
	public String getCurrentDataAttributes(int type);
	
	/**
	 * get current music bean
	 * @param bean
	 */
	public void getCurrentMusicBean(MusicBean bean);
	
	/**
	 * get current music play position
	 * @param position
	 */
	public void getCurrentMusicPlayPosition(String position , Boolean isPlaying);
	
	/**
	 * set current player ap setting
	 * @param nAttrID
	 * @param nAttrValue
	 */
	public void setCurrentPlayerAPSettings(int nAttrID , int nAttrValue);
	
	public void requestAudioFoucs();
	
}

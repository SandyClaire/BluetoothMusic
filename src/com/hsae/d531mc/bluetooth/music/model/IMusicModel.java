package com.hsae.d531mc.bluetooth.music.model;

import java.util.ArrayList;

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
	
	/**
	 * 抢占音源焦点
	 */
	public void requestAudioFoucs();
	
	/**
	 * 更新顺序播放模式
	 * @param AllowList
	 */
	public void updateAttributeRepeat(ArrayList<Integer> AllowList);
	
	/**
	 * 更新随机播放模式
	 * @param AllowList
	 */
	public void updateAttributeShuffle(ArrayList<Integer> AllowList);
	
	/**
	 * 跟新播放模式状态
	 * @param nAttrID
	 * @param nAttrValue
	 */
	public void updataPlayerModel(int nAttrID , int nAttrValue);
	
	/**
	 * 获取播放模式集合
	 * @param nAttrID
	 * @param nAllowArray
	 * @param nArraySize
	 * @return
	 */
	public int retrieveCurrentPlayerAPSupported(int nAttrID, int[] nAllowArray,
			int nArraySize);
	/**
	 * 获取当前模式
	 * @param nAttrID
	 * @return
	 */
	public int retrieveCurrentPlayerAPSetting(int nAttrID);
	
	/**
	 * 播放界面不在最前端
	 */
	public void sendActivityPauseMsg();
	
	/**
	 * 设置音量大小
	 * @param flag
	 * @param vol
	 */
	public void setDeviceVol(boolean flag, int vol);
	
}

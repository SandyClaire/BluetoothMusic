package com.hsae.d531mc.bluetooth.music.model;

import java.util.ArrayList;

import android.graphics.Bitmap;

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
	
	public void setAVRCPControl(int command,int isRelease);

	/**
	 * update play or pause status
	 * @param status
	 */
	public void updatePlayOrPauseStatus(boolean flag);
	
	/**
	 * 获取播放歌曲信息
	 */
	public void getMusicMatedata();
	
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
	 * @param nAttrValue
	 */
	public void setCurrentPlayerRepeatModel(int nAttrValue);
	
	/**
	 * set current player ap setting
	 * @param nAttrValue
	 */
	public void setCurrentPlayerShuffleModel(int nAttrValue);
	
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
	
	/**
	 * finish music activity
	 */
	public void finishMusicActivity();
	
	/**
	 * 自动连接A2DP
	 */
	public void autoConnectA2DP();
	
	/**
	 * 获取缓存背景
	 */
	public Bitmap getBg();
	
	/**
	 * 更新背景
	 */
	public void updateBg();
	
	/**
	 * 获取carlife连接状态
	 * @return
	 */
	public boolean getCarlifeConnectStatus();
	
	/**
	 * 更新carlife连接状态
	 */
	public void updateCarlifeConnectStatus();
	
	/**
	 * 初始化播放状态
	 * @return
	 */
	public boolean initPlayStatus();

	public void onUsbDisConnect();

	public void setPrevClicked();

	public void setNextClicked();

	public void removeAutoPlay();

	public void requestAudioFoucs(boolean b, boolean c);

}

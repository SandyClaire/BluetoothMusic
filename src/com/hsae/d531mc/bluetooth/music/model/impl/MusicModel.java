package com.hsae.d531mc.bluetooth.music.model.impl;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.anwsdk.service.AudioControl;
import com.anwsdk.service.MangerConstant;
import com.hsae.autosdk.source.Source;
import com.hsae.autosdk.source.SourceConst.App;
import com.hsae.autosdk.util.LogUtil;
import com.hsae.d531mc.bluetooth.music.entry.MusicBean;
import com.hsae.d531mc.bluetooth.music.model.IMusicModel;
import com.hsae.d531mc.bluetooth.music.observer.ContactsSubjecter;
import com.hsae.d531mc.bluetooth.music.service.BluetoothMusicModel;
import com.hsae.d531mc.bluetooth.music.util.MusicActionDefine;

/**
 * 
 * @author wangda
 *
 */
public class MusicModel extends ContactsSubjecter implements IMusicModel {

	private static final String TAG = "MusicModel";
	private Context mContext;
	private BluetoothMusicModel mBluetoothMusicModel;
	private Handler mHandler = new Handler();

	public MusicModel(Context mContext) {
		super();
		this.mContext = mContext;
		init();
	}

	private void init() {
		LogUtil.i(TAG, "--- init +++");
		mBluetoothMusicModel = BluetoothMusicModel.getInstance(mContext);
		mBluetoothMusicModel.registMusicListener((IMusicModel) this);
	}

	@Override
	public void releaseModel() {
		LogUtil.i(TAG, "--- releaseModel +++");
		mBluetoothMusicModel.unregistMusicListener();
	}

	/**
	 * 获取A2DP连接状态
	 */
	@Override
	public int getA2DPConnectStatus() {
		int backCode = 0;
		try {
			backCode = mBluetoothMusicModel.getConnectStatus(MangerConstant.PROFILE_AUDIO_STREAM_CHANNEL, 0);
		} catch (RemoteException e) {
			e.printStackTrace();
			backCode = -3;
		}
		LogUtil.i(TAG, "--- getA2DPConnectStatus = " + backCode);
		return backCode;
	}

	@Override
	public void updateConnectStatusMsg(int status) {
		Message msg = Message.obtain();
		msg.what = MusicActionDefine.ACTION_A2DP_CONNECT_STATUS_CHANGE;
		Bundle mBundle = new Bundle();
		mBundle.putInt("connectStatus", status);
		msg.setData(mBundle);
		this.notify(msg, FLAG_RUN_SYNC);
		LogUtil.i(TAG, "--- updateConnectStatusMsg = " + status);
	}

	/**
	 * 蓝牙控制命令
	 */
	@Override
	public void setAVRCPControl(int command) {
		try {
			mBluetoothMusicModel.AVRCPControl(command);
			getMusicMatedata();
			if (command == AudioControl.CONTROL_PAUSE) {
				mBluetoothMusicModel.isHandPuse = true;
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		LogUtil.i(TAG, "setAVRCPControl -- command = " + command);
	}
	
	@Override
	public void setAVRCPControl(int command, int isRelease) {
		try {
			LogUtil.i(TAG, "AVRCPControlEx: command = " + command + " , isRelease = " + isRelease);
			mBluetoothMusicModel.AVRCPControlEx(command, isRelease);
		} catch (RemoteException e) {
		}
	}

	@Override
	public void updatePlayOrPauseStatus(boolean flag) {
		Message msg = Message.obtain();
		msg.what = MusicActionDefine.ACTION_A2DP_PLAY_PAUSE_STATUS_CHANGE;
		Bundle mBundle = new Bundle();
		mBundle.putBoolean("playStatus", flag);
		msg.setData(mBundle);
		this.notify(msg, FLAG_RUN_SYNC);
		LogUtil.i(TAG, "updatePlayOrPauseStatus -- status = " + flag);
	}

	@Override
	public void getMusicMatedata() {
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				try {
					mBluetoothMusicModel.getMusicInfo();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}, 400);
	}

	@Override
	public boolean A2DPSupportMetadata() {
		boolean isSupport = false;
		try {
			isSupport = mBluetoothMusicModel.isA2DPSupportMetadata();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		LogUtil.i(TAG, "A2DPSupportMetadata -- isSupport = " + isSupport);
		return isSupport;
	}

	@Override
	public String getCurrentDataAttributes(int type) {
		String attributes = "";
		try {
			attributes = mBluetoothMusicModel.A2DPGetCurrentAttributes(type);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return attributes;
	}

	@Override
	public void getCurrentMusicBean(MusicBean bean) {
		Message msg = Message.obtain();
		msg.what = MusicActionDefine.ACTION_A2DP_SUPPORT_MATE_DATA_STATUS_CHANGE;
		Bundle mBundle = new Bundle();
		mBundle.putSerializable("musicBean", bean);
		msg.setData(mBundle);
		this.notify(msg, FLAG_RUN_SYNC);
		LogUtil.i(TAG, "getCurrentMusicBean -- bean = " + bean);
	}

	@Override
	public void getCurrentMusicPlayPosition(String position, Boolean isPlaying) {
		Message msg = Message.obtain();
		msg.what = MusicActionDefine.ACTION_A2DP_CURRENT_MUSIC_POSITION_CHANGE;
		Bundle mBundle = new Bundle();
		mBundle.putString("currentTime", position);
		mBundle.putBoolean("playStatus", isPlaying);
		msg.setData(mBundle);
		this.notify(msg, FLAG_RUN_SYNC);
		LogUtil.i(TAG, "getCurrentMusicPlayPosition -- position = " + position + "-- isPlaying = " + isPlaying);
	}

	@Override
	public void requestAudioFoucs() {
		mBluetoothMusicModel.requestAudioFocus(true);
		mBluetoothMusicModel.isActivityShow = true;
	}
	
	@Override
	public void sendActivityPauseMsg() {
		//TODO ... 加判断
		if (mBluetoothMusicModel == null) {
			return;
		}
		if (mBluetoothMusicModel.isFastForward) {
			setAVRCPControl(AudioControl.CONTROL_FASTFORWARD, 1);
		}
		
		if (mBluetoothMusicModel.isRewind) {
			setAVRCPControl(AudioControl.CONTROL_REWIND, 1);
		}
	
		Source source = new Source();
		if (source.getCurrentSource() == App.BT_MUSIC) {
			LogUtil.i(TAG,"mainAudioChanged false begin");
			mBluetoothMusicModel.mainAudioChanged(false);
			LogUtil.i(TAG,"mainAudioChanged false end");
		}
		
		mBluetoothMusicModel.isActivityShow = false;
	}

	@Override
	public void setDeviceVol(boolean flag, int vol) {
		
	}

	@Override
	public void finishMusicActivity() {
		Message msg = Message.obtain();
		msg.what = MusicActionDefine.ACTION_A2DP_ACTIVITY_FINISH;
		this.notify(msg, FLAG_RUN_SYNC);
		LogUtil.i(TAG, "finishMusicActivity");
	}

	@Override
	public void autoConnectA2DP() {
		
	}

	@Override
	public Bitmap getBg() {
		LogUtil.i(TAG, "getBg");
		return mBluetoothMusicModel.getWallPaperBitmap();
	}

	@Override
	public void updateBg() {
		Message msg = Message.obtain();
		msg.what = MusicActionDefine.ACTION_SETTING_UPDATE_BG;
		this.notify(msg, FLAG_RUN_SYNC);
	}

	@Override
	public boolean getCarlifeConnectStatus() {
		return mBluetoothMusicModel.isCarlifeConnected();
	}

	@Override
	public void updateCarlifeConnectStatus() {
		Message msg = Message.obtain();
		msg.what = MusicActionDefine.ACTION_SETTING_GET_CARLIFE_STATUS;
		this.notify(msg, FLAG_RUN_MAIN_THREAD);
	}

	@Override
	public boolean initPlayStatus() {
		return mBluetoothMusicModel.isPlay;
	}

	@Override
	public void onUsbDisConnect() {
		Message msg = Message.obtain();
		msg.what = MusicActionDefine.ACTION_USB_DISCONNECT;
		this.notify(msg, FLAG_RUN_MAIN_THREAD);
	}

	@Override
	public void setPrevClicked() {
		Message msg = Message.obtain();
		msg.what = MusicActionDefine.ACTION_A2DP_PREV;
		this.notify(msg, FLAG_RUN_MAIN_THREAD);
	}

	@Override
	public void setNextClicked() {
		Message msg = Message.obtain();
		msg.what = MusicActionDefine.ACTION_A2DP_NEXT;
		this.notify(msg, FLAG_RUN_MAIN_THREAD);
	}

	@Override
	public void removeAutoPlay() {
		if (mBluetoothMusicModel != null) {
			mBluetoothMusicModel.removeAutoPlay();
		}
	}

	@Override
	public void requestAudioFoucs(boolean b, boolean c) {
		mBluetoothMusicModel.requestAudioFocus(true, true);
	}

	@Override
	public void setHandPause(boolean b) {
		if (mBluetoothMusicModel != null) {
			mBluetoothMusicModel.isHandPuse = false;
		}
	}
	
	/**
	 * 得到当前蓝牙连接状态
	 * @return 
	 */
	@Override
	public int getBTPowerStatus() {

		int backCode = 0;
		try {
			backCode = mBluetoothMusicModel.getBTPowerStatus();
		} catch (RemoteException e) {
			e.printStackTrace();
			backCode = -3;
		}
		LogUtil.i(TAG, "--- getBTPowerStatus = " + backCode);
		return backCode;
	
	}

	@Override
	public void updateBTPowerStatus(int status) {
		
		if(status == MangerConstant.BTPOWER_STATUS_OFF){
			status = -2;
		}else if(status == MangerConstant.BTPOWER_STATUS_ON){
			status = 0;
		}
		
			Message msg = Message.obtain();
			msg.what = MusicActionDefine.ACTION_BLUETOOTH_ENABLE_STATUS_CHANGE;
			Bundle mBundle = new Bundle();
			mBundle.putInt("enableStatus", status);
			msg.setData(mBundle);
			this.notify(msg, FLAG_RUN_SYNC);
	
	}

}

package com.hsae.d531mc.bluetooth.music.model.impl;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;

import com.anwsdk.service.AudioControl;
import com.anwsdk.service.MangerConstant;
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
		int backCode = -1;
		try {
			backCode = mBluetoothMusicModel.getConnectStatus(MangerConstant.PROFILE_AUDIO_STREAM_CHANNEL, 0);
		} catch (RemoteException e) {
			e.printStackTrace();
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
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		if (command == AudioControl.CONTROL_PAUSE) {
			mBluetoothMusicModel.isHandPuse = true;
		}
		LogUtil.i(TAG, "setAVRCPControl -- command = " + command);
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
					mBluetoothMusicModel.getPlayStatus();
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
	public void setCurrentPlayerRepeatModel(int nAttrValue) {
		mBluetoothMusicModel.setPlayModel(AudioControl.PLAYER_ATTRIBUTE_REPEAT,
				mBluetoothMusicModel.mRepeatAllowedlist, nAttrValue);
		LogUtil.i(TAG, "setCurrentPlayerRepeatModel -- currentType = " + nAttrValue);
	}

	@Override
	public void setCurrentPlayerShuffleModel(int nAttrValue) {
		mBluetoothMusicModel.setPlayModel(AudioControl.PLAYER_ATTRIBUTE_SHUFFLE,
				mBluetoothMusicModel.mShuffleAllowedlist, nAttrValue);
		LogUtil.i(TAG, "setCurrentPlayerShuffleModel -- currentType = " + nAttrValue);
	}

	@Override
	public void requestAudioFoucs() {
		mBluetoothMusicModel.requestAudioFocus(true);
	}

	@Override
	public void updateAttributeRepeat(ArrayList<Integer> AllowList) {
		Message msg = Message.obtain();
		msg.what = MusicActionDefine.ACTION_A2DP_REPEAT_ATTRIBUTE;
		Bundle mBundle = new Bundle();
		mBundle.putIntegerArrayList("repeatList", AllowList);
		msg.setData(mBundle);
		this.notify(msg, FLAG_RUN_SYNC);
		LogUtil.i(TAG, "updateAttributeRepeat -- repeatList = " + AllowList);
	}

	@Override
	public void updateAttributeShuffle(ArrayList<Integer> AllowList) {
		Message msg = Message.obtain();
		msg.what = MusicActionDefine.ACTION_A2DP_SHUFFLE_ATTRIBUTE;
		Bundle mBundle = new Bundle();
		mBundle.putIntegerArrayList("shuffleList", AllowList);
		msg.setData(mBundle);
		this.notify(msg, FLAG_RUN_SYNC);
		LogUtil.i(TAG, "updateAttributeShuffle -- shuffleList = " + AllowList);
	}

	@Override
	public void updataPlayerModel(int nAttrID, int nAttrValue) {
		Message msg = Message.obtain();
		msg.what = MusicActionDefine.ACTION_A2DP_PLAYERSETTING_CHANGED_EVENT;
		Bundle mBundle = new Bundle();
		mBundle.putInt("nAttrID", nAttrID);
		mBundle.putInt("nAttrValue", nAttrValue);
		msg.setData(mBundle);
		this.notify(msg, FLAG_RUN_SYNC);
	}

	@Override
	public int retrieveCurrentPlayerAPSupported(int nAttrID, int[] nAllowArray, int nArraySize) {
		int nWriteSize = 0;
		try {
			nWriteSize = mBluetoothMusicModel.retrieveCurrentPlayerAPSupported(nAttrID, nAllowArray, nArraySize);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return nWriteSize;
	}

	@Override
	public int retrieveCurrentPlayerAPSetting(int nAttrID) {
		int attrID = 0;
		try {
			attrID = mBluetoothMusicModel.retrieveCurrentPlayerAPSetting(nAttrID);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		return attrID;
	}

	@Override
	public void sendActivityPauseMsg() {
		mBluetoothMusicModel.mainAudioChanged(false);
	}

	@Override
	public void setDeviceVol(boolean flag, int vol) {
		try {
			mBluetoothMusicModel.setDeviceVol(flag, vol);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
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
		Intent intent = new Intent(MusicActionDefine.ACTION_A2DP_AUTO_CONNECT);
		mContext.sendBroadcast(intent);
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
		return mBluetoothMusicModel.isCarLifeConnected;
	}

	@Override
	public void updateCarlifeConnectStatus() {
		Message msg = Message.obtain();
		msg.what = MusicActionDefine.ACTION_SETTING_GET_CAPLIFE_STATUS;
		this.notify(msg, FLAG_RUN_MAIN_THREAD);
	}

	@Override
	public boolean initPlayStatus() {
		return mBluetoothMusicModel.isPlay;
	}

}

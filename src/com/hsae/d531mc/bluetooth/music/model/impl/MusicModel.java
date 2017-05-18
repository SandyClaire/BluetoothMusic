package com.hsae.d531mc.bluetooth.music.model.impl;

import java.util.regex.Pattern;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;

import com.anwsdk.service.MangerConstant;
import com.hsae.d531mc.bluetooth.music.entry.MusicBean;
import com.hsae.d531mc.bluetooth.music.model.IMusicModel;
import com.hsae.d531mc.bluetooth.music.observer.ContactsSubjecter;
import com.hsae.d531mc.bluetooth.music.service.BluetoothMusicModel;
import com.hsae.d531mc.bluetooth.music.util.MusicActionDefine;

public class MusicModel extends ContactsSubjecter implements IMusicModel{

	private Context mContext;
	private BluetoothMusicModel mBluetoothMusicModel;
	private Handler mHandler = new Handler();

	public MusicModel(Context mContext) {
		super();
		this.mContext = mContext;
		init();
	}
	
	private void init(){
		mBluetoothMusicModel = BluetoothMusicModel.getInstance(mContext);
		mBluetoothMusicModel.registMusicListener((IMusicModel)this);
	}

	@Override
	public void releaseModel() {
		mBluetoothMusicModel.unregistMusicListener();
	}

	@Override
	public int getA2DPConnectStatus() {
		int backCode = -1;
		try {
			backCode = mBluetoothMusicModel.getConnectStatus(MangerConstant.PROFILE_AUDIO_STREAM_CHANNEL, 0);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
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
	}

	@Override
	public void setAVRCPControl(int command) {
		try {
			mBluetoothMusicModel.AVRCPControl(command);
			playStatus();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updatePlayOrPauseStatus(int status) {
		Message msg = Message.obtain();
		msg.what = MusicActionDefine.ACTION_A2DP_PLAY_PAUSE_STATUS_CHANGE;
		Bundle mBundle = new Bundle();
		mBundle.putInt("playStatus", status);
		msg.setData(mBundle);
		this.notify(msg, FLAG_RUN_SYNC);
	}

	@Override
	public void playStatus() {
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				try {
					mBluetoothMusicModel.getPlayStatus();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}, 500);
	}

	@Override
	public boolean A2DPSupportMetadata() {
		boolean isSupport = false;
		try {
			isSupport = mBluetoothMusicModel.isA2DPSupportMetadata();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return isSupport;
	}

	@Override
	public String getCurrentDataAttributes(int type) {
		String attributes = "";
		try {
			attributes = mBluetoothMusicModel.A2DPGetCurrentAttributes(type);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
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
	}
	
	@Override
	public void getCurrentMusicPlayPosition(String position) {
		Message msg = Message.obtain();
		msg.what = MusicActionDefine.ACTION_A2DP_CURRENT_MUSIC_POSITION_CHANGE;
		Bundle mBundle = new Bundle();
		mBundle.putString("currentTime", position);
		msg.setData(mBundle);
		this.notify(msg, FLAG_RUN_SYNC);
	}

	@Override
	public void setCurrentPlayerAPSettings(int nAttrID, int nAttrValue) {
		try {
			mBluetoothMusicModel.setCurrentPlayerAPSetting(nAttrID, nAttrValue);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}	
	
	
	
}

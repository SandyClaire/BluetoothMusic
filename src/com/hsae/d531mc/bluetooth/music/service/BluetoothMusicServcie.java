package com.hsae.d531mc.bluetooth.music.service;

import java.util.ArrayList;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;

import com.anwsdk.service.AudioControl;
import com.anwsdk.service.MangerConstant;
import com.hsae.autosdk.bt.music.BTMusicInfo;
import com.hsae.autosdk.os.Soc;
import com.hsae.autosdk.os.SocConst.UsbDevices;
import com.hsae.autosdk.source.Source;
import com.hsae.autosdk.util.LogUtil;
import com.hsae.d531mc.bluetooth.music.entry.MusicBean;
import com.hsae.d531mc.bluetooth.music.util.MusicActionDefine;

/**
 * 
 * @author wangda
 *
 */
public class BluetoothMusicServcie extends Service {

	private static final String TAG = "BluetoothMusicServcie";
	private BluetoothMusicModel mBluetoothMusicModel;
	private Context mContext;
	private BTBroadcastReceiver mReceiver = null;
	private int mConnectStatus = 0;
	private String mTitle = "";
	private String mAtrist = "";
	private String mAlbum = "";
	private String mTotalTIme = "";
	private boolean isplaying = false;
	private BTMusicManager mBTMmanager;
	private String mTimePosition = "-1";

	@Override
	public IBinder onBind(Intent intent) {
		if (null != mBTMmanager) {
			return mBTMmanager;
		}
		return null;
	}

	@Override
	public void onCreate() {
		mContext = getApplicationContext();
		mBluetoothMusicModel = BluetoothMusicModel.getInstance(mContext);
		mBluetoothMusicModel.bindService();
		registBroadcast();
		mBTMmanager = BTMusicManager.getInstance(getApplicationContext());
		LogUtil.i(TAG, "---------- service oncreat ------------");
		mBluetoothMusicModel.setMusicStreamMute();
		super.onCreate();
	}

	private void registBroadcast() {
		IntentFilter filter = new IntentFilter();
		mReceiver = new BTBroadcastReceiver();
		filter.addAction(MangerConstant.MSG_ACTION_POWER_STATUS);
		filter.addAction(MangerConstant.MSG_ACTION_A2DP_PLAYSTATUS);
		filter.addAction(MangerConstant.MSG_ACTION_CONNECT_STATUS);
		filter.addAction(MangerConstant.MSG_ACTION_A2DP_FEATURE_SUPPORT);
		filter.addAction(MangerConstant.MSG_ACTION_A2DP_METADATA);
		filter.addAction(MangerConstant.MSG_ACTION_A2DP_PLAYBACKPOS);
		filter.addAction(MangerConstant.MSG_ACTION_A2DP_STREAMSTATUS);
		filter.addAction(MangerConstant.MSG_ACTION_AVRCP_PLAYERSETTING_CHANGED_EVENT);
		filter.addAction(MangerConstant.MSG_ACTION_AVRCP_PLAYERSETTING_SUPPORTED_EVENT);
		filter.addAction(MangerConstant.MSG_ACTION_PAIR_STATUS);
		filter.addAction(MusicActionDefine.ACTION_A2DP_AUTO_CONNECT);
		mContext.registerReceiver(mReceiver, filter);
	}

	@Override
	public void onDestroy() {
		mContext.unregisterReceiver(mReceiver);
		LogUtil.i(TAG, "---------- service onDestroy ------------");
		super.onDestroy();
	}

	private class BTBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			String strAction = intent.getAction();
			Bundle mBundle = intent.getExtras();

			/* 蓝牙开关状态 */
			if (strAction.equals(MangerConstant.MSG_ACTION_POWER_STATUS)) {
				if (mBundle != null) {
					boolean bPowerON = mBundle.getBoolean("Value");
					LogUtil.i(TAG, "MSG_ACTION_POWER_STATUS ----- bPowerON = "
							+ bPowerON);
					if (bPowerON) {
						mBluetoothMusicModel
								.updateBTEnalbStatus(MangerConstant.BTPOWER_STATUS_ON);
					} else {
						mBluetoothMusicModel
								.updateBTEnalbStatus(MangerConstant.BTPOWER_STATUS_OFF);
					}
				}
				/* 蓝牙连接装填 */
			} else if (strAction
					.equals(MangerConstant.MSG_ACTION_CONNECT_STATUS)) {
				if (mBundle != null) {
					int nProfile = mBundle.getInt("Profile");
					if (nProfile == MangerConstant.PROFILE_HF_CHANNEL) {
						mConnectStatus = mBundle.getInt("Value");
						mBluetoothMusicModel
								.updateHFPConnectStatus(mConnectStatus);
						LogUtil.i(TAG, "PROFILE_HF_CHANNEL = mConnectStatus");
					} else if (nProfile == MangerConstant.PROFILE_AUDIO_STREAM_CHANNEL) {
						mConnectStatus = mBundle.getInt("Value");
						LogUtil.i(TAG,
								"PROFILE_AUDIO_STREAM_CHANNEL --- mConnectStatus = "
										+ mConnectStatus);
						mBluetoothMusicModel
								.updateMsgByConnectStatusChange(mConnectStatus);
					} else if (nProfile == MangerConstant.PROFILE_AUDIO_CONTROL_CHANNEL) {
						mConnectStatus = mBundle.getInt("Value");

						LogUtil.i(TAG,
								"PROFILE_AUDIO_CONTROL_CHANNEL --- mConnectStatus = "
										+ mConnectStatus);
						notifyAutoCoreConnectStatus(mConnectStatus);
						if (mConnectStatus == 1) {
							try {
								mBluetoothMusicModel.getPlayStatus();
							} catch (RemoteException e) {
								e.printStackTrace();
							}
						}
					}
				}
				/* 蓝牙音乐数据支持状态 */
			} else if (strAction
					.equals(MangerConstant.MSG_ACTION_A2DP_FEATURE_SUPPORT)) {
				if (mBundle != null) {
					boolean bSupport_Metadata = mBundle.getBoolean("MetaData");
					boolean bSupport_PlayStatus = mBundle
							.getBoolean("PlayStatus");

					LogUtil.i(TAG, "-- bSupport_Metadata = "
							+ bSupport_Metadata + "-- bSupport_PlayStatus = "
							+ bSupport_PlayStatus);
				}
				/* 蓝牙音乐数据信息 */
			} else if (strAction
					.equals(MangerConstant.MSG_ACTION_A2DP_METADATA)) {
				if (mBundle != null) {
					int nPlayStatus = mBundle.getInt("PlayStatus");
					int nDataType = mBundle.getInt("DataType");

					if (nDataType == MangerConstant.Anw_SUCCESS)// meta data
					{
						int nId = mBundle.getInt("Attribute_id");
						String strMetadata = mBundle.getString("MetaData");

						switch (nId) {
						case AudioControl.MEDIA_ATTR_MEDIA_TITLE:
							mTitle = strMetadata;
							break;
						case AudioControl.MEDIA_ATTR_ARTIST_NAME:
							mAtrist = strMetadata;
							break;
						case AudioControl.MEDIA_ATTR_ALBUM_NAME:
							mAlbum = strMetadata;
							break;
						case AudioControl.MEDIA_ATTR_PLAYING_TIME_IN_MS:
							mTotalTIme = strMetadata;
							break;
						default:
							break;
						}

						MusicBean bean = new MusicBean(mTitle, mAtrist, mAlbum,
								mTotalTIme);
						mBluetoothMusicModel.updateCurrentMusicInfo(bean);

						BTMusicInfo info = new BTMusicInfo(bean.getTitle(),
								bean.getAtrist(), bean.getAlbum(), null);
						mBluetoothMusicModel.notifyAutroMusicInfo(info);

						LogUtil.i(TAG, "-- nPlayStatus = " + nPlayStatus
								+ " --- mTitle = " + mTitle + " --- mAtrist = "
								+ mAtrist + " --- mTotalTIme = " + mTotalTIme);
					}
				}
				/* 蓝牙音乐播放状态 */
			} else if (strAction
					.equals(MangerConstant.MSG_ACTION_A2DP_PLAYSTATUS)) {
				if (mBundle != null) {
					int nPlayStatus = mBundle.getInt("PlayStatus");

					if (nPlayStatus == AudioControl.PLAYSTATUS_PLAYING) {
						isplaying = true;
						if (!mTimePosition.equals("-1")) {
							mBluetoothMusicModel.updateCurrentPlayTime(
									mTimePosition, isplaying);
							LogUtil.i(TAG, "PlayTime -- mPosition = "
									+ mTimePosition);
						}
						if (!isTicker) {
							setTimingBegins();
						}
					} else {
						setTimingEnd();
						isplaying = false;
					}
					mBluetoothMusicModel.updatePlayStatus(isplaying);
					mBluetoothMusicModel.setMusicStreamMute();
					LogUtil.i(TAG, "A2DP_PLAYSTATUS -- nPlayStatus = "
							+ nPlayStatus);
				}
				/* 蓝牙音乐播放当前时间信息 */
			} else if (strAction
					.equals(MangerConstant.MSG_ACTION_A2DP_PLAYBACKPOS)) {
				if (mBundle != null) {
					mTimePosition = mBundle.getString("Position");
					if (!mTimePosition.equals("-1")) {
						mBluetoothMusicModel.updateCurrentPlayTime(
								mTimePosition, isplaying);
						LogUtil.i(TAG, "A2DP_PLAYBACKPOS -- strPos = "
								+ mTimePosition);
					}
				}
				/* 蓝牙音乐播放音乐流 */
			} else if (strAction
					.equals(MangerConstant.MSG_ACTION_A2DP_STREAMSTATUS)) {
				if (mBundle != null) {
					int nPlayStatus = mBundle.getInt("StreamStatus");
					LogUtil.i(TAG, "A2DP_STREAMSTATUS -- nPlayStatus = "
							+ nPlayStatus);
					switch (nPlayStatus) {
					case AudioControl.STREAM_STATUS_SUSPEND:
						break;
					case AudioControl.STREAM_STATUS_STREAMING:
						isplaying = true;
						mBluetoothMusicModel.updatePlayStatus(true);
						try {
							mBluetoothMusicModel.getPlayStatus();
						} catch (RemoteException e) {
							e.printStackTrace();
						}
						break;
					}
				}
				/* 蓝牙音乐播放模式变化 */
			} else if (strAction
					.equals(MangerConstant.MSG_ACTION_AVRCP_PLAYERSETTING_CHANGED_EVENT)) {
				if (mBundle != null) {
					int nAttrID = mBundle.getInt("AttributeID");
					int nAttrValue = mBundle.getInt("Value");
					LogUtil.i(TAG,
							"current model nAttrID = " + nAttrID + " --- nAttrValue = " + nAttrValue);

					mBluetoothMusicModel.updatePlayerModelSetting(nAttrID,
							nAttrValue);
				}
				/* 蓝牙音乐播放模式数据 */
			} else if (strAction
					.equals(MangerConstant.MSG_ACTION_AVRCP_PLAYERSETTING_SUPPORTED_EVENT)) {
				if (mBundle != null) {
					int nAttrID = mBundle.getInt("AttributeID");
					ArrayList<Integer> AllowList = mBundle
							.getIntegerArrayList("Allowed");
					switch (nAttrID) {
					case AudioControl.PLAYER_ATTRIBUTE_REPEAT:// 2
						mBluetoothMusicModel.updateRepeatModel(AllowList);
						LogUtil.i(TAG,
								"REPEAT AllowList size = " + AllowList.size());

						break;
					case AudioControl.PLAYER_ATTRIBUTE_SHUFFLE:// 3
						mBluetoothMusicModel.updateShuffleModel(AllowList);
						LogUtil.i(TAG,
								"SHUFFLE AllowList size = " + AllowList.size());
						break;
					}
				}
				/* 蓝牙配对状态 */
			} else if (strAction.equals(MangerConstant.MSG_ACTION_PAIR_STATUS)) {
				if (mBundle != null) {
					String mAddress = mBundle.getString("Address");
					int mStatus = mBundle.getInt("Status");
					mBluetoothMusicModel.updatePairRequest(mAddress, mStatus);
					LogUtil.i(TAG, "--------- pair status = " + mStatus);
				}
			} else if (strAction.equals(MusicActionDefine.ACTION_A2DP_AUTO_CONNECT)) {
				
				autoConnA2dp();
				LogUtil.i(TAG, "--------- autoConnA2dp ----------");
			}
		}
	}
	
	/**
	 * 如果A2DP单独断开情况下，自动连接蓝牙音乐；
	 */
	private void autoConnA2dp(){
		int hfpStatus = 0;
		int a2dpStatus = 0;
		Soc soc = new Soc();
		UsbDevices usbDevices = soc.getCurrentDevice();
		LogUtil.i(TAG, "--------- autoConnA2dp usbDevices = " + usbDevices.toString());
		if (usbDevices.equals(UsbDevices.IPOD)) {
			try {
				hfpStatus = mBluetoothMusicModel.getConnectStatus(MangerConstant.PROFILE_HF_CHANNEL, 0);
				a2dpStatus = mBluetoothMusicModel.getConnectStatus(MangerConstant.PROFILE_AUDIO_STREAM_CHANNEL, 0);
				if (hfpStatus == 1 && a2dpStatus != 1) {
					mBluetoothMusicModel.a2dpConnect(getConnectedDevice());
					mBluetoothMusicModel.AVRCPControl(AudioControl.CONTROL_PLAY);
					mBluetoothMusicModel.getPlayStatus();
					LogUtil.i(TAG, "--------- autoConnA2dp if HFP connected = " + getConnectedDevice());
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 获取当连接设备地址
	 * @return
	 */
	private String getConnectedDevice() {
		String[] strAddress = new String[1];
		String[] strName = new String[1];
		try {
			mBluetoothMusicModel.getConnectedDeviceInfo(
					MangerConstant.PROFILE_HF_CHANNEL, strAddress, strName, 0);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return strAddress[0];
	}

	/**
	 * 通知中间件蓝牙连接装填
	 * 
	 * @param connectStatus
	 */
	private void notifyAutoCoreConnectStatus(int connectStatus) {
		Source mSource = new Source();
		if (connectStatus == MangerConstant.Anw_SUCCESS) {
			mSource.notifyBtState(true);
		} else {
			mSource.notifyBtState(false);
		}
	}

	private Handler stepTimeHandler = new Handler();
	private Ticker mTicker;
	private boolean isTicker = false;

	/**
	 * set timer start
	 */
	public void setTimingBegins() {
		if (mTicker == null) {
			mTicker = new Ticker();
			stepTimeHandler.post(mTicker);
			isTicker = true;
		}
	}

	/**
	 * set time end
	 */
	public void setTimingEnd() {
		if (stepTimeHandler != null) {
			stepTimeHandler.removeCallbacks(mTicker);
			mTicker = null;
		}
		isTicker = false;
	}

	/**
	 * ticker
	 * 
	 * @author wangda
	 *
	 */
	private class Ticker implements Runnable {

		public Ticker() {
		}

		@Override
		public void run() {
			try {
				mBluetoothMusicModel.getPlayStatus();
			} catch (RemoteException e) {
				e.printStackTrace();
			}

			long now = SystemClock.uptimeMillis();
			long next = now + (1000 - now % 1000);
			stepTimeHandler.postAtTime(this, next);
		}

	}

}

package com.hsae.d531mc.bluetooth.music.service;

import java.util.ArrayList;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.anwsdk.service.AudioControl;
import com.anwsdk.service.MangerConstant;
import com.hsae.autosdk.bt.music.BTMusicInfo;
import com.hsae.autosdk.source.Source;
import com.hsae.autosdk.source.SourceConst.App;
import com.hsae.autosdk.util.LogUtil;
import com.hsae.d531mc.bluetooth.music.entry.MusicBean;

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

			if (strAction.equals(MangerConstant.MSG_ACTION_POWER_STATUS)) {
				if (mBundle != null) {
					boolean bPowerON = mBundle.getBoolean("Value");
					if (bPowerON) {
						mBluetoothMusicModel
								.updateBTEnalbStatus(MangerConstant.BTPOWER_STATUS_ON);
					} else {
						mBluetoothMusicModel
								.updateBTEnalbStatus(MangerConstant.BTPOWER_STATUS_OFF);
					}
				}
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
					} else if (nProfile == MangerConstant.PROFILE_AVRCP_BROWSING_CHANNEL) {
						mConnectStatus = mBundle.getInt("Value");
					} else if (nProfile == MangerConstant.PROFILE_AUDIO_CONTROL_CHANNEL) {
						mConnectStatus = mBundle.getInt("Value");
					}
				}
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
			} else if (strAction
					.equals(MangerConstant.MSG_ACTION_A2DP_METADATA)) {
				if (mBundle != null) {
					int nPlayStatus = mBundle.getInt("PlayStatus");
					int nDataType = mBundle.getInt("DataType");

					if (nDataType == 1)// meta data
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
						// case AudioControl.MEDIA_ATTR_TRACK_NUM_IN_ALBUM:
						// break;
						// case AudioControl.MEDIA_ATTR_TOTAL_NUM_IN_ALBUM:
						// break;
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
			} else if (strAction
					.equals(MangerConstant.MSG_ACTION_A2DP_PLAYSTATUS)) {
				if (mBundle != null) {
					int nPlayStatus = mBundle.getInt("PlayStatus");

					if (nPlayStatus == AudioControl.PLAYSTATUS_PLAYING) {
						isplaying = true;
						if (!mTimePosition.equals("-1")) {
							mBluetoothMusicModel.updateCurrentPlayTime(
									mTimePosition, isplaying);
							LogUtil.i(TAG,
									"MSG_ACTION_A2DP_PLAYSTATUS -- mPosition = "
											+ mTimePosition);
						}
					} else {
						isplaying = false;
					}
					mBluetoothMusicModel.updatePlayStatus(isplaying);
					getAudioSource();
					LogUtil.i(TAG,"--MSG_ACTION_A2DP_PLAYSTATUS--nPlayStatus = "
									+ nPlayStatus);
				}
			} else if (strAction
					.equals(MangerConstant.MSG_ACTION_A2DP_PLAYBACKPOS)) {
				if (mBundle != null) {
					mTimePosition = mBundle.getString("Position");
					if (!mTimePosition.equals("-1")) {
						mBluetoothMusicModel.updateCurrentPlayTime(
								mTimePosition, isplaying);
						LogUtil.i(TAG,
								"MSG_ACTION_A2DP_PLAYBACKPOS -- strPos = "
										+ mTimePosition);
					}
				}
			} else if (strAction
					.equals(MangerConstant.MSG_ACTION_A2DP_STREAMSTATUS)) {
				if (mBundle != null) {
					int nPlayStatus = mBundle.getInt("StreamStatus");
					LogUtil.i(TAG,
							"MSG_ACTION_A2DP_STREAMSTATUS -- nPlayStatus = "
									+ nPlayStatus);
					switch (nPlayStatus) {
					case AudioControl.STREAM_STATUS_SUSPEND:
						break;
					case AudioControl.STREAM_STATUS_STREAMING:
						break;
					}
				}
			} else if (strAction
					.equals(MangerConstant.MSG_ACTION_AVRCP_PLAYERSETTING_CHANGED_EVENT)) {
				if (mBundle != null) {
					int nAttrID = mBundle.getInt("AttributeID");
					int nAttrValue = mBundle.getInt("Value");

					mBluetoothMusicModel.updatePlayerModelSetting(nAttrID,
							nAttrValue);
				}
			} else if (strAction
					.equals(MangerConstant.MSG_ACTION_AVRCP_PLAYERSETTING_SUPPORTED_EVENT)) {
				if (mBundle != null) {
					int nAttrID = mBundle.getInt("AttributeID");
					ArrayList<Integer> AllowList = mBundle
							.getIntegerArrayList("Allowed");
					switch (nAttrID) {
					case AudioControl.PLAYER_ATTRIBUTE_REPEAT:// 2
						mBluetoothMusicModel.updateRepeatModel(AllowList);
						LogUtil.i("wangda",
								"PLAYER_ATTRIBUTE_REPEAT AllowList size = "
										+ AllowList.size());

						break;
					case AudioControl.PLAYER_ATTRIBUTE_SHUFFLE:// 3
						mBluetoothMusicModel.updateShuffleModel(AllowList);
						LogUtil.i("wangda",
								"PLAYER_ATTRIBUTE_SHUFFLE AllowList size = "
										+ AllowList.size());
						break;

					}
				}
			} else if (strAction.equals(MangerConstant.MSG_ACTION_PAIR_STATUS)) {
				if (mBundle != null) {
					String mAddress = mBundle.getString("Address");
					int mStatus = mBundle.getInt("Status");
					mBluetoothMusicModel.updatePairRequest(mAddress, mStatus);
					LogUtil.i(TAG, "--------- pair status = " + mStatus);
				}
			}
		}
	}

	private void getAudioSource() {
		Source source = new Source();
		try {
			if (source.getCurrentSource() != App.BT_MUSIC) {
				mBluetoothMusicModel.audioSetStreamMode(1);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}

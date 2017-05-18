package com.hsae.d531mc.bluetooth.music.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.anwsdk.service.AudioControl;
import com.anwsdk.service.MangerConstant;
import com.hsae.autosdk.bt.music.BTMusicInfo;
import com.hsae.autosdk.bt.music.BTMusicProxy;
import com.hsae.autosdk.bt.music.IBTMusicListener;
import com.hsae.autosdk.bt.music.IBTMusicManager;
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
	private BTMusicProxy mBTMusicProxy;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		mContext = getApplicationContext();
		mBluetoothMusicModel = BluetoothMusicModel.getInstance(mContext);
		mBluetoothMusicModel.bindService();
		registBroadcast();
		mBTMusicProxy = BTMusicProxy.getInstance();
		mBTMusicProxy.registerBTMusicListener(musicListener);
		mBTMusicProxy.play();
		Log.e(TAG, "---------- service oncreat ------------");
		super.onCreate();
	}
	
	private void registBroadcast() {
		IntentFilter filter = new IntentFilter();
		mReceiver = new BTBroadcastReceiver();
		filter.addAction(MangerConstant.MSG_ACTION_POWER_STATUS);
		filter.addAction(MangerConstant.MSG_ACTION_CONNECT_STATUS);
		filter.addAction(MangerConstant.MSG_ACTION_CALL_STATUS);
		filter.addAction(MangerConstant.MSG_ACTION_CONNECT_REQUEST);
		filter.addAction(MangerConstant.MSG_ACTION_A2DP_FEATURE_SUPPORT);
		filter.addAction(MangerConstant.MSG_ACTION_A2DP_METADATA);
		filter.addAction(MangerConstant.MSG_ACTION_A2DP_PLAYSTATUS);
		filter.addAction(MangerConstant.MSG_ACTION_A2DP_PLAYBACKPOS);
		filter.addAction(MangerConstant.MSG_ACTION_A2DP_STREAMSTATUS);
		filter.addAction(MangerConstant.MSG_ACTION_AVRCP_PLAYERSETTING_CHANGED_EVENT);
		filter.addAction(MangerConstant.MSG_ACTION_AVRCP_PLAYERSETTING_SUPPORTED_EVENT);
		mContext.registerReceiver(mReceiver, filter);
	}

	@Override
	public void onDestroy() {
		mContext.unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	private class BTBroadcastReceiver extends BroadcastReceiver {  
		@Override
		public void onReceive(Context arg0, Intent intent) {
			String strAction = intent.getAction();
			Bundle mBundle = intent.getExtras();

			if (strAction.equals(MangerConstant.MSG_ACTION_CONNECT_STATUS)) {
				if (mBundle != null) {
					int nProfile = mBundle.getInt("Profile");
					if (nProfile == MangerConstant.PROFILE_HF_CHANNEL) {
						mConnectStatus = mBundle.getInt("Value");
						Log.e(TAG, "PROFILE_HF_CHANNEL = mConnectStatus");
					} else if (nProfile == MangerConstant.PROFILE_AUDIO_STREAM_CHANNEL) {
						mConnectStatus = mBundle.getInt("Value");
						Log.e(TAG,
								"PROFILE_AUDIO_STREAM_CHANNEL = mConnectStatus");
						mBluetoothMusicModel
								.updateMsgByConnectStatusChange(mConnectStatus);
						if (mConnectStatus == 1) {
							
						}else {
							
						}
					} else if (nProfile == MangerConstant.PROFILE_AVRCP_BROWSING_CHANNEL) {
						mConnectStatus = mBundle.getInt("Value");
					} else if (nProfile == MangerConstant.PROFILE_AUDIO_CONTROL_CHANNEL) {
						mConnectStatus = mBundle.getInt("Value");
					}
				}
			} else if (strAction
					.equals(MangerConstant.MSG_ACTION_CONNECT_REQUEST)) {
				if (mBundle != null) {
					int nProfile = mBundle.getInt("ProfileType");
					// String strAddress = bundle.getString("Address");
					String mName = mBundle.getString("DeviceName");
					int mIndex = mBundle.getInt("Index");
				}
			} else if (strAction
					.equals(MangerConstant.MSG_ACTION_A2DP_FEATURE_SUPPORT)) {
				if (mBundle != null) {
					int nPlayStatus = mBundle.getInt("PlayStatus");
					Log.e(TAG,
							"MSG_ACTION_A2DP_FEATURE_SUPPORT -- nPlayStatus = "
									+ nPlayStatus);
					boolean bSupport_Metadata = mBundle.getBoolean("MetaData");
					boolean bSupport_PlayStatus = mBundle
							.getBoolean("PlayStatus");
					// ChangerMetaDataSupportStatus();
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
							Log.e(TAG, "MEDIA_ATTR_MEDIA_TITLE = "
									+ strMetadata);
							mTitle = strMetadata;
							break;
						case AudioControl.MEDIA_ATTR_ARTIST_NAME:
							Log.e(TAG, "MEDIA_ATTR_ARTIST_NAME = "
									+ strMetadata);
							mAtrist = strMetadata;
							break;
						case AudioControl.MEDIA_ATTR_ALBUM_NAME:
							Log.e(TAG, "MEDIA_ATTR_ALBUM_NAME = "
									+ strMetadata);
							mAlbum = strMetadata;
							break;
						case AudioControl.MEDIA_ATTR_TRACK_NUM_IN_ALBUM:
							Log.e(TAG, "MEDIA_ATTR_TRACK_NUM_IN_ALBUM = "
									+ strMetadata);
							break;
						case AudioControl.MEDIA_ATTR_TOTAL_NUM_IN_ALBUM:
							Log.e(TAG, "MEDIA_ATTR_TOTAL_NUM_IN_ALBUM = "
									+ strMetadata);
							break;
						case AudioControl.MEDIA_ATTR_PLAYING_TIME_IN_MS:
							mTotalTIme = strMetadata;
							Log.e(TAG, "1111111111111111111111111 = "
									+ strMetadata);
							break;
						default:
							MusicBean bean = new MusicBean(mTitle, mAtrist,
									mAlbum, mTotalTIme);
							mBluetoothMusicModel.updateCurrentMusicInfo(bean);
							break;
						}

						Log.e(TAG,
								"MSG_ACTION_A2DP_METADATA -- nPlayStatus = "
										+ nPlayStatus);
					}
				}
			} else if (strAction
					.equals(MangerConstant.MSG_ACTION_A2DP_PLAYSTATUS)) {
				if (mBundle != null) {
					int nPlayStatus = mBundle.getInt("PlayStatus");
					
					if (nPlayStatus == AudioControl.PLAYSTATUS_PLAYING) {
						isplaying = true;
					}else {
						isplaying = false;
					}
					mBluetoothMusicModel.updatePlayStatus(isplaying);
					// Log.e(TAG,
					// "MSG_ACTION_A2DP_PLAYSTATUS -- nPlayStatus = "
					// + nPlayStatus);
				}
			} else if (strAction
					.equals(MangerConstant.MSG_ACTION_A2DP_PLAYBACKPOS)) {
				if (mBundle != null) {
					String strPos = mBundle.getString("Position");
					mBluetoothMusicModel.updateCurrentPlayTime(strPos,isplaying);
					 Log.e(TAG,
					 "MSG_ACTION_A2DP_PLAYBACKPOS -- strPos = "
					 + strPos);
				}
			} else if (strAction
					.equals(MangerConstant.MSG_ACTION_A2DP_STREAMSTATUS)) {
				if (mBundle != null) {
					int nPlayStatus = mBundle.getInt("PlayStatus");
					// Log.e(TAG,
					// "MSG_ACTION_A2DP_STREAMSTATUS -- nPlayStatus = "
					// + nPlayStatus);
					switch (nPlayStatus) {
					case AudioControl.STREAM_STATUS_SUSPEND:
						// mHandler.removeCallbacks(A2DPActivity.this);
						// mMusicHandler.removeCallbacks(updateMusicPlayTimer);
						break;
					case AudioControl.STREAM_STATUS_STREAMING:
						if (isplaying == false) {
							try {
								isplaying = true;
								String totalTime = mBluetoothMusicModel.A2DPGetCurrentAttributes(AudioControl.MEDIA_ATTR_PLAYING_TIME_IN_MS);
								mBluetoothMusicModel.updateCurrentPlayTime(totalTime , isplaying);
								Log.e(TAG, "------ ################");
							} catch (RemoteException e) {
								e.printStackTrace();
							}
						}
						// if(bPlaying == false)
						// ChangerMetaDataSupportStatus();
						// bPlaying = true;
						break;
					}
				}
			} else if (strAction
					.equals(MangerConstant.MSG_ACTION_AVRCP_PLAYERSETTING_CHANGED_EVENT)) {
				if (mBundle != null) {
					int nPlayStatus = mBundle.getInt("PlayStatus");
					// Log.e(TAG,
					// "MSG_ACTION_AVRCP_PLAYERSETTING_CHANGED_EVENT -- nPlayStatus = "
					// + nPlayStatus);
				}
			} else if (strAction
					.equals(MangerConstant.MSG_ACTION_AVRCP_PLAYERSETTING_SUPPORTED_EVENT)) {
				if (mBundle != null) {
					int nPlayStatus = mBundle.getInt("PlayStatus");
					// Log.e(TAG,
					// "MSG_ACTION_AVRCP_PLAYERSETTING_SUPPORTED_EVENT -- nPlayStatus = "
					// + nPlayStatus);
				}
			} else if (strAction.equals(MangerConstant.MSG_ACTION_CALL_STATUS)) {
				 int callStatus = mBundle.getInt("Value");
//				if (callStatus == MangerConstant.CALLSTATUS_ENDCALL) {
//					if (isplaying) {
//						Log.e(TAG, "MUSIC ISPLAY requestAudioFocus AFTER END CALL");
//						mBluetoothMusicModel.requestAudioFocus();
//					}
//				}
			}
		}
	}
	
	IBTMusicListener musicListener = new IBTMusicListener.Stub() {
		
		@Override
		public void syncBtMusicInfo(BTMusicInfo arg0) throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onTrackProgress(long arg0, long arg1) throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onPlaybackStateChanged(int arg0) throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onModeChanged(int arg0, int arg1) throws RemoteException {
			// TODO Auto-generated method stub
			
		}
	};
	
	IBTMusicManager musicManager =new  IBTMusicManager.Stub() {
		
		@Override
		public void unregisterBTMusicListener(IBTMusicListener arg0)
				throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void show() throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void setShuffleMode(int arg0) throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void setRepeatMode(int arg0) throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void registerBTMusicListener(IBTMusicListener arg0)
				throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void prev() throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void popUpCurrentMode() throws RemoteException {
			
		}
		
		@Override
		public void playByName(String arg0) throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void play() throws RemoteException {
			
		}
		
		@Override
		public void pause() throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onHmiChanged(int arg0, boolean arg1) throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void next() throws RemoteException {
			
		}
		
		@Override
		public boolean isConnected() throws RemoteException {
			
			return false;
		}
		
		@Override
		public void hide() throws RemoteException {
			
		}
		
		@Override
		public String getTrackName() throws RemoteException {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public int getShuffleMode() throws RemoteException {
			// TODO Auto-generated method stub
			return 0;
		}
		
		@Override
		public int getRepeatMode() throws RemoteException {
			// TODO Auto-generated method stub
			return 0;
		}
		
		@Override
		public Bitmap getArtwork() throws RemoteException {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public String getArtistName() throws RemoteException {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public String getAlbumName() throws RemoteException {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public int forward() throws RemoteException {
			// TODO Auto-generated method stub
			return 0;
		}
		
		@Override
		public int backward() throws RemoteException {
			// TODO Auto-generated method stub
			return 0;
		}
	};

}

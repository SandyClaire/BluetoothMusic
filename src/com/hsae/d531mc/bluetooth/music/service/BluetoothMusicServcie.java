package com.hsae.d531mc.bluetooth.music.service;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.anwsdk.service.AudioControl;
import com.anwsdk.service.MangerConstant;
import com.hsae.autosdk.os.Soc;
import com.hsae.autosdk.os.Soc.SocListener;
import com.hsae.autosdk.os.SocConst.UsbDevices;
import com.hsae.autosdk.settings.AutoSettings;
import com.hsae.autosdk.settings.AutoSettings.DisplayListener;
import com.hsae.autosdk.source.Source;
import com.hsae.autosdk.source.SourceConst.App;
import com.hsae.autosdk.util.LogUtil;
import com.hsae.autosdk.vehicle.VehicleConst.Ill.DayNight;
import com.hsae.d531mc.bluetooth.music.entry.MusicBean;
import com.hsae.d531mc.bluetooth.music.util.Util;

/**
 * 
 * @author wangda
 * 
 */
public class BluetoothMusicServcie extends Service implements
		BluetoothAllCallback {

	private static final String TAG = "BluetoothMusicServcie";
	public static final String ACTION_ACC_STATE = "com.hsae.auto.ACTION_ACC_STATE";
	public static final String EXTRA_ACC_STATE = "com.hsae.auto.EXTRA_ACC_STATE";

	private BluetoothMusicModel mBluetoothMusicModel;
	private Context mContext;
	private String mTitle = "", mTotalTIme = "", mAlbum = "", mAtrist = "";
	private String mLastTitle = "", mLastAlbum = "", mLastAtrist = "";
	private int mLastPlayStatus = -1;
	private BTMusicManager mBTMmanager;
	private String mTimePosition = "-1";
	private Soc mSoc;
	// power 状态监听
	private PowerListener mPowerListener = new PowerListener();
	private AutoSettings mAutoSettings;
	private boolean HfpStatus = false;
	private boolean autoPlay = true;
	private AccBroadcastReceiver mReceiver = null;
			
	private static final int BLUETOOTH_MUSIC_CONNECT_STATUS_CHANGE = 1;
	private static final int BLUETOOTH_MUSIC_CONNECT_PLAY = 2;
	/**
	 * 背景监听
	 */

	private Handler mHandler = new Handler(Looper.getMainLooper(),
			new Handler.Callback() {

				@Override
				public boolean handleMessage(Message msg) {

					switch (msg.what) {
					case BLUETOOTH_MUSIC_CONNECT_STATUS_CHANGE:
						if (mBluetoothMusicModel.a2dpStatus == 1
								&& mBluetoothMusicModel.avrcpStatus == 1) {
							playMusic();
							notifyAutoCoreConnectStatus(true);
						} else if (mBluetoothMusicModel.avrcpStatus == 0
								&& mBluetoothMusicModel.a2dpStatus == 0
								&& mBluetoothMusicModel.hfpStatus == 0) {
							resetBtState();
							notifyAutoCoreConnectStatus(false);
						} else {
							resetBtState();
						}
						break;
					case BLUETOOTH_MUSIC_CONNECT_PLAY:
						try {
							if(autoPlay){
								mBluetoothMusicModel.AVRCPControl(AudioControl.CONTROL_PLAY);
								mBluetoothMusicModel.getPlayStatus();
								mBluetoothMusicModel.isPlay = true;
								mBluetoothMusicModel.updatePlayStatus(mBluetoothMusicModel.isPlay);
							}
						
						} catch (RemoteException e) {
						}
						break;
					}
					return false;
				}
			});

	/***
	 * 将蓝牙音乐初始化
	 */
	private void resetBtState() {
		mBluetoothMusicModel.setTimingEnd();
		mTitle = "";
		mAtrist = "";
		mAlbum = "";

		mLastTitle = "";
		mLastAtrist = "";
		mLastAlbum = "";
		mLastPlayStatus = -1;

		mBluetoothMusicModel.isPlay = false;
		mBluetoothMusicModel.removeAutoPlay();
	}

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
		mSoc = new Soc();
		mAutoSettings = AutoSettings.getInstance();
		try {
			mAutoSettings.registerDisplayCallback(mPowerListener);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		registAccBroadcast();
		mBluetoothMusicModel.setBluetoothAllCallback(this);
		mBTMmanager = BTMusicManager.getInstance(getApplicationContext());
		LogUtil.i(TAG, "---------- service oncreat ------------");
		mSoc.registerListener(mSocListener);
		super.onCreate();
	}
	
	private void registAccBroadcast(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_ACC_STATE);
		
		mReceiver = new AccBroadcastReceiver();
		mContext.registerReceiver(mReceiver, filter);
	}
	
	private class AccBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
		
			Log.i(TAG, "action = " + intent.getAction());
			if(intent.getAction().equals(ACTION_ACC_STATE)){
				//TODO .. 记忆播放状态
				boolean accStatus = intent.getExtras().getBoolean(EXTRA_ACC_STATE);
				Log.i(TAG, "accStatus = " + accStatus);
				if (accStatus) {
					
				}else {
					if (mBluetoothMusicModel !=null) {
						autoPlay = mBluetoothMusicModel.isAccPlay;
					}
				}
			}
		}
		
	}

	@Override
	public void onDestroy() {
		try {
			mAutoSettings.unregisterDisplayCallback(mPowerListener);
			mSoc.unregisterListener(mSocListener);
		} catch (RemoteException e) {
		}
		
		mContext.unregisterReceiver(mReceiver);

		LogUtil.i(TAG, "---------- service onDestroy ------------");
		super.onDestroy();
	}

	/**
	 * 连接成功后播放音乐
	 * 
	 * @param status
	 */
	private void playMusic() {
		Source source = new Source();
		if (source.getCurrentSource() == App.BT_MUSIC) {
			LogUtil.i(TAG, "btmusic is connected playMusic  success");
			mHandler.sendEmptyMessageDelayed(BLUETOOTH_MUSIC_CONNECT_PLAY, 1000);
		} else {
			try {
				if (mHandler.hasMessages(BLUETOOTH_MUSIC_CONNECT_PLAY)) {
					mHandler.removeMessages(BLUETOOTH_MUSIC_CONNECT_PLAY);
				}
				LogUtil.i(TAG,"audioSetStreamMode: btmusic is connected playMusic fail");
				mBluetoothMusicModel.AVRCPControl(AudioControl.CONTROL_PAUSE);
				mBluetoothMusicModel.isPlay = false;
				mBluetoothMusicModel
						.updatePlayStatus(mBluetoothMusicModel.isPlay);
				mBluetoothMusicModel.getPlayStatus();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 通知中间件蓝牙连接装填
	 * 
	 * @param connectStatus
	 */
	private void notifyAutoCoreConnectStatus(boolean conn) {
		Source mSource = new Source();
		if (conn) {
			mSource.notifyBtState(true);
		} else {
			mSource.notifyBtState(false);
		}
	}

	class BitmapWorkerTask extends AsyncTask<Bundle, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(Bundle... params) {
			byte[] in = params[0].getByteArray(Util.VALUE);
			return BitmapFactory.decodeByteArray(in, 0, in.length);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			LogUtil.i(TAG, "onPostExecute --- result = " + result);
			mBluetoothMusicModel.deleteWallpaperCache();
			mBluetoothMusicModel.addWallPaperToCache(result);

		}
	}

	/**
	 * 初始化或更新背景
	 */
	@SuppressLint("NewApi")
	public void initBackground() {
		LogUtil.i(TAG, "initBackground");
		Bundle bd = getContentResolver().call(Util.WALL_CONTENT_URI,
				Util.METHOD_GET_VALUE_WALL, Util.WALLPAPER_SET, null);
		if (bd != null) {
			BitmapWorkerTask mTask = new BitmapWorkerTask();
			mTask.execute(bd);
		}
	}

	private static final int USB_CONNECTED_CARLIFE = 1;
	private static final int USB_CONNECTED_CARPLAY = 2;
	private static final int USB_CONNECTED_IPOD = 3;
	private static final int USB_CONNECTED_UNKNOW = 0;
	private int usbType = 0;

	/**
	 * USB是否插上
	 */
	private boolean isUSBOn = false;

	/**
	 * USB连接后断开蓝牙连接
	 * 
	 * @param type
	 */
	private void disconnBTbyUsbConnectStatus(int type) {

	}

	private MusicBean getMusicBean() {
		return new MusicBean(mLastTitle, mLastAtrist, mLastAlbum, mTotalTIme,
				mBluetoothMusicModel.isPlay ? 1 : 0,
				mBluetoothMusicModel.isAudioFocused);
	}

	/**
	 * USB 断开操作
	 * 
	 * @param type
	 */
	private void handleUsbDisconnectAction(int type) {
		switch (type) {
		case USB_CONNECTED_CARLIFE:
			mBluetoothMusicModel.updateCLConnectStatus();
			LogUtil.i(TAG, "USB_DISCONNECTED_CARLIFE");
			break;
		case USB_CONNECTED_CARPLAY:
			// mBluetoothMusicModel.updateCPConnectStatus();
			LogUtil.i(TAG, "USB_DISCONNECTED_CARPLAY");
			break;
		case USB_CONNECTED_IPOD:
			LogUtil.i(TAG, "USB_DISCONNECTED_IPOD");
			mBluetoothMusicModel.onUsbDisConnect();
			break;
		default:
			LogUtil.i(TAG, "onUsbDisConnect");
			mBluetoothMusicModel.onUsbDisConnect();
			break;
		}
	}

	/**
	 * usb 连接断开消息
	 */
	@SuppressLint("HandlerLeak")
	private Handler usbHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case USB_CONNECTED_CARLIFE:

				LogUtil.i(TAG, "USB_CONNECTED_CARLIFE");
				usbType = USB_CONNECTED_CARLIFE;
				mBluetoothMusicModel.isCarLifeConnected = true;
				mBluetoothMusicModel.isCarPlayConnected = false;
				// mBluetoothMusicModel.notifyAutroMusicInfo(null);
				mBluetoothMusicModel.updateCLConnectStatus();
				break;
			case USB_CONNECTED_CARPLAY:

				LogUtil.i(TAG, "USB_CONNECTED_CARPLAY");
				usbType = USB_CONNECTED_CARPLAY;
				mBluetoothMusicModel.isCarPlayConnected = true;
				mBluetoothMusicModel.isCarLifeConnected = false;
				// mBluetoothMusicModel.updateCPConnectStatus();
				break;
			case USB_CONNECTED_IPOD:

				LogUtil.i(TAG, "USB_CONNECTED_IPOD");
				usbType = USB_CONNECTED_IPOD;
				mBluetoothMusicModel.isCarLifeConnected = false;
				mBluetoothMusicModel.isCarPlayConnected = false;

				break;
			case USB_CONNECTED_UNKNOW:
				mBluetoothMusicModel.isCarLifeConnected = false;
				mBluetoothMusicModel.isCarPlayConnected = false;
				handleUsbDisconnectAction(usbType);
				usbType = USB_CONNECTED_UNKNOW;

				break;

			default:
				break;
			}
			disconnBTbyUsbConnectStatus(usbType);
		}
	};

	/**
	 * 监听usb连接
	 */
	public SocListener mSocListener = new SocListener() {

		@Override
		public void onUsbDeviceChanged(int index) {
			UsbDevices usbDevices = UsbDevices.findByIndex(index);
			Log.i(TAG, "usbDevices == " + usbDevices);

			Message msg = Message.obtain();
			if (usbDevices.equals(UsbDevices.CARLIFE)) {
				msg.what = USB_CONNECTED_CARLIFE;
				usbHandler.sendMessage(msg);
				isUSBOn = true;
			} else if (usbDevices.equals(UsbDevices.CARPLAY)) {
				msg.what = USB_CONNECTED_CARPLAY;
				usbHandler.sendMessage(msg);
				isUSBOn = true;
			} else if (usbDevices.equals(UsbDevices.IPOD)) {
				msg.what = USB_CONNECTED_IPOD;
				usbHandler.sendMessage(msg);
				isUSBOn = true;
			} else if (usbDevices.equals(UsbDevices.UNKNOW)) {
				msg.what = USB_CONNECTED_UNKNOW;
				usbHandler.sendMessage(msg);
				isUSBOn = false;
			}
		}

		@Override
		public void onProgressChanged(int arg0) {

		}

		@Override
		public void onMainAudioChanged(int arg0) {

		}

		@Override
		public void onShutDownNotify() {
			
		}
	};

	/**
	 * power 按键监听
	 * 
	 * @author wangda
	 * 
	 */
	private class PowerListener implements DisplayListener {

		@Override
		public void onBrightnessResponse(int arg0) {
		}

		@Override
		public void onContrastResponse(int arg0) {
		}

		@Override
		public void onDayNightAutoStateResponse(DayNight arg0) {
		}

		@Override
		public void onScreenStateResponse(boolean power) {
			
			if (mBluetoothMusicModel == null) {
				LogUtil.i(TAG,
						"onScreenStateResponse mBluetoothMusicModel is null");
				return;
			}
			mBluetoothMusicModel.powerStatus = power;
			if (power) {
				Source source = new Source();
				if (source.getCurrentSource() == App.BT_MUSIC) {
					LogUtil.i(TAG,
							"notifyAutroMusicInfo --- onScreenStateResponse");
					if (mBluetoothMusicModel.a2dpStatus == 1) {
						mBluetoothMusicModel.notifyAutroMusicInfo(
								getMusicBean(), false, true);
					} else {
						LogUtil.i(TAG, "notifyAutoCoreWarning cccccccccc");
						mBluetoothMusicModel.notifyAutoCoreWarning();
					}
				}
			}else {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.addCategory(Intent.CATEGORY_HOME);
				startActivity(intent);
			}
		}
	}

	@Override
	public void onPlayStatusChanged(int state) {
		Log.i(TAG, "PlayStatusChanged,status = " + state);

		int nPlayStatus = state;
		LogUtil.i(TAG, "A2DP_PLAYSTATUS -- nPlayStatus = " + nPlayStatus);

		if (nPlayStatus != mLastPlayStatus) {
			mLastPlayStatus = nPlayStatus;
			try {
				int n = mBTMmanager.mListeners.beginBroadcast();
				for (int i = 0; i < n; i++) {
					mBTMmanager.mListeners.getBroadcastItem(i)
							.onPlaybackStateChanged(nPlayStatus == 1 ? 0 : 1);
				}
				mBTMmanager.mListeners.finishBroadcast();
			} catch (Exception e) {
				LogUtil.i(TAG, " ---- Exception = " + e.toString(), e);
			}
		}
		switch (nPlayStatus) {
		case AudioControl.STREAM_STATUS_SUSPEND:
			mBluetoothMusicModel.isPausing = false;
			mBluetoothMusicModel.isPlay = false;
			mBluetoothMusicModel.isAccPlay = false;
			mBluetoothMusicModel.setTimingEnd();
			break;
		case AudioControl.STREAM_STATUS_STREAMING:
			mBluetoothMusicModel.setTimingBegins();
			mBluetoothMusicModel.isPlaying = false;
			mBluetoothMusicModel.isPlay = true;
			mBluetoothMusicModel.isAccPlay = true;
			autoPlay = true;
			mBluetoothMusicModel.setStreamMute();
			break;
		}

		if (nPlayStatus == AudioControl.PLAYSTATUS_PLAYING) {
			LogUtil.i(TAG, "PlayTime -- mPosition = " + mTimePosition);
			if (!mTimePosition.equals("-1")) {
				mBluetoothMusicModel.updateCurrentPlayTime(mTimePosition,
						mBluetoothMusicModel.isPlay);
			}
		} else if (nPlayStatus == AudioControl.PLAYSTATUS_FWD_SEEK
				|| nPlayStatus == AudioControl.PLAYSTATUS_REV_SEEK) {
			if (mBluetoothMusicModel.isPlay) {
				try {
					mBluetoothMusicModel
							.AVRCPControl(AudioControl.CONTROL_PLAY);
				} catch (RemoteException e) {
				}
			}
		}
		if (mBluetoothMusicModel.streamStatus != nPlayStatus) {
			mBluetoothMusicModel.streamStatus = nPlayStatus;
			LogUtil.i(TAG, "notifyAutroMusicInfo AAAAAA");
			mBluetoothMusicModel.notifyAutroMusicInfo(getMusicBean(), true,
					false);
		}

		mBluetoothMusicModel.updatePlayStatus(mBluetoothMusicModel.isPlay);
		LogUtil.i(TAG, "-- nPlayStatus = " + nPlayStatus + "mTitle = " + mTitle
				+ ",mAtrist = " + mAtrist + ",mTotalTIme = " + mTotalTIme
				+ " ,mAlbum = " + mAlbum);

	}

	@Override
	public void onPositionChanged(String position) {
		Log.i(TAG, "onPositionChanged,position = " + position);

		mTimePosition = position;
		LogUtil.i(TAG, "A2DP_PLAYBACKPOS -- strPos = " + mTimePosition);
		if (!mTimePosition.equals("-1")) {
			mBluetoothMusicModel.updateCurrentPlayTime(mTimePosition,
					mBluetoothMusicModel.isPlay);
		}

	}

	@Override
	public void onID3Changed(String title, String album, String artist,
			String totalTime) {
		Log.i(TAG, "ID3Changed,title = " + title + ",album = " + album
				+ ",artist = " + artist + "totalTime = " + totalTime);

		mTitle = title;
		mBluetoothMusicModel.mTitel = title;
		mBTMmanager.onTitleChange(mTitle);

		mAtrist = artist;
		mAlbum = album;
		mTotalTIme = totalTime;

		mLastAlbum = mAlbum;
		mLastAtrist = mAtrist;
		mLastTitle = mTitle;

		MusicBean bean = getMusicBean();
		LogUtil.i(TAG, "notifyAut : updateCurrentMusicInfo"
				+ mBluetoothMusicModel.mTitel);
		mBluetoothMusicModel.updateCurrentMusicInfo(bean);
		mBluetoothMusicModel.notifyAutroMusicInfo(bean);
		mBluetoothMusicModel.mTitel = mTitle;

	}

	@Override
	public void onConnectStateChanged(int profile, int state, int reason) {
		Log.i(TAG, "onConnectStateChanged,profile = " + profile + ",state = "
				+ state + ",reason = " + reason);

		int nProfile = profile;
		if (nProfile == MangerConstant.PROFILE_HF_CHANNEL) {
			mBluetoothMusicModel.hfpStatus = state;

		} else if (nProfile == MangerConstant.PROFILE_AUDIO_STREAM_CHANNEL) {

			mBluetoothMusicModel.a2dpStatus = state;

			if (mBluetoothMusicModel.a2dpStatus == 0) {
				LogUtil.i(TAG, "notifyAutoCoreWarning AAAAAAA");
				mBluetoothMusicModel.notifyAutoCoreWarning();
			}

			mBluetoothMusicModel.syncBtStatus(mBluetoothMusicModel.a2dpStatus);

			mBluetoothMusicModel
					.updateMsgByConnectStatusChange(mBluetoothMusicModel.a2dpStatus);

			mHandler.sendEmptyMessage(BLUETOOTH_MUSIC_CONNECT_STATUS_CHANGE);

		} else if (nProfile == MangerConstant.PROFILE_AUDIO_CONTROL_CHANNEL) {
			mBluetoothMusicModel.avrcpStatus = state;
			LogUtil.i(TAG, "PROFILE_AUDIO_CONTROL_CHANNEL --- avrcpStatus = "
					+ mBluetoothMusicModel.avrcpStatus);
			mHandler.sendEmptyMessage(BLUETOOTH_MUSIC_CONNECT_STATUS_CHANGE);
		}

	}

	@Override
	public void onPowerStateChanged(int state) {
		Log.i(TAG, "onPowerStateChanged,status = " + state);

		if (state == 1) {

			mBluetoothMusicModel
					.updateBTEnalbStatus(MangerConstant.BTPOWER_STATUS_ON);
		} else if (state == 0) {

			mBluetoothMusicModel
					.updateBTEnalbStatus(MangerConstant.BTPOWER_STATUS_OFF);
			mBluetoothMusicModel.isPlay = false;
		}

	}
}

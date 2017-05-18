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
import com.hsae.autosdk.bt.music.BTMusicInfo;
import com.hsae.autosdk.ipod.IPodProxy;
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
import com.hsae.d531mc.bluetooth.music.util.MusicActionDefine;
import com.hsae.d531mc.bluetooth.music.util.Util;

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
	private String mTitle = "";
	private String mAtrist = "";
	private String mAlbum = "";
	private String mTotalTIme = "";
	private BTMusicManager mBTMmanager;
	private String mTimePosition = "-1";
	private Soc mSoc;
	// power 状态监听
	private PowerListener mPowerListener = new PowerListener();
	private AutoSettings mAutoSettings;

	private static final int BLUETOOTH_MUSIC_CONNECT_STATUS_CHANGE = 1;
	/**
	 * 背景监听
	 */
	// private WallContentObserver mObserver;

	private Handler mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {

			switch (msg.what) {
			case BLUETOOTH_MUSIC_CONNECT_STATUS_CHANGE:
				if (mBluetoothMusicModel.a2dpStatus == 1 && mBluetoothMusicModel.avrcpStatus == 1) {
					playMusic();
					notifyAutoCoreConnectStatus(true);
				} else if (mBluetoothMusicModel.avrcpStatus == 0 && mBluetoothMusicModel.a2dpStatus == 0
						&& mBluetoothMusicModel.hfpStatus == 0) {
					if (isTicker) {
						setTimingEnd();
					}
					mTitle = "";
					mAtrist = "";
					mAlbum = "";
					notifyAutoCoreConnectStatus(false);
					mBluetoothMusicModel.isPlay = false;
				} else {
					if (isTicker) {
						setTimingEnd();
					}
					mTitle = "";
					mAtrist = "";
					mAlbum = "";
					mBluetoothMusicModel.isPlay = false;
				}
				break;
			default:
				break;
			}
			return false;
		}

	});

	private Handler stepTimeHandler = new Handler();
	private Ticker mTicker;

	/**
	 * 计时器是否正在工作
	 */
	private boolean isTicker = false;

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
		mSoc = new Soc();
		mAutoSettings = AutoSettings.getInstance();
		try {
			mAutoSettings.registerDisplayCallback(mPowerListener);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		registBroadcast();
		mBTMmanager = BTMusicManager.getInstance(getApplicationContext());
		LogUtil.i(TAG, "---------- service oncreat ------------");
		mBluetoothMusicModel.setMusicStreamMute();
		// initBackground();
		// registerContentObserver();
		mSoc.registerListener(mSocListener);
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
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		mContext.registerReceiver(mReceiver, filter);
	}

	@Override
	public void onDestroy() {
		mContext.unregisterReceiver(mReceiver);
		// unRegisterContentObserver();
		mBluetoothMusicModel.releaseModel();
		LogUtil.i(TAG, "---------- service onDestroy ------------");
		super.onDestroy();
	}

	private class BTBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent intent) {

			String strAction = intent.getAction();
			LogUtil.i(TAG, "onReceive = " + strAction);
			Bundle mBundle = intent.getExtras();
			/* 蓝牙开关状态 */
			if (strAction.equals(MangerConstant.MSG_ACTION_POWER_STATUS)) {
				if (mBundle != null) {
					boolean bPowerON = mBundle.getBoolean("Value");
					LogUtil.i(TAG, "MSG_ACTION_POWER_STATUS ----- bPowerON = " + bPowerON);
					if (bPowerON) {
						mBluetoothMusicModel.updateBTEnalbStatus(MangerConstant.BTPOWER_STATUS_ON);
					} else {
						mBluetoothMusicModel.updateBTEnalbStatus(MangerConstant.BTPOWER_STATUS_OFF);
						mBluetoothMusicModel.isPlay = false;
					}
				}
				/* 蓝牙连接状态 */
			} else if (strAction.equals(MangerConstant.MSG_ACTION_CONNECT_STATUS)) {
				if (mBundle != null) {
					int nProfile = mBundle.getInt("Profile");
					if (nProfile == MangerConstant.PROFILE_HF_CHANNEL) {
						mBluetoothMusicModel.hfpStatus = mBundle.getInt("Value");
						mBluetoothMusicModel.updateHFPConnectStatus(mBluetoothMusicModel.hfpStatus);
						LogUtil.i(TAG, "PROFILE_HF_CHANNEL hfpStatus =　" + mBluetoothMusicModel.hfpStatus);
						mHandler.sendEmptyMessage(BLUETOOTH_MUSIC_CONNECT_STATUS_CHANGE);

					} else if (nProfile == MangerConstant.PROFILE_AUDIO_STREAM_CHANNEL) {
						mBluetoothMusicModel.a2dpStatus = mBundle.getInt("Value");

						if (mBluetoothMusicModel.isDisByIpod) {
							mBluetoothMusicModel.isDisByIpod = false;
							IPodProxy.getInstance().notifyA2dpConnected(
									mBluetoothMusicModel.a2dpStatus == MangerConstant.Anw_SUCCESS);
						}
						LogUtil.i(TAG, "PROFILE_AUDIO_STREAM_CHANNEL --- a2dpStatus = "
								+ mBluetoothMusicModel.a2dpStatus);

						if (mBluetoothMusicModel.isCarlifeConnected()) {
							// 通知界面不显示
//							mBluetoothMusicModel.updateCLConnectStatus();
							return;
						}

						mBluetoothMusicModel.updateMsgByConnectStatusChange(mBluetoothMusicModel.a2dpStatus);
						// getPlayStatus(a2dpStatus);
						mHandler.sendEmptyMessage(BLUETOOTH_MUSIC_CONNECT_STATUS_CHANGE);
					} else if (nProfile == MangerConstant.PROFILE_AUDIO_CONTROL_CHANNEL) {
						mBluetoothMusicModel.avrcpStatus = mBundle.getInt("Value");
						LogUtil.i(TAG, "PROFILE_AUDIO_CONTROL_CHANNEL --- avrcpStatus = "
								+ mBluetoothMusicModel.avrcpStatus);
						mHandler.sendEmptyMessage(BLUETOOTH_MUSIC_CONNECT_STATUS_CHANGE);
					}
				}
				/* 蓝牙音乐数据支持状态 */
			} else if (strAction.equals(MangerConstant.MSG_ACTION_A2DP_FEATURE_SUPPORT)) {
				if (mBundle != null) {
					boolean bSupport_Metadata = mBundle.getBoolean("MetaData");
					boolean bSupport_PlayStatus = mBundle.getBoolean("PlayStatus");

					LogUtil.i(TAG, "-- bSupport_Metadata = " + bSupport_Metadata + "-- bSupport_PlayStatus = "
							+ bSupport_PlayStatus);
				}
				/* 蓝牙音乐数据信息 */
			} else if (strAction.equals(MangerConstant.MSG_ACTION_A2DP_METADATA)) {
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

						MusicBean bean = new MusicBean(mTitle, mAtrist, mAlbum, mTotalTIme);
						mBluetoothMusicModel.updateCurrentMusicInfo(bean);

						BTMusicInfo info = new BTMusicInfo(bean.getTitle(), bean.getAtrist(), bean.getAlbum(), null);
						mBluetoothMusicModel.notifyAutroMusicInfo(info);

						LogUtil.i(TAG, "-- nPlayStatus = " + nPlayStatus + "mTitle = " + mTitle + ",mAtrist = "
								+ mAtrist + ",mTotalTIme = " + mTotalTIme + " ,mAlbum = " + mAlbum);
					}
				}
				/* 蓝牙音乐播放状态 */
			} else if (strAction.equals(MangerConstant.MSG_ACTION_A2DP_PLAYSTATUS)) {
				if (mBundle != null) {
					int nPlayStatus = mBundle.getInt("PlayStatus");
					LogUtil.i(TAG, "A2DP_PLAYSTATUS -- nPlayStatus = " + nPlayStatus);
					if (nPlayStatus == AudioControl.PLAYSTATUS_PLAYING) {
						mBluetoothMusicModel.isPlay = true;
						if (!mTimePosition.equals("-1")) {
							mBluetoothMusicModel.updateCurrentPlayTime(mTimePosition, mBluetoothMusicModel.isPlay);
							LogUtil.i(TAG, "PlayTime -- mPosition = " + mTimePosition);
						}
						if (!isTicker) {
							setTimingBegins();
						}
					} else if (nPlayStatus == AudioControl.PLAYSTATUS_PAUSED
							|| nPlayStatus == AudioControl.PLAYSTATUS_STOPPED) {
						setTimingEnd();
						mBluetoothMusicModel.isPlay = false;
					} else if (nPlayStatus == AudioControl.PLAYSTATUS_FWD_SEEK
							|| nPlayStatus == AudioControl.PLAYSTATUS_REV_SEEK) {
						if (mBluetoothMusicModel.isPlay) {
							try {
								mBluetoothMusicModel.AVRCPControl(AudioControl.CONTROL_PLAY);
							} catch (RemoteException e) {
							}
						}
					}
					mBluetoothMusicModel.updatePlayStatus(mBluetoothMusicModel.isPlay);
					mBluetoothMusicModel.setMusicStreamMute();

				}
				/* 蓝牙音乐播放当前时间信息 */
			} else if (strAction.equals(MangerConstant.MSG_ACTION_A2DP_PLAYBACKPOS)) {
				if (mBundle != null) {
					mTimePosition = mBundle.getString("Position");
					if (!mTimePosition.equals("-1")) {
						mBluetoothMusicModel.updateCurrentPlayTime(mTimePosition, mBluetoothMusicModel.isPlay);
						LogUtil.i(TAG, "A2DP_PLAYBACKPOS -- strPos = " + mTimePosition);
					}
				}
				/* 蓝牙音乐播放音乐流 */
			} else if (strAction.equals(MangerConstant.MSG_ACTION_A2DP_STREAMSTATUS)) {
				if (mBundle != null) {
					int nPlayStatus = mBundle.getInt("StreamStatus");
					LogUtil.i(TAG, "A2DP_STREAMSTATUS -- nPlayStatus = " + nPlayStatus);
					switch (nPlayStatus) {
					case AudioControl.STREAM_STATUS_SUSPEND:
						break;
					case AudioControl.STREAM_STATUS_STREAMING:
						mBluetoothMusicModel.isPlay = true;
						mBluetoothMusicModel.updatePlayStatus(true);
						break;
					}
				}
				/* 蓝牙音乐播放模式变化 */
			} else if (strAction.equals(MangerConstant.MSG_ACTION_AVRCP_PLAYERSETTING_CHANGED_EVENT)) {
				if (mBundle != null) {
					int nAttrID = mBundle.getInt("AttributeID");
					int nAttrValue = mBundle.getInt("Value");
					LogUtil.i(TAG, "current model nAttrID = " + nAttrID + " --- nAttrValue = " + nAttrValue);

					mBluetoothMusicModel.updatePlayerModelSetting(nAttrID, nAttrValue);
				}
				/* 蓝牙音乐播放模式数据 */
			} else if (strAction.equals(MangerConstant.MSG_ACTION_AVRCP_PLAYERSETTING_SUPPORTED_EVENT)) {
				if (mBundle != null) {
					int nAttrID = mBundle.getInt("AttributeID");
					ArrayList<Integer> AllowList = mBundle.getIntegerArrayList("Allowed");
					switch (nAttrID) {
					case AudioControl.PLAYER_ATTRIBUTE_REPEAT:// 2
						mBluetoothMusicModel.updateRepeatModel(AllowList);
						LogUtil.i(TAG, "REPEAT AllowList size = " + AllowList.size());

						break;
					case AudioControl.PLAYER_ATTRIBUTE_SHUFFLE:// 3
						mBluetoothMusicModel.updateShuffleModel(AllowList);
						LogUtil.i(TAG, "SHUFFLE AllowList size = " + AllowList.size());
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
				LogUtil.i("BluetoothMusicModel", " autoConnA2dp");
			}
		}
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
			try {
				mBluetoothMusicModel.AVRCPControl(AudioControl.CONTROL_PLAY);
				mBluetoothMusicModel.getPlayStatus();
				mBluetoothMusicModel.isPlay = true;
				mBluetoothMusicModel.updatePlayStatus(mBluetoothMusicModel.isPlay);
			} catch (RemoteException e) {
			}
		} else {
			try {
				LogUtil.i(TAG, "audioSetStreamMode: btmusic is connected playMusic fail");
				mBluetoothMusicModel.AVRCPControl(AudioControl.CONTROL_PAUSE);
				// mBluetoothMusicModel.audioSetStreamMode(MangerConstant.AUDIO_STREAM_MODE_DISABLE);
				mBluetoothMusicModel.isPlay = false;
				mBluetoothMusicModel.updatePlayStatus(mBluetoothMusicModel.isPlay);
				mBluetoothMusicModel.getPlayStatus();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 如果A2DP单独断开情况下，自动连接蓝牙音乐；
	 */
	private void autoConnA2dp() {
		Soc soc = new Soc();
		UsbDevices usbDevices = soc.getCurrentDevice();
		LogUtil.i("BluetoothMusicModel", " autoConnA2dp usbDevices = " + usbDevices.toString());
		// if (usbDevices.equals(UsbDevices.IPOD)
		// ||usbDevices.equals(UsbDevices.CARLIFE) ) {
		try {
			LogUtil.i("BluetoothMusicModel", " autoConnA2dp MAC Address = " + getConnectedDevice());
			// if (mBluetoothMusicModel.isCurrentInquiring()) {
			// mBluetoothMusicModel.inquiryBtStop();
			// }
			mBluetoothMusicModel.a2dpConnect(getConnectedDevice());
		} catch (RemoteException e) {
		}
		// }
	}

	/**
	 * 获取当连接设备地址
	 * 
	 * @return
	 */
	private String getConnectedDevice() {
		String[] strAddress = new String[1];
		String[] strName = new String[1];
		try {
			mBluetoothMusicModel.getConnectedDeviceInfo(MangerConstant.PROFILE_HF_CHANNEL, strAddress, strName, 0);
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
	private void notifyAutoCoreConnectStatus(boolean conn) {
		Source mSource = new Source();
		if (conn) {
			mSource.notifyBtState(true);
		} else {
			mSource.notifyBtState(false);
		}
	}

	/**
	 * set timer start
	 */
	public void setTimingBegins() {
		if (mTicker == null) {
			mTicker = new Ticker();
			stepTimeHandler.post(mTicker);
			isTicker = true;
			LogUtil.i(TAG, "setTimingBegins");
		}
	}

	/**
	 * set timer end
	 */
	public void setTimingEnd() {
		if (stepTimeHandler != null) {
			stepTimeHandler.removeCallbacks(mTicker);
			mTicker = null;
			isTicker = false;
			LogUtil.i(TAG, "setTimingEnd");
		}
	}

	/**
	 * ticker
	 * 
	 * @author wangda
	 *
	 */
	private class Ticker implements Runnable {

		@Override
		public void run() {
			try {
				mBluetoothMusicModel.getPlayStatus();
				stepTimeHandler.postDelayed(this, 1000);
			} catch (RemoteException e) {
			}
		}
	}

	// /**
	// * 注册背景数据库监听
	// */
	// public void registerContentObserver() {
	// Uri uri = Uri.parse(Util.WALL_CONTENT_URI + "/" + Util.WALLPAPER_SET);
	// if (mObserver == null) {
	// mObserver = new WallContentObserver(new Handler());
	// }
	// getContentResolver().registerContentObserver(uri, false, mObserver);
	// }
	//
	// /**
	// * 注销背景数据库监听
	// */
	// public void unRegisterContentObserver() {
	// if (mObserver != null) {
	// getContentResolver().unregisterContentObserver(mObserver);
	// }
	// }
	//
	// class WallContentObserver extends ContentObserver {
	//
	// public WallContentObserver(Handler handler) {
	// super(handler);
	// // TODO Auto-generated constructor stub
	// }
	//
	// @Override
	// public void onChange(boolean selfChange) {
	// super.onChange(selfChange);
	// LogUtil.i(TAG, "wall paperchanged");
	// initBackground();
	// }
	// }

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
		Bundle bd = getContentResolver().call(Util.WALL_CONTENT_URI, Util.METHOD_GET_VALUE_WALL, Util.WALLPAPER_SET,
				null);
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

	/**
	 * USB 断开操作
	 * 
	 * @param type
	 */
	private void handleUsbDisconnectAction(int type) {
		switch (type) {
		case USB_CONNECTED_CARLIFE:
			mBluetoothMusicModel.updateCLConnectStatus();
			autoConnA2dp();
			LogUtil.i(TAG, "USB_DISCONNECTED_CARLIFE");
			break;
		case USB_CONNECTED_CARPLAY:
			mBluetoothMusicModel.updateCPConnectStatus();
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
				mBluetoothMusicModel.updateCLConnectStatus();

				break;
			case USB_CONNECTED_CARPLAY:

				LogUtil.i(TAG, "USB_CONNECTED_CARPLAY");
				usbType = USB_CONNECTED_CARPLAY;
				mBluetoothMusicModel.isCarPlayConnected = true;
				mBluetoothMusicModel.isCarLifeConnected = false;
				mBluetoothMusicModel.updateCPConnectStatus();
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
			// TODO Auto-generated method stub

		}

		@Override
		public void onContrastResponse(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onDayNightAutoStateResponse(DayNight arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onScreenStateResponse(boolean power) {
			if (power) {
				Source source = new Source();
				if (source.getCurrentSource() == App.BT_MUSIC) {
					LogUtil.i(TAG, "--- onScreenStateResponse AUDIO_STREAM_MODE_ENABLE");
					try {
						mBluetoothMusicModel.audioSetStreamMode(MangerConstant.AUDIO_STREAM_MODE_ENABLE);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			} else {
				LogUtil.i(TAG, "audioSetStreamMode --- onScreenStateResponse AUDIO_STREAM_MODE_DISABLE");
				try {
					if (mBluetoothMusicModel.getStreamMode() != MangerConstant.AUDIO_STREAM_MODE_DISABLE) {
						mBluetoothMusicModel.audioSetStreamMode(MangerConstant.AUDIO_STREAM_MODE_DISABLE);
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
	}

}

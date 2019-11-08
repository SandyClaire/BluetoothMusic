package com.hsae.d531mc.bluetooth.music.service;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.VelocityTracker.Estimator;

import com.anwsdk.service.AudioControl;
import com.anwsdk.service.MangerConstant;
import com.hsae.autosdk.bt.music.BTMusicInfo;
import com.hsae.autosdk.bt.phone.BtPhoneProxy;
import com.hsae.autosdk.ipod.IPodProxy;
import com.hsae.autosdk.os.Soc;
import com.hsae.autosdk.os.SocConst.UsbDevices;
import com.hsae.autosdk.settings.AutoSettings;
import com.hsae.autosdk.source.Source;
import com.hsae.autosdk.source.SourceConst.App;
import com.hsae.autosdk.util.LogUtil;
import com.hsae.bluetoothsdk.music.MusicCallback;
import com.hsae.bluetoothsdk.music.MusicProxy;
import com.hsae.bluetoothsdk.setting.BluetoothCallback;
import com.hsae.bluetoothsdk.setting.BluetoothProxy;
import com.hsae.d531mc.bluetooth.music.entry.MusicBean;
import com.hsae.d531mc.bluetooth.music.model.IMusicModel;
import com.hsae.d531mc.bluetooth.music.util.Util;

/**
 * 
 * @author wangda
 * 
 */
public class BluetoothMusicModel {

	private static final String TAG = "BluetoothMusicModel";

	private final Object lockOfBTMmanager = new Object();
	private static BluetoothMusicModel mInstance;
	private static Context mContext;
	private IMusicModel mIMusicModel;
	private BTMusicManager mBTMmanager;
	private static final int errorCode = -1;
	public String mTitel = "";
	public int hfpStatus = 0;
	public int a2dpStatus = 0;
	public int avrcpStatus = 0;
	public boolean isDisByIpod = false;
	// 从点击pause到收到回调的这段时间都算是pausing状态
	public boolean isPausing = false;
	// 从点击play到收到回调的这段时间都算是playing状态
	public boolean isPlaying = false;
	/***
	 * 判断是VR调用stop的接口暂停后音频焦点再次回到BT时不执行播放动作
	 */
	// public boolean pauseByVr = false;
	/***
	 * 判断是电话起来时音乐自动暂停、该暂停由手机端发起、DA不发起暂停
	 */
	BtPhoneProxy phoneProxy = BtPhoneProxy.getInstance();

	// 壁纸缓存
	private LruCache<String, Bitmap> mMemoryCache;

	// 获取应用程序最大可用内存
	int cacheSize = (int) Runtime.getRuntime().maxMemory() / 8;

	// 手动暂停标识
	public boolean isHandPuse = false;

	// carplay 是否连接
	public boolean isCarPlayConnected = false;

	// carlife 是否连接
	public boolean isCarLifeConnected = false;

	// 音乐是否播放
	public boolean isPlay = false;

	public final Source mSource = new Source();

	private AudioManager audioManager;
	
	public boolean isAccPlay = false;
	
	public boolean isActivityShow = false;
	
	//ACC状态
	public boolean accState = true;
	
	private MusicProxy mMusicProxy;
	private BluetoothProxy mBluetoothProxy;

	private BluetoothAllCallback mBluetoothAllCallback;
	
	public boolean isCanSync = true;
	
	public static BluetoothMusicModel getInstance(Context context) {
		mContext = context;
		if (null == mInstance) {
			mInstance = new BluetoothMusicModel();

		}
		return mInstance;
	}

	public BluetoothMusicModel() {
		audioManager = (AudioManager) mContext
				.getSystemService(Context.AUDIO_SERVICE); // STREAM_MUSIC

		Log.i(TAG, "BluetoothMusicModel init");

		mMusicProxy = MusicProxy.getInstance(mContext);
		mMusicProxy.registCallback(mMusicProxyCallback);
		
		mBluetoothProxy = BluetoothProxy.getInstance(mContext);
		mBluetoothProxy.registCallback(mBluetoothProxyCallback);
	}

	public MusicCallback mMusicProxyCallback = new MusicCallback() {

		@Override
		public void onPlayStatusChanged(int state) {
			super.onPlayStatusChanged(state);

			if (mBluetoothAllCallback != null) {
				mBluetoothAllCallback.onPlayStatusChanged(state);
			}

		}

		@Override
		public void onPositionChanged(String position) {
			super.onPositionChanged(position);

			if (mBluetoothAllCallback != null) {
				mBluetoothAllCallback.onPositionChanged(position);
			}
		}

		@Override
		public void onID3Changed(String title, String album, String artist,
				String totalTime) {
			super.onID3Changed(title, album, artist, totalTime);

			if (mBluetoothAllCallback != null) {
				mBluetoothAllCallback.onID3Changed(title, album, artist,
						totalTime);
			}
		}

	};

	public BluetoothCallback mBluetoothProxyCallback = new BluetoothCallback() {

		@Override	
		public void onConnectStateChanged(int profile, int state, int reason) {
			super.onConnectStateChanged(profile, state, reason);

			if (mBluetoothAllCallback != null) {
				mBluetoothAllCallback.onConnectStateChanged(profile, state,
						reason);
			}
		}

		@Override
		public void onPowerStateChanged(int state) {
			super.onPowerStateChanged(state);

			if (mBluetoothAllCallback != null) {
				mBluetoothAllCallback.onPowerStateChanged(state);
			}
		}

	};

	/**
	 * 得到当前蓝牙连接状态
	 */
	public int getBTPowerStatus() throws RemoteException {
		if (null == mBluetoothProxy) {
			return errorCode;
		}
		return mBluetoothProxy.getPowerStatus();
	}

	public int getConnectStatus(int nProfileType, int nIndex)
			throws RemoteException {
		if (null == mBluetoothProxy) {
			return errorCode;
		}
		return mBluetoothProxy.getConnectStatus(nProfileType);
	}

	/**
	 * 得到歌曲信息
	 */
	public String A2DPGetCurrentAttributes(int attributesID)
			throws RemoteException {
		if (null == mMusicProxy) {
			return "";
		}
		if (mMusicProxy.getId3() != null) {

			switch (attributesID) {
			case AudioControl.MEDIA_ATTR_MEDIA_TITLE:
				return mMusicProxy.getId3().getTitle();
			case AudioControl.MEDIA_ATTR_ARTIST_NAME:
				return mMusicProxy.getId3().getArtist();
			case AudioControl.MEDIA_ATTR_ALBUM_NAME:
				return mMusicProxy.getId3().getAlbum();
			case AudioControl.MEDIA_ATTR_PLAYING_TIME_IN_MS:
				return mMusicProxy.getId3().getTotalTime();
			}
		}

		return "";

	}

	public void getMusicInfo() throws RemoteException {
		if (null == mMusicProxy) {
			return;
		}
		Log.i(TAG, "getMusicInfo");
		
		mMusicProxy.getMusicInfo();
	}

	/**
	 * This function retrieves A2DP Meta data support capability of a Bluetooth
	 * mobile
	 * 
	 * @return TRUE means mobile device support A2DP Meta data. FALSE means
	 *         mobile device doesn��t support A2DP Meta data.
	 * @throws RemoteException
	 *             Remarks This function is valid after A2DP is connected.
	 */
	// 是否支持播放模式切换
	public boolean isA2DPSupportMetadata() throws RemoteException {

		return mMusicProxy.supportPlayMode();
	}

	public boolean isFastForward = false;
	public boolean isRewind = false;

	/**
	 * This function sends the AVRCP command to mobile phone for controlling the
	 * music playing.
	 * 
	 * @param op_code
	 *            [in] Please reference AVRCP_Operation_ID for detail command
	 *            information.
	 * @param nActFlag
	 *            [in] The ActFlag must be the value in following table.
	 *            AVRCP_CONTROL_ACT_PRESS 0 Means button is pushed
	 *            AVRCP_CONTROL_ACT_RELEASE 1 Means button is released.
	 * @return Returns Anw_SUCCESS on success or returns an error code on
	 *         failure.
	 * @throws RemoteException
	 */

	// 快进,快退
	public int AVRCPControlEx(int op_code, int nActFlag) throws RemoteException {
		if (null == mMusicProxy) {
			return errorCode;
		}
		Log.i(TAG, "op_code = " + op_code + " ,nActFlag = " + nActFlag);
		isOnFW = nActFlag == 0;

		if (op_code == AudioControl.CONTROL_FASTFORWARD) {

			if (nActFlag == 0) {
				mMusicProxy.fastForward(false);
				isFastForward = true;
			} else {
				mMusicProxy.fastForward(true);
				isFastForward = false;
			}
		} else if (op_code == AudioControl.CONTROL_REWIND) {
			if (nActFlag == 0) {
				mMusicProxy.fastbackward(false);
				isRewind = true;
			} else {
				mMusicProxy.fastbackward(true);
				isRewind = false;
			}
		}
		return 1;
	}

	// 得到静音模式
	public int getStreamMode() throws RemoteException {
		if (null == mMusicProxy) {
			return errorCode;
		}
		return mMusicProxy.getMuteStatus();
	}

	// 播放，暂停，上一曲，下一曲
	public int AVRCPControl(int op_code) throws RemoteException {
		if (null == mMusicProxy) {

			return errorCode;
		}

		LogUtil.i(TAG, "AVRCPControl : op_code= " + op_code + " , isPausing = "
				+ isPausing);
		if (op_code == AudioControl.CONTROL_PLAY) {
			if (!isPausing && isPlay) {
				LogUtil.i(TAG, "now is playing , filter out this op_code");
				return -998;
			}

			isHandPuse = false;
			if (a2dpStatus == 0) {
				LogUtil.i(TAG, "AVRCPControl : op_code= " + op_code
						+ ",but now disable to play");
				return -1;
			}
			isPlaying = true;
			setTimingBegins();
			if (!handler.hasMessages(MSG_AUTOPLAY)) {
				handler.sendEmptyMessageDelayed(MSG_AUTOPLAY, 1500);
				audioSetStreamMode(MangerConstant.AUDIO_STREAM_MODE_ENABLE);
			}
			
//			if(!AutoSettings.getInstance().getPowerState()){
//				Log.i(TAG, "setPowerState value = true");
//				AutoSettings.getInstance().setPowerState(true);
//		     }

			mMusicProxy.play();
		} else if (op_code == AudioControl.CONTROL_PAUSE) {
			LogUtil.i(TAG, "AVRCPControl : op_code= " + op_code);
			if (!isPlay && isPlaying) {
				LogUtil.i(TAG, "AVRCPControl : op_code= " + op_code
						+ ",disable to pause, because now is paused");
				return -1;
			}
			isPausing = true;
			removeAutoPlay();
			mMusicProxy.pause();
		}

		if (op_code == AudioControl.CONTROL_FORWARD) {

			mMusicProxy.next();
		}

		if (op_code == AudioControl.CONTROL_BACKWARD) {
			mMusicProxy.previous();
		}

		return 1;
	}

	/**
	 * Use this function to set stream volume to mute or un-mute.
	 * 
	 * @param mode
	 *            public static final int AUDIO_STREAM_MODE_ENABLE = 2; public
	 *            static final int AUDIO_STREAM_MODE_DISABLE = 3;
	 * @return
	 * @throws RemoteException
	 */
	// 设置静音模式
	public synchronized int audioSetStreamMode(int mode) throws RemoteException {
		if (null == mMusicProxy) {
			return errorCode;
		}
		
		int currentAudioMode = getStreamMode();
		LogUtil.i(TAG, "audioSetStreamMode  ---  mode = " + mode
				+ " , currentAudioMode = " + currentAudioMode);
		if (mode == currentAudioMode) {
			return -1;
		}

		if (mode == MangerConstant.AUDIO_STREAM_MODE_DISABLE) {
			mMusicProxy.mute();

		} else if (mode == MangerConstant.AUDIO_STREAM_MODE_ENABLE) {
			mMusicProxy.unmute();
		}
		return 1;
	}
	
	public int getA2dpStatus(){
		
		return mBluetoothProxy.getConnectStatus(2);
	}
	
	public boolean isSupportMusic(){
		Log.i(TAG, "mBluetoothProxy.supportMusic() = " + mBluetoothProxy.supportMusic());
		return mBluetoothProxy.supportMusic();
	}
	
	public int getHfpStatus(){
		return mBluetoothProxy.getConnectStatus(0);
	}
	
	public int getAvrcpStatus(){
	
		return mBluetoothProxy.getConnectStatus(3);
	}
	
	private static final int MSG_AUTOPLAY = 3;
	private static final int SYNC_ID3 = 4;
	
	Handler handler = new Handler() {
		public void handleMessage(final android.os.Message msg) {

			switch (msg.what) {
			case MSG_AUTOPLAY:
				if (!isPlay && playtimes < 4
						&& App.BT_MUSIC.equals(getCurrentSource())) {
					playtimes++;
					LogUtil.i(TAG, "MSG_AUTOPLAY playtimes = " + playtimes);
					try {
						AVRCPControl(AudioControl.CONTROL_PLAY);
					} catch (RemoteException e) {
					}
				} else {
					playtimes = 0;
					handler.removeMessages(MSG_AUTOPLAY);
				}
				break;
				
			case SYNC_ID3:
				isCanSync = true;
				break;
			}
		};
	};

	/**
	 * 更新蓝牙开关状态
	 * 
	 * @param status
	 */
	public void updateBTEnalbStatus(int status) {

		if (mIMusicModel != null) {
			mIMusicModel.updateBTPowerStatus(status);
		}

	}

	/**
	 * regist music status listener
	 * 
	 * @param nMusicModel
	 */
	public void registMusicListener(IMusicModel nMusicModel) {
		mIMusicModel = nMusicModel;
	}

	/**
	 * 更新carlife连接状态
	 */
	public void updateCLConnectStatus() {
		if (null != mIMusicModel) {
			mIMusicModel.updateCarlifeConnectStatus();
		}
	}

	/**
	 * 更新链接状态
	 * 
	 * @param status
	 */
	public void updateMsgByConnectStatusChange(int status) {
		if (null != mIMusicModel) {
			mIMusicModel.updateConnectStatusMsg(status);
		}
		if (status != 1) {
			notifyAutroMusicInfo(null);
		}
	}

	/**
	 * 结束蓝牙音乐
	 */
	public void finishActivity() {
		if (null != mIMusicModel) {
			mIMusicModel.finishMusicActivity();
		}
	}

	/**
	 * 更新播放状态
	 * 
	 * @param flag
	 */
	public void updatePlayStatus(boolean flag) {
		if (null != mIMusicModel) {
			mIMusicModel.updatePlayOrPauseStatus(flag);
		}
	}

	private MusicBean mBean;

	/**
	 * 更新当前音乐信息
	 * 
	 * @param bean
	 */
	public void updateCurrentMusicInfo(MusicBean bean) {
		mBean = bean;
		if (null != mIMusicModel) {
			mIMusicModel.getCurrentMusicBean(bean);
		}
	}

	/**
	 * 更新播放进度时间
	 * 
	 * @param position
	 * @param isPlaying
	 */
	public void updateCurrentPlayTime(String position, boolean isPlaying) {
		if (null != mIMusicModel) {
			mIMusicModel.getCurrentMusicPlayPosition(position, isPlaying);
		}
	}

	/**
	 * unregist music status listener
	 */
	public void unregistMusicListener() {
		mIMusicModel = null;
	}

	/**
	 * 切换音源
	 * 
	 * @return
	 */
	public boolean tryToSwitchSource() {
		LogUtil.i(TAG, "tryToSwitchSource");
		return mSource.tryToSwitchSource(App.BT_MUSIC);
	}

	/**
	 * 判断电话界面是否在最前面
	 */
	@SuppressWarnings("deprecation")
	public boolean isActive() {
		ActivityManager am = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		String MusicPlayUI = "com.hsae.d531mc.bluetooth.music.MusicMainActivity";
		return cn.getClassName().equals(MusicPlayUI);
	}

	/**
	 * @Description: 通知中间件音频焦点是否已获得，并且中间件切换音源
	 * @param isChanged
	 */
	public void mainAudioChanged(boolean isActivite) {
		LogUtil.i(TAG, "mainAudioChanged == " + mSource.getCurrentSource()
				+ "isActivite = " + isActivite);
		mSource.mainAudioChanged(App.BT_MUSIC, isActivite);
	}

	public boolean isAudioFocused = false;
	int playtimes = 0;

	/** 获取Android音频焦点 */
	public void requestAudioFocus(boolean flag) {
		this.requestAudioFocus(flag, false);
	}

	public void requestAudioFocus(boolean showOrBack, boolean fromPlay) {
		LogUtil.i(TAG, "cruze  BT getCurrentSource = " + mSource.getCurrentSource()
				+ ",isHandPuse = " + isHandPuse + "fromPlay =" + fromPlay);
		try {
			if (fromPlay) {
				doPlay(showOrBack);
			} else {
				if (mSource.getCurrentSource() == App.BT_MUSIC) {
					mainAudioChanged(showOrBack);
					// 如果手动点击停止，不进行播放；
					if (!autoConnectA2DP()) {
						if (!isHandPuse) {
							AVRCPControl(AudioControl.CONTROL_PLAY);
						}
					}
					if (a2dpStatus == 0) {
						LogUtil.i(TAG, "notifyAutoCoreWarning DDDDDDDDD");
						notifyAutoCoreWarning();
					}
				} else {
					doRequest(showOrBack);
				}
			}
		} catch (RemoteException e) {
		}
	}

	private void doPlay(boolean showOrBack) {
		try {
			if (!mSource.getFocusedApp().equals(App.BT_MUSIC)) {
				LogUtil.i(TAG, "cruze,doPlay 准备抢占焦点");
				boolean canSwich = tryToSwitchSource();
				if (canSwich) {
					int result = audioManager.requestAudioFocus(mAFCListener,
							AudioManager.STREAM_MUSIC,
							AudioManager.AUDIOFOCUS_GAIN);
					if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
						LogUtil.i(TAG, "cruze,requestAudioFocus == 获取音频焦点成功");
						isAudioFocused = true;
						mSource.setFocusedApp(App.BT_MUSIC.ordinal());
						mainAudioChanged(showOrBack);
						AVRCPControl(AudioControl.CONTROL_PLAY);
					} else {
						LogUtil.i(TAG, "cruze,requestAudioFocus == 获取音频焦点失败");
						isAudioFocused = false;
					}
					notifyLauncherInfo();
				}
			} else {
				AVRCPControl(AudioControl.CONTROL_PLAY);
			}
		} catch (RemoteException e) {
		}
	}

	private void doRequest(boolean showOrBack) {
		try {
			LogUtil.i(TAG, "cruze,doRequest 准备抢占焦点");
			boolean canSwich = tryToSwitchSource();
			if (canSwich) {
				int result = audioManager
						.requestAudioFocus(mAFCListener,
								AudioManager.STREAM_MUSIC,
								AudioManager.AUDIOFOCUS_GAIN);
				if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
					LogUtil.i(TAG, "cruze,requestAudioFocus == 获取音频焦点成功");
					isAudioFocused = true;
					mSource.setFocusedApp(App.BT_MUSIC.ordinal());
					mainAudioChanged(showOrBack);
					audioSetStreamMode(MangerConstant.AUDIO_STREAM_MODE_ENABLE);
					if (!autoConnectA2DP()) {
						if (!isHandPuse) {
							AVRCPControl(AudioControl.CONTROL_PLAY);
						}
					}
					if (a2dpStatus == 0) {
						LogUtil.i(TAG,
								"notifyAutoCoreWarning doRequest 1111111");
						notifyAutoCoreWarning();
					}
				} else {
					LogUtil.i(TAG, "cruze,requestAudioFocus == 获取音频焦点失败");
					isAudioFocused = false;
				}
				notifyLauncherInfo();
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 自动连接蓝牙音乐
	 */
	private boolean autoConnectA2DP() {
		if (mIMusicModel == null) {
			LogUtil.i(TAG, "autoConnectA2DP : mIMusicModel is null ");
			return false;
		}
		LogUtil.i(TAG, "autoConnA2dp : hfpStatus = " + hfpStatus
				+ " --- a2dpStatus = " + a2dpStatus);
		if (hfpStatus == 1 && a2dpStatus != 1) {
			mIMusicModel.autoConnectA2DP();
			return true;
		}
		return false;
	}

	private boolean pauseByMobile = false;
	/**
	 * 音源焦点变化监听
	 */
	OnAudioFocusChangeListener mAFCListener = new OnAudioFocusChangeListener() {

		@Override
		public void onAudioFocusChange(int focusChange) {
			int callstatus = phoneProxy.getPhoneState();
			switch (focusChange) {
			case AudioManager.AUDIOFOCUS_GAIN:
				LogUtil.i(TAG,
						"cruze mAFCListener---audio focus change AUDIOFOCUS_GAIN");
				isAudioFocused = true;
				mSource.setFocusedApp(App.BT_MUSIC.ordinal());
				if (a2dpStatus == 1) {
					LogUtil.i(TAG, "notifyAutroMusicInfo 33333333333333");
					notifyAutroMusicInfo(mBean);
					if (mBean != null) {
						mBean.setAudioFocus(true);
					}
					setTimingBegins();
				} else {
					LogUtil.i(TAG, "notifyAutoCoreWarning 1111111111");
					notifyAutoCoreWarning();
				}
				mainAudioChanged(isActive());
				
				try {
					if (!AutoSettings.getInstance().getPowerState()) {
						Log.i(TAG, "AUDIOFOCUS_GAIN  inPowerOffStatus ");
					    return;
					}
					
					audioSetStreamMode(MangerConstant.AUDIO_STREAM_MODE_ENABLE);
					if (!isHandPuse) {
						if (!pauseByMobile) {
							AVRCPControl(AudioControl.CONTROL_PLAY);
						}
					}
				} catch (RemoteException e) {
					Log.i(TAG, "AUDIOFOCUS_GAIN  error is "  + e);
				}
				break;
			case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
				LogUtil.i(TAG,
						"cruze  mAFCListener---audio focus change AUDIOFOCUS_GAIN_TRANSIENT");
				isAudioFocused = true;
				mainAudioChanged(isActive());
				break;
			case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
				LogUtil.i(TAG,
						"cruze  mAFCListener---audio focus change AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK");
				isAudioFocused = true;
				mainAudioChanged(isActive());
				break;
			case AudioManager.AUDIOFOCUS_LOSS:
				LogUtil.i("cruze", "cruze  AUDIOFOCUS_LOSS");
				try {
					if (callstatus == 0 || callstatus == -1) {
						pauseByMobile = false;
						AVRCPControl(AudioControl.CONTROL_PAUSE);
					} else {
						pauseByMobile = true;
						LogUtil.i(TAG,
								"audiofocus loss caused by callstatus , pause by mobile self");
					}
					audioSetStreamMode(MangerConstant.AUDIO_STREAM_MODE_DISABLE);
				} catch (RemoteException e1) {
				}
				isAudioFocused = false;
				hasSet = false;
				notifyAutroMusicInfo(null);
				break;
			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
				LogUtil.i(TAG,
						"cruze  mAFCListener---audio focus change AUDIOFOCUS_LOSS_TRANSIENT");
				isAudioFocused = false;
				hasSet = false;
				notifyAutroMusicInfo(null);
				break;
			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
				LogUtil.i(TAG,
						"cruze  mAFCListener---audio focus change AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
				isAudioFocused = false;
				hasSet = false;
				notifyAutroMusicInfo(null);
				break;
			}
		}
	};

	/**
	 * 清除缓存壁纸
	 */
	public void deleteWallpaperCache() {
		mMemoryCache = getCache();
		if (getWallPaperBitmap() != null) {
			mMemoryCache.remove(Util.MC_WALLPAPER);
			LogUtil.i(TAG, "deleteWallpaperCache");
		}
	}

	/**
	 * 添加壁纸缓存
	 * 
	 * @param bitmap
	 */
	public void addWallPaperToCache(Bitmap bitmap) {
		mMemoryCache = getCache();

		if (getWallPaperBitmap() == null) {
			mMemoryCache.put(Util.MC_WALLPAPER, bitmap);
			LogUtil.i(TAG, "addWallPaperToCache");
		}
		if (mIMusicModel != null) {
			mIMusicModel.updateBg();
		}
	}

	/**
	 * 获取缓存壁纸
	 * 
	 * @return
	 */
	public Bitmap getWallPaperBitmap() {
		mMemoryCache = getCache();
		return mMemoryCache.get(Util.MC_WALLPAPER);
	}

	/**
	 * 初始化壁纸缓存
	 * 
	 * @return
	 */
	public LruCache<String, Bitmap> getCache() {
		if (mMemoryCache == null) {
			mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
				@SuppressLint("NewApi")
				@Override
				protected int sizeOf(String key, Bitmap bitmap) {
					return bitmap.getByteCount();
				}
			};
		}
		return mMemoryCache;
	}

	public void onUsbDisConnect() {
		if (mIMusicModel != null) {
			mIMusicModel.onUsbDisConnect();
		}
	}

	public void setPrevClicked() {
		mIMusicModel.setPrevClicked();
	}

	public void setNextClicked() {
		mIMusicModel.setNextClicked();
	}

	public boolean isCarlifeConnected() {
		boolean isConnected = false;
		Soc soc = new Soc();
		UsbDevices deivce = soc.getCurrentDevice();
		if (deivce != null) {
			if (deivce == UsbDevices.CARLIFE) {
				isConnected = true;
			}
		}
		Log.i(TAG, "isCARLIFEConnected = " + soc.getCurrentDevice());
		return isConnected;
	}

	/***
	 * 取消自动执行播放的动作
	 */
	public void removeAutoPlay() {
		if (handler.hasMessages(MSG_AUTOPLAY)) {
			playtimes = 0;
			handler.removeMessages(MSG_AUTOPLAY);
		}
	}

	public App getCurrentSource() {
		return mSource.getCurrentSource();
	}

	public synchronized void notifyAutroMusicInfo(MusicBean bean) {
		notifyAutroMusicInfo(bean, false, false);
	}

	private String lastTitle = "";
	private String lastAtrist = "";
	private String lastAlbum = "";
	private int lastPlayStatus = 1;
	public boolean powerStatus = false;
	public boolean accStatus = true;
	private boolean hasSet = false;
	private boolean isOnFW = false; // 是否处于快进快退之中
	public int streamStatus = 0;
	
	public synchronized void notifyAutroMusicInfo(MusicBean bean,
			boolean fromStream, boolean fromPoweroff) {
		if (null == mBTMmanager) {
			mBTMmanager = BTMusicManager.getInstance(mContext);
		}
		if (bean == null) {
			LogUtil.i(TAG, "notifyAutroMusicInfo : bean == null");
			syncMusicInfo(null,false);
			
//			isCanSync = false;
//			if(!handler.hasMessages(SYNC_ID3)){
//				handler.sendEmptyMessageDelayed(SYNC_ID3, 1200);
//			}
			return;
		}
		String title = bean.getTitle();
		String atrist = bean.getAtrist();
		String album = bean.getAlbum();

		boolean audioFocus = bean.isAudioFocus();

		if (isPowerOff()) {
			LogUtil.i(TAG, "notifyAutroMusicInfo isPowerOff");
			return;
		}

		if (!accStatus) {
			LogUtil.i(TAG, "notifyAutroMusicInfo accStatus is false");
			return;
		}

		if (fromStream) {
			
			if(mSource.getCurrentSource() == App.BT_MUSIC){
				
				if(!hasSet){
					LogUtil.i(TAG, "notifyAutroMusicInfo on position change");
					BTMusicInfo info = new BTMusicInfo(lastTitle, lastAtrist,
							lastAlbum, null);
					syncMusicInfo(info,false);
				}else {
					hasSet = false;
				}
				
			}
		}

		if (fromPoweroff) {
			BTMusicInfo info = new BTMusicInfo(lastTitle, lastAtrist,
					lastAlbum, null);
			syncMusicInfo(info,false);
			return;
		}

		if (isOnFW) {
			lastTitle = title;
			lastAtrist = atrist;
			lastAlbum = album;
			// lastPlayStatus = streamStatus;
			BTMusicInfo info = new BTMusicInfo(lastTitle, lastAtrist,
					lastAlbum, null);
			syncMusicInfo(info,false);
		} else if (!lastTitle.equalsIgnoreCase(title)
				|| !lastAtrist.equalsIgnoreCase(atrist)
				|| !lastAlbum.equalsIgnoreCase(album)
				) {
			
			LogUtil.i(TAG, "notifyAutroMusicInfo 6666666666666");
			lastTitle = title;
			lastAtrist = atrist;
			lastAlbum = album;
			// lastPlayStatus = streamStatus;

			BTMusicInfo info = new BTMusicInfo(lastTitle, lastAtrist,
					lastAlbum, null);
			syncMusicInfo(info,true);
		}
	}

	public boolean getHfpConnectState() {

		boolean isHfpConnect = mBluetoothProxy.isHfpConnect();
		Log.i(TAG, "isHfpConnect = " + isHfpConnect);

		return isHfpConnect;
	}

	public boolean isPowerOff() {
		boolean status = false;
		try {
			status = !AutoSettings.getInstance().getPowerState();
		} catch (RemoteException e) {
		}
		return status;
	}

	private void syncMusicInfo(BTMusicInfo info,boolean isId3) {
		
		Log.i(TAG, "isCanSync = " + isCanSync + ", isID3 = " + isId3);
		
		if (!isCanSync) {
			return;
		}
		
		if(isId3){
			isCanSync = false;
			
			if(!handler.hasMessages(SYNC_ID3)){
				handler.sendEmptyMessageDelayed(SYNC_ID3, 400);
			}
		}
		
		synchronized (lockOfBTMmanager) {
			
			if (info == null) {
				LogUtil.i(TAG,
						"notifyAutroMusicInfo syncMusicInfo info is null");
				try {
					int n = mBTMmanager.mListeners.beginBroadcast();
					for (int i = 0; i < n; i++) {
						mBTMmanager.mListeners.getBroadcastItem(i)
								.syncBtMusicInfo(info);
					}
					mBTMmanager.mListeners.finishBroadcast();
				} catch (Exception e) {
					LogUtil.i(TAG, " ---- Exception = " + e.toString(), e);
				}
			} else {
				LogUtil.i(TAG,
						"notifyAutroMusicInfo syncMusicInfo : lastTitle = "
								+ lastTitle + " , lastAtrist = " + lastAtrist
								+ " , lastAlbum = " + lastAlbum
								+ " , lastPlayStatus = " + lastPlayStatus
								+ streamStatus);

				if (isAudioFocused) {
					try {
						int n = mBTMmanager.mListeners.beginBroadcast();
						for (int i = 0; i < n; i++) {
							mBTMmanager.mListeners.getBroadcastItem(i)
									.syncBtMusicInfo(info);
						}
						mBTMmanager.mListeners.finishBroadcast();
					} catch (Exception e) {
						LogUtil.i(TAG, " ---- Exception = " + e.toString(), e);
					}
				}
			}
		}
	}

	/****
	 * 通知中间件蓝牙没有连接
	 */
	public void notifyAutoCoreWarning() {
		if (mBTMmanager == null) {
			mBTMmanager = BTMusicManager.getInstance(mContext);
		}
		if (!accStatus) {
			LogUtil.i(TAG, "notifyAutoCoreWarning : accStatus");
			return;
		}

		if (!isAudioFocused) {
			LogUtil.i(TAG, "notifyAutoCoreWarning : isAudioFocused");
			return;
		}

		synchronized (lockOfBTMmanager) {
			LogUtil.i(TAG, "notifyAutoCoreWarning : NONDISPLAY");
		}
	}

	/**
	 * 通知launcher 音乐信息
	 */
	private void notifyLauncherInfo() {
		int connStatus = 0;
		try {
			connStatus = getConnectStatus(
					MangerConstant.PROFILE_AUDIO_STREAM_CHANNEL, 0);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		if (connStatus == MangerConstant.Anw_SUCCESS && mBean != null) {
			LogUtil.i(TAG, "notifyLauncherInfo");
			notifyAutroMusicInfo(mBean,true,false);
			hasSet = true;
			mBean.setAudioFocus(true);
		} else {
			LogUtil.i(TAG, "notifyAutroMusicInfo FFFFFFFFFFFFFF");
			//notifyAutroMusicInfo(null);
		}
	}

	public void syncBtStatus(int a2dpStatus2) {
		if (isDisByIpod) {
			isDisByIpod = false;
			IPodProxy.getInstance().notifyA2dpConnected(
					a2dpStatus == MangerConstant.Anw_SUCCESS);
		}

		synchronized (lockOfBTMmanager) {
			if (null == mBTMmanager) {
				mBTMmanager = BTMusicManager.getInstance(mContext);
			}
			if (mBTMmanager.mListeners == null) {
				return;
			}
		}
	}

	private static final int MSG_TICKER = 100;
	private final Handler stepTimeHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == MSG_TICKER) {
				
				try {
					stepTimeHandler.sendEmptyMessageDelayed(MSG_TICKER, 1000);
					getMusicInfo();
				} catch (RemoteException e) {
				}
			}
		};
	};

	private final Ticker mTicker = new Ticker();

	/**
	 * set timer start
	 */
	public void setTimingBegins() {
		if (a2dpStatus == 1 && avrcpStatus == 1) {
			if (!stepTimeHandler.hasMessages(MSG_TICKER)) {
				stepTimeHandler.sendEmptyMessage(MSG_TICKER);
			}
		}
	}

	/**
	 * set timer end
	 */
	public void setTimingEnd() {
		stepTimeHandler.removeCallbacks(mTicker);
		if (stepTimeHandler.hasMessages(MSG_TICKER)) {
			stepTimeHandler.removeMessages(MSG_TICKER);
		}
		LogUtil.i(TAG, "setTimingEnd");
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
				if (a2dpStatus == 1 && avrcpStatus == 1) {
					getMusicInfo();
					stepTimeHandler.postDelayed(this, 1000);
				}
			} catch (RemoteException e) {
			}
		}
	}

	public void setStreamMute() {
		try {
			App currentSource = mSource.getCurrentSource();
			boolean isDiagnoseMode = AutoSettings.getInstance()
					.isDiagnoseMode();
			if (currentSource != App.BT_MUSIC || isDiagnoseMode) {
				audioSetStreamMode(MangerConstant.AUDIO_STREAM_MODE_DISABLE);
			} else if (currentSource == App.BT_MUSIC && !isDiagnoseMode) {
				audioSetStreamMode(MangerConstant.AUDIO_STREAM_MODE_ENABLE);
			}
		} catch (IllegalStateException e) {
		} catch (IllegalArgumentException e) {
		} catch (RemoteException e) {
		}
	}

	public void setBluetoothAllCallback(
			BluetoothAllCallback mBluetoothAllCallback) {
		this.mBluetoothAllCallback = mBluetoothAllCallback;
	}
	
	public BTMusicInfo getCurrentMusicInfo(){
		return new BTMusicInfo(lastTitle, lastAtrist,
				lastAlbum, null);
	}
	
	public Source getSource(){
		if(mSource != null){
			return mSource;
		}else {
			return new Source();
		}
	}
}

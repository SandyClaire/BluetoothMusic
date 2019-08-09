package com.hsae.d531mc.bluetooth.music;

import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RotateDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hsae.autosdk.os.Soc;
import com.hsae.autosdk.os.SocConst.UsbDevices;
import com.hsae.autosdk.radio.RadioConst;
import com.hsae.autosdk.source.Source;
import com.hsae.autosdk.source.SourceConst.App;
import com.hsae.autosdk.util.LogUtil;
import com.hsae.d531mc.bluetooth.music.entry.MusicBean;
import com.hsae.d531mc.bluetooth.music.model.impl.MusicModel;
import com.hsae.d531mc.bluetooth.music.observer.IObserver;
import com.hsae.d531mc.bluetooth.music.observer.ISubject;
import com.hsae.d531mc.bluetooth.music.observer.ObserverAdapter;
import com.hsae.d531mc.bluetooth.music.presenter.MusicPersenter;
import com.hsae.d531mc.bluetooth.music.util.MusicActionDefine;
import com.hsae.d531mc.bluetooth.music.util.MySeekBar;
import com.hsae.d531mc.bluetooth.music.view.IMusicView;

/**
 * 
 * @author wangda
 *  
 */
@SuppressLint("NewApi")
public class MusicMainActivity extends Activity implements ISubject,
		IMusicView, OnClickListener {

	enum Media {
		fm, am, usb, bt, ipod;
	}

	private static final String TAG = "MusicMainActivity";

	private static final String KUWO_PACKAGE = "cn.kuwo.kwmusiccar";
	private static final String KUWO_ACTIVITY = "cn.kuwo.kwmusiccar.WelcomeActivity";
	private static final String IPOD_PACKAGE = "com.hsae.d531mc.ipod";
	private static final String IPOD_ACTIVITY = "com.hsae.d531mc.ipod.view.MainActivity";
	private static final String USB_PACKAGE = "com.hsae.d531mc.usbmedia";
	private static final String USB_ACTIVITY = "com.hsae.d531mc.usbmedia.musicui.lifecycle.MusicActivity";
	private static final String SETTINGS_PACKAGE = "com.hsae.d531mc.systemsetting";
	private static final String SETTINGS_Bluetooth_ACTIVITY = "com.hsae.d531mc.systemsetting.SystemSettingsActivity";
	private static final String SETTINGS_Bluetooth_FRAGMENT = "android.hase.settings.BT_SETTINGS";

	
	private static final int MSG_DRAWLAYOUT_SHOW = 111;
	private static final int MSG_SWITCH = 1123;
	private boolean canSwitch = true;

	private MusicPersenter mPresenter;

	private ImageView iOnlineMusic, ivUSB, ivBT;

	private ImageView ivAlbumCut;
	private ImageButton mBtnPrev;
	private ImageView mBtnPlay;
	private ImageButton mBtnNext;
	private TextView mTextTitle;
	private TextView mTextArtist;
	private TextView mTextCurTime;
	private TextView mTextTotalTime;
	private TextView btPowerUnuseText;
	private MySeekBar mSeekBar;
	private FrameLayout unConnectLayout;
	private FrameLayout btPowerUnuse;
	private FrameLayout deviceManagementLayout;
	private boolean ismPlaying = false;
	private ImageView mImageBg;
	private ImageView mSeekTail;
	private TextView mTextTip;
	private boolean isSupportPlaybackpos = false;
	private boolean isSupportMetadata = false;
	private boolean isRequestAudio = true;
	private static final int LONG_CLICK_PREV = 1;
	private static final int LONG_CLICK_NEXT = 2;
	private static final int LONG_FAST_FORWORD_CANCLE = 5;
	private static final int LONG_FAST_BACKWORD_CANCLE = 6;

	private static final int SHORT_CLICK_PREV = 3;
	private static final int SHORT_CLICK_NEXT = 4;

	private boolean isNormalPrev = true;
	private boolean isNormalNext = true;
	private FrameLayout mFraInfo;
	private FrameLayout mFraControl;
	RotateDrawable rDrawable;

	private Handler mHandler = new Handler(Looper.getMainLooper(),
			new Handler.Callback() {

				@Override
				public boolean handleMessage(Message msg) {
					switch (msg.what) {
					case LONG_CLICK_NEXT:
						isNormalNext = false;
						Message msgln = Message.obtain();
						msgln.what = MusicActionDefine.ACTION_A2DP_FASTFORWORD;
						MusicMainActivity.this.notify(msgln, FLAG_RUN_SYNC);
						LogUtil.i(TAG, " --- next long press ---");
						break;
					case LONG_FAST_BACKWORD_CANCLE:
						Message msgbc = Message.obtain();
						msgbc.what = MusicActionDefine.ACTION_A2DP_REWIND_CANCEL;
						MusicMainActivity.this.notify(msgbc, FLAG_RUN_SYNC);
						break;
					case LONG_FAST_FORWORD_CANCLE:
						Message msgfc = Message.obtain();
						msgfc.what = MusicActionDefine.ACTION_A2DP_FASTFORWORD_CANCEL;
						MusicMainActivity.this.notify(msgfc, FLAG_RUN_SYNC);
						break;
					case SHORT_CLICK_NEXT:
						LogUtil.i(TAG, " --- next short press ---");
						Message msgn = Message.obtain();
						msgn.what = MusicActionDefine.ACTION_A2DP_NEXT;
						MusicMainActivity.this.notify(msgn,
								FLAG_RUN_MAIN_THREAD);
						break;
					case LONG_CLICK_PREV:
						isNormalPrev = false;
						Message msglp = Message.obtain();
						msglp.what = MusicActionDefine.ACTION_A2DP_REWIND;
						MusicMainActivity.this.notify(msglp, FLAG_RUN_SYNC);
						LogUtil.i(TAG, " --- prev long press ---");
						break;
					case SHORT_CLICK_PREV:
						LogUtil.i(TAG, " --- prev short press --- ");
						Message msgp = Message.obtain();
						msgp.what = MusicActionDefine.ACTION_A2DP_PREV;
						MusicMainActivity.this.notify(msgp,
								FLAG_RUN_MAIN_THREAD);
						break;
					}
					return false;
				}
			});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.i("wangda", "MusicMainActivity -- onCreate starTime = "
				+ System.currentTimeMillis());
		// 透明状态栏
		getWindow()
				.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		// 透明导航栏
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

		setContentView(R.layout.music_play);
		
		initView();
		initMvp();
		LogUtil.i("wangda","MusicMainActivity -- onCreate endTime = "
						+ System.currentTimeMillis());
	}

	@Override
	protected void onStart() {
		super.onStart();
		LogUtil.i(TAG, "cruze onStart");
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		LogUtil.i(TAG, "cruze onNewIntent");
		
		boolean intentBool = intent.getBooleanExtra("systemUI", false);
		LogUtil.i(TAG, "intentBool = " + intentBool);
		if(intentBool){
			isRequestAudio = false;
		}
		
		Message msg = Message.obtain();
		msg.what = MusicActionDefine.ACTION_APP_ONINTENT;
		this.notify(msg, FLAG_RUN_SYNC);
	}

	private void initMvp() {
		MusicModel model = new MusicModel(this);
		mPresenter = new MusicPersenter(this, model);
		this.attach(mPresenter);

		Message msg = Message.obtain();
		msg.what = MusicActionDefine.ACTION_APP_LAUNCHED;
		this.notify(msg, FLAG_RUN_SYNC);
	}

	private void initView() {

		iOnlineMusic = (ImageView) findViewById(R.id.source_online_music);
		
		ivUSB = (ImageView) findViewById(R.id.source_usb);
		ivBT = (ImageView) findViewById(R.id.source_bt);
		ivAlbumCut = (ImageView) findViewById(R.id.music_defaultcover);

		mBtnPrev = (ImageButton) findViewById(R.id.btn_prev);
		mBtnPlay = (ImageView) findViewById(R.id.btn_play);
		mBtnNext = (ImageButton) findViewById(R.id.btn_next);
		mSeekBar = (MySeekBar) findViewById(R.id.music_seekbar);
		mTextTitle = (TextView) findViewById(R.id.music_title);
		mTextArtist = (TextView) findViewById(R.id.music_artist);
		mTextCurTime = (TextView) findViewById(R.id.music_currenttime);
		mTextTotalTime = (TextView) findViewById(R.id.music_totaltime);
		mImageBg = (ImageView) findViewById(R.id.default_screenbg);
		mSeekTail = (ImageView) findViewById(R.id.seekbar_tail);
		unConnectLayout = (FrameLayout) findViewById(R.id.main_connectTip);
		btPowerUnuse = (FrameLayout) findViewById(R.id.bt_power_unuse);
		btPowerUnuseText = (TextView) findViewById(R.id.bt_power_unuse_text);
		mTextTip = (TextView) findViewById(R.id.text_disconnect_tip);
		mFraInfo = (FrameLayout) findViewById(R.id.layout_musicinfo);
		mFraControl = (FrameLayout) findViewById(R.id.layout_control);
		
		deviceManagementLayout = (FrameLayout)findViewById(R.id.device_management_layout);
		
		mBtnPrev.setOnTouchListener(prevListener);
		mBtnPlay.setOnClickListener(this);
		mBtnNext.setOnTouchListener(nextListener);
		deviceManagementLayout.setOnClickListener(this);
		
		iOnlineMusic.setOnClickListener(this);
		ivUSB.setOnClickListener(this);
		ivBT.setOnClickListener(this);

		rDrawable = (RotateDrawable) ivAlbumCut.getBackground();
		rDrawable.setLevel(0);
	}

	int level = 0;
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case MSG_SWITCH:
				canSwitch = true;
				break;
			default:
				break;
			}

		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.i(TAG, "bluetoothmusic onResume");
		ivBT.setSelected(true);
		
		if(isRequestAudio){ 
			LogUtil.i(TAG, "onResume , isRequestAudio = true");
			Message msg = Message.obtain();
			msg.what = MusicActionDefine.ACTION_A2DP_REQUEST_AUDIO_FOCUSE;
			this.notify(msg, FLAG_RUN_SYNC);
		}
	
		boolean isUsb = !isIpodConnected();
		ivUSB.setImageResource(isUsb ? R.drawable.selector_icon_usb
				: R.drawable.selector_source_ipod);
		
		isRequestAudio = true;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mMusicHandler.removeCallbacks(updateMusicPlayTimer);

		Message msg = Message.obtain();
		msg.what = MusicActionDefine.ACTION_A2DP_ACTIVITY_PAUSE;
		this.notify(msg, FLAG_RUN_SYNC);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	public void finishActivity() {
		Message msg = Message.obtain();
		msg.what = MusicActionDefine.ACTION_APP_EXIT;
		this.notify(msg, FLAG_RUN_SYNC);
		this.detach(mPresenter);
		this.finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.detach(mPresenter);
	}

	@Override
	public boolean attach(IObserver inObserver) {
		return ObserverAdapter.getInstance().register(this, inObserver);
	}

	@Override
	public boolean detach(IObserver inObserver) {
		return ObserverAdapter.getInstance().unregister(this, inObserver);
	}

	@Override
	public void notify(Message inMessage, int... flag) {
		ObserverAdapter.getInstance().notify(this, inMessage, flag);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.source_online_music:
			switchSource(Media.fm);
			break;
			
		case R.id.source_usb:
			switchSource(Media.usb);
			break;
			
		case R.id.source_bt:
			switchSource(Media.bt);
			break;
		
		case R.id.device_management_layout:
			Log.i(TAG, "start setting bluetooth management");

			Intent intent = new Intent();
			ComponentName comp = new ComponentName(SETTINGS_PACKAGE, SETTINGS_Bluetooth_ACTIVITY);
			intent.setComponent(comp);

			int launchFlags = Intent.FLAG_ACTIVITY_NEW_TASK;
			intent.setFlags(launchFlags);
			intent.setAction(SETTINGS_Bluetooth_FRAGMENT);
		
			startActivity(intent);
			
			break;
		case R.id.btn_play:
			Message msgl = Message.obtain();
			if (ismPlaying) {
				msgl.what = MusicActionDefine.ACTION_A2DP_PAUSE;
			} else {
				msgl.what = MusicActionDefine.ACTION_A2DP_PLAY;
			}
			this.notify(msgl, FLAG_RUN_SYNC);
			break;
		
		default:
			break;
		}
	}

	/****
	 * 切换音源
	 * 
	 * @param source
	 */
	private void switchSource(Media source) {
		if (canSwitch) {
			canSwitch = false;
			if (!handler.hasMessages(MSG_SWITCH)) {
				handler.sendEmptyMessageDelayed(MSG_SWITCH, 500);
			}
			Bundle bundle = new Bundle();
			LogUtil.i(TAG, "switchSource :source is " + source);
			if (source.equals(Media.am)) {
//				iOnlineMusic.setSelected(false);
//				ivUSB.setSelected(false);
//				ivBT.setSelected(false);
//				bundle.putInt("band", RadioConst.RadioBandType.AM1);
//				startOtherAPP(App.RADIO, KUWO_PACKAGE, KUWO_ACTIVITY,
//						bundle);
			} else if (source.equals(Media.fm)) {
				iOnlineMusic.setSelected(true);
				ivUSB.setSelected(false);
				ivBT.setSelected(false);
				bundle.putInt("band", RadioConst.RadioBandType.FM1);
				startOtherAPP(App.RADIO, KUWO_PACKAGE, KUWO_ACTIVITY,
						bundle);
			} else if (source.equals(Media.usb)) {
				iOnlineMusic.setSelected(false);
				ivUSB.setSelected(true);
				ivBT.setSelected(false);

				boolean isUsb = isUsbConnected() || !isIpodConnected();
				App app = isUsb ? App.USB_MUSIC : App.IPOD_MUSIC;
				String strPackage = isUsb ? USB_PACKAGE : IPOD_PACKAGE;
				String strClass = isUsb ? USB_ACTIVITY : IPOD_ACTIVITY;
				startOtherAPP(app, strPackage, strClass, bundle);
			} else if (source.equals(Media.bt)) {
				iOnlineMusic.setSelected(false);
				ivUSB.setSelected(false);
				ivBT.setSelected(true);
			}
		} else {
			LogUtil.i(TAG,
					"switchSource can not switch source twice in 500 ms!");
		}
	}

	/**
	 * @Description: 判断USB是否连接
	 * @return
	 */
	private boolean isUsbConnected() {
		boolean isConnected = false;
		Soc soc = new Soc();
		UsbDevices deivce = soc.getCurrentDevice();
		if (deivce != null) {
			if (deivce == UsbDevices.UDISK) {
				isConnected = true;
			}
		}
		Log.i(TAG, "isUsbConnected = " + soc.getCurrentDevice());
		return isConnected;
	}

	/**
	 * @Description: 判断Ipod是否连接
	 * @return
	 */
	private boolean isIpodConnected() {
		Soc soc = new Soc();
		return soc.getCurrentDevice() == UsbDevices.IPOD;
	}

	public void startOtherAPP(App app, String appId, String activityName,
			Bundle bundle) {
		boolean tryToSwitchSource = true;
		try {
			Source source = new Source();
			LogUtil.i(TAG, "tryToSwitchSource == " + tryToSwitchSource);
			tryToSwitchSource = source.tryToSwitchSource(app);
		} catch (Exception e) {
			LogUtil.i(TAG, "tryToSwitchSource == Exception" + e);
		}

		if (tryToSwitchSource) {

			if (isAppInstalled(getApplicationContext(), appId)) {
				Intent intent = new Intent();
				ComponentName comp = new ComponentName(appId, activityName);
				intent.setComponent(comp);

				int launchFlags = Intent.FLAG_ACTIVITY_NEW_TASK;
				intent.setFlags(launchFlags);
				intent.setAction("android.intent.action.VIEW");
				if (bundle != null) {
					intent.putExtras(bundle);
				}
				startActivity(intent);
			}
		}
	}

	/**
	 * @Description: 判断应用是否安装
	 * @param context
	 * @param packagename
	 * @return
	 */
	private boolean isAppInstalled(Context context, String packagename) {
		PackageInfo packageInfo;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(
					packagename, 0);
		} catch (NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}

		boolean isInstalled = (packageInfo == null) ? false : true;
		return isInstalled;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
			
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void updateViewByConnectStatus(int status) {
		LogUtil.i(TAG, "updateViewByConnectStatus " + status);
		
		if (status == 0) {
			updateViewShow(false, false, true,true);
			mSeekTail.setVisibility(View.GONE);
			mTextTotalTime.setText("00:00:00");
			mTextCurTime.setText("00:00:00");
			musicName = "";
			mTextTitle.setText(getResources().getString(
					R.string.music_title_unknow));
			mTextArtist.setText(getResources().getString(
					R.string.music_artist_unknow));
			mSeekBar.setMax(0);
			LogUtil.i(TAG, "Bluetooth A2DP disconnected");
			mHandler.sendEmptyMessageDelayed(MSG_DRAWLAYOUT_SHOW, 500);
		} else if (status == 1) {
			updateViewShow(true, false, true,true);
			LogUtil.i(TAG, "Bluetooth A2DP connected");
		} else if (status == -1) {		// carlife is connected
			
		} else if (status == -2) {		//blue power off
			
			updateViewShow(false, false, false,true);
		} else if(status == -3){		//no support bluetooth music
			updateViewShow(false, false, false,false);
		}
		
	}

	@SuppressWarnings("deprecation")
	private void updateViewShow(boolean flag, boolean isFromCarlife,
			boolean btPowerStatus,boolean isSupportMusicFunction) {
		LogUtil.i(TAG, "updateViewShow : flag = " + flag + "isFromCarlife = "
				+ isFromCarlife + "btPowerStatus = " + btPowerStatus);
		mBtnPrev.setEnabled(flag);
		mBtnPlay.setEnabled(flag);
		mBtnNext.setEnabled(flag);
		mSeekBar.setEnabled(flag);

		if (btPowerStatus) {

			if (flag) {
				mFraInfo.setVisibility(View.VISIBLE);
				mFraControl.setVisibility(View.VISIBLE);
				unConnectLayout.setVisibility(View.GONE);
				btPowerUnuse.setVisibility(View.GONE);
			} else {
				mFraInfo.setVisibility(View.GONE);
				mFraControl.setVisibility(View.GONE);
				unConnectLayout.setVisibility(View.GONE);
				btPowerUnuse.setVisibility(View.VISIBLE);
				btPowerUnuseText.setText(getResources().getString(R.string.music_bluetooth_disconnect_tip));
				/*
				 * if (!isFromCarlife) {
				 * mTextTip.setText(getResources().getString
				 * (R.string.music_bluetooth_disconnect_tip)); } else {
				 * mTextTip.setText(getResources().getString(R.string.
				 * music_bluetooth_carlife_connect_tip)); }
				 */
				ismPlaying = false;
				mMusicHandler.removeCallbacks(updateMusicPlayTimer);
				mBtnPlay.setImageDrawable(getResources().getDrawable(
						R.drawable.selector_stop));
			}
		} else {

			mFraInfo.setVisibility(View.GONE);
			mFraControl.setVisibility(View.GONE);
			unConnectLayout.setVisibility(View.GONE);
			btPowerUnuse.setVisibility(View.VISIBLE);
			if(!isSupportMusicFunction){
				btPowerUnuseText.setText(getResources().getString(R.string.no_support_bluetooth_music));
			}else{
				btPowerUnuseText.setText(getResources().getString(R.string.bluetooth_power_not_open));
			}
			
			ismPlaying = false;
			mMusicHandler.removeCallbacks(updateMusicPlayTimer);
			mBtnPlay.setImageDrawable(getResources().getDrawable(
					R.drawable.selector_stop));
		}

	}

	@SuppressWarnings("deprecation")
	@Override
	public void updatePlayBtnByStatus(boolean flag) {
		LogUtil.i(TAG, "Activity updatePlayBtnByStatus -- flag = " + flag);
		if (flag) {
			ismPlaying = true;
			mBtnPlay.setImageDrawable(getResources().getDrawable(
					R.drawable.selector_play));
		} else {
			ismPlaying = false;
			mMusicHandler.removeCallbacks(updateMusicPlayTimer);
			mBtnPlay.setImageDrawable(getResources().getDrawable(
					R.drawable.selector_stop));
		}
	}

	private String musicName = "";

	@Override
	public void updateMusicDataInfo(MusicBean bean, boolean isSupport) {
		isSupportMetadata = isSupport;
		LogUtil.i(TAG, "Activity updateMusicDataInfo -- isSupport = "
				+ isSupport);
		if (null != bean) {
			LogUtil.i(TAG, " name  = " + bean.getTitle() + " -- isSupport = "
					+ isSupport);
			if ("".equals(bean.getTitle())) {
				musicName = "";
				mTextTitle.setText(getResources().getString(
						R.string.music_title_unknow));
			} else {
				if (!musicName.equals(bean.getTitle())) {
					musicName = bean.getTitle();
					mTextTitle.setText(bean.getTitle());
				}
			}
			if ("".equals(bean.getAtrist())) {
				mTextArtist.setText(getResources().getString(
						R.string.music_artist_unknow));
			} else {
				mTextArtist.setText(bean.getAtrist());
			}
			if ("".equals(bean.getTotalTime())) {
				mTextTotalTime.setText("00:00:00");
			} else {
				mTextTotalTime.setText(getTotalTime(bean.getTotalTime()));
			}

		} else {
			mTextTitle.setText(getResources().getString(
					R.string.music_title_unknow));
			mTextArtist.setText(getResources().getString(
					R.string.music_artist_unknow));
			mTextTotalTime.setText("00:00:00");
			mTextCurTime.setText("00:00:00");
			mSeekBar.setMax(0);
			mSeekTail.setVisibility(View.GONE);
		}
	}

	private String getTotalTime(String nTime) {
		String timeStr = "00:00:00";

		if (nTime.equals("-1")) {
			return timeStr;
		} else {
			if (isNumeric(nTime) == true) {
				try {
					long iMax = Long.valueOf(nTime) / 1000;
					mSeekBar.setMax((int) iMax);
					timeStr = toTime(Long.valueOf(nTime));
				} catch (NumberFormatException e) {
					LogUtil.i(TAG, " is number but is not long");
				}
				return timeStr;
			} else {
				return timeStr;
			}
		}
	}

	private String toTime(long time) {
//		long minute = time / 1000 / 60;
//		long s = time / 1000 % 60;
		String hh = null;
		String mm = null;
		String ss = null;
		
		Long hour = time / ((1000 * 60) * 60);
        Long minute = (time - hour * (1000 * 3600)) / (1000 * 60);
        Long second = (time - hour * (1000 * 3600) - minute * (1000 * 60)) / 1000;
        
        if(hour < 10){
        	hh = "0" + hour;
        }else{
        	hh = hour + "";
        }
        
        if (minute < 10)
			mm = "0" + minute;
		else
			mm = minute + "";

		if (second < 10)
			ss = "0" + second;
		else
			ss = "" + second;

		return hh + ":" + mm + ":" + ss;
	}

	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]+");
		return pattern.matcher(str).matches();
	}

	private String getCurrentTime(String nTime) {
		Log.i(TAG, "getCurrentTime = " + nTime);
		
		if (nTime.equals("-1") && isSupportPlaybackpos == false) {
			return "00:00:00";
		} else {
			if (isNumeric(nTime) == true) {
				isSupportPlaybackpos = true;
				try {
					int pos = Integer.valueOf(nTime) / 1000;
					mSeekBar.setProgress(pos);
//					freshSeekBarTail(pos);
					return toTime(Integer.valueOf(nTime));
				} catch (NumberFormatException e) {

					return "00:00:00";
				}
			} else {
				return "00:00:00";
			}
		}
	}

	private final Handler mMusicHandler = new Handler();

	Runnable updateMusicPlayTimer = new Runnable() {
		@Override
		public void run() {
			UpdatePlayPos();
			mMusicHandler.postDelayed(this, 1000);
		}
	};

	public void UpdatePlayPos() {

		if (isSupportMetadata == true) {
			if (isSupportPlaybackpos == false) {
				mSeekBar.setProgress(0);
				mTextCurTime.setText("00:00:00");
			} else {
				if (mSeekBar.getMax() > 0 && isSupportPlaybackpos) {
					int iPlayTime = mSeekBar.getProgress();
					mSeekBar.setProgress(iPlayTime + 1);
					mTextCurTime.setText(getCurrentTime(String
							.valueOf((iPlayTime + 1) * 1000)));
				}
			}
		} else {
			mSeekBar.setProgress(0);
			mTextCurTime.setText("00:00:00");
		}
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);
		super.onBackPressed();
	}

	@Override
	public void updateMusicPlayCurrentTime(String currentTime, boolean isPlaying) {
		mTextCurTime.setText(getCurrentTime(currentTime));
		LogUtil.i(TAG, "updateMusicPlayCurrentTime - isPlaying = " + isPlaying);
	}

	@Override
	public void finishMusicActivity() {
		finishActivity();
	}

//	public void freshSeekBarTail(int progress) {
//		int mMax = mSeekBar.getMax();
//		int deltaX = 0;
//		if (mMax == 0 || progress == 0) {
//			mSeekTail.setVisibility(View.GONE);
//			return;
//		} else {
//			deltaX = (int) (458 * (progress / (float) mMax));
//			if (deltaX == 0) {
//				mSeekTail.setVisibility(View.GONE);
//				return;
//			} else {
//				mSeekTail.setVisibility(View.VISIBLE);
//			}
//		}
//		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mSeekTail
//				.getLayoutParams();
//
//		if (deltaX <= 30) {
//			lp.width = (int) (deltaX * (480f / 458f)) + 8;
//			lp.leftMargin = 3;
//		} else if (deltaX <= 50) {
//
//			lp.width = (int) (deltaX * (480f / 458f)) + 15;
//			lp.leftMargin = 3;
//		} else if (deltaX <= 165) {
//			lp.width = (int) (deltaX * (480f / 458f)) + 15;
//			lp.leftMargin = 0;
//
//		} else if ((int) (deltaX * (480f / 458f)) <= 170) {
//			lp.width = deltaX + 15;
//			lp.leftMargin = 0;
//
//		} else if (deltaX <= 175) {
//			lp.width = 185;
//			lp.leftMargin = deltaX - 171 + 11;
//
//		} else if (deltaX <= 200) {
//			lp.width = 185;
//			lp.leftMargin = deltaX - 171 + 9;
//
//		} else if (deltaX <= 265) {
//			lp.width = 185;
//			lp.leftMargin = deltaX - 171 + 7;
//
//		} else {
//			lp.width = 185;
//			lp.leftMargin = deltaX - 165;
//		}
//		lp.leftMargin = lp.leftMargin + ADD_LEFT_MARGIN;
//		lp.height = 30;
//		lp.topMargin = 22;
//		mSeekTail.setLayoutParams(lp);
//		mSeekTail.bringToFront();
//		mSeekTail.postInvalidate();
//	}

	private View.OnTouchListener prevListener = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				prevDown();
				LogUtil.i(TAG, "prevListener -- down");
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				prevUp();
				LogUtil.i(TAG, "prevListener -- up");
				break;

			default:
				break;
			}
			return false;
		}
	};

	/**
	 * 上一首按下
	 */
	private void prevDown() {
		LogUtil.i(TAG, " --- prevDown ");
		isNormalPrev = true;
		mHandler.removeMessages(LONG_CLICK_PREV);
		mHandler.sendEmptyMessageDelayed(LONG_CLICK_PREV, 1000);
	}

	/**
	 * 上一首抬起
	 */
	private void prevUp() {
		if (isNormalPrev) {
			LogUtil.i(TAG, " --- prevup ");
			mHandler.sendEmptyMessage(SHORT_CLICK_PREV);
			mHandler.removeMessages(LONG_CLICK_PREV);
		} else {
			mHandler.sendEmptyMessage(LONG_FAST_BACKWORD_CANCLE);
		}
		isNormalPrev = true;
	}

	private View.OnTouchListener nextListener = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			LogUtil.i(TAG, "OnTouchListener " + event.getAction());
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				nextDown();
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				nextUp();
				break;
			}
			return false;
		}
	};

	/**
	 * 下一曲按下
	 */
	private void nextDown() {
		isNormalNext = true;
		LogUtil.i(TAG, " --- nextDown ");
		mHandler.removeMessages(LONG_CLICK_NEXT);
		mHandler.sendEmptyMessageDelayed(LONG_CLICK_NEXT, 1000);
	}

	/**
	 * 下一曲抬起
	 */
	private void nextUp() {

		LogUtil.i(TAG, "nextUp --- nextUp ");
		if (isNormalNext) {
			LogUtil.i(TAG, " --- nextUp ");
			mHandler.removeMessages(LONG_CLICK_NEXT);
			mHandler.sendEmptyMessage(SHORT_CLICK_NEXT);
		} else {
			LogUtil.i(TAG, "LONG_CLICK_NEXT --- nextUp ");
			mHandler.sendEmptyMessage(LONG_FAST_FORWORD_CANCLE);
		}
		isNormalNext = true;
	}

	@Override
	public void updateBgBitmap(Bitmap bg) {
		if (bg != null) {
			Drawable drawable = new BitmapDrawable(bg);
			mImageBg.setBackgroundDrawable(drawable);
			// setWallPaperAlbumScreenbg();
		} else {
			mImageBg.setBackgroundResource(R.drawable.all_bg);
		}
	}

	@Override
	public void updateTextTipShow(boolean conn) {
		LogUtil.i(TAG, "updateTextTipShow " + conn);
		if (conn) {
			mTextTip.setText(getResources().getString(
					R.string.music_bluetooth_carlife_connect_tip));
		} else {
			unConnectLayout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onUsbDesconnet() {
		ivUSB.setImageResource(R.drawable.selector_icon_usb);
	}
}

package com.hsae.d531mc.bluetooth.music;

import java.util.ArrayList;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
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
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.anwsdk.service.AudioControl;
import com.hsae.autosdk.os.Soc;
import com.hsae.autosdk.os.SocConst.UsbDevices;
import com.hsae.autosdk.source.Source;
import com.hsae.autosdk.source.SourceConst.App;
import com.hsae.autosdk.util.LogUtil;
import com.hsae.d531mc.bluetooth.music.entry.MusicBean;
import com.hsae.d531mc.bluetooth.music.fragmet.BluetoothSettingFragment;
import com.hsae.d531mc.bluetooth.music.fragmet.MusicSwitchFragmet;
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
public class MusicMainActivity extends Activity implements ISubject, IMusicView, OnClickListener {

	enum Media {
		fm, am, usb, bt, ipod;
	}

	private static final String TAG = "MusicMainActivity";

	private static final String RADIO_PACKAGE = "com.hsae.d531mc.radio";
	private static final String RADIO_ACTIVITY_AM_FM = "com.hsae.d531mc.radio.RadioActivity";
	private static final String IPOD_PACKAGE = "com.hsae.d531mc.ipod";
	private static final String IPOD_ACTIVITY = "com.hsae.d531mc.ipod.view.MainActivity";
	private static final String USB_PACKAGE = "com.hsae.d531mc.usbmedia";
	private static final String USB_ACTIVITY = "com.hsae.d531mc.usbmedia.music.MusicPlayActivity";

	private static final int MSG_DRAWLAYOUT_SHOW = 111;
	private static final int MSG_ANIM = 1120;
	private static final int MSG_SWITCH = 1123;
	private boolean canSwitch = true;

	private MusicPersenter mPresenter;

	private ImageView ivFM, ivAM, ivUSB, ivBT, ivList;
	// private ImageView mBtnSettings;

	private ImageView ivAlbumCut;
	// private ImageView mCover;
	private ImageButton mBtnPrev;
	private ImageView mBtnPlay;
	private ImageButton mBtnNext;
	private ImageView mBtnHome;
	private TextView mTextTitle;
	private TextView mTextArtist;
	private TextView mTextCurTime;
	private TextView mTextTotalTime;
	private MySeekBar mSeekBar;
	private DrawerLayout mDrawerLayout;
	private FrameLayout mFrameLayout;
	private FragmentManager mFragmentManager;
	private MusicSwitchFragmet mFragmet;
	private boolean ismPlaying = false;
	private ImageView mImageShuffle;
	private ImageView mImageRepeat;
	private BluetoothSettingFragment mSettingFragment;
	private ImageView mImageBg;
	private ImageView mSeekTail;
	private TextView mTextTip;
	private boolean isFramShow = false;
	private boolean isSupportPlaybackpos = false;
	private boolean isSupportMetadata = false;
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

	private Handler mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_DRAWLAYOUT_SHOW:
				showFram(false);
				break;
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
				MusicMainActivity.this.notify(msgn, FLAG_RUN_MAIN_THREAD);
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
				MusicMainActivity.this.notify(msgp, FLAG_RUN_MAIN_THREAD);
				break;
			}
			return false;
		}
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.i("wangda", "MusicMainActivity -- onCreate starTime = " + System.currentTimeMillis());
		// 透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		// 透明导航栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

		setContentView(R.layout.music_main);

		initView();
		initMvp();
		LogUtil.i("wangda", "MusicMainActivity -- onCreate endTime = " + System.currentTimeMillis());
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
		// setHandPause false
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

		ivFM = (ImageView) findViewById(R.id.btn_source_fm);
		ivAM = (ImageView) findViewById(R.id.btn_source_am);
		ivUSB = (ImageView) findViewById(R.id.btn_source_usb);
		ivBT = (ImageView) findViewById(R.id.btn_source_bt);
		ivList = (ImageView) findViewById(R.id.btn_playlist);
		ivAlbumCut = (ImageView) findViewById(R.id.music_defaultcover);

		mBtnPrev = (ImageButton) findViewById(R.id.btn_prev);
		mBtnPlay = (ImageView) findViewById(R.id.btn_play);
		mBtnNext = (ImageButton) findViewById(R.id.btn_next);
		mSeekBar = (MySeekBar) findViewById(R.id.music_seekbar);
		mTextTitle = (TextView) findViewById(R.id.music_title);
		mTextArtist = (TextView) findViewById(R.id.music_artist);
		mTextCurTime = (TextView) findViewById(R.id.music_currenttime);
		mTextTotalTime = (TextView) findViewById(R.id.music_totaltime);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.music_drawerlayout);
		mDrawerLayout.setScrimColor(this.getResources().getColor(R.color.transparent));
		mFrameLayout = (FrameLayout) findViewById(R.id.bluetooth_music_frame);
		mBtnHome = (ImageView) findViewById(R.id.btn_home);
		mImageShuffle = (ImageView) findViewById(R.id.btn_shuffle);
		mImageRepeat = (ImageView) findViewById(R.id.btn_repeat);
		mImageBg = (ImageView) findViewById(R.id.default_screenbg);
		mSeekTail = (ImageView) findViewById(R.id.seekbar_tail);
		mTextTip = (TextView) findViewById(R.id.text_disconnect_tip);
		mFraInfo = (FrameLayout) findViewById(R.id.layout_musicinfo);
		mFraControl = (FrameLayout) findViewById(R.id.layout_control);
		mFragmet = (MusicSwitchFragmet) MusicSwitchFragmet.getInstance(this);
		mSettingFragment = (BluetoothSettingFragment) BluetoothSettingFragment.getInstance(this);
		mFragmentManager = getFragmentManager();
		mBtnPrev.setOnTouchListener(prevListener);
		mBtnPlay.setOnClickListener(this);
		mBtnNext.setOnTouchListener(nextListener);
		mImageRepeat.setOnClickListener(this);
		mImageShuffle.setOnClickListener(this);
		mBtnHome.setOnClickListener(this);
		findViewById(R.id.right_bg).setOnClickListener(this);

		ivAM.setOnClickListener(this);
		ivFM.setOnClickListener(this);
		ivUSB.setOnClickListener(this);
		ivBT.setOnClickListener(this);
		ivList.setOnClickListener(this);

		mDrawerLayout.setOnTouchListener(touchListener);
		mDrawerLayout.setDrawerListener(mDrawerListener);

		rDrawable = (RotateDrawable) ivAlbumCut.getBackground();
		rDrawable.setLevel(0);
	}

	int level = 0;
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_ANIM:
				if (level >= 10000) {
					level = 0;
				}
				rDrawable.setLevel(level);
				level += 10;
				handler.sendEmptyMessageDelayed(MSG_ANIM, 20);
				break;
			case MSG_SWITCH:
				canSwitch = true;
				break;
			default:
				break;
			}

		};
	};

	private void resetAnim() {
		level = 0;
	}

	private void startAnim() {
		if (!handler.hasMessages(MSG_ANIM)) {
			handler.sendEmptyMessage(MSG_ANIM);
		}
	}

	private void pauseAnim() {
		if (handler.hasMessages(MSG_ANIM)) {
			handler.removeMessages(MSG_ANIM);
		}
	}

	DrawerListener mDrawerListener = new DrawerListener() {

		@Override
		public void onDrawerStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onDrawerSlide(View arg0, float arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onDrawerOpened(View arg0) {
			isFramShow = true;

		}

		@Override
		public void onDrawerClosed(View arg0) {
			isFramShow = false;
		}
	};

	private OnTouchListener touchListener = new OnTouchListener() {

		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			float x = event.getX();
			float y = event.getY();
			if (event.getAction() == MotionEvent.ACTION_UP) {
				// ivAM.setImageResource(R.drawable.ic_item_am);
				// ivFM.setImageResource(R.drawable.ic_item_fm);
				// ivUSB.setImageResource(R.drawable.ic_item_usb);
				if (0 < x && x < 170 && 610 < y && y < 720) {
					mBtnHome.performClick();
				}

				// else if (0 < x && x < 85 && 360< y && y < 430) {
				// LogUtil.i(TAG, "touchListener usb");
				// switchSource(Media.usb);
				// }else if (0 < x && x < 85 && 260< y && y < 340) {
				// LogUtil.i(TAG, "touchListener fM");
				// switchSource(Media.fm);
				// }else if (0 < x && x < 85 && 136< y && y < 240) {
				// LogUtil.i(TAG, "touchListener aM");
				// switchSource(Media.am);
				// }
			}

			// else if (event.getAction() == MotionEvent.ACTION_DOWN ||
			// event.getAction() == MotionEvent.ACTION_MOVE ) {
			// LogUtil.i(TAG, "touchListener ACTION_DOWN");
			// if (0 < x && x < 85 && 360< y && y < 430) {
			// ivAM.setImageResource(R.drawable.ic_item_am);
			// ivFM.setImageResource(R.drawable.ic_item_fm);
			// ivUSB.setImageResource(R.drawable.ic_item_usb_down);
			// }else if (0 < x && x < 85 && 260< y && y < 340) {
			// ivAM.setImageResource(R.drawable.ic_item_am);
			// ivFM.setImageResource(R.drawable.ic_item_fm_down);
			// ivUSB.setImageResource(R.drawable.ic_item_usb);
			// }else if (0 < x && x < 85 && 136< y && y < 240) {
			// ivAM.setImageResource(R.drawable.ic_item_am_down);
			// ivFM.setImageResource(R.drawable.ic_item_fm);
			// ivUSB.setImageResource(R.drawable.ic_item_usb);
			// }else{
			// ivAM.setImageResource(R.drawable.ic_item_am);
			// ivFM.setImageResource(R.drawable.ic_item_fm);
			// ivUSB.setImageResource(R.drawable.ic_item_usb);
			// }
			// }
			return false;
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.i(TAG, "bluetoothmusic onResume");
		ivBT.setSelected(true);

		Message msg = Message.obtain();
		msg.what = MusicActionDefine.ACTION_A2DP_REQUEST_AUDIO_FOCUSE;
		this.notify(msg, FLAG_RUN_SYNC);

		boolean isUsb = !isIpodConnected();
		ivUSB.setImageResource(isUsb ? R.drawable.selector_source_usb : R.drawable.selector_source_ipod);
	}

	/**
	 * 显示侧边栏
	 * 
	 * @param flag
	 *            flag : true 显示音源切换 false 显示蓝牙设置
	 */
	private void showFram(boolean flag) {
		if (!hasWindowFocus()) {
			LogUtil.w(TAG, "activity is not on focus now ");
			return;
		}

		if (flag) {
			mFragmentManager.beginTransaction().replace(R.id.bluetooth_music_frame, mFragmet).commitAllowingStateLoss();
		} else {
			mFragmentManager.beginTransaction().replace(R.id.bluetooth_music_frame, mSettingFragment)
					.commitAllowingStateLoss();
		}
		isFramShow = true;
		mDrawerLayout.openDrawer(mFrameLayout); // 显示左侧
	}

	/**
	 * 关闭侧边栏
	 */
	public void closeMusicSwitch() {
		isFramShow = false;
		mDrawerLayout.closeDrawer(mFrameLayout);
	}

	@Override
	protected void onPause() {
		super.onPause();
		pauseAnim();
		if (mMusicHandler != null)
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
		// stopAnimate();
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
		case R.id.btn_source_fm:
			switchSource(Media.fm);
			break;
		case R.id.btn_source_am:
			switchSource(Media.am);
			break;
		case R.id.btn_source_usb:
			switchSource(Media.usb);
			break;
		case R.id.btn_source_bt:
			switchSource(Media.bt);
			break;
		case R.id.btn_playlist:
		case R.id.right_bg:
			showFram(false);
			break;

		// case R.id.btn_source_settings:
		// showFram(true);
		// break;
		// case R.id.btn_bt_settings:
		// showFram(false);
		// break;
		case R.id.btn_play:
			Message msgl = Message.obtain();
			if (ismPlaying) {
				msgl.what = MusicActionDefine.ACTION_A2DP_PAUSE;
			} else {
				msgl.what = MusicActionDefine.ACTION_A2DP_PLAY;
			}
			this.notify(msgl, FLAG_RUN_SYNC);
			break;
		case R.id.btn_repeat:
			Message msgr = Message.obtain();
			msgr.what = MusicActionDefine.ACTION_A2DP_REPEAT_MODEL;
			Bundle rBundle = new Bundle();
			rBundle.putInt("currentRepeatModel", mRepeatMode);
			msgr.setData(rBundle);
			this.notify(msgr, FLAG_RUN_SYNC);
			break;
		case R.id.btn_shuffle:
			Message msgs = Message.obtain();
			msgs.what = MusicActionDefine.ACTION_A2DP_SHUFFLE_MODEL;
			Bundle sBundle = new Bundle();
			sBundle.putInt("currentShuffleModel", mShuffleMode);
			msgs.setData(sBundle);
			this.notify(msgs, FLAG_RUN_SYNC);
			break;
		case R.id.btn_home:
			closeMusicSwitch();
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
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
				ivAM.setSelected(true);
				ivFM.setSelected(false);
				ivUSB.setSelected(false);
				ivBT.setSelected(false);
				bundle.putInt("band", 0x01);
				startOtherAPP(App.RADIO, RADIO_PACKAGE, RADIO_ACTIVITY_AM_FM, bundle);
				closeMusicSwitch();
			} else if (source.equals(Media.fm)) {
				ivAM.setSelected(false);
				ivFM.setSelected(true);
				ivUSB.setSelected(false);
				ivBT.setSelected(false);
				bundle.putInt("band", 0x03);
				startOtherAPP(App.RADIO, RADIO_PACKAGE, RADIO_ACTIVITY_AM_FM, bundle);
				closeMusicSwitch();
			} else if (source.equals(Media.usb)) {
				ivAM.setSelected(false);
				ivFM.setSelected(false);
				ivUSB.setSelected(true);
				ivBT.setSelected(false);

				boolean isUsb = isUsbConnected() || !isIpodConnected();
				App app = isUsb ? App.USB_MUSIC : App.IPOD_MUSIC;
				String strPackage = isUsb ? USB_PACKAGE : IPOD_PACKAGE;
				String strClass = isUsb ? USB_ACTIVITY : IPOD_ACTIVITY;
				startOtherAPP(app, strPackage, strClass, bundle);
				closeMusicSwitch();
			} else if (source.equals(Media.bt)) {
				ivAM.setSelected(false);
				ivFM.setSelected(false);
				ivUSB.setSelected(false);
				ivBT.setSelected(true);
				closeMusicSwitch();
			}
		}else{
			LogUtil.i(TAG, "switchSource can not switch source twice in 500 ms!");
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

	public void startOtherAPP(App app, String appId, String activityName, Bundle bundle) {
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
			packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
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
			if (isFramShow) {
				closeMusicSwitch();
				return false;
			} else {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.addCategory(Intent.CATEGORY_HOME);
				startActivity(intent);
				finishActivity();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void updateViewByConnectStatus(int status) {
		LogUtil.i(TAG, "updateViewByConnectStatus " + status);
		if (status == 0) {
			updateViewShow(false, false);
			mSeekTail.setVisibility(View.GONE);
			mTextTotalTime.setText("00:00");
			mTextCurTime.setText("00:00");
			mTextTitle.setText(getResources().getString(R.string.music_matedate_unsupport));
			mTextTitle.setText(getResources().getString(R.string.music_matedate_unsupport));
			mSeekBar.setMax(0);
			UpdatePlayerModeSetting(AudioControl.PLAYER_ATTRIBUTE_REPEAT, AudioControl.PLAYER_REPEAT_MODE_OFF);
			UpdatePlayerModeSetting(AudioControl.PLAYER_ATTRIBUTE_SHUFFLE, AudioControl.PLAYER_SHUFFLE_OFF);
			mRepeatAllowedlist.clear();
			mShuffleAllowedlist.clear();
			LogUtil.i(TAG, "Bluetooth A2DP disconnected");
			mHandler.sendEmptyMessageDelayed(MSG_DRAWLAYOUT_SHOW, 500);
		} else if (status == 1) {
			updateViewShow(true, false);
			if (isFramShow) {
				closeMusicSwitch();
			}
			LogUtil.i(TAG, "Bluetooth A2DP connected");
		} else if (status == -1) {
			// carlife is connected
			updateViewShow(false, true);
		}
		// else if (status == -2) {
		// // carlife is not connected
		// updateViewShow(true, true);
		// mSeekTail.setVisibility(View.GONE);
		// mTextTotalTime.setText("00:00");
		// mTextCurTime.setText("00:00");
		// mSeekBar.setMax(0);
		// UpdatePlayerModeSetting(AudioControl.PLAYER_ATTRIBUTE_REPEAT,
		// AudioControl.PLAYER_REPEAT_MODE_OFF);
		// UpdatePlayerModeSetting(AudioControl.PLAYER_ATTRIBUTE_SHUFFLE,
		// AudioControl.PLAYER_SHUFFLE_OFF);
		// mRepeatAllowedlist.clear();
		// mShuffleAllowedlist.clear();
		// LogUtil.i(TAG, "Bluetooth A2DP disconnected");
		// }
	}

	private void updateViewShow(boolean flag, boolean isFromCarlife) {
		LogUtil.i(TAG, "updateViewShow : flag = " + flag + "isFromCarlife = " + isFromCarlife);
		mBtnPrev.setEnabled(flag);
		mBtnPlay.setEnabled(flag);
		mBtnNext.setEnabled(flag);
		mImageRepeat.setEnabled(flag);
		mImageShuffle.setEnabled(flag);
		mSeekBar.setEnabled(flag);
		if (flag) {
			mFraInfo.setVisibility(View.VISIBLE);
			mTextTip.setVisibility(View.GONE);
			mFraControl.setVisibility(View.VISIBLE);
		} else {
			mFraInfo.setVisibility(View.GONE);
			mFraControl.setVisibility(View.GONE);
			mTextTip.setVisibility(View.VISIBLE);

			if (!isFromCarlife) {
				mTextTip.setText(getResources().getString(R.string.music_bluetooth_disconnect_tip));
			} else {
				mTextTip.setText(getResources().getString(R.string.music_bluetooth_carlife_connect_tip));
			}
			ismPlaying = false;
			mMusicHandler.removeCallbacks(updateMusicPlayTimer);
			mBtnPlay.setImageDrawable(getResources().getDrawable(R.drawable.btn_music_play));
		}
	}

	@Override
	public void updatePlayBtnByStatus(boolean flag) {
		LogUtil.i(TAG, "Activity updatePlayBtnByStatus -- flag = " + flag);
		if (flag) {
			ismPlaying = true;
			mBtnPlay.setImageDrawable(getResources().getDrawable(R.drawable.btn_music_pause));
			startAnim();
		} else {
			pauseAnim();
			ismPlaying = false;
			mMusicHandler.removeCallbacks(updateMusicPlayTimer);
			mBtnPlay.setImageDrawable(getResources().getDrawable(R.drawable.btn_music_play));
		}
	}

	private String musicName = "";

	@Override
	public void updateMusicDataInfo(MusicBean bean, boolean isSupport) {
		isSupportMetadata = isSupport;
		LogUtil.i(TAG, "Activity updateMusicDataInfo -- isSupport = " + isSupport);
		if (null != bean) {
			LogUtil.i(TAG, " name  = " + bean.getTitle() + " -- isSupport = " + isSupport);
			if ("".equals(bean.getTitle())) {
				musicName = "";
				mTextTitle.setText(getResources().getString(R.string.music_matedate_unsupport));
			} else {
				if (!musicName.equals(bean.getTitle())) {
					musicName = bean.getTitle();
					resetAnim();
					mTextTitle.setText(bean.getTitle());
				} else {
				}
			}
			if ("".equals(bean.getAtrist())) {
				mTextArtist.setText(getResources().getString(R.string.music_matedate_unsupport));
			} else {
				mTextArtist.setText(bean.getAtrist());
			}
			if ("".equals(bean.getTotalTime())) {
				mTextTotalTime.setText("00:00");
			} else {
				mTextTotalTime.setText(getTotalTime(bean.getTotalTime()));
			}

		} else {
			mTextTitle.setText(getResources().getString(R.string.music_matedate_unsupport));
			mTextArtist.setText(getResources().getString(R.string.music_matedate_unsupport));
			mTextTotalTime.setText("00:00");
			mTextCurTime.setText("00:00");
			mSeekBar.setMax(0);
			mSeekTail.setVisibility(View.GONE);
		}
	}

	private String getTotalTime(String nTime) {
		String timeStr = "00:00";

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
		long minute = time / 1000 / 60;
		long s = time / 1000 % 60;
		String mm = null;
		String ss = null;
		if (minute < 10)
			mm = "0" + minute;
		else
			mm = minute + "";

		if (s < 10)
			ss = "0" + s;
		else
			ss = "" + s;

		return mm + ":" + ss;
	}

	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]+");
		return pattern.matcher(str).matches();
	}

	private String getCurrentTime(String nTime) {
		if (nTime.equals("-1") && isSupportPlaybackpos == false) {
			return "00:00";
		} else {
			if (isNumeric(nTime) == true) {
				isSupportPlaybackpos = true;
				try {
					int pos = Integer.valueOf(nTime) / 1000;
					mSeekBar.setProgress(pos);
					freshSeekBarTail(pos);
					return toTime(Integer.valueOf(nTime));
				} catch (NumberFormatException e) {

					return "00:00";
				}
			} else {
				return "00:00";
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
				mTextCurTime.setText("00:00");
			} else {
				if (mSeekBar.getMax() > 0 && isSupportPlaybackpos) {
					int iPlayTime = mSeekBar.getProgress();
					mSeekBar.setProgress(iPlayTime + 1);
					mTextCurTime.setText(getCurrentTime(String.valueOf((iPlayTime + 1) * 1000)));
				}
			}
		} else {
			mSeekBar.setProgress(0);
			mTextCurTime.setText("00:00");
		}
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);
		finishActivity();
		super.onBackPressed();
	}

	@Override
	public void updateMusicPlayCurrentTime(String currentTime, boolean isPlaying) {
		// if (isSupportMetadata) {
		mTextCurTime.setText(getCurrentTime(currentTime));
		LogUtil.i(TAG, "updateMusicPlayCurrentTime - isPlaying = " + isPlaying);
		// }
		// if (isPlaying && (mMusicHandler != null)) {
		// mMusicHandler.removeCallbacks(updateMusicPlayTimer);
		// mMusicHandler.postDelayed(updateMusicPlayTimer, 1000);
		// }
		// if (!isPlaying) {
		// mMusicHandler.removeCallbacks(updateMusicPlayTimer);
		// }
	}

	private ArrayList<Integer> mRepeatAllowedlist = new ArrayList<Integer>();
	private ArrayList<Integer> mShuffleAllowedlist = new ArrayList<Integer>();

	@Override
	public void updateRepeatAllowList(ArrayList<Integer> allowList) {
		mRepeatAllowedlist.clear();
		mRepeatAllowedlist.addAll(allowList);
		LogUtil.i(TAG, "mRepeatAllowedlist size = " + mRepeatAllowedlist.size());
		if (mRepeatAllowedlist.size() <= 0) {
			mImageRepeat.setEnabled(false);
		} else {
			mImageRepeat.setEnabled(true);
		}
	}

	@Override
	public void updateShuffleAllowList(ArrayList<Integer> allowList) {
		mShuffleAllowedlist.clear();
		mShuffleAllowedlist.addAll(allowList);
		LogUtil.i(TAG, "mShuffleAllowedlist size = " + mShuffleAllowedlist.size());
		if (mShuffleAllowedlist.size() <= 0) {
			mImageShuffle.setEnabled(false);
		} else {
			mImageShuffle.setEnabled(true);
		}
	}

	int mShuffleMode = AudioControl.PLAYER_SHUFFLE_OFF;
	int mRepeatMode = AudioControl.PLAYER_REPEAT_MODE_SINGLE_TRACK;

	@Override
	public void UpdatePlayerModeSetting(int nAttrID, int nAttrValue) {
		switch (nAttrID) {

		case AudioControl.PLAYER_ATTRIBUTE_REPEAT:// 2
			if (mRepeatMode != nAttrValue) {
				mRepeatMode = nAttrValue;
				switch (nAttrValue) {
				case AudioControl.PLAYER_REPEAT_MODE_OFF:
					mImageRepeat.setImageDrawable(getResources().getDrawable(R.drawable.btn_music_repeat_order));
					break;
				case AudioControl.PLAYER_REPEAT_MODE_SINGLE_TRACK:
					mImageRepeat.setImageDrawable(getResources().getDrawable(R.drawable.btn_music_repeat_singel));
					break;
				case AudioControl.PLAYER_REPEAT_MODE_ALL_TRACK:
					mImageRepeat.setImageDrawable(getResources().getDrawable(R.drawable.btn_music_repeat_all));
					break;
				case AudioControl.PLAYER_REPEAT_MODE_GROUP:
					mImageRepeat.setImageDrawable(getResources().getDrawable(R.drawable.btn_music_repeat_all));
					break;
				}
			}
			break;
		case AudioControl.PLAYER_ATTRIBUTE_SHUFFLE:// 3

			if (mShuffleMode != nAttrValue) {
				mShuffleMode = nAttrValue;
				switch (nAttrValue) {
				case AudioControl.PLAYER_SHUFFLE_OFF:
					mImageShuffle.setImageDrawable(getResources().getDrawable(R.drawable.btn_music_shuffle_close));
					break;
				case AudioControl.PLAYER_SHUFFLE_ALL_TRACK:
					mImageShuffle.setImageDrawable(getResources().getDrawable(R.drawable.btn_music_shuffle_open));
					break;
				case AudioControl.PLAYER_SHUFFLE_GROUP:
					mImageShuffle.setImageDrawable(getResources().getDrawable(R.drawable.btn_music_shuffle_open));
					break;
				}
			}
			break;
		}
	}

	@Override
	public void updateShuffleAllowArray(int[] AllowArray, int num) {
		mShuffleAllowedlist.clear();
		for (int i = 0; i < num; i++) {
			mShuffleAllowedlist.add(AllowArray[i]);
		}
		if (mShuffleAllowedlist.size() <= 0) {
			mImageShuffle.setEnabled(false);
		} else {
			mImageShuffle.setEnabled(true);
		}
	}

	@Override
	public void updateRepeatAllowArray(int[] AllowArray, int num) {
		mRepeatAllowedlist.clear();
		for (int i = 0; i < num; i++) {
			mRepeatAllowedlist.add(AllowArray[i]);
		}
		if (mRepeatAllowedlist.size() <= 0) {
			mImageRepeat.setEnabled(false);
		} else {
			mImageRepeat.setEnabled(true);
		}
	}

	@Override
	public void finishMusicActivity() {
		finishActivity();
	}

	public void freshSeekBarTail(int progress) {
		int mMax = mSeekBar.getMax();
		int deltaX = 0;
		if (mMax == 0 || progress == 0) {
			mSeekTail.setVisibility(View.GONE);
			return;
		} else {
			deltaX = (int) (458 * (progress / (float) mMax));
			if (deltaX == 0) {
				mSeekTail.setVisibility(View.GONE);
				return;
			} else {
				mSeekTail.setVisibility(View.VISIBLE);
			}
		}
		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mSeekTail.getLayoutParams();

		if (deltaX <= 30) {
			lp.width = (int) (deltaX * (480f / 458f)) + 8;
			lp.leftMargin = 3;
		} else if (deltaX <= 50) {

			lp.width = (int) (deltaX * (480f / 458f)) + 15;
			lp.leftMargin = 3;
		} else if (deltaX <= 165) {
			lp.width = (int) (deltaX * (480f / 458f)) + 15;
			lp.leftMargin = 0;

		} else if ((int) (deltaX * (480f / 458f)) <= 170) {
			lp.width = deltaX + 15;
			lp.leftMargin = 0;

		} else if (deltaX <= 175) {
			lp.width = 185;
			lp.leftMargin = deltaX - 171 + 11;

		} else if (deltaX <= 200) {
			lp.width = 185;
			lp.leftMargin = deltaX - 171 + 9;

		} else if (deltaX <= 265) {
			lp.width = 185;
			lp.leftMargin = deltaX - 171 + 7;

		} else {
			lp.width = 185;
			lp.leftMargin = deltaX - 165;
		}
		lp.height = 30;
		lp.topMargin = 22;
		mSeekTail.setLayoutParams(lp);
		mSeekTail.bringToFront();
		mSeekTail.postInvalidate();
	}

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
			mImageBg.setBackgroundResource(R.drawable.bg_music_main);
		}
	}

	@Override
	public void updateTextTipShow(boolean conn) {
		LogUtil.i(TAG, "updateTextTipShow " + conn);
		if (conn) {
			mTextTip.setText(getResources().getString(R.string.music_bluetooth_carlife_connect_tip));
		} else {
			mTextTip.setText(getResources().getString(R.string.music_bluetooth_disconnect_tip));
		}
	}

	@Override
	public void onUsbDesconnet() {
		ivUSB.setImageResource(R.drawable.selector_source_usb);
	}
}

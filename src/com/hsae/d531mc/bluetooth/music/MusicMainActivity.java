package com.hsae.d531mc.bluetooth.music;

import java.util.ArrayList;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
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
public class MusicMainActivity extends Activity implements ISubject,
		IMusicView, OnClickListener {

	private static final String TAG = "MusicMainActivity";
	private MusicPersenter mPresenter;

	private ImageView mBtnMusicSwith;
	private ImageView mBtnSettings;
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
	private static final int SHORT_CLICK_PREV = 3;
	private static final int SHORT_CLICK_NEXT = 4;
	private boolean isNormalPrev = true;
	private boolean isNormalNext = true;
	private FrameLayout mFraInfo;
	private FrameLayout mFraControl;
	private Handler mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case LONG_CLICK_NEXT:
				isNormalNext = false;
				Message msgln = Message.obtain();
				msgln.what = MusicActionDefine.ACTION_A2DP_FASTFORWORD;
				MusicMainActivity.this.notify(msgln, FLAG_RUN_SYNC);
				LogUtil.i(TAG, " --- next long press ---");
				if (!isNormalNext) {
					mHandler.sendEmptyMessageDelayed(LONG_CLICK_NEXT, 1500);
				}
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
				if (!isNormalPrev) {
					mHandler.sendEmptyMessageDelayed(LONG_CLICK_PREV, 1500);
				}
				break;
			case SHORT_CLICK_PREV:
				LogUtil.i(TAG, " --- prev short press --- ");
				Message msgp = Message.obtain();
				msgp.what = MusicActionDefine.ACTION_A2DP_PREV;
				MusicMainActivity.this.notify(msgp, FLAG_RUN_MAIN_THREAD);
				break;

			default:
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
		getWindow()
				.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		// 透明导航栏
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		setContentView(R.layout.music_main);
		initView();
		initMvp();
		LogUtil.i("wangda", "MusicMainActivity -- onCreate endTime = " + System.currentTimeMillis());
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
		mBtnMusicSwith = (ImageView) findViewById(R.id.btn_source_settings);
		mBtnSettings = (ImageView) findViewById(R.id.btn_bt_settings);
		mBtnPrev = (ImageButton) findViewById(R.id.btn_prev);
		mBtnPlay = (ImageView) findViewById(R.id.btn_play);
		mBtnNext = (ImageButton) findViewById(R.id.btn_next);
		mSeekBar = (MySeekBar) findViewById(R.id.music_seekbar);
		mTextTitle = (TextView) findViewById(R.id.music_title);
		mTextArtist = (TextView) findViewById(R.id.music_artist);
		mTextCurTime = (TextView) findViewById(R.id.music_currenttime);
		mTextTotalTime = (TextView) findViewById(R.id.music_totaltime);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.music_drawerlayout);
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
		mSettingFragment = (BluetoothSettingFragment) BluetoothSettingFragment
				.getInstance(this);
		mFragmentManager = getFragmentManager();
		mBtnMusicSwith.setOnClickListener(this);
		mBtnPrev.setOnTouchListener(prevListener);
		mBtnPlay.setOnClickListener(this);
		mBtnNext.setOnTouchListener(nextListener);
		mImageRepeat.setOnClickListener(this);
		mImageShuffle.setOnClickListener(this);
		mBtnHome.setOnClickListener(this);
		mBtnSettings.setOnClickListener(this);
		mDrawerLayout.setOnTouchListener(touchListener);
		mDrawerLayout.setDrawerListener(mDrawerListener);
//		mFragmentManager.beginTransaction()
//		.replace(R.id.bluetooth_music_frame, mFragmet).commit();
		
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
			if (event.getAction() == MotionEvent.ACTION_UP) {
				float x = event.getX();
				float y = event.getY();
				if (0 < x && x < 170 && 610 < y && y < 720) {
					mBtnHome.performClick();
				}
			}
			return false;
		}
	};

	@Override
	protected void onResume() {
		Message msg = Message.obtain();
		msg.what = MusicActionDefine.ACTION_A2DP_REQUEST_AUDIO_FOCUSE;
		this.notify(msg, FLAG_RUN_SYNC);
		super.onResume();
	}

	@Override
	protected void onPause() {
		Message msg = Message.obtain();
		msg.what = MusicActionDefine.ACTION_A2DP_ACTIVITY_PAUSE;
		this.notify(msg, FLAG_RUN_SYNC);
		super.onPause();
	}

	private void showFram(boolean flag) {
		if (flag) {
			mFragmentManager.beginTransaction()
					.replace(R.id.bluetooth_music_frame, mFragmet).commit();
		} else {
			mFragmentManager.beginTransaction()
					.replace(R.id.bluetooth_music_frame, mSettingFragment)
					.commit();
		}
		isFramShow = true;
		mDrawerLayout.openDrawer(mFrameLayout); // 显示左侧
	}

	public void closeMusicSwitch() {
		isFramShow = false;
		mDrawerLayout.closeDrawer(mFrameLayout);
	}

	@Override
	protected void onStop() {
		
		super.onStop();
	}
	
	public void finishActivity(){
		if (mMusicHandler != null)
			mMusicHandler.removeCallbacks(updateMusicPlayTimer);
		Message msg = Message.obtain();
		msg.what = MusicActionDefine.ACTION_APP_EXIT;
		this.notify(msg, FLAG_RUN_SYNC);
		this.detach(mPresenter);
		this.finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
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
		case R.id.btn_source_settings:
			showFram(true);
			break;
		case R.id.btn_bt_settings:
			showFram(false);
			break;
		case R.id.btn_play:
			Message msgl = Message.obtain();
			if (ismPlaying) {
				msgl.what = MusicActionDefine.ACTION_A2DP_PAUSE;
			} else {
				msgl.what = MusicActionDefine.ACTION_A2DP_PLAY;
			}
			this.notify(msgl, FLAG_RUN_MAIN_THREAD);
			break;
		case R.id.btn_repeat:
			Message msgr = Message.obtain();
			msgr.what = MusicActionDefine.ACTION_A2DP_REPEAT_MODEL;
			Bundle rBundle = new Bundle();
			rBundle.putInt("currentRepeatModel", mRepeatMode);
			rBundle.putIntegerArrayList("repeatList", mRepeatAllowedlist);
			msgr.setData(rBundle);
			this.notify(msgr, FLAG_RUN_MAIN_THREAD);
			break;
		case R.id.btn_shuffle:
			Message msgs = Message.obtain();
			msgs.what = MusicActionDefine.ACTION_A2DP_SHUFFLE_MODEL;
			Bundle sBundle = new Bundle();
			sBundle.putInt("currentShuffleModel", mShuffleMode);
			sBundle.putIntegerArrayList("shuffleList", mShuffleAllowedlist);
			msgs.setData(sBundle);
			this.notify(msgs, FLAG_RUN_MAIN_THREAD);
			break;
		case R.id.btn_home:
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
			finishActivity();
			break;
		default:
			break;
		}
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
		if (status == 0) {
			updateViewShow(false);
			mSeekTail.setVisibility(View.GONE);
			mTextTotalTime.setText("00:00");
			mTextCurTime.setText("00:00");
			mSeekBar.setMax(0);
			UpdatePlayerModeSetting(AudioControl.PLAYER_ATTRIBUTE_REPEAT,
					AudioControl.PLAYER_REPEAT_MODE_OFF);
			UpdatePlayerModeSetting(AudioControl.PLAYER_ATTRIBUTE_SHUFFLE,
					AudioControl.PLAYER_SHUFFLE_OFF);
			mRepeatAllowedlist.clear();
			mShuffleAllowedlist.clear();
			LogUtil.i(TAG, "Bluetooth A2DP disconnected");
		} else if (status == 1) {
			updateViewShow(true);
			if (isFramShow) {
				closeMusicSwitch();
			}
			LogUtil.i(TAG, "Bluetooth A2DP connected");
		}
	}

	private void updateViewShow(boolean flag) {
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
			ismPlaying = false;
			mMusicHandler.removeCallbacks(updateMusicPlayTimer);
			mBtnPlay.setImageDrawable(getResources().getDrawable(
					R.drawable.btn_music_play));
			
		}
	}

	@Override
	public void updatePlayBtnByStatus(boolean flag) {
		LogUtil.i(TAG, "Activity updatePlayBtnByStatus -- flag = " + flag);
		if (flag) {
			ismPlaying = true;
			mBtnPlay.setImageDrawable(getResources().getDrawable(
					R.drawable.btn_music_pause));
		} else {
			ismPlaying = false;
			mMusicHandler.removeCallbacks(updateMusicPlayTimer);
			mBtnPlay.setImageDrawable(getResources().getDrawable(
					R.drawable.btn_music_play));
		}
	}

	@Override
	public void updateMusicDataInfo(MusicBean bean, boolean isSupport) {
		isSupportMetadata = isSupport;
		LogUtil.i(TAG, "Activity updateMusicDataInfo -- isSupport = "
				+ isSupport);
		if (null != bean) {
			if ("".equals(bean.getTitle())) {
				mTextTitle.setText(getResources().getString(
						R.string.music_matedate_unsupport));
			} else {
				mTextTitle.setText(bean.getTitle());
			}
			if ("".equals(bean.getAtrist())) {
				mTextArtist.setText(getResources().getString(
						R.string.music_matedate_unsupport));
			} else {
				mTextArtist.setText(bean.getAtrist());
			}
			if ("".equals(bean.getTotalTime())) {
				mTextTotalTime.setText("00:00");
			} else {
				mTextTotalTime.setText(getTotalTime(bean.getTotalTime()));
			}
		} else {
			mTextTitle.setText(getResources().getString(
					R.string.music_matedate_unsupport));
			mTextArtist.setText(getResources().getString(
					R.string.music_matedate_unsupport));
			mTextTotalTime.setText("00:00");
			mTextCurTime.setText("00:00");
			mSeekBar.setMax(0);
		}
	}

	private String getTotalTime(String nTime) {
		if (nTime.equals("-1")) {
			return "00:00";
		} else {
			if (isNumeric(nTime) == true) {
				int iMax = Integer.valueOf(nTime) / 1000;
				mSeekBar.setMax(iMax);
				return toTime(Integer.valueOf(nTime));
			} else {
				return "00:00";
			}
		}
	}

	private String toTime(int time) {
		int minute = time / 1000 / 60;
		int s = time / 1000 % 60;
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
				int pos = Integer.valueOf(nTime) / 1000;
				mSeekBar.setProgress(pos);
				freshSeekBarTail(pos);
				return toTime(Integer.valueOf(nTime));
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
					mTextCurTime.setText(getCurrentTime(String
							.valueOf((iPlayTime + 1) * 1000)));
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
//		if (isSupportMetadata) {
			mTextCurTime.setText(getCurrentTime(currentTime));
			LogUtil.i(TAG, "updateMusicPlayCurrentTime - isPlaying = " + isPlaying);
//		}
//		if (isPlaying && (mMusicHandler != null)) {
//			mMusicHandler.removeCallbacks(updateMusicPlayTimer);
//			mMusicHandler.postDelayed(updateMusicPlayTimer, 1000);
//		}
//		if (!isPlaying) {
//			mMusicHandler.removeCallbacks(updateMusicPlayTimer);
//		}
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
		LogUtil.i(TAG,
				"mShuffleAllowedlist size = " + mShuffleAllowedlist.size());
		if (mShuffleAllowedlist.size() <= 0) {
			mImageShuffle.setEnabled(false);
		} else {
			mImageShuffle.setEnabled(true);
		}
	}

	int mShuffleMode = AudioControl.PLAYER_SHUFFLE_OFF;
	int mRepeatMode = AudioControl.PLAYER_REPEAT_MODE_OFF;

	@Override
	public void UpdatePlayerModeSetting(int nAttrID, int nAttrValue) {
		switch (nAttrID) {

		case AudioControl.PLAYER_ATTRIBUTE_REPEAT:// 2
			if (mRepeatMode != nAttrValue) {
				mRepeatMode = nAttrValue;
				switch (nAttrValue) {
				case AudioControl.PLAYER_REPEAT_MODE_OFF:
					mImageRepeat
							.setImageDrawable(getResources().getDrawable(R.drawable.btn_music_repeat_order));
					break;
				case AudioControl.PLAYER_REPEAT_MODE_SINGLE_TRACK:
					mImageRepeat
							.setImageDrawable(getResources().getDrawable(R.drawable.btn_music_repeat_singel));
					break;
				case AudioControl.PLAYER_REPEAT_MODE_ALL_TRACK:
					mImageRepeat
							.setImageDrawable(getResources().getDrawable(R.drawable.btn_music_repeat_all));
					break;
				case AudioControl.PLAYER_REPEAT_MODE_GROUP:
					mImageRepeat
							.setImageDrawable(getResources().getDrawable(R.drawable.btn_music_repeat_all));
					break;
				}
			}
			break;
		case AudioControl.PLAYER_ATTRIBUTE_SHUFFLE:// 3

			if (mShuffleMode != nAttrValue) {
				mShuffleMode = nAttrValue;
				switch (nAttrValue) {
				case AudioControl.PLAYER_SHUFFLE_OFF:
					mImageShuffle
							.setImageDrawable(getResources().getDrawable(R.drawable.btn_music_shuffle_close));
					break;
				case AudioControl.PLAYER_SHUFFLE_ALL_TRACK:
					mImageShuffle
							.setImageDrawable(getResources().getDrawable(R.drawable.btn_music_shuffle_open));
					break;
				case AudioControl.PLAYER_SHUFFLE_GROUP:
					mImageShuffle
							.setImageDrawable(getResources().getDrawable(R.drawable.btn_music_shuffle_open));
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
	
	public void freshSeekBarTail(int progress){
		int mMax = mSeekBar.getMax();
//		LogUtil.i(TAG,"freshSeekBarTail");
//		LogUtil.i(TAG, "progress: "+progress+",,,mMax: "+mMax);
		int deltaX;
		if(mMax == 0 || progress == 0){
			mSeekTail.setVisibility(View.GONE);
			return;
		}else{
			deltaX = (int)(480*(progress/(float)mMax));
			if(deltaX == 0){
				mSeekTail.setVisibility(View.GONE);
				return;
			}else{
				mSeekTail.setVisibility(View.VISIBLE);
			}
			
		}
		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mSeekTail
				.getLayoutParams();
		
		if(deltaX <= 30){
			lp.height = 15;
			lp.width = deltaX+10;
			lp.topMargin = 30;
			lp.leftMargin = 0;
		}else if(deltaX <= 50){
			lp.height = 25;
			lp.width = deltaX+20;
			lp.topMargin = 26;
			lp.leftMargin = 0;
		}else if(deltaX <= 100){
			lp.height = 35;
			lp.width = deltaX+25;
			lp.topMargin = 23;
			lp.leftMargin = 0;
		} else if(deltaX <= 135){
			lp.height = 45;
			lp.width = deltaX+25;
			lp.leftMargin = 0;
			lp.topMargin = 18;
		}else if(deltaX <= 165){
			lp.height = 52;
			lp.width = deltaX+35;
			lp.leftMargin = 0;
			lp.topMargin = 17;
		}else if(deltaX <= 320){
			lp.height = 52;
			lp.width = 199;
			lp.leftMargin = deltaX - 199+30;	
			lp.topMargin = 17;
		}else if(deltaX <= 400){
			lp.height = 52;
			lp.width = 199;
			lp.leftMargin = deltaX - 199+20;	
			lp.topMargin = 17;
		}else{
			lp.height = 52;
			lp.width = 199;
			lp.leftMargin = deltaX - 199+15;	
			lp.topMargin = 17;
		}
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
	private void prevDown(){
		LogUtil.i(TAG, " --- prevDown ");
		mHandler.sendEmptyMessageDelayed(LONG_CLICK_PREV,1500);
	}
	
	/**
	 * 上一首抬起
	 */
	private void prevUp(){
		if (mHandler.hasMessages(LONG_CLICK_PREV)) {
			mHandler.removeMessages(LONG_CLICK_PREV);
		}
		if (isNormalPrev) {
			LogUtil.i(TAG, " --- prevup ");
			mHandler.sendEmptyMessage(SHORT_CLICK_PREV);
		}
		isNormalPrev = true;
	}
	
	private View.OnTouchListener nextListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				nextDown();
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				nextUp();
				break;

			default:
				break;
			}
			return false;
		}
	};
	
	/**
	 * 下一曲按下
	 */
	private void nextDown(){
		LogUtil.i(TAG, " --- nextDown ");
		mHandler.sendEmptyMessageDelayed(LONG_CLICK_NEXT, 1500);
	}
	
	/**
	 * 下一曲抬起
	 */
	private void nextUp(){
		if (mHandler.hasMessages(LONG_CLICK_NEXT)) {
			mHandler.removeMessages(LONG_CLICK_NEXT);
		}
		if (isNormalNext) {
			LogUtil.i(TAG, " --- nextUp ");
			mHandler.sendEmptyMessage(SHORT_CLICK_NEXT);
		}
		isNormalNext = true;
	}

	@Override
	public void updateBgBitmap(Bitmap bg) {
		if (bg != null) {
			Drawable drawable =new BitmapDrawable(bg);
			mImageBg.setBackgroundDrawable(drawable);
		} else {
			mImageBg.setBackgroundResource(R.drawable.bg_music_main);
		}
	}
	
}

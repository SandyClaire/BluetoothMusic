package com.hsae.d531mc.bluetooth.music;

import java.util.ArrayList;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
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
import com.hsae.d531mc.bluetooth.music.util.Util;
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

	private Button mBtnMusicSwith;
	private Button mBtnSettings;
	private Button mBtnPrev;
	private Button mBtnPlay;
	private Button mBtnNext;
	private LinearLayout mBtnRepeat;
	private LinearLayout mBtnShuffle;
	private Button mBtnHome;
	private TextView mTextTitle;
	private TextView mTextArtist;
	private TextView mTextAlbum;
	private TextView mTextCurTime;
	private TextView mTextTotalTime;
	private SeekBar mSeekBar;
	private DrawerLayout mDrawerLayout;
	private FrameLayout mFrameLayout;
	private FragmentManager mFragmentManager;
	private MusicSwitchFragmet mFragmet;
	private boolean ismPlaying = false;
	private ImageView mImageShuffle;
	private ImageView mImageRepeat;
	private BluetoothSettingFragment mSettingFragment;
	private LinearLayout mLinBg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 透明状态栏
		getWindow()
				.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		// 透明导航栏
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		setContentView(R.layout.music_main);
		initView();
		initMvp();
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
		mBtnMusicSwith = (Button) findViewById(R.id.btn_source_settings);
		mBtnSettings = (Button) findViewById(R.id.btn_bt_settings);
		mBtnPrev = (Button) findViewById(R.id.btn_prev);
		mBtnPlay = (Button) findViewById(R.id.btn_play);
		mBtnNext = (Button) findViewById(R.id.btn_next);
		mBtnRepeat = (LinearLayout) findViewById(R.id.btn_repeat);
		mBtnShuffle = (LinearLayout) findViewById(R.id.btn_shuffle);
		mSeekBar = (SeekBar) findViewById(R.id.music_seekbar);
		mTextTitle = (TextView) findViewById(R.id.music_title);
		mTextArtist = (TextView) findViewById(R.id.music_artist);
		mTextAlbum = (TextView) findViewById(R.id.music_album);
		mTextCurTime = (TextView) findViewById(R.id.music_currenttime);
		mTextTotalTime = (TextView) findViewById(R.id.music_totaltime);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.music_drawerlayout);
		mFrameLayout = (FrameLayout) findViewById(R.id.bluetooth_music_frame);
		mBtnHome = (Button) findViewById(R.id.btn_home);
		mImageShuffle = (ImageView) findViewById(R.id.img_shuffle);
		mImageRepeat = (ImageView) findViewById(R.id.img_repeat);
		mLinBg = (LinearLayout) findViewById(R.id.lin_music_bg);
		mFragmet = (MusicSwitchFragmet) MusicSwitchFragmet.getInstance(this);
		mSettingFragment = (BluetoothSettingFragment) BluetoothSettingFragment
				.getInstance(this);
		mFragmentManager = getFragmentManager();
		mBtnMusicSwith.setOnClickListener(this);
		mBtnPrev.setOnClickListener(this);
		mBtnPlay.setOnClickListener(this);
		mBtnNext.setOnClickListener(this);
		mBtnRepeat.setOnClickListener(this);
		mBtnShuffle.setOnClickListener(this);
		mBtnHome.setOnClickListener(this);
		mBtnSettings.setOnClickListener(this);
		mDrawerLayout.setOnTouchListener(touchListener);
//		mFragmentManager.beginTransaction()
//		.replace(R.id.bluetooth_music_frame, mFragmet).commit();

		mBtnNext.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				Message msg = Message.obtain();
				msg.what = MusicActionDefine.ACTION_A2DP_FASTFORWORD;
				MusicMainActivity.this.notify(msg, FLAG_RUN_SYNC);
				return false;
			}
		});

		mBtnPrev.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				Message msg = Message.obtain();
				msg.what = MusicActionDefine.ACTION_A2DP_REWIND;
				MusicMainActivity.this.notify(msg, FLAG_RUN_SYNC);
				return false;
			}
		});
		initBackground();
	}
	
	class BitmapWorkerTask extends AsyncTask<Bundle, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(Bundle... params) {
			byte[] in = params[0].getByteArray(Util.VALUE);
			return BitmapFactory.decodeByteArray(in, 0, in.length);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			BitmapDrawable newDrawable = new BitmapDrawable(
					getResources(), result);
			mLinBg.setBackground(newDrawable);
		}

	}
	
	public void initBackground() {
		LogUtil.i(TAG, "initBackground");
		Bundle bd = getContentResolver().call(Util.WALL_CONTENT_URI,
				Util.METHOD_GET_VALUE_WALL, Util.WALLPAPER_SET, null);
		if (bd != null) {
			BitmapWorkerTask mTask = new BitmapWorkerTask();
			mTask.execute(bd);
		}
	}
	
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

		mDrawerLayout.openDrawer(mFrameLayout); // 显示左侧
	}

	public void closeMusicSwitch() {
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
		case R.id.btn_prev:
			Message msgp = Message.obtain();
			msgp.what = MusicActionDefine.ACTION_A2DP_PREV;
			this.notify(msgp, FLAG_RUN_MAIN_THREAD);
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
		case R.id.btn_next:
			Message msgn = Message.obtain();
			msgn.what = MusicActionDefine.ACTION_A2DP_NEXT;
			this.notify(msgn, FLAG_RUN_MAIN_THREAD);
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
	public void updateViewByConnectStatus(int status) {
		if (status == 0) {
			// Toast.makeText(this, "disconnected A2DP", Toast.LENGTH_SHORT)
			// .show();
			updateViewShow(false);
			mTextTitle.setText(getResources().getString(
					R.string.music_bluetooth_disconnect));
			mTextArtist.setText(getResources().getString(
					R.string.music_bluetooth_disconnect_summery));
			mTextAlbum.setText(getResources().getString(
					R.string.music_bluetooth_disconnect));
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
			// Toast.makeText(this, "connected A2DP",
			// Toast.LENGTH_SHORT).show();
			updateViewShow(true);
			LogUtil.i(TAG, "Bluetooth A2DP connected");
		}
	}

	private void updateViewShow(boolean flag) {
		mBtnPrev.setEnabled(flag);
		mBtnPlay.setEnabled(flag);
		mBtnNext.setEnabled(flag);
		mBtnRepeat.setEnabled(flag);
		mBtnShuffle.setEnabled(flag);
		mSeekBar.setEnabled(flag);
		if (!flag) {
			ismPlaying = false;
			mMusicHandler.removeCallbacks(updateMusicPlayTimer);
			mBtnPlay.setBackground(getResources().getDrawable(
					R.drawable.btn_music_play));
		}
	}

	@Override
	public void updatePlayBtnByStatus(boolean flag) {
		LogUtil.i(TAG, "Bluetooth A2DP updatePlayBtnByStatus -- flag = " + flag);
		if (flag) {
			ismPlaying = true;
			mBtnPlay.setBackground(getResources().getDrawable(
					R.drawable.btn_music_pause));
		} else {
			ismPlaying = false;
			mMusicHandler.removeCallbacks(updateMusicPlayTimer);
			mBtnPlay.setBackground(getResources().getDrawable(
					R.drawable.btn_music_play));
		}
	}

	@Override
	public void updateMusicDataInfo(MusicBean bean, boolean isSupport) {
		isSupportMetadata = isSupport;
		LogUtil.i(TAG, "Bluetooth A2DP updateMusicDataInfo -- isSupport = "
				+ isSupport);
		if (isSupport && null != bean) {
			mTextTitle.setText(bean.getTitle());
			mTextArtist.setText(bean.getAtrist());
			mTextAlbum.setText(bean.getAlbum());
			mTextTotalTime.setText(getTotalTime(bean.getTotalTime()));
		} else {
			mTextTitle.setText(getResources().getString(
					R.string.music_matedate_unsupport));
			mTextArtist.setText(getResources().getString(
					R.string.music_matedate_unsupport));
			mTextAlbum.setText(getResources().getString(
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

	private boolean isSupportPlaybackpos = false;
	private boolean isSupportMetadata = false;

	private String getCurrentTime(String nTime) {
		if (nTime.equals("-1") && isSupportPlaybackpos == false) {
			return "00:00";
		} else {
			if (isNumeric(nTime) == true) {
				isSupportPlaybackpos = true;
				int pos = Integer.valueOf(nTime) / 1000;
				mSeekBar.setProgress(pos);
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
	public void updateMusicPlayCurrentTime(String currentTime, boolean isPlaying) {
		mTextCurTime.setText(getCurrentTime(currentTime));
		if (isPlaying && (mMusicHandler != null)) {
			Log.i("wangda", "updateMusicPlayCurrentTime");
			mMusicHandler.removeCallbacks(updateMusicPlayTimer);
			mMusicHandler.postDelayed(updateMusicPlayTimer, 1000);
		}
		if (isPlaying) {
			ismPlaying = true;
			mBtnPlay.setBackground(getResources().getDrawable(
					R.drawable.btn_music_pause));
		} else {
			ismPlaying = false;
			mMusicHandler.removeCallbacks(updateMusicPlayTimer);
			mBtnPlay.setBackground(getResources().getDrawable(
					R.drawable.btn_music_play));
		}
	}

	private ArrayList<Integer> mRepeatAllowedlist = new ArrayList<Integer>();
	private ArrayList<Integer> mShuffleAllowedlist = new ArrayList<Integer>();

	@Override
	public void updateRepeatAllowList(ArrayList<Integer> allowList) {
		mRepeatAllowedlist.clear();
		mRepeatAllowedlist.addAll(allowList);
		LogUtil.i(TAG, "mRepeatAllowedlist size = " + mRepeatAllowedlist.size());
		if (mRepeatAllowedlist.size() <= 0) {
			mBtnRepeat.setVisibility(View.GONE);
		} else {
			mBtnRepeat.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void updateShuffleAllowList(ArrayList<Integer> allowList) {
		mShuffleAllowedlist.clear();
		mShuffleAllowedlist.addAll(allowList);
		LogUtil.i(TAG,
				"mShuffleAllowedlist size = " + mShuffleAllowedlist.size());
		if (mShuffleAllowedlist.size() <= 0) {
			mBtnShuffle.setVisibility(View.GONE);
		} else {
			mBtnShuffle.setVisibility(View.VISIBLE);
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
							.setBackgroundResource(R.drawable.btn_music_repeat_order);
					break;
				case AudioControl.PLAYER_REPEAT_MODE_SINGLE_TRACK:
					mImageRepeat
							.setBackgroundResource(R.drawable.btn_music_repeat_singel);
					break;
				case AudioControl.PLAYER_REPEAT_MODE_ALL_TRACK:
					mImageRepeat
							.setBackgroundResource(R.drawable.btn_music_repeat_all);
					break;
				case AudioControl.PLAYER_REPEAT_MODE_GROUP:
					mImageRepeat
							.setBackgroundResource(R.drawable.btn_music_repeat_all);
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
							.setBackgroundResource(R.drawable.btn_music_shuffle_close);
					break;
				case AudioControl.PLAYER_SHUFFLE_ALL_TRACK:
					mImageShuffle
							.setBackgroundResource(R.drawable.btn_music_shuffle_open);
					break;
				case AudioControl.PLAYER_SHUFFLE_GROUP:
					mImageShuffle
							.setBackgroundResource(R.drawable.btn_music_shuffle_open);
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
			mBtnShuffle.setVisibility(View.GONE);
		} else {
			mBtnShuffle.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void updateRepeatAllowArray(int[] AllowArray, int num) {
		mRepeatAllowedlist.clear();
		for (int i = 0; i < num; i++) {
			mRepeatAllowedlist.add(AllowArray[i]);
		}
		if (mRepeatAllowedlist.size() <= 0) {
			mBtnRepeat.setVisibility(View.GONE);
		} else {
			mBtnRepeat.setVisibility(View.VISIBLE);
		}
	}

	// private BroadcastReceiver mReceiver = new BroadcastReceiver() {
	//
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// MusicMainActivity.this.unregisterReceiver(mReceiver);
	// MusicMainActivity.this.finish();
	// Log.e("wangda", "finish");
	// }
	// };

	// private void registBroadcast(){
	// IntentFilter intent = new IntentFilter();
	// intent.addAction(MusicActionDefine.ACTION_A2DP_FINISH_ACTIVITY);
	// this.registerReceiver(mReceiver, intent);
	// }

}

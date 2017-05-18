package com.hsae.d531mc.bluetooth.music;

import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hsae.d531mc.bluetooth.music.entry.MusicBean;
import com.hsae.d531mc.bluetooth.music.fragmet.MusicSwitchFragmet;
import com.hsae.d531mc.bluetooth.music.model.impl.MusicModel;
import com.hsae.d531mc.bluetooth.music.observer.IObserver;
import com.hsae.d531mc.bluetooth.music.observer.ISubject;
import com.hsae.d531mc.bluetooth.music.observer.ObserverAdapter;
import com.hsae.d531mc.bluetooth.music.presenter.MusicPersenter;
import com.hsae.d531mc.bluetooth.music.util.MusicActionDefine;
import com.hsae.d531mc.bluetooth.music.view.IMusicView;

/**
 * 
 * @author wangda
 *
 */
@SuppressLint("NewApi")
public class MusicMainActivity extends Activity implements ISubject,
		IMusicView, OnClickListener {

	private MusicPersenter mPresenter;

	private Button mBtnMusicSwith;
	private Button mBtnPrev;
	private Button mBtnPlay;
	private Button mBtnNext;
	private Button mBtnRepeat;
	private Button mBtnShuffle;
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
	private boolean isfirst = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.e("wangda",
				"oncreate ------------ 1 --------------- "
						+ System.currentTimeMillis());
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_main);
		initView();
		initMvp();
		Log.e("wangda",
				"oncreate ------------ 2 --------------- "
						+ System.currentTimeMillis());
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
		mBtnMusicSwith = (Button) findViewById(R.id.btn_bt_settings);
		mBtnPrev = (Button) findViewById(R.id.btn_prev);
		mBtnPlay = (Button) findViewById(R.id.btn_play);
		mBtnNext = (Button) findViewById(R.id.btn_next);
		mBtnRepeat = (Button) findViewById(R.id.btn_repeat);
		mBtnShuffle = (Button) findViewById(R.id.btn_shuffle);
		mSeekBar = (SeekBar) findViewById(R.id.music_seekbar);
		mTextTitle = (TextView) findViewById(R.id.music_title);
		mTextArtist = (TextView) findViewById(R.id.music_artist);
		mTextAlbum = (TextView) findViewById(R.id.music_album);
		mTextCurTime = (TextView) findViewById(R.id.music_currenttime);
		mTextTotalTime = (TextView) findViewById(R.id.music_totaltime);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.music_drawerlayout);
		mFrameLayout = (FrameLayout) findViewById(R.id.bluetooth_music_frame);
		mBtnHome = (Button) findViewById(R.id.btn_home);
		mFragmet = (MusicSwitchFragmet) MusicSwitchFragmet.getInstance(this);
		mFragmentManager = getFragmentManager();
		mBtnMusicSwith.setOnClickListener(this);
		mBtnPrev.setOnClickListener(this);
		mBtnPlay.setOnClickListener(this);
		mBtnNext.setOnClickListener(this);
		mBtnRepeat.setOnClickListener(this);
		mBtnShuffle.setOnClickListener(this);
		mBtnHome.setOnClickListener(this);

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
		isfirst = true;
	}

	private void showFram() {
		if (isfirst) {
			mFragmentManager.beginTransaction()
					.replace(R.id.bluetooth_music_frame, mFragmet).commit();
			isfirst = false;
		}
		mDrawerLayout.openDrawer(mFrameLayout); // 显示左侧
	}

	@Override
	protected void onStop() {
		if (mMusicHandler != null)
			mMusicHandler.removeCallbacks(updateMusicPlayTimer);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Message msg = Message.obtain();
		msg.what = MusicActionDefine.ACTION_APP_EXIT;
		this.notify(msg, FLAG_RUN_SYNC);
		this.detach(mPresenter);
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
		Message msg = Message.obtain();
		switch (v.getId()) {
		case R.id.btn_bt_settings:
			showFram();
			break;
		case R.id.btn_prev:
			msg.what = MusicActionDefine.ACTION_A2DP_PREV;
			this.notify(msg, FLAG_RUN_MAIN_THREAD);
			break;
		case R.id.btn_play:
			if (ismPlaying) {
				msg.what = MusicActionDefine.ACTION_A2DP_PAUSE;
			} else {
				msg.what = MusicActionDefine.ACTION_A2DP_PLAY;
			}
			this.notify(msg, FLAG_RUN_MAIN_THREAD);
			break;
		case R.id.btn_next:
			msg.what = MusicActionDefine.ACTION_A2DP_NEXT;
			this.notify(msg, FLAG_RUN_MAIN_THREAD);
			break;
		case R.id.btn_repeat:
			showRepeatPopUp(playModeListener);
			break;
		case R.id.btn_shuffle:
			showShufflePopUp(playModeListener);
			break;
		case R.id.btn_home:
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
			this.finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void updateViewByConnectStatus(int status) {
		if (status == 0) {
			Toast.makeText(this, "disconnected A2DP", Toast.LENGTH_SHORT)
					.show();
			updateViewShow(false);
			mTextTitle.setText(getResources().getString(
					R.string.music_bluetooth_disconnect));
			mTextArtist.setText(getResources().getString(
					R.string.music_bluetooth_disconnect));
			mTextAlbum.setText(getResources().getString(
					R.string.music_bluetooth_disconnect));
			mTextTotalTime.setText("00:00");
			mTextCurTime.setText("00:00");
			mSeekBar.setMax(0);
		} else if (status == 1) {
			Toast.makeText(this, "connected A2DP", Toast.LENGTH_SHORT).show();
			updateViewShow(true);
		}
	}

	private void updateViewShow(boolean flag) {
		mBtnPrev.setEnabled(flag);
		mBtnPlay.setEnabled(flag);
		mBtnNext.setEnabled(flag);
		mBtnRepeat.setEnabled(flag);
		mBtnShuffle.setEnabled(flag);
		mSeekBar.setEnabled(flag);
	}

	@Override
	public void updatePlayBtnByStatus(boolean flag) {
		if (flag) {
			ismPlaying = true;
			mBtnPlay.setBackground(getResources().getDrawable(
					R.drawable.btn_music_pause));
			;
		} else {
			ismPlaying = false;
			mMusicHandler.removeCallbacks(updateMusicPlayTimer);
			mBtnPlay.setBackground(getResources().getDrawable(
					R.drawable.btn_music_play));
			;
		}
	}

	@Override
	public void updateMusicDataInfo(MusicBean bean, boolean isSupport) {
		isSupportMetadata = isSupport;
		if (isSupport && null != bean) {
			mTextTitle.setText(bean.getTitle());
			mTextArtist.setText(bean.getAtrist());
			mTextAlbum.setText(bean.getAlbum());
			mTextTotalTime.setText(getTotalTime(bean.getTotalTime()));
			Log.e("wangda", "~~~~~~~~~~~~~~~~~~~~~~~~ totaltime = "
					+ bean.getTotalTime());
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

	private View mRepeatView;

	private PopupWindow repeatWindow;

	@SuppressLint("InlinedApi")
	private void showRepeatPopUp(OnClickListener clickListener) {
		LayoutInflater inflater = (LayoutInflater) MusicMainActivity.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mRepeatView = inflater.inflate(R.layout.pop_repeat, null);

		LinearLayout btnRepeatAll = (LinearLayout) mRepeatView
				.findViewById(R.id.btn_repeat_all);
		LinearLayout btnRepeatSingle = (LinearLayout) mRepeatView
				.findViewById(R.id.btn_repeat_single);
		LinearLayout btnRepeatOrder = (LinearLayout) mRepeatView
				.findViewById(R.id.btn_repeat_order);
		btnRepeatAll.setOnClickListener(clickListener);
		btnRepeatSingle.setOnClickListener(clickListener);
		btnRepeatOrder.setOnClickListener(clickListener);

		repeatWindow = new PopupWindow(MusicMainActivity.this);
		repeatWindow.setContentView(mRepeatView);
		repeatWindow.setWidth(LayoutParams.WRAP_CONTENT);
		repeatWindow.setHeight(LayoutParams.WRAP_CONTENT);
		repeatWindow.setFocusable(true);
		repeatWindow.showAtLocation(
				MusicMainActivity.this.findViewById(R.id.btn_repeat),
				Gravity.BOTTOM | Gravity.RIGHT, 172, 0);
	}

	private View mShuffleView;

	private PopupWindow shuffleWindow;

	@SuppressLint("InlinedApi")
	private void showShufflePopUp(OnClickListener clickListener) {
		LayoutInflater inflater = (LayoutInflater) MusicMainActivity.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mShuffleView = inflater.inflate(R.layout.pop_shuffle, null);

		LinearLayout btnOpen = (LinearLayout) mShuffleView
				.findViewById(R.id.btn_shuffle_open);
		LinearLayout btnClose = (LinearLayout) mShuffleView
				.findViewById(R.id.btn_shuffle_close);
		btnOpen.setOnClickListener(clickListener);
		btnClose.setOnClickListener(clickListener);

		shuffleWindow = new PopupWindow(MusicMainActivity.this);
		shuffleWindow.setContentView(mShuffleView);
		shuffleWindow.setWidth(LayoutParams.WRAP_CONTENT);
		shuffleWindow.setHeight(LayoutParams.WRAP_CONTENT);
		shuffleWindow.setFocusable(true);
		shuffleWindow.showAtLocation(
				MusicMainActivity.this.findViewById(R.id.btn_shuffle),
				Gravity.BOTTOM | Gravity.RIGHT, 0, 0);
	}

	private OnClickListener playModeListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Message msg = Message.obtain();
			switch (v.getId()) {
			case R.id.btn_repeat_all:
				if (repeatWindow != null) {
					repeatWindow.dismiss();
					repeatWindow = null;
				}
				msg.what = MusicActionDefine.ACTION_A2DP_REPEAT_ALL;
				mBtnRepeat.setBackgroundResource(R.drawable.btn_music_repeat_all);
				break;
			case R.id.btn_repeat_order:
				if (repeatWindow != null) {
					repeatWindow.dismiss();
					repeatWindow = null;
				}
				msg.what = MusicActionDefine.ACTION_A2DP_REPEAT_ORDER;
				mBtnRepeat.setBackgroundResource(R.drawable.btn_music_repeat_order);
				break;
			case R.id.btn_repeat_single:
				if (repeatWindow != null) {
					repeatWindow.dismiss();
					repeatWindow = null;
				}
				msg.what = MusicActionDefine.ACTION_A2DP_REPEAT_SINGLE;
				mBtnRepeat.setBackgroundResource(R.drawable.btn_music_repeat_singel);
				break;
			case R.id.btn_shuffle_open:
				if (shuffleWindow != null) {
					shuffleWindow.dismiss();
					shuffleWindow = null;
				}
				msg.what = MusicActionDefine.ACTION_A2DP_SHUFFLE_OPEN;
				mBtnShuffle.setBackgroundResource(R.drawable.btn_music_shuffle_open);
				break;
			case R.id.btn_shuffle_close:
				if (shuffleWindow != null) {
					shuffleWindow.dismiss();
					shuffleWindow = null;
				}
				msg.what = MusicActionDefine.ACTION_A2DP_SHUFFLE_CLOSE;
				mBtnShuffle.setBackgroundResource(R.drawable.btn_music_shuffle_close);
				break;
			default:

				break;
			}
			MusicMainActivity.this.notify(msg, FLAG_RUN_SYNC);
		}
	};

}

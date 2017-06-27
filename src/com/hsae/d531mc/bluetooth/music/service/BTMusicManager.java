package com.hsae.d531mc.bluetooth.music.service;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.anwsdk.service.AudioControl;
import com.anwsdk.service.MangerConstant;
import com.hsae.autosdk.bt.music.BTMusicInfo;
import com.hsae.autosdk.bt.music.IBTMusicListener;
import com.hsae.autosdk.bt.music.IBTMusicManager;
import com.hsae.autosdk.hmi.HmiConst;
import com.hsae.autosdk.popup.PopupListener;
import com.hsae.autosdk.popup.PopupRequest;
import com.hsae.autosdk.popup.constant.PopupConst.Popup;
import com.hsae.autosdk.source.Source;
import com.hsae.autosdk.source.SourceConst.App;
import com.hsae.autosdk.util.LogUtil;
import com.hsae.d531mc.bluetooth.music.R;

public class BTMusicManager extends IBTMusicManager.Stub {

	private static final String TAG = "BTMusicManager";

	private Context mContext;
	private static BTMusicManager mManager;
	private BluetoothMusicModel mBluetoothMusicModel;
	public IBTMusicListener mListener;

	private BTMusicManager(Context mContext) {
		super();
		this.mContext = mContext;
		mBluetoothMusicModel = BluetoothMusicModel.getInstance(mContext);
		LogUtil.e(TAG, "------------------ INIT");
	}

	public static BTMusicManager getInstance(Context context) {
		if (null == mManager) {
			mManager = new BTMusicManager(context);
		}
		return mManager;
	}

	@Override
	public int backward() throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int forward() throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getAlbumName() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getArtistName() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bitmap getArtwork() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getRepeatMode() throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getShuffleMode() throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getTrackName() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void hide() throws RemoteException {
		mBluetoothMusicModel.finishActivity();
		LogUtil.i(TAG, "------------------ hide");
	}

	@Override
	public boolean isConnected() throws RemoteException {
		return mBluetoothMusicModel.a2dpStatus == 1;
	}

	boolean isFrist = true;
	long downTime = System.currentTimeMillis();
	long upTime = System.currentTimeMillis();

	/**
	 * 方控上一首下一首按键
	 */
	@Override
	public void onHmiChanged(int hmiIndex, boolean down) {
		LogUtil.i("onHmiChanged", "down == " + down + ", hmiIndex == " + hmiIndex);
		if (hmiIndex == HmiConst.HMI.SEEKUP.ordinal() || hmiIndex == HmiConst.HMI.SEEKDOWN.ordinal()) {

			if (down) {
				downTime = isFrist ? System.currentTimeMillis() : (downTime == 0) ? System.currentTimeMillis()
						: downTime;
				isFrist = false;
			} else {
				upTime = System.currentTimeMillis();
				LogUtil.e(TAG, "hmi long seek = " + (upTime - downTime));
				if ((upTime - downTime) < 1500) {
					seek(hmiIndex, false);
				} else {
					downTime = 0;
					upTime = 0;
					return;
				}
			}
		}
	}

	private void seek(int index, boolean isLong) {
		LogUtil.i("seek", "isLong == " + isLong + ", index == " + index);
		if (!isLong) {
			if (index == HmiConst.HMI.SEEKDOWN.ordinal()) {
				try {
					mBluetoothMusicModel.AVRCPControl(AudioControl.CONTROL_FORWARD);
					// showPop();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				LogUtil.i(TAG, "------------- SEEKUP_NEXT ");
			} else if (index == HmiConst.HMI.SEEKUP.ordinal()) {
				try {
					mBluetoothMusicModel.AVRCPControl(AudioControl.CONTROL_BACKWARD);
					// showPop();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				LogUtil.i(TAG, "------------- SEEKDOWN_PREV ");
			}
		}
		downTime = 0;
		upTime = 0;
	}

	@Override
	public void pause() throws RemoteException {
		mBluetoothMusicModel.AVRCPControl(AudioControl.CONTROL_PAUSE);
		mBluetoothMusicModel.isHandPuse = true;
		LogUtil.i(TAG, "------------- PAUSE ");
	}

	@Override
	public void play() throws RemoteException {
		mBluetoothMusicModel.isHandPuse = false;
		mBluetoothMusicModel.tryToSwitchSource();
		mBluetoothMusicModel.requestAudioFocus(false);
		LogUtil.i(TAG, "------------- PLAY2 ");
	}

	// public void playByVr() throws RemoteException {
	// mBluetoothMusicModel.isHandPuse = false;
	// mBluetoothMusicModel.tryToSwitchSource();
	// mBluetoothMusicModel.requestAudioFocus(false);
	// LogUtil.i(TAG, "------------- playByVr ");
	// }

	@Override
	public void playByName(String arg0) throws RemoteException {
		// TODO Auto-generated method stub
	}

	@Override
	public void popUpCurrentMode() throws RemoteException {
		LogUtil.i(TAG, "popUpCurrentMode ");
		showPop();
	}

	private void showPop() {
		new Handler(Looper.getMainLooper()).post(new Runnable() {

			@Override
			public void run() {
				showPopUp(mContext.getResources().getString(R.string.app_name));
			}
		});
	}

	@Override
	public void prev() throws RemoteException {
		mBluetoothMusicModel.isHandPuse = false;
		mBluetoothMusicModel.AVRCPControl(AudioControl.CONTROL_BACKWARD);
		LogUtil.i(TAG, "------------- PREV ");
	}

	@Override
	public void next() throws RemoteException {
		mBluetoothMusicModel.isHandPuse = false;
		mBluetoothMusicModel.AVRCPControl(AudioControl.CONTROL_FORWARD);
		LogUtil.i(TAG, "------------- NEXT ");
	}

	@Override
	public void registerBTMusicListener(IBTMusicListener arg0) throws RemoteException {
		mListener = arg0;
		LogUtil.e(TAG, " ----------- mListener" + mListener);
	}

	@Override
	public void setRepeatMode(int nAttrValue) throws RemoteException {
		mBluetoothMusicModel.setPlayModel(AudioControl.PLAYER_ATTRIBUTE_REPEAT,
				mBluetoothMusicModel.mRepeatAllowedlist, nAttrValue);
		LogUtil.i(TAG, "setCurrentPlayerRepeatModel -- currentType = " + nAttrValue);
	}

	@Override
	public void setShuffleMode(int nAttrValue) throws RemoteException {
		mBluetoothMusicModel.setPlayModel(AudioControl.PLAYER_ATTRIBUTE_SHUFFLE,
				mBluetoothMusicModel.mShuffleAllowedlist, nAttrValue);
		LogUtil.i(TAG, "setCurrentPlayerShuffleModel -- currentType = " + nAttrValue);
	}

	@Override
	public void show() throws RemoteException {
		mBluetoothMusicModel.isHandPuse = false;
		Intent intent = new Intent();
		intent.setPackage("com.hsae.d531mc.bluetooth.music");
		intent.setClassName(mContext, "com.hsae.d531mc.bluetooth.music.MusicMainActivity");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);

		// mBluetoothMusicModel.requestAudioFocus(true);
		LogUtil.i(TAG, "------------- show ");
	}

	@Override
	public void unregisterBTMusicListener(IBTMusicListener arg0) throws RemoteException {
		mListener = null;
		System.gc();
	}

	public BTMusicInfo getBtMusicInfo() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void disconnectA2dp() throws RemoteException {
		LogUtil.i(TAG, "--------- disconnectA2dp ");
		mBluetoothMusicModel.isDisByIpod = true;
		mBluetoothMusicModel.a2dpDisconnect();
	}

	@Override
	public String getBtMacAddress() throws RemoteException {
		LogUtil.i(TAG, "--------- getBtMacAddress ");
		return mBluetoothMusicModel.getLocalAddress();
	}

	@Override
	public void exitDiagnoseMode() throws RemoteException {
		LogUtil.i(TAG, "------------- exitDiagnoseMode ");
		Source source = new Source();
		if (source.getCurrentSource() == App.BT_MUSIC) {
			mBluetoothMusicModel.audioSetStreamMode(MangerConstant.AUDIO_STREAM_MODE_ENABLE);
		}
	}

	@Override
	public void playByVR() throws RemoteException {
		this.show();
	}

	private PopupRequest tipRequest;
	private TextView tipText;
	private boolean isTipPopShow = false;

	private void showPopUp(String tip) {
		LogUtil.i(TAG, "tipListener tipRequest =null " + (tipRequest == null));

		if (null == tipRequest) {

			final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
			params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
			params.flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
					| WindowManager.LayoutParams.FLAG_DIM_BEHIND | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
			params.format = PixelFormat.TRANSLUCENT;

			params.width = LayoutParams.MATCH_PARENT;
			params.height = LayoutParams.WRAP_CONTENT;

			params.dimAmount = 0.0f;
			params.gravity = Gravity.TOP;

			final View tipView = LayoutInflater.from(mContext).inflate(R.layout.popup_tip, null);
			tipText = (TextView) tipView.findViewById(R.id.text_tip);
			// LogUtil.i(TAG, "tipListener mTitel = " +
			// mBluetoothMusicModel.mTitel);
			tipText.setText(tip + " " + mBluetoothMusicModel.mTitel);
			tipText.setText(tip);
			try {
				tipRequest = PopupRequest.getPopupRequest(mContext, Popup.BT_MUSIC, tipView, params, tipListener);
			} catch (Exception e) {

				LogUtil.i(TAG, "Exception " + e);// TODO: handle exception
			}
		}

		LogUtil.i(TAG, "tipListener isTipPopShow = " + isTipPopShow);

		if (isTipPopShow && tipRequest != null) {
			tipRequest.hidePopup();
			// tipRequest.release();
			tipRequest.showPopup();
			// tipRequest = null;
		} else if (tipRequest != null) {
			tipRequest.showPopup();
		}
	}

	PopupListener tipListener = new PopupListener() {

		@Override
		public void onShow() {
			LogUtil.i(TAG, "tipListener onShow");
			isTipPopShow = true;
		}

		@Override
		public void onHide() {
			LogUtil.i(TAG, "tipListener onHide");
			isTipPopShow = false;
		}
	};

	@Override
	public void pauseByVr() throws RemoteException {
		mBluetoothMusicModel.pauseByVr = true;
		mBluetoothMusicModel.AVRCPControl(AudioControl.CONTROL_PAUSE);
		LogUtil.i(TAG, "------------- pauseByVr ");
	}
}

package com.hsae.d531mc.bluetooth.music.service;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.RemoteException;

import com.anwsdk.service.AudioControl;
import com.anwsdk.service.MangerConstant;
import com.hsae.autosdk.bt.music.BTMusicInfo;
import com.hsae.autosdk.bt.music.IBTMusicListener;
import com.hsae.autosdk.bt.music.IBTMusicManager;
import com.hsae.autosdk.hmi.HmiConst;
import com.hsae.autosdk.source.Source;
import com.hsae.autosdk.source.SourceConst.App;
import com.hsae.autosdk.util.LogUtil;

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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void next() throws RemoteException {
		// TODO Auto-generated method stub
	}

	boolean isFrist = true;
	long downTime = System.currentTimeMillis();
	long upTime = System.currentTimeMillis();

	/**
	 * 方控上一首下一首按键
	 */
	@Override
	public void onHmiChanged(int hmiIndex, boolean down) {
		LogUtil.i("onHmiChanged", "down == " + down + ", hmiIndex == "
				+ hmiIndex);
		if (hmiIndex == HmiConst.HMI.SEEKUP.ordinal()
				|| hmiIndex == HmiConst.HMI.SEEKDOWN.ordinal()) {

			if (down) {
				downTime = isFrist ? System.currentTimeMillis()
						: (downTime == 0) ? System.currentTimeMillis()
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
					mBluetoothMusicModel
							.AVRCPControl(AudioControl.CONTROL_FORWARD);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				LogUtil.i(TAG, "------------- SEEKUP_NEXT ");
			} else if (index == HmiConst.HMI.SEEKUP.ordinal()) {
				try {
					mBluetoothMusicModel
							.AVRCPControl(AudioControl.CONTROL_BACKWARD);
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
		LogUtil.i(TAG, "------------- PLAY ");
	}

	@Override
	public void playByName(String arg0) throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void popUpCurrentMode() throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void prev() throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerBTMusicListener(IBTMusicListener arg0)
			throws RemoteException {
		mListener = arg0;
		LogUtil.e(TAG, " ----------- mListener" + mListener);
	}

	@Override
	public void setRepeatMode(int nAttrValue) throws RemoteException {
		mBluetoothMusicModel.setPlayModel(AudioControl.PLAYER_ATTRIBUTE_REPEAT,
				mBluetoothMusicModel.mRepeatAllowedlist, nAttrValue);
		LogUtil.i(TAG, "setCurrentPlayerRepeatModel -- currentType = " + nAttrValue );
	}

	@Override
	public void setShuffleMode(int nAttrValue) throws RemoteException {
		mBluetoothMusicModel.setPlayModel(AudioControl.PLAYER_ATTRIBUTE_SHUFFLE,
				mBluetoothMusicModel.mShuffleAllowedlist, nAttrValue);
		LogUtil.i(TAG, "setCurrentPlayerShuffleModel -- currentType = " + nAttrValue );
	}

	@Override
	public void show() throws RemoteException {
		mBluetoothMusicModel.isHandPuse = false;
		Intent intent = new Intent();
		intent.setPackage("com.hsae.d531mc.bluetooth.music");
		intent.setClassName(mContext,
				"com.hsae.d531mc.bluetooth.music.MusicMainActivity");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);
		
//		mBluetoothMusicModel.requestAudioFocus(true);
		LogUtil.i(TAG, "------------- show ");
	}

	@Override
	public void unregisterBTMusicListener(IBTMusicListener arg0)
			throws RemoteException {
		// TODO Auto-generated method stub

	}

	public BTMusicInfo getBtMusicInfo() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void disconnectA2dp() throws RemoteException {
		LogUtil.i(TAG, "--------- disconnectA2dp ");
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

}

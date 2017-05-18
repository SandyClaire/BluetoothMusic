package com.hsae.d531mc.bluetooth.music.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import com.anwsdk.service.AudioControl;
import com.hsae.autosdk.bt.music.BTMusicInfo;
import com.hsae.autosdk.bt.music.IBTMusicListener;
import com.hsae.autosdk.bt.music.IBTMusicManager;
import com.hsae.autosdk.hmi.HmiConst;
import com.hsae.autosdk.util.LogUtil;



public class BTMusicManager extends IBTMusicManager.Stub{
	
	private static final String TAG = "BTMusicManager";
	
	private Context mContext;
	private static BTMusicManager mManager;
	private BluetoothMusicModel mBluetoothMusicModel;
	public IBTMusicListener mListener;
	
	private BTMusicManager(Context mContext) {
		super();
		this.mContext = mContext;
		mBluetoothMusicModel = BluetoothMusicModel.getInstance(mContext);
		LogUtil.e(TAG , "------------------ INIT");
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
//		Intent intent = new Intent();
//		intent.setAction(MusicActionDefine.ACTION_A2DP_FINISH_ACTIVITY);
//		mContext.sendBroadcast(intent);
		LogUtil.e(TAG , "------------------ hide");
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

	@Override
	public void onHmiChanged(int arg0, boolean arg1) throws RemoteException {
		removeHandlerMessages();
		Message msg = Message.obtain();
		
		if (arg1) {
			msg.what = KEY_DOWN;
			mHandler.sendMessageDelayed(msg, 2000);
			mIsLongPress = false;
		} else {
			int hmiIndex = keyEventDispatcher(arg0);
			msg.what = hmiIndex;
			mHandler.sendMessage(msg);
			LogUtil.i(TAG, "------------- KEY UP info = " + msg.what);
		}
	}
	
	private void removeHandlerMessages() {
		mHandler.removeMessages(SEEKUP_NEXT);
		mHandler.removeMessages(SEEKDOWN_PREV);
		mHandler.removeMessages(SEEKUP_FORWARD);
		mHandler.removeMessages(SEEKDOWN_BACKWARD);
		mHandler.removeMessages(KEY_DOWN);
	}
	
	private int keyEventDispatcher(int hmiIndex) {
		int index =-1;
		if (mIsLongPress) {
			if (HmiConst.HMI.SEEKUP.ordinal() ==  hmiIndex) {
				index = SEEKUP_FORWARD;
			}else if (HmiConst.HMI.SEEKDOWN.ordinal() == hmiIndex) {
				index = SEEKDOWN_BACKWARD;
			}
			
		} else {
			if (HmiConst.HMI.SEEKUP.ordinal() ==  hmiIndex) {
				index = SEEKUP_NEXT;
			}else if (HmiConst.HMI.SEEKDOWN.ordinal() == hmiIndex) {
				index = SEEKDOWN_PREV;
			}
		}
		
		return index;
	}
	
	private static final int SEEKUP_NEXT = 0x01;
	private static final int SEEKDOWN_PREV = 0x02;
	private static final int SEEKUP_FORWARD = 0x03;
	private static final int SEEKDOWN_BACKWARD = 0x04;
	
	private static final int KEY_DOWN = 0x07;
	
	private boolean mIsLongPress = false;
	
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler =  new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SEEKUP_NEXT:
				try {
					mBluetoothMusicModel.AVRCPControl(AudioControl.CONTROL_FORWARD);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				LogUtil.i(TAG, "------------- SEEKUP_NEXT = " + msg.what);
				break;
			case SEEKDOWN_PREV:
				try {
					mBluetoothMusicModel.AVRCPControl(AudioControl.CONTROL_BACKWARD);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				LogUtil.i(TAG, "------------- SEEKDOWN_PREV = " + msg.what);
				break;
			case SEEKUP_FORWARD:
				try {
					mBluetoothMusicModel.AVRCPControl(AudioControl.CONTROL_FASTFORWARD);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				LogUtil.i(TAG, "------------- SEEKUP_FORWARD = " + msg.what);
				break;
			case SEEKDOWN_BACKWARD:
				try {
					mBluetoothMusicModel.AVRCPControl(AudioControl.CONTROL_REWIND);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				LogUtil.i(TAG, "------------- SEEKDOWN_BACKWARD = " + msg.what);
				break;
			case KEY_DOWN:
				mIsLongPress = true;
				break;
				default:
			}
		};
	};
	

	@Override
	public void pause() throws RemoteException {
		mBluetoothMusicModel.AVRCPControl(AudioControl.CONTROL_PAUSE);
		LogUtil.i(TAG, "------------- PAUSE " );
	}

	@Override
	public void play() throws RemoteException {
		mBluetoothMusicModel.tryToSwitchSource();
		mBluetoothMusicModel.requestAudioFocus(false);
		mBluetoothMusicModel.AVRCPControl(AudioControl.CONTROL_PLAY);
		LogUtil.i(TAG, "------------- PLAY " );
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
	public void setRepeatMode(int arg0) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setShuffleMode(int arg0) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() throws RemoteException {
	        Intent intent = new Intent();
	        intent.setAction(Intent.ACTION_MAIN);
	        intent.addCategory(Intent.CATEGORY_LAUNCHER);
	        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
	        intent.setPackage("com.hsae.d531mc.bluetooth.music");
	        intent.setClassName(mContext, "com.hsae.d531mc.bluetooth.music.MusicMainActivity");
	        mContext.startActivity(intent);
	        mBluetoothMusicModel.AVRCPControl(AudioControl.CONTROL_PLAY);
			LogUtil.i(TAG, "------------- show " );
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
	
}


package com.hsae.d531mc.bluetooth.music.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.anwsdk.service.AudioControl;
import com.anwsdk.service.IAnwPhoneLink;
import com.anwsdk.service.MangerConstant;
import com.hsae.autosdk.source.Source;
import com.hsae.autosdk.source.SourceConst.App;
import com.hsae.d531mc.bluetooth.music.entry.MusicBean;
import com.hsae.d531mc.bluetooth.music.model.IMusicModel;
import com.hsae.d531mc.bluetooth.music.util.ShowLog;

/**
 * 
 * @author wangda
 *
 */
public class BluetoothMusicModel {

	private static final String TAG = "BluetoothMusicModel";
	private static BluetoothMusicModel mInstance;
	private static Context mContext;
	private IAnwPhoneLink mIAnwPhoneLink;
	private BluetoothConnection mConnection = new BluetoothConnection();
	private int nPowerStatus = MangerConstant.BTPOWER_STATUS_OFF;
	private static ShowLog mLog;
	private IMusicModel mIMusicModel;
	private static final int errorCode = -1;

	public static BluetoothMusicModel getInstance(Context context) {
		mContext = context;
		if (null == mInstance) {
			mInstance = new BluetoothMusicModel();
		}
		mLog = new ShowLog();
		return mInstance;
	}

	public void bindService() {
		Intent intent = new Intent(MangerConstant.ServiceActionName);
		mContext.startService(intent);
		mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	public void unbindService() {
		mContext.unbindService(mConnection);
	}

	private class BluetoothConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mLog.showIlog(TAG, "---------- onServiceConnected ------------");
			mIAnwPhoneLink = IAnwPhoneLink.Stub.asInterface(service);
			if(mIAnwPhoneLink==null){
				return;
			}
			try {
				nPowerStatus = mIAnwPhoneLink.ANWBT_GetBTPowerStatus();
				if (nPowerStatus == MangerConstant.BTPOWER_STATUS_OFF)
					mIAnwPhoneLink.ANWBT_BTPowerOn();
				
				if (getConnectStatus(MangerConstant.PROFILE_AUDIO_CONTROL_CHANNEL, 0) == 1) {
					getCurrentPlayerAPSetting();
				}

			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mLog.showIlog(TAG, "---------- onServiceDisconnected ------------");
			mIAnwPhoneLink = null;
		}
	}

	/**
	 * This function returns the current power status
	 * 
	 * @return BTPOWER_STATUS_OFF 0 BT power off BTPOWER_STATUS_ON 1 BT power on
	 *         BTPOWER_STATUS_ONING 2 In power on process BTPOWER_STATUS_OFFING
	 *         3 In power off process
	 * @throws RemoteException
	 */
	public int getBTPowerStatus() throws RemoteException {
		if (null == mIAnwPhoneLink) {
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
			return errorCode;
		}
		return mIAnwPhoneLink.ANWBT_GetBTPowerStatus();
	}

	/**
	 * This function retrieves the connect status of remote device.
	 * 
	 * @param nProfileType
	 *            [in] The profile that want to check. Please reference ��Profile
	 *            type�� section in MangerConstant object for detail.
	 * @param mIndex
	 *            [in] The connection��s ID, used in SPP connection only.
	 * @return Returns Anw_SUCCESS on success or returns an error code on
	 *         failure.
	 * @throws RemoteException
	 */
	public int getConnectStatus(int nProfileType, int nIndex)
			throws RemoteException {
		if (null == mIAnwPhoneLink) {
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
			return errorCode;
		}
		return mIAnwPhoneLink.ANWBT_GetConnectStatus(nProfileType, nIndex);
	}

	/**
	 * Use this function to retrieve the current status of A2DP. When A2DP is
	 * playing, this function will return true, otherwise return false.
	 * 
	 * @return Returns the current status of A2DP.
	 * @throws RemoteException
	 */
	public boolean isCurrentA2DPPlaying() throws RemoteException {
		if (null == mIAnwPhoneLink) {
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
			return false;
		}
		return mIAnwPhoneLink.ANWBT_IsCurrentA2DPPlaying();
	}

	/**
	 * Use this function to establish the Bluetooth connection with the paired
	 * device. It connects to the A2DP profile.
	 * 
	 * @param address
	 *            [in] The Bluetooth address of remote device
	 * @return Returns Anw_SUCCESS on success or returns an error code on
	 *         failure.
	 * @throws RemoteException
	 */
	public int A2DPConnect(String address) throws RemoteException {
		if (null == mIAnwPhoneLink) {
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
			return errorCode;
		}
		return mIAnwPhoneLink.ANWBT_A2DPConnect(address);
	}

	/**
	 * This function is used to disconnect a previous connected Bluetooth
	 * device.
	 * 
	 * @return Returns Anw_SUCCESS on success or returns an error code on
	 *         failure.
	 * @throws RemoteException
	 */
	public int A2DPDisconnect() throws RemoteException {
		if (null == mIAnwPhoneLink) {
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
			return errorCode;
		}
		return mIAnwPhoneLink.ANWBT_A2DPDisconnect();
	}

	/**
	 * This function retrieves the information of current connected device.
	 * 
	 * @param nProfileType
	 *            [in] The profile that want to check. Please reference ��Profile
	 *            type�� section in MangerConstant object for detail.
	 * @param strAddress
	 *            [out] The Bluetooth device��s MAC address
	 * @param strName
	 *            [out] The Bluetooth device��s name
	 * @param nIndex
	 *            [in] The connection��s ID, used in SPP connection only.
	 * @return Returns Anw_SUCCESS on success or returns an error code on
	 *         failure.
	 * @throws RemoteException
	 */
	public int getConnectedDeviceInfo(int nProfileType, String[] strAddress,
			String[] strName, int nIndex) throws RemoteException {
		if (null == mIAnwPhoneLink) {
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
			return errorCode;
		}
		return mIAnwPhoneLink.ANWBT_GetConnectedDeviceInfo(nProfileType,
				strAddress, strName, nIndex);
	}

	/**
	 * This function retrieves A2DP Meta data of current connected A2DP
	 * Bluetooth device.
	 * 
	 * @param attributesID
	 *            [in] The attribute id desired to retrieve. Please reference
	 *            Media Attributes ID for detail.
	 * @return Meta data string.
	 * @throws RemoteException
	 *             Remarks This function is valid after AVRCP is connected.
	 */
	public String A2DPGetCurrentAttributes(int attributesID)
			throws RemoteException {
		if (null == mIAnwPhoneLink) {
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
			return "";
		}
		return mIAnwPhoneLink.ANWBT_A2DPGetCurrentAttributes(attributesID);
	}

	/**
	 * Use this function to get the current play status, the current play
	 * position and the total playing time. This function returns immediately.
	 * You must use registerReceiver to register a BroadcastReceiver with action
	 * MSG_ACTION_A2DP_METADATA ,MSG_ACTION_A2DP_PLAYSTATUS ,
	 * MSG_ACTION_A2DP_STREAMSTATUSand MSG_ACTION_A2DP_PLAYBACKPOS to get those
	 * information .
	 * 
	 * @return Returns Anw_SUCCESS on success or returns an error code on
	 *         failure.
	 * @throws RemoteException
	 */
	public int getPlayStatus() throws RemoteException {
		if (null == mIAnwPhoneLink) {
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
			return errorCode;
		}
		return mIAnwPhoneLink.ANWBT_AudioGetPlayStatus();
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
	public boolean isA2DPSupportMetadata() throws RemoteException {
		if (null == mIAnwPhoneLink) {
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
			return false;
		}
		return mIAnwPhoneLink.ANWBT_IsA2DPSupportMetadata();
	}

	/**
	 * Use this function to retrieve the current player application setting
	 * supported attributes and allowed attribute values that store in
	 * AnwSdkService.
	 * 
	 * @param nAttrID
	 *            [in] Attribute ID.
	 * @param nAllowArray
	 *            [in] The allowed attribute values arrary.
	 * @param nArraySize
	 *            [in] The size of nAllowArray.
	 * @return Returns Anw_SUCCESS on success or returns an error code on
	 *         failure.
	 * @throws RemoteException
	 */
	public int retrieveCurrentPlayerAPSupported(int nAttrID, int[] nAllowArray,
			int nArraySize) throws RemoteException {
		if (null == mIAnwPhoneLink) {
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
			return errorCode;
		}
		return mIAnwPhoneLink.ANWBT_AudioRetrieveCurrentPlayerAPSupported(
				nAttrID, nAllowArray, nArraySize);
	}

	/**
	 * Use this function to retrieve the current player application setting that
	 * store in AnwSdkService.
	 * 
	 * @param nAttrID
	 *            [in] Attribute ID.
	 * @return Returns Anw_SUCCESS on success or returns an error code on
	 *         failure.
	 * @throws RemoteException
	 */
	public int retrieveCurrentPlayerAPSetting(int nAttrID)
			throws RemoteException {
		if (null == mIAnwPhoneLink) {
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
			return errorCode;
		}
		return mIAnwPhoneLink
				.ANWBT_AudioRetrieveCurrentPlayerAPSetting(nAttrID);
	}

	/**
	 * Use this function to get the current player application setting by send
	 * avrcp command to target.. This function returns immediately. You must use
	 * registerReceiver to register a BroadcastReceiver with action
	 * MSG_ACTION_AVRCP_PLAYERSETTING_CHANGED_EVENT to get those informations
	 * 
	 * @return Returns Anw_SUCCESS on success or returns an error code on
	 *         failure.
	 * @throws RemoteException
	 */
	public int getCurrentPlayerAPSetting() throws RemoteException {
		if (null == mIAnwPhoneLink) {
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
			return errorCode;
		}
		return mIAnwPhoneLink.ANWBT_AudioGetCurrentPlayerAPSetting();
	}

	/**
	 * Use this function to set the current player application setting.
	 * 
	 * @param nAttrID
	 *            [in] Player Application Setting Attribute
	 * @param nAttrValue
	 *            [in] Attribute value.
	 * @return Returns Anw_SUCCESS on success or returns an error code on
	 *         failure.
	 * @throws RemoteException
	 * 
	 *             Remarks When AVRCP channel connect, SDK will call the A2DP
	 *             event callback function with event type set as
	 *             TYPE_EVENT_NOTIFICATION and event id set as
	 *             EVENT_PLAYER_APPLICATION_SETTING_SUPPORTED to notify
	 *             application the target supported player application setting
	 *             attributes and value. Application can use API
	 *             ANWBT_AudioSetCurrentPlayerAPSetting to set the player
	 *             attribute value if it has supported.
	 */
	public int setCurrentPlayerAPSetting(int nAttrID, int nAttrValue)
			throws RemoteException {
		if (null == mIAnwPhoneLink) {
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
			return errorCode;
		}
		return mIAnwPhoneLink.ANWBT_AudioSetCurrentPlayerAPSetting(nAttrID,
				nAttrValue);
	}

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
	public int AVRCPControlEx(int op_code, int nActFlag) throws RemoteException {
		if (null == mIAnwPhoneLink) {
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
			return errorCode;
		}
		return mIAnwPhoneLink.ANWBT_AVRCPControlEx(op_code, nActFlag);
	}

	/**
	 * Use this function to get stream volume is mute or un-mute.
	 * 
	 * @return Returns 1 means mute now, 0 means un-mute.
	 * @throws RemoteException
	 */
	public int getStreamMode() throws RemoteException {
		if (null == mIAnwPhoneLink) {
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
			return errorCode;
		}
		return mIAnwPhoneLink.ANWBT_AudioGetStreamMode();
	}

	/**
	 * Use this function to set stream volume to mute or un-mute.
	 * 
	 * @param mode
	 *            [in] 1 means mute, 0 means un-mute
	 * @return Returns Anw_SUCCESS on success or returns an error code on
	 *         failure.
	 * @throws RemoteException
	 */
	public int setStreamMode(int mode) throws RemoteException {
		if (null == mIAnwPhoneLink) {
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
			return errorCode;
		}
		return mIAnwPhoneLink.ANWBT_AudioSetStreamMode(mode);
	}

	/**
	 * Use this function to set stream volume gain value.
	 * 
	 * @param volume
	 *            [in] The volume gain value (0.0f ~ 1.0f)
	 * @return Returns Anw_SUCCESS on success or returns an error code on
	 *         failure.
	 * @throws RemoteException
	 *             This function must be used on streaming start, otherwise
	 *             return an error.
	 */
	public int setStreamVolume(float volume) throws RemoteException {
		if (null == mIAnwPhoneLink) {
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
			return errorCode;
		}
		return mIAnwPhoneLink.ANWBT_AudioSetStreamVolume(volume);
	}

	/**
	 * This function sends the AVRCP command to mobile phone for controlling the
	 * music playing.
	 * 
	 * @param op_code
	 *            [in] Please reference AVRCP_Operation_ID for detail command
	 *            information.
	 * @return Returns Anw_SUCCESS on success or returns an error code on
	 *         failure.
	 * @throws RemoteException
	 */
	public int AVRCPControl(int op_code) throws RemoteException {
		if (null == mIAnwPhoneLink) {
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
			return errorCode;
		}
		return mIAnwPhoneLink.ANWBT_AVRCPControl(op_code);
	}
	
	/**
	 * regist music status listener
	 * @param nMusicModel
	 */
	public void registMusicListener(IMusicModel nMusicModel) {
		mIMusicModel = nMusicModel;
	}
	
	/**
	 * 更新链接状态
	 * @param status
	 */
	public void updateMsgByConnectStatusChange(int status) {
		if (null != mIMusicModel) {
			mIMusicModel.updateConnectStatusMsg(status);
		}
	}
	
	/**
	 * 跟新播放状态
	 * @param flag
	 */
	public void updatePlayStatus(boolean flag) {
		if (null != mIMusicModel) {
			mIMusicModel.updatePlayOrPauseStatus(flag);
		}
	}
	
	/**
	 * 更新当前音乐信息
	 * @param bean
	 */
	public void updateCurrentMusicInfo(MusicBean bean) {
		if (null != mIMusicModel) {
			mIMusicModel.getCurrentMusicBean(bean);
		}
	}
	
	/**
	 * 更新播放进度时间
	 * @param position
	 * @param isPlaying
	 */
	public void updateCurrentPlayTime(String position , boolean isPlaying) {
		if (null != mIMusicModel) {
			mIMusicModel.getCurrentMusicPlayPosition(position,isPlaying);
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
	 * @return
	 */
	public boolean tryToSwitchSource() {
		Source source = new Source();
        boolean isSwitch = source.tryToSwitchSource(App.BT_MUSIC, true);
        return isSwitch;
    }
	
	/**
     * @Description: 通知中间件音频焦点是否已获得，并且中间件切换音源
     * @param isChanged
     */
    private void mainAudioChanged(boolean isChanged) {
    	Source source = new Source();
    	source.mainAudioChanged(App.BT_MUSIC, isChanged);
        Log.i(TAG, "requestAudioSource == " + isChanged);
    }
    
    public boolean isAudioFocused = false;
    public AudioManager audioManager;
    
    /** 获取Android音频焦点 */
    public void requestAudioFocus() {
        Log.i(TAG, "requestAudioFocus---request audio focus");
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE); //STREAM_MUSIC
        int result = audioManager.requestAudioFocus(mAFCListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.i(TAG, "requestAudioFocus---AudioManager.AUDIOFOCUS_REQUEST_GRANTED" + "FocusManager获取音频焦点成功");
            isAudioFocused = true;
        } else if (result == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
            Log.i(TAG, "requestAudioFocus---" + "FocusManager获取音频焦点失败");
            isAudioFocused = false;
        } else {
        	isAudioFocused = false;
            Log.i(TAG, "requestAudioFocus---" + "FocusManager获取音频焦点失败");
        }
        mainAudioChanged(isAudioFocused);
        if (isAudioFocused) {
			try {
				AVRCPControl(AudioControl.CONTROL_PLAY);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
    }
    
    public void releaseAudioFocus() {
    	if (audioManager != null) {
    		audioManager.abandonAudioFocus(mAFCListener);
		}
	}
    
    /**
     * 音源焦点变化监听
     */
    OnAudioFocusChangeListener mAFCListener = new OnAudioFocusChangeListener() {
		
		@Override
		public void onAudioFocusChange(int focusChange) {
		    switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
            	isAudioFocused = true;
                Log.i(TAG, "mAFCListener---audio focus change AUDIOFOCUS_GAIN");
                break;
            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
            	isAudioFocused = true;
                Log.i(TAG, "mAFCListener---audio focus change AUDIOFOCUS_GAIN_TRANSIENT");
                break;
            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
            	isAudioFocused = true;
                Log.i(TAG, "mAFCListener---audio focus change AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK");
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
            	isAudioFocused = false;
                Log.i(TAG, "mAFCListener---audio focus change AUDIOFOCUS_LOSS");
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
            	isAudioFocused = false;
                Log.i(TAG, "mAFCListener---audio focus change AUDIOFOCUS_LOSS_TRANSIENT");
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
            	isAudioFocused = false;
                Log.i(TAG, "mAFCListener---audio focus change AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                break;
            }
		    if (isAudioFocused) {
				try {
					AVRCPControl(AudioControl.CONTROL_PLAY);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}else {
				try {
					AVRCPControl(AudioControl.CONTROL_PAUSE);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		    mainAudioChanged(isAudioFocused);
		}
	};

}

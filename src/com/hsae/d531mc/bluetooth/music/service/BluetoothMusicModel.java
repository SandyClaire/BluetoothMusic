package com.hsae.d531mc.bluetooth.music.service;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.anwsdk.service.AudioControl;
import com.anwsdk.service.BT_ADV_DATA;
import com.anwsdk.service.IAnwInquiryCallBackEx;
import com.anwsdk.service.IAnwPhoneLink;
import com.anwsdk.service.MangerConstant;
import com.hsae.autosdk.bt.BTConst;
import com.hsae.autosdk.bt.music.BTMusicInfo;
import com.hsae.autosdk.bt.phone.BtPhoneProxy;
import com.hsae.autosdk.os.Soc;
import com.hsae.autosdk.os.SocConst.UsbDevices;
import com.hsae.autosdk.settings.AutoSettings;
import com.hsae.autosdk.source.Source;
import com.hsae.autosdk.source.SourceConst.App;
import com.hsae.autosdk.util.LogUtil;
import com.hsae.d531mc.bluetooth.music.entry.BluetoothDevice;
import com.hsae.d531mc.bluetooth.music.entry.MusicBean;
import com.hsae.d531mc.bluetooth.music.model.IBluetoothSettingModel;
import com.hsae.d531mc.bluetooth.music.model.IMusicModel;
import com.hsae.d531mc.bluetooth.music.util.Util;

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
	private IMusicModel mIMusicModel;
	private BTMusicManager mBTMmanager;
	private IBluetoothSettingModel mIBluetoothSettingModel;
	private static final int errorCode = 0;
	public String mTitel = "";

	public int hfpStatus = 0;
	public int a2dpStatus = 0;
	public int avrcpStatus = 0;
	public boolean isDisByIpod = false;

	/***
	 * 判断是VR调用stop的接口暂停后音频焦点再次回到BT时不执行播放动作
	 */
//	public boolean pauseByVr = false;
	/***
	 * 判断是电话起来时音乐自动暂停、该暂停由手机端发起、DA不发起暂停
	 */
	// boolean isPauseByCall;
	BtPhoneProxy phoneProxy = BtPhoneProxy.getInstance();

	// 壁纸缓存
	private LruCache<String, Bitmap> mMemoryCache;

	// 获取应用程序最大可用内存
	int cacheSize = (int) Runtime.getRuntime().maxMemory() / 8;

	// 缓存可用设备列表
	public List<BluetoothDevice> mListDevices = new ArrayList<BluetoothDevice>();

	// 手动暂停标识
	public boolean isHandPuse = false;

	// carplay 是否连接
	public boolean isCarPlayConnected = false;

	// carlife 是否连接
	public boolean isCarLifeConnected = false;

	// 音乐是否播放
	public boolean isPlay = false;

	/**
	 * 循环集合
	 */
	public ArrayList<Integer> mRepeatAllowedlist = new ArrayList<Integer>();

	private AudioManager audioManager;

	/**
	 * 随机集合
	 */
	public ArrayList<Integer> mShuffleAllowedlist = new ArrayList<Integer>();

	public static BluetoothMusicModel getInstance(Context context) {
		mContext = context;
		if (null == mInstance) {
			mInstance = new BluetoothMusicModel();

		}
		return mInstance;
	}

	public BluetoothMusicModel() {
		audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE); // STREAM_MUSIC
	}

	public void bindService() {
		Intent intent = new Intent(MangerConstant.ServiceActionName);
		mContext.startService(intent);
		mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	public void releaseModel() {
		mRepeatAllowedlist.clear();
		mShuffleAllowedlist.clear();
		mListDevices.clear();
	}

	private class BluetoothConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			LogUtil.i(TAG, "---------- onServiceConnected ------------");
			mIAnwPhoneLink = IAnwPhoneLink.Stub.asInterface(service);
			if (mIAnwPhoneLink == null) {
				return;
			}
			try {
				if (getConnectStatus(MangerConstant.PROFILE_AUDIO_CONTROL_CHANNEL, 0) == 1) {
					getCurrentPlayerAPSetting();
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			LogUtil.i(TAG, "---------- onServiceDisconnected ------------");
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
	 *            [in] The profile that want to check. Please reference
	 *            ��Profile type�� section in MangerConstant object for detail.
	 * @param mIndex
	 *            [in] The connection��s ID, used in SPP connection only.
	 * @return Returns Anw_SUCCESS on success or returns an error code on
	 *         failure.
	 * @throws RemoteException
	 */
	public int getConnectStatus(int nProfileType, int nIndex) throws RemoteException {
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
	 *            [in] The profile that want to check. Please reference
	 *            ��Profile type�� section in MangerConstant object for detail.
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
	public int getConnectedDeviceInfo(int nProfileType, String[] strAddress, String[] strName, int nIndex)
			throws RemoteException {
		if (null == mIAnwPhoneLink) {
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
			return errorCode;
		}
		return mIAnwPhoneLink.ANWBT_GetConnectedDeviceInfo(nProfileType, strAddress, strName, nIndex);
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
	public String A2DPGetCurrentAttributes(int attributesID) throws RemoteException {
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
	public int retrieveCurrentPlayerAPSupported(int nAttrID, int[] nAllowArray, int nArraySize) throws RemoteException {
		if (null == mIAnwPhoneLink) {
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
			return errorCode;
		}
		return mIAnwPhoneLink.ANWBT_AudioRetrieveCurrentPlayerAPSupported(nAttrID, nAllowArray, nArraySize);
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
	public int retrieveCurrentPlayerAPSetting(int nAttrID) throws RemoteException {
		if (null == mIAnwPhoneLink) {
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
			return errorCode;
		}
		return mIAnwPhoneLink.ANWBT_AudioRetrieveCurrentPlayerAPSetting(nAttrID);
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
	public int setCurrentPlayerAPSetting(int nAttrID, int nAttrValue) throws RemoteException {
		if (null == mIAnwPhoneLink) {
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
			return errorCode;
		}
		return mIAnwPhoneLink.ANWBT_AudioSetCurrentPlayerAPSetting(nAttrID, nAttrValue);
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
		// if (!isAudioFocused) {
		// LogUtil.i(TAG, "未获得焦点、无法操作蓝牙音乐");
		// return -999;
		// }
		//

		if (op_code == AudioControl.CONTROL_PLAY) {
			isHandPuse = false;
			if (a2dpStatus == 0) {
				LogUtil.i(TAG, "AVRCPControl : op_code= " + op_code + ",but now disable to play");
				return -1;
			}
			if (!handler.hasMessages(MSG_AUTOPLAY)) {
				handler.sendEmptyMessageDelayed(MSG_AUTOPLAY, 1500);
				audioSetStreamMode(MangerConstant.AUDIO_STREAM_MODE_ENABLE);
			}
		}else if (op_code == AudioControl.CONTROL_PAUSE ) {
			removeAutoPlay();
		}
		LogUtil.i(TAG, "AVRCPControl : op_code= " + op_code);
		return mIAnwPhoneLink.ANWBT_AVRCPControl(op_code);
	}

	/**
	 * This function is used to inquiry remote Bluetooth mobile devices in the
	 * nearby area.
	 * 
	 * run in thread
	 * 
	 * @throws RemoteException
	 */
	public int inquiryBtDevices(IAnwInquiryCallBackEx mInquiryCallBack) throws RemoteException {
		if (null == mIAnwPhoneLink) {
			return errorCode;
		}
		return mIAnwPhoneLink.ANWBT_DeviceInquiryEx(mInquiryCallBack);
	}

	/**
	 * This function cancels the current inquiry operation.
	 * 
	 * @return
	 * @throws RemoteException
	 */
	public int inquiryBtStop() throws RemoteException {
		if (null == mIAnwPhoneLink) {
			return errorCode;
		}
		return mIAnwPhoneLink.ANWBT_DeviceInquiryStop();
	}

	/**
	 * This function is used to pair with Bluetooth remote device.
	 * 
	 * @param address
	 *            The Bluetooth address of remote device
	 * @param pin_code
	 *            The PIN code for synchronizing the stack driver and Bluetooth
	 *            mobile
	 * @param cod
	 *            The cod of remote device, -1 means unknown.
	 * @return Returns Anw_SUCCESS on success or returns an error code on
	 *         failure.
	 * @throws RemoteException
	 */
	public int pair(String address, String pin_code, int cod) throws RemoteException {
		if (null == mIAnwPhoneLink) {
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
			return errorCode;
		}
		return mIAnwPhoneLink.ANWBT_DevicePair(address, pin_code, cod);
	}

	/**
	 * Use this function to retrieve the current status of inquiry.
	 * 
	 * @return
	 * @throws RemoteException
	 */
	public boolean isCurrentInquiring() throws RemoteException {
		if (null == mIAnwPhoneLink) {
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
			return false;
		}
		return mIAnwPhoneLink.ANWBT_IsCurrentInquiring();
	}

	/**
	 * This function retrieves the paired list.
	 * 
	 * @param nCount
	 *            [out] How many paired information return back to application.
	 *            The maximum count of paired list is 16.
	 * @param Name
	 *            [out] Name of paired device.
	 * @param Address
	 *            [out] Address of paired device.
	 * @param cod
	 *            [out] COD of paired device.-1 means unknown.
	 * @return Returns Anw_SUCCESS on success or returns an error code on
	 *         failure.
	 * @throws RemoteException
	 */
	public int getPairedList(int[] nCount, String[] Name, String[] Address, int[] cod) throws RemoteException {
		if (null == mIAnwPhoneLink) {
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
			return errorCode;
		}

		return mIAnwPhoneLink.ANWBT_GetPairedList(nCount, Name, Address, cod);
	}

	/**
	 * This function is used to disconnect a previous connected Bluetooth
	 * device.
	 * 
	 * @return Returns Anw_SUCCESS on success or returns an error code on
	 *         failure.
	 * @throws RemoteException
	 */
	public int disconnectMobiel() throws RemoteException {
		if (null == mIAnwPhoneLink) {
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
			return errorCode;
		}
		return mIAnwPhoneLink.ANWBT_DisconnectMobile();
	}

	/**
	 * This function is used to disconnect a previous connected Bluetooth
	 * device.
	 * 
	 * @return Returns Anw_SUCCESS on success or returns an error code on
	 *         failure.
	 * @throws RemoteException
	 */
	public int a2dpDisconnect() throws RemoteException {
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
	 * This function is used to un-pair the Bluetooth device which is already
	 * paired.
	 * 
	 * @param address
	 *            The Bluetooth address of remote device
	 * @return Returns Anw_SUCCESS on success or returns an error code on
	 *         failure.
	 * @throws RemoteException
	 */
	public int unPair(String address) throws RemoteException {
		if (null == mIAnwPhoneLink) {
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
			return errorCode;
		}
		return mIAnwPhoneLink.ANWBT_DeviceUnPair(address);
	}

	/**
	 * Use this function to establish the Bluetooth connection with the paired
	 * device. It connects to the A2DP profile.
	 * 
	 * @param address
	 *            The Bluetooth address of remote device
	 * @return Returns Anw_SUCCESS on success or returns an error code on
	 *         failure.
	 * @throws RemoteException
	 */
	public int a2dpConnect(String address) throws RemoteException {
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
	 * Use this function to establish the Bluetooth connection with the paired
	 * device. It connects to the HF profile and the profile that can retrieves
	 * PIM data.
	 * 
	 * This function returns immediately. You must use registerReceiver to
	 * register a BroadcastReceiver with action MSG_ACTION_CONNECT_STATUS .
	 * Phonelink SDK will broadcast message to notify the device connected or
	 * not.
	 * 
	 * @param address
	 *            The Bluetooth address of remote device
	 * @return Returns Anw_SUCCESS on success or returns an error code on
	 *         failure.
	 * @throws RemoteException
	 */
	public int connectMobile(String address) throws RemoteException {
		if (null == mIAnwPhoneLink) {
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
			return errorCode;
		}
		return mIAnwPhoneLink.ANWBT_ConnectMobile(address);
	}

	/**
	 * Use this function to set stream volume to mute or un-mute.
	 * 
	 * @param mode
	 * @return
	 * @throws RemoteException
	 */
	public int audioSetStreamMode(int mode) throws RemoteException {
		if (null == mIAnwPhoneLink) {
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
			return errorCode;
		}
		LogUtil.i(TAG, "audioSetStreamMode  ---  mode = " + mode);
		return mIAnwPhoneLink.ANWBT_AudioSetStreamMode(mode);
	}

	/**
	 * 获取本机名称
	 * 
	 * @return
	 * @throws RemoteException
	 */
	public String getDeviceName() throws RemoteException {
		if (null == mIAnwPhoneLink) {
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
			return "";
		}
		return mIAnwPhoneLink.ANWBT_GetDeviceName();
	}

	public String getLocalAddr() throws RemoteException {
		if (null == mIAnwPhoneLink) {
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
			return "";
		}
		return mIAnwPhoneLink.ANWBT_ReadLocalAddr();
	}

	/**
	 * 设置手机声音
	 * 
	 * @param bSpeaker
	 * @param nVal
	 * @return
	 * @throws RemoteException
	 */
	public boolean setDeviceVol(boolean bSpeaker, int nVal) throws RemoteException {
		if (null == mIAnwPhoneLink) {
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
			return false;
		}
		return mIAnwPhoneLink.ANWBT_SetDeviceVol(bSpeaker, nVal);
	}

	/**
	 * Use this function to retrieve the Bluetooth name of remote device.
	 * 
	 * @return
	 * @throws RemoteException
	 */
	public String getLocalAddress() throws RemoteException {
		if (null == mIAnwPhoneLink) {
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
			return "";
		}
		return mIAnwPhoneLink.ANWBT_ReadLocalAddr();
	}

	/**
	 * 注册 Blutooth Setting 监听
	 * 
	 * @param bluetoothSettingModel
	 */
	public void registBluetoothSettingListener(IBluetoothSettingModel bluetoothSettingModel) {
		mIBluetoothSettingModel = bluetoothSettingModel;
	}

	/**
	 * 更新配对状态
	 * 
	 * @param address
	 * @param status
	 */
	public void updatePairRequest(String address, int status) {
		if (null != mIBluetoothSettingModel) {
			mIBluetoothSettingModel.updateDevicePair(address, status);
		}
		updateUnpairListByStatus(status, address);
	}

	/**
	 * 更新carplay连接状态
	 */
	public void updateCPConnectStatus() {
		if (null != mIBluetoothSettingModel) {
			mIBluetoothSettingModel.updateCarplayConnectStatus();
		}
	}

	/**
	 * 取消Blutooth Setting 监听
	 */
	public void unregistBluetoothSettingListener() {
		mIBluetoothSettingModel = null;
	}

	/**
	 * 搜索蓝牙设备主调
	 */
	public void getBluetoothVisibleDevices() {
		new InitStatusTask().execute();
		LogUtil.i(TAG, "--- inquiryThread.star");
	}

	class InitStatusTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			try {
				mListDevices.clear();
				return inquiryBtDevices(InquiryCallBack);
			} catch (RemoteException e) {
			}
			return 0;
		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(Integer result) {
			LogUtil.i(TAG, "getBluetoothVisibleDevices : code = " + result);
			// 根据供应商反馈搜索结果为647时、执行一次stop的动作、且1S内不能执行搜索
			if (result == 647) {
				mIBluetoothSettingModel.stopInquiry();
			}
			mIBluetoothSettingModel.onInquiryCallBack(result);
		}
	}

	private static final int MSG_SETSTREAM_MODE = 2;
	private static final int MSG_AUTOPLAY = 3;

	Handler handler = new Handler() {
		public void handleMessage(final android.os.Message msg) {

			switch (msg.what) {
			case MSG_SETSTREAM_MODE:
				try {
					audioSetStreamMode(msg.getData().getInt("mode"));
					LogUtil.i(TAG, "MSG_SETSTREAM_MODE");
				} catch (RemoteException e) {
				}
				break;
			case MSG_AUTOPLAY:
				if (!isPlay && playtimes < 4 && App.BT_MUSIC.equals(getCurrentSource())) {
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
			}
		};
	};

	/**
	 * 搜索设备回调
	 */
	private IAnwInquiryCallBackEx InquiryCallBack = new IAnwInquiryCallBackEx.Stub() {

		@Override
		public void InquiryDataRsp(String address, String strName, int cod, int RSSI, BT_ADV_DATA EirData,
				boolean bComplete) throws RemoteException {
			final boolean mComplete = bComplete;
			final String mName = strName;
			final String mAddress = address;
			final int mCod = cod;
			final int mRSSI = RSSI;

			final int[] mCount = new int[1];
			final String[] Name = new String[16];
			final String[] Address = new String[16];
			final int[] COD = new int[16];

			mHandler.post(new Runnable() {
				public void run() {
					if (mComplete == false) {
						StringBuilder mBuilder = new StringBuilder();
						mBuilder.append("0x");
						mBuilder.append(Integer.toHexString(mCod));
						try {
							getPairedList(mCount, Name, Address, COD);
						} catch (RemoteException e) {
						}
						for (int i = 0; i < mCount[0]; i++) {
							if (mAddress.equals(Address[i])) {
								return;
							}
						}
						BluetoothDevice bean = new BluetoothDevice(mName, mAddress, mBuilder.toString(), mRSSI,
								BluetoothDevice.DEVICE_UNPAIR);
						mListDevices.add(bean);
						if (mIBluetoothSettingModel != null) {
							mIBluetoothSettingModel.getVisibleDevices(bean, false);
						}
					} else {
						if (mIBluetoothSettingModel != null) {
							mIBluetoothSettingModel.getVisibleDevices(null, true);
						}
					}
				}
			});
		}
	};

	/**
	 * 更新缓存列表状态
	 * 
	 * @param status
	 * @param address
	 */
	public void updateUnpairListByStatus(int status, String address) {
		LogUtil.i(TAG, "updateUnpairListByStatus -- status = " + status + "-- address = " + address);
		if (status == MangerConstant.Anw_SUCCESS) {
			for (int i = 0; i < mListDevices.size(); i++) {
				if (mListDevices.get(i).getAddress().equals(address)) {
					mListDevices.remove(i);
				}
			}
		} else {
			for (int i = 0; i < mListDevices.size(); i++) {
				if (mListDevices.get(i).getAddress().equals(address)) {
					mListDevices.get(i).setStatus(BluetoothDevice.DEVICE_UNPAIR);
				}
			}
		}
	}

	/**
	 * 监听蓝牙开关状体
	 * 
	 * @param status
	 */
	public void updateBTEnalbStatus(int status) {
		if (null != mIBluetoothSettingModel) {
			mIBluetoothSettingModel.updateBtEnableStatus(status);
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					mIBluetoothSettingModel.updateLocalName();
				}
			}, 2000);
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
		if (null != mIBluetoothSettingModel) {
			mIBluetoothSettingModel.updateCarlifeConnectStatus();
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
	 * 更新HFP链接状态
	 * 
	 * @param status
	 */
	public void updateHFPConnectStatus(int status) {
		if (null != mIBluetoothSettingModel) {
			mIBluetoothSettingModel.updateConnectStatus(status);
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
	 * 跟新播放状态
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
		if (null != mIMusicModel) {
			mBean = bean;
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
	 * 更新循环模式
	 * 
	 * @param AllowList
	 */
	public void updateRepeatModel(ArrayList<Integer> AllowList) {
		if (null != mIMusicModel) {
			mIMusicModel.updateAttributeRepeat(AllowList);
		}
		mRepeatAllowedlist.clear();
		mRepeatAllowedlist.addAll(AllowList);
	}

	/**
	 * 更新随机模式
	 * 
	 * @param AllowList
	 */
	public void updateShuffleModel(ArrayList<Integer> AllowList) {
		if (null != mIMusicModel) {
			mIMusicModel.updateAttributeShuffle(AllowList);
		}
		mShuffleAllowedlist.clear();
		mShuffleAllowedlist.addAll(AllowList);
	}

	/**
	 * 切换模式后更新当前模式
	 * 
	 * @param nAttrID
	 * @param nAttrValue
	 */
	public void updatePlayerModelSetting(int nAttrID, int nAttrValue) {
		if (null != mIMusicModel) {
			mIMusicModel.updataPlayerModel(nAttrID, nAttrValue);
		}
	}

	/**
	 * 切换音乐模式
	 * 
	 * @param nAttriID
	 * @param AllowedList
	 * @param nCurrentMode
	 */
	public void setPlayModel(int nAttriID, ArrayList<Integer> AllowedList, int nCurrentMode) {
		int nSupportSize = 0;
		if (AllowedList != null) {
			nSupportSize = AllowedList.size();
			if (nSupportSize > 0) {
				int i = 0;
				int nValue = 0;
				int nNextMode = -1;
				for (i = 0; i < nSupportSize; i++) {
					nValue = AllowedList.get(i);
					if (nValue == nCurrentMode) {
						int j = i + 1;
						if (j >= nSupportSize)
							j = 0;
						nNextMode = AllowedList.get(j);
						break;
					}
				}
				if (nNextMode >= 0) {
					try {
						setCurrentPlayerAPSetting(nAttriID, nNextMode);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}
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
		Source source = new Source();
		return source.tryToSwitchSource(App.BT_MUSIC);
	}

	/**
	 * 判断电话界面时候在最前面
	 */
	@SuppressWarnings("deprecation")
	public boolean isActive() {
		ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		String MusicPlayUI = "com.hsae.d531mc.bluetooth.music.MusicMainActivity";
		return cn.getClassName().equals(MusicPlayUI);
	}

	/**
	 * @Description: 通知中间件音频焦点是否已获得，并且中间件切换音源
	 * @param isChanged
	 */
	public void mainAudioChanged(boolean isActivite) {
		Source source = new Source();
		LogUtil.i(TAG, "mainAudioChanged == " + source.getCurrentSource() + "isActivite = " + isActivite);
		source.mainAudioChanged(App.BT_MUSIC, isActivite);
	}

	/**
	 * 如果焦点不再蓝牙音乐，将蓝牙音乐mute/在就只为unmute
	 */
	public void setMusicStreamMute() {
		Source source = new Source();
		App soApp = source.getCurrentSource();
		AutoSettings mAutoSettings = AutoSettings.getInstance();
		try {
			int mode = getStreamMode();
			LogUtil.i(TAG, "audioSetStreamMode : getCurrentSource = " + soApp);
			if (mAutoSettings.isDiagnoseMode() || !mAutoSettings.getPowerState()) {
				audioSetStreamMode(MangerConstant.AUDIO_STREAM_MODE_DISABLE);
				return;
			}

			LogUtil.i(TAG, "audioSetStreamMode : mode = " + mode);
			if (soApp != App.BT_MUSIC
					&& !isDisByIpod
					&& (mode == MangerConstant.AUDIO_STREAM_MODE_ENABLE || mode == MangerConstant.AUDIO_STREAM_MODE_UNMUTE)) {
				Bundle data = new Bundle();
				data.putInt("mode", MangerConstant.AUDIO_STREAM_MODE_DISABLE);
				Message msg = Message.obtain();
				msg.what = MSG_SETSTREAM_MODE;
				msg.setData(data);
				handler.sendMessage(msg);
				LogUtil.i(TAG, "audioSetStreamMode : AUDIO_STREAM_MODE_DISABLE");
			} else if (soApp == App.BT_MUSIC) {
				if (mode == MangerConstant.AUDIO_STREAM_MODE_DISABLE) {
					Bundle data = new Bundle();
					data.putInt("mode", MangerConstant.AUDIO_STREAM_MODE_ENABLE);
					Message msg = Message.obtain();
					msg.what = MSG_SETSTREAM_MODE;
					msg.setData(data);
					handler.sendMessage(msg);
					LogUtil.i(TAG, "audioSetStreamMode : AUDIO_STREAM_MODE_ENABLE");
				} else if (mode == MangerConstant.AUDIO_STREAM_MODE_MUTE) {
					Bundle data = new Bundle();
					data.putInt("mode", MangerConstant.AUDIO_STREAM_MODE_UNMUTE);
					Message msg = Message.obtain();
					msg.what = MSG_SETSTREAM_MODE;
					msg.setData(data);
					handler.sendMessage(msg);
					LogUtil.i(TAG, "audioSetStreamMode : AUDIO_STREAM_MODE_UNMUTE");
				}
			}
		} catch (RemoteException e) {
		}
	}

	public boolean isAudioFocused = false;
	int playtimes = 0;

	/** 获取Android音频焦点 */
	public void requestAudioFocus(boolean flag) {
		this.requestAudioFocus(flag, false);
	}

	public void requestAudioFocus(boolean showOrBack, boolean fromPlay) {
		Source source = new Source();
		LogUtil.i(TAG, " BT getCurrentSource = " + source.getCurrentSource() + ",isHandPuse = " + isHandPuse
				+ "fromPlay =" + fromPlay);
		try {
			if (fromPlay) {
				doPlay(showOrBack);
			} else {
				if (source.getCurrentSource() == App.BT_MUSIC) {
					mainAudioChanged(showOrBack);
					// 如果手动点击停止，不进行播放；
					if (!autoConnectA2DP()) {
						if (!isHandPuse) {
							AVRCPControl(AudioControl.CONTROL_PLAY);
						}
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
			if (!isAudioFocused) {
				LogUtil.i("cruze", "doPlay 准备抢占焦点");
				boolean canSwich = tryToSwitchSource();
				if (canSwich) {
					int result = audioManager.requestAudioFocus(mAFCListener, AudioManager.STREAM_MUSIC,
							AudioManager.AUDIOFOCUS_GAIN);
					if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
						LogUtil.i("cruze", "requestAudioFocus == 获取音频焦点成功");
						isAudioFocused = true;
						mainAudioChanged(showOrBack);
						AVRCPControl(AudioControl.CONTROL_PLAY);
					} else {
						LogUtil.i("cruze", "requestAudioFocus == 获取音频焦点失败");
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
			LogUtil.i("cruze", "doRequest 准备抢占焦点");
			boolean canSwich = tryToSwitchSource();
			if (canSwich) {
				int result = audioManager.requestAudioFocus(mAFCListener, AudioManager.STREAM_MUSIC,
						AudioManager.AUDIOFOCUS_GAIN);
				if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
					LogUtil.i("cruze", "requestAudioFocus == 获取音频焦点成功");
					isAudioFocused = true;
					mainAudioChanged(showOrBack);
					if (!autoConnectA2DP()) {
						if (!isHandPuse) {
							AVRCPControl(AudioControl.CONTROL_PLAY);
						}
						getPlayStatus();
					}
				} else {
					LogUtil.i("cruze", "requestAudioFocus == 获取音频焦点失败");
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
		LogUtil.i(TAG, "autoConnA2dp : hfpStatus = " + hfpStatus + " --- a2dpStatus = " + a2dpStatus);
		if (hfpStatus == 1 && a2dpStatus != 1) {
			mIMusicModel.autoConnectA2DP();
			return true;
		}
		return false;
	}

	private Handler mHandler = new Handler();

	/**
	 * 通知launcher 音乐信息
	 */
	private void notifyLauncherInfo() {
		int connStatus = 0;
		try {
			connStatus = getConnectStatus(MangerConstant.PROFILE_AUDIO_STREAM_CHANNEL, 0);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		if (connStatus == MangerConstant.Anw_SUCCESS && mBean != null) {
			BTMusicInfo info = new BTMusicInfo(mBean.getTitle(), mBean.getAtrist(), mBean.getAlbum(), null);
			notifyAutroMusicInfo(info);
		} else {
			BTMusicInfo info = new BTMusicInfo("", "", "", null);
			notifyAutroMusicInfo(info);
		}
	}

	public void notifyAutroMusicInfo(BTMusicInfo info) {
		if (null == mBTMmanager) {
			mBTMmanager = BTMusicManager.getInstance(mContext);
		}
		try {
			if (null != mBTMmanager.mListener) {
				mBTMmanager.mListener.syncBtMusicInfo(info);
			}

		} catch (Exception e) {
			LogUtil.i(TAG, " ---- Exception = " + e.toString());
		}
	}

	/**
	 * 音源焦点变化监听
	 */
	OnAudioFocusChangeListener mAFCListener = new OnAudioFocusChangeListener() {

		@Override
		public void onAudioFocusChange(int focusChange) {
			int callstatus = phoneProxy.getPhoneState();
			switch (focusChange) {
			case AudioManager.AUDIOFOCUS_GAIN:
				LogUtil.i(TAG, "cruze mAFCListener---audio focus change AUDIOFOCUS_GAIN");
				isAudioFocused = true;
				mainAudioChanged(isActive());
				break;
			case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
				LogUtil.i(TAG, "cruze  mAFCListener---audio focus change AUDIOFOCUS_GAIN_TRANSIENT");
				isAudioFocused = true;
				mainAudioChanged(isActive());
				break;
			case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
				LogUtil.i(TAG, "cruze  mAFCListener---audio focus change AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK");
				isAudioFocused = true;
				mainAudioChanged(isActive());
				break;
			case AudioManager.AUDIOFOCUS_LOSS:
				// if (callstatus != BTConst.Phone.UNKOWN) {
				// isPauseByCall = true;
				// } else {
				// isPauseByCall = false;
				// }
				// LogUtil.i(TAG,
				// "cruze  mAFCListener---audio focus change AUDIOFOCUS_LOSS &&  isPauseByCall ="
				// + isPauseByCall);
				isAudioFocused = false;
				notifyAutroMusicInfo(null);
				break;
			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
				LogUtil.i(TAG, "cruze  mAFCListener---audio focus change AUDIOFOCUS_LOSS_TRANSIENT");
				isAudioFocused = false;
				notifyAutroMusicInfo(null);
				break;
			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
				LogUtil.i(TAG, "cruze  mAFCListener---audio focus change AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
				isAudioFocused = false;
				notifyAutroMusicInfo(null);
				break;
			}
			LogUtil.i("cruze", "cruze  mAFCListener : isAudioFocused =  " + isAudioFocused + " ,isHandPuse = "
					+ isHandPuse + "  isPlay = " + isPlay + " isPauseByCall = " );
			try {
				if (isAudioFocused) {
					// 如果是手动暂停 不执行播放
					if (!isHandPuse) {
						// audioSetStreamMode(MangerConstant.AUDIO_STREAM_MODE_ENABLE);
						AVRCPControl(AudioControl.CONTROL_PLAY);
					}
				} else {
					// audioSetStreamMode(MangerConstant.AUDIO_STREAM_MODE_DISABLE);
					if (handler.hasMessages(MSG_AUTOPLAY)) {
						handler.removeMessages(MSG_AUTOPLAY);
					}
					AVRCPControl(AudioControl.CONTROL_PAUSE);
				}
			} catch (RemoteException e) {
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
		Source source = new Source();
		return source.getCurrentSource();
	}

}

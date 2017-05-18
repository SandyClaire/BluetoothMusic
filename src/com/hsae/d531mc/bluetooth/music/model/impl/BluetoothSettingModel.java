package com.hsae.d531mc.bluetooth.music.model.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.anwsdk.service.BT_ADV_DATA;
import com.anwsdk.service.IAnwInquiryCallBackEx;
import com.anwsdk.service.MangerConstant;
import com.hsae.autosdk.util.LogUtil;
import com.hsae.d531mc.bluetooth.music.entry.BluetoothDevice;
import com.hsae.d531mc.bluetooth.music.model.IBluetoothSettingModel;
import com.hsae.d531mc.bluetooth.music.observer.ContactsSubjecter;
import com.hsae.d531mc.bluetooth.music.service.BluetoothMusicModel;
import com.hsae.d531mc.bluetooth.music.util.MusicActionDefine;

/**
 * Bluetooth Settings
 * 
 * @author wangda
 *
 */
public class BluetoothSettingModel extends ContactsSubjecter implements
		IBluetoothSettingModel {

	private static final String TAG = "BluetoothSettingModel";
	private Context mContext;
	private BluetoothMusicModel mBluetoothModel;
	private int backcode = 0;
	private Handler mHandler = new Handler();
	private static final int conDelayTime = 1500;
	private static final int pairDelayTime = 1500;
	private List<BluetoothDevice> mListPairedBeans = new ArrayList<BluetoothDevice>();

	public BluetoothSettingModel(Context context) {
		super();
		this.mContext = context;
		init();
	}

	private void init() {
		LogUtil.i(TAG, "--- init +++");
		mBluetoothModel = BluetoothMusicModel.getInstance(mContext);
		mBluetoothModel
				.registBluetoothSettingListener((IBluetoothSettingModel) this);
	}

	@Override
	public void releaseModel() {
		LogUtil.i(TAG, "--- releaseModel +++");
		mBluetoothModel.unregistBluetoothSettingListener();
	}

	@Override
	public int getBluetoothVisibleDevices() {

		Thread inquiryThread = null;
		inquiryThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					backcode = mBluetoothModel.inquiryBtDevices(InquiryCallBack);
				} catch (RemoteException e) {
					Log.e(TAG, "inquiryThread-RemoteException = "
							+ e.toString());
				}
			}
		}, "inquiryThread");
		inquiryThread.start();
		LogUtil.i(TAG, "--- inquiryThread.star");
		return backcode;
	}

	private IAnwInquiryCallBackEx InquiryCallBack = new IAnwInquiryCallBackEx.Stub() {

		@Override
		public void InquiryDataRsp(String address, String strName, int cod,
				int RSSI, BT_ADV_DATA EirData, boolean bComplete)
				throws RemoteException {
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
							mBluetoothModel.getPairedList(mCount, Name,
									Address, COD);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
						for (int i = 0; i < mCount[0]; i++) {
							if (mAddress.equals(Address[i])) {
								return;
							}
						}
						BluetoothDevice bean = new BluetoothDevice(mName,
								mAddress, mBuilder.toString(), mRSSI,
								BluetoothDevice.DEVICE_UNPAIR);
						updateVisibleDevices(bean);
					} else {
						SearchFinish();
					}
				}
			});
		}
	};

	/**
	 * 返回搜索到的设备
	 * 
	 * @param bean
	 */
	private void updateVisibleDevices(BluetoothDevice bean) {
		Message msg_search = Message.obtain();
		msg_search.what = MusicActionDefine.ACTION_SETTING_INQUIRY_DEVICES;
		Bundle mBundle = new Bundle();
		mBundle.putSerializable("devciesbean", bean);
		msg_search.setData(mBundle);
		this.notify(msg_search, FLAG_RUN_SYNC);
	}

	/**
	 * 返回搜索停止
	 */
	private void SearchFinish() {
		Message msg_search = Message.obtain();
		msg_search.what = MusicActionDefine.ACTION_SETTING_INQUIRY_FINISH;
		this.notify(msg_search, FLAG_RUN_SYNC);
	}

	@Override
	public int stopInquiry() {
		try {
			backcode = mBluetoothModel.inquiryBtStop();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return backcode;
	}

	@Override
	public int devicePair(final String address, final String strCOD) {
		final Thread pairThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					backcode = mBluetoothModel.pair(address, "0000",
							Integer.parseInt(strCOD, 16));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}, "pairthread");

		if (isCurrentInquring()) {
			stopInquiry();
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					pairThread.start();
				}
			}, pairDelayTime);

		} else {
			pairThread.start();
		}
		return backcode;
	}

	/**
	 * 判断当前是否正在搜索设备
	 */
	@Override
	public boolean isCurrentInquring() {

		try {
			return mBluetoothModel.isCurrentInquiring();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获取配对列表
	 */
	@Override
	public List<BluetoothDevice> getPairedDevies() {

		int[] nCount = new int[1];
		String[] Name = new String[16];
		String[] Address = new String[16];
		int[] COD = new int[16];
		String[] strAddress = new String[1];
		String[] strName = new String[1];
		mListPairedBeans.clear();
		try {

			mBluetoothModel.getConnectedDeviceInfo(
					MangerConstant.PROFILE_HF_CHANNEL, strAddress, strName, 0);
			mBluetoothModel.getPairedList(nCount, Name, Address, COD);
			for (int i = 0; i < nCount[0]; i++) {
				BluetoothDevice bean;
				if (null != Address[i] && Address[i].equals(strAddress[0])) {
					bean = new BluetoothDevice(Name[i], Address[i],
							String.valueOf(COD[i]), 0,
							BluetoothDevice.DEVICE_CONNECTED);
					mListPairedBeans.add(bean);
				} else {
					bean = new BluetoothDevice(Name[i], Address[i],
							String.valueOf(COD[i]), 0,
							BluetoothDevice.DEVICE_PAIRED);
					mListPairedBeans.add(bean);
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		return mListPairedBeans;
	}

	@Override
	public String getConnectedDevice() {
		String[] strAddress = new String[1];
		String[] strName = new String[1];
		try {
			mBluetoothModel.getConnectedDeviceInfo(
					MangerConstant.PROFILE_HF_CHANNEL, strAddress, strName, 0);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return strAddress[0];
	}

	@Override
	public void updateConnectStatus(int status) {
		Message msg = Message.obtain();
		msg.what = MusicActionDefine.ACTION_SETTING_GET_PAIRED_DEVICES;
		this.notify(msg, FLAG_RUN_SYNC);
	}

	@Override
	public void updateDevicePair(String address, int status) {
		Message msg = Message.obtain();
		msg.what = MusicActionDefine.ACTION_SETTING_PAIR_STATUS_CHANGE;
		Bundle mBundle = new Bundle();
		mBundle.putString("pairAddress", address);
		mBundle.putInt("pairStatus", status);
		msg.setData(mBundle);
		this.notify(msg, FLAG_RUN_SYNC);
	}

	@Override
	public void disconnectMoblie() {
		try {
			mBluetoothModel.disconnectMobiel();
			mBluetoothModel.a2dpDisconnect();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int unpairDevice(String pairedAddress) {
		try {
			backcode = mBluetoothModel.unPair(pairedAddress);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return backcode;
	}

	@Override
	public void connectMoblie(final String connectAddress) {
		if (isCurrentInquring()) {
			stopInquiry();
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					try {
						mBluetoothModel.connectMobile(connectAddress);
						// mBluetoothModel.a2dpConnect(connectAddress);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}, conDelayTime);

		} else {
			try {
				mBluetoothModel.connectMobile(connectAddress);
				// mBluetoothModel.a2dpConnect(connectAddress);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String getLocalName() {
		String name = "";
		try {
			name = mBluetoothModel.getDeviceName();
			Log.e("wangda", "------- local name = " + name);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return name;
	}

	@Override
	public int getConnectStatus(int profile) {
		int backCode = -1;
		try {
			backCode = mBluetoothModel.getConnectStatus(profile, 0);
		} catch (RemoteException e) {
			Log.i(TAG, "--- GetConnectStatus --- RemoteException: "
					+ e.toString());
			e.printStackTrace();
		}
		return backCode;
	}

	@Override
	public void updateBtEnableStatus(int status) {
		Message msg = Message.obtain();
		msg.what = MusicActionDefine.ACTION_BLUETOOTH_ENABLE_STATUS_CHANGE;
		Bundle mBundle = new Bundle();
		mBundle.putInt("enableStatus", status);
		msg.setData(mBundle);
		this.notify(msg, FLAG_RUN_SYNC);
	}

	@Override
	public int getBTEnableStatus() {
		try {
			return mBluetoothModel.getBTPowerStatus();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return MangerConstant.BTPOWER_STATUS_OFF;
	}

}

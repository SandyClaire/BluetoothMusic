package com.hsae.d531mc.bluetooth.music.model.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;

import com.anwsdk.service.MangerConstant;
import com.hsae.autosdk.bt.phone.BtPhoneProxy;
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
public class BluetoothSettingModel extends ContactsSubjecter implements IBluetoothSettingModel {

	private static final String TAG = "MusicBTModel";
	private Context mContext;
	private BluetoothMusicModel mBluetoothModel;
	private List<BluetoothDevice> mListPairedBeans = new ArrayList<BluetoothDevice>();

	/**
	 * 配对列表最大数量
	 */
	private static final int PAIRED_DEVICES_MAX_NUM = 5;
	/**
	 * 搜索是否完成；
	 */
	private boolean mComplete = true;
	/**
	 * 当前连接地址
	 */
	private String mConnAddress = "";
	/**
	 * 配对线程
	 */
	private Thread pairThread;
	/**
	 * 是否执行配对
	 */
	private boolean ispairing = false;

	/**
	 * 是否执行连接
	 */
	private boolean isConnecting = false;

	/**
	 * 配对MAC地址
	 */
	private String pairAddress = "";

	public BluetoothSettingModel(Context context) {
		super();
		this.mContext = context;
		init();
	}

	private void init() {
		LogUtil.i(TAG, "--- init +++");
		mBluetoothModel = BluetoothMusicModel.getInstance(mContext);
		mBluetoothModel.registBluetoothSettingListener((IBluetoothSettingModel) this);
	}

	@Override
	public void releaseModel() {
		LogUtil.i(TAG, "--- releaseModel +++");
		stopInquiry();
		mBluetoothModel.unregistBluetoothSettingListener();
	}

	/**
	 * 返回搜索停止
	 */
	private void SearchFinish() {
		Message msg_search = Message.obtain();
		msg_search.what = MusicActionDefine.ACTION_SETTING_INQUIRY_FINISH;
		this.notify(msg_search, FLAG_RUN_SYNC);
		LogUtil.i(TAG, " --- SearchFinish");

		if (ispairing && pairThread != null) {
			pairThread.start();
			updatePairItem(pairAddress);
			ispairing = false;
			LogUtil.i(TAG, "SearchFinish --- pair");
		}
		if (isConnecting) {
			connect(mConnAddress);
			isConnecting = false;
			LogUtil.i(TAG, "SearchFinish --- connect");
		}
	}

	@Override
	public int stopInquiry() {
		int backcode = -1;
		try {
			backcode = mBluetoothModel.inquiryBtStop();
			if (backcode == -1) {
				SearchFinish();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		LogUtil.i(TAG, " --- stopInquiry");
		return backcode;
	}

	/**
	 * 发起配对
	 */
	@Override
	public void devicePair(final String address, final String strCOD) {
		pairAddress = address;
		pairThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					mBluetoothModel.pair(address, "0000", Integer.parseInt(strCOD, 16));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}, "pairthread");

		if (mComplete) {
			pairThread.start();
			updatePairItem(pairAddress);
		} else {
			ispairing = true;
			stopInquiry();
		}

	}

	/**
	 * 修改缓存中的数据状态
	 * 
	 * @param address
	 */
	private void updatePairItem(String address) {
		if (mBluetoothModel.mListDevices != null) {
			for (int i = 0; i < mBluetoothModel.mListDevices.size(); i++) {
				if (address.equals(mBluetoothModel.mListDevices.get(i).getAddress())) {
					mBluetoothModel.mListDevices.get(i).setStatus(BluetoothDevice.DEVICE_PAIRING);
				}
			}
		}
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

			mBluetoothModel.getConnectedDeviceInfo(MangerConstant.PROFILE_HF_CHANNEL, strAddress, strName, 0);
			mBluetoothModel.getPairedList(nCount, Name, Address, COD);
			for (int i = 0; i < nCount[0]; i++) {
				BluetoothDevice bean;
				if (nCount[0] < PAIRED_DEVICES_MAX_NUM) {
					if (null != Address[i] && Address[i].equals(strAddress[0])) {
						bean = new BluetoothDevice(Name[i], Address[i], String.valueOf(COD[i]), 0,
								BluetoothDevice.DEVICE_CONNECTED);
						mListPairedBeans.add(bean);
					} else {
						bean = new BluetoothDevice(Name[i], Address[i], String.valueOf(COD[i]), 0,
								BluetoothDevice.DEVICE_PAIRED);
						mListPairedBeans.add(bean);
					}
				} else if (i < nCount[0] && i >= (nCount[0] - PAIRED_DEVICES_MAX_NUM)) {
					if (null != Address[i] && Address[i].equals(strAddress[0])) {
						bean = new BluetoothDevice(Name[i], Address[i], String.valueOf(COD[i]), 0,
								BluetoothDevice.DEVICE_CONNECTED);
						mListPairedBeans.add(bean);
					} else {
						bean = new BluetoothDevice(Name[i], Address[i], String.valueOf(COD[i]), 0,
								BluetoothDevice.DEVICE_PAIRED);
						mListPairedBeans.add(bean);
					}
				} else {
					mBluetoothModel.unPair(Address[i]);
				}
				LogUtil.i(TAG, "Name[i] = " + Name[i]);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		LogUtil.i(TAG, " getPairedDevies -- SIZE = " + mListPairedBeans.size());

		return mListPairedBeans;
	}

	@Override
	public String getConnectedDevice() {
		String[] strAddress = new String[1];
		String[] strName = new String[1];
		try {
			mBluetoothModel.getConnectedDeviceInfo(MangerConstant.PROFILE_HF_CHANNEL, strAddress, strName, 0);
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
			mBluetoothModel.a2dpDisconnect();
			mBluetoothModel.disconnectMobiel();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int unpairDevice(String pairedAddress) {
		int backcode = -1;
		try {
			backcode = mBluetoothModel.unPair(pairedAddress);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return backcode;
	}

	@Override
	public void connectMoblie(final String connectAddress) {
		mConnAddress = connectAddress;
		LogUtil.i(TAG, "connectMoblie --- mComplete = " + mComplete);
		if (mComplete) {
			connect(connectAddress);
		} else {
			stopInquiry();
			isConnecting = true;
		}
		// 停止自动连接功能
		BtPhoneProxy.getInstance().settingStartBtConnect();
	}

	/**
	 * 连接手机
	 * 
	 * @param address
	 */
	private void connect(String address) {
		try {
			mBluetoothModel.connectMobile(address);
			LogUtil.i(TAG, "connect --- address = " + address);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getLocalName() {
		String name = "";
		try {
			name = mBluetoothModel.getDeviceName();
			LogUtil.e(TAG, "getLocalName -- name = " + name);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return name;
	}

	@Override
	public int getConnectStatus(int profile) {
		int status = -1;
		try {
			status = mBluetoothModel.getConnectStatus(profile, 0);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return status;
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

	@Override
	public void inquiryVisibleDevices() {
		mBluetoothModel.getBluetoothVisibleDevices();
	}

	@Override
	public void getVisibleDevices(BluetoothDevice bean, boolean complete) {
		mComplete = complete;
		if (complete) {
			SearchFinish();
		} else {
			Message msg_search = Message.obtain();
			msg_search.what = MusicActionDefine.ACTION_SETTING_INQUIRY_DEVICES;
			Bundle mBundle = new Bundle();
			mBundle.putSerializable("devciesbean", bean);
			msg_search.setData(mBundle);
			this.notify(msg_search, FLAG_RUN_SYNC);
			LogUtil.i(TAG, "getVisibleDevices Name = " + bean.getDeviceName());
		}
	}

	@Override
	public List<BluetoothDevice> getVisibleList() {
		return mBluetoothModel.mListDevices;
	}

	@Override
	public boolean getCarplayConnectstatus() {
		return mBluetoothModel.isCarPlayConnected;
	}

	@Override
	public void updateCarplayConnectStatus() {
		Message msg = Message.obtain();
		msg.what = MusicActionDefine.ACTION_SETTING_GET_CARPLAY_STATUS;
		this.notify(msg, FLAG_RUN_MAIN_THREAD);
	}

	@Override
	public boolean getCarLifeConnectstatus() {
		return mBluetoothModel.isCarlifeConnected();
	}

	@Override
	public void updateCarlifeConnectStatus() {
		Message msg = Message.obtain();
		msg.what = MusicActionDefine.ACTION_SETTING_GET_CARLIFE_STATUS;
		this.notify(msg, FLAG_RUN_MAIN_THREAD);
	}

	@Override
	public void onInquiryCallBack(Integer result) {
		Bundle data = new Bundle();
		Message msg = Message.obtain();
		data.putInt("code", result);
		msg.setData(data);
		msg.what = MusicActionDefine.ACTION_SEACH_CALLBACK;
		this.notify(msg, FLAG_RUN_SYNC);
	}

}

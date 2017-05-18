package com.hsae.d531mc.bluetooth.music.presenter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.anwsdk.service.MangerConstant;
import com.hsae.autosdk.util.LogUtil;
import com.hsae.d531mc.bluetooth.music.entry.BluetoothDevice;
import com.hsae.d531mc.bluetooth.music.model.IBluetoothSettingModel;
import com.hsae.d531mc.bluetooth.music.observer.IObserver;
import com.hsae.d531mc.bluetooth.music.observer.ISubject;
import com.hsae.d531mc.bluetooth.music.util.MusicActionDefine;
import com.hsae.d531mc.bluetooth.music.view.IBluetoothSettingView;

/**
 * 
 * @author wangda
 *
 */
public class BluetoothSettingPresenter implements IObserver {

	private static final String TAG = "MusicBTPresenter";
	private IBluetoothSettingModel mBluetoothSettingModel;
	private IBluetoothSettingView mIBluetoothSettingView;

	public BluetoothSettingPresenter(IBluetoothSettingModel btModel,
			IBluetoothSettingView btSettingsView, Context mContext) {
		super();
		this.mBluetoothSettingModel = btModel;
		this.mIBluetoothSettingView = btSettingsView;
	}

	@Override
	public void listen(Message inMessage) {
		switch (inMessage.what) {
		case MusicActionDefine.ACTION_APP_LAUNCHED:
			init();
			break;
		case MusicActionDefine.ACTION_APP_EXIT:
			exit();
			break;
		case MusicActionDefine.ACTION_SETTING_GET_CAPLAY_STATUS:
			boolean conn = mBluetoothSettingModel.getCarplayConnectstatus();
			mIBluetoothSettingView.updateTextTipShow(conn);
			break;
		case MusicActionDefine.ACTION_BLUETOOTH_ENABLE_STATUS_CHANGE :
			int enableStatus = inMessage.getData().getInt("enableStatus");
			mIBluetoothSettingView.updateBtEnable(enableStatus);
			if (enableStatus == MangerConstant.BTPOWER_STATUS_ON) {
				List<BluetoothDevice> pairedList = mBluetoothSettingModel
						.getPairedDevies();
				mIBluetoothSettingView.showPairedDevices(pairedList);
			}
			break;
		case MusicActionDefine.ACTION_SETTING_INQUIRY:
			mBluetoothSettingModel.inquiryVisibleDevices();
			break;
		case MusicActionDefine.ACTION_SETTING_STOP_INQUIRY:
			mBluetoothSettingModel.stopInquiry();
			break;
			
		case MusicActionDefine.ACTION_SETTING_PAIR:
			String address = inMessage.getData().getString("address");
			String strCOD = inMessage.getData().getString("strcod");
			mBluetoothSettingModel.devicePair(address, strCOD);
			LogUtil.i(TAG, "--- pair  -  strCOD = " + strCOD + "address" + address);
			break;
		case MusicActionDefine.ACTION_SETTING_INQUIRY_DEVICES:
			BluetoothDevice bean = (BluetoothDevice) inMessage.getData()
					.getSerializable("devciesbean");
			mIBluetoothSettingView.showVisibleDevices(bean);
			break;
		case MusicActionDefine.ACTION_SETTING_INQUIRY_FINISH:
			mIBluetoothSettingView.inquiryFinish();
			break;
		case MusicActionDefine.ACTION_SETTING_PAIR_STATUS_CHANGE:
			int pairStatus = inMessage.getData().getInt("pairStatus");
			String pairAddress = inMessage.getData().getString("pairAddress");
			mIBluetoothSettingView.updateUnpairListByStatus(pairStatus,
					pairAddress);
			if (pairStatus == MangerConstant.Anw_SUCCESS) {
				List<BluetoothDevice> pairedList = mBluetoothSettingModel
						.getPairedDevies();
				mIBluetoothSettingView.showPairedDevices(pairedList);
				connectMoblie(pairAddress);
				mIBluetoothSettingView.showConnecttingStatus(pairAddress);
			}
			break;
		case MusicActionDefine.ACTION_SETTING_DISCONNECT_MOBILE:
			mBluetoothSettingModel.disconnectMoblie();
			break;
		case MusicActionDefine.ACTION_SETTING_UNPAIR:
			String pairedAddress = inMessage.getData()
					.getString("pairdAddress");
			int code = mBluetoothSettingModel.unpairDevice(pairedAddress);
			if (code == MangerConstant.Anw_SUCCESS) {
				List<BluetoothDevice> pairedList = mBluetoothSettingModel
						.getPairedDevies();
				mIBluetoothSettingView.showPairedDevices(pairedList);
			}
			break;
		case MusicActionDefine.ACTION_SETTING_CONNECT_MOBILE:
			String conAddress = inMessage.getData().getString("connectAddress");
			connectMoblie(conAddress);
			// mBluetoothSettingModel.connectMoblie(conAddress);
			break;
		case MusicActionDefine.ACTION_SETTING_GET_PAIRED_DEVICES:
			List<BluetoothDevice> pairedList = mBluetoothSettingModel
					.getPairedDevies();
			mIBluetoothSettingView.showPairedDevices(pairedList);
			if (isConnect) {
				Message msg = Message.obtain();
				msg.what = DISCONNECT_SUSSECC;
				mHandler.sendMessage(msg);
				isConnect = false;
			}
			break;

		default:
			break;
		}
	}

	private static final int DISCONNECT_SUSSECC = 0;

	private boolean isConnect = false;

	private String connAddress = "";

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case DISCONNECT_SUSSECC:
				if (!connAddress.equals("")) {
					mIBluetoothSettingView.showConnecttingStatus(connAddress);
					mBluetoothSettingModel.connectMoblie(connAddress);
					connAddress = "";
				}
				break;
			}
		}
	};

	private void connectMoblie(final String address) {
		int conn = mBluetoothSettingModel
				.getConnectStatus(MangerConstant.PROFILE_HF_CHANNEL);
		if (conn == MangerConstant.Anw_SUCCESS) {
			connAddress = address;
			mBluetoothSettingModel.disconnectMoblie();
			isConnect = true;
		} else {
			mBluetoothSettingModel.connectMoblie(address);
		}
	}

	private void init() {
		LogUtil.i(TAG, "--- init +++");
		((ISubject) mBluetoothSettingModel).attach(this);
		((ISubject) mIBluetoothSettingView).attach(this);
		initBtName();
		initBtEnableStatus();
	}

	private void initBtName() {
		String name  = mBluetoothSettingModel.getLocalName();
		mIBluetoothSettingView.showLocalName(name);
	}

	private void initBtEnableStatus() {
		InitBTEnableTask enableTask = new InitBTEnableTask();
		enableTask.execute();
	}

	class InitBTEnableTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			return mBluetoothSettingModel.getBTEnableStatus();
		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(Integer result) {
			LogUtil.i(TAG, "--- InitBTEnableTask enable = " + result);
			mIBluetoothSettingView.updateBtEnable(result);
			if (result == MangerConstant.BTPOWER_STATUS_ON) {
				List<BluetoothDevice> pairedList = mBluetoothSettingModel
						.getPairedDevies();
				mIBluetoothSettingView.showPairedDevices(pairedList);
				List<BluetoothDevice> visibleList = mBluetoothSettingModel
						.getVisibleList();
				mIBluetoothSettingView.initVisibleList(visibleList);
			}
		}
	}

	private void exit() {
		LogUtil.i(TAG, "--- exit +++");
		mBluetoothSettingModel.releaseModel();
		((ISubject) mBluetoothSettingModel).detach(this);
		((ISubject) mIBluetoothSettingView).detach(this);
	}

}

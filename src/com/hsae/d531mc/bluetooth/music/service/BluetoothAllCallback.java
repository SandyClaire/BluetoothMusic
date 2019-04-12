package com.hsae.d531mc.bluetooth.music.service;

import com.anwsdk.service.MangerConstant;

import android.os.IBinder;
import android.os.RemoteException;

public interface BluetoothAllCallback {

	public void onA2dpStatusChanged(int status);

	public void onPlayStatusChanged(int state);//strAction.equals(MangerConstant.MSG_ACTION_A2DP_PLAYSTATUS)

	public void onPositionChanged(String position);//MangerConstant.MSG_ACTION_A2DP_PLAYBACKPOS

	public void onID3Changed(String title, String album, String artist,
			String totalTime);//(strAction.equals(MangerConstant.MSG_ACTION_A2DP_METADATA))

	public void onPlayModelChanged(int modelStatus);//strAction.equals(MangerConstant.MSG_ACTION_AVRCP_PLAYERSETTING_CHANGED_EVENT)

	public void onConnectStateChanged(int profile, int state, int reason);//(strAction.equals(MangerConstant.MSG_ACTION_CONNECT_STATUS))

	public void onPairStateChanged(String address, int status);//strAction.equals(MangerConstant.MSG_ACTION_PAIR_STATUS)
														
	public void onPowerStateChanged(int state);//strAction.equals(MangerConstant.MSG_ACTION_POWER_STATUS)

}

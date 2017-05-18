package com.hsae.d531mc.bluetooth.music.model;

import java.util.List;

import com.hsae.d531mc.bluetooth.music.entry.BluetoothDevice;


/**
 * 
 * @author wangda
 *
 */
public interface IBluetoothSettingModel {

	/**
	 * release model
	 */
	public void releaseModel();

	/**
	 * get bluetooth visible devcies
	 * 
	 * @param inqCallBack
	 * @return
	 */
	public int getBluetoothVisibleDevices();
	
	/**
	 * stop inquiry
	 * @return
	 */
	public int stopInquiry();
	
	/**
	 * 
	 * device pair
	 * 
	 * @param address
	 * @param strCOD
	 * @return
	 */
	public int devicePair(String address , String strCOD);
	
	
	/**
	 * get inquiry current status
	 * @return
	 */
	public boolean isCurrentInquring();
	
	/**
	 * get paired device list
	 * @return
	 */
	public List<BluetoothDevice> getPairedDevies();
	
	/**
	 * get connected devcie address
	 * @return
	 */
	public String getConnectedDevice();
	
	/**
	 * update device pair for service
	 * @param address
	 * @param status
	 */
	public void updateDevicePair(String address , int status);

	/**
	 * disconnect device
	 */
	public void disconnectMoblie();
	
	/**
	 * connect device
	 * @param connectAddress
	 */
	public void connectMoblie(String connectAddress);

	/**
	 * unpair devcie
	 * @param pairedAddress
	 * @return
	 */
	public int unpairDevice(String pairedAddress);
	
	/**
	 * get local name
	 * @return
	 */
	public String getLocalName() ;
	
	/**
	 * get connect status
	 * @param profile
	 * @return
	 */
	public int getConnectStatus(int profile);

}

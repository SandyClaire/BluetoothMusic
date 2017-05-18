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
	public void inquiryVisibleDevices();

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
	public void devicePair(String address , String strCOD);
	
	
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
	
	/**
	 * update bluetooth enable status
	 * @param status
	 */
	public void updateBtEnableStatus(int status);
	
	/**
	 * get bluetooth enable status
	 * @return
	 */
	public int getBTEnableStatus();
	
	/**
	 * get visible devices
	 * @param list
	 */
	public void getVisibleDevices(BluetoothDevice bean, boolean complete);

	/**
	 * 获取缓存可用设备数据
	 */
	public List<BluetoothDevice> getVisibleList();
	
	/**
	 * 更新连接状态
	 * @param status
	 */
	public void updateConnectStatus(int status);

	/**
	 * 获取carplay连接状态
	 * @return
	 */
	public boolean getCarplayConnectstatus();
	
	/**
	 * 更新carplay连接状态
	 */
	public void updateCarplayConnectStatus();
	
}

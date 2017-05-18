package com.hsae.d531mc.bluetooth.music.view;

import java.util.List;

import com.hsae.d531mc.bluetooth.music.entry.BluetoothDevice;

/**
 * 
 * @author wangda
 *
 */
public interface IBluetoothSettingView {

	/**
	 * get remote bluetooth devices
	 * 
	 * @param bean
	 */
	public void showVisibleDevices(BluetoothDevice bean);

	/**
	 * inquiry finish
	 */
	public void inquiryFinish();
	
	/**
	 * update llist show after click upair item
	 * @param status
	 * @param bean
	 */
	public void updateUnpairListByStatus(int status , String address);
	
	/**
	 * show paired list
	 * @param mList
	 */
	public void showPairedDevices(List<BluetoothDevice> mList);
	
	/**
	 * show local name
	 */
	public void showLocalName(String name);
	
	/**
	 * show connect other devices
	 * @param address
	 */
	public void showConnecttingStatus(String address);
	
}

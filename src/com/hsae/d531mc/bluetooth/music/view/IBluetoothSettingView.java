//package com.hsae.d531mc.bluetooth.music.view;
//
//import java.util.List;
//
//import com.hsae.d531mc.bluetooth.music.entry.BluetoothDevice;
//
///**
// * 
// * @author wangda
// *
// */
//public interface IBluetoothSettingView {
//
//	/**
//	 * get remote bluetooth devices
//	 * 
//	 * @param bean
//	 */
//	public void showVisibleDevices(BluetoothDevice bean);
//
//	/**
//	 * inquiry finish
//	 */
//	public void inquiryFinish();
//	
//	/**
//	 * update llist show after click upair item
//	 * @param status
//	 * @param bean
//	 */
//	public void updateUnpairListByStatus(int status , String address);
//	
//	/**
//	 * show paired list
//	 * @param mList
//	 */
//	public void showPairedDevices(List<BluetoothDevice> mList);
//	
//	/**
//	 * show local name
//	 */
//	public void showLocalName(String name);
//	
//	/**
//	 * show connect other devices
//	 * @param address
//	 */
//	public void showConnecttingStatus(String address);
//	
//	
//	/**
//	 * update bluetooth enable status
//	 * @param status
//	 */
//	public void updateBtEnable(int status);
//	
//	/**
//	 * int visible list
//	 * @param list
//	 */
//	public void initVisibleList(List<BluetoothDevice> list);
//	
//	/**
//	 * 更细显示提示语
//	 * @param i
//	 */
//	public void updateTextTipShow(int i);
//
//	public void isSearching(boolean currentInquring);
//
//	public void setButtonClickable(boolean b);
//
//
//}

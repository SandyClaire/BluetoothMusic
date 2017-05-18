package com.hsae.d531mc.bluetooth.music.entry;

import java.io.Serializable;

/**
 * 
 * @author wangda
 *
 */
public class BluetoothDevice implements Serializable {

	private static final long serialVersionUID = 1L;
	private String deviceName;
	private String address;
	private String strCOD;
	private int nRSSI;
	private int status;
	public static final String PREFERENCE_NAME = "D531_preference" ;
	public static final String CURRENT_BLUETOOTHDEVICE_ADDRESS = "lastDeviceAddress" ;
	
	public static final int DEVICE_UNPAIR = 0 ;
	public static final int DEVICE_PAIRED = 1 ;
	public static final int DEVICE_CONNECTED = 2 ;
	public static final int DEVICE_PAIRING = 4 ;
	public static final int DEVICE_UNPAIRING = 5 ;
	public static final int DEVICE_CONNECTING = 6 ;
	public static final int DEVICE_DISCONNECTING =7 ;

	public BluetoothDevice(String deviceName, String address, String strCOD,
			int nRSSI, int status) {
		super();
		this.deviceName = deviceName;
		this.address = address;
		this.strCOD = strCOD;
		this.nRSSI = nRSSI;
		this.status = status;
	}

	public BluetoothDevice() {
		super();
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getnRSSI() {
		return nRSSI;
	}

	public void setnRSSI(int nRSSI) {
		this.nRSSI = nRSSI;
	}

	public String getStrCOD() {
		return strCOD;
	}

	public void setStrCOD(String strCOD) {
		this.strCOD = strCOD;
	}

	/**
	 * 0:unpair device
	 * 
	 * 1:paired devcie
	 * 
	 * 2:connected devcie
	 * 
	 * @return
	 */
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}

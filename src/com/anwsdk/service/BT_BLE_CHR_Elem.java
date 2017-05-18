package com.anwsdk.service;

import android.os.Parcel;

public class BT_BLE_CHR_Elem {
	public BT_Service uuid;
	public int handle; /* unsigned short */
	public int properties; /* unsigned char */
	public int value_handle; /* unsigned short */
	public int desnum; /* unsigned char description conut */
	public BT_BLE_DES_Elem des_elem[]; /* description detail information */

	public void setService(BT_Service data) {
		uuid = data;
	}

	public BT_Service getService() {
		return uuid;
	}

	public void setHandle(int h) {
		handle = h & 0xFFFF;
	}

	public int getHandle() {
		return handle;
	}

	public void setProperties(int pro) {
		properties = pro & 0xFF;
	}

	public int getProperties() {
		return properties;
	}

	public void setValueHandle(int h) {
		value_handle = h & 0xFFFF;
	}

	public int getValueHandle() {
		return value_handle;
	}

	public void setDesNum(int num) {
		desnum = num & 0xFF;
	}

	public int getDesNum() {
		return desnum;
	}

	public void writeToParcel(Parcel arg0, int arg1) {
		uuid.writeToParcel(arg0, arg1);
		arg0.writeInt(handle);
		arg0.writeInt(properties);
		arg0.writeInt(value_handle);
		arg0.writeInt(desnum);
		for (int i = 0; i < desnum; i++) {
			des_elem[i].writeToParcel(arg0, arg1);
		}
	}

	public void readFromParcel(Parcel in) {
		uuid = new BT_Service();
		uuid.readFromParcel(in);
		handle = in.readInt();

		properties = in.readInt();
		value_handle = in.readInt();
		desnum = in.readInt();
		if (desnum > 0)
			des_elem = new BT_BLE_DES_Elem[desnum];
		else
			des_elem = null;

		for (int i = 0; i < desnum; i++) {
			des_elem[i] = new BT_BLE_DES_Elem();
			des_elem[i].readFromParcel(in);
		}
	}

	public BT_BLE_CHR_Elem(BT_Service ser, int h, int pro, int v_handle,
			int num, BT_BLE_DES_Elem elem[]) {
		uuid = ser;
		handle = h & 0xFFFF;
		properties = pro & 0xFF;
		value_handle = v_handle & 0xFFFF;
		desnum = num & 0xFF;
		if (desnum > 0 && elem != null) {
			des_elem = new BT_BLE_DES_Elem[elem.length];
			System.arraycopy(elem, 0, des_elem, 0, elem.length);
		} else
			des_elem = null;
	}

	public BT_BLE_CHR_Elem() {
		uuid = new BT_Service();
		handle = 0;
		properties = 0;
		value_handle = 0;
		desnum = 0;

		des_elem = null;
	}
}
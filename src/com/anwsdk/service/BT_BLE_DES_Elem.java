package com.anwsdk.service;

import android.os.Parcel;

public class BT_BLE_DES_Elem {
	public BT_Service 	uuid;
	public int   		handle; /*unsigned short*/
	
	public void setService(BT_Service data)
	{
		uuid = data;
	}
	public BT_Service getService()
	{
		return uuid ;
	}
	
	public void sethandle(int h) {
		handle = h & 0xFFFF;;
	}	
	public int gethandle() {
		return handle;
	}
	public void writeToParcel(Parcel arg0, int arg1)
	{
		uuid.writeToParcel(arg0,arg1);
		arg0.writeInt(handle);

	}
	public void readFromParcel(Parcel in) {
		uuid = new BT_Service();
		uuid.readFromParcel(in);
		handle = in.readInt();

	}
	public BT_BLE_DES_Elem(BT_Service ser, int h)
	{
		uuid = ser;
		handle = h & 0xFFFF;;
	
	}
	public BT_BLE_DES_Elem()
	{
		uuid = new BT_Service();
		handle =0;
	}
}
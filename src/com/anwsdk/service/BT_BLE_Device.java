package com.anwsdk.service;
import android.os.Parcel;
import android.os.Parcelable;

public class BT_BLE_Device implements Parcelable {
	public String 			bt_address;
	public int			addrType;
	public int			eventType;
	public int 			rssi;
	public BT_ADV_DATA  	adv_data;

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		arg0.writeString(bt_address);
		arg0.writeInt(addrType);
		arg0.writeInt(eventType);
		arg0.writeInt(rssi);
		adv_data.writeToParcel(arg0, arg1);

		
	}
	public void readFromParcel(Parcel in) {
		bt_address = in.readString();
		addrType = in.readInt();
		eventType = in.readInt();
		rssi = in.readInt();
		adv_data = new BT_ADV_DATA(in);
	}
	public static final Parcelable.Creator<BT_BLE_Device> CREATOR =
			new Parcelable.Creator<BT_BLE_Device>() {
			public BT_BLE_Device createFromParcel(Parcel in) {
				return new BT_BLE_Device(in);
			}
			public BT_BLE_Device[] newArray(int size) {
				return new BT_BLE_Device[size];
			}
		};	
	private BT_BLE_Device(Parcel in) {
			readFromParcel(in);
	}		
	public BT_BLE_Device(String Addr, int AddrType, int EventType,  int RSSI,BT_ADV_DATA advdata) {
		bt_address = Addr;
		addrType = AddrType;
		eventType = EventType;
		rssi = RSSI;
		adv_data = advdata;
		
	}
		
	public BT_BLE_Device() {
		bt_address ="";
		addrType =0;
		eventType =0;
		rssi = 0;
	}
}
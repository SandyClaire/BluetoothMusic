package com.anwsdk.service;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class BT_ADV_DATA implements Parcelable {

	public int 			ad_mask;  /*unsigned short*//* mask of the valid BT_ADV_DATA data field */ 
	public String 			bt_name;
	public int 			flag; /*unsigned char*/ // Limited Discoverable Mode ,General Discoverable Mode,BR/EDR Not Supported,Simultaneous LE and BR/EDR to Same Device Capable (Controller), Simultaneous LE and BR/EDR to Same Device Capable (Host)
	public int 			service_count;/*unsigned char*/
	public BT_Service 		service[];
	public int 			tx_power;/*unsigned char*/	/*tx power*/
	public int 			appearence;/*unsigned short*/	/*appearence*/
	public BT_ServiceData	service_data;
	public int 			int_range_low;     /*unsigned short*/ /* slave prefered conn interval range - low*/
	public int			int_range_hi;     /*unsigned short*/  /* slave prefered conn interval range - high*/
	public int 			other_elem_count;/*unsigned char*/
	public BT_ADV_Element	other_elem[];  /*ANW_BLE_MAX_ADV_ELEMENT_COUNT 16*/

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		int i =0;
		arg0.writeInt(ad_mask);
		arg0.writeString(bt_name);
		arg0.writeInt(flag);
		arg0.writeInt(service_count);

		for(i = 0;i<service_count;i++) {
			//writeServiceToParcel(arg0,service[i]);
			service[i].writeToParcel(arg0,arg1);
		}		
		arg0.writeInt(tx_power);
		arg0.writeInt(appearence);
		writeServiceDataToParcel(arg0,arg1);
		
		arg0.writeInt(int_range_low);
		arg0.writeInt(int_range_hi);
		arg0.writeInt(other_elem_count);
		for(i = 0;i<other_elem_count;i++) {
			writeElementToParcel(arg0,i);
		}		
				
	}
/*	public void writeServiceToParcel(Parcel arg0,BT_Service ser) {
		// TODO Auto-generated method stub
		arg0.writeInt(ser.service_type.getValue());
		arg0.writeInt(ser.uuid_16b);
		arg0.writeLong(ser.uuid_32b);
		arg0.writeByteArray(ser.uuid_128b);
	}	*/	

	public void writeServiceDataToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
	//	writeServiceToParcel(arg0,service_data.service);
		service_data.service.writeToParcel(arg0,arg1);
		arg0.writeInt(service_data.data_len);
		if(service_data.data_len >0)
			arg0.writeByteArray(service_data.service_data);
	}
	public void writeElementToParcel(Parcel arg0,int nIndex) {
		// TODO Auto-generated method stub
		arg0.writeInt(other_elem[nIndex].element_type);
		arg0.writeInt(other_elem[nIndex].data_len);
		if(other_elem[nIndex].data_len >0)
			arg0.writeByteArray(other_elem[nIndex].element_data);

	}	
	public void readFromParcel(Parcel in) {
		int i =0;
		ad_mask = in.readInt();
		bt_name = in.readString();
		flag = in.readInt();
		service_count = in.readInt();
		
		if(service_count >0)
			service = new BT_Service[service_count];
		else 
			service = null;
		for(i = 0;i<service_count;i++) {
			service[i] = new BT_Service();
		//	readServiceFromParcel(in,service[i]);
			service[i].readFromParcel(in);
		}
		tx_power = in.readInt();
		appearence = in.readInt();
		readServiceDataFromParcel(in);
		int_range_low = in.readInt();
		int_range_hi = in.readInt();
		other_elem_count = in.readInt();
		if(other_elem_count >0)
			other_elem = new BT_ADV_Element[other_elem_count];
		else
			other_elem = null;
		
		
		for(i = 0;i<other_elem_count;i++) {
			other_elem[i] = new BT_ADV_Element();
			readElementFromParcel(in,i);
		}

	}	
/*	public void readServiceFromParcel(Parcel in,BT_Service ser)	{
		int type = in.readInt();
		ser.SetServiceType(type);
		ser.uuid_16b = in.readInt();
		ser.uuid_32b = in.readLong();
		ser.uuid_128b = new byte[16];
		in.readByteArray(ser.uuid_128b);
	}*/
	public void readServiceDataFromParcel(Parcel in)	{
		BT_Service ser = new BT_Service();
		//readServiceFromParcel(in,ser);
		ser.readFromParcel(in);
		service_data = new BT_ServiceData();
		service_data.service = ser;
		service_data.data_len = in.readInt();	
		if(service_data.data_len>0)
		{
			service_data.service_data = new byte[service_data.data_len];
			in.readByteArray(service_data.service_data );
		}
	}
	public void readElementFromParcel(Parcel in,int nIndex)	{
		other_elem[nIndex].element_type = in.readInt();	
		other_elem[nIndex].data_len = in.readInt();	
		if(other_elem[nIndex].data_len>0)
		{
			other_elem[nIndex].element_data = new byte[other_elem[nIndex].data_len];
			in.readByteArray(other_elem[nIndex].element_data);
		
		}
		
	}
	public static final Parcelable.Creator<BT_ADV_DATA> CREATOR =
			new Parcelable.Creator<BT_ADV_DATA>() {
			public BT_ADV_DATA createFromParcel(Parcel in) {
				return new BT_ADV_DATA(in);
			}
			public BT_ADV_DATA[] newArray(int size) {
				return new BT_ADV_DATA[size];
			}
		};

	public BT_ADV_DATA(Parcel in) {
		readFromParcel(in);
	}

		
		
	public void setMask(int value) {
		ad_mask = value;
	}

	public int getMask() {
		return ad_mask;
	}	
	public void setBtName(String value) {
		bt_name = value;
	}

	public String getBtName() {
		return bt_name;
	}		
	public void setFlag(int value) {
		flag = value;
	}

	public int getFlag() {
		return flag;
	}	
	public void setServiceCount(int value) {
		service_count = value;
	}

	public int getServiceCount() {
		return service_count;
	}
	
	public BT_Service getService(int i) {
		if(i >=service_count )
		{
			return null;
		}
		else
			return service[i];
	}	
	
	public void setTxPower(int value) {
		tx_power = value;
	}

	public int getTxPower() {
		return tx_power;
	}	
	public void setAppearence(int value) {
		appearence = value;
	}

	public int getAppearence() {
		return appearence;
	}	
	
	public void setServiceData(BT_ServiceData data) {
		service_data = data;
	}
	public BT_ServiceData getServiceData() {
		return service_data ;
	}	
	
	public void setRangeLow(int value) {
		int_range_low = value;
	}

	public int getRangeLow() {
		return int_range_low;
	}		
	public void setRangeHigh(int value) {
		int_range_hi = value;
	}

	public int getRangeHigh() {
		return int_range_hi;
	}		
	public void setOtherElemCount(int value) {
		other_elem_count = value;
	}

	public int getOtherElemCount() {
		return other_elem_count;
	}		
	
	public BT_ADV_Element getElement(int i) {
		if(i >=other_elem_count )
		{
			return null;
		}
		else
			return other_elem[i];
	}		
	public BT_ADV_DATA(int mask, String name,byte mflag,byte mservice_count,BT_Service mservice[],byte mtx_power,
			int mappearence,BT_ServiceData mservice_data,int mint_range_low,int mint_range_hi,byte mother_elem_count,BT_ADV_Element mother_elem[] ) {
		ad_mask =  mask & 0xFFFF; //signed to unsigned
		bt_name = name;
		flag = mflag & 0xFF;
		service_count = mservice_count & 0xFF;
		
		if(mservice_count >0 && mservice !=null)
		{
			service = new BT_Service[mservice.length];
			System.arraycopy(mservice, 0, service, 0, mservice.length);
		}
		else 
			service = null;		

		tx_power = mtx_power & 0xFF;
		appearence =  mappearence & 0xFFFF;
		service_data = mservice_data;
		int_range_low = mint_range_low & 0xFFFF;
		int_range_hi = mint_range_hi & 0xFFFF;
		other_elem_count = mother_elem_count & 0xFF;

		if(other_elem_count >0 && mother_elem!=null)
		{
			other_elem = new BT_ADV_Element[mother_elem.length];
			System.arraycopy(mother_elem, 0, other_elem, 0, mother_elem.length);
		}
		else
			other_elem = null;

	}
	public BT_ADV_DATA() {
		ad_mask =  0;
		bt_name = "";
		flag = 0;
		service_count =0;
		
		service = null;

		tx_power = 0;
		appearence =  0;
		service_data = new BT_ServiceData();
		int_range_low = 0;
		int_range_hi = 0;
		other_elem_count = 0;

		other_elem = null;

	}
}
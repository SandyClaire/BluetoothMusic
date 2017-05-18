package com.anwsdk.service;
import android.os.Parcel;
import android.os.Parcelable;

public class BT_BLE_Service implements Parcelable {
	public BT_Service 		uuid;
	public int 				start;/*unsigned short att_range*/
	public int 				end;/*unsigned short att_range*/
		
	public int 			chrnum;/*unsigned char characteristic count*/
	public BT_BLE_CHR_Elem		chr_elem[];  /*characteristic detail information*/


	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		uuid.writeToParcel(arg0,arg1);
		arg0.writeInt(start);
		arg0.writeInt(end);
		arg0.writeInt(chrnum);
		for(int i = 0;i<chrnum;i++) {
			chr_elem[i].writeToParcel(arg0,arg1);
		}		

		
	}
	public void readFromParcel(Parcel in) {
		uuid = new BT_Service();
		uuid.readFromParcel(in);
		start = in.readInt();
		end = in.readInt();
		
		chrnum = in.readInt();
		if(chrnum >0)
			chr_elem = new BT_BLE_CHR_Elem[chrnum];
		else
			chr_elem = null;
		
		
		for(int i = 0;i<chrnum;i++) {
			chr_elem[i] = new BT_BLE_CHR_Elem();
			chr_elem[i].readFromParcel(in);
		}
	}
	public static final Parcelable.Creator<BT_BLE_Service> CREATOR =
			new Parcelable.Creator<BT_BLE_Service>() {
			public BT_BLE_Service createFromParcel(Parcel in) {
				return new BT_BLE_Service(in);
			}
			public BT_BLE_Service[] newArray(int size) {
				return new BT_BLE_Service[size];
			}
		};	
	private BT_BLE_Service(Parcel in) {
			readFromParcel(in);
	}		
	public BT_BLE_Service(BT_Service ser, int startindex, int endindex, int num,BT_BLE_CHR_Elem elem[]) {
		uuid = ser;
		start = startindex;
		end = endindex;
		chrnum = num & 0xFF;
		if(chrnum  >0 && elem !=null)
		{
			chr_elem = new BT_BLE_CHR_Elem[elem.length];
			System.arraycopy(elem, 0, chr_elem, 0, elem.length);
		}
		else 
			chr_elem = null;		

		
	}
		
	public BT_BLE_Service() {
		uuid  = new BT_Service();
		start = 0;
		end = 0;
		chrnum =0;
		chr_elem = null;
	}
}
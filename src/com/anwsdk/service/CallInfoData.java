package com.anwsdk.service;

import android.os.Parcel;
import android.os.Parcelable;

public class CallInfoData implements Parcelable {
	
	public String	CallNumber;
	public String	CallName;
	public int	nCallStatus;
	public int	nCallIndex;
	
	public void setCallStatus(int value) {
		nCallStatus = value;
	}
	public int getCallStatus() {
		return nCallStatus;
	}
	public void setCallIndex(int value) {
		nCallIndex = value;
	}
	public int getCallIndex() {
		return nCallIndex;
	}

	
	public void setCallNumber(String value) {
		CallNumber = value;
	}

	public String getCallNumber() {
		return CallNumber;
	}
	
	public void setCallName(String value) {
		CallName = value;
	}

	public String getCallName() {
		return CallName;
	}
	
	
	public CallInfoData(String strNumber,String strName,int nStatus,int nIndex) 
	{
		nCallStatus = nStatus;
		CallNumber = strNumber;
		CallName = strName;
		nCallIndex = nIndex;
	}
	
	public CallInfoData() {
		nCallStatus = 0;
		CallNumber = "";
		CallName = "";
		nCallIndex = 0;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		arg0.writeInt(nCallStatus);
		arg0.writeString(CallNumber);
		arg0.writeString(CallName);
		arg0.writeInt(nCallIndex);


	}
	public void readFromParcel(Parcel in) {
		nCallStatus = in.readInt();
		CallNumber = in.readString();		
		CallName = in.readString();		
		nCallIndex = in.readInt();

	}	

	public static final Parcelable.Creator<CallInfoData> CREATOR =
		new Parcelable.Creator<CallInfoData>() {
		public CallInfoData createFromParcel(Parcel in) {
			return new CallInfoData(in);
		}
		public CallInfoData[] newArray(int size) {
			return new CallInfoData[size];
		}
	};

	private CallInfoData(Parcel in) {
		readFromParcel(in);
	}
}

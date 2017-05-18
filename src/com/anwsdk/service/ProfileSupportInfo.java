package com.anwsdk.service;

import android.os.Parcel;
import android.os.Parcelable;

public class ProfileSupportInfo implements Parcelable {

	public int	bSupport;
	public int	nScn;
	public int	nProfile_version;
	public int	nSupported_features;
	
	public void setSupport(int value) {
		bSupport = value;
	}
	public int getSupport() {
		return bSupport;
	}

	public void setScn(int value) {
		nScn = value;
	}
	public int getScn() {
		return nScn;
	}
	
	public void setProfileversion(int value) {
		nProfile_version = value;
	}
	public int getProfileversion() {
		return nProfile_version;
	}	
	
	
	public void setSupportedfeatures(int value) {
		nSupported_features = value;
	}
	public int getSupportedfeatures() {
		return nSupported_features;
	}	
		

	
	public ProfileSupportInfo(int nSupport,int scn,int nVersion,int nfeature) 
	{
		bSupport = nSupport;
		nScn = scn;
		nProfile_version = nVersion;
		nSupported_features = nfeature;
	}
	
	public ProfileSupportInfo() {
		bSupport = 0;
		nScn = 0;
		nProfile_version = 0;
		nSupported_features = 0;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		arg0.writeInt(bSupport);
		arg0.writeInt(nScn);
		arg0.writeInt(nProfile_version);
		arg0.writeInt(nSupported_features);


	}
	public void readFromParcel(Parcel in) {
		bSupport = in.readInt();
		nScn = in.readInt();		
		nProfile_version = in.readInt();		
		nSupported_features = in.readInt();		

	}	

	public static final Parcelable.Creator<ProfileSupportInfo> CREATOR =
		new Parcelable.Creator<ProfileSupportInfo>() {
		public ProfileSupportInfo createFromParcel(Parcel in) {
			return new ProfileSupportInfo(in);
		}
		public ProfileSupportInfo[] newArray(int size) {
			return new ProfileSupportInfo[size];
		}
	};

	private ProfileSupportInfo(Parcel in) {
		readFromParcel(in);
	}
}

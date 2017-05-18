package com.anwsdk.service;

import android.os.Parcel;
import android.os.Parcelable;

public class MAPFolderInfo implements Parcelable {
	private static final int GSM_MAP_MAX_FOLDER_NUM = 20;

	public int FoldersNum;

	public String FolderName[];

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		arg0.writeInt(FoldersNum);
		for(int i = 0;i<FoldersNum;i++) {
			arg0.writeString(FolderName[i]);
		}
	}

	public void readFromParcel(Parcel in) {
		FoldersNum = in.readInt();

		if(FoldersNum >0)
			FolderName = new String[FoldersNum];
		for(int i = 0;i<FoldersNum;i++) {
			FolderName[i] = in.readString();
		}
	}

	public static final Parcelable.Creator<MAPFolderInfo> CREATOR =
		new Parcelable.Creator<MAPFolderInfo>() {
		public MAPFolderInfo createFromParcel(Parcel in) {
			return new MAPFolderInfo(in);
		}
		public MAPFolderInfo[] newArray(int size) {
			return new MAPFolderInfo[size];
		}
	};

	private MAPFolderInfo(Parcel in) {
		readFromParcel(in);
	}




	public MAPFolderInfo(int folders_num, String name_entries[]) {
		FoldersNum = folders_num;
		FolderName = new String[name_entries.length];
		System.arraycopy(name_entries, 0, FolderName, 0, name_entries.length);
	}

	public MAPFolderInfo() {
		FoldersNum = 0;
		FolderName = new String[GSM_MAP_MAX_FOLDER_NUM];
	}
}

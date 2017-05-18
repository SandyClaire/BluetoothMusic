package com.anwsdk.service;

import android.os.Parcel;
import android.os.Parcelable;

public class PBAPBrowsingItemData implements Parcelable {
	public enum GsmMemoryType {
		// Internal memory of the mobile equipment
		MEM_ERROR(0),
		MEM_ME(1),

		// SIM card memory
		MEM_SM(2),

		// Own numbers
		MEM_ON(3),

		// Dialled calls
		MEM_DC(4),

		// Received calls
		MEM_RC(5),

		// Missed calls
		MEM_MC(6),

		// Combined ME and SIM phonebook
		MEM_MT(7),

		// Fixed dial
		MEM_FD(8),

		// Voice mailbox
		MEM_VM(9),

		MEM7110_CG(0xf0), // Caller groups memory
		MEM7110_SP(0xf1); // Speed dial memory

		private final int id;

		GsmMemoryType(int id) { this.id = id;}
		public int getValue() { return id; }
	}

	public GsmMemoryType MemoryType;
	
	public String	Handle;
	public String	Name;
	public String	LastName;
	public String	FirstName;
	public String	MiddleName;
	public String	Prefix;
	public String	Suffix;
	public String	Reserved1;
	public String	Reserved2;
	
	public void setHandle(String value) {
		Handle = value;
	}

	public String getHandle() {
		return Handle;
	}
	
	public void setName(String value) {
		Name = value;
	}

	public String getName() {
		return Name;
	}
	
	public void setLastName(String value) {
		LastName = value;
	}

	public String getLastName() {
		return LastName;
	}
	
	public void setFirstName(String value) {
		FirstName = value;
	}

	public String getFirstName() {
		return FirstName;
	}
	
	public void setMiddleName(String value) {
		MiddleName = value;
	}

	public String getMiddleName() {
		return MiddleName;
	}		
	
	public void setPrefix(String value) {
		Prefix = value;
	}

	public String getPrefix() {
		return Prefix;
	}			
	
	public void setSuffix(String value) {
		Suffix = value;
	}

	public String getSuffix() {
		return Suffix;
	}		
	
	public void setReserved1(String value) {
		Reserved1 = value;
	}

	public String getReserved1() {
		return Reserved1;
	}			
	
	public void setReserved2(String value) {
		Reserved2 = value;
	}

	public String getReserved2() {
		return Reserved2;
	}			
				
	public void setGsmMemoryType(int value){
		switch(value) {
		case 0:
			MemoryType = GsmMemoryType.MEM_ERROR;
			break;
		case 1:
			MemoryType = GsmMemoryType.MEM_ME;
			break;
		case 2:
			MemoryType = GsmMemoryType.MEM_SM;
			break;
		case 3:
			MemoryType = GsmMemoryType.MEM_ON;
			break;
		case 4:
			MemoryType = GsmMemoryType.MEM_DC;
			break;
		case 5:
			MemoryType = GsmMemoryType.MEM_RC;
			break;
		case 6:
			MemoryType = GsmMemoryType.MEM_MC;
			break;
		case 7:
			MemoryType = GsmMemoryType.MEM_MT;
			break;
		case 8:
			MemoryType = GsmMemoryType.MEM_FD;
			break;
		case 9:
			MemoryType = GsmMemoryType.MEM_VM;
			break;
		case 0xf0:
			MemoryType = GsmMemoryType.MEM7110_CG;
			break;
		case 0xf1:
			MemoryType = GsmMemoryType.MEM7110_SP;
			break;
		default:
			MemoryType = GsmMemoryType.MEM_ERROR;
		}
	}
	
	public PBAPBrowsingItemData(int  memory_type,String sHandle, String sName, String sLastName, String sFirstName, 
				String sMiddleName, String sPrefix,String sSuffix,String sReserved1,String sReserved2) {
		
		setGsmMemoryType(memory_type);
		
		Handle = sHandle;
		Name = sName;
		LastName = sLastName;
		FirstName = sFirstName;
		MiddleName = sMiddleName;
		Prefix = sPrefix;
		Suffix = sSuffix;
		Reserved1 = sReserved1;
		Reserved2 = sReserved2;
	}
	
	public PBAPBrowsingItemData() {
		MemoryType = GsmMemoryType.MEM_ERROR;
		
		Handle = "";
		Name = "";
		LastName = "";
		FirstName = "";
		MiddleName = "";
		Prefix = "";
		Suffix = "";
		Reserved1 = "";
		Reserved2 = "";	
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		arg0.writeInt(MemoryType.getValue());
		
		arg0.writeString(Handle);
		arg0.writeString(Name);
		arg0.writeString(LastName);
		arg0.writeString(FirstName);
		arg0.writeString(MiddleName);
		arg0.writeString(Prefix);		
		arg0.writeString(Suffix);
		arg0.writeString(Reserved1);	
		arg0.writeString(Reserved2);
	}
	public void readFromParcel(Parcel in) {
		int memtype = in.readInt();
		//MemoryType.setValue(memtype);
		if(GsmMemoryType.MEM_ERROR.getValue() == memtype)
			MemoryType = GsmMemoryType.MEM_ERROR;
		else if(GsmMemoryType.MEM_ME.getValue() == memtype)
			MemoryType = GsmMemoryType.MEM_ME;
		else if(GsmMemoryType.MEM_SM.getValue() == memtype)
			MemoryType = GsmMemoryType.MEM_SM;
		else if(GsmMemoryType.MEM_ON.getValue() == memtype)
			MemoryType = GsmMemoryType.MEM_ON;
		else if(GsmMemoryType.MEM_DC.getValue() == memtype)
			MemoryType = GsmMemoryType.MEM_DC;
		else if(GsmMemoryType.MEM_RC.getValue() == memtype)
			MemoryType = GsmMemoryType.MEM_RC;
		else if(GsmMemoryType.MEM_MC.getValue() == memtype)
			MemoryType = GsmMemoryType.MEM_MC;
		else if(GsmMemoryType.MEM_MT.getValue() == memtype)
			MemoryType = GsmMemoryType.MEM_MT;
		else if(GsmMemoryType.MEM_FD.getValue() == memtype)
			MemoryType = GsmMemoryType.MEM_FD;
		else if(GsmMemoryType.MEM_VM.getValue() == memtype)
			MemoryType = GsmMemoryType.MEM_VM;
		else if(GsmMemoryType.MEM7110_CG.getValue() == memtype)
			MemoryType = GsmMemoryType.MEM7110_CG;
		else if(GsmMemoryType.MEM7110_SP.getValue() == memtype)
			MemoryType = GsmMemoryType.MEM7110_SP;

		Handle = in.readString();		
	
		Name = in.readString();
		LastName = in.readString();
		FirstName = in.readString();
		MiddleName = in.readString();
		Prefix = in.readString();
		Suffix = in.readString();
		Reserved1 = in.readString();
		Reserved2 = in.readString();
			
	}	

	public static final Parcelable.Creator<PBAPBrowsingItemData> CREATOR =
		new Parcelable.Creator<PBAPBrowsingItemData>() {
		public PBAPBrowsingItemData createFromParcel(Parcel in) {
			return new PBAPBrowsingItemData(in);
		}
		public PBAPBrowsingItemData[] newArray(int size) {
			return new PBAPBrowsingItemData[size];
		}
	};

	private PBAPBrowsingItemData(Parcel in) {
		readFromParcel(in);
	}
}

package com.anwsdk.service;

import android.os.Parcel;
import android.os.Parcelable;

public class GsmMemoryEntry implements Parcelable {
	private static final int GSM_PHONEBOOK_ENTRIES = 26;

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

	/*
	 * Used memory for phonebook entry
	 * C type : GSM_MemoryType
	 */
	public GsmMemoryType MemoryType;
	/*
	 * Used location for phonebook entry
	 * C type : char[100]
	 */
	public String szIndex;
	// Number of SubEntries in Entries table.
	public int EntriesNum;
	/*
	 * Values of SubEntries
	 * C type : GSM_SubMemoryEntry[26]
	 */
	public GsmSubMemoryEntry gsmSubMemoryEntries[];/* = new GsmSubMemoryEntry[GSM_PHONEBOOK_ENTRIES];*/

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		arg0.writeInt(MemoryType.getValue());
		arg0.writeString(szIndex);
		arg0.writeInt(EntriesNum);
		for(int i = 0;i<EntriesNum;i++) {
			writeSubEntryToParcel(arg0,i);
		}
	}

	public void writeSubEntryToParcel(Parcel arg0, int nIndex) {
		// TODO Auto-generated method stub
		arg0.writeInt(gsmSubMemoryEntries[nIndex].EntryType.getValue());
		arg0.writeString(gsmSubMemoryEntries[nIndex].Text);
		arg0.writeInt(gsmSubMemoryEntries[nIndex].Date.getYear());
		arg0.writeInt(gsmSubMemoryEntries[nIndex].Date.getMonth());
		arg0.writeInt(gsmSubMemoryEntries[nIndex].Date.getDay());
		arg0.writeInt(gsmSubMemoryEntries[nIndex].Date.getHour());
		arg0.writeInt(gsmSubMemoryEntries[nIndex].Date.getMinute());
		arg0.writeInt(gsmSubMemoryEntries[nIndex].Date.getSecond());
		arg0.writeInt(gsmSubMemoryEntries[nIndex].Number);
		arg0.writeInt(gsmSubMemoryEntries[nIndex].VoiceTag);
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

		szIndex = in.readString();
		EntriesNum = in.readInt();
		if(EntriesNum >0)
			gsmSubMemoryEntries = new GsmSubMemoryEntry[EntriesNum];
		for(int i = 0;i<EntriesNum;i++) {
			gsmSubMemoryEntries[i] = new GsmSubMemoryEntry();
			readSubEntryFromParcel(in,i);
		}
	}
	public void readSubEntryFromParcel(Parcel in,int nIndex) {
		int EntryType = in.readInt();
		gsmSubMemoryEntries[nIndex].SetEntryType(EntryType);
		gsmSubMemoryEntries[nIndex].Text = in.readString();
		int nYear = in.readInt();
		int nMonth = in.readInt();
		int nDay = in.readInt();
		int nHour = in.readInt();
		int nMinute = in.readInt();
		int nSec = in.readInt();
		gsmSubMemoryEntries[nIndex].Date.setYear(nYear);
		gsmSubMemoryEntries[nIndex].Date.setMonth(nMonth);
		gsmSubMemoryEntries[nIndex].Date.setDay(nDay);
		gsmSubMemoryEntries[nIndex].Date.setHour(nHour);
		gsmSubMemoryEntries[nIndex].Date.setMinute(nMinute);
		gsmSubMemoryEntries[nIndex].Date.setSecond(nSec);
		gsmSubMemoryEntries[nIndex].Number = in.readInt();
		gsmSubMemoryEntries[nIndex].VoiceTag = in.readInt();

	}
	public static final Parcelable.Creator<GsmMemoryEntry> CREATOR =
		new Parcelable.Creator<GsmMemoryEntry>() {
		public GsmMemoryEntry createFromParcel(Parcel in) {
			return new GsmMemoryEntry(in);
		}
		public GsmMemoryEntry[] newArray(int size) {
			return new GsmMemoryEntry[size];
		}
	};

	private GsmMemoryEntry(Parcel in) {
		readFromParcel(in);
	}

	public boolean IsPhoneNumberEntryType(int nIndex)
	{
		GsmSubMemoryEntry.GsmEntryType entryType =gsmSubMemoryEntries[nIndex].EntryType;
		if(entryType == GsmSubMemoryEntry.GsmEntryType.PBK_Number_General ||
				entryType == GsmSubMemoryEntry.GsmEntryType.PBK_Number_Mobile ||
				entryType == GsmSubMemoryEntry.GsmEntryType.PBK_Number_Work ||
				entryType == GsmSubMemoryEntry.GsmEntryType.PBK_Number_Fax ||
				entryType == GsmSubMemoryEntry.GsmEntryType.PBK_Number_Home ||
				entryType == GsmSubMemoryEntry.GsmEntryType.PBK_Number_Pager ||
				entryType == GsmSubMemoryEntry.GsmEntryType.PBK_Number_Other ||
				entryType == GsmSubMemoryEntry.GsmEntryType.PBK_Number_Sex ||
				entryType == GsmSubMemoryEntry.GsmEntryType.PBK_Number_Light ||
				entryType == GsmSubMemoryEntry.GsmEntryType.PBK_Number_Mobile_Home ||
				entryType == GsmSubMemoryEntry.GsmEntryType.PBK_Number_Mobile_Work ||
				entryType == GsmSubMemoryEntry.GsmEntryType.PBK_Number_Fax_Home ||
				entryType == GsmSubMemoryEntry.GsmEntryType.PBK_Number_Fax_Work ||
				entryType == GsmSubMemoryEntry.GsmEntryType.PBK_Number_Pager_Home ||
				entryType == GsmSubMemoryEntry.GsmEntryType.PBK_Number_Pager_Work ||
				entryType == GsmSubMemoryEntry.GsmEntryType.PBK_Number_VideoCall ||
				entryType == GsmSubMemoryEntry.GsmEntryType.PBK_Number_VideoCall_Home ||
				entryType == GsmSubMemoryEntry.GsmEntryType.PBK_Number_VideoCall_Work ||
				entryType == GsmSubMemoryEntry.GsmEntryType.PBK_Number_Assistant ||
				entryType == GsmSubMemoryEntry.GsmEntryType.PBK_Number_Business ||
				entryType == GsmSubMemoryEntry.GsmEntryType.PBK_Number_Callback ||
				entryType == GsmSubMemoryEntry.GsmEntryType.PBK_Number_Car ||
				entryType == GsmSubMemoryEntry.GsmEntryType.PBK_Number_ISDN ||
				entryType == GsmSubMemoryEntry.GsmEntryType.PBK_Number_Primary ||
				entryType == GsmSubMemoryEntry.GsmEntryType.PBK_Number_Radio ||
				entryType == GsmSubMemoryEntry.GsmEntryType.PBK_Number_Telix ||
				entryType == GsmSubMemoryEntry.GsmEntryType.PBK_Number_TTYTDD	) {
			return true;
		}

		return false;
	}

	public String GetEntryName(boolean bfirst_last) 	{
		String strName;
		String strFirstName;
		String strLastName;
		strFirstName = "";
		strLastName = "";
		strName = "";
		for (int i = 0; i<EntriesNum; i++) {
			if(gsmSubMemoryEntries[i].EntryType == GsmSubMemoryEntry.GsmEntryType.PBK_Text_FirstName) {
				strFirstName = gsmSubMemoryEntries[i].Text;
			}
			else if(gsmSubMemoryEntries[i].EntryType == GsmSubMemoryEntry.GsmEntryType.PBK_Text_LastName) {
				strLastName  = gsmSubMemoryEntries[i].Text;
			}
			else if(gsmSubMemoryEntries[i].EntryType == GsmSubMemoryEntry.GsmEntryType.PBK_Text_Name) {
				strName  = gsmSubMemoryEntries[i].Text;
			}
		}
		if(strName.equals("")) {
			StringBuilder builder = new StringBuilder();
			if(bfirst_last) {
				if(strFirstName.equals("") == false) {
					builder.append(strFirstName);
					builder.append(" ");
				}
				builder.append(strLastName);
			}
			else {
				if(strLastName.equals("") == false) {
					builder.append(strLastName);
					builder.append(" ");
				}
				builder.append(strFirstName);
			}
			strName = builder.toString();
		}

		return strName;
	}

	public void setSzIndex(String value) {
		szIndex = value;
	}

	public String getSzIndex() {
		return szIndex;
	}

	public void setEntriesNum(int value) {
		EntriesNum = value;
	}

	public int getEntriesNum() {
		return EntriesNum;
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

	public GsmMemoryEntry(int/*GsmMemoryType*/ memory_type, String sz_index, int entries_num, GsmSubMemoryEntry gsm_submemory_entries[]) {
		/*MemoryType = */setGsmMemoryType(memory_type);
		szIndex = sz_index;
		EntriesNum = entries_num;
		gsmSubMemoryEntries = new GsmSubMemoryEntry[gsm_submemory_entries.length];
		System.arraycopy(gsm_submemory_entries, 0, gsmSubMemoryEntries, 0, gsm_submemory_entries.length);
	}

	public GsmMemoryEntry() {
		MemoryType = GsmMemoryType.MEM_ERROR;
		szIndex = "";
		EntriesNum = 0;
		gsmSubMemoryEntries = new GsmSubMemoryEntry[GSM_PHONEBOOK_ENTRIES];
	}
}

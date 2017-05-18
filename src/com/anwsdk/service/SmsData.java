package com.anwsdk.service;

import android.os.Parcel;
import android.os.Parcelable;

public class SmsData implements Parcelable {
	public enum GsmSmsState {
		SMS_Sent(1),
		SMS_UnSent(2),
		SMS_Read(3),
		SMS_UnRead(4);
		
		private final int id;
		
		GsmSmsState(int id) { this.id = id;}
		public int getValue() { return id; }
	}

	/*
	 *Short message Service (SMS) Service center (SMSC) address.
	 * C type : ANW_CHAR[16]
	 */
	public String	SCA;
	/*
	 * The phone number of the receiver/target.
	 * C type : ANW_CHAR[40]
	 */
	public String	TPA;
	/*
	 * Protocol Identifier (TP-PID)
	 * C type : ANW_CHAR
	 */
	public byte	TP_PID;
	/*
	 * The text encoding of the SMS message.
	 * Set as 1 means using Unicode encoding; 
	 * set as others means GSM encoding.
	 * C type : ANW_CHAR
	 */
	public byte	TP_DCS;
	/*
	 * The time of day that the message is received.
	 * C type : ANW_CHAR[40]
	 */
	public String	TP_SCTS; 
	/*
	 * The content of SMS.
	 * C type : ANW_CHAR[MAX_TP_UD+2]
	 * MAX_TP_UD = 3220
	 */
	public String	TP_UD;
	
	// The index of SMS.
	public int	index;
	/*
	 *   0: The SMS is saved in Outbox
     *   1: The SMS is saved in Inbox
   */
	public int	whichFolder;
	// Indicates the storage area of the SMS.
	public int	memType;

	public GsmSmsState gsmSmsState;
	
	public void setSCA(String value) {
		SCA = value;
	}

	public String getSCA() {
		return SCA;
	}

	public void setTPA(String value) {
		TPA = value;
	}

	public String getTPA() {
		return TPA;
	}

	public void setTP_PID(byte value) {
		TP_PID = value;
	}

	public byte getTP_PID() {
		return TP_PID;
	}

	public void setTP_DCS(byte value) {
		TP_DCS = value;
	}

	public byte getTP_DCS() {
		return TP_DCS;
	}

	public void setTP_SCTS(String value) {
		TP_SCTS = value;
	}

	public String getTP_SCTS() {
		return TP_SCTS;
	}

	public void setTP_UD(String value) {
		TP_UD = value;
	}

	public String getTP_UD() {
		return TP_UD;
	}

	public void setIndex(int value) {
		index = value;
	}

	public int getIndex() {
		return index;
	}

	public void setWhichFolder(int value) {
		whichFolder = value;
	}

	public int getWhichFolder() {
		return whichFolder;
	}

	public void setMemType(int value) {
		memType = value;
	}

	public int getMemType() {
		return memType;
	}

	public void setGsmSmsState(int value){
		switch(value) {
		case 1:
			gsmSmsState = GsmSmsState.SMS_Sent;
			break;
		case 2:
			gsmSmsState = GsmSmsState.SMS_UnSent;
			break;
		case 3:
			gsmSmsState = GsmSmsState.SMS_Read;
			break;
		case 4:
			gsmSmsState = GsmSmsState.SMS_UnRead;
			break;
		default:
			gsmSmsState = GsmSmsState.SMS_UnSent;
		}
	}
	
	public void setGsmSmsState(GsmSmsState value) {
		gsmSmsState = value;
	}

	public GsmSmsState getGsmSmsState() {
		return gsmSmsState;
	}
	public int getGsmSmsStateValue() {
		return gsmSmsState.getValue();
	}	
	public SmsData(String sca, String tpa, byte	tp_pid, byte tp_dcs, String tp_scts, String tp_ud, int index_number, int which_folder, int mem_type, int/*GsmSmsState*/ gsm_sms_state) {
		SCA = sca;
		TPA = tpa;
		TP_PID = tp_pid;
		TP_DCS = tp_dcs;
		TP_SCTS = tp_scts;
		TP_UD = tp_ud;
		index = index_number;
		whichFolder = which_folder;
		memType = mem_type;
		setGsmSmsState(gsm_sms_state);
	}
	
	public SmsData() {
		SCA = "";
		TPA = "";
		TP_PID = 0;
		TP_DCS = 0;
		TP_SCTS = "";
		TP_UD = "";
		index = 0;
		whichFolder = 0;
		memType = 0;
		gsmSmsState = GsmSmsState.SMS_UnSent;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		arg0.writeString(SCA);
		arg0.writeString(TPA);
		arg0.writeByte(/*(byte)*/TP_PID);
		arg0.writeByte(/*(byte)*/TP_DCS);
		arg0.writeString(TP_SCTS);
		arg0.writeString(TP_UD);
		arg0.writeInt(index);
		arg0.writeInt(whichFolder);
		arg0.writeInt(memType);
		arg0.writeInt(gsmSmsState.id);

	}
	public void readFromParcel(Parcel in) {
		SCA = in.readString();
		TPA = in.readString();
		TP_PID = /*(byte)*/ in.readByte();
		TP_DCS = /*(byte)*/ in.readByte();
		TP_SCTS = in.readString();
		TP_UD = in.readString();
		index = in.readInt();
		whichFolder = in.readInt();
		memType = in.readInt();
		int state =in.readInt();
/*
		switch(state) {
		case 1:
			gsmSmsState =  GsmSmsState.SMS_Sent;
			break;
		case 2:
			gsmSmsState =  GsmSmsState.SMS_UnSent;
			break;
		case 3:
			gsmSmsState =  GsmSmsState.SMS_Read;
			break;
		case 4:
			gsmSmsState =  GsmSmsState.SMS_UnRead;
			break;
		default:
			gsmSmsState =  GsmSmsState.SMS_UnSent;
		}
*/
		setGsmSmsState(state);
	}	

	public static final Parcelable.Creator<SmsData> CREATOR =
		new Parcelable.Creator<SmsData>() {
		public SmsData createFromParcel(Parcel in) {
			return new SmsData(in);
		}
		public SmsData[] newArray(int size) {
			return new SmsData[size];
		}
	};

	private SmsData(Parcel in) {
		readFromParcel(in);
	}
}

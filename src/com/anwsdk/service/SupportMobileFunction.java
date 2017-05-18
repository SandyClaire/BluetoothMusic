package com.anwsdk.service;

import android.os.Parcel;
import android.os.Parcelable;

public class SupportMobileFunction implements Parcelable {
	public byte	PhoneBook;

	public byte	CallLog;

	public byte	SMS;

	public byte	SMS_Get;

	public byte	SMS_Send; 

	// Calendar
	public byte	Calendar;

	// Todolist(Work)
	public byte	Todolist;

	public byte	Memo;

	public byte	Mail;

	public byte GPRS;

	public byte	FileManager;

	public byte VoiceDial;
	
	public byte[] Other;
	
	public void setPhoneBook(byte value) {
		PhoneBook = value;
	}

	public byte getSCA() {
		return PhoneBook;
	}

	public void setCallLog(byte value) {
		CallLog = value;
	}

	public byte getCallLog() {
		return CallLog;
	}
	
	public void setSMS(byte value) {
		SMS = value;
	}

	public byte getSMS() {
		return SMS;
	}

	public void setSMS_Get(byte value) {
		SMS_Get = value;
	}

	public byte getSMS_Get() {
		return SMS_Get;
	}
	
	public void setSMS_Send(byte value) {
		SMS_Send = value;
	}

	public byte getSMS_Send() {
		return SMS_Send;
	}
	
	public void setCalendar(byte value) {
		Calendar = value;
	}

	public byte getCalendar() {
		return Calendar;
	}
	
	public void setTodolist(byte value) {
		Todolist = value;
	}

	public byte getTodolist() {
		return Todolist;
	}
	
	public void setMemo(byte value) {
		Memo = value;
	}

	public byte getMemo() {
		return Memo;
	}
	
	public void setMail(byte value) {
		Mail = value;
	}

	public byte getMail() {
		return Mail;
	}
	
	public void setGPRS(byte value) {
		GPRS = value;
	}

	public byte getGPRS() {
		return GPRS;
	}

	public void setFileManager(byte value) {
		FileManager = value;
	}

	public byte getFileManager() {
		return FileManager;
	}

	public void setVoiceDial(byte value) {
		VoiceDial = value;
	}

	public byte getVoiceDial() {
		return VoiceDial;
	}

	public void setOther(byte[] value) {
		Other = value;
	}

	public byte[] getOther() {
		return Other;
	}

	public SupportMobileFunction(byte phonebook,
								byte calllog,
								byte sms,
								byte sms_get,
								byte sms_send,
								byte calendar,
								byte todolist,
								byte memo,
								byte mail,
								byte gprs,
								byte filemanager,
								byte voicedial,
								byte[] other) {
		PhoneBook = phonebook;
		CallLog = calllog;
		SMS = sms;
		SMS_Get = sms_get;
		SMS_Send = sms_send;
		Calendar = calendar;
		Todolist = todolist;
		Memo = memo;
		Mail = mail;
		GPRS = gprs;
		FileManager = filemanager;
		VoiceDial = voicedial;
		Other = other;
	}
	
	public SupportMobileFunction() {
		PhoneBook = 0;
		CallLog = 0;
		SMS = 0;
		SMS_Get = 0;
		SMS_Send = 0; 
		Calendar = 0;
		Todolist = 0;
		Memo = 0;
		Mail = 0;
		GPRS = 0;
		FileManager = 0;
		VoiceDial = 0;
		Other = new byte[13];
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		arg0.writeByte(PhoneBook);
		arg0.writeByte(CallLog);
		arg0.writeByte(SMS);
		arg0.writeByte(SMS_Get);
		arg0.writeByte(SMS_Send);
		arg0.writeByte(Calendar);
		arg0.writeByte(Todolist);
		arg0.writeByte(Memo);
		arg0.writeByte(Mail);
		arg0.writeByte(GPRS);
		arg0.writeByte(FileManager);
		arg0.writeByte(VoiceDial);		
		arg0.writeByteArray(Other);
	}

	public void readFromParcel(Parcel in) {
		PhoneBook = in.readByte();
		CallLog = in.readByte();
		SMS = in.readByte();
		SMS_Get = in.readByte();
		SMS_Send = in.readByte();
		Calendar = in.readByte();
		Todolist = in.readByte();
		Memo = in.readByte();
		Mail = in.readByte();
		GPRS = in.readByte();
		FileManager = in.readByte();
		VoiceDial = in.readByte();
		Other = new byte[13];
		in.readByteArray(Other);
	}	

	public static final Parcelable.Creator<SupportMobileFunction> CREATOR =
		new Parcelable.Creator<SupportMobileFunction>() {
		public SupportMobileFunction createFromParcel(Parcel in) {
			return new SupportMobileFunction(in);
		}
		public SupportMobileFunction[] newArray(int size) {
			return new SupportMobileFunction[size];
		}
	};

	private SupportMobileFunction(Parcel in) {
		readFromParcel(in);
	}
}

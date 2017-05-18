package com.anwsdk.service;

import android.os.Parcel;
import android.os.Parcelable;

public class EmailData implements Parcelable {
	public enum GsmSmsState {
		SMS_Sent(1),
		SMS_UnSent(2),
		SMS_Read(3),
		SMS_UnRead(4);
		
		private final int id;
		
		GsmSmsState(int id) { this.id = id;}
		public int getValue() { return id; }
	}
	public enum GsmFolderType {
		FOLDER_Inbox(1),
		FOLDER_Sent(2),
		FOLDER_Deleted(3),
		FOLDER_Outbox(4),
		FOLDER_Draft(5);
		
		private final int id;
		
		GsmFolderType(int id) { this.id = id;}
		public int getValue() { return id; }
	}
	public String	handle;/**/
	public GsmSmsState email_state;
	public String	sender_name;/*sender_name*/
	public String	sender_address;/*sender_address: FROM:*/
	public String	recipient_name;/*recipient_name*/
	public String	recipient_address;/*recipient_address: To:*/
	public String	carbon_copy;/*cc address*/
	public int    email_length;/*Length of the EMAIL message.*/
	public String	subject;
	public String	dateTime_char;
	public String	Text;
	public int    Location;
	public GsmFolderType folder_type;

	public void setEmailState(int value){
		switch(value) {
		case 1:
			email_state = GsmSmsState.SMS_Sent;
			break;
		case 2:
			email_state = GsmSmsState.SMS_UnSent;
			break;
		case 3:
			email_state = GsmSmsState.SMS_Read;
			break;
		case 4:
			email_state = GsmSmsState.SMS_UnRead;
			break;
		default:
			email_state = GsmSmsState.SMS_UnSent;
		}
	}
	public void setEmailState(GsmSmsState value) {
		email_state = value;
	}
	public GsmSmsState getEmailState() {
		return email_state;
	}
	public int getEmailStateValue() {
		return email_state.getValue();
	}		
	
	
	public void setHandle(String value) {
		handle = value;
	}

	public String getHandle() {
		return handle;
	}

	public void setSenderName(String value) {
		sender_name = value;
	}

	public String getSenderName() {
		return sender_name;
	}
	
	public void setSenderAddress(String value) {
		sender_address = value;
	}

	public String getSenderAddress() {
		return sender_address;
	}	

	public void setRecipientName(String value) {
		recipient_name = value;
	}

	public String getRecipientName() {
		return recipient_name;
	}
	
	public void setRecipientAddress(String value) {
		recipient_address = value;
	}

	public String getRecipientAddress() {
		return recipient_address;
	}	
	public void setCarbonCopy(String value) {
		carbon_copy = value;
	}

	public String getCarbonCopy() {
		return carbon_copy;
	}		
	public void setEmaillength(int value) {
		email_length = value;
	}

	public int getEmaillength() {
		return email_length;
	}
	public void setSubject(String value) {
		subject = value;
	}

	public String getSubject() {
		return subject;
	}	
	public void setDateTime(String value) {
		dateTime_char = value;
	}

	public String getDateTime() {
		return dateTime_char;
	}	
	public void setText(String value) {
		Text = value;
	}

	public String getText() {
		return Text;
	}
	public void setLocation(int value) {
		Location = value;
	}

	public int getLocation() {
		return Location;
	}
	
	
	public void setFolderType(int value){
		switch(value) {
		case 1:
			folder_type = GsmFolderType.FOLDER_Inbox;
			break;
		case 2:
			folder_type = GsmFolderType.FOLDER_Sent;
			break;
		case 3:
			folder_type = GsmFolderType.FOLDER_Deleted;
			break;
		case 4:
			folder_type = GsmFolderType.FOLDER_Outbox;
			break;
		case 5:
			folder_type = GsmFolderType.FOLDER_Draft;
			break;
		default:
			folder_type = GsmFolderType.FOLDER_Inbox;
		}
	}
	
	public void setFolderType(GsmFolderType value) {
		folder_type = value;
	}

	public GsmFolderType getFolderType() {
		return folder_type;
	}
	public int getFolderTypeValue() {
		return folder_type.getValue();
	}	
	public EmailData(String Handle,int emailstate,String SenderName,String SenderAddress,String RecipientName,
						String RecipientAddress,String CarbonCopy,int    emaillen,String Subject,String DateTime,String sText,int location,int nFolderType) {
		handle = Handle;
		setEmailState(emailstate);
		sender_name = SenderName;
		sender_address = SenderAddress;
		recipient_name = RecipientName;
		recipient_address = RecipientAddress;
		carbon_copy = CarbonCopy;
		email_length = emaillen;
		subject = Subject;
		dateTime_char = DateTime;
		Text = sText;
		Location = location;
		setFolderType(nFolderType);	
	}
	
	public EmailData() {
		handle = "";
		email_state = GsmSmsState.SMS_UnSent;
		sender_name = "";
		sender_address = "";
		recipient_name = "";
		recipient_address = "";
		carbon_copy = "";
		email_length = 0;
		subject = "";
		dateTime_char = "";
		Text = "";
		Location = 0;
		folder_type = GsmFolderType.FOLDER_Inbox;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		arg0.writeString(handle);
		arg0.writeInt(email_state.id);
		arg0.writeString(sender_name);
		arg0.writeString(sender_address);
		arg0.writeString(recipient_name);
		arg0.writeString(recipient_address);
		arg0.writeString(carbon_copy);
		arg0.writeInt(email_length);
		arg0.writeString(subject);
		arg0.writeString(dateTime_char);
		arg0.writeString(Text);
		arg0.writeInt(Location);
		arg0.writeInt(folder_type.id);
	}
	public void readFromParcel(Parcel in) {
		handle = in.readString();
		int state =in.readInt();
		setEmailState(state);
		sender_name = in.readString();
		sender_address = in.readString();
		recipient_name = in.readString();
		recipient_address = in.readString();
		carbon_copy = in.readString();
		email_length =in.readInt();
		subject = in.readString();
		dateTime_char = in.readString();
		Text = in.readString();
		Location =in.readInt();
		state =in.readInt();
		setFolderType(state);

	}	

	public static final Parcelable.Creator<EmailData> CREATOR =
		new Parcelable.Creator<EmailData>() {
		public EmailData createFromParcel(Parcel in) {
			return new EmailData(in);
		}
		public EmailData[] newArray(int size) {
			return new EmailData[size];
		}
	};

	private EmailData(Parcel in) {
		readFromParcel(in);
	}
}

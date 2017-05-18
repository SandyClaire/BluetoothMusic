package com.anwsdk.service;

import java.util.Arrays;

import android.os.Parcel;

public class BT_Service {
	public enum UUIDServiceType {
		SERVICE_UUID_UNSPEC(0),
		SERVICE_UUID_16BITS(16),			/* 16 bits services */
		SERVICE_UUID_32BITS(32),			/* 32 bits services */
		SERVICE_UUID_128BITS(128),		/* 128 bits service */
		SERVICE_UUID_SOL_16BITS(129),		/* 16 bit Service Solicitation UUIDs */
		SERVICE_UUID_SOL_32BITS(130),		/* 32 bit Service Solicitation UUIDs */
		SERVICE_UUID_SOL_128BITS(131);	/* 128 bit Service Solicitation UUIDs */
		private final int id;
		UUIDServiceType(int id) { this.id = id;}
		public int getValue() { return id; }
	}
	
	public UUIDServiceType service_type;
	public int uuid_16b; //C/C++ unsigned 16 bits
	public long uuid_32b;
	public byte[] uuid_128b;

	public void SetServiceType(int nType) {
		service_type = UUIDServiceType.SERVICE_UUID_UNSPEC;
		
		if(UUIDServiceType.SERVICE_UUID_UNSPEC.getValue() == nType)
			service_type = UUIDServiceType.SERVICE_UUID_UNSPEC;
		else if(UUIDServiceType.SERVICE_UUID_16BITS.getValue() == nType)
			service_type = UUIDServiceType.SERVICE_UUID_16BITS;
		else if(UUIDServiceType.SERVICE_UUID_32BITS.getValue() == nType)
			service_type = UUIDServiceType.SERVICE_UUID_32BITS;
		else if(UUIDServiceType.SERVICE_UUID_128BITS.getValue() == nType)
			service_type = UUIDServiceType.SERVICE_UUID_128BITS;
		else if(UUIDServiceType.SERVICE_UUID_SOL_16BITS.getValue() == nType)
			service_type = UUIDServiceType.SERVICE_UUID_SOL_16BITS;
		else if(UUIDServiceType.SERVICE_UUID_SOL_32BITS.getValue() == nType)
			service_type = UUIDServiceType.SERVICE_UUID_SOL_32BITS;
		else if(UUIDServiceType.SERVICE_UUID_SOL_128BITS.getValue() == nType)
			service_type = UUIDServiceType.SERVICE_UUID_SOL_128BITS;
	}
	public int getServiceType()
	{
		return service_type.getValue();
	}
	public void setUUID16Bit(int value) {
		uuid_16b = value & 0xFFFF; //signed to unsigned
	}

	public int getUUID16Bit() {
		return uuid_16b;
	}	
	public void setUUID32Bit(long value) {
		uuid_32b = value & 0xFFFFFFFFL;//signed to unsigned
	}

	public long getUUID32Bit() {
		return uuid_32b;
	}	
	public void setUUID128bit(byte[] uuid128) {
		uuid_128b = uuid128;
	}

	public byte[] getUUID128bit() {
		return uuid_128b;
	}
	/*
	public String getUUID128String()
	{
		int i=0;
	   StringBuilder sb = new StringBuilder();
		   
		//unsigned int    service_uuid;
	   sb.append(String.format("%02x", uuid_128b[3] & 0xff));
	   sb.append(String.format("%02x", uuid_128b[2] & 0xff));
	   sb.append(String.format("%02x", uuid_128b[1] & 0xff));
	   sb.append(String.format("%02x", uuid_128b[0] & 0xff));
	   sb.append("-");
	   // unsigned short    data2;   
	   sb.append(String.format("%02x", uuid_128b[5] & 0xff));
	   sb.append(String.format("%02x", uuid_128b[4] & 0xff));
	   sb.append("-");

		// unsigned short    data3;   
	   sb.append(String.format("%02x", uuid_128b[7] & 0xff));
	   sb.append(String.format("%02x", uuid_128b[6] & 0xff));
	   sb.append("-");
	   //unsigned char     data4[8]; 
  	   byte [] subArray = Arrays.copyOfRange(uuid_128b, 8, 16);
	   for(byte b: subArray)
		      sb.append(String.format("%02x", b & 0xff));
		return sb.toString();
	}*/
	public String getUUIDString()
	{
		StringBuilder sb = new StringBuilder();
		if(service_type == UUIDServiceType.SERVICE_UUID_16BITS || service_type == UUIDServiceType.SERVICE_UUID_SOL_16BITS)
		{
			sb.append(Integer.toHexString(uuid_16b));
		}
		else if(service_type == UUIDServiceType.SERVICE_UUID_32BITS || service_type == UUIDServiceType.SERVICE_UUID_SOL_32BITS)
		{
			sb.append(Long.toHexString(uuid_32b));
		}
		else if(service_type == UUIDServiceType.SERVICE_UUID_128BITS || service_type == UUIDServiceType.SERVICE_UUID_SOL_128BITS)
		{
			int i=0;
			   
			//unsigned int    service_uuid;
		   sb.append(String.format("%02x", uuid_128b[3] & 0xff));
		   sb.append(String.format("%02x", uuid_128b[2] & 0xff));
		   sb.append(String.format("%02x", uuid_128b[1] & 0xff));
		   sb.append(String.format("%02x", uuid_128b[0] & 0xff));
		   sb.append("-");
		   // unsigned short    data2;   
		   sb.append(String.format("%02x", uuid_128b[5] & 0xff));
		   sb.append(String.format("%02x", uuid_128b[4] & 0xff));
		   sb.append("-");

			// unsigned short    data3;   
		   sb.append(String.format("%02x", uuid_128b[7] & 0xff));
		   sb.append(String.format("%02x", uuid_128b[6] & 0xff));
		   sb.append("-");
		   //unsigned char     data4[8]; 
	  	   byte [] subArray = Arrays.copyOfRange(uuid_128b, 8, 16);
		   for(byte b: subArray)
			      sb.append(String.format("%02x", b & 0xff));			
		}
		return sb.toString();
	}
	public boolean IsSame(BT_Service data)
	{
		boolean bSame = false;
		if(data.service_type == service_type)
		{
			if(service_type == UUIDServiceType.SERVICE_UUID_16BITS || service_type == UUIDServiceType.SERVICE_UUID_SOL_16BITS)
			{
				if(uuid_16b == data.uuid_16b) bSame = true;
			}
			else if(service_type == UUIDServiceType.SERVICE_UUID_32BITS || service_type == UUIDServiceType.SERVICE_UUID_SOL_32BITS)
			{
				if(uuid_32b == data.uuid_32b) bSame = true;
			
			}
			else if(service_type == UUIDServiceType.SERVICE_UUID_128BITS || service_type == UUIDServiceType.SERVICE_UUID_SOL_128BITS)
			{
				if(Arrays.equals(uuid_128b, data.uuid_128b) == true) 
					bSame = true;
			}
		}
		return bSame;
	}
	public void writeToParcel(Parcel arg0, int arg1) 
	{
		arg0.writeInt(service_type.getValue());
		arg0.writeInt(uuid_16b);
		arg0.writeLong(uuid_32b);
		arg0.writeByteArray(uuid_128b);
		
	}
	public void readFromParcel(Parcel in) {
		int type = in.readInt();
		SetServiceType(type);
		uuid_16b = in.readInt();
		uuid_32b = in.readLong();
		uuid_128b = new byte[16];
		in.readByteArray(uuid_128b);
		
	}
	public BT_Service(int type, int uuid16b, long uuid32b,  byte[] uuid128) {
		SetServiceType(type);
		setUUID16Bit(uuid16b);
		setUUID32Bit(uuid32b);
		uuid_128b = uuid128;
	}
		
	public BT_Service() {
		service_type =UUIDServiceType.SERVICE_UUID_UNSPEC;
		uuid_16b =0;
		uuid_32b =0;
		uuid_128b =  new byte[16];
		Arrays.fill( uuid_128b, (byte) 0 );
	}
}
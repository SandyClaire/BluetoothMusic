package com.anwsdk.service;

import android.os.Parcel;
import android.os.Parcelable;

public class DeviceIDInfoData implements Parcelable {
	public int specification_id;
	public int  vendor_id;
	public int  product_id;
	public int  version;
	public int  vendor_id_source;
	public byte  primary_record;
	
	public String	ClientExecutableUrl;
	public String	ServiceDescription;
	public String	DocumentationUrl;
	
	public void setSpecificationID(int value) {
		specification_id = value;
	}
	public int getSpecificationID() {
		return specification_id;
	}
	public void setVendorID(int value) {
		vendor_id = value;
	}
	public int getVendorID() {
		return vendor_id;
	}
	public void setProductID(int value) {
		product_id = value;
	}
	public int getProductID() {
		return product_id;
	}	
	public void setVersion(int value) {
		version = value;
	}
	public int getVersion() {
		return version;
	}	
	public void setVendorIdSource(int value) {
		vendor_id_source = value;
	}
	public int getVendorIdSource() {
		return vendor_id_source;
	}	
	public void setPrimary(byte value) {
		primary_record = value;
	}
	public byte getPrimary() {
		return primary_record;
	}	
	
	public void setClientExecutableUrl(String value) {
		ClientExecutableUrl = value;
	}

	public String getClientExecutableUrl() {
		return ClientExecutableUrl;
	}
	
	public void setServiceDescriptione(String value) {
		ServiceDescription = value;
	}

	public String getServiceDescription() {
		return ServiceDescription;
	}
	
	public void setDocumentationUrl(String value) {
		DocumentationUrl = value;
	}

	public String getDocumentationUrl() {
		return DocumentationUrl;
	}
	

	
	public DeviceIDInfoData(int sSpecId,int sVendorId, int sProductId, int sVersion, int sVendorIdISource, 
				byte sprimary, String sClientExecutableUrl,String sServiceDescription,String sDocumentationUrl) {
		
		specification_id = sSpecId;
		vendor_id = sVendorId;
		product_id = sProductId;
		version = sVersion;
		vendor_id_source = sVendorIdISource;
		primary_record = sprimary;		
		ClientExecutableUrl = sClientExecutableUrl;
		ServiceDescription = sServiceDescription;
		DocumentationUrl = sDocumentationUrl;

	}
	
	public DeviceIDInfoData() {
		specification_id = 0;
		vendor_id = 0;
		product_id = 0;
		version = 0;
		vendor_id_source = 0;
		primary_record = 0;		
		ClientExecutableUrl = "";
		ServiceDescription = "";
		DocumentationUrl = "";
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		arg0.writeInt(specification_id);
		arg0.writeInt(vendor_id);
		arg0.writeInt(product_id);
		arg0.writeInt(version);
		arg0.writeInt(vendor_id_source);
		arg0.writeByte(primary_record);
		
		arg0.writeString(ClientExecutableUrl);
		arg0.writeString(ServiceDescription);
		arg0.writeString(DocumentationUrl);

	}
	public void readFromParcel(Parcel in) {
		specification_id = in.readInt();
		vendor_id = in.readInt();
		product_id = in.readInt();
		version = in.readInt();
		vendor_id_source = in.readInt();
		primary_record = in.readByte();

		ClientExecutableUrl = in.readString();		
		ServiceDescription = in.readString();
		DocumentationUrl = in.readString();
			
	}	

	public static final Parcelable.Creator<DeviceIDInfoData> CREATOR =
		new Parcelable.Creator<DeviceIDInfoData>() {
		public DeviceIDInfoData createFromParcel(Parcel in) {
			return new DeviceIDInfoData(in);
		}
		public DeviceIDInfoData[] newArray(int size) {
			return new DeviceIDInfoData[size];
		}
	};

	private DeviceIDInfoData(Parcel in) {
		readFromParcel(in);
	}
}

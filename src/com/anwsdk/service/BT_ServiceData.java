package com.anwsdk.service;

public class BT_ServiceData {
	public BT_Service service;
	public int    data_len;
	public byte[] service_data;

	public void setService(BT_Service data)
	{
		service = data;
	}
	public BT_Service getService()
	{
		return service ;
	}
	
	public void setServiceData(byte[] data) {
		if(data !=null)
		{
			data_len = data.length;
			service_data = data;
		}
		else
			service_data =null;
	}	
	public byte[] getServiceData() {
		return service_data;
	}

	public BT_ServiceData(BT_Service ser, byte[] data)
	{
		service = ser;
		setServiceData(data);
	
	}
	public BT_ServiceData()
	{
		service = new BT_Service();
		data_len =0;
		service_data =null;
	}
}
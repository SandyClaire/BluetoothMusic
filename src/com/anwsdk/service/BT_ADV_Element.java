package com.anwsdk.service;

public class BT_ADV_Element {
	public int   element_type;
	public int   data_len;
	public byte[] element_data;

	public int getElementType() {
		return element_type;
	}
	public byte[] getElementData() {
		return element_data;
	}	
	public BT_ADV_Element(int type,  byte[] data) {
		element_type =  type & 0xFF; //signed to unsigned
		if(data!=null)
		{
			data_len = data.length;
			element_data = data;
		}
		else 
			element_data = null;		
	}
	public BT_ADV_Element()
	{
		element_type =0;
		element_data = null;
	}
}
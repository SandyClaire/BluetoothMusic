package com.anwsdk.service;
import com.anwsdk.service.BT_ADV_DATA;

interface IAnwInquiryCallBackEx {
	void InquiryDataRsp(String address,String strName,int cod,int RSSI,in BT_ADV_DATA EirData,boolean bComplete);
}

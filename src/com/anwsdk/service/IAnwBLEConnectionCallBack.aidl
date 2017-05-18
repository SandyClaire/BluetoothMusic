package com.anwsdk.service;
import com.anwsdk.service.BT_BLE_Service;

interface IAnwBLEConnectionCallBack {
	int OnBLEGetAllServiceRsp(int index,in BT_BLE_Service ServiceData,int error,int Flag);
	void OnBLEBLENotification(int index,int handle,in byte[] data,long datasize);
	void OnBLEExchangeMtu(int index,int status,int mtu);
	void OnBLEWriteChar(int index,int handle,int status,in byte[] data,long datasize);
	void OnBLEWriteCharWithoutRes(int index,int handle,int status,in byte[] data,long datasize);
	void OnBLEReadChar(int index,int handle,int status,in byte[] data,long datasize);

}

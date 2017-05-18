package com.anwsdk.service;
import com.anwsdk.service.BT_BLE_Device;

interface IAnwBLEDiscoveryCallBack {
	void BLEDiscoveryRsp(in BT_BLE_Device DeviceData,boolean bUpdate,boolean bComplete);
}

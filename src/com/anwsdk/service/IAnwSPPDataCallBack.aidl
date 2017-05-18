package com.anwsdk.service;

interface IAnwSPPDataCallBack {
	void SPPDataIND(int nIndex,in byte[] data,int DataLength);
}

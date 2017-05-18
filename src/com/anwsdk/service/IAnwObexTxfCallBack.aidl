package com.anwsdk.service;

interface IAnwObexTxfCallBack {
	int ObexTxfStatus(int nIndex,int nTxfType,String localfilename,int nCur,int nTotal,int nErrorcode,int bComplete);
}

package com.anwsdk.service;

import com.anwsdk.service.GsmMemoryEntry;

interface IAnwPhonebookCallBack {
	int ContactDataRsp(int nDataType,int nCur,int nTotal,int error,in GsmMemoryEntry entrydata,int flag);
}

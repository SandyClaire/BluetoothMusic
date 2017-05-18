package com.anwsdk.service;
import com.anwsdk.service.EmailData;

interface IAnwEmailCallBack {
	int EmailDataRsp(int nCur,int nTotal,int error,in EmailData email,int flag);
}

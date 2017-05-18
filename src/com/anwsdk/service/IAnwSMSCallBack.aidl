package com.anwsdk.service;
import com.anwsdk.service.SmsData;

interface IAnwSMSCallBack {
	int SMSDataRsp(int nCur,int nTotal,int error,in SmsData sms,int flag);
}

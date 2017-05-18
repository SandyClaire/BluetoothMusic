package com.anwsdk.service;
import com.anwsdk.service.MediaItemData;

interface IAnwBrowsingCallBack {
	int BrowsingMediaDataRsp(in MediaItemData media_data,int nCur,int nTotal,int error_code,int bComplete);
}

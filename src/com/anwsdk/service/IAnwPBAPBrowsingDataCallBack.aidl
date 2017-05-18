package com.anwsdk.service;

import com.anwsdk.service.PBAPBrowsingItemData;

interface IAnwPBAPBrowsingDataCallBack {
	int ListingItemDataRsp(int nDataType,int nCur,int nTotal,int error,in PBAPBrowsingItemData itemdata,int flag);
}

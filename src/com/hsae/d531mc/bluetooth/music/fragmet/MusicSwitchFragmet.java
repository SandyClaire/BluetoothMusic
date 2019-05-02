//package com.hsae.d531mc.bluetooth.music.fragmet;
//
//import android.annotation.SuppressLint;
//import android.app.Fragment;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager.NameNotFoundException;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.hsae.autosdk.os.Soc;
//import com.hsae.autosdk.os.SocConst.UsbDevices;
//import com.hsae.autosdk.source.Source;
//import com.hsae.autosdk.source.SourceConst.App;
//import com.hsae.autosdk.util.LogUtil;
//import com.hsae.d531mc.bluetooth.music.MusicMainActivity;
//import com.hsae.d531mc.bluetooth.music.R;
//
///**
// * 
// * @author wangda
// *
// */
//@SuppressLint("NewApi")
//public class MusicSwitchFragmet extends Fragment implements OnClickListener {
//
//	private static final String TAG = "MusicSwitchFragmet";
//	private View mView;
//	private LinearLayout mLinAM;
//	private LinearLayout mLinFM;
//	private LinearLayout mLinIpod;
//	private LinearLayout mLinBluetooth;
//	private TextView mTextAM;
//	private TextView mTextFM;
//	private TextView mTextIPOD;
//	private TextView mTextBT;
//	private TextView mTextUSB;
//	private TextView mTextSpite;
//	private Button mBtnClose;
//	private static MusicSwitchFragmet fragment;
//	private static final String RADIO_PACKAGE = "com.hsae.d531mc.radio";
//	private static final String RADIO_ACTIVITY_AM_FM = "com.hsae.d531mc.radio.RadioActivity";
//	private static final String IPOD_PACKAGE = "com.hsae.d531mc.ipod";
//	private static final String IPOD_ACTIVITY = "com.hsae.d531mc.ipod.view.MainActivity";
//	private static final String USB_PACKAGE = "com.hsae.d531mc.usbmedia";
//	private static final String USB_ACTIVITY = "com.hsae.d531mc.usbmedia.music.MusicPlayActivity";
//	private static final int SOURCE_AM = 1;
//	private static final int SOURCE_FM = 2;
//	private static final int SOURCE_USB_IPOD = 3;
//	private static final int SOURCE_BT = 0;
//
//	public static Fragment getInstance(Context mContext) {
//		if (null == fragment) {
//			fragment = new MusicSwitchFragmet();
//		}
//		return fragment;
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		mView = inflater.inflate(R.layout.music_switch, container, false);
//		return mView;
//	}
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//	}
//
//	@Override
//	public void onActivityCreated(Bundle savedInstanceState) {
//		initView();
//		super.onActivityCreated(savedInstanceState);
//	}
//
//	private void initView() {
//		LogUtil.i(TAG, "initView");
//		mLinAM = (LinearLayout) mView.findViewById(R.id.lin_music_am);
//		mLinFM = (LinearLayout) mView.findViewById(R.id.lin_music_fm);
//		mLinIpod = (LinearLayout) mView.findViewById(R.id.lin_music_ipod);
//		mLinBluetooth = (LinearLayout) mView
//				.findViewById(R.id.lin_music_bluetooth);
//		mTextAM = (TextView) mView.findViewById(R.id.text_switch_am);
//		mTextFM = (TextView) mView.findViewById(R.id.text_switch_fm);
//		mTextIPOD = (TextView) mView.findViewById(R.id.text_switch_ipod);
//		mTextBT = (TextView) mView.findViewById(R.id.text_switch_bluetooth);
//		mTextUSB = (TextView) mView.findViewById(R.id.text_switch_usb);
//		mTextSpite = (TextView) mView.findViewById(R.id.text_switch_spite);
//		mBtnClose = (Button) mView.findViewById(R.id.btn_close_switch);
//		mBtnClose.setOnClickListener(this);
//		mLinAM.setOnClickListener(this);
//		mLinFM.setOnClickListener(this);
//		mLinIpod.setOnClickListener(this);
//		mLinBluetooth.setOnClickListener(this);
//		updateSelectedShow(0);
//	}
//
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//	}
//
//	@Override
//	public void onClick(View v) {
//		Bundle bundle = new Bundle();
//		switch (v.getId()) {
//		case R.id.lin_music_am:
//			updateSelectedShow(SOURCE_AM);
//			bundle.putInt("band", 0x01);
//			startOtherAPP(App.RADIO, RADIO_PACKAGE, RADIO_ACTIVITY_AM_FM,
//					bundle);
//			((MusicMainActivity) getActivity()).closeMusicSwitch();
//			((MusicMainActivity)getActivity()).finishActivity();
//			break;
//		case R.id.lin_music_fm:
//			updateSelectedShow(SOURCE_FM);
//			bundle.putInt("band", 0x03);
//			startOtherAPP(App.RADIO, RADIO_PACKAGE, RADIO_ACTIVITY_AM_FM,
//					bundle);
//			((MusicMainActivity) getActivity()).closeMusicSwitch();
//			((MusicMainActivity)getActivity()).finishActivity();
//			break;
//		case R.id.lin_music_ipod:
//			updateSelectedShow(SOURCE_USB_IPOD);
//			boolean isUsb = isUsbConnected() || !isIpodConnected();
//			App app = isUsb ? App.USB_MUSIC : App.IPOD_MUSIC;
//			String strPackage = isUsb ? USB_PACKAGE : IPOD_PACKAGE;
//			String strClass = isUsb ? USB_ACTIVITY : IPOD_ACTIVITY;
//			LogUtil.i(TAG, "isUsb == " + isUsb + ", isUsbConnected == "
//					+ isUsbConnected() + " ,  !isIpodConnected == "
//					+ !isIpodConnected());
//			startOtherAPP(app, strPackage, strClass, bundle);
//			((MusicMainActivity) getActivity()).closeMusicSwitch();
//			((MusicMainActivity)getActivity()).finishActivity();
//			break;
//		case R.id.lin_music_bluetooth:
//			updateSelectedShow(SOURCE_BT);
//			((MusicMainActivity) getActivity()).closeMusicSwitch();
//			break;
//		case R.id.btn_close_switch:
//			((MusicMainActivity) getActivity()).closeMusicSwitch();
//			break;
//		default:
//			break;
//		}
//	}
//
//	/**
//	 * @Description: 判断USB是否连接
//	 * @return
//	 */
//	private boolean isUsbConnected() {
//		boolean isConnected = false;
//		Soc soc = new Soc();
//		UsbDevices deivce = soc.getCurrentDevice();
//		if (deivce != null) {
//			if (deivce == UsbDevices.UDISK) {
//				isConnected = true;
//			}
//		}
//		Log.i(TAG, "isUsbConnected = " + soc.getCurrentDevice());
//		return isConnected;
//	}
//
//	/**
//	 * @Description: 判断Ipod是否连接
//	 * @return
//	 */
//	private boolean isIpodConnected() {
//		boolean isConnected = false;
//		Soc soc = new Soc();
//		UsbDevices deivce = soc.getCurrentDevice();
//		if (deivce != null) {
//			if (deivce == UsbDevices.IPOD) {
//				isConnected = true;
//			}
//		}
//		Log.i(TAG, "isIPodConnected = " + soc.getCurrentDevice());
//		return isConnected;
//	}
//	
//	
//	
//
//	/**
//	 * 更新按钮状态
//	 * @param flag
//	 */
//	private void updateSelectedShow(int flag) {
//		switch (flag) {
//		case SOURCE_BT:
//			mTextAM.setTextColor(getResources().getColor(R.color.white));
//			mTextAM.setEnabled(true);
//			mTextFM.setTextColor(getResources().getColor(R.color.white));
//			mTextFM.setEnabled(true);
//			mTextIPOD.setTextColor(getResources().getColor(R.color.white));
//			mTextIPOD.setEnabled(true);
//			mTextUSB.setTextColor(getResources().getColor(R.color.white));
//			mTextSpite.setTextColor(getResources().getColor(R.color.white));
//			mTextUSB.setEnabled(true);
//			mTextBT.setTextColor(getResources().getColor(R.color.light_orange));
//			mTextBT.setEnabled(false);
//			break;
//		case SOURCE_AM:
//			mTextAM.setTextColor(getResources().getColor(R.color.light_orange));
//			mTextAM.setEnabled(false);
//			mTextFM.setTextColor(getResources().getColor(R.color.white));
//			mTextFM.setEnabled(true);
//			mTextIPOD.setTextColor(getResources().getColor(R.color.white));
//			mTextIPOD.setEnabled(true);
//			mTextUSB.setTextColor(getResources().getColor(R.color.white));
//			mTextSpite.setTextColor(getResources().getColor(R.color.white));
//			mTextUSB.setEnabled(true);
//			mTextBT.setTextColor(getResources().getColor(R.color.white));
//			mTextBT.setEnabled(true);
//			break;
//		case SOURCE_FM:
//			mTextFM.setTextColor(getResources().getColor(R.color.light_orange));
//			mTextFM.setEnabled(false);
//			mTextAM.setTextColor(getResources().getColor(R.color.white));
//			mTextAM.setEnabled(true);
//			mTextIPOD.setTextColor(getResources().getColor(R.color.white));
//			mTextIPOD.setEnabled(true);
//			mTextUSB.setTextColor(getResources().getColor(R.color.white));
//			mTextSpite.setTextColor(getResources().getColor(R.color.white));
//			mTextUSB.setEnabled(true);
//			mTextBT.setTextColor(getResources().getColor(R.color.white));
//			mTextBT.setEnabled(true);
//			break;
//		case SOURCE_USB_IPOD:
//			mTextIPOD.setTextColor(getResources()
//					.getColor(R.color.light_orange));
//			mTextUSB.setTextColor(getResources().getColor(R.color.light_orange));
//			mTextSpite.setTextColor(getResources().getColor(
//					R.color.light_orange));
//			mTextUSB.setEnabled(false);
//			mTextIPOD.setEnabled(false);
//			mTextFM.setTextColor(getResources().getColor(R.color.white));
//			mTextFM.setEnabled(true);
//			mTextAM.setTextColor(getResources().getColor(R.color.white));
//			mTextAM.setEnabled(true);
//			mTextBT.setTextColor(getResources().getColor(R.color.white));
//			mTextBT.setEnabled(true);
//			break;
//		default:
//			break;
//		}
//	}
//
//	/**
//	 * @Description: 跳转至其他应用
//	 * @param appId
//	 * @param activityName
//	 * @param bundle
//	 */
//	public void startOtherAPP(App app, String appId, String activityName,
//			Bundle bundle) {
//		Source source = new Source();
//		boolean tryToSwitchSource = source.tryToSwitchSource(app);
//		LogUtil.i(TAG, "tryToSwitchSource == " + tryToSwitchSource);
//		if (tryToSwitchSource) {
//
//			if (isAppInstalled(getActivity(), appId)) {
//				Intent intent = new Intent();
//				ComponentName comp = new ComponentName(appId, activityName);
//				intent.setComponent(comp);
//
//				int launchFlags = Intent.FLAG_ACTIVITY_NEW_TASK;
//				intent.setFlags(launchFlags);
//				intent.setAction("android.intent.action.VIEW");
//				if (bundle != null) {
//					intent.putExtras(bundle);
//				}
//
//				getActivity().startActivity(intent);
//				getActivity().finish();
//			}
//		}
//	}
//
//	/**
//	 * @Description: 判断应用是否安装
//	 * @param context
//	 * @param packagename
//	 * @return
//	 */
//	private boolean isAppInstalled(Context context, String packagename) {
//		PackageInfo packageInfo;
//		try {
//			packageInfo = context.getPackageManager().getPackageInfo(
//					packagename, 0);
//		} catch (NameNotFoundException e) {
//			packageInfo = null;
//			e.printStackTrace();
//		}
//
//		boolean isInstalled = (packageInfo == null) ? false : true;
//		return isInstalled;
//	}
//
//}

package com.hsae.d531mc.bluetooth.music.fragmet;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hsae.autosdk.os.Soc;
import com.hsae.autosdk.os.SocConst.UsbDevices;
import com.hsae.autosdk.source.Source;
import com.hsae.autosdk.source.SourceConst.App;
import com.hsae.d531mc.bluetooth.music.MusicMainActivity;
import com.hsae.d531mc.bluetooth.music.R;

/**
 * 
 * @author wangda
 *
 */
@SuppressLint("NewApi")
public class MusicSwitchFragmet extends Fragment implements OnClickListener {

	private static final String TAG = "MusicSwitchFragmet";
	private View mView;
	private LinearLayout mLinAM;
	private LinearLayout mLinFM;
	private LinearLayout mLinIpod;
	private LinearLayout mLinBluetooth;
	private TextView mTextAM;
	private TextView mTextFM;
	private TextView mTextIPOD;
	private TextView mTextBT;
	private TextView mTextUSB;
	private TextView mTextSpite;
	private Button mBtnClose;
	private static MusicSwitchFragmet fragment;
	private static final String RADIO_PACKAGE = "com.hsae.d531mc.radio";
	private static final String RADIO_ACTIVITY_AM_FM = "com.hsae.d531mc.radio.RadioActivity";
	private static final String IPOD_PACKAGE = "com.hsae.d531mc.ipod";
	private static final String IPOD_ACTIVITY = "com.hsae.d531mc.ipod.view.MainActivity";
	private static final String USB_PACKAGE = "com.hsae.d531mc.usbmedia";
	private static final String USB_ACTIVITY = "com.hsae.d531mc.usbmedia.music.MusicPlayActivity";

	public static Fragment getInstance(Context mContext) {
		if (null == fragment) {
			fragment = new MusicSwitchFragmet();
		}
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.music_switch, container, false);
		// setBlurBackground();
		return mView;
	}

	// private void setBlurBackground() {
	// int scaleRatio = 35;
	// int blurRadius = 8;
	// Bitmap originBitmap = BitmapFactory.decodeResource(getResources(),
	// R.drawable.bg_bluetoothsettings);
	// Bitmap scaledBitmap = Bitmap.createScaledBitmap(originBitmap,
	// originBitmap.getWidth() / scaleRatio,
	// originBitmap.getHeight() / scaleRatio,
	// false);
	// Bitmap blurBitmap = FastBlurUtil.doBlur(scaledBitmap, blurRadius, true);
	// Drawable background = new BitmapDrawable(getResources(), blurBitmap);
	// mView.setBackground(background);
	// }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		initView();
		super.onActivityCreated(savedInstanceState);
	}

	private void initView() {
		mLinAM = (LinearLayout) mView.findViewById(R.id.lin_music_am);
		mLinFM = (LinearLayout) mView.findViewById(R.id.lin_music_fm);
		mLinIpod = (LinearLayout) mView.findViewById(R.id.lin_music_ipod);
		mLinBluetooth = (LinearLayout) mView
				.findViewById(R.id.lin_music_bluetooth);
		mTextAM = (TextView) mView.findViewById(R.id.text_switch_am);
		mTextFM = (TextView) mView.findViewById(R.id.text_switch_fm);
		mTextIPOD = (TextView) mView.findViewById(R.id.text_switch_ipod);
		mTextBT = (TextView) mView.findViewById(R.id.text_switch_bluetooth);
		mTextUSB = (TextView) mView.findViewById(R.id.text_switch_usb);
		mTextSpite = (TextView) mView.findViewById(R.id.text_switch_spite);
		mBtnClose = (Button) mView.findViewById(R.id.btn_close_switch);
		mBtnClose.setOnClickListener(this);
		mLinAM.setOnClickListener(this);
		mLinFM.setOnClickListener(this);
		mLinIpod.setOnClickListener(this);
		mLinBluetooth.setOnClickListener(this);
		updateSelectedShow(0);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		Bundle bundle = new Bundle();
		switch (v.getId()) {
		case R.id.lin_music_am:
			updateSelectedShow(1);
			bundle.putInt("band", 0x01);
			startOtherAPP(App.RADIO, RADIO_PACKAGE, RADIO_ACTIVITY_AM_FM,
					bundle);
			break;
		case R.id.lin_music_fm:
			updateSelectedShow(2);
			bundle.putInt("band", 0x03);
			startOtherAPP(App.RADIO, RADIO_PACKAGE, RADIO_ACTIVITY_AM_FM,
					bundle);
			break;
		case R.id.lin_music_ipod:
			updateSelectedShow(3);
			boolean isUsb = isUsbConnected() || !isIpodConnected();
			App app = isUsb ? App.USB_MUSIC : App.IPOD_MUSIC;
			String strPackage = isUsb ? USB_PACKAGE : IPOD_PACKAGE;
			String strClass = isUsb ? USB_ACTIVITY : IPOD_ACTIVITY;
			// Log.i("wangda", "isUsb == " + isUsb + ", isUsbConnected == " +
			// isUsbConnected() + " ,  !isIpodConnected == " +
			// !isIpodConnected());
			startOtherAPP(app, strPackage, strClass, bundle);
			break;
		case R.id.lin_music_bluetooth:
			updateSelectedShow(0);
			break;
		case R.id.btn_close_switch:
			((MusicMainActivity) getActivity()).closeMusicSwitch();
			break;
		default:
			break;
		}
	}

	/**
	 * @Description: 判断USB是否连接
	 * @return
	 */
	private boolean isUsbConnected() {
		boolean isConnected = false;
		Soc soc = new Soc();
		UsbDevices deivce = soc.getCurrentDevice();
		if (deivce != null) {
			if (deivce == UsbDevices.UDISK) {
				isConnected = true;
			}
		}
		Log.i(TAG, "isUsbConnected = " + soc.getCurrentDevice());
		return isConnected;
	}

	/**
	 * @Description: 判断Ipod是否连接
	 * @return
	 */
	private boolean isIpodConnected() {
		boolean isConnected = false;
		Soc soc = new Soc();
		UsbDevices deivce = soc.getCurrentDevice();
		if (deivce != null) {
			if (deivce == UsbDevices.IPOD) {
				isConnected = true;
			}
		}
		Log.i(TAG, "isIPodConnected = " + soc.getCurrentDevice());
		return isConnected;
	}

	private void updateSelectedShow(int flag) {
		switch (flag) {
		case 0:
			mTextAM.setTextColor(getResources().getColor(R.color.white));
			mTextAM.setEnabled(true);
			mTextFM.setTextColor(getResources().getColor(R.color.white));
			mTextFM.setEnabled(true);
			mTextIPOD.setTextColor(getResources().getColor(R.color.white));
			mTextIPOD.setEnabled(true);
			mTextUSB.setTextColor(getResources().getColor(R.color.white));
			mTextSpite.setTextColor(getResources().getColor(R.color.white));
			mTextUSB.setEnabled(true);
			mTextBT.setTextColor(getResources().getColor(R.color.light_orange));
			mTextBT.setEnabled(false);
			break;
		case 1:
			mTextAM.setTextColor(getResources().getColor(R.color.light_orange));
			mTextAM.setEnabled(false);
			mTextFM.setTextColor(getResources().getColor(R.color.white));
			mTextFM.setEnabled(true);
			mTextIPOD.setTextColor(getResources().getColor(R.color.white));
			mTextIPOD.setEnabled(true);
			mTextUSB.setTextColor(getResources().getColor(R.color.white));
			mTextSpite.setTextColor(getResources().getColor(R.color.white));
			mTextUSB.setEnabled(true);
			mTextBT.setTextColor(getResources().getColor(R.color.white));
			mTextBT.setEnabled(true);
			break;
		case 2:
			mTextFM.setTextColor(getResources().getColor(R.color.light_orange));
			mTextFM.setEnabled(false);
			mTextAM.setTextColor(getResources().getColor(R.color.white));
			mTextAM.setEnabled(true);
			mTextIPOD.setTextColor(getResources().getColor(R.color.white));
			mTextIPOD.setEnabled(true);
			mTextUSB.setTextColor(getResources().getColor(R.color.white));
			mTextSpite.setTextColor(getResources().getColor(R.color.white));
			mTextUSB.setEnabled(true);
			mTextBT.setTextColor(getResources().getColor(R.color.white));
			mTextBT.setEnabled(true);
			break;
		case 3:
			mTextIPOD.setTextColor(getResources()
					.getColor(R.color.light_orange));
			mTextUSB.setTextColor(getResources().getColor(R.color.light_orange));
			mTextSpite.setTextColor(getResources().getColor(
					R.color.light_orange));
			mTextUSB.setEnabled(false);
			mTextIPOD.setEnabled(false);
			mTextFM.setTextColor(getResources().getColor(R.color.white));
			mTextFM.setEnabled(true);
			mTextAM.setTextColor(getResources().getColor(R.color.white));
			mTextAM.setEnabled(true);
			mTextBT.setTextColor(getResources().getColor(R.color.white));
			mTextBT.setEnabled(true);
			break;
		default:
			break;
		}
	}

	/**
	 * @Description: 跳转至其他应用
	 * @param appId
	 * @param activityName
	 * @param bundle
	 */
	public void startOtherAPP(App app, String appId, String activityName,
			Bundle bundle) {
		// Log.i("wangda", "tryToSwitchSource -------------------------- ");
		Source source = new Source();
		boolean tryToSwitchSource = source.tryToSwitchSource(app);
		// Log.i("wangda", "tryToSwitchSource == " + tryToSwitchSource);
		if (tryToSwitchSource) {

			if (isAppInstalled(getActivity(), appId)) {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);
				ComponentName comp = new ComponentName(appId, activityName);
				intent.setComponent(comp);

				int launchFlags = Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED;
				intent.setFlags(launchFlags);
				intent.setAction("android.intent.action.VIEW");
				if (bundle != null) {
					intent.putExtras(bundle);
				}

				// Log.i("wangda", "activityName == " + activityName);
				getActivity().startActivity(intent);
				getActivity().finish();
			}
		}
	}

	/**
	 * @Description: 判断应用是否安装
	 * @param context
	 * @param packagename
	 * @return
	 */
	private boolean isAppInstalled(Context context, String packagename) {
		PackageInfo packageInfo;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(
					packagename, 0);
		} catch (NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}

		boolean isInstalled = (packageInfo == null) ? false : true;
		return isInstalled;
	}

}

package com.hsae.d531mc.bluetooth.music.fragmet;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hsae.d531mc.bluetooth.music.R;

/**
 * 
 * @author wangda
 *
 */
@SuppressLint("NewApi")
public class MusicSwitchFragmet extends Fragment implements OnClickListener{

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
	private Context mContext;
	
	public MusicSwitchFragmet(android.content.Context context) {
		super();
		mContext = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.music_switch, container, false);
		return mView;
	}
	
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
	
	private void initView(){
		mLinAM = (LinearLayout) mView.findViewById(R.id.lin_music_am);
		mLinFM = (LinearLayout) mView.findViewById(R.id.lin_music_fm);
		mLinIpod = (LinearLayout) mView.findViewById(R.id.lin_music_ipod);
		mLinBluetooth = (LinearLayout) mView.findViewById(R.id.lin_music_bluetooth);
		mTextAM = (TextView) mView.findViewById(R.id.text_switch_am);
		mTextFM = (TextView) mView.findViewById(R.id.text_switch_fm);
		mTextIPOD = (TextView) mView.findViewById(R.id.text_switch_ipod);
		mTextBT = (TextView) mView.findViewById(R.id.text_switch_bluetooth);
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
			startOtherAPP("com.hsae.d531mc.radio", "com.hsae.d531mc.radio.RadioActivity", bundle);
			break;
		case R.id.lin_music_fm:
			updateSelectedShow(2);
			bundle.putInt("band", 0x03);
			startOtherAPP("com.hsae.d531mc.radio", "com.hsae.d531mc.radio.RadioActivity", bundle);
			break;
		case R.id.lin_music_ipod:
			updateSelectedShow(3);
			startOtherAPP("com.hsae.d531mc.ipod", "com.hsae.d531mc.ipod.view.MainActivity", bundle);
			break;
		case R.id.lin_music_bluetooth:
			updateSelectedShow(0);
			break;

		default:
			break;
		}
	}
	
	private void updateSelectedShow(int flag){
		switch (flag) {
		case 0:
			mTextAM.setTextColor(getResources().getColor(R.color.white));
			mTextAM.setEnabled(true);
			mTextFM.setTextColor(getResources().getColor(R.color.white));
			mTextFM.setEnabled(true);
			mTextIPOD.setTextColor(getResources().getColor(R.color.white));
			mTextIPOD.setEnabled(true);
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
			mTextBT.setTextColor(getResources().getColor(R.color.white));
			mTextBT.setEnabled(true);
			break;
		case 3:
			mTextIPOD.setTextColor(getResources().getColor(R.color.light_orange));
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
    public void startOtherAPP(String appId, String activityName, Bundle bundle) {
        if (isAppInstalled(getActivity(), appId)) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName comp = new ComponentName(appId, activityName);
            intent.setComponent(comp);

            int launchFlags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED;
            intent.setFlags(launchFlags);
            intent.setAction("android.intent.action.VIEW");
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            getActivity().startActivity(intent);
            getActivity().finish();
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
            packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }

        boolean isInstalled = (packageInfo == null) ? false : true;
        return isInstalled;
    }
	
}

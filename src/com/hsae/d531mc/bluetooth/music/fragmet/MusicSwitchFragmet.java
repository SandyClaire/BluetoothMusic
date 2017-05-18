package com.hsae.d531mc.bluetooth.music.fragmet;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
		mLinAM.setOnClickListener(this);
		mLinFM.setOnClickListener(this);
		mLinIpod.setOnClickListener(this);
		mLinBluetooth.setOnClickListener(this);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lin_music_am:
			
			break;
		case R.id.lin_music_fm:
			
			break;
		case R.id.lin_music_ipod:
	
			break;
		case R.id.lin_music_bluetooth:
	
			break;

		default:
			break;
		}
	}
	
	
}

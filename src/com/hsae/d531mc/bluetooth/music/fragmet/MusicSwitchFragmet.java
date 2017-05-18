package com.hsae.d531mc.bluetooth.music.fragmet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.hsae.d531mc.bluetooth.music.R;

/**
 * 
 * @author wangda
 *
 */
@SuppressLint("NewApi")
public class MusicSwitchFragmet extends Fragment {

	private static final String TAG = "MusicSwitchFragmet";
	private View mView;
	private ListView mListSwitch;
	private SwitchAdapter mAdapter;
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
		mListSwitch = (ListView) mView.findViewById(R.id.list_music_switch);
		mAdapter = new SwitchAdapter();
		mListSwitch.setAdapter(mAdapter);
	}
	
	@Override
	public void onDestroy() {
		
		super.onDestroy();
	}
	
	private class SwitchAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder mHolder;
			if (convertView == null) {
				mHolder = new ViewHolder();
				convertView = ((Activity) mContext).getLayoutInflater()
						.inflate(R.layout.list_switch_item, null);
				mHolder.mLinSwitch = (LinearLayout) convertView.findViewById(R.id.lin_switch_item);
				convertView.setTag(mHolder);
			}else {
				mHolder = (ViewHolder) convertView.getTag();
			}
			return convertView;
		}
		
	}
	
	public class ViewHolder{
		LinearLayout mLinSwitch;
	}
	
	
	
}

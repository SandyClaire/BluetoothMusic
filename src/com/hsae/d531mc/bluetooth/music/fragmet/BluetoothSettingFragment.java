//package com.hsae.d531mc.bluetooth.music.fragmet;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import android.annotation.SuppressLint;
//import android.app.Fragment;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Message;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import com.anwsdk.service.MangerConstant;
//import com.hsae.autosdk.util.LogUtil;
//import com.hsae.d531mc.bluetooth.music.MusicMainActivity;
//import com.hsae.d531mc.bluetooth.music.R;
//import com.hsae.d531mc.bluetooth.music.entry.BluetoothDevice;
//import com.hsae.d531mc.bluetooth.music.model.impl.BluetoothSettingModel;
//import com.hsae.d531mc.bluetooth.music.observer.IObserver;
//import com.hsae.d531mc.bluetooth.music.observer.ISubject;
//import com.hsae.d531mc.bluetooth.music.observer.ObserverAdapter;
//import com.hsae.d531mc.bluetooth.music.presenter.BluetoothSettingPresenter;
//import com.hsae.d531mc.bluetooth.music.util.ListViewEx;
//import com.hsae.d531mc.bluetooth.music.util.MusicActionDefine;
//import com.hsae.d531mc.bluetooth.music.view.IBluetoothSettingView;
//
///**
// * 
// * @author wangda
// *
// */
//public class BluetoothSettingFragment extends Fragment implements ISubject, IBluetoothSettingView, OnClickListener {
//
//	private static final String TAG = "BluetoothSettingFragment";
//	private static final String ACTION_REMOTE_BLUETOOTHPAIR = "com.remote.bluetooth.pair";
//
//	private View mView;
//	private ListView mListVisible;
//	private Button mBtnSearch;
//	private Button mBtnClose;
//	private BluetoothDeviceAdapter mVisibleAdapter;
//	private BluetoothSettingPresenter mPresenter;
//	private boolean isSearching = false;
//	private boolean isPairing = false;
//	private boolean isStop = true;
//	private LayoutInflater mInflater;
//	private ProgressBar mProSearch;
//	private TextView mTextLocalName;
//	private LinearLayout mLinClose;
//
//	private static Context mContext;
//	private ListViewEx mListPaired;
//	private PairedAdapter mPairedAdapter;
//	private LinearLayout mLinPair;
//	private LinearLayout mLinVis;
//	private TextView mTextEnable;
//	private static BluetoothSettingFragment fragment;
//	private List<BluetoothDevice> mListPairedDevices = new ArrayList<BluetoothDevice>();
//	private List<BluetoothDevice> mListVisibleDevices = new ArrayList<BluetoothDevice>();
//
//	public static Fragment getInstance(Context nContext) {
//		if (null == fragment) {
//			fragment = new BluetoothSettingFragment();
//		}
//		mContext = nContext;
//		return fragment;
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		mView = inflater.inflate(R.layout.bluetooth_setting_fragment, container, false);
//		return mView;
//	}
//
//	@Override
//	public void onActivityCreated(Bundle savedInstanceState) {
//		super.onActivityCreated(savedInstanceState);
//		initView();
//		initMVP();
//	}
//
//	@SuppressLint("InflateParams")
//	private void initView() {
//		mInflater = LayoutInflater.from(mContext);
//		View headerView = mInflater.inflate(R.layout.devices_list_header_view, null);
//		mProSearch = (ProgressBar) headerView.findViewById(R.id.pro_search2);
//		mListVisible = (ListView) mView.findViewById(R.id.list_visible_devices);
//		mBtnSearch = (Button) mView.findViewById(R.id.btn_search);
//		mListPaired = (ListViewEx) headerView.findViewById(R.id.list_paired_device);
//		mLinPair = (LinearLayout) headerView.findViewById(R.id.linear_pair);
//		mLinVis = (LinearLayout) headerView.findViewById(R.id.lin_visible_text);
//		mTextEnable = (TextView) mView.findViewById(R.id.text_enable_tip);
//		mTextLocalName = (TextView) mView.findViewById(R.id.text_local_name);
//		mBtnClose = (Button) mView.findViewById(R.id.btn_setting_close);
//		mLinClose = (LinearLayout) mView.findViewById(R.id.lin_setting_close);
//
//		mVisibleAdapter = new BluetoothDeviceAdapter(mContext);
//		mPairedAdapter = new PairedAdapter();
//		mListPaired.setAdapter(mPairedAdapter);
//		mListVisible.setAdapter(mVisibleAdapter);
//		mListVisible.addHeaderView(headerView);
//		mBtnSearch.setOnClickListener(this);
//		mBtnClose.setOnClickListener(this);
//		mLinClose.setOnClickListener(this);
//	}
//
//	private void initMVP() {
//		LogUtil.i(TAG, "BluetoothSettingFragment initMVP");
//		BluetoothSettingModel btModel = new BluetoothSettingModel(mContext);
//		mPresenter = new BluetoothSettingPresenter(btModel, this, mContext);
//		this.attach(mPresenter);
//		Message msg = Message.obtain();
//		msg.what = MusicActionDefine.ACTION_APP_LAUNCHED;
//		this.notify(msg, FLAG_RUN_SYNC);
//		LogUtil.i(TAG, "--- initMVP +++");
//	}
//
//	private void searchDevices() {
//		mVisibleAdapter.removeAll();
//		updateSearchBtnShow(true);
//		Message msg_search = Message.obtain();
//		msg_search.what = MusicActionDefine.ACTION_SETTING_INQUIRY;
//		this.notify(msg_search, FLAG_RUN_SYNC);
//	}
//
//	public void stopDeviceSearching() {
//		updateSearchBtnShow(false);
//		Message msg_stop = Message.obtain();
//		msg_stop.what = MusicActionDefine.ACTION_SETTING_STOP_INQUIRY;
//		this.notify(msg_stop, FLAG_RUN_MAIN_THREAD);
//	}
//
//	private void updateSearchBtnShow(boolean flag) {
//		if (flag) {
//			isSearching = true;
//			if (mBtnSearch != null && mProSearch != null) {
//				mBtnSearch.setBackgroundResource(R.drawable.btn_bluetoothsettings_search_stop);
//				mProSearch.setVisibility(View.VISIBLE);
//			}
//		} else {
//			isSearching = false;
//			if (mBtnSearch != null && mProSearch != null) {
//				mBtnSearch.setBackgroundResource(R.drawable.btn_bluetoothsettings_search);
//				mProSearch.setVisibility(View.INVISIBLE);
//			}
//		}
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//
//		Message msg = Message.obtain();
//		msg.what = MusicActionDefine.ACTION_APP_RESUME;
//		this.notify(msg, FLAG_RUN_SYNC);
//	}
//
//	@Override
//	public void onDetach() {
//		super.onDetach();
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.btn_search:
//			LogUtil.i(TAG, "isPairing = " + isPairing + "isStop = " + isStop);
//			if (isPairing || (!isStop && isSearching)) {
//				return;
//			}
//			isSearching();
//			if (isSearching) {
//				isStop = false;
//				stopDeviceSearching();
//			} else {
//				searchDevices();
//			}
//			break;
//
//		case R.id.btn_setting_close:
//			((MusicMainActivity) getActivity()).closeMusicSwitch();
//			break;
//		case R.id.lin_setting_close:
//			break;
//		default:
//			break;
//		}
//	}
//
//	private void isSearching() {
//		Message msg = Message.obtain();
//		msg.what = MusicActionDefine.ACTION_ISBTSEARCHING;
//		this.notify(msg, FLAG_RUN_MAIN_THREAD);
//	}
//
//	@Override
//	public boolean attach(IObserver inObserver) {
//		return ObserverAdapter.getInstance().register(this, inObserver);
//	}
//
//	@Override
//	public boolean detach(IObserver inObserver) {
//		return ObserverAdapter.getInstance().unregister(this, inObserver);
//	}
//
//	@Override
//	public void notify(Message inMessage, int... flag) {
//		ObserverAdapter.getInstance().notify(this, inMessage, flag);
//	}
//
//	@Override
//	public void showVisibleDevices(BluetoothDevice bean) {
//		mVisibleAdapter.addBean(bean);
//	}
//
//	@Override
//	public void inquiryFinish() {
//		this.isStop = true;
//		updateSearchBtnShow(false);
//	}
//
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//		Message msg = Message.obtain();
//		msg.what = MusicActionDefine.ACTION_APP_EXIT;
//		this.notify(msg, FLAG_RUN_SYNC);
//		this.detach(mPresenter);
//	}
//
//	private class PairedAdapter extends BaseAdapter {
//
//		class ViewHolder {
//			TextView mTextDeviceName;
//			// TextView mTextDeviceStatus;
//			LinearLayout mBtnDeviceUpair;
//			ImageView mImageUnpair;
//			TextView tvDisConnect;
//			LinearLayout mLinItem;
//
//			ProgressBar progressBar;
//		}
//
//		@Override
//		public int getCount() {
//			return mListPairedDevices.size();
//		}
//
//		@Override
//		public Object getItem(int position) {
//			return null;
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return 0;
//		}
//
//		@SuppressLint({ "NewApi", "InflateParams" })
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			ViewHolder holder;
//			if (null == convertView) {
//				convertView = mInflater.inflate(R.layout.devices_item, null);
//				holder = new ViewHolder();
//				holder.mTextDeviceName = (TextView) convertView.findViewById(R.id.tv_devicename);
//				holder.mLinItem = (LinearLayout) convertView.findViewById(R.id.lin_devcies_list_item);
//				// holder.mTextDeviceStatus = (TextView)
//				// convertView.findViewById(R.id.tv_devicestatus);
//				holder.mBtnDeviceUpair = (LinearLayout) convertView.findViewById(R.id.btn_unpair_disconnect);
//				holder.mImageUnpair = (ImageView) convertView.findViewById(R.id.img_upair_disconnect);
//				holder.progressBar = (ProgressBar) convertView.findViewById(R.id.pro_pairing);
//				holder.tvDisConnect = (TextView) convertView.findViewById(R.id.tv_disconnect);
//				convertView.setTag(holder);
//			} else {
//				holder = (ViewHolder) convertView.getTag();
//			}
//
//			final BluetoothDevice bean = mListPairedDevices.get(position);
//			holder.mBtnDeviceUpair.setVisibility(View.VISIBLE);
//			holder.mBtnDeviceUpair.setBackgroundResource(R.drawable.bg_list_item_selected);
//			holder.mTextDeviceName.setText(bean.getDeviceName());
//			holder.progressBar.setVisibility(View.GONE);
//			// holder.mTextDeviceStatus.setVisibility(View.VISIBLE);
//			holder.mImageUnpair.setVisibility(View.VISIBLE);
//			if (bean.getStatus() == BluetoothDevice.DEVICE_CONNECTED) {
//				holder.progressBar.setVisibility(View.GONE);
//				holder.mImageUnpair.setVisibility(View.GONE);
//				holder.tvDisConnect.setVisibility(View.VISIBLE);
//				holder.mBtnDeviceUpair.setBackground(null);
//
//				// holder.mTextDeviceStatus.setVisibility(View.GONE);
//				// holder.mTextDeviceStatus.setText(getResources().getString(R.string.bluetooth_status_connected));
//				// holder.mImageUnpair.setBackground(getResources().getDrawable(R.drawable.btn_disconnect_device));
//
//				holder.mTextDeviceName.setTextColor(getResources().getColor(R.color.yellow_trans));
//				holder.mLinItem.setEnabled(true);
//			} else if (bean.getStatus() == BluetoothDevice.DEVICE_PAIRED) {
//				holder.progressBar.setVisibility(View.GONE);
//				holder.mImageUnpair.setVisibility(View.VISIBLE);
//				holder.tvDisConnect.setVisibility(View.GONE);
//
//				// holder.mTextDeviceStatus.setVisibility(View.GONE);
//				holder.mImageUnpair.setBackground(getResources().getDrawable(R.drawable.btn_unpair_device));
//				holder.mTextDeviceName.setTextColor(getResources().getColor(R.color.white_trans));
//				holder.mLinItem.setEnabled(true);
//			} else if (bean.getStatus() == BluetoothDevice.DEVICE_PAIRING) {
//				holder.progressBar.setVisibility(View.VISIBLE);
//				holder.mImageUnpair.setVisibility(View.GONE);
//				holder.tvDisConnect.setVisibility(View.GONE);
//				// holder.mTextDeviceStatus.setVisibility(View.VISIBLE);
//				// holder.mTextDeviceStatus.setText(getResources().getString(R.string.bluetooth_status_connecting));
//				holder.mImageUnpair.setBackground(getResources().getDrawable(R.drawable.btn_unpair_device));
//				holder.mTextDeviceName.setTextColor(getResources().getColor(R.color.white_trans));
//				holder.mLinItem.setEnabled(false);
//			}
//
//			else if (bean.getStatus() == BluetoothDevice.DEVICE_CONNECTING) {
//				holder.progressBar.setVisibility(View.VISIBLE);
//				holder.mImageUnpair.setVisibility(View.GONE);
//				holder.tvDisConnect.setVisibility(View.GONE);
//
//				// holder.mTextDeviceStatus.setVisibility(View.VISIBLE);
//				// holder.mTextDeviceStatus.setText(getResources().getString(R.string.bluetooth_status_connecting));
//				holder.mImageUnpair.setBackground(getResources().getDrawable(R.drawable.btn_unpair_device));
//				holder.mTextDeviceName.setTextColor(getResources().getColor(R.color.white_trans));
//				holder.mLinItem.setEnabled(false);
//			} else if (bean.getStatus() == BluetoothDevice.DEVICE_DISCONNECTING) {
//
//				holder.progressBar.setVisibility(View.GONE);
//				holder.mImageUnpair.setVisibility(View.VISIBLE);
//				holder.tvDisConnect.setVisibility(View.GONE);
//
//				// holder.mTextDeviceStatus.setVisibility(View.VISIBLE);
//				// holder.mTextDeviceStatus.setText(getResources().getString(R.string.bluetooth_status_disconnecting));
//				holder.mImageUnpair.setBackground(getResources().getDrawable(R.drawable.btn_unpair_device));
//				holder.mTextDeviceName.setTextColor(getResources().getColor(R.color.white_trans));
//				holder.mLinItem.setEnabled(false);
//			}
//
//			holder.mBtnDeviceUpair.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					LogUtil.i(TAG, "unpairRequest -- status =  " + bean.getStatus());
//					if (bean.getStatus() == BluetoothDevice.DEVICE_CONNECTED) {
//						disconnectRequest(bean);
//					} else if (bean.getStatus() == BluetoothDevice.DEVICE_PAIRED) {
//						unpairRequest(bean);
//					}
//				}
//			});
//
//			holder.mLinItem.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					for (BluetoothDevice mBluetoothDevice : mListPairedDevices) {
//						if (mBluetoothDevice.getStatus() == BluetoothDevice.DEVICE_CONNECTING) {
//							return;
//						}
//					}
//					connectRequest(bean);
//				}
//			});
//
//			return convertView;
//		}
//	}
//
//	private void connectRequest(BluetoothDevice bean) {
//		if (bean.getStatus() == BluetoothDevice.DEVICE_PAIRED) {
//
//			// 用于判断是否从车机端发出的配对。此处若是之前已配对，但手机端已移除配对、则再此连接时会发出配对请求
//			Intent intent = new Intent(ACTION_REMOTE_BLUETOOTHPAIR);
//			intent.putExtra("remoteAddress", bean.getAddress());
//			mContext.sendBroadcast(intent);
//
//			bean.setStatus(BluetoothDevice.DEVICE_CONNECTING);
//			mPairedAdapter.notifyDataSetChanged();
//
//			Message msg = Message.obtain();
//			msg.what = MusicActionDefine.ACTION_SETTING_CONNECT_MOBILE;
//			Bundle mBundle = new Bundle();
//			mBundle.putString("connectAddress", bean.getAddress());
//			msg.setData(mBundle);
//			BluetoothSettingFragment.this.notify(msg, FLAG_RUN_SYNC);
//		}
//	}
//
//	private void disconnectRequest(BluetoothDevice bean) {
//		bean.setStatus(BluetoothDevice.DEVICE_DISCONNECTING);
//		mPairedAdapter.notifyDataSetChanged();
//		Message msg = Message.obtain();
//		msg.what = MusicActionDefine.ACTION_SETTING_DISCONNECT_MOBILE;
//		BluetoothSettingFragment.this.notify(msg, FLAG_RUN_SYNC);
//	}
//
//	private void unpairRequest(BluetoothDevice bean) {
//		LogUtil.i(TAG, "unpairRequest -- name =  " + bean.getDeviceName());
//		Message msg = Message.obtain();
//		msg.what = MusicActionDefine.ACTION_SETTING_UNPAIR;
//		Bundle mBundle = new Bundle();
//		mBundle.putString("pairdAddress", bean.getAddress());
//		msg.setData(mBundle);
//		BluetoothSettingFragment.this.notify(msg, FLAG_RUN_SYNC);
//	}
//
//	@Override
//	public void showPairedDevices(List<BluetoothDevice> mList) {
//		LogUtil.i(TAG, "showPairedDevices--SIZE = " + mList.size());
//		if (null != mList) {
//			if (mList.size() == 0) {
//				mListPairedDevices.clear();
//			} else {
//				mListPairedDevices.clear();
//				mListPairedDevices.addAll(mList);
//				moveConnectedDeviceToFirst(mListPairedDevices);
//			}
//			updatePairListVisible();
//			mPairedAdapter.notifyDataSetChanged();
//		}
//	}
//
//	private void moveConnectedDeviceToFirst(List<BluetoothDevice> pairList) {
//		for (int i = 0; i < pairList.size(); i++) {
//			if (pairList.get(i).getStatus() == BluetoothDevice.DEVICE_CONNECTED) {
//				BluetoothDevice nDevice = pairList.get(0);
//				BluetoothDevice currentDevice = pairList.get(i);
//				pairList.set(0, currentDevice);
//				pairList.set(i, nDevice);
//			}
//		}
//	}
//
//	@Override
//	public void updateUnpairListByStatus(int status, String address) {
//		LogUtil.i(TAG, "updateUnpairListByStatus -- status = " + status + "-- address = " + address);
//		if (status == MangerConstant.Anw_SUCCESS) {
//			for (int i = 0; i < mListVisibleDevices.size(); i++) {
//				if (mListVisibleDevices.get(i).getAddress().equals(address)) {
//					mListVisibleDevices.remove(i);
//				}
//			}
//		} else {
//			for (int i = 0; i < mListVisibleDevices.size(); i++) {
//				if (mListVisibleDevices.get(i).getAddress().equals(address)) {
//					mListVisibleDevices.get(i).setStatus(BluetoothDevice.DEVICE_UNPAIR);
//				}
//			}
//		}
//		isPairing = false;
//		mVisibleAdapter.notifyDataSetChanged();
//	}
//
//	public class BluetoothDeviceAdapter extends BaseAdapter {
//
//		class ViewHolder {
//			TextView mTextDeviceName;
//			// TextView mTextStatus;
//			LinearLayout mLinItem;
//			ProgressBar progressBar;
//		}
//
//		private Context mContext;
//		private LayoutInflater mInflater;
//
//		public BluetoothDeviceAdapter(Context nContext) {
//			super();
//			this.mContext = nContext;
//			mInflater = LayoutInflater.from(mContext);
//		}
//
//		@Override
//		public int getCount() {
//			return mListVisibleDevices.size();
//		}
//
//		@Override
//		public Object getItem(int position) {
//			return mListVisibleDevices.get(position);
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return position;
//		}
//
//		public void removeAll() {
//			if (null == mListVisibleDevices)
//				return;
//			mListVisibleDevices.clear();
//			notifyDataSetChanged();
//		}
//
//		public void addBean(BluetoothDevice bean) {
//			if (null == mListVisibleDevices)
//				return;
//			mListVisibleDevices.add(bean);
//			notifyDataSetChanged();
//		}
//
//		public void remove(int index) {
//			if (mListVisibleDevices == null || (index >= mListVisibleDevices.size()) || index < 0)
//				return;
//			mListVisibleDevices.remove(index);
//			notifyDataSetChanged();
//		}
//
//		@SuppressLint("InflateParams")
//		@Override
//		public View getView(final int position, View convertView, ViewGroup parent) {
//			ViewHolder mHolder;
//			if (null == convertView) {
//				convertView = mInflater.inflate(R.layout.devices_item, null);
//				mHolder = new ViewHolder();
//				mHolder.mTextDeviceName = (TextView) convertView.findViewById(R.id.tv_devicename);
//				// mHolder.mTextStatus = (TextView)
//				// convertView.findViewById(R.id.tv_devicestatus);
//				mHolder.mLinItem = (LinearLayout) convertView.findViewById(R.id.lin_devcies_list_item);
//				mHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.pro_pairing);
//
//				convertView.setTag(mHolder);
//			} else {
//				mHolder = (ViewHolder) convertView.getTag();
//			}
//
//			BluetoothDevice bean = mListVisibleDevices.get(position);
//
//			if (bean != null) {
//				mHolder.mTextDeviceName.setText(bean.getDeviceName());
//				if (bean.getStatus() == BluetoothDevice.DEVICE_PAIRING) {
//					mHolder.mLinItem.setEnabled(false);
//					mHolder.progressBar.setVisibility(View.VISIBLE);
//					// mHolder.mTextStatus.setVisibility(View.VISIBLE);
//					// mHolder.mTextStatus.setText(getResources().getText(R.string.bluetooth_status_pairing));
//					// mHolder.mTextStatus.setTextColor(getResources().getColor(R.color.white));
//
//				} else if (bean.getStatus() == BluetoothDevice.DEVICE_UNPAIR) {
//					mHolder.mLinItem.setEnabled(true);
//					mHolder.progressBar.setVisibility(View.GONE);
//					// mHolder.mTextStatus.setVisibility(View.GONE);
//				}
//			} else {
//				mHolder.mTextDeviceName.setText("");
//			}
//
//			mHolder.mLinItem.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					isPairing = true;
//					for (BluetoothDevice mBluetoothDevice : mListVisibleDevices) {
//						if (mBluetoothDevice.getStatus() == BluetoothDevice.DEVICE_PAIRING) {
//							return;
//						}
//					}
//					BluetoothDevice bean = mListVisibleDevices.get(position);
//					bean.setStatus(BluetoothDevice.DEVICE_PAIRING);
//					notifyDataSetChanged();
//					pairRequest(bean);
//				}
//			});
//
//			return convertView;
//		}
//
//	}
//
//	private void pairRequest(BluetoothDevice bean) {
//		Intent intent = new Intent(ACTION_REMOTE_BLUETOOTHPAIR);
//		intent.putExtra("remoteAddress", bean.getAddress());
//		mContext.sendBroadcast(intent);
//
//		Message msg_pair = Message.obtain();
//		String strCod = bean.getStrCOD().substring(2);
//		msg_pair.what = MusicActionDefine.ACTION_SETTING_PAIR;
//		Bundle mBundle = new Bundle();
//		mBundle.putString("strcod", strCod);
//		mBundle.putString("address", bean.getAddress());
//		msg_pair.setData(mBundle);
//		this.notify(msg_pair, FLAG_RUN_SYNC);
//	}
//
//	private void updatePairListVisible() {
//		if (mListPairedDevices.size() > 0) {
//			mLinPair.setVisibility(View.VISIBLE);
//		} else {
//			mLinPair.setVisibility(View.GONE);
//		}
//	}
//
//	@Override
//	public void showLocalName(String name) {
//		mTextLocalName.setText(name);
//	}
//
//	@Override
//	public void showConnecttingStatus(String address) {
//		for (int i = 0; i < mListPairedDevices.size(); i++) {
//			if (address.equals(mListPairedDevices.get(i).getAddress())) {
//				BluetoothDevice device = mListPairedDevices.get(i);
//				device.setStatus(BluetoothDevice.DEVICE_CONNECTING);
//			}
//		}
//		mPairedAdapter.notifyDataSetChanged();
//	}
//
//	@Override
//	public void updateBtEnable(int status) {
//		LogUtil.i(TAG, "updateBtEnable : status = " + status);
//
//		if (status == MangerConstant.BTPOWER_STATUS_ON) {
//			mLinVis.setVisibility(View.VISIBLE);
//			mTextEnable.setVisibility(View.GONE);
//			mListVisible.setVisibility(View.VISIBLE);
//			mBtnSearch.setVisibility(View.VISIBLE);
//			Message msg_stop = Message.obtain();
//			msg_stop.what = MusicActionDefine.ACTION_SETTING_GET_PAIRED_DEVICES;
//			this.notify(msg_stop, FLAG_RUN_MAIN_THREAD);
//		} else if (status == MangerConstant.BTPOWER_STATUS_OFF) {
//			mLinVis.setVisibility(View.GONE);
//			mTextEnable.setVisibility(View.VISIBLE);
//			mListVisible.setVisibility(View.GONE);
//			mBtnSearch.setVisibility(View.INVISIBLE);
//			// getCaplayStatus();
//			mTextEnable.setText(getResources().getString(R.string.bluetooth_enable_text));
//			mListPairedDevices.clear();
//			mListVisibleDevices.clear();
//			updatePairListVisible();
//			mVisibleAdapter.notifyDataSetChanged();
//			mPairedAdapter.notifyDataSetChanged();
//		} else if (status == -2) {
//			// carlife is connected
//			mLinVis.setVisibility(View.GONE);
//			mTextEnable.setVisibility(View.VISIBLE);
//			mListVisible.setVisibility(View.GONE);
//			mBtnSearch.setVisibility(View.INVISIBLE);
//			// getCaplayStatus();
//			mTextEnable.setText(getResources().getString(R.string.music_bluetooth_carlife_connect_tip));
//			mListPairedDevices.clear();
//			mListVisibleDevices.clear();
//			updatePairListVisible();
//			mVisibleAdapter.notifyDataSetChanged();
//			mPairedAdapter.notifyDataSetChanged();
//		} else if (status == -1) {
//			mLinVis.setVisibility(View.GONE);
//			mTextEnable.setVisibility(View.VISIBLE);
//			mListVisible.setVisibility(View.GONE);
//			mBtnSearch.setVisibility(View.INVISIBLE);
//			// getCaplayStatus();
//			mTextEnable.setText(getResources().getString(R.string.bluetooth_enable_carplay_text));
//			mListPairedDevices.clear();
//			mListVisibleDevices.clear();
//			updatePairListVisible();
//			mVisibleAdapter.notifyDataSetChanged();
//			mPairedAdapter.notifyDataSetChanged();
//		}
//	}
//
//	@Override
//	public void initVisibleList(List<BluetoothDevice> list) {
//		mVisibleAdapter.removeAll();
//		LogUtil.i(TAG, "initVisibleList size = " + list.size());
//		if (list.size() == 0) {
//			searchDevices();
//		} else {
//			for (int i = 0; i < list.size(); i++) {
//				showVisibleDevices(list.get(i));
//				if (list.get(i).getStatus() == BluetoothDevice.DEVICE_PAIRING) {
//					isPairing = true;
//				}
//			}
//		}
//	}
//
//	@Override
//	public void updateTextTipShow(int conn) {
//		if (!isAdded()) {
//			LogUtil.e(TAG, "java.lang.IllegalStateException: Fragment not attached to Activity");
//			return;
//		}
//
//		if (conn == -1) {
//			mTextEnable.setText(getResources().getString(R.string.bluetooth_enable_carplay_text));
//		} else if (conn == 0) {
//			mTextEnable.setText(getResources().getString(R.string.bluetooth_enable_text));
//		} else if (conn == -2) {
//			mTextEnable.setText(getResources().getString(R.string.music_bluetooth_carlife_connect_tip));
//		}
//	}
//
//	@Override
//	public void isSearching(boolean currentInquring) {
//		updateSearchBtnShow(currentInquring);
//	}
//
//	@Override
//	public void setButtonClickable(boolean b) {
//		mBtnSearch.setEnabled(b);
//	}
//
//}

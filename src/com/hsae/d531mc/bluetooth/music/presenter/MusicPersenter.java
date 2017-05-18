package com.hsae.d531mc.bluetooth.music.presenter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Message;

import com.anwsdk.service.AudioControl;
import com.anwsdk.service.MangerConstant;
import com.hsae.autosdk.util.LogUtil;
import com.hsae.d531mc.bluetooth.music.entry.MusicBean;
import com.hsae.d531mc.bluetooth.music.model.IMusicModel;
import com.hsae.d531mc.bluetooth.music.observer.IObserver;
import com.hsae.d531mc.bluetooth.music.observer.ISubject;
import com.hsae.d531mc.bluetooth.music.util.MusicActionDefine;
import com.hsae.d531mc.bluetooth.music.view.IMusicView;

public class MusicPersenter implements IObserver {

	private static final String TAG = "MusicPersenter";
	private IMusicModel mIMusicModel;
	private IMusicView mIMusicView;

	public MusicPersenter(Context mContext, IMusicModel mIMusicModel) {
		super();
		this.mIMusicModel = mIMusicModel;
		this.mIMusicView = (IMusicView) mContext;
	}

	@Override
	public void listen(Message inMessage) {
		switch (inMessage.what) {
		case MusicActionDefine.ACTION_APP_LAUNCHED:
			init();
			break;
		case MusicActionDefine.ACTION_APP_EXIT:
			exit();
			break;
		case MusicActionDefine.ACTION_SETTING_GET_CAPLIFE_STATUS:
			boolean conn = mIMusicModel.getCarlifeConnectStatus();
			mIMusicView.updateTextTipShow(conn);
			break;
		case MusicActionDefine.ACTION_A2DP_REQUEST_AUDIO_FOCUSE:
			mIMusicModel.requestAudioFoucs();
			LogUtil.i(TAG, "requestAudioFoucs");
			break;
		case MusicActionDefine.ACTION_A2DP_CONNECT_STATUS_CHANGE:
			int conStatus = inMessage.getData().getInt("connectStatus");
			mIMusicView.updateViewByConnectStatus(conStatus);
			LogUtil.i(TAG, "updateViewByConnectStatus -- conStatus = " + conStatus);
			if (conStatus == MangerConstant.Anw_SUCCESS) {
				initMusicModel();
			}
			break;
		case MusicActionDefine.ACTION_A2DP_PLAY_PAUSE_STATUS_CHANGE:
			boolean playStatus = inMessage.getData().getBoolean("playStatus");
			mIMusicView.updatePlayBtnByStatus(playStatus);
			LogUtil.i(TAG, "updatePlayBtnByStatus -- playStatus = " + playStatus);
			break;
		case MusicActionDefine.ACTION_A2DP_ACTIVITY_FINISH:
			mIMusicView.finishMusicActivity();
			LogUtil.i(TAG, "finishMusicActivity");
			break;
		case MusicActionDefine.ACTION_A2DP_PREV:
			mIMusicModel.setAVRCPControl(AudioControl.CONTROL_BACKWARD);
			LogUtil.i(TAG, "CONTROL_BACKWARD");
			break;
		case MusicActionDefine.ACTION_A2DP_PAUSE:
			mIMusicModel.setAVRCPControl(AudioControl.CONTROL_PAUSE);
			LogUtil.i(TAG, "CONTROL_PAUSE");
			break;
		case MusicActionDefine.ACTION_A2DP_PLAY:
			mIMusicModel.setAVRCPControl(AudioControl.CONTROL_PLAY);
			LogUtil.i(TAG, "CONTROL_PLAY");
			break;
		case MusicActionDefine.ACTION_A2DP_NEXT:
			mIMusicModel.setAVRCPControl(AudioControl.CONTROL_FORWARD);
			LogUtil.i(TAG, "CONTROL_FORWARD");
			break;
		case MusicActionDefine.ACTION_A2DP_FASTFORWORD:
			mIMusicModel.setAVRCPControl(AudioControl.CONTROL_FASTFORWARD);
			LogUtil.i(TAG, "CONTROL_FASTFORWARD");
			break;
		case MusicActionDefine.ACTION_A2DP_REWIND:
			mIMusicModel.setAVRCPControl(AudioControl.CONTROL_REWIND);
			LogUtil.i(TAG, "CONTROL_REWIND");
			break;
		case MusicActionDefine.ACTION_A2DP_SUPPORT_MATE_DATA_STATUS_CHANGE:
			boolean isSupport = mIMusicModel.A2DPSupportMetadata();
			MusicBean bean = (MusicBean) inMessage.getData().getSerializable(
					"musicBean");
			LogUtil.i(TAG, " name  = " + bean.getTitle() + " -- isSupport = " + isSupport);
			mIMusicView.updateMusicDataInfo(bean, isSupport);
			break;
		case MusicActionDefine.ACTION_A2DP_CURRENT_MUSIC_POSITION_CHANGE:
			String currentTime = inMessage.getData().getString("currentTime");
			boolean isPlaying = inMessage.getData().getBoolean("playStatus");
			LogUtil.i(TAG, " currentTime  = " + currentTime + " -- isPlaying = " + isPlaying);
			mIMusicView.updateMusicPlayCurrentTime(currentTime, isPlaying);
			break;
		case MusicActionDefine.ACTION_A2DP_REPEAT_ATTRIBUTE:
			ArrayList<Integer> allowRepeatList = inMessage.getData()
					.getIntegerArrayList("repeatList");
			mIMusicView.updateRepeatAllowList(allowRepeatList);
			break;
		case MusicActionDefine.ACTION_A2DP_SHUFFLE_ATTRIBUTE:
			ArrayList<Integer> allowShuffleList = inMessage.getData()
					.getIntegerArrayList("shuffleList");
			mIMusicView.updateShuffleAllowList(allowShuffleList);
			break;
		case MusicActionDefine.ACTION_A2DP_PLAYERSETTING_CHANGED_EVENT:
			int nAttrID = inMessage.getData().getInt("nAttrID");
			int nAttrValue = inMessage.getData().getInt("nAttrValue");
			mIMusicView.UpdatePlayerModeSetting(nAttrID, nAttrValue);
			break;
		case MusicActionDefine.ACTION_A2DP_REPEAT_MODEL:
			int nCurrentMode = inMessage.getData().getInt("currentRepeatModel");
			ArrayList<Integer> allowlist = inMessage.getData()
					.getIntegerArrayList("repeatList");
			setPlayModel(AudioControl.PLAYER_ATTRIBUTE_REPEAT, allowlist,
					nCurrentMode);
			break;
		case MusicActionDefine.ACTION_A2DP_SHUFFLE_MODEL:
			int sCurrentMode = inMessage.getData()
					.getInt("currentShuffleModel");
			ArrayList<Integer> sallowlist = inMessage.getData()
					.getIntegerArrayList("shuffleList");
			setPlayModel(AudioControl.PLAYER_ATTRIBUTE_SHUFFLE, sallowlist,
					sCurrentMode);
			break;
		case MusicActionDefine.ACTION_A2DP_ACTIVITY_PAUSE :
			mIMusicModel.sendActivityPauseMsg();
			break;
		case MusicActionDefine.ACTION_SETTING_UPDATE_BG :
			initBg();
			break;
			
		default:
			break;
		}
	}

	private void init() {
		((ISubject) mIMusicModel).attach(this);
		((ISubject) mIMusicView).attach(this);
		int status = mIMusicModel.getA2DPConnectStatus();
		mIMusicView.updateViewByConnectStatus(status);
		initBg();
		LogUtil.i(TAG, " --- init +++ ");
		if (status == MangerConstant.Anw_SUCCESS) {
			mIMusicModel.playStatus();
			boolean isSupport = mIMusicModel.A2DPSupportMetadata();
			String title = mIMusicModel
					.getCurrentDataAttributes(AudioControl.MEDIA_ATTR_MEDIA_TITLE);
			String atrist = mIMusicModel
					.getCurrentDataAttributes(AudioControl.MEDIA_ATTR_ARTIST_NAME);
			String album = mIMusicModel
					.getCurrentDataAttributes(AudioControl.MEDIA_ATTR_ALBUM_NAME);
			String totalTime = mIMusicModel
					.getCurrentDataAttributes(AudioControl.MEDIA_ATTR_PLAYING_TIME_IN_MS);
			MusicBean bean = new MusicBean(title, atrist, album, totalTime);
			mIMusicView.updateMusicDataInfo(bean, isSupport);
		}
		initMusicModel();
	}
	
	private void initBg(){
		Bitmap bg = mIMusicModel.getBg();
		LogUtil.i(TAG, " --- initBg --- bg = " + bg);
		mIMusicView.updateBgBitmap(bg);
	}

	private void exit() {
		mIMusicModel.sendActivityPauseMsg();
		mIMusicModel.releaseModel();
		((ISubject) mIMusicModel).detach(this);
		((ISubject) mIMusicModel).detach(this);
		LogUtil.i(TAG, " --- exit +++ ");
	}

	/**
	 * 初始化音乐模式
	 */
	private void initMusicModel() {
		int[] AllowArray = new int[10];
		int repeatNum = mIMusicModel.retrieveCurrentPlayerAPSupported(
				AudioControl.PLAYER_ATTRIBUTE_REPEAT, AllowArray, 10);
		mIMusicView.updateRepeatAllowArray(AllowArray, repeatNum);
		int shuffleNum = mIMusicModel.retrieveCurrentPlayerAPSupported(
				AudioControl.PLAYER_ATTRIBUTE_SHUFFLE, AllowArray, 10);
		mIMusicView.updateShuffleAllowArray(AllowArray, shuffleNum);

		mIMusicView
				.UpdatePlayerModeSetting(
						AudioControl.PLAYER_ATTRIBUTE_REPEAT,
						mIMusicModel
								.retrieveCurrentPlayerAPSetting(AudioControl.PLAYER_ATTRIBUTE_REPEAT));
		mIMusicView
				.UpdatePlayerModeSetting(
						AudioControl.PLAYER_ATTRIBUTE_SHUFFLE,
						mIMusicModel
								.retrieveCurrentPlayerAPSetting(AudioControl.PLAYER_ATTRIBUTE_SHUFFLE));
	}

	/**
	 * 切换音乐模式
	 * @param nAttriID
	 * @param AllowedList
	 * @param nCurrentMode
	 */
	private void setPlayModel(int nAttriID, ArrayList<Integer> AllowedList,
			int nCurrentMode) {
		int nSupportSize = 0;
		if (AllowedList != null) {
			nSupportSize = AllowedList.size();
			if (nSupportSize > 0) {
				int i = 0;
				int nValue = 0;
				int nNextMode = -1;
				for (i = 0; i < nSupportSize; i++) {
					nValue = AllowedList.get(i);
					if (nValue == nCurrentMode) {
						int j = i + 1;
						if (j >= nSupportSize)
							j = 0;
						nNextMode = AllowedList.get(j);
						break;
					}
				}
				if (nNextMode >= 0) {
					mIMusicModel
							.setCurrentPlayerAPSettings(nAttriID, nNextMode);
				}
			}
		}
	}

}

package com.hsae.d531mc.bluetooth.music.presenter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Message;
import android.util.Log;

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
		case MusicActionDefine.ACTION_APP_ONINTENT:
			mIMusicModel.setHandPause(false);
			break;
		case MusicActionDefine.ACTION_USB_DISCONNECT:
			mIMusicView.onUsbDesconnet();
			break;
		case MusicActionDefine.ACTION_BLUETOOTH_ENABLE_STATUS_CHANGE:
			
			int enableStatus = inMessage.getData().getInt("enableStatus");
			mIMusicView.updateViewByConnectStatus(enableStatus);
			break;
		case MusicActionDefine.ACTION_APP_EXIT:
			exit();
			break;
		case MusicActionDefine.ACTION_SETTING_GET_CARLIFE_STATUS:
			boolean conn = mIMusicModel.getCarlifeConnectStatus();
			LogUtil.i(TAG, "getCarlifeConnectStatus" + conn);
			mIMusicView.updateTextTipShow(conn);
			mIMusicView.updateViewByConnectStatus(conn?-1:0);
			break;
		case MusicActionDefine.ACTION_A2DP_REQUEST_AUDIO_FOCUSE:
			LogUtil.i(TAG, "requestAudioFoucs");
			mIMusicModel.requestAudioFoucs();
			break;
		case MusicActionDefine.ACTION_A2DP_CONNECT_STATUS_CHANGE:
			int conStatus = inMessage.getData().getInt("connectStatus");
			mIMusicView.updateViewByConnectStatus(conStatus);
			LogUtil.i(TAG, "updateViewByConnectStatus -- conStatus = " + conStatus);
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
			mIMusicModel.removeAutoPlay();
			LogUtil.i(TAG, "CONTROL_PAUSE");
			break;
		case MusicActionDefine.ACTION_A2DP_PLAY:
			mIMusicModel.requestAudioFoucs(true,true);
			LogUtil.i(TAG, "CONTROL_PLAY");
			break;
		case MusicActionDefine.ACTION_A2DP_NEXT:
			mIMusicModel.setAVRCPControl(AudioControl.CONTROL_FORWARD);
			LogUtil.i(TAG, "CONTROL_FORWARD");
			break;
		case MusicActionDefine.ACTION_A2DP_FASTFORWORD:
			mIMusicModel.setAVRCPControl(AudioControl.CONTROL_FASTFORWARD, 0);
			LogUtil.i(TAG, "CONTROL_FASTFORWARD");
			break;
		case MusicActionDefine.ACTION_A2DP_REWIND:
			mIMusicModel.setAVRCPControl(AudioControl.CONTROL_REWIND, 0);
			LogUtil.i(TAG, "CONTROL_REWIND");
			break;
		case MusicActionDefine.ACTION_A2DP_FASTFORWORD_CANCEL:
			mIMusicModel.setAVRCPControl(AudioControl.CONTROL_FASTFORWARD, 1);
			LogUtil.i(TAG, "CONTROL_FASTFORWARD");
			break;
		case MusicActionDefine.ACTION_A2DP_REWIND_CANCEL:
			mIMusicModel.setAVRCPControl(AudioControl.CONTROL_REWIND, 1);
			LogUtil.i(TAG, "CONTROL_REWIND");
			break;
		case MusicActionDefine.ACTION_A2DP_SUPPORT_MATE_DATA_STATUS_CHANGE:
			boolean isSupport = mIMusicModel.A2DPSupportMetadata();
			MusicBean bean = (MusicBean) inMessage.getData().getSerializable("musicBean");
			mIMusicView.updateMusicDataInfo(bean, isSupport);
			break;
		case MusicActionDefine.ACTION_A2DP_CURRENT_MUSIC_POSITION_CHANGE:
			String currentTime = inMessage.getData().getString("currentTime");
			boolean isPlaying = inMessage.getData().getBoolean("playStatus");
			LogUtil.i(TAG, " currentTime  = " + currentTime + " -- isPlaying = " + isPlaying);
			mIMusicView.updateMusicPlayCurrentTime(currentTime, isPlaying);
			break;

		case MusicActionDefine.ACTION_A2DP_ACTIVITY_PAUSE:
			mIMusicModel.sendActivityPauseMsg();
			break;
		case MusicActionDefine.ACTION_SETTING_UPDATE_BG:
			initBg();
			break;
		case MusicActionDefine.ACTIVITY_RESUME:
			LogUtil.i(TAG, "ACTIVITY_RESUME");
			mIMusicModel.activityResume();
			break;
			
		case MusicActionDefine.ACTIVITY_START:
			LogUtil.i(TAG, "ACTIVITY_START");
			mIMusicModel.activityStart();
			break;
			
		case MusicActionDefine.ACTIVITY_STOP:
			LogUtil.i(TAG, "ACTIVITY_STOP");
			mIMusicModel.activityStop();
			break;

		default:
			break;
		}
	}

	private void init() {
		((ISubject) mIMusicModel).attach(this);
		((ISubject) mIMusicView).attach(this);
		int btPowerStatus = mIMusicModel.getBTPowerStatus();
		int status = mIMusicModel.getA2DPConnectStatus();
		//boolean isCarlifeConnected = mIMusicModel.getCarlifeConnectStatus(); 
		
		if(btPowerStatus != MangerConstant.BTPOWER_STATUS_ON){
			status = -2;
		}
		
		/*if (isCarlifeConnected) {
			status = -1;
		}*/
		LogUtil.i(TAG, " --- init +++ status = " + status);
		LogUtil.i(TAG, " --- init +++ btPowerStatus = " + btPowerStatus);
		
		LogUtil.i(TAG, " --- init +++ ");
		if (status == MangerConstant.Anw_SUCCESS) {
			mIMusicModel.getMusicMatedata();
			boolean isSupport = mIMusicModel.A2DPSupportMetadata();
			String title = mIMusicModel.getCurrentDataAttributes(AudioControl.MEDIA_ATTR_MEDIA_TITLE);
			String atrist = mIMusicModel.getCurrentDataAttributes(AudioControl.MEDIA_ATTR_ARTIST_NAME);
			String album = mIMusicModel.getCurrentDataAttributes(AudioControl.MEDIA_ATTR_ALBUM_NAME);
			String totalTime = mIMusicModel.getCurrentDataAttributes(AudioControl.MEDIA_ATTR_PLAYING_TIME_IN_MS);
			MusicBean bean = new MusicBean(title, atrist, album, totalTime);
			mIMusicView.updateMusicDataInfo(bean, isSupport);
			boolean isPlay = mIMusicModel.initPlayStatus();
			mIMusicView.updatePlayBtnByStatus(isPlay);
		}
		
		mIMusicView.updateViewByConnectStatus(status);
		initBg();
		
	}

	private void initBg() {
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
	
	boolean playStatus = false;
	/***
	 * 获取当前实时的播放状态
	 * @return
	 */
	public boolean getPlayStatus() {
		LogUtil.i(TAG, "getPlayStatus : playStatus = " + playStatus);
		return playStatus;
	}
}

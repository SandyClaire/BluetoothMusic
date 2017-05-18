package com.hsae.d531mc.bluetooth.music.presenter;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.anwsdk.service.AudioControl;
import com.hsae.d531mc.bluetooth.music.entry.MusicBean;
import com.hsae.d531mc.bluetooth.music.model.IMusicModel;
import com.hsae.d531mc.bluetooth.music.observer.IObserver;
import com.hsae.d531mc.bluetooth.music.observer.ISubject;
import com.hsae.d531mc.bluetooth.music.util.MusicActionDefine;
import com.hsae.d531mc.bluetooth.music.view.IMusicView;

public class MusicPersenter implements IObserver {

	private Context mContext;
	private IMusicModel mIMusicModel;
	private IMusicView mIMusicView;
	
	public MusicPersenter(Context mContext, IMusicModel mIMusicModel) {
		super();
		this.mContext = mContext;
		this.mIMusicModel = mIMusicModel;
		this.mIMusicView = (IMusicView)mContext;
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
		case MusicActionDefine.ACTION_A2DP_CONNECT_STATUS_CHANGE:
			int conStatus = inMessage.getData().getInt("connectStatus");
//			if (conStatus == 1) {
//				mIMusicModel.playStatus();
//			}
			mIMusicView.updateViewByConnectStatus(conStatus);
			break;
		case MusicActionDefine.ACTION_A2DP_PLAY_PAUSE_STATUS_CHANGE:
			boolean playStatus = inMessage.getData().getBoolean("playStatus");
			mIMusicView.updatePlayBtnByStatus(playStatus);
			break;
		case MusicActionDefine.ACTION_A2DP_PREV:
			mIMusicModel.setAVRCPControl(AudioControl.CONTROL_BACKWARD);
			break;
		case MusicActionDefine.ACTION_A2DP_PAUSE:
			mIMusicModel.setAVRCPControl(AudioControl.CONTROL_PAUSE);
			break;
		case MusicActionDefine.ACTION_A2DP_PLAY:
			mIMusicModel.setAVRCPControl(AudioControl.CONTROL_PLAY);
			break;
		case MusicActionDefine.ACTION_A2DP_NEXT:
			mIMusicModel.setAVRCPControl(AudioControl.CONTROL_FORWARD);
			break;
		case MusicActionDefine.ACTION_A2DP_FASTFORWORD:
			mIMusicModel.setAVRCPControl(AudioControl.CONTROL_FASTFORWARD);
			break;
		case MusicActionDefine.ACTION_A2DP_REWIND:
			mIMusicModel.setAVRCPControl(AudioControl.CONTROL_REWIND);
			break;
		case MusicActionDefine.ACTION_A2DP_SUPPORT_MATE_DATA_STATUS_CHANGE:
			boolean isSupport = mIMusicModel.A2DPSupportMetadata();
			MusicBean bean = (MusicBean) inMessage.getData().getSerializable("musicBean");
			mIMusicView.updateMusicDataInfo(bean , isSupport);
			break;
		case MusicActionDefine.ACTION_A2DP_CURRENT_MUSIC_POSITION_CHANGE:
			String currentTime = inMessage.getData().getString("currentTime");
			boolean isPlaying = inMessage.getData().getBoolean("playStatus");
			mIMusicView.updateMusicPlayCurrentTime(currentTime,isPlaying);
			break;
		case MusicActionDefine.ACTION_A2DP_REPEAT_ALL:
			mIMusicModel.setCurrentPlayerAPSettings(AudioControl.PLAYER_ATTRIBUTE_REPEAT, 4);
			break;
		case MusicActionDefine.ACTION_A2DP_REPEAT_ORDER:
			mIMusicModel.setCurrentPlayerAPSettings(AudioControl.PLAYER_ATTRIBUTE_REPEAT, 3);
			break;
		case MusicActionDefine.ACTION_A2DP_REPEAT_SINGLE:
			mIMusicModel.setCurrentPlayerAPSettings(AudioControl.PLAYER_ATTRIBUTE_REPEAT, 2);
			break;
		case MusicActionDefine.ACTION_A2DP_SHUFFLE_OPEN:
			mIMusicModel.setCurrentPlayerAPSettings(AudioControl.PLAYER_ATTRIBUTE_SHUFFLE, 1);
			break;
		case MusicActionDefine.ACTION_A2DP_SHUFFLE_CLOSE:
			mIMusicModel.setCurrentPlayerAPSettings(AudioControl.PLAYER_ATTRIBUTE_SHUFFLE, 2);
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
		if (status == 1) {
			int playStatus = mIMusicModel.playStatus();
			Log.e("wangda", "init ---- playStatus = " + playStatus);
			boolean isSupport = mIMusicModel.A2DPSupportMetadata();
			String title = mIMusicModel.getCurrentDataAttributes(AudioControl.MEDIA_ATTR_MEDIA_TITLE);
			String atrist = mIMusicModel.getCurrentDataAttributes(AudioControl.MEDIA_ATTR_ARTIST_NAME);
			String album = mIMusicModel.getCurrentDataAttributes(AudioControl.MEDIA_ATTR_ALBUM_NAME);
			String totalTime = mIMusicModel.getCurrentDataAttributes(AudioControl.MEDIA_ATTR_PLAYING_TIME_IN_MS);
			MusicBean bean = new MusicBean(title, atrist, album, totalTime);
			mIMusicView.updateMusicDataInfo(bean , isSupport);
//			mIMusicView.updatePlayBtnByStatus(playStatus);
		}
	}

	private void exit() {
		mIMusicModel.releaseModel();
		((ISubject) mIMusicModel).detach(this);
		((ISubject) mIMusicModel).detach(this);
	}

}

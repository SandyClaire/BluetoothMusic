package com.hsae.d531mc.bluetooth.music.entry;

import java.io.Serializable;

/**
 * 
 * @author wangda
 *
 */
public class MusicBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//歌曲名称
	private String title;
	//歌手姓名
	private String atrist;
	private String album;
	//歌曲总时间
	private String totalTime;
	
	private int playStatus = 0;
	
	private boolean isAudioFocus = true;

	public boolean isAudioFocus() {
		return isAudioFocus;
	}

	public void setAudioFocus(boolean isAudioFocus) {
		this.isAudioFocus = isAudioFocus;
	}

	public int getPlayStatus() {
		return playStatus;
	}

	public void setPlayStatus(int playStatus) {
		this.playStatus = playStatus;
	}

	public MusicBean() {
	}

	public MusicBean(String title, String atrist, String album, String totalTime,int playStatus,boolean isAudioFocus) {
		super();
		this.title = title;
		this.atrist = atrist;
		this.album = album;
		this.totalTime = totalTime;
		this.playStatus = playStatus;
		this.isAudioFocus = isAudioFocus;
	}
	
	public MusicBean(String title, String atrist, String album, String totalTime) {
		super();
		this.title = title;
		this.atrist = atrist;
		this.album = album;
		this.totalTime = totalTime;
	}
	
	
	

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAtrist() {
		return atrist;
	}

	public void setAtrist(String atrist) {
		this.atrist = atrist;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(String totalTime) {
		this.totalTime = totalTime;
	}

}

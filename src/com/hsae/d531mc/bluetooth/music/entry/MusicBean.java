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
	private String title;
	private String atrist;
	private String album;
	private String totalTime;

	public MusicBean() {
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

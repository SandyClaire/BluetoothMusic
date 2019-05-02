package com.hsae.d531mc.bluetooth.music.service;

public interface BluetoothAllCallback {

	public void onPlayStatusChanged(int state);

	public void onPositionChanged(String position);

	public void onID3Changed(String title, String album, String artist,
			String totalTime);

	public void onConnectStateChanged(int profile, int state, int reason);

	public void onPowerStateChanged(int state);

}

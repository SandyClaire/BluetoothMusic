package com.hsae.d531mc.bluetooth.music.service;

import com.anwsdk.service.AudioControl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;

/**
 * @category 接收关机广播，以暂停手机端播放的音乐 
 * @author zhaoxing
 */
public class ShutdownBroadcastReceiver extends BroadcastReceiver{
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		if(intent.getAction().equals(Intent.ACTION_SHUTDOWN)){
			
			Log.i("BluetoothMusicModel", "reciver SHUTDOWN broadcast");
			try {
				new BluetoothMusicModel().AVRCPControl(AudioControl.CONTROL_PAUSE);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

}

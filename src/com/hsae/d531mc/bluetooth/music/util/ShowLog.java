package com.hsae.d531mc.bluetooth.music.util;

import android.util.Log;

/**
 * log tools
 * 
 * @author wangda
 *
 */
public class ShowLog {

	private final static boolean debug = true;

	public void showElog(String tag, String msg) {
		if (debug) {
			Log.e(tag, msg);
		}
	}

	public void showVlog(String tag, String msg) {
		if (debug) {
			Log.v(tag, msg);
		}
	}

	public void showIlog(String tag, String msg) {
		if (debug) {
			Log.i(tag, msg);
		}
	}
}

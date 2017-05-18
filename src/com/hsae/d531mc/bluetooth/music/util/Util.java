package com.hsae.d531mc.bluetooth.music.util;

import android.net.Uri;

public class Util {
	
	public final static String VALUE = "value";
	public static final String AUTHORITY = "com.hsae.d531mc.systemsetting";
	public static final Uri WALL_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/haseSettingsTableWallPaper");
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/haseSettingsTable");
	public static final String METHOD_PUT_VALUE_WALL = "putValueWall";
	public static final String WALLPAPER_SET = "wallpaperSet";
	public static final String METHOD_PUT_VALUE = "putValue";
	public static final String METHOD_GET_VALUE_WALL = "getValueWall";

}

package com.friendlylimo;

import android.content.Context;

import com.network.InternetDetector;

public final class Constants {

	// fields
	public static final float ZOOM_LEVEL = 14;

	public static final String OK = "OK";
	public static final String CANCEL = "Cancel";
	public static final String PING_METHOD_POST = "POST";
	public static final String PING_METHOD_GET = "GET";

	// methods
	public static final boolean detectInternet(Context context) {
		InternetDetector detectInt = new InternetDetector(context);
		if (detectInt.detectInternet())
			return true;
		return false;
	}
}

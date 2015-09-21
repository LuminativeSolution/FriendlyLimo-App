package com.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InternetDetector {

	private Context context;

	public InternetDetector(Context context) {
		this.context = context;
	}

	public boolean detectInternet() {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] info = cm.getAllNetworkInfo();
		if (info != null)
			for (int i = 0; i < info.length; ++i)
				if (info[i].getState() == NetworkInfo.State.CONNECTED)
					return true;
		return false;
	}
}

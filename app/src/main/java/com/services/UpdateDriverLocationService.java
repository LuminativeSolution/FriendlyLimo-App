package com.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UpdateDriverLocationService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		Log.d("binded", "binded");
		return null;
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.d("started", "started");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("start_command", "start_command");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("destroyed", "destroyed");
	}
}

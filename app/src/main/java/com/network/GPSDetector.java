package com.network;
//package com.friendlylimo.network;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.location.LocationManager;
//import android.preference.PreferenceManager.OnActivityResultListener;
//
//import com.friendlylimo.PersonType;
//import com.friendlylimo.driver.home.DriverHome;
//
//public class GPSDetector {
//
//	private static final int REQUEST_CODE = 1;
//
//	private Activity activity;
//
//	public GPSDetector(Activity activity) {
//		this.activity = activity;
//	}
//
//	public void detectGPS() {
//		LocationManager locationManager = (LocationManager) activity
//				.getSystemService(Context.LOCATION_SERVICE);
//		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
//			activity.startActivity(new Intent(activity, DriverHome.class));
//		else
//			enableGPSAlert();
//	}
//
//	// enableGPSAlert()
//	private void enableGPSAlert() {
//		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//		builder.setTitle("Enable GPS");
//		builder.setMessage("Please enable GPS to proceed");
//		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				Intent callGPSSettingIntent = new Intent(
//						android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//				activity.startActivityForResult(callGPSSettingIntent,
//						REQUEST_CODE);
//			}
//		});
//		builder.setNegativeButton("Cancel",
//				new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						activity.startActivity(new Intent(activity,
//								PersonType.class));
//					}
//				});
//	}
//
//	OnActivityResultListener listener = new OnActivityResultListener() {
//
//		@Override
//		public boolean onActivityResult(int requestCode, int resultCode,
//				Intent data) {
//			activity.startActivity(new Intent(activity, DriverHome.class));
//			return false;
//		}
//	};
//}

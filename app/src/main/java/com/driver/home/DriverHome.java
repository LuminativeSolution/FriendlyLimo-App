package com.driver.home;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;

import com.GPS.GPSTracker;
import com.friendlylimo.util.json.JSONParser;
import com.driver.accounts.DriverLogin;
import com.friendlylimo.Constants;
import com.friendlylimo.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DriverHome extends FragmentActivity {

	private static final String URL = "http://tabi1.couthymedia.com/Wajahat/update_driver_location.php";
	private static final String KEY_ID = "id";
	private static final String KEY_LATITUDE = "latitude";
	private static final String KEY_LONGITUDE = "longitude";
	private static final long UPDATE_LOCATION_INTERVAL = 5 * 1000;

	double lat = 0;
	double lng = 0;

	private JSONParser jParser;
	private GoogleMap googleMap;
	private GPSTracker gpsTracker;
	private SupportMapFragment fm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.driver_home);
		if (isGooglePlayServiceAvailable())
			proceedWithGooglePlayServicesAvailable();
	}

	// isGooglePlayServiceAvailable()
	private boolean isGooglePlayServiceAvailable() {
		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getBaseContext());

		// Showing status
		if (status != ConnectionResult.SUCCESS) { // Google Play Services are
													// not available
			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
					requestCode);
			dialog.show();

			return false;
		}

		return true;
	}

	// proceedWithGooglePlayServicesActive()
	private void proceedWithGooglePlayServicesAvailable() {
		init();
		googleMap.setMyLocationEnabled(true);
		drawMarker(new LatLng(lat, lng));
		setCamera();
		updateLocation();
	}

	// init()
	private void init() {
		fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(
				R.id.map);
		googleMap = fm.getMap();
		gpsTracker = new GPSTracker(this);

		lat = gpsTracker.getLatitude();
		lng = gpsTracker.getLongitude();

		jParser = new JSONParser();
	}

	// setCamera()
	private void setCamera() {
		googleMap.moveCamera(CameraUpdateFactory
				.newLatLng(new LatLng(lat, lng)));
		googleMap.animateCamera(CameraUpdateFactory
				.zoomTo(Constants.ZOOM_LEVEL));
	}

	private void drawMarker(LatLng point) {
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(point);
		googleMap.addMarker(markerOptions);
	}

	/** UPDATING DRIVER LOCATION TO THE SERVER STARTS */

	private void updateLocation() {
		final Handler handler = new Handler();
		Runnable run = new Runnable() {

			@Override
			public void run() {
				try {
					new LoadUpdateLocation().execute();
					handler.postDelayed(this, UPDATE_LOCATION_INTERVAL);
				} catch (Exception e) {

				} finally {
					handler.postDelayed(this, UPDATE_LOCATION_INTERVAL);
				}
			}
		};

		handler.postDelayed(run, UPDATE_LOCATION_INTERVAL);
	}

	// background to update driver's location
	private class LoadUpdateLocation extends AsyncTask<Void, String, Void> {

		@Override
		protected Void doInBackground(Void... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(KEY_ID, DriverLogin.driverId));
			params.add(new BasicNameValuePair(KEY_LATITUDE, String.valueOf(lat)));
			params.add(new BasicNameValuePair(KEY_LONGITUDE, String
					.valueOf(lng)));
			jParser.makeHttpRequest(URL, "POST", params);

			return null;
		}
	}

	/** UPDATING DRIVER LOCATION TO THE SERVER ENDS */
}
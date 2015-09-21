package com.GPS;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GPSTracker implements LocationListener {

	private Context context;

	boolean canGetLocation;
	boolean isGPSEnabled;
	boolean isNetworkEnabled;

	private static final int MIN_DISTANCE_UPDATE = 1;
	private static final int MIN_TIME_UPDATE = 1;

	double latitude;
	double longitude;

	Location location;
	LocationManager locManager;

	Geocoder geocoder;
	List<Address> addresses;

	public String getFormattedAddressFromLatLng( Context context, double latitude, double longitude){

		geocoder = new Geocoder(context, Locale.getDefault());

		try {
			addresses = geocoder.getFromLocation(latitude, longitude, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String address = addresses.get(0).getAddressLine(0);
		String city = addresses.get(0).getLocality();
		String state = addresses.get(0).getAdminArea();
		String country = addresses.get(0).getCountryName();

		String formattedAddress = address+" "+city+" "+state+" "+country;
		return formattedAddress;
	}

	public GPSTracker(Context context) {
		this.context = context;
		getLocation();
	}

	public Location getLocation() {

		Location locationNetwork = null;

		locManager = (LocationManager) this.context
				.getSystemService(Context.LOCATION_SERVICE);
		isGPSEnabled = locManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		isNetworkEnabled = locManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		Log.d("GPSTracker_network_status", String.valueOf(isNetworkEnabled));
		if (!isNetworkEnabled && !isGPSEnabled) {

		} else {
			this.canGetLocation = true;
			if (isNetworkEnabled) {
				locManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, MIN_TIME_UPDATE,
						MIN_DISTANCE_UPDATE, this);
				Log.d("GPSTracker_Location_Manager", String.valueOf(locManager));
				if (locManager != null) {
					Log.d("GPSTracker_entered_if", String.valueOf(locManager));
					location = locManager
							.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					locationNetwork = location;
					Log.d("GPSTracker_entered_if_location",
							String.valueOf(locManager
									.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)));
					if (location != null) {
						Log.d("location_inner", String.valueOf(location));
						latitude = location.getLatitude();
						longitude = location.getLongitude();
					}
				}
			}
			if (isGPSEnabled) {
				locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
						MIN_TIME_UPDATE, MIN_DISTANCE_UPDATE, this);
				if (locManager != null) {
					location = locManager
							.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					Log.d("location_GPS_second_if", String.valueOf(location));
					if (location != null) {
						latitude = location.getLatitude();
						longitude = location.getLongitude();
					}
				}
			}
		}

		Log.d("location_return", String.valueOf(location));
		return locationNetwork != null ? locationNetwork : location;
	}

	public boolean canGetLocation() {
		return this.canGetLocation;
	}

	public double getLatitude() {
		Log.d("GPSTracker_location", String.valueOf(location));
		if (location != null)
			latitude = location.getLatitude();
		return latitude;
	}

	public double getLongitude() {
		if (location != null)
			longitude = location.getLongitude();
		return longitude;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}



}

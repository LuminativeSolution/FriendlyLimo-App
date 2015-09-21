package com.client.home;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.GPS.GPSTracker;
import com.friendlylimo.util.json.JSONParser;
import com.client.accounts.TaxiRequest;
import com.friendlylimo.Constants;
import com.friendlylimo.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.models.Driver;

public class ClientHome extends FragmentActivity {

	private static final String URL = "http://tabi1.couthymedia.com/Wajahat/get_all_drivers.php";
	private static final String KEY_DRIVER_ID = "driver_id";
	private static final String KEY_LATITUDE = "latitude";
	private static final String KEY_LONGITUDE = "longitude";
	private static final String KEY_JSON_DRIVERS_ARRAY = "drivers";

	private String selectedDriverId = "";
	private static int numDrivers = 0;
	private double userLat = 0, userLng = 0;

	// both of their lengths will be same
	private ArrayList<String> aListServerIds, aListMarkerIds;
	private ArrayList<Driver> aListDrivers;

	private Context context;

	private JSONParser jParser;
	private HashMap<Marker, Driver> mapDrivers;
	private GoogleMap googleMap;

	// temp
	private RelativeLayout rlWrapper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client_home);
		// temp
		rlWrapper = (RelativeLayout) findViewById(R.id.rlWrapper);
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
		// index '-1' sent as an arbitrary value because this index has to have
		// no deal with the user, that's why -1 is sent as an argument because
		// there is a check in the setMarker(which is called by the drawMarker)
		// that if the driverIndex is '-1' then, do nothing.
		drawMarker(new LatLng(userLat, userLng), -1);
		setCamera();

		new LoadDrivers().execute();
	}

	// init()
	private void init() {
		SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		googleMap = fm.getMap();
		mapDrivers = new HashMap<Marker, Driver>();

		context = ClientHome.this;

		GPSTracker gpsTracker = new GPSTracker(context);

		userLat = gpsTracker.getLatitude();
		userLng = gpsTracker.getLongitude();

		aListServerIds = new ArrayList<String>();
		aListMarkerIds = new ArrayList<String>();
		aListDrivers = new ArrayList<Driver>();
	}

	// setCamera()
	private void setCamera() {
		googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(userLat,
				userLng)));
		googleMap.animateCamera(CameraUpdateFactory
				.zoomTo(Constants.ZOOM_LEVEL));
	}

	private void drawMarker(LatLng point, int driverIndex) {
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(point);

		googleMap.addMarker(markerOptions);

		setMarkers(point.latitude, point.longitude, driverIndex);
	}

	private void setMarkers(double currentuserLat, double currentLong,
			int driverIndex) {
		Driver driver = new Driver();

		Marker marker = allocateMarker(driver, currentuserLat, currentLong);

		// first marker will be added for the user and his icon will be default
		// marker. Therefore when the drivers are greater than 0 than the
		// following taxi marker will be added
		if (numDrivers > 0) {
			marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
					.decodeResource(getResources(), R.drawable.map_taxi)));
			aListMarkerIds.add(marker.getId());
		}

		mapDrivers.put(marker, driver);

		googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				int selectedDriverIndex = 0;
				try {
					// markers and drivers list must be of same size
					for (int i = 0; i < aListMarkerIds.size(); ++i) {
						if (aListMarkerIds.get(i).equals(marker.getId())) {
							selectedDriverIndex = i;
							break;
						}
					}

					try {
						selectedDriverId = aListServerIds
								.get(selectedDriverIndex);
						new LoadDriverInfo().execute();
					} catch (Exception ex) {
						Log.e("excep_selected_driver_id", ex.toString());
					}
				} catch (Exception e) {
					Log.e("excep_find_drivers", e.toString());
				}

				return false;
			}
		});
	}

	private Marker allocateMarker(Driver driver, double currentLat,
			double currentLong) {
		return googleMap.addMarker(new MarkerOptions().position(new LatLng(
				currentLat, currentLong)));
	}

	/** GET DISTANCE BETWEEN CURRENT AND TAPPED LOCATION STARTS */

	/** GET DISTANCE BETWEEN CURRENT AND TAPPED LOCATION ENDS */

	@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
	}

	/** SHOW DRIVERS ON THE MAP STARTS */

	private class LoadDrivers extends
			AsyncTask<Void, String, ArrayList<Driver>> {

		ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(ClientHome.this);
			pd.show();
		}

		@Override
		protected ArrayList<Driver> doInBackground(Void... args) {
			jParser = new JSONParser();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			JSONObject jObj = jParser.makeHttpRequest(URL,
					Constants.PING_METHOD_GET, params);

			try {
				JSONArray jArray = jObj.getJSONArray(KEY_JSON_DRIVERS_ARRAY);
				if (jArray.length() > 0) {
					for (int i = 0; i < jArray.length(); ++i) {
						JSONObject jObjDriver = jArray.getJSONObject(i);
						Driver driver = new Driver();
						double lat, lng;
						try {
							String sLat = jObjDriver.getString(KEY_LATITUDE);
							lat = Double.parseDouble(sLat);
							String sLng = jObjDriver.getString(KEY_LONGITUDE);
							lng = Double.parseDouble(sLng);
						} catch (Exception e) {

							lat = 0f;
							lng = 0f;

							Log.e("excep_latlng", "excep_latln " + e.toString());
						}

						aListServerIds.add(jObjDriver.getString(KEY_DRIVER_ID));
						driver.setLat(lat);
						driver.setLng(lng);

						aListDrivers.add(driver);
					}

				} else {
					Log.e("no_json_obj", "no_json_obj");
				}

				return aListDrivers;
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<Driver> result) {
			pd.dismiss();
			try {
				if (result.size() != 0) {
					numDrivers = result.size();
					for (int i = 0; i < result.size(); ++i) {
						drawMarker(new LatLng(result.get(i).getLat(), result
								.get(i).getLng()), i);
					}
				}
			} catch (Exception e) {
				Log.e("result_zero", "result zero: " + e.toString());
				Toast.makeText(ClientHome.this, "No drivers available yet.",
						Toast.LENGTH_SHORT).show();
			} finally {
				pd = null;
			}
		}
	}

	/** SHOW DRIVERS ON THE MAP ENDS */

	/** SHOW SELECTED DRIVER INFO POPUP STARTS */

	private class LoadDriverInfo extends AsyncTask<Void, Void, String> {

		private static final String URL_DRIVER_INFO = "http://tabi1.couthymedia.com/Wajahat/get_driver_info.php";
		private static final String KEY_DRIVER_ID = "driver_id";
		private static final String KEY_IMG_URL = "img";
		private static final String KEY_DRIVER = "driver_name";
		private static final String KEY_TAXI = "taxi";
		private static final String KEY_RATING = "rating";
		private static final String TAG_SUCCESS = "success";

		// Views
		private TextView tvDriver, tvTaxi, tvRating;
		private ImageView ivDriver;
		private ImageButton ibCallDriver;

		private String imgUrl = "";
		private String driverName = "", taxi = "", rating = "";

		private JSONParser jParser;

		ProgressDialog pd;
		PopupWindow popup;

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(context);
			pd.show();
			init();
		}

		// init()
		@SuppressLint("InflateParams")
		private void init() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflater.inflate(R.layout.popup_driver_info, null);
			initViews(v);
		}

		// initViews()
		private void initViews(View v) {
			popup = new PopupWindow(v, LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);

			tvDriver = (TextView) v.findViewById(R.id.tvDriver);
			tvTaxi = (TextView) v.findViewById(R.id.tvTaxi);
			tvRating = (TextView) v.findViewById(R.id.tvRating);

			ivDriver = (ImageView) v.findViewById(R.id.ivDriver);

			ibCallDriver = (ImageButton) v.findViewById(R.id.ibCallDriver);

			jParser = new JSONParser();

			showPopup();
		}

		// showPopup()
		private void showPopup() {
			popup.showAtLocation(rlWrapper, Gravity.BOTTOM, 0, 0);
		}

		@Override
		protected String doInBackground(Void... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(KEY_DRIVER_ID, selectedDriverId));
			JSONObject jObj = jParser.makeHttpRequest(URL_DRIVER_INFO,
					Constants.PING_METHOD_POST, params);
			Log.d("request_made", selectedDriverId);

			try {
				int success = jObj.getInt(TAG_SUCCESS);
				Log.d("got_status", String.valueOf(success));
				if (success == 1) {
					Log.d("status_success", "status_success");
					imgUrl = jObj.getString(KEY_IMG_URL);

					try {
						new LoadProcessImg(ivDriver).execute(imgUrl);
					} catch (Exception e) {
					}

					try {
						driverName = jObj.getString(KEY_DRIVER);
						Log.d("driver", driverName);
					} catch (Exception e) {
						Log.e("excep_", "excep_ " + e.toString());
					}

					try {
						taxi = jObj.getString(KEY_TAXI);
						Log.d("taxi", taxi);
					} catch (Exception e) {
						Log.e("excep_", "excep_ " + e.toString());
					}

					try {
						rating = jObj.getString(KEY_RATING);
						Log.d("rating", rating);
					} catch (Exception e) {
						Log.e("excep_", "excep_ " + e.toString());
					}
				}
			} catch (Exception e) {

			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				pd.dismiss();
				pd = null;
			} catch (Exception e) {
			}

			Log.d("post_execute", "post_execute");
			// setting views values
			try {
				tvDriver.setText(driverName);
				tvTaxi.setText(taxi);
				tvRating.setText(rating);

				setCallDriverListener(ibCallDriver, popup);
				setMapListener();
			} catch (Exception e) {
				Log.e("excep_views", e.toString());
			}
		}
	}

	// setCallDriverListener
	private void setCallDriverListener(ImageButton ibCallDriver,
			final PopupWindow popup) {
		ibCallDriver.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popup.dismiss();
				try {
//					showDriverInfoPopup();
					Intent intent = new Intent(getApplicationContext(), TaxiRequest.class);
					startActivity(intent);
				} catch (Exception e) {
				}
			}
		});
	}

	@SuppressLint("InflateParams")
	private void showDriverInfoPopup() {
		PopupWindow driverInfoPopup;
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.popup_current_to_dest, null);

		// Views
		TextView tvCurrent, tvDest;
		tvCurrent = (TextView) v.findViewById(R.id.tvCurrent);
		tvDest = (TextView) v.findViewById(R.id.tvDest);

		GPSTracker gpsTracker = new GPSTracker(context);
		double lat = gpsTracker.getLatitude();
		double lng = gpsTracker.getLongitude();
		String address = "";

		Geocoder gcd = new Geocoder(context, Locale.getDefault());
		List<Address> addresses = null;
		try {
			addresses = gcd.getFromLocation(lat, lng, 1);
			if (addresses.size() > 0) {
				String subLocality = "";
				String thoroughFare = "";
				String locality = "";
				try {
					subLocality = addresses.get(0).getSubLocality();
				} catch (Exception e) {
					subLocality = "";
				}

				try {
					thoroughFare = addresses.get(0).getThoroughfare();
				} catch (Exception e) {
					thoroughFare = "";
				}

				try {
					locality = addresses.get(0).getLocality();
				} catch (Exception e) {
					locality = "";
				}

				address = subLocality + ", " + thoroughFare + " " + locality;
				tvCurrent.setText(address);
			}
		} catch (Exception e) {
			Log.e("excep_address", e.toString());
		}

		driverInfoPopup = new PopupWindow(v, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		driverInfoPopup.showAtLocation(rlWrapper, Gravity.BOTTOM, 0, 0);
	}

	/* PROCESS DRIVER IMAGE STARTS */

	private class LoadProcessImg extends AsyncTask<String, Void, Bitmap> {

		ImageView iv;

		public LoadProcessImg(ImageView iv) {
			this.iv = iv;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			try {
				URL url = new java.net.URL(params[0]);
				InputStream is = url.openStream();
				return BitmapFactory.decodeStream(is);
			} catch (Exception e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			iv.setImageBitmap(result);
		}
	}

	/* PROCESS DRIVER IMAGE ENDS */

	// setMapListener()
	private void setMapListener() {
		googleMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng point) {

			}
		});
	}
	/** SHOW SELECTED DRIVER INFO POPUP ENDS */
}
package com.fragments;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.friendlylimo.util.json.JSONParser;
import com.friendlylimo.Constants;
import com.friendlylimo.R;

public class FragSelectedDriverInfo extends Fragment {

	private static final String URL_DRIVER_INFO = "http://tabi1.couthymedia.com/Wajahat/get_driver_info.php";
	private static final String KEY_DRIVER_ID = "driver_id";
	private static final String KEY_IMG_URL = "img";
	private static final String KEY_DRIVER = "driver_name";
	private static final String KEY_TAXI = "taxi";
	private static final String KEY_RATING = "rating";
	private static final String TAG_SUCCESS = "success";

	// Views
	private ImageView ivDriver;
	private TextView tvDriver, tvTaxi, tvRating;

	private String driverId = "";
	private String imgUrl = "";
	private String driverName = "", taxi = "", rating = "";

	private Activity activity;
	private JSONParser jParser;

	public FragSelectedDriverInfo() {

	}

	public FragSelectedDriverInfo(String driverId) {
		this.driverId = driverId;
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.popup_driver_info, null);
		init(v);
		new LoadDriverInfo().execute();
		return v;
	}

	// init()
	private void init(View v) {
		ivDriver = (ImageView) v.findViewById(R.id.ivDriver);

		tvDriver = (TextView) v.findViewById(R.id.tvDriver);
		tvTaxi = (TextView) v.findViewById(R.id.tvTaxi);
		tvRating = (TextView) v.findViewById(R.id.tvRating);

		activity = getActivity();
		jParser = new JSONParser();
	}

	/** SHOW SELECTED DRIVER INFO STARTS */

	private class LoadDriverInfo extends AsyncTask<Void, Void, String> {

		ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(activity);
			pd.show();
		}

		@Override
		protected String doInBackground(Void... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(KEY_DRIVER_ID, driverId));
			JSONObject jObj = jParser.makeHttpRequest(URL_DRIVER_INFO,
					Constants.PING_METHOD_POST, params);
			Log.d("request_made", driverId);

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

					// driverName = jObj.getString(KEY_DRIVER);
					// taxi = jObj.getString(KEY_TAXI);
					// rating = jObj.getString(KEY_RATING);
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
			} catch (Exception e) {
				Log.e("excep_views", e.toString());
			}
		}
	}

	/** SHOW SELECTED DRIVER INFO ENDS */

	/** PROCESS DRIVER IMAGE STARTS */

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

	/** PROCESS DRIVER IMAGE ENDS */
}
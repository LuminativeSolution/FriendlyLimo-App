package com.driver.accounts;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlylimo.util.json.JSONParser;
import com.driver.home.DriverHome;
import com.friendlylimo.PersonType;
import com.friendlylimo.R;
import com.services.UpdateDriverLocationService;

public class DriverLogin extends Activity {

	private static final String URL = "http://tabi1.couthymedia.com/Wajahat/driver_login.php";
	private static final String KEY_EMAIL = "email";
	private static final String KEY_PWD = "pwd";
	private static final String PING_METHOD = "POST";
	private static final String TAG_ID = "id";
	private static final String TAG_SUCCESS = "success";

	private static final int REQUEST_CODE = 1;

	// Widgets
	private TextView tvFriendly, tvLimo, tvSign, tvIn;
	private Button bLogin;
	private EditText etEmail, etPwd;

	private Typeface tfRalewayReg, tfRalewayBold;
	private JSONParser jParser;

	public static String driverId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.driver_login);
		init();
		setTpeFaces();
		setListeners();
	}

	// init()
	private void init() {
		tvFriendly = (TextView) findViewById(R.id.tvFriendly);
		tvLimo = (TextView) findViewById(R.id.tvLimo);
		tvSign = (TextView) findViewById(R.id.tvSign);
		tvIn = (TextView) findViewById(R.id.tvIn);

		etEmail = (EditText) findViewById(R.id.etEmail);
		etPwd = (EditText) findViewById(R.id.etPwd);

		bLogin = (Button) findViewById(R.id.bLogin);

		tfRalewayReg = Typeface.createFromAsset(getAssets(),
				"Raleway-Regular.ttf");
		tfRalewayBold = Typeface.createFromAsset(getAssets(),
				"Raleway-Bold.ttf");
	}

	// setTpeFaces()
	private void setTpeFaces() {
		tvFriendly.setTypeface(tfRalewayReg);
		tvLimo.setTypeface(tfRalewayBold);
		tvSign.setTypeface(tfRalewayReg);
		tvIn.setTypeface(tfRalewayBold);
		bLogin.setTypeface(tfRalewayReg);
	}

	// setListeners()
	private void setListeners() {
		bLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (validateLogin())
					new LoadLogin().execute();
			}
		});
	}

	// validateLogin()
	private boolean validateLogin() {
		return etEmail.getText().toString().length() > 0
				|| etPwd.getText().toString().length() > 0;
	}

	// load login
	private class LoadLogin extends AsyncTask<String, String, String> {

		ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(DriverLogin.this);
			pd.show();

			jParser = new JSONParser();
		}

		@Override
		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(KEY_EMAIL, etEmail.getText()
					.toString()));
			params.add(new BasicNameValuePair(KEY_PWD, etPwd.getText()
					.toString()));
			JSONObject jObj = jParser.makeHttpRequest(URL, PING_METHOD, params);

			try {
				int success = jObj.getInt(TAG_SUCCESS);
				if (success == 1) {
					driverId = jObj.getString(TAG_ID);
					// driverId = driverId != null ? driverId : "empty";
					Log.d("driver_id", String.valueOf(driverId));
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("null_driver", e.toString());
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				if (pd != null) {
					pd.dismiss();
					detectGPS();
					DriverLogin.this.startService(new Intent(DriverLogin.this,
							UpdateDriverLocationService.class));
				}
			} catch (Exception e) {

			}
		}
	}

	//
	public void detectGPS() {
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
			startActivity(new Intent(DriverLogin.this, DriverHome.class));
		else
			enableGPSAlert();
	}

	// enableGPSAlert()
	private void enableGPSAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(DriverLogin.this);
		builder.setTitle("Enable GPS");
		builder.setMessage("Please enable GPS to proceed");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent callGPSSettingIntent = new Intent(
						android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivityForResult(callGPSSettingIntent, REQUEST_CODE);
			}
		});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						startActivity(new Intent(DriverLogin.this,
								PersonType.class));
					}
				});
		builder.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Toast.makeText(DriverLogin.this, "Came back", Toast.LENGTH_LONG).show();
		startActivity(new Intent(DriverLogin.this, DriverHome.class));
	}
}

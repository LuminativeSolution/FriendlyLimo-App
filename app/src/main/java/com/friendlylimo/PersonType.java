package com.friendlylimo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.client.accounts.ClientLogin;
import com.driver.accounts.DriverLogin;

public class PersonType extends Activity {

	private static final int REQUEST_CODE = 1;
	private TextView tvFriendly, tvLimo;
	private Button bUser, bDriver;

	private Typeface tfRalewayReg, tfRalewayBold;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fullScreenWindow();
		setContentView(R.layout.person_type);
		init();
		setTpeFaces();
		setListeners();
	}

	// fullScreenWindow()
	private void fullScreenWindow() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	// alertNoInternet()
	private void alertNoInternet() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.no_internet_title);
		builder.setMessage(R.string.no_internet_msg);
		builder.setPositiveButton(Constants.OK,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						exitApplication();
					}
				});
	}

	// init()
	private void init() {
		tvFriendly = (TextView) findViewById(R.id.tvFriendly);
		tvLimo = (TextView) findViewById(R.id.tvLimo);

		bUser = (Button) findViewById(R.id.bUser);
		bDriver = (Button) findViewById(R.id.bDriver);

		tfRalewayReg = Typeface.createFromAsset(getAssets(),
				"Raleway-Regular.ttf");
		tfRalewayBold = Typeface.createFromAsset(getAssets(),
				"Raleway-Bold.ttf");
	}

	// setTpeFaces()
	private void setTpeFaces() {
		tvFriendly.setTypeface(tfRalewayReg);
		tvLimo.setTypeface(tfRalewayBold);
		bUser.setTypeface(tfRalewayReg);
		bDriver.setTypeface(tfRalewayReg);
	}

	// setListeners()
	private void setListeners() {
		bUser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Constants.detectInternet(PersonType.this)) {
					LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
					if (locationManager
							.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
						startActivity(new Intent(PersonType.this,
								ClientLogin.class));
					} else
						enableGPSAlert();
				} else {
					alertNoInternet();
				}
			}
		});

		bDriver.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Constants.detectInternet(PersonType.this)) {
					LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
					if (locationManager
							.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
						startActivity(new Intent(PersonType.this,
								DriverLogin.class));
					} else
						enableGPSAlert();
				} else {
					alertNoInternet();
				}
			}
		});
	}

	// enableGPSAlert()
	private void enableGPSAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(PersonType.this);
		builder.setTitle("Enable GPS");
		builder.setMessage("Please enable GPS to proceed");
		builder.setCancelable(false);
		builder.setPositiveButton(Constants.OK,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent callGPSSettingIntent = new Intent(
								android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivityForResult(callGPSSettingIntent,
								REQUEST_CODE);
					}
				});
		builder.setNegativeButton(Constants.CANCEL,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						exitApplication();
					}
				});
		builder.show();
	}

	// exitApplication()
	private void exitApplication() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		init();
		setTpeFaces();
		setListeners();
	}
}

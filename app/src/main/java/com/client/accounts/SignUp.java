package com.client.accounts;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlylimo.util.json.JSONParser;
import com.client.home.ClientHome;
import com.friendlylimo.R;

public class SignUp extends Activity {

	private static final String URL = "http://tabi1.couthymedia.com/Wajahat/sign_up.php";
	private static final String SUCCESS_MSG = "Account successfully created";
	private static final String KEY_FIRSTNAME = "first_name";
	private static final String KEY_LASTNAME = "last_name";
	private static final String KEY_EMAIL = "email";
	private static final String KEY_PHONE = "phone";
	private static final String KEY_PWD = "pwd";
	private static final String PING_METHOD = "POST";

	// Widgets
	private TextView tvFriendly, tvLimo, tvSign, tvUp;
	private EditText etFirstName, etLastName, etEmail, etPhn, etPwd;
	private Button bSignUp;

	private Typeface tfRalewayReg, tfRalewayBold;
	private JSONParser jParser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// fullScreenWindow();
		setContentView(R.layout.sign_up);
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

	// init()
	private void init() {
		tvFriendly = (TextView) findViewById(R.id.tvFriendly);
		tvLimo = (TextView) findViewById(R.id.tvLimo);
		tvSign = (TextView) findViewById(R.id.tvSign);
		tvUp = (TextView) findViewById(R.id.tvUp);

		etFirstName = (EditText) findViewById(R.id.etFirstName);
		etLastName = (EditText) findViewById(R.id.etLastName);
		etEmail = (EditText) findViewById(R.id.etEmail);
		etPhn = (EditText) findViewById(R.id.etPhn);
		etPwd = (EditText) findViewById(R.id.etPwd);

		bSignUp = (Button) findViewById(R.id.bSignUp);

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
		tvUp.setTypeface(tfRalewayBold);
		bSignUp.setTypeface(tfRalewayReg);
	}

	// setListeners()
	private void setListeners() {
		bSignUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (validateSignUp())
					new LoadSignUp().execute();
			}
		});
	}

	// validateSignUp()
	private boolean validateSignUp() {
		return etFirstName.getText().toString().length() > 0
				|| etLastName.getText().toString().length() > 0
				|| etEmail.getText().toString().length() > 0
				|| etPhn.getText().toString().length() > 0
				|| etPwd.getText().toString().length() > 0;
	}

	// load sign up
	private class LoadSignUp extends AsyncTask<String, String, String> {

		ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(SignUp.this);
			pd.show();

			jParser = new JSONParser();
		}

		@Override
		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(KEY_FIRSTNAME, etFirstName
					.getText().toString()));
			params.add(new BasicNameValuePair(KEY_LASTNAME, etLastName
					.getText().toString()));
			params.add(new BasicNameValuePair(KEY_EMAIL, etEmail.getText()
					.toString()));
			params.add(new BasicNameValuePair(KEY_PHONE, etPhn.getText()
					.toString()));
			params.add(new BasicNameValuePair(KEY_PWD, etPwd.getText()
					.toString()));
			jParser.makeHttpRequest(URL, PING_METHOD, params);

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				if (pd != null) {
					pd.dismiss();
					Toast.makeText(SignUp.this, SUCCESS_MSG, Toast.LENGTH_LONG)
							.show();
					startActivity(new Intent(SignUp.this, ClientHome.class));
				}
			} catch (Exception e) {
			}
		}
	}
}

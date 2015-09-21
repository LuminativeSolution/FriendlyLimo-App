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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.friendlylimo.util.json.JSONParser;
import com.client.home.ClientHome;
import com.friendlylimo.R;

public class ClientLogin extends Activity {

	private static final String URL = "http://tabi1.couthymedia.com/Wajahat/client_login.php";
	private static final String PING_METHOD = "POST";
	private static final String KEY_EMAIL = "email";
	private static final String KEY_PWD = "pwd";

	// Widgets
	private EditText etEmail, etPwd;
	private Button bLogin;
	private TextView tvFriendly, tvLimo, tvSign, tvIn, tvSignUp;

	private Typeface tfRalewayReg, tfRalewayBold;
	private JSONParser jParser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client_login);
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
		tvSignUp = (TextView) findViewById(R.id.tvSignUp);

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
		tvSignUp.setTypeface(tfRalewayReg);
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

		tvSignUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(ClientLogin.this, SignUp.class));
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
			pd = new ProgressDialog(ClientLogin.this);
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
			jParser.makeHttpRequest(URL, PING_METHOD, params);

			Log.d("reached", "reached");
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			pd.dismiss();
			// Toast.makeText(ClientLogin.this, "Hello",
			// Toast.LENGTH_LONG).show();
			startActivity(new Intent(ClientLogin.this, ClientHome.class));
			// try {
			// if (pd != null) {
			// }
			// } catch (Exception e) {
			// }
		}
	}
}

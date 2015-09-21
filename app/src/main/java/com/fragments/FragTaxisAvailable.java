package com.fragments;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.friendlylimo.R;

public class FragTaxisAvailable extends ListFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.popup_current_to_dest, null);
		return v;
	}

	// init()
	private void init(View v) {
		
	}
}

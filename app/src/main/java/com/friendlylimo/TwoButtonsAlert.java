package com.friendlylimo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class TwoButtonsAlert {

	private Context context;

	public TwoButtonsAlert(Context context) {
		this.context = context;
	}

	// public boolean showTwoButtonsAlert(String title, String message,
	// String posBtnText, String negBtnText) {
	// AlertDialog.Builder builder = new AlertDialog.Builder(context);
	// builder.setTitle(title);
	// builder.setMessage(message);
	// builder.setPositiveButton(posBtnText,
	// new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	//
	// }
	// });
	// builder.setPositiveButton(negBtnText,
	// new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.dismiss();
	// }
	// });
	// }
}

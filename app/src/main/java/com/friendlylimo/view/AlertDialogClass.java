package com.friendlylimo.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Muzamil Hussain on 9/2/2015.
 */
public class AlertDialogClass extends Activity{

    // this context will use when we work with Alert Dialog
    final Context context = this;

    // just for test, when we click this button, we will see the alert dialog.
    private Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.alert);




        /* Alert Dialog Code Start*/
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Select Your SIM Type"); //Set Alert dialog title here
        //alert.setMessage("Enter your Name Here"); //Message here

        alert.setPositiveButton("Post Paid", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // String value = input.getText().toString();
                // Do something with value!
                //You will get input data in this variable.

                Toast.makeText(getApplicationContext(),"Post Paid", Toast.LENGTH_LONG).show();
                //db.addContact(new SimDetails( telephonyManager.getSimSerialNumber(), telephonyManager.getNetworkOperatorName(),"Post"));


                Intent returnIntent = new Intent();
                returnIntent.putExtra("TYPE","POST Paid");
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });

        alert.setNegativeButton("Pre Paid", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
                Toast.makeText(getApplicationContext(),"Pre Paid", Toast.LENGTH_LONG).show();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("TYPE","PRE Paid");
                setResult(RESULT_CANCELED,returnIntent);
                finish();
            }
        });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
        /* Alert Dialog Code End*/




    }


    /*
    @Override
    public void finish() {
      Intent data = new Intent();
      // Return some hard-coded values
      data.putExtra("TYPE", "Swinging on a star. ");
      data.putExtra("returnKey2", "You could be better then you are. ");
      setResult(RESULT_OK, data);
      super.finish();
    }*/

}

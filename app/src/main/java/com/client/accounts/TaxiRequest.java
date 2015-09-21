package com.client.accounts;

/**
 * Created by Muzamil Hussain on 9/1/2015.
 */

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SimpleAdapter;

import com.GPS.GPSTracker;
import com.friendlylimo.util.json.PlaceJSONParser;
import com.friendlylimo.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Muzamil Hussain on 8/28/2015.
 */
public class TaxiRequest extends Activity {

    AutoCompleteTextView fromPlaces, toPlaces;
    CheckBox useCurrentLocation;
    Button taxiRequestOk;

    PlacesTask placesTask;
    ParserTask parserTask;

    GPSTracker gpsTracker;
    private double userLat = 0, userLng = 0;
    public boolean isFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxi_request);

        useCurrentLocation = (CheckBox) findViewById(R.id.isCurrentLocation);
        useCurrentLocation.setOnCheckedChangeListener(checkedChangeListener);

        fromPlaces = (AutoCompleteTextView) findViewById(R.id.from);
        fromPlaces.setThreshold(1);

        toPlaces = (AutoCompleteTextView) findViewById(R.id.destination);
        toPlaces.setThreshold(1);

        taxiRequestOk = (Button) findViewById(R.id.taxi_request_ok);
        taxiRequestOk.setOnClickListener(okListener);

        gpsTracker = new GPSTracker(this);
        userLat = gpsTracker.getLatitude();
        userLng = gpsTracker.getLongitude();
        String currentLocation = gpsTracker.getFormattedAddressFromLatLng(this, userLat, userLng);
        Log.d("LOC: ", currentLocation);
        fromPlaces.setText(currentLocation);

        fromPlaces.addTextChangedListener(new MyTextWatcher(fromPlaces));
        toPlaces.addTextChangedListener(new MyTextWatcher(toPlaces));
    }

    View.OnClickListener okListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String originAddress = fromPlaces.getText().toString();
            String destAddress = toPlaces.getText().toString();

            Log.d("origin_address", originAddress);
            Log.d("origin_address", "" + destAddress);

            LatLng originLatLng = getLocationFromAddress(originAddress);
            LatLng destLatLng = getLocationFromAddress(destAddress);

            Log.d("origin_lat", "" + originLatLng.latitude);
            Log.d("origin_lng", ""+originLatLng.longitude);
            Log.d("dest_lat", ""+destLatLng.latitude);
            Log.d("dest_lng", ""+destLatLng.longitude);

            Intent intent = new Intent(TaxiRequest.this, TaxiRequestConfirm.class);

            // Passing latitude and longitude to the MapActiv
            intent.putExtra("origin_lat", originLatLng.latitude);
            intent.putExtra("origin_lng", originLatLng.longitude);
            intent.putExtra("dest_lat", destLatLng.latitude);
            intent.putExtra("dest_lng", destLatLng.longitude);


            startActivity(intent);

        }
    };

    private class MyTextWatcher implements TextWatcher {

        private View view;
        private MyTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if(view.getId() == R.id.from){
                Log.d("view", "From");
                isFrom = true;
            }else{
                Log.d("view", "Destination");
                isFrom = false;
            }
            placesTask = new PlacesTask();
            placesTask.execute(s.toString());
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches all places from GooglePlaces AutoComplete Web Service
    private class PlacesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... place) {
            // For storing data from web service
            String data = "";

            // Obtain browser key from https://code.google.com/apis/console
            String key = "key=AIzaSyDoike0kciGJgcWhjbMD88vPf_G1PSLzMg";

            String input="";

            try {
                input = "input=" + URLEncoder.encode(place[0], "utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            // place type to be searched
            String types = "types=geocode";

            // Sensor enabled
            String sensor = "sensor=false";

            // Building the parameters to the web service
            String parameters = input+"&"+types+"&"+sensor+"&"+key;

            // Output format
            String output = "json";

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"+output+"?"+parameters;

            try{
                // Fetching the data from we service
                data = downloadUrl(url);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Creating ParserTask
            parserTask = new ParserTask();

            // Starting Parsing the JSON string returned by Web Service
            parserTask.execute(result);
        }
    }
    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{

        JSONObject jObject;

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;

            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try{
                jObject = new JSONObject(jsonData[0]);
                Log.d("json", jObject.toString());
                // Getting the parsed data as a List construct
                places = placeJsonParser.parse(jObject);

            }catch(Exception e){
                Log.d("Exception",e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {

            String[] from = new String[] { "description"};
            int[] to = new int[] { android.R.id.text1 };

            // Creating a SimpleAdapter for the AutoCompleteTextView
            SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result, android.R.layout.simple_list_item_1, from, to);

            // Setting the adapter
            if(isFrom){
                Log.d("post", "From");
                fromPlaces.setAdapter(adapter);
            }else{
                Log.d("post", "Destination");
                toPlaces.setAdapter(adapter);
            }


        }
    }

    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if(!buttonView.isChecked()){
                fromPlaces.setEnabled(true);
                fromPlaces.setText("");
            }else{
                String currentLocation = gpsTracker.getFormattedAddressFromLatLng(TaxiRequest.this, userLat, userLng);
                Log.d("LOC: ", currentLocation);
                fromPlaces.setText(currentLocation);
                fromPlaces.setEnabled(false);
            }
        }
    };
}


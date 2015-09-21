package com.client.accounts;

/**
 * Created by Muzamil Hussain on 8/30/2015.
 */

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.friendlylimo.util.json.DirectionsJSONParser;
import com.friendlylimo.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaxiRequestConfirm extends FragmentActivity {

    GoogleMap map;
    ArrayList<LatLng> markerPoints;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxi_request_confirm);

        Bundle extras = getIntent().getExtras();

        // Initializing
        markerPoints = new ArrayList<LatLng>();

        // Getting reference to SupportMapFragment of the activity_main
        SupportMapFragment fm = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting Map for the SupportMapFragment
        map = fm.getMap();

        if(map!=null) {

            // Enable MyLocation Button in the Map
            map.setMyLocationEnabled(true);

            // Receiving latitude from MainActivity screen
            double latFrom = 0;
            double latDest = 0;

            // Receiving longitude from MainActivity screen
            double lngFrom = 0;
            double lngDest = 0;


            if (extras != null){
                latFrom = extras.getDouble("origin_lat", 0);
                latDest = extras.getDouble("dest_lat", 0);

                // Receiving longitude from MainActivity screen
                lngFrom = extras.getDouble("origin_lng", 0);
                lngDest = extras.getDouble("dest_lng", 0);
            }

            Log.d("origin_lat2", "" + latFrom);
            Log.d("origin_lng2", ""+latDest);
            Log.d("dest_lat2", ""+lngFrom);
            Log.d("dest_lng2", ""+lngDest);

            final LatLng origin = new LatLng(latFrom, lngFrom);
            final LatLng destination = new LatLng(latDest, lngDest);


            setLocation(markerPoints, origin, destination);

            map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {

                    if(marker.getSnippet().equals("origin")){
                        setLocation(markerPoints, marker.getPosition(), destination);
                    }else{
                        setLocation(markerPoints, origin, marker.getPosition());
                    }
                }
            });
//            markerPoints.add(origin);
//            markerPoints.add(destination);
//
//            // Creating MarkerOptions
//            MarkerOptions optionsOrigin = new MarkerOptions();
//            MarkerOptions optionsDest = new MarkerOptions();
//            optionsOrigin.draggable(true);
//            optionsDest.draggable(true);
//
//            // Setting position for the MarkerOptions
//            optionsOrigin.position(origin);
//            optionsDest.position(destination);
//
//            /**
//             * For the start location, the color of marker is GREEN and
//             * for the end location, the color of marker is RED.
//             */
//            optionsOrigin.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//            optionsDest.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));


//            // Add new marker to the Google Map Android API V2
//            map.addMarker(optionsOrigin);
//            map.addMarker(optionsDest);

//            // Checks, whether start and end locations are captured
//            if (markerPoints.size() >= 2) {
//                LatLng originPoint = markerPoints.get(0);
//                LatLng destPoint = markerPoints.get(1);
//
//                // Getting URL to the Google Directions API
//                String url = getDirectionsUrl(originPoint, destPoint);
//
//                DownloadTask downloadTask = new DownloadTask();
//
//                // Start downloading json data from Google Directions API
//                downloadTask.execute(url);
//            }
        }
    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&key=AIzaSyDoike0kciGJgcWhjbMD88vPf_G1PSLzMg";

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
        Log.d("url", url);

        return url;
    }
    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
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

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>{

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);
                    double lat = 0;
                    double lng = 0;
                    if(point.get("lat") != null && point.get("lng")!=null){
                        lat = Double.parseDouble(point.get("lat"));
                        lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        points.add(position);
                    }




                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);
            }


            // Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);
            fixZoom(lineOptions);
        }
    }

    private void setLocation(ArrayList<LatLng> markerPoints, LatLng origin, LatLng destination){
        markerPoints.clear();
        map.clear();

        markerPoints.add(origin);
        markerPoints.add(destination);


        map.addMarker(addMarkerOption(origin, true, "G", "origin"));
        map.addMarker(addMarkerOption(destination, true, "R", "destination"));

        // Checks, whether start and end locations are captured
        if (markerPoints.size() >= 2) {
            LatLng originPoint = markerPoints.get(0);
            LatLng destPoint = markerPoints.get(1);

            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(originPoint, destPoint);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
        }

    }

    private MarkerOptions addMarkerOption(LatLng latlng, boolean isDraggable, String color, String snippet){


        // Creating MarkerOptions
        MarkerOptions option = new MarkerOptions();
        option.draggable(isDraggable);

        // Setting position for the MarkerOptions
        option.position(latlng);

        /**
         * For the start location, the color of marker is GREEN and
         * for the end location, the color of marker is RED.
         */
        if(color.equals("G"))
            option.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        else
            option.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        option.snippet(snippet);

        return option;
    }
    private void fixZoom(PolylineOptions polylineOptions) {
        List<LatLng> points = polylineOptions.getPoints();

        LatLngBounds.Builder bc = new LatLngBounds.Builder();

        for (LatLng item : points) {
            bc.include(item);
        }

        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 17));
    }
}
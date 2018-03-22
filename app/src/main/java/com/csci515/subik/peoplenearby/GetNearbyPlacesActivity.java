package com.csci515.subik.peoplenearby;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
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

public class GetNearbyPlacesActivity extends AppCompatActivity {

    GoogleMap mGoogleMap;
    Context context;
    String[] data;
    static String name, gender, age, cus_id;
    static double destination_lat, destination_long;

    //Intent has some values

    @Override
    protected void onCreate(Bundle savedInstanceState) throws SecurityException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_nearby_places);
        Intent intent = getIntent();
        cus_id = intent.getStringExtra("GoingOutWithID");
        String details = intent.getStringExtra("GoingOutWithDetails");
        data = details.split("\n");
        //Toast.makeText(getApplicationContext(), String.valueOf(intent), Toast.LENGTH_LONG);
        name = data[0].substring(data[0].lastIndexOf(": ")+2);
        age = data[1].substring(data[1].lastIndexOf(": ")+2);
        gender = data[2].substring(data[2].lastIndexOf(": ")+2);
        context = GetNearbyPlacesActivity.this;
        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        if (status != ConnectionResult.SUCCESS){
            // Google Play Services are not available.
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
            finish();
        }

        // Google Play Services are available.
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.placesMap);
        mGoogleMap = supportMapFragment.getMap();
        mGoogleMap.setMyLocationEnabled(true);

        // Use the LocationManager class to obtain GPS locations.
        LocationManager locationManager = (LocationManager) getSystemService( LOCATION_SERVICE );
        Criteria criteria = new Criteria();
        // Output is gps
        String bestProvider =  locationManager.getBestProvider(criteria, true);
        //Log.d("Best provider Value: ", bestProvider);
        Location location = locationManager.getLastKnownLocation(bestProvider);

        if (location != null){
            // Building the URL including Google Directions API
            String url = getPlacesUrl(location);
            //Log.d("URL: ", url);
            DownloadTask downloadTask = new DownloadTask( );
            // Start downloading JSON data from Google Directions API.
            downloadTask.execute( url );
        }
    }

    private String getPlacesUrl(Location location) {
        String Web_Key = getResources().getString(R.string.google_web_key);
        // Sensor enabled
        String sensor = "sensor=true";
        // Building the parameters to the web service
        //String parameters = "location=" + latitude + "," + longitude + "&radius=500&type=food&name=cruise&key=" + API_Key;
        String parameters = "location=" + location.getLatitude() + "," + location.getLongitude() + "&radius=5000&type=resturant"+"&"+sensor+"&key=" + Web_Key;
        // Output format
        String output = "json";
        // Building the URL for the web serviceString

        return "https://maps.googleapis.com/maps/api/place/nearbysearch/" + output + "?" + parameters;
    }


    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";
            try {
                // Fetching the data from web service
                data = downloadUrl( url[0] );
                Log.d( "Downloaded url", data );
            }
            catch( Exception e ) {
                Log.d( "Background Task", e.toString( ) );
            }
            return data;
        }  // End of doInBackground


        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            ParserTask parserTask = new ParserTask( );
            // Invokes the thread for parsing the JSON data.
            parserTask.execute( data );
        }  // End of onPostExecute

    }  // End of DownloadTask


    private String downloadUrl(String strUrl) throws IOException {

        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL( strUrl );
            // Creating an HTTP connection to communicate with URL
            urlConnection = (HttpURLConnection) url.openConnection( );
            // Connecting to URL
            urlConnection.connect( );
            // Reading data from URL
            iStream = urlConnection.getInputStream( );
            BufferedReader br = new BufferedReader(
                    new InputStreamReader( iStream ) );
            StringBuffer sb  = new StringBuffer( );
            String line = "";
            while( ( line = br.readLine( ) ) != null ) {
                sb.append( line );
            }
            data = sb.toString( );
            br.close( );
        }  // End of try
        catch( Exception e ) {
            Log.d( "Exception downloading ", e.toString( ) );
        }
        finally {
            iStream.close( );
            urlConnection.disconnect( );
        }
        return data;

    }  // End of downloadUrl


    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {
        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<HashMap<String, String>> placesData = null;
            try {
                jObject = new JSONObject( jsonData[0] );
                PlacesJSONParser parser = new PlacesJSONParser( );
                // Starts parsing data.
                placesData = parser.parse( jObject );
            } catch (JSONException e) {
                e.printStackTrace();
            }finally {

                Log.d("Nearby Places: ", placesData.toString());
                return placesData;
            }
        }  // End of doInBackground

        @Override
        protected void onPostExecute(List<HashMap<String, String>> nearbyPlacesList) {
            super.onPostExecute(nearbyPlacesList);
            for (int i = 0; i < nearbyPlacesList.size(); i++) {
                Log.d("onPostExecute","Entered into showing locations");
                MarkerOptions markerOptions = new MarkerOptions();
                HashMap<String, String> googlePlace = nearbyPlacesList.get(i);
                double lat = Double.parseDouble(googlePlace.get("lat"));
                double lng = Double.parseDouble(googlePlace.get("lng"));
                String placeName = googlePlace.get("place_name");
                String vicinity = googlePlace.get("vicinity");
                LatLng latLng = new LatLng(lat, lng);
                markerOptions.position(latLng);
                markerOptions.title(placeName);
                markerOptions.snippet(vicinity);
                //Log.d("LatLong: ", String.valueOf(latLng));
                //markerOptions.title(placeName + " : " + vicinity);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                mGoogleMap.addMarker(markerOptions);
                //move map camera
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(14));

                mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
                {

                    @Override
                    public boolean onMarkerClick(final Marker arg0) {
                        //if(arg0.getTitle().equals("Current Location")){
                        Log.d("location details: ", arg0.getPosition().toString());
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Resturant Description: ");
                        // Setting Dialog Message
                        builder.setMessage("Name: "+arg0.getTitle()+"\nLocation: "+arg0.getSnippet());
                        //+"\nLatLng: "+arg0.getPosition()
                        destination_lat = arg0.getPosition().latitude;
                        destination_long = arg0.getPosition().longitude;
                        // Setting Positive "Yes" Btn
                        builder.setPositiveButton("Send Request", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
                                progressDialog.setIndeterminate(true);
                                progressDialog.setMessage("Please Wait...");
                                progressDialog.show();
                                new android.os.Handler().postDelayed(new Runnable() {
                                    public void run() {

                                        String[] array = {cus_id, name, age, gender, String.valueOf(destination_lat),
                                                String.valueOf(destination_long), arg0.getTitle(), arg0.getSnippet()};
                                        Intent intent = new Intent(context, SendRequestActivity.class);
                                        intent.putExtra("Array", array);
                                        context.startActivity(intent);
                                        //Toast.makeText(context, "Notification Sent...", Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                    }
                                }, 1000);

                            }
                        });

                        // Setting Negative "NO" Btn
                        builder.setNegativeButton("Take me Home",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(context, HomePageActivity.class);
                                        context.startActivity(intent);
                                        Toast.makeText(context, "You clicked on Discard", Toast.LENGTH_SHORT).show();
                                        dialog.cancel();
                                    }
                                });

                        // Showing Alert Dialog
                        builder.show();
                        //}
                        //Log.d("You clicked: ", arg0.getSnippet());
                        return true;
                    }
                });
            }
        }  // End of onPostExecute
    }    // End of ParserTask
}

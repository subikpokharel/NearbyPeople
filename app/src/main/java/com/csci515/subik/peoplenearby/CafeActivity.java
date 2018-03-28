package com.csci515.subik.peoplenearby;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CafeActivity extends AppCompatActivity {

    static String[] destination = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafe);

        Intent intent = getIntent();
        String friend_id = intent.getStringExtra("friendId");
        String dest = intent.getStringExtra("destination");
        destination = dest.split("/");
        /*double lat = Double.parseDouble(destination[0]);
        double lng = Double.parseDouble(destination[1]);*/
        //LatLng dest_lat_lng = new LatLng(Double.parseDouble(destination[0]), Double.parseDouble(destination[1]));
        String url = getPlacesUrl(Double.parseDouble(destination[0]), Double.parseDouble(destination[1]));
        Log.d("Cafe Url: ", url);
        CafeActivity.DownloadTask downloadTask = new CafeActivity.DownloadTask( );
        // Start downloading JSON data from Google Directions API.
        downloadTask.execute( url );

    }

    private String getPlacesUrl(Double lat, Double lng) {
        String Web_Key = getResources().getString(R.string.google_web_key);
        // Sensor enabled
        String sensor = "sensor=true";
        // Building the parameters to the web service
        //String parameters = "location=" + latitude + "," + longitude + "&radius=500&type=food&name=cruise&key=" + API_Key;
        String parameters = "location=" + lat + "," + lng + "&radius=500&type=cafe"+"&"+sensor+"&key=" + Web_Key;
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
            CafeActivity.ParserTask parserTask = new CafeActivity.ParserTask( );
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

                Log.d("Nearby Cafe: ", placesData.toString());
                return placesData;
            }
        }  // End of doInBackground

        @Override
        protected void onPostExecute(List<HashMap<String, String>> cafeNearby) {
            super.onPostExecute(cafeNearby);
            ArrayList<String> distance = new ArrayList<>();
            for (int i = 0; i < cafeNearby.size(); i++) {

                int Radius = 6371;// radius of earth in Km
                HashMap<String, String> googlePlace = cafeNearby.get(i);
                double lat = Double.parseDouble(googlePlace.get("lat"));
                double lng = Double.parseDouble(googlePlace.get("lng"));

                double dLat = Math.toRadians(Double.parseDouble(destination[0]) - lat);
                double dLon = Math.toRadians(Double.parseDouble(destination[1]) - lng);
                double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                        + Math.cos(Math.toRadians(lat))
                        * Math.cos(Math.toRadians(Double.parseDouble(destination[0]))) * Math.sin(dLon / 2)
                        * Math.sin(dLon / 2);
                double c = 2 * Math.asin(Math.sqrt(a));
                double valueResult = Radius * c;
                double km = valueResult / 1;
                DecimalFormat newFormat = new DecimalFormat("####");
                int kmInDec = Integer.valueOf(newFormat.format(km));
                double meter = valueResult % 1000;
                int meterInDec = Integer.valueOf(newFormat.format(meter));
                Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                        + " Meter   " + meterInDec);
            }
        }
    }    // End of ParserTask
}

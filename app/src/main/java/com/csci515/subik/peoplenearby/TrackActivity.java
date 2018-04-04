package com.csci515.subik.peoplenearby;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Connection;
import android.util.Log;
import android.widget.Toast;

import com.csci515.subik.peoplenearby.myApplication.MyApplication;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TrackActivity extends FragmentActivity implements LocationListener {

    String[] destination = null;
    static ArrayList<String> from = new ArrayList<>();
    static ArrayList<String> to = new ArrayList<>();
    static boolean color = false, cafe = false;
    GoogleMap mGoogleMap;
    //RequestHandler requestHandler = new RequestHandler();
    String friend_id = null;
    String my_id = null;
    LatLng dest_location = null;
    int i = 0;
    LatLng cafeto_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) throws SecurityException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        Intent intent = getIntent();
        friend_id = intent.getStringExtra("friendId");
        String dest = intent.getStringExtra("destination");
        destination = dest.split("/");
        MyApplication myApplication = (MyApplication) getApplication();
        my_id = myApplication.getSavedValue("Id");
        String isavailableCafe = intent.getStringExtra("cafe");

        new RequestHandler().execute("Track", friend_id, my_id);
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        if (status != ConnectionResult.SUCCESS){
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
            finish();
        }

        if (!isavailableCafe.equals("null")) {
            String[] temp = isavailableCafe.split(",");
            dest_location = new LatLng(Double.parseDouble(temp[0]), Double.parseDouble(temp[1]));
            cafeto_location = new LatLng(Double.parseDouble(destination[0]), Double.parseDouble(destination[1]));
            /*final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 1s = 1000ms
                    drawMarker(cafeto_location, "cafe");
                }
            }, 1000);*/

        }
        else
            dest_location = new LatLng(Double.parseDouble(destination[0]), Double.parseDouble(destination[1]));


        Log.i("LatLng of set program: ", dest_location.toString());


        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.trackMap);
        mGoogleMap = supportMapFragment.getMap();
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);

        //drawMarker(dest_location, "Destination");

        if (location != null){
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(provider, 20000, 0, this);



    }

    public static void parseJson(String json_people) {
        try {

            //from.clear();
            JSONObject jsonRootObj = new JSONObject(json_people);
            JSONArray data = jsonRootObj.getJSONArray("location");
            /*if (data.length()>0){
                from.clear();
                to.clear();
            }*/
            for (int i = 0; i < data.length(); i++) {

                from.clear();
                to.clear();
                JSONObject jsonObject = data.getJSONObject(i);

                String from_name = jsonObject.optString("Fname");
                String to_name = jsonObject.optString("Tname");
                //String from_id = jsonObject.optString("FCusId");
                //String to_id = jsonObject.optString("TCusId");
                String flatitude = jsonObject.optString("FLatitude");
                String flongitude = jsonObject.optString("FLongitude");
                String tlatitude = jsonObject.optString("TLatitude");
                String tlongitude = jsonObject.optString("TLongitude");

                from.add(from_name); from.add(flatitude); from.add(flongitude);
                to.add(to_name); to.add(tlatitude); to.add(tlongitude);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        i++;
        mGoogleMap.clear();
        double mLatitude = location.getLatitude();
        double mLongitude = location.getLongitude();
        LatLng point = new LatLng( mLatitude, mLongitude );
        if (i == 1) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(point));
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        }

        new RequestHandler().execute("Track", friend_id, my_id);
        new DatabaseCustomer(getApplicationContext(), null).execute("insertLatLong", String.valueOf(mLatitude),
                String.valueOf(mLongitude),my_id);

        drawMarker(point, "Me");
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 2s = 2000ms

                LatLng friends_point = new LatLng(Double.parseDouble(from.get(1)), Double.parseDouble(from.get(2)));
                drawMarker(friends_point, "Friend");
                drawMarker(cafeto_location, "cafe");
                drawMarker(dest_location, "Destination");
            }
        }, 2000);
    }

    private void drawMarker(LatLng point, String key) {

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(point);
        //LatLng dest = dest_location;
        LatLng dest = cafeto_location;
        if (key.equals("Destination")){
            markerOptions.title(destination[2]);
            markerOptions.snippet(destination[3]);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        }else if (key.equals("Friend")){
            markerOptions.title(from.get(0));
            markerOptions.snippet("On thier way");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

            //LatLng origin = point;
            //LatLng dest = new LatLng(Double.parseDouble(destination[0]), Double.parseDouble(destination[1]));

            color = false;
            /// Building the URL including Google Directions API
            String url = getDirectionsUrl( point, dest );
            //Log.d("Url: ", url);
            Log.d("Point Friend: ", point.toString());
            Log.d("Point Friend Dest: ", dest.toString());
            DownloadTask downloadTask = new DownloadTask( );
            // Start downloading JSON data from Google Directions API.
            downloadTask.execute( url );
        }else if (key.equals("Me")){
            //LatLng origin = point;
            //LatLng dest = new LatLng(Double.parseDouble(destination[0]), Double.parseDouble(destination[1]));
            color = true;
            /// Building the URL including Google Directions API
            String url = getDirectionsUrl( point, dest );
            //Log.d("Url: ", url);
            Log.d("Point Me: ", point.toString());
            Log.d("Point Me Dest: ", dest.toString());
            DownloadTask downloadTask = new DownloadTask( );
            // Start downloading JSON data from Google Directions API.
            downloadTask.execute( url );
        }else if (key.equals("cafe")){
            markerOptions.title("Meeting Point");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            //String url = getDirectionsUrl( point, dest );
            String url = getDirectionsUrl( dest_location, dest );
            //Log.d("Url: ", url);
            Log.d("Point Final: ", point.toString());
            Log.d("Point Cafe: ", dest.toString());
            cafe = true;
            DownloadTask downloadTask = new DownloadTask( );
            // Start downloading JSON data from Google Directions API.
            downloadTask.execute( url );
        }
        mGoogleMap.addMarker(markerOptions);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private static class RequestHandler extends AsyncTask<String, Void, String> {
        /*String status = null;
        String app = null;*/
        @Override
        protected String doInBackground(String... args) {
            //status = args[0];
            String link = "http://undcemcs02.und.edu/~subik.pokharel/515/1/RequestHandler.php";
            String data = null;
            try {
                // Connect to the server.
                URL url = new URL(link);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);

                data = URLEncoder.encode("key", "UTF-8") + "=";
                data += URLEncoder.encode(args[0], "UTF-8") + "&";
                data += URLEncoder.encode("from", "UTF-8") + "=";
                data += URLEncoder.encode(args[1], "UTF-8") + "&";
                data += URLEncoder.encode("to", "UTF-8") + "=";
                data += URLEncoder.encode(args[2], "UTF-8");

                Log.i("Data to database", args[1]);
                OutputStreamWriter wr = new OutputStreamWriter(
                        conn.getOutputStream());
                wr.write(data);
                wr.flush();
                // Read server response.
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                Log.d("Output: ", sb.toString());
                return sb.toString();
            } catch (Exception e) {
                //Log.d("Exception connecting: " , e.getMessage());
                return new String("Exception while connecting: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            parseJson(s);
        }
    }

    private String getDirectionsUrl( LatLng origin, LatLng dest ) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest  = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        // Output format
        String output = "json";
        // Building the URL for the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }  // End of getDirectionsUrl


    // A method to download JSON data from URL
    private String downloadUrl( String strUrl ) throws IOException {
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
            Log.d( "Exception download url", e.toString( ) );
        }
        finally {
            iStream.close( );
            urlConnection.disconnect( );
        }
        return data;
    }  // End of downloadUrl

    // A class to download data from Google Directions URL
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground( String... url ) {
            // For storing data from web service
            String data = "";
            try {
                // Fetching the data from web service
                data = downloadUrl( url[0] );
            }
            catch( Exception e ) {
                Log.d( "Background Task", e.toString( ) );
            }
            return data;
        }  // End of doInBackground


        // Executes in UI thread, after the execution of doInBackground( )
        @Override
        protected void onPostExecute( String result ) {
            super.onPostExecute( result );
            ParserTask parserTask = new ParserTask( );
            // Invokes the thread for parsing the JSON data.
            parserTask.execute( result );
        }  // End of onPostExecute

    }  // End of DownloadTask

    // A class to parse the Google Directions in JSON format
    private class ParserTask extends AsyncTask<String, Integer,
            List<List<HashMap<String,String>>> > {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>>
        doInBackground( String... jsonData ) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject( jsonData[0] );
                DirectionsJSONParser parser = new DirectionsJSONParser( );
                // Starts parsing data.
                routes = parser.parse( jObject );
            }
            catch( Exception e ) {
                e.printStackTrace( );
            }
            return routes;
        }  // End of doInBackground


        // Executes in UI thread, after the parsing process.
        @Override
        protected void onPostExecute(
                List<List<HashMap<String, String>>> result ) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for ( int i=0; i<result.size( ); i++ ) {
                points = new ArrayList<LatLng>( );
                lineOptions = new PolylineOptions( );
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get( i );

                // Fetching all the points in i-th route
                for ( int j=0; j<path.size( ); j++ ) {
                    HashMap<String,String> point = path.get( j );
                    double lat = Double.parseDouble( point.get( "lat" ) );
                    double lng = Double.parseDouble( point.get( "lng" ) );
                    LatLng position = new LatLng( lat, lng );
                    points.add( position );
                }  // End of inner for

                // Adding all the points in the route to LineOptions
                lineOptions.addAll( points );
                lineOptions.width( 2 );
                if (!cafe){
                    if (!color)
                        lineOptions.color( Color.BLUE );

                    else {
                        lineOptions.color(Color.RED);
                        color = false;
                    }
                }else{
                    if (!color) {
                        lineOptions.color(Color.MAGENTA);
                        cafe = false;
                    }else
                        lineOptions.color(Color.GRAY);
                }

            }  // End of outer for
            if (lineOptions != null)
                // Drawing polyline in the Google Map for the i-th route
                mGoogleMap.addPolyline( lineOptions );
        }  // End of onPostExecute

    }  // End of ParserTask
}

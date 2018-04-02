package com.csci515.subik.peoplenearby;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.csci515.subik.peoplenearby.myApplication.MyApplication;
import com.csci515.subik.peoplenearby.parsing.Customer;
import com.csci515.subik.peoplenearby.parsing.LatLong;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class HomePageActivity extends FragmentActivity implements LocationListener{

    static GoogleMap mGoogleMap;
    static ArrayList<LatLong> mPeoplePosition;
    static ArrayList<Customer> mPeople;
    double mLatitude  = 0;
    double mLongitude = 0;
    MyApplication myApplication;
    String user_id;
    TextView tv;
    static Context context;
    Button viewAppointment;
    @Override
    protected void onCreate(Bundle savedInstanceState) throws SecurityException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        context = HomePageActivity.this;
        myApplication = (MyApplication) getApplication();
        user_id = myApplication.getSavedValue("Id");
        //Toast.makeText(getApplicationContext(), user_id, Toast.LENGTH_SHORT).show();

        viewAppointment = findViewById(R.id.btnViewAppointment);
        viewAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, AppointmentActivity.class);
                startActivity(intent);
            }
        });

        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        if (status != ConnectionResult.SUCCESS){
            // Google Play Services are not available.
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
            finish();
        }
        // Getting reference to SupportMapFragment of the activity_main
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById( R.id.peopleMap );
        // Getting Map for the SupportMapFragment
        mGoogleMap = supportMapFragment.getMap();
        // Enables MyLocation Button in the Map.
        mGoogleMap.setMyLocationEnabled(true);

        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        /*mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);
        mGoogleMap.getUiSettings().setScrollGesturesEnabled(false);
        mGoogleMap.getUiSettings().setTiltGesturesEnabled(false);*/

        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService( LOCATION_SERVICE );
        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();
        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);
        // Getting Current Location From GPS
        Location location = locationManager.getLastKnownLocation( provider );

        if ( location != null){
            onLocationChanged( location );
            DownloadUsers dowloadUsers = new DownloadUsers( );
            // Start downloading JSON data from Google Directions API.
            dowloadUsers.execute( "getUsers", user_id);
        }
        //check every 2 min if the location has changed or not
        locationManager.requestLocationUpdates(provider, 200000, 0, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        //setLatLong(mLatitude, mLongitude);
        new DatabaseCustomer(getApplicationContext(), tv).execute("insertLatLong", String.valueOf(mLatitude),
                String.valueOf(mLongitude),user_id);
        final LatLng points = new LatLng(mLatitude, mLongitude);
        /*mGoogleMap.addMarker(new MarkerOptions().position(points)
                .title("Current Location")
                .snippet("You are around UND memorial union."));*/
        /*Circle circle = mGoogleMap.addCircle(new CircleOptions()
                .center(new LatLng(mLatitude, mLongitude))
                .radius(1)
                .strokeColor(Color.RED)
                .fillColor(Color.BLUE));*/
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 0.5s = 500ms
                mGoogleMap.moveCamera( CameraUpdateFactory.newLatLng(points));
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            }
        }, 1000);



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

    private static class DownloadUsers extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... args) {
            String link = "http://undcemcs02.und.edu/~subik.pokharel/515/1/Customer.php";
            try{
                // Connect to the server.
                URL url = new URL( link );
                URLConnection conn = url.openConnection( );
                conn.setDoOutput( true );

                String data = URLEncoder.encode( "cus_id", "UTF-8" ) + "=";
                data += URLEncoder.encode( args[1],   "UTF-8" ) + "&";
                data += URLEncoder.encode( "key", "UTF-8" ) + "=";
                data += URLEncoder.encode( args[0],   "UTF-8" );
                Log.d("data sending for user: ", data);
                OutputStreamWriter wr = new OutputStreamWriter(
                        conn.getOutputStream( ) );
                wr.write( data );
                wr.flush( );


                // Read server response.
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader( conn.getInputStream( ) ));
                StringBuilder sb = new StringBuilder( );
                String      line;
                while (( line = reader.readLine( ) ) != null ) {
                    sb.append( line );
                    break;
                }
                Log.d("Output Json: ", sb.toString());
                return sb.toString( );
            }catch (Exception e){
                return new String( "Exception while connecting: " + e.getMessage( ) );
            }
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonRootObj = new JSONObject(result);
                JSONArray people = jsonRootObj.getJSONArray("people");
                mPeoplePosition = new ArrayList<>(people.length());
                mPeople = new ArrayList<>(people.length());

                for (int i = 0; i < people.length(); i++) {
                    JSONObject jsonObject = people.getJSONObject(i);

                    int id = Integer.parseInt(jsonObject.optString("ID"));
                    int cus_id = Integer.parseInt(jsonObject.optString("CusId"));
                    String latitude = jsonObject.optString("Latitude");
                    String longitude = jsonObject.optString("Longitude");
                    String name = jsonObject.optString("Name");
                    String age = jsonObject.optString("Age");
                    String gender = jsonObject.optString("Gender");
                    String email = jsonObject.optString("Email");
                    int status = Integer.parseInt(jsonObject.optString("Status"));
                    mPeoplePosition.add(new LatLong(id, cus_id,latitude, longitude));
                    //Log.d("name: ", name);
                    mPeople.add(new Customer(cus_id,Integer.parseInt(age), name, gender, email, status));

                    //    Toast.makeText(GroceryJson.this,  groceryArrayList+ "  Clicked Glocery", Toast.LENGTH_LONG).show();
                }
                drawPeople(mPeoplePosition, mPeople);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void drawPeople(ArrayList<LatLong> mPeoplePosition, ArrayList<Customer> mPeople) {
            //Log.d("LATLANG: ", String.valueOf(mPeoplePosition.size()));
            for (int i = 0; i < mPeoplePosition.size(); i++) {
                MarkerOptions markerOptions = new MarkerOptions();
                double lat = Double.parseDouble(mPeoplePosition.get(i).getLatitude());
                double lng = Double.parseDouble(mPeoplePosition.get(i).getLongitude());
                String name = String.valueOf(mPeople.get(i).getName());
                String age = String.valueOf(mPeople.get(i).getAge());
                String gender = String.valueOf(mPeople.get(i).getGender());
                String cus_id = String.valueOf(mPeoplePosition.get(i).getCus_id());
                final int status = mPeople.get(i).getStatus();
                LatLng latLng = new LatLng(lat, lng);
                if (status == 0){
                    String title = "Name: "+name + "\nAge: "+age+"\nGender: "+gender;
                    markerOptions.position(latLng);
                    markerOptions.title(title);
                    markerOptions.snippet(cus_id);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }else {
                    String title = "Status: BOOKED \nSeems like the person is going out with someone else. \nSorry for the inconvenience.";
                    markerOptions.position(latLng);
                    markerOptions.title(title);
                    markerOptions.snippet("Booked");
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }

                mGoogleMap.addMarker(markerOptions);

                mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
                {

                    @Override
                    public boolean onMarkerClick(final Marker arg0) {
                        //if(arg0.getTitle().equals("Current Location")){
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("You are about to set an Appointment with...");
                            // Setting Dialog Message
                            builder.setMessage(arg0.getTitle());
                            // Setting Positive "Yes" Btn
                            builder.setPositiveButton("Select Resturant", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
                                    progressDialog.setIndeterminate(true);
                                    progressDialog.setMessage("Fetching Resturants near by...");
                                    progressDialog.show();
                                    new android.os.Handler().postDelayed(new Runnable() {
                                        public void run() {
                                            String status = arg0.getSnippet();
                                            if (!status.equals("Booked")){
                                                Intent intent = new Intent(context, GetNearbyPlacesActivity.class);
                                                intent.putExtra("GoingOutWithID", arg0.getSnippet());
                                                intent.putExtra("GoingOutWithDetails", arg0.getTitle());
                                                context.startActivity(intent);
                                            }else {
                                                Toast.makeText(context, "Sorry we cannot process your request at this moment...", Toast.LENGTH_LONG).show();
                                            }

                                            //Toast.makeText(context, "You will be out...", Toast.LENGTH_LONG).show();
                                            progressDialog.dismiss();
                                        }
                                    }, 1000);

                                }
                            });

                            // Setting Negative "NO" Btn
                            builder.setNegativeButton("Discard",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
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
                //Log.d("LATLANG: ", String.valueOf(lat)+" "+String.valueOf(lng));
            }
        }
    }
}


//SELECT * FROM tbl_location WHERE cus_id NOT IN (2) 
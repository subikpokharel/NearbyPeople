package com.csci515.subik.peoplenearby;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.csci515.subik.peoplenearby.adapter.AppointmentAdapter;
import com.csci515.subik.peoplenearby.myApplication.MyApplication;
import com.csci515.subik.peoplenearby.parsing.Appointment;

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

public class AppointmentActivity extends AppCompatActivity implements AppointmentAdapter.DataTransferInterface {

    TextView request_sent, request_received, no_data;
    ListView listView;
    MyApplication myApplication;
    View enable_view;
    static Context appContext;
    AppointmentAdapter appointmentAdapter = null;
    static ArrayList<Appointment> appointments = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);
        appContext = AppointmentActivity.this;
        init();
    }

    private void init() {

        request_sent = findViewById(R.id.tvSent);
        request_received = findViewById(R.id.tvReceived);
        no_data = findViewById(R.id.nodata);
        listView = findViewById(R.id.lv_appointments);
        myApplication = (MyApplication) getApplication();
        appointments = new ArrayList<Appointment>();
        request_sent.setOnClickListener(listner);
        request_received.setOnClickListener(listner);
        getData("sent");

    }

    View.OnClickListener listner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            enable_view = v;
            switch (v.getId()){

                case R.id.tvSent:
                    //Toast.makeText(AppointmentActivity.this, appointmentAdapter.getCount(), Toast.LENGTH_LONG).show();
                    //listView.removeAllViews();
                    request_sent.setTextColor(getResources().getColor(R.color.black));
                    request_received.setTextColor(getResources().getColor(R.color.colorIcons));
                    request_sent.setTypeface(Typeface.DEFAULT_BOLD);
                    request_received.setTypeface(Typeface.DEFAULT);
                    no_data.setVisibility(v.GONE);
                    listView.setVisibility(v.GONE);
                    getData("sent");
                    break;


                case R.id.tvReceived:
                    //Toast.makeText(AppointmentActivity.this, appointmentAdapter.getCount(), Toast.LENGTH_LONG).show();
                    //listView.removeAllViews();
                    request_received.setTextColor(getResources().getColor(R.color.black));
                    request_sent.setTextColor(getResources().getColor(R.color.colorIcons));
                    request_received.setTypeface(Typeface.DEFAULT_BOLD);
                    request_sent.setTypeface(Typeface.DEFAULT);
                    no_data.setVisibility(v.GONE);
                    listView.setVisibility(v.GONE);
                    getData("received");
                    break;
            }
        }
    };

    private void getData(String key) {

        //String id = "6";
        final String param = key;
        String id = myApplication.getSavedValue("Id");
        final GetAppointment appointment = new GetAppointment();
        appointment.execute(key, id);


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                if (appointments.size() != 0){
                    no_data.setVisibility(enable_view.GONE);
                    listView.setVisibility(enable_view.VISIBLE);
                    appointmentAdapter = new AppointmentAdapter(AppointmentActivity.this,R.layout.activity_container_appointment,appointments, param);
                    listView.setAdapter(appointmentAdapter);
                }else {
                    no_data.setVisibility(enable_view.VISIBLE);
                    listView.setVisibility(enable_view.GONE);
                }
            }
        }, 1500);

    }

    @Override
    public void makeHyperlink(TextView textView) {
        SpannableStringBuilder ssb = new SpannableStringBuilder( );
        ssb.append( textView.getText( ) );
        ssb.setSpan( new URLSpan("#"), 0, ssb.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        textView.setText( ssb, TextView.BufferType.SPANNABLE );
    }

    @Override
    public void clickHyperlink(final Appointment data, String job) {
        /*Toast.makeText(getApplicationContext(), job, Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(), String.valueOf(cus_id), Toast.LENGTH_LONG).show();*/
        if (job.equals("view")){
            AlertDialog.Builder builder = new AlertDialog.Builder(appContext);
            builder.setTitle("Accept/Reject Request");
            // Setting Dialog Message
            builder.setMessage("Request from: "+data.getFrom_name()+"\n"+data.getResturant_name()+"\n"+data.getResturant_address());
            builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    final ProgressDialog progressDialog = new ProgressDialog(appContext, R.style.AppTheme_Dark_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Please Wait...");
                    progressDialog.show();
                    new android.os.Handler().postDelayed(new Runnable() {
                        public void run() {
                            //send to database
                            String id = myApplication.getSavedValue("Id");
                            RequestHandler requestHandler = new RequestHandler();
                            requestHandler.execute("Accept", String.valueOf(data.getCus_id()), id );
                            //refresh this page
                            Toast.makeText(appContext, "Request accepted...", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    }, 1000);

                }
            });

            // Setting Negative "NO" Btn
            builder.setNegativeButton("Reject",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            final ProgressDialog progressDialog = new ProgressDialog(appContext, R.style.AppTheme_Dark_Dialog);
                            progressDialog.setIndeterminate(true);
                            progressDialog.setMessage("Please Wait...");
                            progressDialog.show();
                            new android.os.Handler().postDelayed(new Runnable() {
                                public void run() {
                                    //send to database
                                    String id = myApplication.getSavedValue("Id");
                                    RequestHandler requestHandler = new RequestHandler();
                                    requestHandler.execute("Reject", String.valueOf(data.getCus_id()), id );
                                    Toast.makeText(appContext, "Request rejected...", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            }, 1500);
                        }
                    });

            // Showing Alert Dialog
            builder.show();
        }else if (job.equals("track")){
            //send to tracking page with details of appointments
            //String id = myApplication.getSavedValue("Id");

            Intent intent = new Intent(appContext, CafeActivity.class);
            intent.putExtra("destination", data.toString());
            intent.putExtra("friendId", String.valueOf(data.getCus_id()));
            appContext.startActivity(intent);


            //RequestHandler requestHandler = new RequestHandler();
            //requestHandler.execute("Track", String.valueOf(data.getCus_id()), id, data.toString());
            //Toast.makeText(getApplicationContext(),"Where is: "+ String.valueOf(data.getCus_id()), Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(),"Waiting for: "+ String.valueOf(data.getCus_id()), Toast.LENGTH_LONG).show();
        }
    }


    private static class GetAppointment extends AsyncTask<String, Void, String>{

        private String status;
        @Override
        protected String doInBackground(String... strings) {

            status = strings[0];
            try {
                String link = "http://undcemcs02.und.edu/~subik.pokharel/515/1/Customer.php";

                // Connect to the server.
                URL url = new URL( link );
                URLConnection conn = url.openConnection( );
                conn.setDoOutput( true );

                String data = URLEncoder.encode( "key", "UTF-8" ) + "=";
                data += URLEncoder.encode( status,   "UTF-8" ) + "&";
                data += URLEncoder.encode( "Id", "UTF-8" ) + "=";
                data += URLEncoder.encode( strings[1],   "UTF-8" );

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
                Log.d("Output: ", sb.toString());
                return sb.toString();

            }catch (Exception e){
                Log.d("Exception connecting: " , e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                if (result != null){
                    super.onPostExecute(result);
                    JSONObject jsonRootObj = new JSONObject(result);
                    JSONArray data = jsonRootObj.getJSONArray("details");
                    appointments = new ArrayList<>(data.length());
                    //Log.d("Data length: ", String.valueOf(data.length()));
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonObject = data.getJSONObject(i);

                        String from_name = jsonObject.optString("From");
                        String to_name = jsonObject.optString("To");
                        String time = jsonObject.optString("Time");
                        String latitude = jsonObject.optString("Latitude");
                        String longitude = jsonObject.optString("Longitude");
                        String resturant_name = jsonObject.optString("Res_Name");
                        String resturant_address = jsonObject.optString("Address");
                        int status = Integer.parseInt(jsonObject.optString("Status"));
                        int cus_id = Integer.parseInt(jsonObject.optString("CusId"));

                        appointments.add(new Appointment(from_name, to_name, time, latitude, longitude, resturant_name, resturant_address, status, cus_id));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
                /*if (args[0].equals("Track")){
                    app = args[3];
                    //Toast.makeText(appContext, app, Toast.LENGTH_LONG).show();
                    //Log.d("Appointment: ", app);
                }*/
                // Connect to the server.
                URL url = new URL( link );
                URLConnection conn = url.openConnection( );
                conn.setDoOutput( true );

                data = URLEncoder.encode( "key", "UTF-8" ) + "=";
                data += URLEncoder.encode( args[0],   "UTF-8" ) + "&";
                data += URLEncoder.encode( "from", "UTF-8" ) + "=";
                data += URLEncoder.encode( args[1],   "UTF-8" ) + "&";
                data += URLEncoder.encode( "to", "UTF-8" ) + "=";
                data += URLEncoder.encode( args[2],   "UTF-8" );


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
                Log.d("Output: ", sb.toString());
                return sb.toString( );
            }catch (Exception e){
                //Log.d("Exception connecting: " , e.getMessage());
                return  new String( "Exception while connecting: " + e.getMessage( ) );
            }
        }

       @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
           if(s.equals("1")){
               //Log.d("Query Success: ", s);
               Intent intent = new Intent(appContext, AppointmentActivity.class);
               appContext.startActivity(intent);
           }
            /*if (status.equals("Track")){
                //Appointment appointment = app;
                Intent intent = new Intent(appContext, TrackActivity.class);
                intent.putExtra("JsonData", s);
                intent.putExtra("destination", app);
                appContext.startActivity(intent);
            }else{

            }*/

        }
    }
}

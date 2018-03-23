package com.csci515.subik.peoplenearby;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class AppointmentActivity extends AppCompatActivity {

    TextView request_sent, request_received, no_data;
    ListView listView;
    MyApplication myApplication;
    View enable_view;
    static ArrayList<Appointment> appointments = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);
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

                    //listView.removeAllViews();
                    request_sent.setTextColor(getResources().getColor(R.color.black));
                    request_received.setTextColor(getResources().getColor(R.color.colorIcons));
                    request_sent.setTypeface(Typeface.DEFAULT_BOLD);
                    request_received.setTypeface(Typeface.DEFAULT);
                    no_data.setVisibility(v.GONE);
                    getData("sent");
                    break;


                case R.id.tvReceived:

                    //listView.removeAllViews();
                    request_received.setTextColor(getResources().getColor(R.color.black));
                    request_sent.setTextColor(getResources().getColor(R.color.colorIcons));
                    request_received.setTypeface(Typeface.DEFAULT_BOLD);
                    request_sent.setTypeface(Typeface.DEFAULT);
                    no_data.setVisibility(v.GONE);
                    getData("received");
                    break;
            }
        }
    };

    private void getData(String key) {

        //String id = "6";
        String id = myApplication.getSavedValue("Id");
        final GetAppointment appointment = new GetAppointment();
        appointment.execute(key, id);


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                Log.d("Data length: ", String.valueOf(appointments.size()));
                if (appointments.size() != 0){
                    no_data.setVisibility(enable_view.GONE);
                    AppointmentAdapter appointmentAdapter = new AppointmentAdapter(AppointmentActivity.this,R.layout.activity_container_appointment,appointments);
                    listView.setAdapter(appointmentAdapter);
                    //Toast.makeText(AppointmentActivity.this, "Empty result", Toast.LENGTH_LONG).show();
                }else
                    //Toast.makeText(AppointmentActivity.this, "Not Empty result", Toast.LENGTH_LONG).show();
                    no_data.setVisibility(enable_view.VISIBLE);
            }
        }, 1500);

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

                        appointments.add(new Appointment(from_name, to_name, time, latitude, longitude, resturant_name, resturant_address, status));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

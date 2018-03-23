package com.csci515.subik.peoplenearby;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.csci515.subik.peoplenearby.myApplication.MyApplication;
import com.csci515.subik.peoplenearby.parsing.Appointment;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class AppointmentActivity extends AppCompatActivity {

    TextView request_sent, request_received;
    ListView listView;
    MyApplication myApplication;
    ArrayList<Appointment> appointments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);
        init();
    }

    private void init() {

        request_sent = findViewById(R.id.tvSent);
        request_received = findViewById(R.id.tvReceived);
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
            switch (v.getId()){

                case R.id.tvSent:

                    request_sent.setTextColor(getResources().getColor(R.color.black));
                    request_received.setTextColor(getResources().getColor(R.color.colorIcons));
                    request_sent.setTypeface(Typeface.DEFAULT_BOLD);
                    request_received.setTypeface(Typeface.DEFAULT);
                    getData("sent");
                    break;


                case R.id.tvReceived:

                    request_received.setTextColor(getResources().getColor(R.color.black));
                    request_sent.setTextColor(getResources().getColor(R.color.colorIcons));
                    request_received.setTypeface(Typeface.DEFAULT_BOLD);
                    request_sent.setTypeface(Typeface.DEFAULT);
                    getData("received");
                    break;
            }
        }
    };

    private void getData(String key) {

        String id = myApplication.getSavedValue("Id");
        GetAppointment appointment = new GetAppointment();
        appointment.execute(key, id);
    }



    private static class GetAppointment extends AsyncTask<String, Void, ArrayList<Appointment>>{

        private String status;
        @Override
        protected ArrayList<Appointment> doInBackground(String... strings) {

            status = strings[0];
            try {
                String link = "http://undcemcs02.und.edu/~subik.pokharel/515/1/Customer.php";

                // Connect to the server.
                URL url = new URL( link );
                URLConnection conn = url.openConnection( );
                conn.setDoOutput( true );

                String data = URLEncoder.encode( "key", "UTF-8" ) + "=";
                data += URLEncoder.encode( strings[0],   "UTF-8" ) + "&";
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

            }catch (Exception e){
                Log.d("Exception connecting: " , e.getMessage());
            }


            return null;
        }
    }
}

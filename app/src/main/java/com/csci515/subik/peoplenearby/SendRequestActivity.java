package com.csci515.subik.peoplenearby;


import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.csci515.subik.peoplenearby.myApplication.MyApplication;

import java.util.Calendar;


public class SendRequestActivity extends AppCompatActivity {

    String[] array_details;
    TextView name, age, gender, resturant, address, time, tvStatus;
    Button btn_confirm;
    MyApplication myApplication;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request);
        Bundle extras = getIntent().getExtras();
        array_details = extras.getStringArray("Array");
        init();
        myApplication = (MyApplication) getApplication();
        /*for (int i =0; i<array_details.length; i++)
            Log.d("Received intent: ", array_details[i]);*/


    }

    private void init() {
        name = findViewById(R.id.person_name);
        age = findViewById(R.id.person_age);
        gender = findViewById(R.id.person_gender);
        resturant = findViewById(R.id.resturant_name);
        address = findViewById(R.id.resturant_address);
        time = findViewById(R.id.text_time);
        btn_confirm = findViewById(R.id.btnConfirm);
        tvStatus = findViewById(R.id.text_status);
        tvStatus.setText("0");


        name.setText(array_details[1]);
        age.setText(array_details[2]);
        gender.setText(array_details[3]);
        resturant.setText(array_details[6]);
        address.setText(array_details[7]);
    }

    public void setTime(View view) {

        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(SendRequestActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                time.setText( "Time of meeting- "+String.format("%02d:%02d",selectedHour, selectedMinute) );
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
        btn_confirm.setEnabled(true);
    }

    public void confirmAppointment(View view) {
        String dest_id = array_details[0];
        String my_id = myApplication.getSavedValue("Id");
        String temp = time.getText().toString();
        String time_hrs = temp.substring(temp.lastIndexOf("-")+2);//lastIndexOf(": ")+2);
        new DatabaseCustomer(getApplicationContext(), tvStatus).execute("insertAppointment", my_id, dest_id, time_hrs,
                array_details[4], array_details[5], array_details[6], array_details[7]);

        final ProgressDialog progressDialog = new ProgressDialog(SendRequestActivity.this,R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Sending your Request...");
        progressDialog.show();
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {

                        int db_status = Integer.parseInt(tvStatus.getText().toString());
                        if (db_status == 1) {
                            Toast.makeText(getApplicationContext(), "Request Successfully Sent!", Toast.LENGTH_LONG).show();
                        }else {
                            //either the entered username or password doesnt match
                            Toast.makeText(getApplicationContext(), "You have already set a request to "+name.getText()+".", Toast.LENGTH_LONG).show();
                        }
                        Intent intent = new Intent(SendRequestActivity.this, HomePageActivity.class);
                        startActivity(intent);
                        progressDialog.dismiss();
                    }
                }, 3000);
    }
}

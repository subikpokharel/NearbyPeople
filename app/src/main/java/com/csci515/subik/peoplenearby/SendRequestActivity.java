package com.csci515.subik.peoplenearby;


import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;


public class SendRequestActivity extends AppCompatActivity {

    String[] array_details;
    TextView name, age, gender, resturant, address, time;
    Button btn_confirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request);
        Bundle extras = getIntent().getExtras();
        array_details = extras.getStringArray("Array");
        init();

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


        name.setText(array_details[1]);
        age.setText(array_details[2]);
        gender.setText(array_details[3]);
        resturant.setText(array_details[6]);
        address.setText(array_details[7]);
    }

    public void setTime(View view) {

        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY) - 5;
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(SendRequestActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                time.setText( "Time of meeting: "+String.format("%02d : %02d",selectedHour, selectedMinute) );
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
        btn_confirm.setEnabled(true);
    }
}

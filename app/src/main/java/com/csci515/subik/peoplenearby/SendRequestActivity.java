package com.csci515.subik.peoplenearby;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class SendRequestActivity extends AppCompatActivity {

    String[] array_details;
    TextView name, age, gender, resturant, address, time;
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


        name.setText(array_details[1]);
        age.setText(array_details[2]);
        gender.setText(array_details[3]);
        resturant.setText(array_details[6]);
        address.setText(array_details[7]);
    }
}

package com.csci515.subik.peoplenearby;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.progressBarText);
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textView.setText(R.string.onStartWait);
                textView.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale));
            }
        }, 1000);


        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (checkNetwork()) {
                    gotoLoginActivity();
                } else {
                    Toast.makeText(MainActivity.this, "No Internet Access", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(i);
                }

            }
        }, 1000);
    }

    private void gotoLoginActivity() {
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    //Check for Internet
    private boolean checkNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }
}

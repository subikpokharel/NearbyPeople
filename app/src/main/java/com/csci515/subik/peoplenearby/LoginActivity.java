package com.csci515.subik.peoplenearby;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.csci515.subik.peoplenearby.myApplication.MyApplication;

public class LoginActivity extends AppCompatActivity {

    EditText editEmail;
    TextView tv;
    Button btnLogin;
    TextView link_signup;
    private static final int REQUEST_SIGNUP = 0;
    MyApplication myApplication;
    int cus_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        //editEmail.setText("sami@und.edu");
    }

    private void init() {
        editEmail = findViewById(R.id.input_email);
        btnLogin = findViewById(R.id.btn_login);
        tv = findViewById(R.id.cusId);
        tv.setText("0");
        myApplication = (MyApplication) getApplication();
        link_signup =  findViewById(R.id.link_signup);
        makeTextViewHyperlink(link_signup);
    }

    public void signUp(View view) {
        //Start SignUp Activity
        Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivityForResult(i, REQUEST_SIGNUP);
    }

    //once signup is completed, the result comes here
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                new android.os.Handler().postDelayed(new Runnable() {
                    public void run() {
                        //after signup, presently, after 5s delay subik() is called.
                        Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }, 100);
            }
        }
    }

    public void login(View view) {
        if (!validate()) {
            //if the entered details are not valid, goto the following function
            onLoginFailed();
            return;
        }
        //else if the endered data is valid, perform following
        btnLogin.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
        final String email = editEmail.getText().toString();

        cus_id = 0;
        String status = "Login";

        new DatabaseCustomer(getApplicationContext(), tv).execute(status, email);

        new android.os.Handler().postDelayed(new Runnable() {
            public void run() {

                cus_id = Integer.parseInt(tv.getText().toString());
               if (cus_id > 0) {
                    //If the entered password is the same that is stored in the database
                    // Login is Successful
                    onLoginSuccess();
                    progressDialog.dismiss();

                } else {
                    //either the entered username or password doesnt match
                    Toast.makeText(LoginActivity.this, "Username Did Not Match", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    btnLogin.setEnabled(true);
                }
            }
        }, 3000);
    }

    private void onLoginSuccess() {
        btnLogin.setEnabled(true);
        //Get the email and name of the user
        String email = editEmail.getText().toString();

        Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
        myApplication.saveToken("EmailS",email);
        myApplication.saveToken("Id", String.valueOf(cus_id));
        startActivity(intent);
    }

    private boolean validate() {

        boolean valid = true;

        String email = editEmail.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Enter a valid email address");
            valid = false;
        } else {
            editEmail.setError(null);
        }
        return valid;
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        btnLogin.setEnabled(true);
    }


    public static void makeTextViewHyperlink(TextView textView) {
        SpannableStringBuilder ssb = new SpannableStringBuilder( );
        ssb.append( textView.getText( ) );
        ssb.setSpan( new URLSpan("#"), 0, ssb.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        textView.setText( ssb, TextView.BufferType.SPANNABLE );


    }
}

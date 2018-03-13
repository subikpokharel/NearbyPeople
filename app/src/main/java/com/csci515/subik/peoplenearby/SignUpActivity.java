package com.csci515.subik.peoplenearby;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {

    EditText input_name, input_age, input_email;
    RadioGroup radioGroup;
    Button create_account;
    TextView link_login, tv;
    RadioButton radioButton;
    int output = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        init();
    }


    public void createAccount(View view) {
        create_account.setEnabled(false);
        if (!validate()){
            onSignupFailed();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this,R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();
        String name = input_name.getText().toString();
        String email = input_email.getText().toString();
        String age = input_age.getText().toString();
        //String gender = null;
        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(selectedId);

        String status = "SignUp";
        output = 0;
        new DatabaseCustomer(getApplicationContext(), tv).execute(status, name, age, String.valueOf(radioButton.getText()), email);
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {

                        output = Integer.parseInt(tv.getText().toString());
                        if (output > 0) {
                            // On complete call either onLoginSuccess or onLoginFailed
                            ////CHECK CONNECTION RESULT and DO WHAT is Predered
                            onSignupSuccess();
                            progressDialog.dismiss();
                        }else {
                            //either the entered username or password doesnt match
                            onSignupFailed();
                            progressDialog.dismiss();
                        }
                    }
                }, 3000);

    }


    private void init() {
        input_name = findViewById(R.id.input_name);
        input_age = findViewById(R.id.input_age);
        input_email = findViewById(R.id.input_email);
        radioGroup = findViewById(R.id.radioGroupGender);
        create_account = findViewById(R.id.btn_signup);
        tv = findViewById(R.id.signupStatus);
        link_login = findViewById(R.id.link_login);
        makeTextViewHyperlink(link_login);
    }

    public void backToLogin(View view) {
        finish();
    }

    public void onSignupSuccess() {

        //Returns to the LoginActivity
        create_account.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    private void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Signup failed", Toast.LENGTH_LONG).show();
        create_account.setEnabled(true);
    }



    private boolean validate() {

        boolean valid = true;

        String name = input_name.getText().toString();
        String email = input_email.getText().toString();
        String age = input_age.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            input_email.setError("Enter a valid email address");
            valid = false;
        } else {
            input_email.setError(null);
        }

        if (name.isEmpty()){
            input_name.setError("Please enter a Name");
            valid = false;
        } else {
            input_name.setError(null);
        }

        if (age.isEmpty()){
            input_age.setError("Please enter your Age");
            valid = false;
        } else {
            input_age.setError(null);
        }

        return valid;
    }

    public static void makeTextViewHyperlink(TextView textView) {
        SpannableStringBuilder ssb = new SpannableStringBuilder( );
        ssb.append( textView.getText( ) );
        ssb.setSpan( new URLSpan("#"), 0, ssb.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        textView.setText( ssb, TextView.BufferType.SPANNABLE );
    }

}

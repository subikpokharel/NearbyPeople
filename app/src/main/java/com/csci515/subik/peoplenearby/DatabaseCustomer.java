package com.csci515.subik.peoplenearby;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by subik on 3/12/18.
 */

public class DatabaseCustomer extends AsyncTask<String, Void, String> {

    private Context context;
    private TextView cus_id;
    private String status;

    public DatabaseCustomer(Context applicationContext, TextView textView) {
        this.context = applicationContext;
        this.cus_id = textView;
    }

    @Override
    protected String doInBackground(String... args) {

        status = args[0];
        try {
            String link = "http://undcemcs02.und.edu/~subik.pokharel/515/1/Customer.php";

            // Connect to the server.
            URL url = new URL( link );
            URLConnection conn = url.openConnection( );
            conn.setDoOutput( true );
            String data = null;
            if (args[0].equals("Login")){
                String email = args[1];
                data = URLEncoder.encode( "email", "UTF-8" ) + "=";
                data += URLEncoder.encode( args[1],   "UTF-8" ) + "&";
                data += URLEncoder.encode( "key", "UTF-8" ) + "=";
                data += URLEncoder.encode( args[0],   "UTF-8" );
            }
            else if (args[0].equals("SignUp")){

                data = URLEncoder.encode( "name", "UTF-8" ) + "=";
                data += URLEncoder.encode( args[1],   "UTF-8" ) + "&";
                data += URLEncoder.encode( "age", "UTF-8" ) + "=";
                data += URLEncoder.encode( args[2],   "UTF-8" )+ "&";
                data += URLEncoder.encode( "gender", "UTF-8" ) + "=";
                data += URLEncoder.encode( args[3],   "UTF-8" )+ "&";
                data += URLEncoder.encode( "email", "UTF-8" ) + "=";
                data += URLEncoder.encode( args[4],   "UTF-8" )+ "&";
                data += URLEncoder.encode( "key", "UTF-8" ) + "=";
                data += URLEncoder.encode( args[0],   "UTF-8" );
            }

            Log.d("data sending: ", data);
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
            return new String( "Exception while connecting: " + e.getMessage( ) );
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (status.equals("Login")){
            if (!result.equals("failed") ){
                this.cus_id.setText(result);
            }else{
                this.cus_id.setText("0");
            }
        }

        else if (status.equals("SignUp")){
            if (!result.equals("failed") ){
                this.cus_id.setText(result);
            }else{
                this.cus_id.setText("0");
            }
        }

    }
}

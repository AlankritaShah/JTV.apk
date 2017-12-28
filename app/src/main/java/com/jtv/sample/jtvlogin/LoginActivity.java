package com.jtv.sample.jtvlogin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.jtv.sample.mypushnotificationlibrary.SharedPrefManager;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LoginActivity extends Activity {
    
    Button buttonlogin;
    EditText ed1, ed2;
    TextView t1, t2;
    double x, y, f, g;
    public String fname, lname, dob, email, password, path;
    String URL = config.Login_api;
    // private static final String URL = "http://192.168.1.163/jtv/mail/ios/signin/";
    private static final int MY_PERMISSION_REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        buttonlogin = (Button) findViewById(R.id.button);
        ed1 = (EditText) findViewById(R.id.editText);
        ed2 = (EditText) findViewById(R.id.editText2);
        t1 = (TextView) findViewById(R.id.text1);
        t2 = (TextView) findViewById(R.id.text2);

        ed1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        //Asking for location permission
        if (ContextCompat.checkSelfPermission(LoginActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_REQUEST_LOCATION);
            }
            else {
                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_REQUEST_LOCATION);
            }
        }
        else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            try {
                if (location != null) {
                    x = location.getLatitude();
                    y = location.getLongitude();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //on clicking the login button
        buttonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String token = SharedPrefManager.getInstance(getApplicationContext()).getDeviceToken();
                Log.d("token",token);

                email = ed1.getText().toString().trim();
                password = ed2.getText().toString().trim();
                final String deviCe = "Android";
                String lat = String.valueOf(x);
                String lon = String.valueOf(y);
                String loctn = hereLocation(x, y);

                Log.e("lat", lat);
                Log.e("lon", lon);
                Log.e("city", loctn);

                new ExecuteTask().execute(email, password, token, deviCe, loctn);
            }
        });

        //on clicking forgot password
        t1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Toast.makeText(getApplicationContext(),"Hahahaha!!",Toast.LENGTH_SHORT).show();
            }
        });

        //on clicking sign up option
        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent j = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(j);
                finish();
            }
        });
    }

    //checking location permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case MY_PERMISSION_REQUEST_LOCATION:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    if ( ContextCompat.checkSelfPermission( LoginActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
                        if(ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION )){
                            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},MY_PERMISSION_REQUEST_LOCATION);
                        }
                        else{
                            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},MY_PERMISSION_REQUEST_LOCATION);
                        }
                    }
                    else{
                        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        try{
                            if(location!=null){
                                f=location.getLatitude();
                                g = location.getLongitude();}
                            String lat1 = String.valueOf(f);
                            String lon1 = String.valueOf(g);
                            Log.e("lat", lat1);
                            Log.e("lon", lon1);
                            String loctn1 = hereLocation(f, g);
                            Log.e("city", loctn1);

                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    //get closest city name
    public String hereLocation(double lat, double lon){
        String curCity = "";

        Geocoder geocoder = new Geocoder(LoginActivity.this, Locale.getDefault());
        List<Address> addressList;
        try{
            addressList = geocoder.getFromLocation(lat,lon,1);
            if(addressList.size()>0) {
                curCity = addressList.get(0).getLocality();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return curCity;
    }


    class ExecuteTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String a = PostData(params);
            Log.e("xyz", a);
            return a;
        }

        @Override
        protected void onPostExecute(String b) {
            Log.e("b",b);
            if(b.equals("true"))
            {
                Log.e("p","accepted");
                Toast.makeText(getApplicationContext(), "Accepted", Toast.LENGTH_SHORT).show();
                UserDetails.getInstance(getApplicationContext()).saveUserdetails(fname, lname, email,dob,password,path);
                Intent j = new Intent(LoginActivity.this, HomeActivity.class);
                j.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(j);
                finish();
            }
            else{
                Toast.makeText(getApplicationContext(), "Wrong credentials", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Function to pass data to the JSONParser
    public String PostData(String[] values) {
        try {
            List<NameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("email", values[0]));
            list.add(new BasicNameValuePair("password", values[1]));
            list.add(new BasicNameValuePair("token", values[2]));
            list.add(new BasicNameValuePair("deviCe", values[3]));
            list.add(new BasicNameValuePair("loctn",values[4]));

            JSONParser jp = new JSONParser();
            JSONObject json = jp.getJSONFromUrl(URL, list);
            Log.e("json",json.toString());

            String result=json.getString("result");
            if(result.equals("true")) {
                fname = json.getString("fname");
                lname = json.getString("lname");
                dob = json.getString("dob");
                path = json.getString("path");
            }
            Log.e("result",result);

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}




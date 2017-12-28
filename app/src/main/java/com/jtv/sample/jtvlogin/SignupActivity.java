package com.jtv.sample.jtvlogin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.widget.DatePicker;
import com.jtv.sample.mypushnotificationlibrary.SharedPrefManager;
import java.util.List;
import java.util.Locale;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

public class SignupActivity extends Activity {
    Button b, b1;
    EditText ed1, ed2, ed3, ed4, ed6;
    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;
    double x,y;
    double f,g;
    String URL = config.Signup_api;
 //   private static final String URL = "http://192.168.1.163/JTV/mail/ios/";
    private static final int MY_PERMISSION_REQUEST_LOCATION=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        b = (Button) findViewById(R.id.btn_signup);
        ed1 = (EditText) findViewById(R.id.input_fname);
        ed2 = (EditText) findViewById(R.id.input_email);
        ed3 = (EditText) findViewById(R.id.input_password);
        ed4 = (EditText) findViewById(R.id.input_lname);
        ed6 = (EditText) findViewById(R.id.input_repass);
        b1 = (Button) findViewById(R.id.button1);

        dateView = (TextView) findViewById(R.id.textView3);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month + 1, day);

        //on clicking the dob button to select date from the calender
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setDate(v);
            }
        });

        //on clicking the sign up button
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                String email = ed2.getText().toString().trim();
                final String token = SharedPrefManager.getInstance(getApplicationContext()).getDeviceToken();
                Log.d("token", token);
                int b = token.length();
                System.out.println(b);
                final String fname = ed1.getText().toString();
                final String lname = ed4.getText().toString();
                final String password = ed3.getText().toString();
                final String password2 = ed6.getText().toString();
                final String dob = dateView.getText().toString();
                final String deviCe = "Android";

                String lat = String.valueOf(x);
                String lon = String.valueOf(y);

                Log.e("lat", lat);
                Log.e("lon", lon);
                String loctn = hereLocation(x, y);
                Log.e("city", loctn);

                if (!password.equals(password2)) {
                    Toast.makeText(getApplicationContext(), "Password does not match!", Toast.LENGTH_SHORT).show();
                } else {
                    new ExecuteTask().execute(fname, lname, email, password, dob, token, deviCe, loctn);
                }
            }
        });
    }

    //get closest city name
    public String hereLocation(double lat, double lon){
        String curCity = "";

        Geocoder geocoder = new Geocoder(SignupActivity.this, Locale.getDefault());
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
            return a;
        }

        @Override
        protected void onPostExecute(String a) {
            Toast.makeText(getApplicationContext(), a, Toast.LENGTH_SHORT).show();
        }
    }

    //pass the data to the JSONParser
    public String PostData(String[] values) {
        try {
            List<NameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("fname", values[0]));
            list.add(new BasicNameValuePair("lname", values[1]));
            list.add(new BasicNameValuePair("email", values[2]));
            list.add(new BasicNameValuePair("password", values[3]));
            list.add(new BasicNameValuePair("dob", values[4]));
            list.add(new BasicNameValuePair("token", values[5]));
            list.add(new BasicNameValuePair("deviCe", values[6]));
            list.add(new BasicNameValuePair("loctn", values[7]));

            JSONParser jp = new JSONParser();
            JSONObject json = jp.getJSONFromUrl(URL, list);
            Log.e("json", json.toString());
            String result=json.getString("result");
            String path = json.getString("path");
            String statusmsg = json.getString("status_message");
            if(result.equals("true")){
                UserDetails.getInstance(getApplicationContext()).saveUserdetails(values[0], values[1], values[2],values[4],values[3],path);
                Intent j = new Intent(SignupActivity.this, HomeActivity.class);
                j.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(j);
                finish();
            }
            return statusmsg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "select date",
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub

        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    showDate(arg1, arg2 + 1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        dateView.setText(new StringBuilder().append(day).append("-")
                .append(month).append("-").append(year));
    }
}























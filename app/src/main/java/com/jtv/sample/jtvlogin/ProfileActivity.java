package com.jtv.sample.jtvlogin;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ProfileActivity extends Activity{
    TextView temail;
    ImageView profimage;
    Button buttonsave;
    ImageButton buttonedit, buttonback;
    public String fname,lname,email,password,dob,path;
    EditText editfname, editlname;
    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;
//    private static final String URL = "http://192.168.1.163/jtv/login/user_update.php";
    String URL = config.editprofile_api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        profimage = (ImageView) findViewById(R.id.profimage);
        temail = (TextView) findViewById(R.id.email);
        buttonedit = (ImageButton) findViewById(R.id.edit_profile);
        buttonback = (ImageButton) findViewById(R.id.back);
        buttonsave = (Button) findViewById(R.id.savechangesbutton);
        editfname = (EditText) findViewById(R.id.edit_fname);
        editlname = (EditText) findViewById(R.id.edit_lname);
        dateView = (TextView) findViewById(R.id.dob);

        profimage.clearFocus();
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month + 1, day);

       // fetch details fromuser details
        fname = UserDetails.getInstance(getApplicationContext()).getFirstName();
        lname = UserDetails.getInstance(getApplicationContext()).getLastName();
        dob = UserDetails.getInstance(getApplicationContext()).getDob();
        email = UserDetails.getInstance(getApplicationContext()).getEmail();
        password = UserDetails.getInstance(getApplicationContext()).getPassword();
        path = UserDetails.getInstance(getApplicationContext()).getPath();

        if(path.equals("")) {
            path="http://192.168.1.163/JTV/user.png";
        }

        Log.e("path", path);
        Picasso.with(this).load(path).into(profimage);

        //set up clicking edit option
        editfname.setText(fname);
        editlname.setText(lname);
        temail.setText(email);
        dateView.setText(dob);
        editfname.setEnabled(false);
        editlname.setEnabled(false);
        buttonsave.setEnabled(false);
        buttonsave.setVisibility(View.GONE);

        //on clicking back button
        buttonback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, HomeActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });

        //on clicking edit option
        buttonedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profimage.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v){
                        Log.e("passed path",path);
                        Intent i = new Intent(ProfileActivity.this, Uploadimage.class);
                        startActivity(i);
                        finish();
                    }
                });

                editfname.setEnabled(true);
                editlname.setEnabled(true);
                buttonedit.setVisibility(View.GONE);
                buttonsave.setVisibility(View.VISIBLE);
                buttonsave.setEnabled(true);

                dateView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        setDate(v);
                    }
                });

                buttonsave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fname = editfname.getText().toString();
                        lname = editlname.getText().toString();
                        dob = dateView.getText().toString();

                        new ProfileActivity.ExecuteTask().execute(email, lname, password, dob, fname,path);
                        UserDetails.getInstance(getApplicationContext()).saveUserdetails(fname, lname, email,dob,password,path);
                        Intent j = new Intent(ProfileActivity.this, HomeActivity.class);
                        startActivity(j);
                        finish();

                    }
                });
            }
        });
    }


    class ExecuteTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String a = PostData(params);
            return a;
        }
        @Override
        protected void onPostExecute(String a) {
            Toast.makeText(getApplicationContext(), a, Toast.LENGTH_LONG).show();
        }
    }

    public String PostData(String[] values) {
        try {
            List<NameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("email", values[0]));
            list.add(new BasicNameValuePair("fname", values[4]));
            list.add(new BasicNameValuePair("lname", values[1]));
            list.add(new BasicNameValuePair("pswd", values[2]));
            list.add(new BasicNameValuePair("dob", values[3]));
            list.add(new BasicNameValuePair("path", values[5]));

            JSONParser jp = new JSONParser();
            JSONObject json = jp.getJSONFromUrl(URL, list);
            Log.e("jsonedit", json.toString());

            String result=json.getString("result");
            String status = json.getString("status");
            return result;

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
//    private Bitmap getBitmapFromURL(String strURL) {
//        try {
//            java.net.URL url = new URL(strURL);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setDoInput(true);
//            connection.connect();
//            InputStream input = connection.getInputStream();
//            Bitmap myBitmap = BitmapFactory.decodeStream(input);
//            return myBitmap;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
}
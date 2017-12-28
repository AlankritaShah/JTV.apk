package com.jtv.sample.jtvlogin;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class UserDetails {
    private static final String SHARED_PREF_NAME = "UserSharedPref";
    private static final String FIRST_NAME = "FirstName";
    private static final String LAST_NAME = "LastName";
    private static final String PASSWORD = "Password";
    private static final String DOB = "Dob";
    private static final String EMAIL = "Email";
    private static final String PATH = "Path";
    private static UserDetails mInstance;
    private static Context mCtx;

    private UserDetails(Context context) {
        mCtx = context;
    }

    public static synchronized UserDetails getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new UserDetails(context);
        }
        return mInstance;
    }

    //this method will save the user details to shared preferences
    public boolean saveUserdetails(String fname, String lname, String email, String dob, String password, String path){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(FIRST_NAME, fname);
        edit.putString(LAST_NAME, lname);
        edit.putString(EMAIL, email);
        edit.putString(DOB, dob);
        edit.putString(PASSWORD, password);
        edit.putString(PATH, path);
        edit.apply();
        return true;
    }

    //these methods will fetch the details from shared preferences
    public String getFirstName(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(FIRST_NAME, "no first name");
    }

    public String getLastName(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(LAST_NAME, "no last name");
    }

    public String getEmail(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(EMAIL, "no email");
    }

    public String getPassword(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(PASSWORD, "no password");
    }

    public String getDob(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(DOB, "no dob");
    }

    public String getPath(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(PATH, "no image path");
    }
}
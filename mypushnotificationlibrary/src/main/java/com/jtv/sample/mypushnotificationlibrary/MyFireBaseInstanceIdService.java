package com.jtv.sample.mypushnotificationlibrary;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFireBaseInstanceIdService extends FirebaseInstanceIdService{
    private static final String REG_TOKEN = "REG_TOKEN";
   // private static final String SHARED_PREF_NAME = "FCMSharedPref";
    @Override
    public void onTokenRefresh(){
        String recent_token = FirebaseInstanceId.getInstance().getToken();
//        int b = recent_token.length();
        Log.d(REG_TOKEN,recent_token);
      //  Log.d("token length", );
        storeToken(recent_token);

    }

    private void storeToken(String token) {
        //saving the token on shared preferences
        Log.d("in store token",token);
        SharedPrefManager.getInstance(getApplicationContext()).saveDeviceToken(token);
//        if(m==true){Log.d("in store token","token is saved");}
//        else {Log.d("in store token","token is not saved");}
    }

}

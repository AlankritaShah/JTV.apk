package com.jtv.sample.mypushnotificationlibrary;

import android.content.Intent;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import org.json.JSONException;
import org.json.JSONObject;

public class MyFireBaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData());
                sendPushNotification(json);
            } catch (Exception e) {
               Log.e(TAG, "Exception1: " + e.getMessage());
            }

    }

    //this method will display the notification
    //We are passing the JSONObject that is received from
    //firebase cloud messaging
    private void sendPushNotification(JSONObject json) {
        //optionally we can display the json into log

        try {

            String title = json.getString("title");
            String message = json.getString("body");
            String imageUrl = json.getString("imageUrl");

            Log.e("title", title);
            Log.e("message", message);
            Log.e("imageUrl", imageUrl);

            //creating MyNotificationManager object
            MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());

            //creating an intent for the notification
           Intent intent = new Intent(getApplicationContext(), Class.forName("com.jtv.sample.jtvlogin.SplashActivity"));
          // intent.setClassName("com.jtv.sample.jtvlogin", "MainActivity.class");

            //if there is no image
            if(imageUrl.equals("none")){
                //displaying small notification
                mNotificationManager.showSmallNotification(title, message, intent);
            }else{
                //if there is an image
                //displaying a big notification
                mNotificationManager.showBigNotification(title, message, imageUrl, intent);

            }
            Log.e(TAG, "Notification JSON " + json.toString());

        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

}
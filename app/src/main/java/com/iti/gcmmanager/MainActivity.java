package com.iti.gcmmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Button sendNotification;
    String API_KEY = "AAAApFd9mEQ:APA91bFAHnbU9tcDvhdgeW2a_xS5_H6XlkzgKuGhzij9WkucfpOw0blKNH3R_VrlB9Aps81ja_U9WT51wEzEBV21a-GvK3Rf_S0hkbELae7kOs7iuoA6ZiwWVAUrXR3Vkc-PipNO5RSf";
    //String to= "c7DqVclnvoM:APA91bHWuXUJ7Rnu6Ul32tmbUK9xR2iDhapENkiSr_91J5chzYQ_ZVazDHRtUCrxiWTxcuINSD9_0XEHdsYvxPD30TrnEm7CDwHpzKTm847dTLasfTfmMzvRWtf135gVSry69780XakT";
    //String API_KEY = "AAAApFd9mEQ:APA91bFAHnbU9tcDvhdgeW2a_xS5_H6XlkzgKuGhzij9WkucfpOw0blKNH3R_VrlB9Aps81ja_U9WT51wEzEBV21a-GvK3Rf_S0hkbELae7kOs7iuoA6ZiwWVAUrXR3Vkc-PipNO5RSf";
    //String to= "c7DqVclnvoM:APA91bHWuXUJ7Rnu6Ul32tmbUK9xR2iDhapENkiSr_91J5chzYQ_ZVazDHRtUCrxiWTxcuINSD9_0XEHdsYvxPD30TrnEm7CDwHpzKTm847dTLasfTfmMzvRWtf135gVSry69780XakT";
    String to = "/topics/emp";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    EditText notificationTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendNotification = (Button) findViewById(R.id.button);
        notificationTitle = (EditText) findViewById(R.id.title);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Check type of intent filter
                if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)) {
                    //Registration success
                    String token = intent.getStringExtra("token");
                    Toast.makeText(getApplicationContext(), "GCM token:" + token, Toast.LENGTH_LONG).show();
                } else if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)) {
                    //Registration error
                    Toast.makeText(getApplicationContext(), "GCM registration error!!!", Toast.LENGTH_LONG).show();
                } else {
                    //Tobe define
                }
            }
        };

        //Check status of Google play service in device
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (ConnectionResult.SUCCESS != resultCode) {
            //Check type of error
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                //So notification
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());
            } else {
                Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }
        } else {
            //Start service
            Intent itent = new Intent(this, GCMRegistrationIntentService.class);
            startService(itent);
        }

        sendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = notificationTitle.getText().toString();
                if (title.equals("")) {
                    Toast.makeText(getApplicationContext(), "please enter title of notification", Toast.LENGTH_LONG).show();
                } else {
                    if (isNetworkAvailable()) {
                        String newDate = new Date().toString();
                        GCMPushReceiverService.rides.put(newDate, "free Ride");
                        new NotificationSender().sendNotification("you have new ride called "+title, "", newDate,title);
                        notificationTitle.setText("");
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });
    }


    public boolean isNetworkAvailable() {

        ConnectivityManager connectivigetyManager = (ConnectivityManager) getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivigetyManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.w("MainActivity", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w("MainActivity", "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }
}
package com.iti.gcmmanager;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by HP on 13/05/2017.
 */

public class NotificationSender {


    String API_KEY = "AAAApFd9mEQ:APA91bFAHnbU9tcDvhdgeW2a_xS5_H6XlkzgKuGhzij9WkucfpOw0blKNH3R_VrlB9Aps81ja_U9WT51wEzEBV21a-GvK3Rf_S0hkbELae7kOs7iuoA6ZiwWVAUrXR3Vkc-PipNO5RSf";
    //String to= "c7DqVclnvoM:APA91bHWuXUJ7Rnu6Ul32tmbUK9xR2iDhapENkiSr_91J5chzYQ_ZVazDHRtUCrxiWTxcuINSD9_0XEHdsYvxPD30TrnEm7CDwHpzKTm847dTLasfTfmMzvRWtf135gVSry69780XakT";
    String to ="/topics/emp";

    public void sendNotification(final String message, final String token, final String date,final String title) {


        Thread background = new Thread(new Runnable() {
            @Override
            public void run() {


                try {
                    // Prepare JSON containing the GCM message content. What to send and where to send.
                    Log.i("send notifictaion ",date);
                    JSONObject jGcmData = new JSONObject();
                    JSONObject jData = new JSONObject();
                    jData.put("message", message);
                    jData.put("title",title);
                    jData.put("time",date);
                    jData.put("acceptedToken",token);


                    jGcmData.put("to", to);

                    // What to send in GCM message.
                    jGcmData.put("data", jData);

                    // Create connection to send GCM Message request.
                    URL url = new URL("https://android.googleapis.com/gcm/send");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Authorization", "key=" + API_KEY);
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    // Send GCM message content.
                    OutputStream outputStream = conn.getOutputStream();
                    outputStream.write(jGcmData.toString().getBytes());

                    // Read GCM response.
                    InputStream inputStream = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    Log.i("result", "4");
                    StringBuilder str = new StringBuilder();
                    String line = null;
                    Log.i("result", "5");

                    while ((line = reader.readLine()) != null) {
                        Log.i("result", "reading before appeand");
                        str.append(line);
                        //Log.i("result","reading");
                    }
                    Log.i("result", "6");
                    String resultFromWs = str.toString();
                    Log.i("result", resultFromWs);
                } catch (IOException e) {
                    System.out.println("Unable to send GCM message.");
                    System.out.println("Please ensure that API_KEY has been replaced by the server " +
                            "API key, and that the device's registration token is correct (if specified).");
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }


        });
        background.start();
    }
}

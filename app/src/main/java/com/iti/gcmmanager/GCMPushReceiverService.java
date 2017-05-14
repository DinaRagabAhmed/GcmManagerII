package com.iti.gcmmanager;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by HP on 12/05/2017.
 */

public class GCMPushReceiverService extends GcmListenerService {

    public static HashMap<String,String> rides=new HashMap<String, String>();
    public String acceptedToken;
    //String date="";
    @Override
    public void onMessageReceived(String from, Bundle data) {

        Log.i("reciever message","re");
        String title = data.getString("title");
        acceptedToken=data.getString("acceptedToken");
        String date=data.getString("time");
        //String title=data.getString("title");
        Log.i("time= ",date);

        synchronized (this)
        {
            if(!(rides.get(date).equals("free Ride")))
            {
                //isTaken=false;
                return;
            }
        }

            rides.put(date,"taken ride");
            sendNotification(title, date);
            new NotificationSender().sendNotification("Sorry ride "+title+" already accepted", acceptedToken, date,title);

    }


    private void sendNotification(String message,String date) {

        Log.i("bildd notfication","re");
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int requestCode = 0;//Your request code
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        //Setup notification
        //Sound
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //Build notification
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Trip accepted")
                .setContentText("Trip "+message+" have been accepted")
                .setAutoCancel(false)
                .setContentIntent(pendingIntent);

        SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss",
                Locale.ENGLISH);

        Date date2= null;
        try {
            date2 = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int i = (int)date2.getTime();

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(i, noBuilder.build()); //0 = ID of notification
    }
}

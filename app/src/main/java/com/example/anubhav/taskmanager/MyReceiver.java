package com.example.anubhav.taskmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        throw new UnsupportedOperationException("Not yet implemented");
////        Intent i=new Intent();
//
//        android.support.v4.app.NotificationCompat.Builder builder=new NotificationCompat.Builder(context).
//                setSmallIcon(R.mipmap.clock_image)
//                .setContentTitle("Upcoming Task")
//                .setAutoCancel(false);
////                .setContentText();
    }
}

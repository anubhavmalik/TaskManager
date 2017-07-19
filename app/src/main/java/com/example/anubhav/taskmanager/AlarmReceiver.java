package com.example.anubhav.taskmanager;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by anubhavmalik on 19-07-2017.
 */

public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        String titleAlarm = intent.getExtras().getString("titleAlarm");
        int idAlarm =  intent.getIntExtra("idAlarm",-1);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(titleAlarm)
                .setAutoCancel(false)
                .setContentText("Your deadline is approaching");




        Intent resultIntent = new Intent(context,MainActivity.class);
//        resultIntent.putExtra("id", id);
//        intent.putExtra()
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,idAlarm++,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Log.i("PendingIntentId",""+idAlarm);

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(idAlarm, mBuilder.build());
        Log.i("AlarmReceiverId",""+idAlarm);
    }
}

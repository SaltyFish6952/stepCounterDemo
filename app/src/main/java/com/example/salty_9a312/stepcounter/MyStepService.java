package com.example.salty_9a312.stepcounter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class MyStepService extends Service {

    private Notification.Builder notifictionBuilder;
    private NotificationManager notificationManager;

    private static final int stepNotification_ID = 122;

    public MyStepService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void initNotification(){

        notifictionBuilder = new Notification.Builder(this);

        notifictionBuilder
                .setContentTitle("Step Counter")
                .setContentText("hello")
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher);

        Notification notification = notifictionBuilder.build();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        startForeground(stepNotification_ID,notification);


    }

}

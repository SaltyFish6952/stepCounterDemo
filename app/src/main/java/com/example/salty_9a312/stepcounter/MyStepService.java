package com.example.salty_9a312.stepcounter;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MyStepService extends Service implements SensorEventListener {

    private Callback callback;
    private int current_count = 0;

    DBUtils dbUtils;


    private int storage_total_count = 0;


    private boolean isFirst = true;
//    private boolean isReboot = false;

    private Notification.Builder notificationBuilder;
    private NotificationManager notificationManager;


    private static final int stepNotification_ID = 122;

    public MyStepService() {

        dbUtils = MainActivity.getDbUtils();

    }

    @Override
    public IBinder onBind(Intent intent) {

        initNotification();

        if (callback != null) {
            callback.onDataChange(999);

        }

        try {
            init();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Binder();
    }

    @Override
    public boolean onUnbind(Intent intent) {


        updateCurrentData();
        Log.d("nice","doneeeeeeee");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {

        //write total_count into DataBase


        super.onDestroy();


    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {

            int eventValue = (int) event.values[0];

            if (isFirst) {


                String queryDate = isHasToday();

                Log.d("isFirst", queryDate + " maybe null");

                //if the database has record
                if (queryDate != null) {

                    //read database
                    storage_total_count = getCount(queryDate);

//                    //if reboot, reset today's total
//                    if(eventValue < storage_total_count){
//
//                        updateTotalData(eventValue);
//                        storage_total_count = eventValue;
//
//                        isReboot = true;
//
//                    }

                    Log.d("queryDate not null storage_total_count", storage_total_count + "");


                } else {


                    storage_total_count = eventValue;
                    //write storage;
                    Log.d("insert", "i want to insert " + storage_total_count);
                    insertData();

                    String s = isHasToday();

                    Log.e("date",s + " maybe null");
                    Log.e("inside count",getCount(s) + "");

                }


                isFirst = false;
            }

            Log.d("storage_total_count", "count : " + storage_total_count);
            Log.d("eventValue", "count : " + eventValue);

//            if(isReboot) {
//
//                storage_total_count = storage_total_count - getCurrentCount();
//                Log.d("after storage_total_count", "count : " + storage_total_count);
//
//                isReboot = false;
//            }
            current_count = eventValue - storage_total_count;

            updateCurrentData();
            Log.d("save","has been saved.");

            Log.d("current_count", "count : " + current_count);


            Log.e("total", event.values[0] + "");


        }


        updateStepCount();


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    private void updateStepCount() {


        Notification notification = notificationBuilder
                .setContentText("step : " + current_count)
                .setWhen(System.currentTimeMillis())
                .build();
        notificationManager.notify(stepNotification_ID, notification);


        if (callback != null) {

            Log.e("in service", "fff " + current_count);
            callback.onDataChange(current_count);

        }

    }


    public class Binder extends android.os.Binder {
        public void setData(int count) {
            MyStepService.this.current_count = count;
        }

        public MyStepService getMyService() {
            return MyStepService.this;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();

//        new Thread(new Runnable() {
//
//
//            int i = 0;
//
//            public void run() {
//
//                while(true){
//
//
//
//                    try {
//
//                        Log.e("in Service",i + " check");
//
//                        if(callback != null){
//                            callback.onDataChange(i);
//                        }
//
//                        sleep(1000);
//                        i++;
//
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//
//
//
//
//            }
//        }).start();

    }

    private void initNotification() {

        String CHANNEL_ID = "step_counter";
        String CHANNEL_NAME = "CH_1";

        NotificationChannel notificationChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
//            notificationChannel.enableLights(true);
//            notificationChannel.setLightColor(Color.RED);
//            notificationChannel.setShowBadge(true);
            notificationChannel.setSound(null, null);
            notificationChannel.setVibrationPattern(new long[]{0});
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
        }


        notificationBuilder = new Notification.Builder(this);


        notificationBuilder
                .setContentTitle("Step Counter")
                .setContentText("hello")
                .setAutoCancel(false)
                .setOngoing(true)

                .setSmallIcon(R.mipmap.ic_launcher);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setChannelId(CHANNEL_ID);
        }

        Notification notification = notificationBuilder.build();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        startForeground(stepNotification_ID, notification);


    }

    public static interface Callback {
        void onDataChange(int count);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public Callback getCallback() {
        return callback;
    }


    //init sensor


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void init() throws Exception {

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Sensor stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
//        Sensor stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);


        if (stepCounterSensor == null) {

            Toast.makeText(this, "your device is not supported counter.", Toast.LENGTH_SHORT).show();

//            throw new Exception("this device is not support");


        }

//        if(stepDetectorSensor == null){
//            Toast.makeText(this,"your device is not supported detector.",Toast.LENGTH_SHORT).show();
//
////            throw new Exception("this device is not support");
//
//        }


        sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
//        sensorManager.registerListener(this, stepDetectorSensor, sensorManager.SENSOR_DELAY_NORMAL);

    }


    private String isHasToday() {

        Cursor cursor = dbUtils.query(new String[]{"date"}, null, null, "date desc");

        if (cursor.moveToFirst()) {

            String date = cursor.getString(cursor.getColumnIndex("date"));

            Log.d("cursor date", date);


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            try {


                Date databaseDate = sdf.parse(date);
                Date nowDate = new Date();

                Log.v("nowDate", sdf.format(nowDate));
                Log.v("databaseDate", sdf.format(databaseDate));

                if (sdf.format(databaseDate).equals(sdf.format(nowDate))) {
                    return sdf.format(databaseDate);
                }


            } catch (ParseException e) {
                e.printStackTrace();
            }


        }
        while (cursor.moveToNext()) ;

        cursor.close();


        return null;
    }

    private int getCount(String date) {

        Cursor cursor = dbUtils.query(new String[]{"total_step"},
                "date=?",
                new String[]{date},
                null);

        int count = 0;

        if (cursor.moveToFirst()) {


            count = cursor.getInt(cursor.getColumnIndex("total_step"));
            Log.d("cursor get INT ",cursor.getInt(cursor.getColumnIndex("total_step"))+"");


        }

        cursor.close();

        return count;

    }

    private int getCurrentCount() {

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


        Cursor cursor = dbUtils.query(new String[]{"current_step"},
                "date=?",
                new String[]{sdf.format(date)},
                null);

        int count = 0;

        if (cursor.moveToFirst()) {


            count = cursor.getInt(cursor.getColumnIndex("current_step"));
            Log.d("cursor get INT ",cursor.getInt(cursor.getColumnIndex("current_step"))+"");


        }

        cursor.close();

        return count;

    }


    private void insertData() {

        Date date = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Log.d("insert data", "date:" + sdf.format(date) + "  " + storage_total_count);

        dbUtils.insert(storage_total_count, sdf.format(date));

    }

    private void updateCurrentData() {

        dbUtils.updateCurrent(storage_total_count, current_count);

    }

    private void updateTotalData(int newTotalStep){

        dbUtils.updateTotal(storage_total_count,newTotalStep);

    }


}

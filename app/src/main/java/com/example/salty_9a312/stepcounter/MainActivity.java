package com.example.salty_9a312.stepcounter;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void init() throws Exception {

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Sensor stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        Sensor stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);


        if (stepCounterSensor == null || stepDetectorSensor == null || sensorManager == null) {

            throw new Exception("this device is not support");

        }



        SensorEventListener mSensorEventListener = new SensorEventListener() {
            private float stepDetector;
            private float stepCounter;

            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                /**
                 * 计步计数传感器传回的历史累积总步数
                 */
                if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                    stepCounter = sensorEvent.values[0];
                    Log.d("ahah", "STEP_COUNTER:" + stepCounter);
                }

                /**
                 * 计步检测传感器检测到的步行动作是否有效？
                 */
                if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                    stepDetector = sensorEvent.values[0];
                    Log.d("ahah", "STEP_DETECTOR:" + stepDetector);
                    if (stepDetector == 1.0) {
                        Log.d("ahah", "一次有效的步行");
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };


        sensorManager.registerListener(mSensorEventListener,stepCounterSensor,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(mSensorEventListener,stepDetectorSensor,sensorManager.SENSOR_DELAY_NORMAL);

    }
}

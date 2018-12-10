package com.example.salty_9a312.stepcounter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements ServiceConnection {

    private MyStepService.Binder myStepBinder = null;
    private static DBUtils dbUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbUtils = new DBUtils(this, 1);

        Intent intent = new Intent(MainActivity.this, MyStepService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);

        initGraph();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.achievements){

            Intent intent = new Intent(MainActivity.this,AchievementsActivity.class);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

        myStepBinder = (MyStepService.Binder) iBinder;

        myStepBinder.getMyService().setCallback(new MyStepService.Callback() {
            @Override
            public void onDataChange(int count) {

                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt("count", count);
                msg.setData(bundle);

                handler.sendMessage(msg);


            }
        });

    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e("in Activity", "check " + msg.getData().getInt("count"));

            TextView textView = findViewById(R.id.counter);
            textView.setText("your step is : " + msg.getData().getInt("count"));
            initGraph();

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(this);
    }

    public static DBUtils getDbUtils() {
        return dbUtils;
    }




    public void initGraph() {

        ArrayList<DataPoint> dataPoints = new ArrayList<>();
        String[] Labels = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        int[] checks = new int[]{0, 0, 0, 0, 0, 0, 0};


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


        GraphView graph = (GraphView) findViewById(R.id.graph);

        graph.removeAllSeries();

        Cursor cursor = dbUtils.query(new String[]{"current_step", "date"},
                null, null, null);


        int week = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);

        int i = 0;


        if (cursor.moveToFirst()) {

            do {


                Date date;
                Calendar cal = Calendar.getInstance();
                try {

                    date = sdf.parse(cursor.getString(cursor.getColumnIndex("date")));


                    cal.setTime(date);

                    if (week != cal.get(Calendar.WEEK_OF_YEAR))
                        continue;


                } catch (ParseException e) {
                    e.printStackTrace();
                }

                int dateWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;

                dataPoints.add(
                        dateWeek, new DataPoint(++i,
                                cursor.getInt(cursor.getColumnIndex("current_step"))));

                checks[dateWeek] = 1;


            } while (cursor.moveToNext());

        }


        cursor.close();

        for (int j = 0; j < 7; j++) {

            if (checks[j] == 0) {
                dataPoints.add(j, new DataPoint(j, 0));
            }

        }


        DataPoint[] dataPoint = dataPoints.toArray(new DataPoint[dataPoints.size()]);

        Date d = new Date();
        Calendar cal = Calendar.getInstance();

        cal.setTime(d);
        cal.get(Calendar.DAY_OF_WEEK);

        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(dataPoint);


        graph.addSeries(series);

// styling
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX() * 255 / 4, (int) Math.abs(data.getY() * 255 / 6), 100);
            }
        });

        series.setSpacing(90);

//        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graph.getGridLabelRenderer().setLabelsSpace(2);
// draw values on top
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.RED);
        series.setDataWidth(1);


        graph.getViewport().setMinY(-1);
        graph.getViewport().setMaxY(series.getHighestValueY() * 1.2);
        graph.getGridLabelRenderer().setNumHorizontalLabels(7);
        graph.getGridLabelRenderer().setVerticalLabelsVisible(false);
        graph.getViewport().setYAxisBoundsManual(true);

//        double xInterval = 25;
//        graph.getViewport().setXAxisBoundsManual(true);
//        // Shunt the viewport, per v3.1.3 to show the full width of the first and last bars.
//        graph.getViewport().setMinX(series.getLowestValueX() - 0.1);
//        graph.getViewport().setMaxX(series.getHighestValueX() + 0.1);
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        staticLabelsFormatter.setHorizontalLabels(Labels);

    }

}

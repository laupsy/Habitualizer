package com.example.laurabarbera.habitualizer;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.util.FloatMath.sqrt;


public class Debug extends ActionBarActivity implements SensorEventListener {

    private float acceleration, acceleration_cur, acceleration_prev;
    private SensorManager sensorManager;
    private Sensor sensor;

    private int steps = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        init();

        UserProfile u = new UserProfile(this, getSharedPreferences(this.getString(R.string.SHARED_PREFERENCES), MODE_PRIVATE));
        String test = u.getMotionSetting();
        Log.d(test, "hellothere");

        // MOTION

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        acceleration = 0.00f;
        acceleration_cur = SensorManager.GRAVITY_EARTH;
        acceleration_prev = SensorManager.GRAVITY_EARTH;

        // testing database

        final Database db = new Database(this);
        db.updateMotion();
        float[] motion24hours = db.getMotion();
        Log.d(motion24hours[0] + "", "hello");


    }
    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
    }
    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
    @Override
    public void onSensorChanged(SensorEvent e) {
        float THRESHOLD = 1;
        if ( e.sensor.getType() == Sensor.TYPE_ACCELEROMETER ) {
            float x = e.values.clone()[0];
            float y = e.values.clone()[1];
            float z = e.values.clone()[2];
            acceleration_prev = acceleration_cur;
            acceleration_cur = sqrt(x * x + y * y + z * z);
            float change = acceleration_cur - acceleration_prev;
            acceleration += change;
            if ( acceleration > THRESHOLD ) {
                TextView m = (TextView) findViewById(R.id.motion_test);
                steps++;
                String s = "Steps: " + steps;
                m.setText(s);
            }
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public void init() {
        final Handler handler = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable(){
                    public void run() {
                        ActionBar a = getSupportActionBar();
                        setTitle(R.string.getstarted);
                        a.setElevation(0);
                        a.setDisplayHomeAsUpEnabled(false);
                        a.setDisplayShowHomeEnabled(false);
                        a.setDisplayShowCustomEnabled(true);
                        a.setDisplayShowTitleEnabled(false);
                        getSupportActionBar().setCustomView(R.layout.actionbar_layout);
                        ImageView v = (ImageView) findViewById(R.id.visualizer);
                        v.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                Intent visualizer = new Intent(Debug.this, Visualizer.class);
                                Debug.this.startActivity(visualizer);
                                Debug.this.finish();
                            }
                        });
                        ImageView icon = (ImageView) findViewById(R.id.home_icon);
                        icon.setBackgroundResource(R.drawable.icon_back);
                        icon.setOnClickListener(new View.OnClickListener(){
                            public void onClick(View v) {
                                Intent back = new Intent(Debug.this, Dashboard.class);
                                Debug.this.startActivity(back);
                                Debug.this.finish();
                            }
                        });
                    }
                });
            }
        };
        Thread t = new Thread(r);
        t.start();
    }
}

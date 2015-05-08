package com.example.laurabarbera.habitualizer;

import android.content.ContentValues;
import android.content.Context;
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


public class Visualizer extends ActionBarActivity implements SensorEventListener {

    private float acceleration, acceleration_cur, acceleration_prev;
    private SensorManager sensorManager;
    private Sensor sensor;
    private float steps = 0;

    boolean doMotion;

    private Database db;
    private Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizer);
        c = this;
        init();

        startService(new Intent(c, BgSensor.class));

        // MOTION

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        acceleration = 0.00f;
        acceleration_cur = SensorManager.GRAVITY_EARTH;
        acceleration_prev = SensorManager.GRAVITY_EARTH;

        // Check if motion is enabled
        UserProfile u = new UserProfile(c, getSharedPreferences(c.getString(R.string.SHARED_PREFERENCES), MODE_PRIVATE));
        if ( u.getMotionSetting().equals("On") ) { doMotion = true; }
        else { doMotion = false; }

        // testing database

        db = new Database(this);
        db.updateMotion();
        //float[] motion24hours = db.getMotion();
        Log.d("hi" + "", "hello");


    }
    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
    }
    @Override
    public void onPause() {
        super.onPause();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
    }
    @Override
    public void onSensorChanged(SensorEvent e) {
        db = new Database(c);
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
                db.updateMotion();
                TextView m = (TextView) findViewById(R.id.motion_daily);
                //db.getMotion();
                steps = db.getMotion()[8];
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
                                Intent visualizer = new Intent(Visualizer.this, Visualizer.class);
                                Visualizer.this.startActivity(visualizer);
                                Visualizer.this.finish();
                            }
                        });
                        ImageView icon = (ImageView) findViewById(R.id.home_icon);
                        icon.setBackgroundResource(R.drawable.icon_back);
                        icon.setOnClickListener(new View.OnClickListener(){
                            public void onClick(View v) {
                                Intent back = new Intent(Visualizer.this, Dashboard.class);
                                Visualizer.this.startActivity(back);
                                Visualizer.this.finish();
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

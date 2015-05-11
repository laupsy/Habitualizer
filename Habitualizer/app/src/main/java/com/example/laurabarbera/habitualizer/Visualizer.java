package com.example.laurabarbera.habitualizer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import static android.util.FloatMath.sqrt;


public class Visualizer extends ActionBarActivity implements SensorEventListener {

    private float acceleration, acceleration_cur, acceleration_prev;
    private SensorManager sensorManager;
    private Sensor sensor;
    private float[] relMotion;
    private ArrayList<String> questions;
    private ArrayList<float[]> yesPerHour;

    private Database db;
    private Context c;

    private int questionLevel, notifDelay;

    private LineChart motionChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizer);
        c = this;
        init();
        db = new Database(this);
        notifDelay = 108000;

        startService(new Intent(c, BgSensor.class));

        // MOTION

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        acceleration = 0.00f;
        acceleration_cur = SensorManager.GRAVITY_EARTH;
        acceleration_prev = SensorManager.GRAVITY_EARTH;

        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask notifyInterval = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @SuppressWarnings("unchecked")
                    public void run() {
                        try {
                            askQuestion();
                        } catch(Exception e){}
                    }
                });
            }
        };
        timer.schedule(notifyInterval, notifDelay, notifDelay);
        Runnable r = new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable(){
                    public void run() {

                    }
                });
            }
        };
        Thread t = new Thread(r);
        t.start();
        GetQuestions getQuestions = new GetQuestions();
        getQuestions.execute();

        // seconds per week * 1000
        if ( questionLevel == 0 ) notifDelay = 6048000;
            // seconds per day * 1000
        else if ( questionLevel == 1) notifDelay = 864000;
            // seconds per 3 hours * 1000
        else notifDelay = 108000;

        // Set Up Graph

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
    public void askQuestion() {

        /* COPY/PASTED THIS FROM
            http://developer.android.com/training/notify-user/build-notification.html */

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle("Habitualizer")
                        .setContentText("It's time to post an update.");

        Intent resultIntent = new Intent(this, AskQuestion.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);
        int mNotificationId = 001;
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

        /* COPY/PASTE OVER */
    }

    private class GetQuestions extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            Database db = new Database(c);
            questions = db.getQuestions();
            relMotion = db.getRelativeMotion();
            yesPerHour = new ArrayList<>();
            questionLevel = db.getQuestionSetting();
            for ( int i = 0; i < questions.size(); i++ ) {
                int qId = Integer.parseInt(questions.get(i).split(">>")[0]);
                float[] f = db.getAnswersWithTime(qId);
                yesPerHour.add(f);
            }
            return true;
        }

        protected void onPostExecute(Boolean result) {
            TextView dbug = (TextView) findViewById(R.id.q_answers);
            for ( int i = 0; i < yesPerHour.size(); i++ ) {
                float max = 0;
                float maxNdx = 0;
                for ( int j = 0; j < 8; j++ ) {
                    if ( yesPerHour.get(i)[j] > 0 ) {
                        if ( yesPerHour.get(i)[j] > max ) {
                            maxNdx = j;
                        }
                    }
                }
                String thisTime;
                TimeZone timezone = TimeZone.getDefault();
                int timeInt = Math.round((maxNdx+1)*3);
                int offset = timezone.getOffset(0, Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.DAY_OF_WEEK, timeInt * 3600000);
                timeInt = Math.abs(timeInt + (offset / 3600000));
                if ( timeInt < 13 ) thisTime = timeInt + " am";
                else thisTime = (timeInt - 12) + " pm";
                dbug.setText(dbug.getText() + "\n" + questions.get(i).split(">>")[1] + "\n" + "Mostly at " + thisTime);
            }
            initGraph();
        }
    }

    void initGraph() {

        // Setting up graph

        // MOTION

        motionChart = (LineChart) findViewById(R.id.motion_chart);
        ArrayList<Entry> vals1 = new ArrayList<>();
        ArrayList titles = new ArrayList<>();
        ArrayList<LineDataSet> sets = new ArrayList<>();
        for ( int i = 0; i < 8; i++ ) {
            Entry e = new Entry(relMotion[i], i);
            vals1.add(e);
            String time;
            TimeZone timezone = TimeZone.getDefault();
            int timeInt = (1 + i*3);
            int offset = timezone.getOffset(0, Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.DAY_OF_WEEK, timeInt * 3600000);
            timeInt = Math.abs(timeInt + (offset / 3600000));
            if ( timeInt < 13 ) {
                time = timeInt + " am";
            }
            else {
                time = (timeInt - 12) + " pm";
            }
            titles.add(time);
        }
        LineDataSet lds = new LineDataSet(vals1, "Motion");
        sets.add(lds);
        LineData data = new LineData(titles, sets);
        lds.setLineWidth(0);
        lds.setDrawCubic(true);
        lds.setCubicIntensity(0.3f);
        lds.setDrawFilled(true);
        lds.setFillColor(getResources().getColor(R.color.graph4));
        lds.setColor(getResources().getColor(R.color.graph4));
        lds.setFillAlpha(200);
        lds.setDrawCircles(false);


        // QUESTION TEST

        for ( int i = 0; i < yesPerHour.size(); i++ ) {
            int thisTotal = 0;
            ArrayList<Entry> questionVals = new ArrayList<>();
            ArrayList questionTitles = new ArrayList<>();
            float[] qtest = yesPerHour.get(i);
            for ( int j = 0; j < 8; j++ ) {
                thisTotal++;
                float percent = (qtest[j] / thisTotal) * 100;
                Entry e = new Entry(percent,j);
                questionVals.add(e);
                questionTitles.add(questions.get(0).split(">>")[1]);
            }

            LineDataSet questionTestData = new LineDataSet(questionVals, questions.get(i).split(">>")[1]);
            questionTestData.setLineWidth(0);
            questionTestData.setDrawCubic(true);
            questionTestData.setCubicIntensity(0.3f);
            questionTestData.setDrawFilled(true);
            questionTestData.setFillAlpha(200);
            questionTestData.setDrawCircles(false);

            int id = getResources().getIdentifier("graph"+i,"color",getPackageName());
            questionTestData.setFillColor(getResources().getColor(id));
            questionTestData.setColor(getResources().getColor(id));
            sets.add(questionTestData);
            LineData data2 = new LineData(questionTitles, sets);
            motionChart.setData(data2);
        }


        motionChart.setData(data);
        motionChart.getLineData().setDrawValues(false);
        XAxis xAxis = motionChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setLabelsToSkip(0);
        motionChart.getAxisLeft().setEnabled(false);
        motionChart.getAxisRight().setEnabled(false);
        motionChart.setDrawGridBackground(false);
        //motionChart.getLegend().setEnabled(false);
        motionChart.setBackgroundColor(getResources().getColor(R.color.dataBackground));
        motionChart.setDescription("");
        motionChart.setTouchEnabled(false);
        motionChart.invalidate();
    }
}
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
import android.location.LocationManager;
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
import com.github.mikephil.charting.components.Legend;
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
    private double[] distancePerHour;
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
                            trackLocation();
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

        // seconds per day * 1000
        if ( questionLevel == 0 ) notifDelay = 86400000;
            // seconds 6 hours * 1000
        else if ( questionLevel == 1) notifDelay = 21600000;
            // seconds per 3 hours * 1000
        else notifDelay = 10800000;

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
                        getSupportActionBar().setCustomView(R.layout.actionbar_layout);
                        getSupportActionBar().setTitle("Your Activity");
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

                                /* got from
                                http://stackoverflow.com/questions/10243557/how-to-slide-animation-between-two-activity-in-android*/

                                overridePendingTransition(R.anim.slide_in_rtl, R.anim.slide_out_rtl);
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
                        .setSmallIcon(R.drawable.miniicon)
                        .setContentTitle("Habitualizer")
                        .setContentText("It's time to post an update.")
                        .setVibrate(new long[] { 0, 1000 } )
                        .setLights(Color.MAGENTA, 3000, 3000);

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
            distancePerHour = db.getDistancePerHour();
            questionLevel = db.getQuestionSetting();
            for ( int i = 0; i < questions.size(); i++ ) {
                int qId = Integer.parseInt(questions.get(i).split(">>")[0]);
                float[] f = db.getAnswersWithTime(qId);
                yesPerHour.add(f);
            }
            return true;
        }

        protected void onPostExecute(Boolean result) {
            String addNew = "";
            for ( int i = 0; i < yesPerHour.size(); i++ ) {
                float max = 0;
                float maxNdx = 0;
                float total = 0;
                for ( int j = 0; j < 8; j++ ) {
                    total += yesPerHour.get(i)[j];
                    if ( yesPerHour.get(i)[j] > 0 ) {
                        if ( yesPerHour.get(i)[j] > max ) {
                            maxNdx = j;
                            max = yesPerHour.get(i)[j];
                        }
                    }
                }

                String time = utcToLocal(Math.round(maxNdx));
                String questionText = questions.get(i).split(">>")[1].substring(0, questions.get(i).split(">>")[1].length()-1);
                String questionWord = questionText.split(" ")[0];
                String questionAction = "";
                String[] question = questionText.split(" ");
                for ( int j = 2; j < question.length; j++ ) { questionAction += (" " + question[j]); }

                if ( questionWord.equals("Are")) {
                    for ( int j = 2; j < question.length; j++ ) {
                        //questionAction += (" " + question[j]);
                    }
                    if ( max > 0 ) {
                        addNew = "You're usually" + questionAction + " at " + time + ".";
                    }
                    else {
                        addNew = "You haven't been" + questionAction + " yet!";
                    }
                }
                else if ( questionWord.equals("Did") ) {
                    for ( int j = 2; j < question.length; j++ ) {
                        questionAction += (" " + question[j]);
                    }
                    float howOften = (max / total) * 100;
                    if ( howOften > 75 ) {
                        addNew = "You" + questionAction + " very consistently.";
                    }
                    else if ( howOften > 50 && howOften < 75 ) {
                        addNew = "You" + questionAction
                                + " frequently.";
                    }
                    else if ( howOften > 25 && howOften < 50 ) {
                        addNew = "You don't" + questionAction + " do this often.";
                    }
                    else {
                        addNew = "You didn't" + questionAction + ".";
                    }
                }

                int id = getResources().getIdentifier("answer"+i,"id",getPackageName());
                int id2 = getResources().getIdentifier("sep"+i,"id",getPackageName());
                int id3 = getResources().getIdentifier("buff"+i,"id",getPackageName());
                TextView tv = (TextView) findViewById(id);
                tv.setText(addNew);
                tv.setPadding(0, 100, 0, 100);
                tv.setTextColor(getResources().getColor(R.color.gray));
                tv.setTextSize(16);
                tv.setVisibility(View.VISIBLE);
                View v = findViewById(id2);
                View v2 = findViewById(id3);
                v2.setVisibility(View.VISIBLE);
                v.setVisibility(View.VISIBLE);
            }
            initGraph();
            trackLocation();
        }
    }

    void initGraph() {

        // Setting up graph

        // MOTION

        motionChart = (LineChart) findViewById(R.id.motion_chart);
        ArrayList<Entry> vals1 = new ArrayList<>();
        ArrayList titles = new ArrayList<>();
        ArrayList<LineDataSet> sets = new ArrayList<>();
        int maxNdx = 0;
        float max = 0;
        for ( int i = 0; i < 8; i++ ) {
            if ( relMotion[i] > max ) {
                max = relMotion[i];
                maxNdx = i;
            }
            if ( relMotion[i] >= 90 ) relMotion[i] -= 10;
            Entry e = new Entry(relMotion[i], i);
            vals1.add(e);

            String time = utcToLocal(i);
            titles.add(time + "");
        }

        String maxNdxStr = utcToLocal(maxNdx);

        TextView motionAnalysis = (TextView) findViewById(R.id.motion_analysis);
        motionAnalysis.setText("You are most active around " + maxNdxStr + ".");
        motionAnalysis.setPadding(0, 100, 0, 100);
        motionAnalysis.setTextColor(getResources().getColor(R.color.graph0));
        motionAnalysis.setTextSize(21);

        LineDataSet lds = new LineDataSet(vals1, "Motion");
        sets.add(lds);
        LineData data = new LineData(titles, sets);
        lds.setLineWidth(0);
        lds.setDrawCubic(true);
        lds.setCubicIntensity(0.3f);
        lds.setDrawFilled(true);
        lds.setFillColor(getResources().getColor(R.color.graph0));
        lds.setColor(getResources().getColor(R.color.graph0));
        lds.setFillAlpha(150);
        lds.setDrawCircles(false);

        // QUESTION GRAPHING

        for ( int i = 0; i < yesPerHour.size(); i++ ) {
            String question = questions.get(i).split(">>")[1];
            if ( question.split(" ")[0].equals("Are") ) {
                int thisTotal = 0;
                ArrayList<Entry> questionVals = new ArrayList<>();
                ArrayList questionTitles = new ArrayList<>();
                float[] qtest = yesPerHour.get(i);
                for ( int j = 0; j < 8; j++ ) {
                    thisTotal += qtest[j];
                }
                for ( int j = 0; j < 8; j++ ) {
                    float percent = (qtest[j] / thisTotal) * 100;
                    Entry e = new Entry(percent,j);
                    questionVals.add(e);
                    questionTitles.add(question);
                }

                LineDataSet questionTestData = new LineDataSet(questionVals, questions.get(i).split(">>")[1]);
                questionTestData.setLineWidth(0);
                questionTestData.setDrawCubic(true);
                questionTestData.setCubicIntensity(0.3f);
                questionTestData.setDrawFilled(true);
                questionTestData.setFillAlpha(150);
                questionTestData.setDrawCircles(false);

                int id = getResources().getIdentifier("graph"+(i+1),"color",getPackageName());
                questionTestData.setFillColor(getResources().getColor(id));
                questionTestData.setColor(getResources().getColor(id));
                sets.add(questionTestData);
                LineData data2 = new LineData(questionTitles, sets);
                motionChart.setData(data2);
            }
        }

        // DISTANCE GRAPHING

        double totalDist = 0;
        double maxDist = 0;
        int maxDistHr = 0;
        for ( int i = 0; i < 8; i++ ) {
            if ( distancePerHour[i] > maxDist ) {
                totalDist += distancePerHour[i];
                maxDist = distancePerHour[i];
                maxDistHr = i;
            }
        }

        TextView distanceAnalysis = (TextView) findViewById(R.id.dist_analysis);
        if ( maxDist < 1 ) distanceAnalysis.setText("You haven't left home!");
        else distanceAnalysis.setText("You travel farthest at " + utcToLocal(maxDistHr) + ".");
        distanceAnalysis.setPadding(0, 100, 0, 100);
        distanceAnalysis.setTextColor(getResources().getColor(R.color.graph7));
        distanceAnalysis.setTextSize(21);

        ArrayList<Entry> distances = new ArrayList<>();
        ArrayList txt = new ArrayList<>();
        for ( int i = 0; i < 8; i++ ) {
            Entry e = new Entry((float)(distancePerHour[i]/totalDist)*100,i);
            distances.add(e);
            txt.add("");
        }

        LineDataSet distanceData = new LineDataSet(distances, "");
        distanceData.setLineWidth(0);
        distanceData.setDrawCubic(true);
        distanceData.setCubicIntensity(0.3f);
        distanceData.setDrawFilled(true);
        distanceData.setFillAlpha(150);
        distanceData.setDrawCircles(false);
        distanceData.setFillColor(getResources().getColor(R.color.graph7));
        distanceData.setColor(getResources().getColor(R.color.graph7));
        sets.add(distanceData);
        LineData data4 = new LineData(txt, sets);
        motionChart.setData(data4);

        YAxis yAxis = motionChart.getAxisLeft();
        yAxis.setStartAtZero(true);
        yAxis.setAxisMaxValue(110);
        XAxis xAxis = motionChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setLabelsToSkip(0);
        xAxis.setTextColor(getResources().getColor(R.color.custom_text_light));
        motionChart.setData(data);
        motionChart.getLineData().setDrawValues(false);
        motionChart.getAxisLeft().setEnabled(false);
        motionChart.getAxisRight().setEnabled(false);
        motionChart.setDrawGridBackground(false);
        motionChart.setBackgroundColor(getResources().getColor(R.color.dataBackground));
        motionChart.setDescription("");
        motionChart.setTouchEnabled(false);
        Legend legend = motionChart.getLegend();
        legend.setEnabled(false);
        motionChart.invalidate();
    }
    String utcToLocal(int index) {
        String time;
        TimeZone timezone = TimeZone.getDefault();
        int timeInt = Math.round((index + 1) * 3 - 3);
        int offset = (timezone.getOffset(0, Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.DAY_OF_WEEK, timeInt * 3600000) / 3600000) + 1;
        if ( (timeInt + offset) < 0 ) timeInt += 24;
        int localTime = timeInt + offset;
        if ( localTime < 13 ) {
            time = localTime + " am";
            if ( localTime == 0 ) {
                time = "12 am";
            }
        }
        else {
            time = (localTime - 12) + " pm";
        }
        return time;
    }
    void trackLocation() {
        LocationManager l = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        android.location.Location loc = l.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longi = loc.getLongitude();
        double lati = loc.getLatitude();

        final Handler handler = new Handler();
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
        TrackLocation trackLocation = new TrackLocation(longi, lati);
        trackLocation.execute();
    }
    private class TrackLocation extends AsyncTask<String, Void, Boolean> {
        private double longi, lati;

        public TrackLocation(double longi, double lati) {
            this.longi = longi;
            this.lati = lati;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Database db = new Database(c);
            db.recordDistance(longi, lati);
            Log.d("yes?", "y");
            return true;
        }

        protected void onPostExecute(Boolean result) {
        }
    }
}
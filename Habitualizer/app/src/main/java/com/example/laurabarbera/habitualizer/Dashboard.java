package com.example.laurabarbera.habitualizer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;


public class Dashboard extends ActionBarActivity {

    private final int RESET_DISPLAY_LENGTH = 500;
    final Context c = this;
    String curName,
           curQuestionSetting,
           curLocationSetting,
           curMotionSetting,
           curBatterySetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        init();
        setListeners();

        // click reset data button
        Button reset = (Button)findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                resetData();
            }
        });
    }
    private class LoadPrefs extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            UserProfile u = new UserProfile(getSharedPreferences(Globals.SHARED_PREFERENCES, MODE_PRIVATE));
            curName = u.getName();
            curBatterySetting = u.getBatterySetting(c);
            curLocationSetting = u.getLocation(c);
            curMotionSetting = u.getMotionSetting(c);
            curQuestionSetting = u.getQuestionLevel(c);
            return true;
        }
        protected void onPostExecute(Boolean result) {
            TextView name = (TextView)findViewById(R.id.confirmName);
            TextView questionSetting = (TextView)findViewById(R.id.notificationSetting);
            TextView locationSetting = (TextView)findViewById(R.id.locationSetting);
            TextView motionSetting = (TextView)findViewById(R.id.motionSetting);
            TextView batterySetting = (TextView)findViewById(R.id.batterySetting);
            name.setText(curName);
            questionSetting.setText(curQuestionSetting);
            locationSetting.setText(curLocationSetting);
            motionSetting.setText(curMotionSetting);
            batterySetting.setText(curBatterySetting);
        }
    }
    private class ResetData extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {

            UserProfile u = new UserProfile(getSharedPreferences(Globals.SHARED_PREFERENCES, MODE_PRIVATE));
            u.setName(null);
            u.setLocation(0);
            u.setMotion(0);
            u.setPerformancelevel(0);
            u.setQuestionLevel(0);

            return true;
        }
        protected void onPostExecute(Boolean result) {}
    }
    private class UpdateSetting extends AsyncTask<String, Void, Boolean> {
        public UpdateSetting(Object t) {
            this.t = t;
        }
        @Override
        protected Boolean doInBackground(String... params) {
            String s = (String) this.t;
            UserProfile u = new UserProfile(getSharedPreferences(Globals.SHARED_PREFERENCES, MODE_PRIVATE));
            if ( s.equals(Globals.NAME_SETTING) ) {
                u.setName("test");
            }
//            else if ( s.equals(Globals.QUESTIONS_SETTING) ) {
//                u.setQuestionLevel(2);
//            }
//            else if ( s.equals(Globals.PERFORMANCE_SETTING) ) {
//                u.setPerformancelevel(2);
//            }
//            else if ( s.equals(Globals.MOTION_SETTING) ) {
//                u.setMotion(1);
//            }
//            else if ( s.equals(Globals.LOCATION_SETTING) ) {
//                u.setLocation(1);
//            }
            return true;
        }
        protected void onPostExecute(Boolean result) {
            LoadPrefs loadPrefs = new LoadPrefs();
            loadPrefs.execute();
        }
        private Object t;
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
                    }
                });
            }
        };
        Thread t = new Thread(r);
        t.start();
        LoadPrefs loadPrefs = new LoadPrefs();
        loadPrefs.execute();
    }
    public void resetData() {

        final Handler handler = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        new AlertDialog.Builder(c)
                                .setMessage(R.string.error_reset)
                                .setPositiveButton(R.string.error_yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new Handler().postDelayed(new Runnable() {
                                            // make it seem like it's deleting a lot of stuff by delaying for a moment
                                            @Override
                                            public void run() {
                                                Intent returnBack = new Intent(Dashboard.this, FacebookLogin.class);
                                                Dashboard.this.startActivity(returnBack);
                                                Dashboard.this.finish();
                                            }
                                        }, RESET_DISPLAY_LENGTH);
                                    }
                                })
                                .setNegativeButton(R.string.error_no, null)
                                .show();
                    }
                });
            }
        };

        Thread t = new Thread(r);
        t.start();

        ResetData resetData = new ResetData();
        resetData.execute();
    }
    public void updateSetting(final Object tag) {
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
        UpdateSetting updateSetting = new UpdateSetting(tag);
        updateSetting.execute();
    }
    public void setListeners() {
        RelativeLayout settingsView = (RelativeLayout) findViewById(R.id.settings_layout);
        ArrayList<TextView> allSettings = new ArrayList<>();

        for ( int i = 0; i < settingsView.getChildCount(); i++ ) {
            View v = settingsView.getChildAt(i);
            if ( v instanceof TextView ) {
                allSettings.add((TextView) settingsView.getChildAt(i) );
            }
        }
        OnClickListener nameClick = new OnClickListener() {
            public void onClick(View v) {
                Object tag = v.getTag();
                updateSetting(tag);
            }
        };
        for ( int i = 0; i < allSettings.size(); i++ ) {
            allSettings.get(i).setOnClickListener(nameClick);
        }
    }
}

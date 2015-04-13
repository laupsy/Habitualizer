package com.example.laurabarbera.habitualizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class Dashboard extends ActionBarActivity {

    private final int RESET_DISPLAY_LENGTH = 500;

    String curName,
           curQuestionSetting,
           curLocationSetting,
           curMotionSetting,
           curBatterySetting;

    final Context c = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        init();

        Button reset = (Button)findViewById(R.id.reset);

//        UserProfile u = new UserProfile(getSharedPreferences("userProfile", MODE_PRIVATE));
//        String username = u.getName();
//        name.setText(username);

        reset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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
        });

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
        Thread t1 = new Thread(r);
        t1.start();
        LoadPrefs loadPrefs = new LoadPrefs();
        loadPrefs.execute();
    }
    private class LoadPrefs extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            UserProfile u = new UserProfile(getSharedPreferences("userProfile", MODE_PRIVATE));
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

            UserProfile u = new UserProfile(getSharedPreferences("userProfile", MODE_PRIVATE));
            u.setName(null);

            return true;
        }
        protected void onPostExecute(Boolean result) {}
    }
    private class UpdateName extends AsyncTask<String, Void, Boolean> {
        public UpdateName(String newName) {
            this.newName = newName;
        }
        @Override
        protected Boolean doInBackground(String... params) {
            UserProfile u = new UserProfile(getSharedPreferences("userProfile", MODE_PRIVATE));
            u.setName(this.newName);
            return true;
        }
        protected void onPostExecute(Boolean result) {}
        private String newName;
    }
    private class UpdateQuestions extends AsyncTask<String, Void, Boolean> {
        public UpdateQuestions(int questionLevel) {
            this.questionLevel = questionLevel;
        }
        @Override
        protected Boolean doInBackground(String... params) {
            UserProfile u = new UserProfile(getSharedPreferences("userProfile", MODE_PRIVATE));
            u.setQuestionLevel(this.questionLevel);
            return true;
        }
        protected void onPostExecute(Boolean result) {}
        private int questionLevel;
    }

    public void init() {
        ActionBar a = getSupportActionBar();
        setTitle(R.string.getstarted);
        a.setElevation(0);
        a.setDisplayHomeAsUpEnabled(false);
        a.setDisplayShowHomeEnabled(false);
        a.setDisplayShowCustomEnabled(true);
        a.setDisplayShowTitleEnabled(false);
        getSupportActionBar().setCustomView(R.layout.actionbar_layout);

    }
}

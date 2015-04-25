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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

// Menu that handles all user settings

public class Dashboard extends ActionBarActivity {

    private final int RESET_DISPLAY_LENGTH = 500;
    private final Context c = this;

    String curName,
            curQuestionSetting,
            curLocationSetting,
            curMotionSetting,
            curBatterySetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        // Load the preferences, load the interface
        init();
        // Set up the listeners
        setListeners();
        // Reset all data if you click the reset button - brings back to setup
        Button reset = (Button)findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                resetData();
            }
        });
    }

    // Background Thread
    // Get the data from shared preferences and update the UI
    private class LoadPrefs extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            UserProfile u = new UserProfile(c, getSharedPreferences(c.getString(R.string.SHARED_PREFERENCES), MODE_PRIVATE));
            curName = u.getName();
            curBatterySetting = u.getBatterySetting();
            curLocationSetting = u.getLocation();
            curMotionSetting = u.getMotionSetting();
            curQuestionSetting = u.getQuestionLevel();
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

            // Debug mode
            Button debug = (Button) findViewById(R.id.debug_button);
            if ( curName.equals("Developer") ) {
                debug.setVisibility(View.VISIBLE);
            }
            else {
                debug.setVisibility(View.INVISIBLE);
            }
            debug.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    Intent dbug = new Intent(Dashboard.this, Debug.class);
                    Dashboard.this.startActivity(dbug);
                    Dashboard.this.finish();
                }
            });
        }
    }
    // Reset all of the shared preferences to default settings
    private class ResetData extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {

            UserProfile u = new UserProfile(c, getSharedPreferences(c.getString(R.string.SHARED_PREFERENCES), MODE_PRIVATE));
            u.setName("");
            u.setLocation("");
            u.setQuestionLevel("");
            u.setPerformancelevel("");
            u.setMotion("");

            return true;
        }
        protected void onPostExecute(Boolean result) {}
    }
    // Update a shared preference from user
    private class UpdateSetting extends AsyncTask<String, Void, Boolean> {
        public UpdateSetting(Object t) {
            this.t = t;
        }
        @Override
        protected Boolean doInBackground(String... params) {
            return true;
        }
        protected void onPostExecute(Boolean result) {
            String s = (String) this.t;
            Intent goTo;
            String className = "com.example.laurabarbera.habitualizer." + s.substring(0,1).toUpperCase() + s.substring(1);
            Log.d(className,"D");
            // Go to the setting page and modify the setting
            try {
                goTo = new Intent(Dashboard.this, Class.forName(className));
                goTo.putExtra("IS_SETUP",false);
            }
            catch(Exception e) { goTo = new Intent(Dashboard.this, Dashboard.class); }
            Dashboard.this.startActivity(goTo);
            Dashboard.this.finish();
            LoadPrefs loadPrefs = new LoadPrefs();
            loadPrefs.execute();
        }
        private Object t;
    }
    // UI Thread
    // Load the UI and set some default UI settings
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
                                Intent visualizer = new Intent(Dashboard.this, Visualizer.class);
                                Dashboard.this.startActivity(visualizer);
                                Dashboard.this.finish();
                            }
                        });
                    }
                });
            }
        };
        Thread t = new Thread(r);
        t.start();
        LoadPrefs loadPrefs = new LoadPrefs();
        loadPrefs.execute();
    }
    // Go back to setup page
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
                                                Intent returnBack = new Intent(Dashboard.this, Config.class);
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
    // Reflect updates on settings page
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
    // Set buttons to listen for taps
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

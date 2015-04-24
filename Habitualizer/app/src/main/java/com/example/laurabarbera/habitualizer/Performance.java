package com.example.laurabarbera.habitualizer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class Performance extends ActionBarActivity {

    private final Context c = this;
    private String curSetting;
    private String newSetting;
    private TextView low;
    private TextView med;
    private TextView high;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performanc);
        low = (TextView) findViewById(R.id.batteryLow);
        med = (TextView) findViewById(R.id.batteryMed);
        high = (TextView) findViewById(R.id.batteryHigh);
        init();
        Button update = (Button) findViewById(R.id.save_setting);
        Button cancel = (Button) findViewById(R.id.cancel_setting);
        low.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                newSetting = (String) low.getText();
                select();
            }
        });
        med.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                newSetting = (String) med.getText();
                select();
            }
        });
        high.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                newSetting = (String) high.getText();
                select();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // Cancel any changes
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
                SetSetting setSetting = new SetSetting(curSetting);
                setSetting.execute();
            }
        });
        update.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                final Handler handler = new Handler();
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        handler.post(new Runnable(){
                            public void run(){

                            }
                        });
                    }
                };
                Thread t = new Thread(r);
                t.start();
                SetSetting setSetting = new SetSetting(newSetting);
                setSetting.execute();
            }
        });
    }

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
                    }
                });
            }
        };
        Thread t = new Thread(r);
        t.start();
        LoadSetting loadSetting = new LoadSetting();
        loadSetting.execute();
    }

    private class LoadSetting extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            UserProfile u = new UserProfile(c, getSharedPreferences(c.getString(R.string.SHARED_PREFERENCES), MODE_PRIVATE));
            curSetting = u.getBatterySetting(c);
            return true;
        }
        protected void onPostExecute(Boolean result) {
            if ( curSetting == c.getResources().getString(R.string.batterySetting_low)) {
                low.setBackgroundColor(c.getResources().getColor(R.color.selected));
                med.setBackgroundColor(c.getResources().getColor(R.color.unselected));
                high.setBackgroundColor(c.getResources().getColor(R.color.unselected));
            }
            else if ( curSetting == c.getResources().getString(R.string.batterySetting_medium)) {
                low.setBackgroundColor(c.getResources().getColor(R.color.unselected));
                med.setBackgroundColor(c.getResources().getColor(R.color.selected));
                high.setBackgroundColor(c.getResources().getColor(R.color.unselected));
            }
            else {
                low.setBackgroundColor(c.getResources().getColor(R.color.unselected));
                med.setBackgroundColor(c.getResources().getColor(R.color.unselected));
                high.setBackgroundColor(c.getResources().getColor(R.color.selected));
            }
        }
    }

    private class SetSetting extends AsyncTask<String, Void, Boolean> {
        private String n;
        public SetSetting(String n) {
            this.n = n;
        }
        @Override
        protected Boolean doInBackground(String... params) {
            UserProfile u = new UserProfile(c, getSharedPreferences(c.getString(R.string.SHARED_PREFERENCES), MODE_PRIVATE));
            u.setPerformancelevel(n);
            return true;
        }
        protected void onPostExecute(Boolean result) {
            // Go back
            Intent goToSettings = new Intent(Performance.this, Dashboard.class);
            Performance.this.startActivity(goToSettings);
            Performance.this.finish();
        }
    }

    private void select(){

        if ( newSetting == c.getResources().getString(R.string.batterySetting_low)) {
            low.setBackgroundColor(c.getResources().getColor(R.color.selected));
            med.setBackgroundColor(c.getResources().getColor(R.color.unselected));
            high.setBackgroundColor(c.getResources().getColor(R.color.unselected));
        }
        else if ( newSetting == c.getResources().getString(R.string.batterySetting_medium)) {
            low.setBackgroundColor(c.getResources().getColor(R.color.unselected));
            med.setBackgroundColor(c.getResources().getColor(R.color.selected));
            high.setBackgroundColor(c.getResources().getColor(R.color.unselected));
        }
        else {
            low.setBackgroundColor(c.getResources().getColor(R.color.unselected));
            med.setBackgroundColor(c.getResources().getColor(R.color.unselected));
            high.setBackgroundColor(c.getResources().getColor(R.color.selected));
        }
    }
}

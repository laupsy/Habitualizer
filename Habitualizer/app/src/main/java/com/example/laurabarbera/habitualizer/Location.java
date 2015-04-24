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


public class Location extends ActionBarActivity {

    private final Context c = this;
    private String curSetting;
    private String newSetting;
    private TextView on;
    private TextView off;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        on = (TextView) findViewById(R.id.locationOn);
        off = (TextView) findViewById(R.id.locationOff);
        init();
        Button update = (Button) findViewById(R.id.save_setting);
        Button cancel = (Button) findViewById(R.id.cancel_setting);
        on.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                newSetting = (String) on.getText();
                select();
            }
        });
        off.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                newSetting = (String) off.getText();
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
            curSetting = u.getLocation(c);
            return true;
        }
        protected void onPostExecute(Boolean result) {
            if ( curSetting == c.getResources().getString(R.string.locationSetting_on)) {
                on.setBackgroundColor(c.getResources().getColor(R.color.selected));
                off.setBackgroundColor(c.getResources().getColor(R.color.unselected));
            }
            else if ( curSetting == c.getResources().getString(R.string.locationSetting_off)) {
                on.setBackgroundColor(c.getResources().getColor(R.color.unselected));
                off.setBackgroundColor(c.getResources().getColor(R.color.selected));
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
            u.setLocation(n);
            return true;
        }
        protected void onPostExecute(Boolean result) {
            // Go back
            Intent goToSettings = new Intent(Location.this, Dashboard.class);
            Location.this.startActivity(goToSettings);
            Location.this.finish();
        }
    }

    private void select(){

        if ( newSetting == c.getResources().getString(R.string.locationSetting_on)) {
            on.setBackgroundColor(c.getResources().getColor(R.color.selected));
            off.setBackgroundColor(c.getResources().getColor(R.color.unselected));
        }
        else {
            on.setBackgroundColor(c.getResources().getColor(R.color.unselected));
            off.setBackgroundColor(c.getResources().getColor(R.color.selected));
        }
    }
}

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


public class Motion extends ActionBarActivity {

    private final Context c = this;
    private String curSetting;
    private String newSetting;
    private TextView on;
    private TextView off;

    private boolean is_setup = false;
    private Class nextStep = Dashboard.class;
    private Button update, cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion);
        on = (TextView) findViewById(R.id.motionOn);
        off = (TextView) findViewById(R.id.motionOff);
        init();
        TextView setupHeader = (TextView) findViewById(R.id.setup_header);
        is_setup = getIntent().getExtras().getBoolean("IS_SETUP");
        update = (Button) findViewById(R.id.save_setting);
        cancel = (Button) findViewById(R.id.cancel_setting);
        if ( is_setup ) {
            cancel.setVisibility(View.INVISIBLE);
            update.setText(R.string.button_next);
            nextStep = Location.class;
        }
        else {
            setupHeader.setVisibility(View.GONE);
        }
        on.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                newSetting = (String) on.getText();
                select(newSetting);
            }
        });
        off.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                newSetting = (String) off.getText();
                select(newSetting);
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
            curSetting = u.getMotionSetting();
            return true;
        }
        protected void onPostExecute(Boolean result) {
            if ( !is_setup ) {
                TextView head = (TextView) findViewById(R.id.motion_setting_header);
                head.setText(head.getText() + ": " + curSetting);
                select(curSetting);
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
            u.setMotion(n);
            return true;
        }
        protected void onPostExecute(Boolean result) {
            // Go back
            Intent goTo = new Intent(Motion.this, nextStep);
            if ( is_setup ) goTo.putExtra("IS_SETUP",true);
            Motion.this.startActivity(goTo);
            Motion.this.finish();
        }
    }

    private void select(String match){

        if ( match.equals(c.getResources().getString(R.string.motionSetting_on))) {
            on.setBackgroundColor(c.getResources().getColor(R.color.selected));
            off.setBackgroundColor(c.getResources().getColor(R.color.unselected));
        }
        else {
            on.setBackgroundColor(c.getResources().getColor(R.color.unselected));
            off.setBackgroundColor(c.getResources().getColor(R.color.selected));
        }

        if ( !match.equals(curSetting) ) {
            if (is_setup) {
                update.setBackgroundResource(R.drawable.button_start);
                update.setText(R.string.button_next);
            } else {
                update.setBackgroundResource(R.drawable.button);
                update.setText(R.string.save);
            }
            update.setTextColor(getResources().getColor(R.color.button_light_text));
            update.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final Handler handler = new Handler();
                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            handler.post(new Runnable() {
                                public void run() {
                                    if (newSetting == null) newSetting = curSetting;
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
    }
}

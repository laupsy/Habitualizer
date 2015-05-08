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


public class Questions extends ActionBarActivity {

    private final Context c = this;
    private String curSetting;
    private String newSetting;
    private TextView low;
    private TextView med;
    private TextView high;

    private boolean is_setup = false;
    private Class nextStep = Dashboard.class;
    Button update, cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        TextView setupHeader = (TextView) findViewById(R.id.setup_header);
        low = (TextView) findViewById(R.id.notifLow);
        med = (TextView) findViewById(R.id.notifMed);
        high = (TextView) findViewById(R.id.notifHigh);
        init();
        is_setup = getIntent().getExtras().getBoolean("IS_SETUP");
        update = (Button) findViewById(R.id.save_setting);
        cancel = (Button) findViewById(R.id.cancel_setting);
        if ( is_setup ) {
            cancel.setVisibility(View.INVISIBLE);
            update.setText(R.string.button_next);
            nextStep = Motion.class;
        }
        else {
            setupHeader.setVisibility(View.GONE);
        }
        low.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                newSetting = (String) low.getText();
                select(newSetting);
            }
        });
        med.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                newSetting = (String) med.getText();
                select(newSetting);
            }
        });
        high.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                newSetting = (String) high.getText();
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
            Database db = new Database(c);
            if ( db.getQuestionSetting() == 1 ) curSetting = c.getResources().getString(R.string.notificationSetting_medium);
            else if ( db.getQuestionSetting() == 2 ) curSetting = c.getResources().getString(R.string.notificationSetting_high);
            else curSetting = c.getResources().getString(R.string.notificationSetting_low);
            return true;
        }
        protected void onPostExecute(Boolean result) {
            if ( !is_setup ) {
                TextView head = (TextView) findViewById(R.id.question_setting_header);
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
            int i = 0;
            if ( n.equals(c.getResources().getString(R.string.notificationSetting_medium)) ) i = 1;
            else if ( n.equals(c.getResources().getString(R.string.notificationSetting_high)) ) i = 2;
            Database db = new Database(c);
            db.setQuestionSetting(i);
            return true;
        }
        protected void onPostExecute(Boolean result) {
            // Go back
            Intent goTo = new Intent(Questions.this, nextStep);
            if ( is_setup ) goTo.putExtra("IS_SETUP",true);
            Questions.this.startActivity(goTo);
            Questions.this.finish();
        }
    }

    private void select(String match){

        if ( match.equals(c.getResources().getString(R.string.notificationSetting_low))) {
            low.setBackgroundColor(c.getResources().getColor(R.color.selected));
            med.setBackgroundColor(c.getResources().getColor(R.color.unselected));
            high.setBackgroundColor(c.getResources().getColor(R.color.unselected));
        }
        else if ( match.equals(c.getResources().getString(R.string.notificationSetting_medium))) {
            low.setBackgroundColor(c.getResources().getColor(R.color.unselected));
            med.setBackgroundColor(c.getResources().getColor(R.color.selected));
            high.setBackgroundColor(c.getResources().getColor(R.color.unselected));
        }
        else if ( match.equals(c.getResources().getString(R.string.notificationSetting_high)))  {
            low.setBackgroundColor(c.getResources().getColor(R.color.unselected));
            med.setBackgroundColor(c.getResources().getColor(R.color.unselected));
            high.setBackgroundColor(c.getResources().getColor(R.color.selected));
        }

        if ( !match.equals(curSetting)) {
            if ( is_setup ) {
                update.setBackgroundResource(R.drawable.button_start);
                update.setText(R.string.button_next);
            }
            else {
                update.setBackgroundResource(R.drawable.button);
                update.setText(R.string.save);
            }
            update.setTextColor(getResources().getColor(R.color.button_light_text));
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
    }
}

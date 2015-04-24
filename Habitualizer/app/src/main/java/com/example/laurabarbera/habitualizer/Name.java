package com.example.laurabarbera.habitualizer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class Name extends ActionBarActivity {

    private final Context c = this;
    private String curName;
    private EditText in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        init();
        Button update = (Button) findViewById(R.id.save_setting);
        Button cancel = (Button) findViewById(R.id.cancel_setting);
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
                SetName setName = new SetName(curName);
                setName.execute();
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
                SetName setName = new SetName(in.getText().toString());
                setName.execute();
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
        LoadName loadName = new LoadName();
        loadName.execute();
    }

    private class LoadName extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            UserProfile u = new UserProfile(c, getSharedPreferences(c.getString(R.string.SHARED_PREFERENCES), MODE_PRIVATE));
            curName = u.getName();
            return true;
        }
        protected void onPostExecute(Boolean result) {
            in = (EditText) findViewById(R.id.name_input);
            in.setHint(curName);
        }
    }

    private class SetName extends AsyncTask<String, Void, Boolean> {
        private String n;
        public SetName(String n) {
            this.n = n;
        }
        @Override
        protected Boolean doInBackground(String... params) {
            UserProfile u = new UserProfile(c, getSharedPreferences(c.getString(R.string.SHARED_PREFERENCES), MODE_PRIVATE));
            u.setName(n);
            return true;
        }
        protected void onPostExecute(Boolean result) {
            // Go back
            Intent goToSettings = new Intent(Name.this, Dashboard.class);
            Name.this.startActivity(goToSettings);
            Name.this.finish();
        }
    }
}

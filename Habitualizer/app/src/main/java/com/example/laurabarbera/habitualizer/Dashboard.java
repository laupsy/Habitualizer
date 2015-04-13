package com.example.laurabarbera.habitualizer;

import android.app.Activity;
import android.app.AlertDialog;
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


public class Dashboard extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        init();

        final Context context = this;

        Button reset = (Button)findViewById(R.id.reset);
        TextView name = (TextView)findViewById(R.id.confirmName);
        UserProfile u = new UserProfile(getSharedPreferences("userProfile", MODE_PRIVATE));
        String username = u.getName();
        name.setText(username);

        reset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final Handler handler = new Handler();
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() {
                            public void run() {
                                new AlertDialog.Builder(context)
                                        .setMessage(R.string.error_reset)
                                        .setPositiveButton(R.string.error_yes, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent returnBack = new Intent(Dashboard.this, FacebookLogin.class);
                                                Dashboard.this.startActivity(returnBack);
                                                Dashboard.this.finish();
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

                SaveData save = new SaveData();
                save.execute();
            }
        });
    }
    private class SaveData extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            SharedPreferences userProfile = getSharedPreferences("userProfile", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = userProfile.edit();
            editor.putString("name",null);
            editor.commit();

            return true;
        }
        protected void onPostExecute(Boolean result) {
        }
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

package com.example.laurabarbera.habitualizer;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;


public class Visualizer extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizer);
        init();
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
                            }
                        });
                    }
                });
            }
        };
        Thread t = new Thread(r);
        t.start();
    }
}

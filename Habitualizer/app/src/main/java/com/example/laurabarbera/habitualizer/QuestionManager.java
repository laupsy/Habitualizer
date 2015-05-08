package com.example.laurabarbera.habitualizer;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class QuestionManager extends ActionBarActivity {

    private Context c;
    private ArrayList<String> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_manager);
        c = this;
        Button add = (Button)findViewById(R.id.add);

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
                                Intent visualizer = new Intent(QuestionManager.this, Visualizer.class);
                                QuestionManager.this.startActivity(visualizer);
                                QuestionManager.this.finish();
                            }
                        });
                        ImageView icon = (ImageView) findViewById(R.id.home_icon);
                        icon.setBackgroundResource(R.drawable.icon_back);
                        icon.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                Intent back = new Intent(QuestionManager.this, Dashboard.class);
                                QuestionManager.this.startActivity(back);
                                QuestionManager.this.finish();
                            }
                        });
                    }
                });
            }
        };

        Thread t = new Thread(r);
        t.start();
        GetQuestions getQuestions = new GetQuestions();
        getQuestions.execute();

    }

    private class GetQuestions extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            Database db = new Database(c);
            questions = db.getQuestions();
            return true;
        }

        protected void onPostExecute(Boolean result) {
            LinearLayout qm = (LinearLayout) findViewById(R.id.qmanager_layout);
            for ( int i = 0; i < questions.size(); i++ ) {
                /* Got how to add views programmatically from here:
                    http://stackoverflow.com/questions/2395769/how-to-programmatically-add-views-to-views
                 */
                TextView t = new TextView(c);
                View d = new View(c);
                d.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        2
                ));
                d.setBackgroundColor(getResources().getColor(R.color.gray_light));
                t.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));

                t.setText("(" + (i+1) + ") " + questions.get(i));
                t.setPadding(
                        Math.round(getResources().getDimension(R.dimen.activity_horizontal_margin)),
                        Math.round(getResources().getDimension(R.dimen.header_margin)),
                        Math.round(getResources().getDimension(R.dimen.activity_horizontal_margin)),
                        Math.round(getResources().getDimension(R.dimen.header_margin))
                );
                t.setTextSize(16);
                t.setTextColor(getResources().getColor(R.color.label_text));
                qm.addView(t);
                qm.addView(d);
            }
        }
    }
}

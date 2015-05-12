package com.example.laurabarbera.habitualizer;

import android.app.NotificationManager;
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
import android.widget.TextView;

import java.util.ArrayList;


public class AskQuestion extends ActionBarActivity {

    private Context c;
    private String question;
    private int num;
    private TextView questionField;
    private Button yes;
    private Button no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_question);
        ActionBar a = getSupportActionBar();
        setTitle(R.string.getstarted);
        a.setElevation(0);
        a.setDisplayHomeAsUpEnabled(false);
        a.setDisplayShowHomeEnabled(false);
        a.setDisplayShowCustomEnabled(true);
        a.setDisplayShowTitleEnabled(false);
        getSupportActionBar().setCustomView(R.layout.actionbar_layout);
        c = this;

        yes = (Button) findViewById(R.id.Yes);
        no = (Button) findViewById(R.id.No);

        yes.setOnClickListener(new View.OnClickListener(){
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
                AnswerYes ay = new AnswerYes();
                ay.execute();
            }
        });
        no.setOnClickListener(new View.OnClickListener(){
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
                AnswerNo an = new AnswerNo();
                an.execute();
            }
        });

        final Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {

            }
        };

        Thread t = new Thread();
        t.start();
        GetQuestion getQuestion = new GetQuestion();
        getQuestion.execute();
    }

    private class GetQuestion extends AsyncTask<String, Void, Boolean> {
        private String uname;
        @Override
        protected Boolean doInBackground(String... params) {
            Database db = new Database(c);
            uname = db.getName();
            String q[] = db.getRandomQuestion().split(">>");
            num = Integer.parseInt(q[0]);
            question = q[1];
            return true;
        }

        protected void onPostExecute(Boolean result) {
            TextView tv = (TextView) findViewById(R.id.ask_name);
            tv.setText(uname + ",");
            questionField = (TextView) findViewById(R.id.question_field);
            questionField.setText(question);
        }
    }

    private class AnswerYes extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            Database db = new Database(c);
            db.answerYes(num);
            return true;
        }

        protected void onPostExecute(Boolean result) {
            Intent i = new Intent(AskQuestion.this, Visualizer.class);
            AskQuestion.this.startActivity(i);
            AskQuestion.this.finish();
            int mNotificationId = 001;
            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.cancel(mNotificationId);
        }
    }

    private class AnswerNo extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            Database db = new Database(c);
            db.answerNo(num);
            return true;
        }

        protected void onPostExecute(Boolean result) {
            Intent i = new Intent(AskQuestion.this, Visualizer.class);
            AskQuestion.this.startActivity(i);
            AskQuestion.this.finish();
            int mNotificationId = 001;
            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.cancel(mNotificationId);
        }
    }

}

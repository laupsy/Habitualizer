package com.example.laurabarbera.habitualizer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class AskQuestion extends ActionBarActivity {

    private Context c;
    private String question;
    private TextView questionField;

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
        @Override
        protected Boolean doInBackground(String... params) {
            Database db = new Database(c);
            question = db.getRandomQuestion();
            return true;
        }

        protected void onPostExecute(Boolean result) {
            questionField = (TextView) findViewById(R.id.question_field);
            questionField.setText(question);
        }
    }

}

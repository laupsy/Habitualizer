package com.example.laurabarbera.habitualizer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class QuestionManager extends ActionBarActivity {

    private Context c;
    private ArrayList<String> questions;
    private RelativeLayout createNew;
    private EditText createNewInput;
    private LinearLayout questionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_manager);
        c = this;
        Button add = (Button)findViewById(R.id.add);
        createNew = (RelativeLayout) findViewById(R.id.create_new);
        createNewInput = (EditText) findViewById(R.id.create_new_input);
        questionList = (LinearLayout) findViewById(R.id.qmanager_layout);

        Button createNewButton = (Button) findViewById(R.id.add);

        createNewButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createNew.setVisibility(View.VISIBLE);
                        /* COPY/PASTED FROM http://stackoverflow.com/questions/5105354/how-to-show-soft-keyboard-when-edittext-is-focused */
                createNewInput.setFocusableInTouchMode(true);
                createNewInput.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(createNewInput, InputMethodManager.SHOW_IMPLICIT);
            }
        });

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

        saveQuestion(createNewInput);

    }

    private class GetQuestions extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            Database db = new Database(c);
            questions = db.getQuestions();
            return true;
        }

        protected void onPostExecute(Boolean result) {
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

                t.setText("(" + (i + 1) + ") " + questions.get(i).split(">>")[1]);
                t.setPadding(
                        Math.round(getResources().getDimension(R.dimen.activity_horizontal_margin)),
                        Math.round(getResources().getDimension(R.dimen.header_margin)),
                        Math.round(getResources().getDimension(R.dimen.activity_horizontal_margin)),
                        Math.round(getResources().getDimension(R.dimen.header_margin))
                );
                t.setTextSize(16);
                t.setTextColor(getResources().getColor(R.color.label_text));
                questionList.addView(t);
                questionList.addView(d);
            }
        }
    }

    public void saveQuestion(final EditText questionEntry) {

        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                String checkMe = dest.toString() + source.toString();
                Pattern pattern = Pattern.compile("(\\S+)");
                Matcher matcher = pattern.matcher(checkMe);
                boolean valid = matcher.matches();
                if ( !valid ) {
                    return "";
                }
                else {

                }
                return null;
            }
        };

        //questionEntry.setFilters(new InputFilter[]{filter});

        questionEntry.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        keyCode == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager imm =
                            (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(questionEntry.getWindowToken(), 0);
                    String question = questionEntry.getText().toString().substring(0,1).toUpperCase() + questionEntry.getText().toString().substring(1);

                    // Save name and do on background thread!

                    final Handler handler = new Handler();
                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            handler.post(new Runnable() {
                                public void run() {
                                    createNew.setVisibility(View.GONE);
                                }
                            });
                        }
                    };

                    Thread t = new Thread(r);
                    t.start();

                    SaveQuestion save = new SaveQuestion(question);
                    save.execute();

                }
                return false;
            }
        });
    }
    private class SaveQuestion extends AsyncTask<String, Void, Boolean> {

        private String question;
        private int id;

        public SaveQuestion(String question) {
            this.question = question;
        }

        @Override
        protected Boolean doInBackground(String... params) {

            Database db = new Database(c);
            id = db.getLastQuestionId() + 1;
            db.addQuestion(question, id);
            return true;
        }
        protected void onPostExecute(Boolean result) {

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

            t.setText("(" + id + ") " + question);
            t.setPadding(
                    Math.round(getResources().getDimension(R.dimen.activity_horizontal_margin)),
                    Math.round(getResources().getDimension(R.dimen.header_margin)),
                    Math.round(getResources().getDimension(R.dimen.activity_horizontal_margin)),
                    Math.round(getResources().getDimension(R.dimen.header_margin))
            );
            t.setTextSize(16);
            t.setTextColor(getResources().getColor(R.color.label_text));
            questionList.addView(t);
            questionList.addView(d);
        }
    }
}

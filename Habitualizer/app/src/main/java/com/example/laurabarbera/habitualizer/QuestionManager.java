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
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class QuestionManager extends ActionBarActivity {

    private Context c;
    private ArrayList<String> questions;
    private RelativeLayout createNew;
    private EditText createNewInput;
    private RelativeLayout questionList;
    private Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_manager);
        c = this;
        add = (Button)findViewById(R.id.add);
        createNew = (RelativeLayout) findViewById(R.id.create_new);
        createNewInput = (EditText) findViewById(R.id.create_new_input);
        questionList = (RelativeLayout) findViewById(R.id.qmanager_layout);

        Button createNewButton = (Button) findViewById(R.id.add);

        TextView h = (TextView) findViewById(R.id.question_manager_header);
        h.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                /* got hide keyboard from
                http://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard */
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(createNewInput.getWindowToken(), 0);
                TextView qhelp = (TextView) findViewById(R.id.q_help);
                qhelp.setVisibility(View.VISIBLE);
                EditText et = (EditText) findViewById(R.id.create_new_input);
                et.setText("");
                TextView header = (TextView) findViewById(R.id.question_manager_header);
                header.setText(getResources().getString(R.string.manageQuestions_header));
                createNew.setVisibility(View.GONE);
                ImageView close = (ImageView) findViewById(R.id.createnew_close);
                close.setVisibility(View.GONE);
                add.setVisibility(View.VISIBLE);
                try {
                    for ( int i = 0; i < questions.size(); i++ ) {
                        int textviewid = getResources().getIdentifier("q_"+i,"id",getPackageName());
                        TextView tv = (TextView) findViewById(textviewid);
                        int viewid = getResources().getIdentifier("sep_"+i,"id",getPackageName());
                        View vv = findViewById(viewid);
                        vv.setVisibility(View.VISIBLE);
                        tv.setVisibility(View.VISIBLE);
                    }
                } catch(Exception e){}
            }
        });

        createNewButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createNew.setVisibility(View.VISIBLE);
                        /* COPY/PASTED FROM http://stackoverflow.com/questions/5105354/how-to-show-soft-keyboard-when-edittext-is-focused */
                createNewInput.setFocusableInTouchMode(true);
                createNewInput.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(createNewInput, InputMethodManager.SHOW_IMPLICIT);
                TextView header = (TextView) findViewById(R.id.question_manager_header);
                TextView qhelp = (TextView) findViewById(R.id.q_help);
                qhelp.setVisibility(View.GONE);
                add.setVisibility(View.GONE);
                ImageView close = (ImageView) findViewById(R.id.createnew_close);
                close.setVisibility(View.VISIBLE);
                header.setText("Create New Question");
                try {
                    for ( int i = 0; i < questions.size(); i++ ) {
                        int textviewid = getResources().getIdentifier("q_"+i,"id",getPackageName());
                        int viewid = getResources().getIdentifier("sep_"+i,"id",getPackageName());
                        TextView tv = (TextView) findViewById(textviewid);
                        View vv = findViewById(viewid);
                        tv.setVisibility(View.GONE);
                        vv.setVisibility(View.GONE);
                    }
                } catch(Exception e){}
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

                                /* got from
                                http://stackoverflow.com/questions/10243557/how-to-slide-animation-between-two-activity-in-android*/

                                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                            }
                        });
                        ImageView icon = (ImageView) findViewById(R.id.home_icon);
                        icon.setBackgroundResource(R.drawable.icon_back);
                        icon.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                Intent back = new Intent(QuestionManager.this, Dashboard.class);
                                QuestionManager.this.startActivity(back);
                                QuestionManager.this.finish();

                                /* got from
                                http://stackoverflow.com/questions/10243557/how-to-slide-animation-between-two-activity-in-android*/

                                overridePendingTransition(R.anim.slide_in_rtl, R.anim.slide_out_rtl);
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

        private Database db;

        @Override
        protected Boolean doInBackground(String... params) {
            db = new Database(c);
            questions = db.getQuestions();
            return true;
        }

        protected void onPostExecute(Boolean result) {
            for ( int i = 0; i < questions.size(); i++ ) {
                int textviewid = getResources().getIdentifier("q_"+i,"id",getPackageName());
                int viewid = getResources().getIdentifier("sep_"+i,"id",getPackageName());
                final TextView t = (TextView) findViewById(textviewid);
                final View d = findViewById(viewid);
                d.setVisibility(View.VISIBLE);
                t.setVisibility(View.VISIBLE);
                t.setText(questions.get(i).split(">>")[1]);

                /* got how to implement an onTouchListener from
                http://stackoverflow.com/questions/11779082/listener-for-pressing-and-releasing-a-button */
                final int id = i+1;
                t.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(final View v, MotionEvent event) {
                        db.removeQuestion(id);
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                v.animate().translationX(100);
                                return true;
                            case MotionEvent.ACTION_UP:
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        v.animate().translationX(-1000);
                                    }
                                }, 250);
                                Handler handler2 = new Handler();
                                d.animate().alpha(0f);
                                handler2.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        t.setVisibility(View.GONE);
                                        d.setVisibility(View.GONE);
                                    }
                                }, 500);
                                return true;
                        }
                        return false;
                    }
                });
            }
        }
    }

    public void saveQuestion(final EditText questionEntry) {

        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                String checkMe = dest.toString() + source.toString();
                /* got the regular expression from
                http://stackoverflow.com/questions/15805555/java-regex-to-validate-full-name-allow-only-spaces-and-letters
                 */
                Pattern pattern = Pattern.compile("(^[\\\\p{L} .'-]+$)");
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
                    String question = "";
                    try {
                        question = questionEntry.getText().toString().substring(0,1).toUpperCase() + questionEntry.getText().toString().substring(1);
                    } catch(Exception e){}
                    if (question.length() < 3) {
                        Toast.makeText(c, "Invalid question!", Toast.LENGTH_LONG).show();
                    } else {

                        InputMethodManager imm =
                                (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(questionEntry.getWindowToken(), 0);
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

                }
                return false;
            }
        });
    }
    private class SaveQuestion extends AsyncTask<String, Void, Boolean> {

        private String question;
        private int id;
        private boolean worked;
        private Database db;

        public SaveQuestion(String question) {
            this.question = question;
        }

        @Override
        protected Boolean doInBackground(String... params) {

            db = new Database(c);
            id = db.getLastQuestionId() + 1;
            Log.d("THIS IS AN ", id + "");
            worked = db.addQuestion(question, id);
            return true;
        }
        protected void onPostExecute(Boolean result) {

            if ( worked ) {

                Intent i = new Intent(QuestionManager.this, QuestionManager.class);
                QuestionManager.this.startActivity(i);
                QuestionManager.this.finish();

                overridePendingTransition(R.anim.abc_fade_out, R.anim.abc_fade_in);
            }
        }
    }
}

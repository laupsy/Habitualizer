package com.example.laurabarbera.habitualizer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* This screen appears on user setup - if the user hasn't created a local profile yet,
the splash screen directs to this activity, which takes them through establishing name,
battery preferences, notification preferences, location preferences, and motion preferences.
These preferences all get saved in Shared Preferences.
 */

public class Config extends ActionBarActivity {

    private String username;
    private TextView nameView, welcome;
    private EditText nameEntry;
    private Context c;
    private Button proceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /* Sets up UI - removes action bar shadow, sets custom action bar view.
         */

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        ActionBar a = getSupportActionBar();
        setTitle(R.string.getstarted);
        a.setElevation(0);
        a.setDisplayHomeAsUpEnabled(false);
        a.setDisplayShowHomeEnabled(false);
        a.setDisplayShowCustomEnabled(true);
        a.setDisplayShowTitleEnabled(false);
        getSupportActionBar().setCustomView(R.layout.actionbar_layout);
        c = this;

        /* Identify the views that must be worked with in this activity.
         */

        nameEntry = (EditText)findViewById(R.id.name_input);
        nameView = (TextView)findViewById(R.id.hello);
        proceed = (Button)findViewById(R.id.login);
        welcome = (TextView)findViewById(R.id.welcome);

        /* Get a name input from the user, check that the input is valid,
        change the input to a proper noun if it's not a proper noun, and
        listen for the enter key as a motion to proceed to the next step.
        Save the name in the background thread on shared preferences.
         */

        setName(nameEntry);
    }

    public void setName(final EditText nameEntry) {

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

        nameEntry.setFilters(new InputFilter[]{filter});

        nameEntry.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        keyCode == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager imm =
                            (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(nameEntry.getWindowToken(), 0);
                    username = nameEntry.getText().toString().substring(0,1).toUpperCase() + nameEntry.getText().toString().substring(1);

                    // Save name and do on background thread!

                    final Handler handler = new Handler();
                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            handler.post(new Runnable() {
                                public void run() {
                                    nameEntry.setVisibility(View.GONE);
                                    welcome.setVisibility(View.GONE);
                                    proceed.setBackgroundResource(R.drawable.button_start);
                                    proceed.setText(R.string.button_next);
                                    proceed.setTextColor(getResources().getColor(R.color.button_light_text));
                                    proceed.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            Intent nextStep = new Intent(Config.this, Questions.class);
                                            nextStep.putExtra("IS_SETUP",true);
                                            Config.this.startActivity(nextStep);
                                            Config.this.finish();
                                        }
                                    });
                                }
                            });
                        }
                    };

                    Thread t = new Thread(r);
                    t.start();

                    SaveData save = new SaveData();
                    save.execute();

                }
                return false;
            }
        });
    }

    private class SaveData extends AsyncTask<String, Void, Boolean> {

        private String name;

        @Override
        protected Boolean doInBackground(String... params) {

            UserProfile u = new UserProfile(c, getSharedPreferences(c.getString(R.string.SHARED_PREFERENCES), Activity.MODE_PRIVATE));
            u.setName(username);
            name = u.getName();

            return true;
        }
        protected void onPostExecute(Boolean result) {
            nameView.setText(getResources().getString(R.string.greeting)
                    + ", " + name + ".");
        }
    }
}

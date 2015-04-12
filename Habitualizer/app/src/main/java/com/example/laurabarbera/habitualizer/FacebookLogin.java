package com.example.laurabarbera.habitualizer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.FacebookSdk;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FacebookLogin extends ActionBarActivity {

    private String username;
    private TextView nameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        setTitle("Get Started");
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setIcon(R.drawable.icon);

        final EditText nameEntry = (EditText)findViewById(R.id.name_input);
        nameView = (TextView)findViewById(R.id.hello);

        setName(nameEntry, nameView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_facebook_login, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void hideKeyboard(EditText te) {
        InputMethodManager imm =
                (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(te.getWindowToken(), 0);
    }
    public String convertToProperNoun(EditText te) {
        String name = te.getText().toString().substring(0,1).toUpperCase() +
                te.getText().toString().substring(1);
        return name;
    }
    public void setName(final EditText nameEntry, final TextView nameResult) {

        final Button proceed = (Button)findViewById(R.id.login);
        final TextView welcome = (TextView)findViewById(R.id.welcome);

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
                if ( (keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        keyCode == KeyEvent.KEYCODE_ENTER) {
                    hideKeyboard(nameEntry);
                    username = convertToProperNoun(nameEntry);

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
                                    proceed.setText(R.string.button_configure);
                                    proceed.setTextColor(getResources().getColor(R.color.button_light_text));
                                }
                            });
                        }
                    };

                    Thread t = new Thread(r);
                    t.start();

                    SaveData save = new SaveData();
                    save.execute();

                    // thread over

                }
                return false;
            }
        });
    }

    private class SaveData extends AsyncTask<String, Void, Boolean> {

        private String savedData = "boop";

        @Override
        protected Boolean doInBackground(String... params) {
            // background code, no UI stuff!


            SharedPreferences userProfile = getSharedPreferences("name", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = userProfile.edit();
            editor.putString("Name", username);
            editor.commit();
            savedData = userProfile.getString("Name","");

            if(true) {
                return true;
            }
            else {
                return false;
            }
        }
        protected void onPostExecute(Boolean result) {
            nameView.setText(getResources().getString(R.string.greeting)
                    + ", " + savedData + ".");
        }
    }
}

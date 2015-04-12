package com.example.laurabarbera.habitualizer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class Dashboard extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        SharedPreferences userProfile = getSharedPreferences("userProfile", Activity.MODE_PRIVATE);

        SharedPreferences shared = getSharedPreferences("userProfile",Activity.MODE_PRIVATE);
        String n = shared.getString("name","");

        getSupportActionBar().setElevation(0);
        Button reset = (Button)findViewById(R.id.reset);
        TextView name = (TextView)findViewById(R.id.confirmName);
        name.setText(n);
        reset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("fdfd", "fdd");
                SharedPreferences userProfile = getSharedPreferences("userProfile", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = userProfile.edit();
                editor.putString("name",null);
                editor.commit();
            }
        });
    }
}

package com.example.laurabarbera.habitualizer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;


public class Splash extends Activity {

    // wait time
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    private final Context c = this;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // check if this is a first time user
                Database db = new Database(c);
                String name = db.getName();
                Intent goTo;
                if ( name.length() < 1 ){
                    goTo = new Intent(Splash.this, Config.class);
                }
                else {
                    goTo = new Intent(Splash.this, Dashboard.class);
                }
                Intent mainIntent = goTo;
                Splash.this.startActivity(mainIntent);
                Splash.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

    }
}

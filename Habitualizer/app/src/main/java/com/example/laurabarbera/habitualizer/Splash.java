package com.example.laurabarbera.habitualizer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;


public class Splash extends Activity {

    // wait time
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // check if this is a first time user
                SharedPreferences shared = getSharedPreferences("userProfile",Activity.MODE_PRIVATE);
                String name = shared.getString("name","");
                Intent goTo;
                if ( shared.getString("name","").length() < 1 ){
                    goTo = new Intent(Splash.this, FacebookLogin.class);
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

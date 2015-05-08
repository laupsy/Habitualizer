package com.example.laurabarbera.habitualizer;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

/**
 * Created by laupsy on 5/7/15.
 */
public class BgSensor extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        Receiver r = new Receiver();
        IntentFilter f = new IntentFilter(Intent.ACTION_ALL_APPS);
        registerReceiver(r, f);
        return START_STICKY;
    }
}

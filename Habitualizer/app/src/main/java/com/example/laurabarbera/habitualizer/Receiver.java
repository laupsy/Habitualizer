package com.example.laurabarbera.habitualizer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by laupsy on 5/7/15.
 */
public class Receiver extends BroadcastReceiver {
    private Database db;
    @Override
    public void onReceive(Context context, Intent intent) {
        db = new Database(context);
        db.updateMotion();
    }
}

package com.example.laurabarbera.habitualizer;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by laurabarbera on 4/12/15.
 */
public class UserProfile {
    public UserProfile(Context c, SharedPreferences s) {
        super();
        this.shared = s;
        this.c = c;
    }
    public void setName(final String n) {
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(c.getResources().getString(R.string.NAME_SETTING), n);
        editor.commit();
    }
    public void setQuestionLevel(final int questionLevel) {
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(c.getResources().getString(R.string.QUESTIONS_SETTING), questionLevel);
    }
    public void setLocation(final int location) {
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(c.getResources().getString(R.string.LOCATION_SETTING), location);
    }
    public void setMotion(final int motion) {
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(c.getResources().getString(R.string.MOTION_SETTING), motion);
    }
    public void setPerformancelevel(final int performanceLevel) {
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(c.getResources().getString(R.string.PERFORMANCE_SETTING), performanceLevel);
    }
    public String getName() {
        String n = shared.getString(c.getResources().getString(R.string.NAME_SETTING), "");
        return n;
    }
    public String getQuestionLevel(Context c) {
        String s;
        int n = shared.getInt(c.getResources().getString(R.string.QUESTIONS_SETTING),0);
        if ( n == 0 ) {
            s = c.getString(R.string.notificationSetting_low);
        }
        else if ( n == 1 ) {
            s = c.getString(R.string.notificationSetting_medium);
        }
        else {
            s = c.getString(R.string.notificationSetting_high);
        }
        return s;
    }
    public String getLocation(Context c) {
        String s;
        int n = shared.getInt(c.getResources().getString(R.string.LOCATION_SETTING),0);
        if ( n == 0 ) {
            s = c.getString(R.string.locationSetting_off);
        }
        else {
            s = c.getString(R.string.locationSetting_on);
        }
        return s;
    }
    public String getBatterySetting(Context c) {
        String s;
        int n = shared.getInt(c.getResources().getString(R.string.PERFORMANCE_SETTING),0);
        if ( n == 0 ) {
            s = c.getString(R.string.batterySetting_low);
        }
        else if ( n == 1 ) {
            s = c.getString(R.string.batterySetting_medium);
        }
        else {
            s = c.getString(R.string.batterySetting_high);
        }
        return s;
    }
    public String getMotionSetting(Context c) {
        String s;
        int n = shared.getInt(c.getResources().getString(R.string.MOTION_SETTING),0);
        if ( n == 0 ) {
            s = c.getString(R.string.motionSetting_off);
        }
        else {
            s = c.getString(R.string.motionSetting_on);
        }
        return s;
    }
    private SharedPreferences shared;
    private Context c;
}

package com.example.laurabarbera.habitualizer;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by laurabarbera on 4/12/15.
 */
public class UserProfile {
    public UserProfile(SharedPreferences s) {
        super();
        this.shared = s;
    }
    public void setName(final String n) {
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(Globals.NAME_SETTING, n);
        editor.commit();
    }
    public void setQuestionLevel(final int questionLevel) {
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(Globals.QUESTIONS_SETTING, questionLevel);
    }
    public void setLocation(final int location) {
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(Globals.LOCATION_SETTING, location);
    }
    public void setMotion(final int motion) {
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(Globals.MOTION_SETTING, motion);
    }
    public void setPerformancelevel(final int performanceLevel) {
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(Globals.PERFORMANCE_SETTING, performanceLevel);
    }
    public String getName() {
        String n = shared.getString(Globals.NAME_SETTING, "");
        return n;
    }
    public String getQuestionLevel(Context c) {
        String s;
        int n = shared.getInt(Globals.QUESTIONS_SETTING,0);
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
        int n = shared.getInt(Globals.LOCATION_SETTING,0);
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
        int n = shared.getInt(Globals.PERFORMANCE_SETTING,0);
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
        int n = shared.getInt(Globals.MOTION_SETTING,0);
        if ( n == 0 ) {
            s = c.getString(R.string.motionSetting_off);
        }
        else {
            s = c.getString(R.string.motionSetting_on);
        }
        return s;
    }
    private SharedPreferences shared;
}

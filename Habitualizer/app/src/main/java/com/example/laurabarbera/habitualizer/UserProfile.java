package com.example.laurabarbera.habitualizer;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

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
    public void setQuestionLevel(final String questionLevel) {
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(c.getResources().getString(R.string.QUESTIONS_SETTING), questionLevel);
        editor.commit();
    }
    public void setLocation(final String location) {
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(c.getResources().getString(R.string.LOCATION_SETTING), location);
        editor.commit();
    }
    public void setMotion(final String motion) {
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(c.getResources().getString(R.string.MOTION_SETTING), motion);
        editor.commit();
    }
    public void setPerformancelevel(final String performanceLevel) {
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(c.getResources().getString(R.string.PERFORMANCE_SETTING), performanceLevel);
        editor.commit();
    }
    public String getName() {
        return shared.getString(c.getResources().getString(R.string.NAME_SETTING), "");
    }
    public String getQuestionLevel(Context c) {
        return shared.getString(c.getResources().getString(R.string.QUESTIONS_SETTING), "");
    }
    public String getLocation(Context c) {
        return shared.getString(c.getResources().getString(R.string.LOCATION_SETTING), "");
    }
    public String getBatterySetting(Context c) {
        return shared.getString(c.getResources().getString(R.string.PERFORMANCE_SETTING), "");
    }
    public String getMotionSetting(Context c) {
        return shared.getString(c.getResources().getString(R.string.MOTION_SETTING), "");
    }
    private SharedPreferences shared;
    private Context c;
}

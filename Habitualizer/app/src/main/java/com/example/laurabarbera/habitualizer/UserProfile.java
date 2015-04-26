package com.example.laurabarbera.habitualizer;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

/**
 * Created by laurabarbera on 4/12/15.
 */
public class UserProfile {
    private SharedPreferences.Editor editor;
    public UserProfile(Context c, SharedPreferences s) {
        super();
        this.shared = s;
        this.c = c;
        editor = shared.edit();
    }
    public void setName(final String n) {
        editor.putString(c.getResources().getString(R.string.NAME_SETTING), n);
        editor.apply();
    }
    public void setQuestionLevel(final String questionLevel) {
        editor.putString(c.getResources().getString(R.string.QUESTIONS_SETTING), questionLevel);
        editor.apply();
    }
    public void setLocation(final String location) {
        editor.putString(c.getResources().getString(R.string.LOCATION_SETTING), location);
        editor.apply();
    }
    public void setMotion(final String motion) {
        editor.putString(c.getResources().getString(R.string.MOTION_SETTING), motion);
        editor.apply();
    }
    public void setPerformancelevel(final String performanceLevel) {
        editor.putString(c.getResources().getString(R.string.PERFORMANCE_SETTING), performanceLevel);
        editor.apply();
    }
    public void updateMovement() {
    }
    public String getName() {
        return shared.getString(c.getResources().getString(R.string.NAME_SETTING), "");
    }
    public String getQuestionLevel() {
        return shared.getString(c.getResources().getString(R.string.QUESTIONS_SETTING), "");
    }
    public String getLocation() {
        return shared.getString(c.getResources().getString(R.string.LOCATION_SETTING), "");
    }
    public String getBatterySetting() {
        return shared.getString(c.getResources().getString(R.string.PERFORMANCE_SETTING), "");
    }
    public String getMotionSetting() {
        return shared.getString(c.getResources().getString(R.string.MOTION_SETTING), "");
    }
    private SharedPreferences shared;
    private Context c;
}

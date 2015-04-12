package com.example.laurabarbera.habitualizer;

import android.content.SharedPreferences;

/**
 * Created by laurabarbera on 4/12/15.
 */
public class UserProfile {
    public UserProfile(SharedPreferences s) {
        super();
        this.shared = s;
    }
    public String getName() {
        String n = shared.getString("name","");
        return n;
    }
    public void setName(final String n) {
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("name",n);
        editor.commit();
    }
    private SharedPreferences shared;
}

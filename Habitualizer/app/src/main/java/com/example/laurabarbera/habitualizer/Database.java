package com.example.laurabarbera.habitualizer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;


public class Database extends SQLiteOpenHelper {

    static final int DATABASE_VERSION = 33;
    static final String DATABASE_NAME = "userActivity";

    Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE MotionTable (Timestamp TEXT, Motion INTEGER);");
        db.execSQL("CREATE TABLE UserTable (_id INTEGER, Name VARCAR(50), Motion INTEGER, Location INTEGER, Performance INTEGER, Questions INTEGER);");
        db.execSQL("INSERT INTO UserTable VALUES (1, '', 0, 0, 0, 0)");
    }

    // USER SETTINGS
    public void setName(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE UserTable SET Name = '" + name + "' WHERE _id = 1");
        db.close();
    }
    public void setMotionSetting(int motion) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE UserTable SET Motion = '" + motion + "' WHERE _id = 1");
        db.close();
    }
    public void setLocationSetting(int location) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE UserTable SET Location = '" + location + "' WHERE _id = 1");
        db.close();
    }
    public void setPowerSetting(int usage) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE UserTable SET Performance = '" + usage + "' WHERE _id = 1");
        db.close();
    }
    public void setQuestionSetting(int notif) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE UserTable SET Questions = '" + notif + "' WHERE _id = 1");
        db.close();
    }
    public String getName() {
        String name = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT Name FROM UserTable WHERE _id = 1", null);
        if ( cursor.moveToFirst()) {
            do {
                name = cursor.getString(0);
            } while ( cursor.moveToNext() );
        }
        return name;
    }
    public int getMotionSetting() {
        int motion = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT Motion FROM UserTable WHERE _id = 1", null);
        if ( cursor.moveToFirst()) {
            do {
                motion = cursor.getInt(0);
            } while ( cursor.moveToNext() );
        }
        return motion;
    }
    public int getLocationSetting() {
        int location = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT Location FROM UserTable WHERE _id = 1", null);
        if ( cursor.moveToFirst()) {
            do {
                location = cursor.getInt(0);
            } while ( cursor.moveToNext() );
        }
        return location;
    }
    public int getPowerSetting() {
        int usage = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT Performance FROM UserTable WHERE _id = 1", null);
        if ( cursor.moveToFirst()) {
            do {
                usage = cursor.getInt(0);
            } while ( cursor.moveToNext() );
        }
        return usage;
    }
    public int getQuestionSetting() {
        int notif = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT Questions FROM UserTable WHERE _id = 1", null);
        if ( cursor.moveToFirst()) {
            do {
                notif = cursor.getInt(0);
            } while ( cursor.moveToNext() );
        }
        return notif;
    }

    // UPDATING STEPS / GETTING STEPS
    public void updateMotion() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO MotionTable VALUES (datetime(), 1)");
        db.close();
    }
    public float[] getMotion() {
        float totalMotion = 0;
        float motionPerHour[] = new float[9];
        ArrayList<Integer> motion = new ArrayList<Integer>();
        ArrayList<String> time = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT Motion FROM MotionTable", null);
        Cursor cursor2 = db.rawQuery("SELECT Timestamp FROM MotionTable", null);
        if ( cursor.moveToFirst()) {
            do {
                totalMotion++;
                motion.add(cursor.getInt(0));
            } while ( cursor.moveToNext() );
        }

        if ( cursor2.moveToFirst()) {
            do {
                time.add(cursor2.getString(0));
            } while ( cursor2.moveToNext() );
        }

            Log.d("heyyyy", time.size() + "");

//        for ( int j = 0; j < motion.size(); j++ ) {
//            Log.d("heyyyy", motion.get(j) + "");
//        }

        motionPerHour[0] = 0;
        motionPerHour[1] = 0;
        motionPerHour[2] = 0;
        motionPerHour[3] = 0;
        motionPerHour[4] = 0;
        motionPerHour[5] = 0;
        motionPerHour[6] = 0;
        motionPerHour[7] = 0;
        motionPerHour[8] = totalMotion;

        return motionPerHour;
    }


    public void onUpgrade(SQLiteDatabase s, int a, int b) {

    }
}

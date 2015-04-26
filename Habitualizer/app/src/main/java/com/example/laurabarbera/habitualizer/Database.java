package com.example.laurabarbera.habitualizer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;


public class Database extends SQLiteOpenHelper {

    static final int DATABASE_VERSION = 33;
    static final String DATABASE_NAME = "userActivity";
    static final String MOTION_TABLE = "Motion";
    static final String COL_TIMESTAMP = "Timestamp";
    static final String COL_MOTION = "Motion";

    Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + MOTION_TABLE +
                        "(" + COL_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                        + COL_MOTION + " INTEGER);");
    }
    public void onUpgrade(SQLiteDatabase s, int a, int b) {

    }
    public void insert(int i) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_MOTION, i);
        db.insert(MOTION_TABLE, null, cv);
        db.close();
    }
    public ArrayList<Integer> get() {
        ArrayList<Integer> motion = new ArrayList<Integer>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + MOTION_TABLE, null);
        if ( cursor.moveToFirst()) {
            do {
                motion.add(cursor.getInt(0));
            } while ( cursor.moveToNext() );
        }
        return motion;
    }
}

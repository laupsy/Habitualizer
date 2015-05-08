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
import java.util.Iterator;
import java.util.List;
import java.util.Random;


public class Database extends SQLiteOpenHelper {

    static final int DATABASE_VERSION = 33;
    static final String DATABASE_NAME = "userActivity";

    Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE MotionTable (Timestamp TEXT, Motion INTEGER);");
        db.execSQL("CREATE TABLE UserTable (_id INTEGER, Name TEXT, Motion INTEGER, Location INTEGER, Performance INTEGER, Questions INTEGER);");
        db.execSQL("CREATE TABLE QuestionList (_id INTEGER, QuestionPhrase TEXT, YesCount INTEGER, NoCount INTEGER);");
        db.execSQL("INSERT INTO UserTable VALUES (1, '', 0, 0, 0, 0)");
        db.execSQL("INSERT INTO QuestionList VALUES (1, 'Are you hungry?', 0, 0)");
        db.execSQL("INSERT INTO QuestionList VALUES (2, 'Are you happy?', 0, 0)");
    }

    // QUESTIONS
    public void addQuestion(String question, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO QuestionList VALUES(" + id + ", " + question + ")");
        db.close();
    }

    public ArrayList<String> getQuestions() {
        int ndx = 0;
        ArrayList<String> questions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT QuestionPhrase FROM QuestionList", null);
        if ( cursor.moveToFirst()) {
            do {
                ndx++;
                questions.add(ndx + ">>" + cursor.getString(0));
            } while ( cursor.moveToNext() );
        }
        return questions;
    }

    public String getRandomQuestion() {
        ArrayList<String> questions = getQuestions();
        Random r = new Random();
        int ndx = r.nextInt(questions.size());
        return questions.get(ndx);
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

    public void answerYes(int qNum) {
        int curCount = getAnswerYes(qNum);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE QuestionList SET YesCount = " + (curCount + 1) + " WHERE _id = " + qNum);
    }

    public void answerNo(int qNum) {
        int curCount = getAnswerNo(qNum);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE QuestionList SET NoCount = " + (curCount + 1) + " WHERE _id = " + qNum);
    }

    public int getAnswerYes(int qNum) {
        int curCount = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT YesCount FROM QuestionList WHERE _id = " + qNum, null);
        if ( cursor.moveToFirst()) {
            do {
                curCount = cursor.getInt(0);
            } while ( cursor.moveToNext() );
        }
        return curCount;
    }

    public int getAnswerNo(int qNum) {
        int curCount = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT NoCount FROM QuestionList WHERE _id = " + qNum, null);
        if ( cursor.moveToFirst()) {
            do {
                curCount = cursor.getInt(0);
            } while ( cursor.moveToNext() );
        }
        return curCount;
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

        /* initialize some stuff. motionPerHour array will
        collect total motion at hours 0, 3, 6, 9, 12, 15, 18, and 21,
        and then total daily motion, which is why it is 9 long
         */

        float totalMotion = 0;
        float motionPerHour[] = new float[9];
        for ( int a = 0; a < 9; a++ ) { motionPerHour[a] = 0; }

        // set up structs to get database timestamps and motion booleans

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

        // Break down motion into hours

        for ( int i = 0; i < time.size(); i++ ) {
            int day = Integer.parseInt(time.get(i).substring(8, 10), 10);

            int hour = Integer.parseInt(time.get(i).substring(11, 13), 10);

            if ( hour == 0 || hour == 1 || hour == 2) {
                motionPerHour[0] += 1;
            }
            else if ( hour == 3 || hour == 4 || hour == 5 ) {
                motionPerHour[1] += 1;
            }
            else if ( hour == 6 || hour == 7 || hour == 8 ) {
                motionPerHour[2] += 1;
            }
            else if ( hour == 9 || hour == 10 || hour == 11 ) {
                motionPerHour[3] += 1;
            }
            else if ( hour == 12 || hour == 13 || hour == 14 ) {
                motionPerHour[4] += 1;
            }
            else if ( hour == 15 || hour == 16 || hour == 17 ) {
                motionPerHour[5] += 1;
            }
            else if ( hour == 18 || hour == 19 || hour == 20 ) {
                motionPerHour[6] += 1;
            }
            else if ( hour == 21 || hour == 22 || hour == 23 ) {
                motionPerHour[7] += 1;
            }
        }

        motionPerHour[8] = totalMotion;

        return motionPerHour;
    }


    public void onUpgrade(SQLiteDatabase s, int a, int b) {

    }
}

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
        db.execSQL("CREATE TABLE QuestionList (_id INTEGER, QuestionPhrase TEXT);");
        db.execSQL("CREATE TABLE Distance (Timestamp TEXT, Longitude INTEGER, Latitude INTEGER);");
        db.execSQL("INSERT INTO UserTable VALUES (1, '', 0, 0, 0, 0)");
        db.execSQL("INSERT INTO QuestionList VALUES (1, 'Are you hungry?')");
        db.execSQL("INSERT INTO QuestionList VALUES (2, 'Are you happy?')");
        db.execSQL("CREATE TABLE Answers(_id INTEGER, Answer INTEGER, Timestamp TEXT)");
    }

    // QUESTIONS
    public boolean addQuestion(String question, int id) {
        if ( getLastQuestionId() <= 10 ) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("INSERT INTO QuestionList VALUES(" + id + ", '" + question + "')");
            answerNo(id);
            db.close();
            return true;
        }
        else {
            return false;
        }
    }
    public void recordDistance(double longi, double lati) {
        Log.d("blah", longi + " " + lati);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO Distance VALUES (datetime(), " + longi + ", " + lati + ")");
        db.close();
    }
    public double[] getEstHome() {
        SQLiteDatabase db = this.getReadableDatabase();
        double[] estHome = new double[2];
        Cursor longi = db.rawQuery("SELECT Longitude FROM Distance", null);
        Cursor lati = db.rawQuery("SELECT Latitude FROM Distance", null);
        double curLong = 0;
        double curLat = 0;
        int countLong = 0;
        int countLat = 0;
        ArrayList<Integer> totalLongs = new ArrayList<>();
        ArrayList<Integer> totalLats = new ArrayList<>();
        ArrayList<Double> mapToLat = new ArrayList<>();
        ArrayList<Double> mapToLong = new ArrayList<>();
        if ( longi.moveToFirst() ) {
            do {
                double l = longi.getDouble(0);
                if ( curLong != l ) {
                    curLong = l;
                    totalLongs.add(countLong);
                    mapToLong.add(curLong);
                    countLong = 0;
                }
                else {
                    countLong++;
                }
            } while ( longi.moveToNext() );
        }
        if ( lati.moveToFirst() ) {
            do {
                double l = lati.getDouble(0);
                if ( curLat != l ) {
                    curLat = l;
                    totalLats.add(countLat);
                    mapToLat.add(curLat);
                    countLat = 0;
                }
                else {
                    countLat++;
                }

            } while ( lati.moveToNext() );
        }
        double maxLong = 0;
        double maxLat = 0;
        int maxLongNdx = 0;
        int maxLatNdx = 0;
        for ( int i = 0; i < totalLongs.size(); i++ ) {
            if ( maxLong < totalLongs.get(i) ) {
                maxLong = totalLongs.get(i);
                maxLongNdx = i;
            }
        }
        for ( int i = 0; i < totalLats.size(); i++ ) {
            if ( maxLat < totalLats.get(i) ) {
                maxLat = totalLats.get(i);
                maxLatNdx = i;
            }
        }
        estHome[0] = mapToLat.get(maxLatNdx);
        estHome[1] = mapToLong.get(maxLongNdx);
        Log.d("home is ", estHome[0] +" "+ estHome[1]);
        return estHome;
    }
    public void removeQuestion(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.execSQL("DELETE FROM QuestionList WHERE _id = '" + (id+1) + "'");
        db.delete("QuestionList", "_id = '" + id + "'", null);
        db.delete("Answers", "_id = '" + id + "'", null);
        db.close();
    }
    public double[] getDistancePerHour() {
        double[] dph = new double[8];
        double[] estHome = getEstHome();
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> time = new ArrayList<>();
        ArrayList<Double> lati = new ArrayList<>();
        ArrayList<Double> longi = new ArrayList<>();
        Cursor timedata = db.rawQuery("SELECT Timestamp FROM Distance", null);
        Cursor latidata = db.rawQuery("SELECT Latitude FROM Distance", null);
        Cursor longidata = db.rawQuery("SELECT Longitude FROM Distance", null);
        if ( timedata.moveToFirst() ) {
            do {
                time.add(timedata.getString(0));
            } while ( timedata.moveToNext() );
        }
        if ( latidata.moveToFirst() ) {
            do {
                lati.add(latidata.getDouble(0));
            } while ( latidata.moveToNext() );
        }
        if ( longidata.moveToFirst() ) {
            do {
                longi.add(longidata.getDouble(0));
            } while ( longidata.moveToNext() );
        }


        for ( int i = 0; i < time.size(); i++ ) {
            int hour = Integer.parseInt(time.get(i).substring(11, 13), 10);
            double distance = Math.sqrt( (estHome[0] - lati.get(i))*(estHome[0] - lati.get(i)) + (estHome[1] - longi.get(i)) * (estHome[1] - longi.get(i)) );
            if ( hour == 0 || hour == 1 || hour == 2) {
                dph[0] = distance;
            }
            else if ( hour == 3 || hour == 4 || hour == 5 ) {
                dph[1] = distance;
            }
            else if ( hour == 6 || hour == 7 || hour == 8 ) {
                dph[2] = distance;
            }
            else if ( hour == 9 || hour == 10 || hour == 11 ) {
                dph[3] = distance;
            }
            else if ( hour == 12 || hour == 13 || hour == 14 ) {
                dph[4] = distance;
            }
            else if ( hour == 15 || hour == 16 || hour == 17 ) {
                dph[5] = distance;
            }
            else if ( hour == 18 || hour == 19 || hour == 20 ) {
                dph[6] = distance;
            }
            else if ( hour == 21 || hour == 22 || hour == 23 ) {
                dph[7] = distance;
            }
        }

        return dph;
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
        db.close();
        return questions;
    }
    public int getLastQuestionId() {
        int id = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _id FROM QuestionList", null);
        if ( cursor.moveToFirst()) {
            do {
                id = cursor.getInt(0);
            } while ( cursor.moveToNext() );
        }
        db.close();
        return id;
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
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO Answers VALUES(" + qNum + ", 1, datetime())");
        db.close();
    }
    public void answerNo(int qNum) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO Answers VALUES(" + qNum + ", 0, datetime())");
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
        db.close();
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
        db.close();
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
        db.close();
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
        db.close();
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
        db.close();
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

        db.close();
        return motionPerHour;
    }

    public float[] getAnswersWithTime(int id) {
        float[] yesPerHour = new float[9];
        ArrayList<Integer> answers = new ArrayList<>();
        ArrayList<String> time = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor answerdata = db.rawQuery("SELECT Answer FROM Answers WHERE _id = " + id, null);
        Cursor timedata = db.rawQuery("SELECT Timestamp FROM Answers WHERE _id = " + id, null);
        if ( answerdata.moveToFirst() ) {
            do {
                answers.add(answerdata.getInt(0));
            } while ( answerdata.moveToNext() );
        }
        if ( timedata.moveToFirst() ) {
            do {
                time.add(timedata.getString(0));
            } while (timedata.moveToNext());
        }

        for ( int i = 0; i < time.size(); i++ ) {
            if ( answers.get(i) == 1 ) {
                int hour = Integer.parseInt(time.get(i).substring(11, 13), 10);
                if ( hour == 0 || hour == 1 || hour == 2) {
                    yesPerHour[0] += 1;
                }
                else if ( hour == 3 || hour == 4 || hour == 5 ) {
                    yesPerHour[1] += 1;
                }
                else if ( hour == 6 || hour == 7 || hour == 8 ) {
                    yesPerHour[2] += 1;
                }
                else if ( hour == 9 || hour == 10 || hour == 11 ) {
                    yesPerHour[3] += 1;
                }
                else if ( hour == 12 || hour == 13 || hour == 14 ) {
                    yesPerHour[4] += 1;
                }
                else if ( hour == 15 || hour == 16 || hour == 17 ) {
                    yesPerHour[5] += 1;
                }
                else if ( hour == 18 || hour == 19 || hour == 20 ) {
                    yesPerHour[6] += 1;
                }
                else if ( hour == 21 || hour == 22 || hour == 23 ) {
                    yesPerHour[7] += 1;
                }
            }
        }
        return yesPerHour;
    }

    public float[] getRelativeMotion() {
        float[] relMotion = getMotion();
        float totalMotion = relMotion[8];
        for ( int i = 0; i < 9; i++ ) {
            relMotion[i] = (relMotion[i] / totalMotion) * 100;
            if ( relMotion[i] == 0 ) relMotion[i] = 1 + (float) Math.random()*2;
        }

        return relMotion;
    }

    public void onUpgrade(SQLiteDatabase s, int a, int b) {

    }

    public float[] getRelativeYes(int id) {
        float[] relYes = getAnswersWithTime(id);
        float totalYes = relYes[8];
        for ( int i = 0; i < 9; i++ ) {
            relYes[i] = (relYes[i] / totalYes) * 100;
            if ( relYes[i] == 0 ) relYes[i] = 1 + (float) Math.random()*2;
        }
        return relYes;
    }
}

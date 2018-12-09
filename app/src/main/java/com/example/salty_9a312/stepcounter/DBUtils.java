package com.example.salty_9a312.stepcounter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBUtils {

    private DB_Helper db_helper;
    private SQLiteDatabase sqLite;
    private static final String DBName = "step.db";
    private static final String STEP = "step";


    DBUtils(Context context, int version) {

        db_helper = new DB_Helper(context, version);
        sqLite = db_helper.getWritableDatabase();

    }


    public Cursor query(String[] columms, String selection, String[] selectionArgs, String orderBy) {

        Cursor cursor = sqLite.query(STEP,
                columms,
                selection,
                selectionArgs,
                null,
                null,
                orderBy);

        return cursor;

    }

    public void insert(int total_step, String date) {

        ContentValues values = new ContentValues();

        values.put("total_step", total_step);
        values.put("date", date);

        sqLite.insert(STEP, null, values);


    }

    public void updateCurrent(int total_step, int current_step) {

        ContentValues values = new ContentValues();
        values.put("current_step", current_step);

        sqLite.update(STEP, values, "total_step=?", new String[]{total_step + ""});
    }

    public void updateTotal(int oldTotal_step, int newTotal_step){

        ContentValues values = new ContentValues();
        values.put("total_step", newTotal_step);

        sqLite.update(STEP, values, "total_step=?", new String[]{oldTotal_step + ""});

    }


    public class DB_Helper extends SQLiteOpenHelper {


        private static final String CREATE_STEP_TABLE
                = "create table if not exists " + STEP +
                " (id integer primary key AUTOINCREMENT, total_step integer, current_step integer, date text)";

        public DB_Helper(Context context, int version) {

            super(context, DBName, null, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_STEP_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }


}

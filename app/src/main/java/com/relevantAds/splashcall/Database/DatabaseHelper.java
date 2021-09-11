package com.relevantAds.splashcall.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.relevantAds.splashcall.Database.Model.PhoneNumber;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {


    /**
     * Database Version
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Database Name
     */
    private static final String DATABASE_NAME = "relevantAds_splashCall_db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(PhoneNumber.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PhoneNumber.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }
    public long insertPhoneNumber(String number){
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        /**
         * referring to values to put inside table
         */
        ContentValues values = new ContentValues();
        /**
         * Inserting project name in project_name column.
         */
        values.put(PhoneNumber.COLUMN_PHONE_NUMBER,number);
        /**
         * inserting in database
         */
        long id = db.insert(PhoneNumber.TABLE_NAME,null,values);
        /**
         * Closing Database Reference.
         */
        db.close();

        /**
         * this will return an id : project id.
         */
        return id;
    }
    public ArrayList<PhoneNumber> getAllNumbers(){
        ArrayList<PhoneNumber> numbers = new ArrayList<>();

        /**
         * Select All Query from projects table order by project created date descending
         */
        String selectQuery = "SELECT  * FROM " + PhoneNumber.TABLE_NAME+ " ORDER BY " +
                PhoneNumber.COLUMN_TIMESTAMP + " DESC";

        /**
         * refers to Writeable database
         */
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to ArrayList<Project>
        if (cursor.moveToFirst()) {
            do {
                PhoneNumber phoneNumber = new PhoneNumber();
                phoneNumber.setId(cursor.getInt(cursor.getColumnIndex(PhoneNumber.COLUMN_ID)));
                phoneNumber.setAddedPhoneNumber(cursor.getString(cursor.getColumnIndex(PhoneNumber.COLUMN_PHONE_NUMBER)));
                phoneNumber.setCreatedDate(cursor.getString(cursor.getColumnIndex(PhoneNumber.COLUMN_TIMESTAMP)));

                numbers.add(phoneNumber);
            } while (cursor.moveToNext());
        }
        /**
         *Closing database connection
         */
        db.close();
        return numbers;
    }
}

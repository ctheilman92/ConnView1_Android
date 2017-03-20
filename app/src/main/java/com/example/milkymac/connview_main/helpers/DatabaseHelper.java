package com.example.milkymac.connview_main.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.milkymac.connview_main.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by milkymac on 3/20/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "UserManager.db";
    private static final String DATABASE_TABLE_NAME = "User";

    //region User COLUMNS
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USER_NAME = "user_name";
    private static final String COLUMN_USER_EMAIL = "user_email";
    private static final String COLUMN_USER_PASSWORD= "user_password";
    //endregion


    //CREATE USER TABLE
    private String CREATE_USER_TABLE = "CREATE TABLE " + DATABASE_TABLE_NAME + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_NAME + " TEXT,"
            + COLUMN_USER_EMAIL + " TEXT,"
            + COLUMN_USER_PASSWORD + " TEXT)";

    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS" + DATABASE_TABLE_NAME;



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_USER_TABLE);
        onCreate(db);
    }


    //region METHODS


    public List<User> listAllUsers() {
        String[] cols = {
                COLUMN_USER_ID,
                COLUMN_USER_NAME,
                COLUMN_USER_EMAIL,
                COLUMN_USER_PASSWORD
        };

        // sorting orders
        String sortOrder = COLUMN_USER_NAME + " ASC";
        List<User> userList = new ArrayList<User>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DATABASE_TABLE_NAME, cols, null, null, null, null, sortOrder);


        //row traversal -> add to userList
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setUID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID))));
                user.setName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(COLUMN_USER_PASSWORD)));
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return userList;
    }

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());

        db.insert(DATABASE_TABLE_NAME, null, values);
        db.close();
    }

    public void updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());

        db.update(DATABASE_TABLE_NAME, values, COLUMN_USER_ID +
                "= ?", new String[]{String.valueOf(user.getUID())});

        db.close();

    }

    public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(DATABASE_TABLE_NAME, COLUMN_USER_ID
                + "= ?", new String[]{String.valueOf(user.getUID())});
        db.close();
    }


    /* BY EMAIL & PASSWORD COMBINATION */
    public boolean checkUserExists(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();


        String col[] = { COLUMN_USER_ID };
        String Criteria = COLUMN_USER_EMAIL + "= ? AND " + COLUMN_USER_PASSWORD + "= ?";
        String SelectArgs[] = {email, password};

        Cursor cursor = db.query(DATABASE_TABLE_NAME,
                col, Criteria, SelectArgs,
                null, null, null);

        int count = cursor.getCount();
        db.close();

        return (count > 0) ? true : false;
    }

    /* BY EMAIL ONLY */
    public boolean checkUserExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();


        String col[] = { COLUMN_USER_ID };
        String Criteria = COLUMN_USER_EMAIL + "= ?";
        String SelectArgs[] = { email };

        Cursor cursor = db.query(DATABASE_TABLE_NAME,
                col, Criteria, SelectArgs,
                null, null, null);

        int count = cursor.getCount();
        db.close();

        return (count > 0) ? true : false;
    }
}

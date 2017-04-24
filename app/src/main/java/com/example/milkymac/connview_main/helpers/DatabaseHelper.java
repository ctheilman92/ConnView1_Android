package com.example.milkymac.connview_main.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.milkymac.connview_main.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by milkymac on 3/20/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "UserManager";
    private static final String DATABASE_TABLE_NAME = "User";

    //region User COLUMNS
    private static final String COLUMN_USER_ID = "UID";
    private static final String COLUMN_USER_NAME = "Name";
    private static final String COLUMN_USER_EMAIL = "Email";
    private static final String COLUMN_USER_PASSWORD= "Password";
    //endregion


    //CREATE USER TABLE
    public static final String CREATE_USER_TABLE = "CREATE TABLE " + DATABASE_TABLE_NAME + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_NAME + " TEXT NOT NULL,"
            + COLUMN_USER_EMAIL + " TEXT NOT NULL,"
            + COLUMN_USER_PASSWORD + " TEXT NOT NULL)";

    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + DATABASE_TABLE_NAME;



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL("INSERT INTO  " + DATABASE_TABLE_NAME + "(" + COLUMN_USER_ID + "," + COLUMN_USER_NAME + ","
                + COLUMN_USER_EMAIL + "," + COLUMN_USER_PASSWORD + ") values(999, 'root', 'Root@root.com', 'root')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_USER_TABLE);
        onCreate(db);
    }


    //region METHODS


    public List<User> listAllUsers() {
        Log.d("DATABASE_HELPER", "BEGIN LIST OF ALL USERS");
        String[] cols = {
                COLUMN_USER_ID,
                COLUMN_USER_NAME,
                COLUMN_USER_EMAIL,
                COLUMN_USER_PASSWORD
        };

        // sorting orders
        List<User> userList = new ArrayList<User>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DATABASE_TABLE_NAME, cols, null, null, null, null, null);


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
        Log.d("ADD_USER_CHECK", "Adding user " + user.getPassword());
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
        Log.d("DATABASE_HELPER", "CHECKING IF USER EXISTS BY EMAIL!!!!");

        SQLiteDatabase db = this.getReadableDatabase();


        String Criteria = COLUMN_USER_EMAIL + "= ? AND " + COLUMN_USER_PASSWORD + "= ?";
        String SelectArgs[] = {email, password};

        Cursor c = db.query(DATABASE_TABLE_NAME, null, Criteria, new String[] {email, password}, null, null, null);



        int count = c.getCount();
        Log.d("DATABASE_HELPER", "ROW COUNT"+ String.valueOf(count));
        db.close();

        return (count > 0) ? true : false;
    }

    /* BY EMAIL ONLY */
    public boolean checkUserExistsEmail(String email) {
        Log.d("DATABASE_HELPER", "CHECKING IF USER EXISTS BY EMAIL!!!!");
        boolean exists = false;
        SQLiteDatabase db = this.getReadableDatabase();

        String Criteria = COLUMN_USER_EMAIL + "= ?";
        String SelectArgs[] = { email };

        Cursor cursor = db.query(DATABASE_TABLE_NAME,
                null, Criteria, SelectArgs,
                null, null, null);

        int count = cursor.getCount();
        db.close();
        return (count > 0) ? true : false;
    }
}

package com.example.milkymac.connview_main.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.example.milkymac.connview_main.models.MyNet;
import com.example.milkymac.connview_main.models.Network;
import com.example.milkymac.connview_main.models.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by milkymac on 3/20/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 12;
    private static final String DATABASE_NAME = "UserManager";
    private static final String DATABASE_TABLE_NAME = "User";

    //region User COLUMNS
    private static final String COLUMN_USER_ID = "UID";
    private static final String COLUMN_USER_NAME = "Name";
    private static final String COLUMN_USER_EMAIL = "Email";
    private static final String COLUMN_USER_PASSWORD= "Password";
    //endregion


    //region Networks COLUMNS
    private static final String DATABASE_TABLE_NETWORKS = "Networks";

    private static final String COLUMN_NETWORKS_SSID = "SSID";
    private static final String COLUMN_NETWORKS_COUNTER = "Counter";
    private static final String COLUMN_NETWORKS_ADDRESS = "Address";
    private static final String COLUMN_NETWORKS_USER = "UserName";
    private static final String COLUMN_NETWORKS_ID = "NID";
    private static final String COLUMN_NETWORKS_LASTCONNECTED = "LastConnected";
    //endregion


    //CREATE TABLES
    public static final String CREATE_USER_TABLE = "CREATE TABLE " + DATABASE_TABLE_NAME + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_NAME + " TEXT NOT NULL,"
            + COLUMN_USER_EMAIL + " TEXT NOT NULL,"
            + COLUMN_USER_PASSWORD + " TEXT NOT NULL)";

    public static final String CREATE_NETWORKS_TABLE = "CREATE TABLE " + DATABASE_TABLE_NETWORKS + "("
            + COLUMN_NETWORKS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NETWORKS_COUNTER + " INTEGER NOT NULL,"
            + COLUMN_NETWORKS_USER + " TEXT NOT NULL,"
            + COLUMN_NETWORKS_ADDRESS + " TEXT NOT NULL,"
            + COLUMN_NETWORKS_SSID + " TEXT NOT NULL,"
            + COLUMN_NETWORKS_LASTCONNECTED + " TEXT NOT NULL)";

    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + DATABASE_TABLE_NAME;
    private String DROP_NETWORKS_TABLE = "DROP TABLE IF EXISTS " + DATABASE_TABLE_NETWORKS;



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);

        //super user. I never claimed to be a good person.
        db.execSQL("INSERT INTO  " + DATABASE_TABLE_NAME + "(" + COLUMN_USER_ID + "," + COLUMN_USER_NAME + ","
                + COLUMN_USER_EMAIL + "," + COLUMN_USER_PASSWORD + ") values(999, 'root', 'root@root.com', 'root')");
        db.execSQL(CREATE_NETWORKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_USER_TABLE);
        db.execSQL(DROP_NETWORKS_TABLE);
        onCreate(db);
    }


    //region networks METHODS
    public void updateNetCounter(MyNet mynet) throws ParseException {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean foundRecord = false;
        List<MyNet> templist = listUserNetworks(mynet.getUserName());

        MyNet newNet = getuserNetwork(mynet.getUserName(), mynet.getSSID());
        ContentValues values = new ContentValues();

        if (newNet.getUserName().equals(mynet.getUserName())) {
            foundRecord = true;
            Log.d("DB_NETWORK_HELPER", "updating user-network history record");
            values.put(COLUMN_NETWORKS_COUNTER, mynet.getTimesConnected()+1);
            values.put(COLUMN_NETWORKS_LASTCONNECTED, mynet.getLastConnected());
            db.update(DATABASE_TABLE_NETWORKS, values, COLUMN_NETWORKS_USER + " = ? AND " + COLUMN_NETWORKS_SSID + " = ?", new String[]{newNet.getUserName(), newNet.getSSID()});
        }


        if (!foundRecord) { Log.d("DB_UPDATE_COUNTER", "failed to find record of saved state. User has never used this network"); }
        db.close();
    }


    public boolean checkNetworkHistory(String un, String ssid) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(DATABASE_TABLE_NETWORKS, null, COLUMN_NETWORKS_USER + "= ? AND " + COLUMN_NETWORKS_SSID + "= ?", new String[] {un, ssid}, null, null, null);
        int count = c.getCount();

        Log.d("DATABASE_HELPER", "ROW COUNT: "+ String.valueOf(count));
        db.close();

        return (count > 0) ? true : false;
    }

    public MyNet getuserNetwork(String un, String ssid) throws ParseException {
        SQLiteDatabase db = this.getReadableDatabase();
        MyNet mn = new MyNet();
        Cursor c = db.query(DATABASE_TABLE_NETWORKS, null, COLUMN_NETWORKS_USER + "= ? AND " + COLUMN_NETWORKS_SSID + "= ?", new String[] {un, ssid}, null, null, null);

        if (c.moveToFirst()) {
            mn.setTimesConnected(Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_NETWORKS_COUNTER))));
            mn.setSSID(c.getString(c.getColumnIndex(COLUMN_NETWORKS_SSID)));
            mn.setUserName(c.getString(c.getColumnIndex(COLUMN_NETWORKS_USER)));
            mn.setNetIP(c.getString(c.getColumnIndex(COLUMN_NETWORKS_ADDRESS)));
            mn.setLastConnected(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(c.getString(c.getColumnIndex(COLUMN_NETWORKS_LASTCONNECTED))));
        }

        return mn;
    }


    //initial adding network to history
    //counter = 1;
    public void addUserNetwork(MyNet mynet) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NETWORKS_SSID, mynet.getSSID());
        values.put(COLUMN_NETWORKS_USER, mynet.getUserName());
        values.put(COLUMN_NETWORKS_ADDRESS, mynet.getNetIP());
        values.put(COLUMN_NETWORKS_COUNTER, 1);
        values.put(COLUMN_NETWORKS_LASTCONNECTED, mynet.getLastConnected());

        db.insert(DATABASE_TABLE_NETWORKS, null, values);
        db.close();
    }


    public List<MyNet> listUserNetworks(String un) throws ParseException {


        List<MyNet> netList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        //specify where clause for specific USER ID
        Cursor cursor = db.query(DATABASE_TABLE_NETWORKS, null, COLUMN_NETWORKS_USER + "= ?", new String[] {un}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                MyNet mynet = new MyNet();
                mynet.setTimesConnected(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_NETWORKS_COUNTER))));
                mynet.setSSID(cursor.getString(cursor.getColumnIndex(COLUMN_NETWORKS_SSID)));
                mynet.setUserName(cursor.getString(cursor.getColumnIndex(COLUMN_NETWORKS_USER)));
                mynet.setNetIP(cursor.getString(cursor.getColumnIndex(COLUMN_NETWORKS_ADDRESS)));
                mynet.setLastConnected(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(cursor.getString(cursor.getColumnIndex(COLUMN_NETWORKS_LASTCONNECTED))));

                if (!mynet.getSSID().equals("----")) { netList.add(mynet); }
            } while (cursor.moveToNext());
        }

        return netList;
    }

    /*INCOMPLETE*/
    public List<MyNet> listMostRecentNetworks(String un) throws ParseException {
        SQLiteDatabase db = this.getReadableDatabase();
        List<MyNet> allUserNetworks = listUserNetworks(un);

            Collections.sort(allUserNetworks, new Comparator<MyNet>() {
                @Override
                public int compare(MyNet o1, MyNet o2) {
                    return (o1.getLastConnectedDate().compareTo(o2.getLastConnectedDate()) > 0) ? -1 : 1;
                }
            });


        if (allUserNetworks.size() < 3) { return allUserNetworks.subList(0, (allUserNetworks.size())); }
        else { return allUserNetworks.subList(0, 3); }
    }

    public List<MyNet> listAllSavedNetworks() {
        String[] cols = {
                COLUMN_NETWORKS_ID,
                COLUMN_NETWORKS_COUNTER,
                COLUMN_NETWORKS_USER,
                COLUMN_NETWORKS_ADDRESS,
                COLUMN_NETWORKS_USER
        };

        List<MyNet> netList = new ArrayList<MyNet>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DATABASE_TABLE_NETWORKS, cols, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                MyNet mynet = new MyNet();
                mynet.setTimesConnected(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_NETWORKS_COUNTER))));
                mynet.setSSID(cursor.getString(cursor.getColumnIndex(COLUMN_NETWORKS_SSID)));
                mynet.setUserName(cursor.getString(cursor.getColumnIndex(COLUMN_NETWORKS_USER)));
                mynet.setNetIP(cursor.getString(cursor.getColumnIndex(COLUMN_NETWORKS_ADDRESS)));
                netList.add(mynet);
            } while (cursor.moveToNext());
        }

        return netList;
    }
    //endregion


    //region user METHODS
    public List<User> listAllUsers() {
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
        values.put(COLUMN_USER_EMAIL, user.getEmail().toLowerCase());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());

        db.insert(DATABASE_TABLE_NAME, null, values);
        db.close();
    }

    public void updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_EMAIL, user.getEmail().toLowerCase());
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
        Cursor c = db.query(DATABASE_TABLE_NAME, null, Criteria, new String[] {email.toLowerCase(), password}, null, null, null);



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
        String SelectArgs[] = { email.toLowerCase() };

        Cursor cursor = db.query(DATABASE_TABLE_NAME,
                null, Criteria, SelectArgs,
                null, null, null);

        int count = cursor.getCount();
        db.close();
        return (count > 0) ? true : false;
    }
}

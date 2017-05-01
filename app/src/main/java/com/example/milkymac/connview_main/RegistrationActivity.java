package com.example.milkymac.connview_main;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ContentFrameLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.milkymac.connview_main.helpers.DatabaseHelper;
import com.example.milkymac.connview_main.models.User;

import java.util.List;


public class RegistrationActivity extends AppCompatActivity {

    private final String PREFS_NAME = "userPrefs";

    //region UI VARS
    EditText etName;
    EditText etEmail;
    EditText etPassword;
    EditText etPassword2;
    Button btnRegister;

    User newUser;
    DatabaseHelper dbhelper;
    Context context;
    Editor editor;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        context = RegistrationActivity.this;
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        dbhelper = new DatabaseHelper(getApplicationContext());
        editor = prefs.edit();


        initvars();
        registerListeners();
    }


    public void initvars() {
        etName = (EditText) findViewById(R.id.etRegName);
        etEmail = (EditText) findViewById(R.id.etRegEmail);
        etPassword = (EditText) findViewById(R.id.etRegPassword);
        etPassword2 = (EditText) findViewById(R.id.etPasswordVerif);
        btnRegister = (Button) findViewById(R.id.btnRegister);
    }


    public void registerListeners() {
        etName.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(RegistrationActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    etName.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

                    etName.setText("");
                    return super.onDoubleTap(e);
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                etName.requestFocus();
                gestureDetector.onTouchEvent(event);

                return true;
            }
        });

        etEmail.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(RegistrationActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    etEmail.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

                    etEmail.setText("");
                    return super.onDoubleTap(e);
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                etEmail.requestFocus();
                gestureDetector.onTouchEvent(event);

                return true;
            }
        });


        etPassword.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(RegistrationActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    etPassword.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

                    etPassword.setText("");
                    return super.onDoubleTap(e);
                }
            });



            @Override
            public boolean onTouch(View v, MotionEvent event) {
                etPassword.requestFocus();
                gestureDetector.onTouchEvent(event);

                return true;
            }
        });


        etPassword2.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(RegistrationActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    etPassword2.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                    Log.d("TEST", "onDoubleTap");
                    etPassword2.setText("");
                    return super.onDoubleTap(e);
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                etPassword2.requestFocus();
                gestureDetector.onTouchEvent(event);

                return true;
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });
    }


    public void clearUI() {
        etName.setText("");
        etEmail.setText("");
        etPassword.setText("");
        etPassword2.setText("");
    }


    public void registerNewUser() {
        if (validateForm()) {
            try {
                if (dbhelper.checkUserExistsEmail(etEmail.getText().toString().toLowerCase())) {
                    Toast.makeText(context, "A user is already present in Database.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    try {
                        newUser = new User(etName.getText().toString(), etEmail.getText().toString().toLowerCase(), etPassword2.getText().toString());
                        dbhelper.addUser(newUser);

                        //come back and insert UID for user
                        List<User> tempList = dbhelper.listAllUsers();
                        if (!tempList.isEmpty()) {
                            for (User u : tempList) {
                                if (u.getEmail().toLowerCase().equals(newUser.getEmail().toLowerCase())) {
                                    newUser.setUID(u.getUID());
                                    Toast.makeText(context, "Welecome: " + u.getName(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                        Toast.makeText(context, "WELCOME TO THE FAMILY "+newUser.getName(), Toast.LENGTH_LONG).show();
                        editor.putString("EMAIL_KEY",newUser.getEmail());
                        editor.putInt("UID_KEY", newUser.getUID());
                        editor.putString("USERNAME_KEY", newUser.getName());
                        editor.commit();
                    }
                    catch (Exception ex) {
                        Log.d("EXCEPTION_CONN2_DB", ex.toString());
                    }
                }
            }
            catch (Exception ex) {
                Log.d("EXCEPTION_CONN2_DB", ex.toString());

            }
            launchMain();
        }
        else return;
    }

    public boolean checkEmail(CharSequence e) {
        return !android.util.Patterns.EMAIL_ADDRESS.matcher(e).matches();
    }

    public boolean validateForm() {
        if (TextUtils.isEmpty(etName.getText().toString()) || TextUtils.isEmpty(etEmail.getText().toString()) || TextUtils.isEmpty(etPassword.getText().toString()) || TextUtils.isEmpty(etPassword2.getText().toString())) {
            Toast.makeText(context, "Please fill out the required fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (checkEmail(etEmail.getText())) {
            Toast.makeText(context, "Email must specify the correct format!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            String p1 = etPassword.getText().toString().trim();
            String p2 = etPassword2.getText().toString().trim();
            if (!p1.equals(p2)) {
                Log.d("TESTPASSES", "PASS1: "+ p1 + " --PASS2: " + p2);
                Toast.makeText(context, "Please verify your password - Enter 2x", Toast.LENGTH_SHORT).show();
                etPassword.setText("");
                etPassword2.setText("");
                return false;
            }
        }
        return true;
    }

    public void launchMain() {
        Intent intent = new Intent(this, MainActivity.class);
        finish();   //cannot go back to login page (CHANGE LATER BASED ON ACTIVITY TIMER AND CONNECTION
        startActivity(intent);
    }
}

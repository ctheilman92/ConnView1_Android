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
import android.view.KeyEvent;
import android.view.View;
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

    boolean nameIsedited;
    boolean emailIsedited;
    boolean password1IsEdited;
    boolean password2IsEdited;

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

        nameIsedited = false;
        emailIsedited = false;
        password1IsEdited = false;
        password2IsEdited = false;
    }


    public void registerListeners() {
        etName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!nameIsedited) {
                    etName.setText("");
                    nameIsedited = true;
                }
            }
        });
        etEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!emailIsedited) {
                    etEmail.setText("");
                    emailIsedited = true;
                }

            }
        });
        etPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!password1IsEdited) {
                    etPassword.setText("");
                    password1IsEdited = true;
                }
            }
        });
        etPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == 66) { etPassword2.requestFocus(); etPassword2.setText(""); }
                return false;
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
                if (dbhelper.checkUserExistsEmail(etEmail.getText().toString())) {
                    Toast.makeText(context, "A user is already present in Database.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    try {
                        newUser = new User(etName.getText().toString(), etEmail.getText().toString(), etPassword.getText().toString().trim());
                        dbhelper.addUser(newUser);

                        //come back and insert UID for user
                        List<User> tempList = dbhelper.listAllUsers();
                        if (!tempList.isEmpty()) {
                            for (User u : tempList) {
                                if (u.getEmail() == newUser.getEmail()) {
                                    newUser.setUID(u.getUID());
                                    Toast.makeText(context, "Welecome: " + u.getEmail(), Toast.LENGTH_LONG).show();
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

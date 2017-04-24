package com.example.milkymac.connview_main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.milkymac.connview_main.helpers.DatabaseHelper;
import com.example.milkymac.connview_main.models.User;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    public final String PREFS_NAME = "userPrefs";

    //region UI
    private TextView tvlogo;
    private Button btnLogin;
    private Button btnRegister;
    private EditText etEmail;
    private EditText etPassword;
    //endregion


    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    

    private User CurrentUser;
    public DatabaseHelper dbhelper;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        context = LoginActivity.this;

        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
        initVars();

        //listeners
        registerFocusListeners();
        registerTextWatchers();
        registerbuttonListeners();

    }


    public void initVars() {
        tvlogo = (TextView) findViewById(R.id.tvLoginLog0o);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
    }


    //region LISTENERS
    public void registerbuttonListeners() {

        //LOGIN
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateLogin();
            }
        });

        //REGISTER
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               launchRegister();
            }
        });
    }


    public void registerFocusListeners() {
        etEmail.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(LoginActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    etEmail.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                    Log.d("TEST", "onDoubleTap");
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
            private GestureDetector gestureDetector = new GestureDetector(LoginActivity.this, new GestureDetector.SimpleOnGestureListener() {
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
    }

    public void registerTextWatchers() {

        //UN
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //PW
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    //endregion

    public void emptyInput() {
        etPassword.setText("");
        etEmail.setText("");
    }


    public void validateLogin() {
        dbhelper = new DatabaseHelper(getApplicationContext());
        if (dbhelper.checkUserExists(etEmail.getText().toString(), etPassword.getText().toString())) {
            String validEmail = etEmail.getText().toString().trim();


            if (!dbhelper.checkUserExistsEmail(validEmail))
                Toast.makeText(context, "add user failed...", Toast.LENGTH_LONG);

            List<User> getDBUsers = dbhelper.listAllUsers();

            for (User u : getDBUsers) {
                Log.d("NEXT_USER", u.getName().toString());
                if (u.getEmail().equals(validEmail)) {
                    CurrentUser = new User(u.getUID(), u.getName(), u.getEmail(), u.getPassword());
                    Log.d("SHARED_PREFS_UID_GET", String.valueOf(u.getUID()));
                    editor.putString("EMAIL_KEY", CurrentUser.getEmail());
                    editor.putString("USERNAME_KEY", CurrentUser.getName());
                    editor.putString("USERPASS_KEY", CurrentUser.getPassword());
                    editor.commit();
                }
            }
            if (CurrentUser != null)
                launchMain();
        }
        else {
            Toast.makeText(context, "User with these credentials does not exist!", Toast.LENGTH_SHORT).show();
        }
    }

    public void launchRegister() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    public void launchMain() {
        Intent intent = new Intent(this, MainActivity.class);
        finish();   //cannot go back to login page (CHANGE LATER BASED ON ACTIVITY TIMER AND CONNECTION
        startActivity(intent);
    }
}

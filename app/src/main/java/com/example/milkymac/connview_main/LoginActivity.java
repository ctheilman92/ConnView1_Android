package com.example.milkymac.connview_main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.milkymac.connview_main.models.User;

public class LoginActivity extends AppCompatActivity {

    //region UI
    private TextView tvlogo;
    private Button btnLogin;
    private Button btnRegister;
    private EditText etUsername;
    private EditText etPassword;
    //endregion

    private boolean unIsEdited = false;
    private boolean pwIsEdited = false;

    private String UN;
    private String PW;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
    }

    //region LISTENERS
    public void registerbuttonListeners() {



        //LOGIN
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchMain();
            }
        });

        //REGISTER
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               launchMain();
            }
        });
    }


    public void registerFocusListeners() {
        etUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { if (!unIsEdited) { etUsername.setText(""); unIsEdited = true; }}
        });

        etPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pwIsEdited) { etPassword.setText(""); pwIsEdited = true; }
            }
        });
    }

    public void registerTextWatchers() {

        //UN
        etUsername.addTextChangedListener(new TextWatcher() {
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


    public void launchMain() {
        Intent intent = new Intent(this, MainActivity.class);
        finish();   //cannot go back to login page (CHANGE LATER BASED ON ACTIVITY TIMER AND CONNECTION
        startActivity(intent);
    }
}

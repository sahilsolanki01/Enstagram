/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.solanki.enstagram;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.parse.starter.R;


public class MainActivity extends AppCompatActivity implements View.OnKeyListener {
    private static final String TAG = "MainActivity";
    EditText editTextUsername;
    EditText editTextPassword;
    Button buttonSignUp, buttonLogin;
    RelativeLayout backgroundLayout;
    ImageView imageView;
    int count = 0;

    public void showList() {
        editTextUsername.setText("");
        editTextPassword.setText("");
        Intent intent = new Intent(getApplicationContext(), ListActivity.class);
        startActivity(intent);
    }


    public void keyboardDown(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }


    public void toggle(View view) {

        if (editTextUsername.getText().toString().matches("") || editTextPassword.getText().toString().matches("")) {
            Toast.makeText(MainActivity.this, "Username and Password required", Toast.LENGTH_LONG).show();
        }else {

            ParseUser.logInInBackground(editTextUsername.getText().toString(), editTextPassword.getText().toString(), new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e == null) {
                        Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG).show();
                        editTextUsername.setText("");
                        editTextPassword.setText("");
                        showList();

                    } else {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("86878774759", "done: log");
                    }
                }
            });
        }


    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            signUpClick(view);
        }
        return false;
    }

    public void signUpClick(View view) {

        if (editTextUsername.getText().toString().matches("") || editTextPassword.getText().toString().matches("")) {
            Toast.makeText(MainActivity.this, "Username and Password required", Toast.LENGTH_LONG).show();
        } else {

                ParseUser user = new ParseUser();
                user.setUsername(editTextUsername.getText().toString());
                user.setPassword(editTextPassword.getText().toString());

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(MainActivity.this, "New Account Created", Toast.LENGTH_LONG).show();
                            editTextUsername.setText("");
                            editTextPassword.setText("");
                            showList();

                        } else {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e("86878774759", "done: sign");
                        }
                    }
                });
            }
        }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPaasword);
        buttonSignUp = (Button) findViewById(R.id.buttonSignUp);
        buttonLogin = findViewById(R.id.buttonLogin);
        backgroundLayout = (RelativeLayout) findViewById(R.id.backgroundLayout);
        imageView = findViewById(R.id.imageView);


        editTextPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (editTextPassword.getRight() - editTextPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (count == 0) {
                            count = 1;
                            editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            editTextPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_black_24dp, 0);

                            return true;
                        } else if (count == 1) {
                            count = 0;
                            editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            editTextPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off_black_24dp, 0);
                            return true;
                        }
                    }
                }
                return false;
            }
        });


        if (ParseUser.getCurrentUser() != null) {
            showList();
        }


        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

}
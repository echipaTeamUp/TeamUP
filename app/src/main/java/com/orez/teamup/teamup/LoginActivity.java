package com.orez.teamup.teamup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;


public class LoginActivity extends AppCompatActivity {
    EditText mUsernameEt;
    EditText mPasswordEt;
    Button mLoginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    mUsernameEt=(EditText) findViewById(R.id.usernameEt);
    mPasswordEt=(EditText) findViewById(R.id.passwordEt);
    mLoginBtn=(Button) findViewById(R.id.loginBtn);

        //iote comentariu


    }

}

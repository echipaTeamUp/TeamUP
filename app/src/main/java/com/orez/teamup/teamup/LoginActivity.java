package com.orez.teamup.teamup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class LoginActivity extends AppCompatActivity {
    /*user si parola de test:
    test@test.com
    test123
    cu toate ca inca nu am functionalitatea de login facuta
     */
    EditText mUsernameEt;
    EditText mPasswordEt;
    Button mLoginBtn;
    Button mSignupBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    mUsernameEt=(EditText) findViewById(R.id.login_usernameEt);
    mPasswordEt=(EditText) findViewById(R.id.login_passwordEt);
    mLoginBtn=(Button) findViewById(R.id.loginBtn);
    mSignupBtn=(Button) findViewById(R.id.login_signupBtn);
    //OnClick care duce la activitatea de signup
    mSignupBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i=new Intent(LoginActivity.this,SignupActivity.class);
            startActivity(i);
        }
    });

        //iote comentariu


    }

}

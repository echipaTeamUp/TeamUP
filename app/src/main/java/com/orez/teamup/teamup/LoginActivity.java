package com.orez.teamup.teamup;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {
    /*user si parola de test:
    test@test.com
    test123
    cu toate ca inca nu am functionalitatea de login facuta
     */
    EditText mEmailEt;
    EditText mPasswordEt;
    Button mLoginBtn;
    Button mSignupBtn;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        mEmailEt = (EditText) findViewById(R.id.login_usernameEt);
        mPasswordEt = (EditText) findViewById(R.id.login_passwordEt);
        mLoginBtn = (Button) findViewById(R.id.loginBtn);
        mSignupBtn = (Button) findViewById(R.id.login_signupBtn);
        mAuth = FirebaseAuth.getInstance();
        //OnClick care duce la activitatea de signup
        mSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });

        //OnClick pentru logare
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();

            }
        });
    }
    //practic incearca logarea
    void login() {
        mAuth.signInWithEmailAndPassword(mEmailEt.getText().toString().trim(),
                mPasswordEt.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // A mers
                            Intent i = new Intent(LoginActivity.this, MenuActivity.class);
                            startActivity(i);
                            FirebaseUser user = mAuth.getCurrentUser();

                        } else {
                            // Nu a mers
                            Toast.makeText(LoginActivity.this, "Ai gresit datele,baiatul meu",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}

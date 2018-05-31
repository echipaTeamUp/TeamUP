package com.orez.teamup.teamup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;


public class LoginActivity extends Activity {

    EditText mEmailEt;
    EditText mPasswordEt;
    Button mLoginBtn;
    Button mSignupBtn;
    private FirebaseAuth mAuth;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();
        //Verifica daca esti conectat la internet
        if (!verifyInternetConnectivty())
            Toast.makeText(LoginActivity.this, "Please connect to the internet", Toast.LENGTH_SHORT).show();

        //daca esti deja logat te duce direct in menu activity
        if (mAuth.getCurrentUser() != null) {
            retrieve_user();
        }
        //daca nu esti logat
        else {
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
                    startActivityForResult(i, 1);
                }
            });

            //OnClick pentru logare
            mLoginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    login();
                    //am pus urmatoarea linie pentru ca altfel ramane parola acolo daca te loghezi si dai
                    //sign out in aceeasi sesiune
                    mPasswordEt.setText("");
                }
            });

        }
    }

    //practic incearca logarea
    void login() {
        if (!mEmailEt.getText().toString().isEmpty() && !mPasswordEt.getText().toString().isEmpty()) {
            //daca stringurile sunt goale da crash metoda de la firebase
            mAuth.signInWithEmailAndPassword(mEmailEt.getText().toString().trim(),
                    mPasswordEt.getText().toString().trim())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // A mers
                                retrieve_user();

                            } else {
                                // Nu a mers
                                Toast.makeText(LoginActivity.this, "Ai gresit datele, baiatul meu",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(LoginActivity.this, "N-ai introdus datele, baiatul meu",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //functie care verifica daca esti conectat la net
    protected boolean verifyInternetConnectivty() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //asta e pentru cand vine din signup, completeaza automat campurile
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            mPasswordEt.setText(data.getStringExtra("pass"));
            mEmailEt.setText(data.getStringExtra("email"));
        }
            }


    //pune datele in obiectul user si trece in meniu
    public void retrieve_user() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference myRef = database.child("id").child(uid);
        user = new User();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                Intent i = new Intent(LoginActivity.this, MenuActivity.class);
                i.putExtra("User", user);
                startActivity(i);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, databaseError.toException().toString(), Toast.LENGTH_LONG).show();
            }

        });

    }



}

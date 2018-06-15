package com.orez.teamup.teamup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends Activity {

    EditText mEmailEt;
    EditText mPasswordEt;
    Button mLoginBtn;
    Button mSignupBtn;
    private FirebaseAuth mAuth;
    User user;
    ImageView gif;
    ImageView logo;
    Button mresend_verificationBtn;
    Button mreset_passwordBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        gif=(ImageView) findViewById(R.id.gif);
        mEmailEt = (EditText) findViewById(R.id.login_usernameEt);
        mPasswordEt = (EditText) findViewById(R.id.login_passwordEt);
        mLoginBtn = (Button) findViewById(R.id.loginBtn);
        mSignupBtn = (Button) findViewById(R.id.login_signupBtn);
        logo=(ImageView) findViewById(R.id.imageView);
        mresend_verificationBtn =(Button) findViewById(R.id.resend_verificationBtn);
        mreset_passwordBtn =(Button) findViewById(R.id.reset_passwordBtn);
        mAuth = FirebaseAuth.getInstance();
        //Verifica daca esti conectat la internet
        if (!verifyInternetConnectivty())
            Toast.makeText(LoginActivity.this, "Please connect to the internet", Toast.LENGTH_SHORT).show();

        //daca esti deja logat te duce direct in menu activity
        if (mAuth.getCurrentUser() != null) {
            startgif();
            retrieve_user();
        }
        //daca nu esti logat
        else {



            //OnClick care duce la activitatea de signup
            mSignupBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                    startActivityForResult(i, 1);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
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
    mreset_passwordBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            resetpassword();
        }
    });

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
                                FirebaseAuth mAuth=FirebaseAuth.getInstance();
                                FirebaseUser user=mAuth.getCurrentUser();
                                if(user.isEmailVerified()){
                                startgif();
                                retrieve_user();}
                                else{
                                    Toast.makeText(LoginActivity.this,"Please verify your email" +
                                            " adress before you sign in",Toast.LENGTH_LONG).show();
                                    makeresendvisible();
                                }

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
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference myRef = database;
        user = new User();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.child("id").child(uid).getValue(User.class);
                String LobbyID = dataSnapshot.child("id").child(uid).child("Lobby").getValue(String.class);
                if (LobbyID != null){
                    Intent i = new Intent(LoginActivity.this, LobbyActivity.class);
                    i.putExtra("User", user);
                    i.putExtra("Lobby", dataSnapshot.child("SportsLobby").child(LobbyID).getValue(LobbySports.class));
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(LoginActivity.this, MenuActivity.class);
                    i.putExtra("User", user);
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, databaseError.toException().toString(), Toast.LENGTH_LONG).show();
            }

        });

    }

    void startgif(){
        GlideDrawableImageViewTarget glideTarget = new GlideDrawableImageViewTarget(gif);

        Glide.with(LoginActivity.this)
                .load(R.raw.teamuppending2)
                .into(glideTarget);
        gif.setVisibility(View.VISIBLE);
        mEmailEt.setVisibility(View.INVISIBLE);
        mEmailEt.setEnabled(false);
        mPasswordEt.setVisibility(View.INVISIBLE);
        mPasswordEt.setEnabled(false);
        logo.setVisibility(View.INVISIBLE);
        logo.setEnabled(false);
        mSignupBtn.setVisibility(View.INVISIBLE);
        mSignupBtn.setEnabled(false);
        mLoginBtn.setVisibility(View.INVISIBLE);
        mLoginBtn.setEnabled(false);
        mreset_passwordBtn.setVisibility(View.INVISIBLE);
        mresend_verificationBtn.setVisibility(View.GONE);
    }

    void makeresendvisible(){
        mresend_verificationBtn.setVisibility(View.VISIBLE);
        Toast.makeText(LoginActivity.this, "Signup succeded",
                Toast.LENGTH_SHORT).show();
        final FirebaseUser fuser=mAuth.getCurrentUser();
        fuser.sendEmailVerification()
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,
                                    "Verification email sent to " + fuser.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void resetpassword(){
        String email=mEmailEt.getText().toString().trim();
        if(!isEmailValid(email))
            Toast.makeText(LoginActivity.this,"Please enter your email",Toast.LENGTH_SHORT).show();
        else{
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,"Password reset email sent",Toast.LENGTH_SHORT).show();
                        }
                    }
                });}
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}

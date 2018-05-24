package com.orez.teamup.teamup;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText mpassEt;
    EditText memailEt;
    Button msignupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        memailEt=(EditText) findViewById(R.id.signup_emailEt);
        mpassEt=(EditText) findViewById(R.id.signup_passwordEt);
        msignupBtn=(Button) findViewById(R.id.signupBtn);
        mAuth = FirebaseAuth.getInstance();
        msignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Daca exista ceva orice in casuta de mail si are parola de minim 6 caractere
                if(memailEt.getText().toString().trim()!="" && mpassEt.getText().toString().trim().length()>=6)
                    signup();
                else if(mpassEt.getText().toString().trim().length()<6)
                    Toast.makeText(SignupActivity.this, "Password should be at least 6 characters long", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(SignupActivity.this, "Invalid email", Toast.LENGTH_SHORT).show();
            }
        });

    }
    protected void signup(){
        mAuth.createUserWithEmailAndPassword(memailEt.getText().toString(), mpassEt.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Daca a fost inregistrat cu succes
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(SignupActivity.this, "Signup succeded",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            //Daca nu a fost inregistrat cu succes
                            Toast.makeText(SignupActivity.this, "Signup failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}

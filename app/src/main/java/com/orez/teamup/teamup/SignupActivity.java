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
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText mpassEt;
    EditText memailEt;
    EditText mpassrepeatEt;
    Button msignupBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        memailEt=(EditText) findViewById(R.id.signup_emailEt);
        mpassEt=(EditText) findViewById(R.id.signup_passwordEt);
        msignupBtn=(Button) findViewById(R.id.signupBtn);
        mpassrepeatEt=(EditText) findViewById(R.id.signup_password_repeatEt);
        mAuth = FirebaseAuth.getInstance();
        msignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isEmailValid(memailEt.getText().toString().trim()) && mpassEt.getText().toString().trim().length()>=6 &&
                        mpassEt.getText().toString().trim().equals(mpassrepeatEt.getText().toString().trim()))
                    //Daca mailul e valid si are parola de minim 6 caractere si parolele se potrivesc
                    signup();
                else if(!isEmailValid(memailEt.getText().toString().trim()))
                    //Daca mailul nu e valid
                    Toast.makeText(SignupActivity.this, "Invalid email", Toast.LENGTH_SHORT).show();
                else if(mpassEt.getText().toString().trim().length()<6)
                    //Daca parola e prea scurta
                    Toast.makeText(SignupActivity.this, "Passwords should be at least 6 characters long", Toast.LENGTH_SHORT).show();
                else if(!mpassEt.getText().toString().trim().equals(mpassrepeatEt.getText().toString().trim()))
                    //Daca parolele nu se potrivesc
                    Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
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
                            try{
                                throw task.getException();
                            }
                            catch (FirebaseAuthUserCollisionException existEmail)
                            {
                                //Daca mailul e deja folosit
                                Toast.makeText(SignupActivity.this, "Email already exists",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            //Daca nu a fost inregistrat cu succes din alt motiv
                            Toast.makeText(SignupActivity.this, "Signup failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    //Verifica validitatea mailului
    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}

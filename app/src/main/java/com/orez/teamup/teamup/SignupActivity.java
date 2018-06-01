package com.orez.teamup.teamup;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;


public class SignupActivity extends Activity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    EditText mpassEt;
    EditText memailEt;
    EditText mpassrepeatEt;
    EditText mfirstnameEt;
    EditText mlastnameEt;
    EditText mbirthdayEt;
    Button msignupBtn;
    Button mphotoBtn;
    Uri photouri;
    Uri file;
    FirebaseUser user;
    int minAge = 16;
    User muser;
    private StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        memailEt = (EditText) findViewById(R.id.signup_emailEt);
        mpassEt = (EditText) findViewById(R.id.signup_passwordEt);
        msignupBtn = (Button) findViewById(R.id.signupBtn);
        mphotoBtn=(Button) findViewById(R.id.signup_upload_photo);
        mpassrepeatEt = (EditText) findViewById(R.id.signup_password_repeatEt);
        mfirstnameEt = (EditText) findViewById(R.id.signup_first_nameEt);
        mlastnameEt = (EditText) findViewById(R.id.signup_last_nameEt);
        mbirthdayEt = (EditText) findViewById(R.id.signup_birthdayEt);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        msignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isEmailValid(memailEt.getText().toString().trim()) && mpassEt.getText().toString().trim().length() >= 6 &&
                        mpassEt.getText().toString().trim().equals(mpassrepeatEt.getText().toString().trim()) && checkdata())
                    //Daca mailul e valid si are parola de minim 6 caractere si parolele se potrivesc
                    signup();
                else if (!isEmailValid(memailEt.getText().toString().trim()))
                    //Daca mailul nu e valid
                    Toast.makeText(SignupActivity.this, "Invalid email", Toast.LENGTH_SHORT).show();
                else if (mpassEt.getText().toString().trim().length() < 6)
                    //Daca parola e prea scurta
                    Toast.makeText(SignupActivity.this, "Passwords should be at least 6 characters long", Toast.LENGTH_SHORT).show();
                else if (!mpassEt.getText().toString().trim().equals(mpassrepeatEt.getText().toString().trim()))
                    //Daca parolele nu se potrivesc
                    Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                else if (!checkdata())
                    //Daca nu e completat numele sau parola
                    Toast.makeText(SignupActivity.this, "You should fill in all the fields", Toast.LENGTH_SHORT).show();
            }
        });
        //daca ai selectat campul de zi de nastere
        mbirthdayEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkdate();
            }
        });
        //onClick pentru upload image, face un intent implicit pentru galerie
        mphotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        Intent i=new Intent(Intent.ACTION_PICK);
                        i.setType("image/*");
                        startActivityForResult(i,1);

            }
        });

    }

    protected void signup() {
        mAuth.createUserWithEmailAndPassword(memailEt.getText().toString(), mpassEt.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Daca a fost inregistrat cu succes
                            user = mAuth.getCurrentUser();
                            Toast.makeText(SignupActivity.this, "Signup succeded",
                                    Toast.LENGTH_SHORT).show();
                            setvalues();
                            returntologin();
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException existEmail) {
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

    //Verifica daca datele sunt goale
    boolean checkdata() {
        if (mfirstnameEt.getText().toString().trim().equals("") ||
                mlastnameEt.getText().toString().trim().equals("") ||
                mbirthdayEt.getText().toString().trim().equals(""))
            return false;
        else return true;
    }

    //pune ce mai trebuie in database
    void setvalues() {
        muser=new User(mfirstnameEt.getText().toString().trim(),mlastnameEt.getText().toString().trim(),
                mbirthdayEt.getText().toString());
        StorageReference ref=mStorageRef.child(user.getUid());
        ref.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                photouri = taskSnapshot.getDownloadUrl();
                mphotoBtn.setText(photouri.toString());
            }
        });
        mDatabase.child("id").child(user.getUid()).
                setValue(muser);
    }

    //in mod surprinzator, se intoarce in login si completeaza automat casutele
    public void returntologin() {
        Intent i = new Intent();
        i.putExtra("email", memailEt.getText().toString());
        i.putExtra("pass", mpassEt.getText().toString());
        setResult(Activity.RESULT_OK, i);
        finish();
    }

    //verifica data si daca userul e destul de mare
    public void checkdate() {

        final Calendar c = Calendar.getInstance();
        final int mDay, mYear, mMonth;
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        //aici face chestiuta de calendar
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        if (isoldenough(mYear, mMonth + 1, mDay, year, monthOfYear + 1, dayOfMonth))
                            mbirthdayEt.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        else
                            Toast.makeText(SignupActivity.this, "You must be " + Integer.toString(minAge) + " or older to sign up", Toast.LENGTH_SHORT).show();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
    //verifica varsta
    public boolean isoldenough(int cyear, int cmonth, int cday, int year, int month, int day) {
        if (cyear - year > minAge)
            return true;
        if (cyear - year == minAge && (month < cmonth))
            return true;
        if (cyear - year == minAge && cmonth == month && day <= cday)
            return true;
        else return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //pentru cand vine din galerie
        if(requestCode==1){
            file=data.getData();

        }
    }
}

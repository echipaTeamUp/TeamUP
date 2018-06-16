package com.orez.teamup.teamup;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MenuActivity extends Activity {

    ImageButton mprofileBtn;
    ImageButton msignoutBtn;
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Intent i = getIntent();

        user = (User) i.getSerializableExtra("User");
        mprofileBtn = (ImageButton) findViewById(R.id.menu_profileBtn);
        msignoutBtn = (ImageButton) findViewById(R.id.menu_signoutBtn);

        checkLocationPermission();
        //Daca apesi pe profil, te duce la profil
        mprofileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuActivity.this, ProfileActivity.class);
                i.putExtra("User", user);
                i.putExtra("Req_code",1);
                startActivity(i);
            }
        });



        msignoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                setResult(Activity.RESULT_OK);
                Intent i = new Intent(MenuActivity.this, LoginActivity.class);
                startActivity(i);
                finish();

            }
        });
    }

    public void loadSportsActivity(View view) {
        Intent i = new Intent(MenuActivity.this, SportsActivity.class);
        i.putExtra("User",user);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    public void checkLocationPermission() {
        //aici verifica daca ai deja permisiunea
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //Aici zice de ce vrea permisiunea
            new AlertDialog.Builder(this)
                    .setMessage(R.string.text_location_permission)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Aici cere efectiv permisiunea
                            ActivityCompat.requestPermissions(MenuActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    1);
                        }
                    })
                    .create()
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1 && (grantResults.length==0 || grantResults[0]==PackageManager.PERMISSION_DENIED))
        {}// TODO:facem ceva sa blocam aplicatia daca nu avem permisiune de locatie
    }
}

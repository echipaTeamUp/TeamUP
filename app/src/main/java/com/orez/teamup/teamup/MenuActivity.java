package com.orez.teamup.teamup;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuActivity extends Activity {

    ImageButton mProfileBtn;
    ImageButton mSignoutBtn;
    Button mSportsBtn;
    Button mEsportsBtn;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Intent i = getIntent();

        user = (User) i.getSerializableExtra("User");
        mProfileBtn = (ImageButton) findViewById(R.id.menu_profileBtn);
        mSignoutBtn = (ImageButton) findViewById(R.id.menu_signoutBtn);
        mSportsBtn = (Button) findViewById(R.id.sportsBtn);
        mEsportsBtn = (Button) findViewById(R.id.esportsBtn);

        checkLocationPermission();

        // Daca esti deja intr-un lobby te baga in el
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Verifica daca esti intr-un lobby
                final String LobbyID = dataSnapshot.child("id").child(uid).child("Lobby").getValue(String.class);

                // Daca da, te duce in lobby
                if (LobbyID != null) {
                    // verifica daca lobby-ul e de tip sports sau esports
                    if (dataSnapshot.child("SportsLobby").child(LobbyID).exists()){
                        Intent i = new Intent(MenuActivity.this, LobbyActivity.class);
                        i.putExtra("User", user);
                        i.putExtra("Lobby", dataSnapshot.child("SportsLobby").child(LobbyID).getValue(LobbySports.class));
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                    } else {
                        Intent i = new Intent(MenuActivity.this, LobbyEsportsActivity.class);
                        i.putExtra("User", user);
                        i.putExtra("Lobby", dataSnapshot.child("EsportsLobby").child(LobbyID).getValue(LobbyEsports.class));
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MenuActivity.this, databaseError.toException().toString(), Toast.LENGTH_LONG).show();
            }
        });

        //Daca apesi pe profil, te duce la profil
        mProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuActivity.this, ProfileActivity.class);
                i.putExtra("User", user);
                i.putExtra("Req_code", 1);
                startActivity(i);
            }
        });

        //Signout
        mSignoutBtn.setOnClickListener(new View.OnClickListener() {
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
        i.putExtra("User", user);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    public void loadeSportsActivity(View view) {
        /* sori viju
        Intent i = new Intent(MenuActivity.this, ProfileActivity.class);
        i.putExtra("Req_code", 2);
        i.putExtra("Uid", "xIWQ0gd4NDhO16SAzLQAKguagOc2");
        startActivity(i);*/

        Intent i = new Intent(MenuActivity.this, EsportsActivity.class);
        i.putExtra("User", user);
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
        if (requestCode == 1 && (grantResults.length == 0 || grantResults[0] == PackageManager.PERMISSION_DENIED)) {
            mSportsBtn.setVisibility(View.GONE);
            mSportsBtn.setVisibility(View.GONE);
        }
    }
}

package com.orez.teamup.teamup;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


public class SportsActivity extends Activity {
    private ArrayList<String> data;
    private ArrayList<LobbySports> arr = new ArrayList<>();
    ImageButton mfab;
    FloatingActionButton mSendFab;
    User user;
    LocationManager mLocationManager;
    LocationListener mLocationListener;
    ImageButton mProfileBtn;
    ImageButton mSignoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports);

//        Resources res = getResources();
//        String[] sports = res.getStringArray(R.array.Sports);
        user = (User) getIntent().getSerializableExtra("User");
        data = new ArrayList<String>();
        mfab = (ImageButton) findViewById(R.id.floatingactionbutton_create);
        mSendFab = (FloatingActionButton) findViewById(R.id.floatingActionButton_send);
        mProfileBtn = (ImageButton) findViewById(R.id.menu_profileBtn);
        mSignoutBtn = (ImageButton) findViewById(R.id.menu_signoutBtn);

        data.addAll(Collections.singleton(com.orez.teamup.teamup.sports.values().toString()));

        final Spinner mSelectSportSpinner = (Spinner) findViewById(R.id.select_filter_spinner);
        mSelectSportSpinner.setAdapter(new ArrayAdapter<sports>(this, android.R.layout.simple_list_item_1, sports.values()));

        //Daca apesi pe profil, te duce la profil
        mProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SportsActivity.this, ProfileActivity.class);
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
                Intent i = new Intent(SportsActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        mfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SportsActivity.this, New_lobby_activity.class);
                i.putExtra("User", user);
                startActivity(i);
            }
        });

        mSendFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if (mLocationManager != null) {
                            mLocationManager.removeUpdates(mLocationListener);
                            mLocationManager = null;
                        }
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };
                double longitude=0,latitude=0;
                mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    if (ActivityCompat.checkSelfPermission(SportsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SportsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000,
                            300, mLocationListener);
                    longitude=mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
                    latitude=mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
                }
                else if(mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                    if (ActivityCompat.checkSelfPermission(SportsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SportsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000,
                            300, mLocationListener);
                    longitude=mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude();
                    latitude=mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude();
                }
                else {
                    Toast.makeText(SportsActivity.this,"Please enable your location services",Toast.LENGTH_SHORT);
                    return;
                }



                final FilterSports mFilterSport = new FilterSports(user.getAge(),
                        20, skillGroupSports.ALL, (sports) mSelectSportSpinner.getSelectedItem(), longitude,latitude);

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("SportsLobby");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        arr.clear();
                        arr.addAll(LobbySports.filter(dataSnapshot, mFilterSport));

                        Intent i = new Intent(SportsActivity.this, ResultsActivity.class);
                        i.putExtra("User", user);
                        i.putExtra("lobbys", arr);
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


            }
        });
    }

    // animeaza cand apesi pe back ca sa te intorci in activitatea trecuta
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    @Override
    protected void onStart() {
        super.onStart();
         }
}
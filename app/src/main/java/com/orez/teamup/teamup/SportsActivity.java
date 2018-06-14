package com.orez.teamup.teamup;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class SportsActivity extends Activity {
    private ArrayList<String> data;
    private ArrayList<LobbySports> arr = new ArrayList<>();
    FloatingActionButton mfab;
    FloatingActionButton mSendFab;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports);

//        Resources res = getResources();
//        String[] sports = res.getStringArray(R.array.Sports);
        user = (User) getIntent().getSerializableExtra("User");
        data = new ArrayList<String>();
        mfab = (FloatingActionButton) findViewById(R.id.floatingactionbutton_create);
        mSendFab = (FloatingActionButton) findViewById(R.id.floatingActionButton_send);

        data.addAll(Collections.singleton(com.orez.teamup.teamup.sports.values().toString()));

        final Spinner mSelectSportSpinner = (Spinner) findViewById(R.id.select_filter_spinner);
        mSelectSportSpinner.setAdapter(new ArrayAdapter<sports>(this, android.R.layout.simple_list_item_1, sports.values()));

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
                 LocationListener mLocationListener=new LocationListener() {
                     @Override
                     public void onLocationChanged(Location location) {

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
                LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                if (ActivityCompat.checkSelfPermission(SportsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SportsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000,
                        300, mLocationListener);

                final FilterSports mFilterSport = new FilterSports(user.getAge(),
                        20,skillGroupSports.ALL,(sports) mSelectSportSpinner.getSelectedItem(),mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).
                        getLongitude(), mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude());

                // TODO: in loc de filtrele astea trebuie luate sporturile din listview si atasate la niste filtre

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
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // TODO: HANDLE ERROR
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
}
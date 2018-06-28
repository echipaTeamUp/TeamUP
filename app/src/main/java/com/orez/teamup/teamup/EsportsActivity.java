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
import android.view.View;
import android.widget.AdapterView;
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


public class EsportsActivity extends Activity {
    ImageButton mfab;
    FloatingActionButton mSendFab;
    User user;
    LocationManager mLocationManager;
    LocationListener mLocationListener;
    ImageButton mProfileBtn;
    ImageButton mSignoutBtn;
    Spinner mranks_spinner;
    Spinner mSelectSportSpinner;
    private ArrayList<String> data;
    private ArrayList<LobbyEsports> arr = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esports);

        user = (User) getIntent().getSerializableExtra("User");
        data = new ArrayList<String>();
        mfab = (ImageButton) findViewById(R.id.floatingactionbutton_create);
        mSendFab = (FloatingActionButton) findViewById(R.id.floatingActionButton_send);
        mProfileBtn = (ImageButton) findViewById(R.id.menu_profileBtn);
        mSignoutBtn = (ImageButton) findViewById(R.id.menu_signoutBtn);
        mranks_spinner = (Spinner) findViewById(R.id.select_rank_spinner);
        mSelectSportSpinner = (Spinner) findViewById(R.id.select_filter_spinner);

        //Daca apesi pe profil, te duce la profil
        mProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EsportsActivity.this, ProfileActivity.class);
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
                Intent i = new Intent(EsportsActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        data.addAll(Collections.singleton(com.orez.teamup.teamup.esports.values().toString()));

        mSelectSportSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changeranks();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSelectSportSpinner.setAdapter(new ArrayAdapter<esports>(this, android.R.layout.simple_list_item_1, esports.values()));

        mfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EsportsActivity.this, New_lobby_esports_activity.class);
                i.putExtra("User", user);
                startActivity(i);
            }
        });

        mSendFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if(mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                {
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
                    if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                        if (ActivityCompat.checkSelfPermission(EsportsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(EsportsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }

                        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000,
                                300, mLocationListener);
                        longitude=mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
                        latitude=mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
                    }
                    else if(mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                        if (ActivityCompat.checkSelfPermission(EsportsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(EsportsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }

                        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000,
                                300, mLocationListener);
                        longitude=mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude();
                        latitude=mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude();
                    }
                    else {
                        Toast.makeText(EsportsActivity.this,"Please enable your location services",Toast.LENGTH_SHORT);
                        return;
                    }
                CSGOranks csgorank;
                LoLranks LoLrank;
                switch ((esports) mSelectSportSpinner.getSelectedItem()) {
                    case CSGO:
                        csgorank = (CSGOranks) mranks_spinner.getSelectedItem();
                        LoLrank = null;
                        break;
                    case LoL:
                        csgorank = null;
                        LoLrank = (LoLranks) mranks_spinner.getSelectedItem();
                        break;
                    default:
                        csgorank = (CSGOranks) mranks_spinner.getSelectedItem();
                        LoLrank = LoLranks.Bronze1;
                        break;
                }

                final FilterEsports mFilterEsport = new FilterEsports(20, (esports) mSelectSportSpinner.getSelectedItem(), longitude,latitude,csgorank,LoLrank);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("EsportsLobby");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        arr.clear();
                        arr.addAll(LobbyEsports.filter(dataSnapshot, mFilterEsport));

                        Intent i = new Intent(EsportsActivity.this, ResultsEsportsActivity.class);
                        i.putExtra("User", user);
                        i.putExtra("lobbys", arr);
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // TODO: HANDLE ERROR
                    }
                });
            }else Toast.makeText(EsportsActivity.this,"Please enable your location services",Toast.LENGTH_SHORT).show();
            }

        });
    }

    void changeranks() {
        switch ((esports) mSelectSportSpinner.getSelectedItem()) {
            case CSGO:
                mranks_spinner.setAdapter(new ArrayAdapter<CSGOranks>(this, android.R.layout.simple_list_item_1, CSGOranks.values()));
                break;
            case LoL:
                mranks_spinner.setAdapter(new ArrayAdapter<LoLranks>(this, android.R.layout.simple_list_item_1, LoLranks.values()));
                break;
        }

    }

    // animeaza cand apesi pe back ca sa te intorci in activitatea trecuta
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }
}
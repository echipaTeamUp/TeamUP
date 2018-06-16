package com.orez.teamup.teamup;


import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

public class New_lobby_activity extends AppCompatActivity implements OnMapReadyCallback {
    MapView mapView;
    GoogleMap map;
    EditText mnumber_playersEt;
    Spinner mspors_spinner;
    Button mnewLobbyBtn;
    User user;
    LatLng latlong;
    TextView mplaceTV;
    TextView mtimeTV;
    Marker marker;
    RadioButton today_Rbtn;
    RadioButton tomorrow_Rbtn;
    RadioGroup radioGroup;
    int Lmonth,Lday,Lhour,Lminute;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_lobby_activity);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        mnumber_playersEt = (EditText) findViewById(R.id.number_playersET);
        mspors_spinner = (Spinner) findViewById(R.id.sport_spinner);
        mnewLobbyBtn = (Button) findViewById(R.id.new_lobbyBtn);
        mplaceTV = (TextView) findViewById(R.id.placeTV);
        mtimeTV = (TextView) findViewById(R.id.timeTV);
        today_Rbtn=(RadioButton) findViewById(R.id.today_RBtn);
        tomorrow_Rbtn=(RadioButton) findViewById(R.id.tomorrow_RBtn);
        radioGroup=(RadioGroup) findViewById(R.id.radioGroup);
        user = (User) getIntent().getSerializableExtra("User");
        //adapter pentru spinner
        mspors_spinner.setAdapter(new ArrayAdapter<sports>(this, android.R.layout.simple_list_item_1, sports.values()));
        //onclick creare lobby
        mnewLobbyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkallfields()) {
                    Calendar calendar=Calendar.getInstance();
                    if(tomorrow_Rbtn.isChecked())
                        calendar.add(Calendar.DAY_OF_YEAR,1);
                    Lmonth=calendar.get(Calendar.MONTH);Lday=calendar.get(Calendar.DAY_OF_MONTH);
                    int maxlobbysize = Integer.parseInt(mnumber_playersEt.getText().toString());
                    LobbySports mlobby = new LobbySports(Lobby.getNewID(), maxlobbysize, user.getAge() - 3,
                            user.getAge() + 3, (sports) mspors_spinner.getSelectedItem(),
                            skillGroupSports.ALL, latlong.longitude, latlong.latitude, FirebaseAuth.getInstance().getUid(),
                            mplaceTV.getText().toString(),Lmonth,Lday,Lhour,Lminute);
                    mlobby.setSkill(skillGroupSports.ALL);
                    Log.v("log", "apeleaza writetodb din new_lobby_activity");
                    mlobby.writeToDB();

                    Intent i = new Intent(New_lobby_activity.this, LobbyActivity.class);
                    i.putExtra("Lobby", mlobby);
                    i.putExtra("User", user);
                    makeToast("Lobby created");
                    startActivity(i);
                }
            }
        });
        //onClick selectat ora
        mtimeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                final int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(New_lobby_activity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        //Daca ora e buna
                        if (checktime(selectedHour, selectedMinute, mcurrentTime.get(Calendar.HOUR_OF_DAY),
                                mcurrentTime.get(Calendar.MINUTE))) {
                            mtimeTV.setText("Hour: " + selectedHour + ":" + selectedMinute);
                            Lhour=selectedHour;Lminute=selectedMinute;
                        }
                        else
                            makeToast("Time should be in at least an hour from now");
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        //Daca schimba data, reseteaza ora
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mtimeTV.setText(R.string.select_an_hour);

            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(New_lobby_activity.this), 1);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
        if (ActivityCompat.checkSelfPermission(New_lobby_activity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(New_lobby_activity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);

    }


    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //cand se intoarce din placepicker
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                mplaceTV.setText("Location: " + place.getName());
                //astea sunt coordonatele care vor fi incarcate in FireBase
                latlong = place.getLatLng();
                //daca pusese alt marker inainte, il sterge pe ala
                if (marker != null)
                    marker.remove();
                marker = map.addMarker(new MarkerOptions()
                        .position(latlong)
                        .title("Lobby location"));
                zoomcamera(latlong);
            }
        }
    }

    //duce camera unde vrei
    protected void zoomcamera(LatLng latLng) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        map.animateCamera(CameraUpdateFactory.zoomIn());
        map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }

    //verifica sa fie ora in cel putin o ora de la ora curenta
    public boolean checktime(int hour, int minute, int chour, int cminute) {
        if(tomorrow_Rbtn.isChecked()&& chour<23)
            return true;
        if(tomorrow_Rbtn.isChecked() && chour==23 && hour==0 && minute<cminute)
            return false;
        if (hour - chour > 1)
            return true;
        if (hour - chour == 1 && minute >= cminute)
            return true;
        else return false;
    }

    public boolean checkallfields() {
        int maxlobbysize;
        try {
            maxlobbysize = Integer.parseInt(mnumber_playersEt.getText().toString());
        } catch (Exception e) {
            makeToast("Max lobby size is not a valid format");
            return false;
        }
        if (maxlobbysize < 2) {
            makeToast("The max lobby size shoud be at least 2");
            return false;
        }
        if (marker == null) {
            makeToast("Please select a location");
            return false;
        }
        if (mtimeTV.getText().toString().equals(R.string.select_an_hour)) {
            makeToast("Please select an hour");
            return false;
        }
        return true;
    }

    public void makeToast(String string) {
        Toast.makeText(New_lobby_activity.this, string, Toast.LENGTH_SHORT).show();
    }
}

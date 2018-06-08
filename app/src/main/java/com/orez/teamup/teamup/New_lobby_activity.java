package com.orez.teamup.teamup;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

public class New_lobby_activity extends AppCompatActivity implements OnMapReadyCallback{
    MapView mapView;
    GoogleMap map;
    EditText mnumber_playersEt;
    Spinner mspors_spinner;
    Button mnewLobbyBtn;
    User user;
    String latlong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_lobby_activity);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        mnumber_playersEt=(EditText) findViewById(R.id.number_playersET);
        mspors_spinner=(Spinner) findViewById(R.id.sport_spinner);
        mnewLobbyBtn=(Button) findViewById(R.id.new_lobbyBtn);
        user=(User) getIntent().getSerializableExtra("User");
        //adapter pentru spinner
        mspors_spinner.setAdapter(new ArrayAdapter<sports>(this,android.R.layout.simple_list_item_1,sports.values()));
        mnewLobbyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int maxlobbysize=Integer.parseInt(mnumber_playersEt.getText().toString());
                if(maxlobbysize>1) {
                    {
                        LobbySports mlobby = new LobbySports(lobbyAvailability.ANYONE, maxlobbysize, user.getAge() - 3,
                                user.getAge() + 3, (sports) mspors_spinner.getSelectedItem(), skillGroupSports.ALL);
                        mlobby.setSkill(skillGroupSports.ALL);
                        mlobby.writeToDB();
                    }
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng arg0) {
                Toast.makeText(New_lobby_activity.this, "Ai apasat", Toast.LENGTH_SHORT).show();
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
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                latlong=place.getLatLng().toString();
            }
        }
    }
}

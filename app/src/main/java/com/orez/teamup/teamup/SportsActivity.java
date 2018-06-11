package com.orez.teamup.teamup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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

                ArrayList<FilterSports> fuckyou = new ArrayList<>();
                FilterSports mFilterSport = new FilterSports();
                mFilterSport.setSport((sports) mSelectSportSpinner.getSelectedItem());
                mFilterSport.setAge(user.getAge());
                fuckyou.add(mFilterSport);

                final ArrayList<FilterSports> filters = fuckyou;

                // TODO: in loc de filtrele astea trebuie luate sporturile din listview si atasate la niste filtre

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("SportsLobby");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (FilterSports f: filters){
                            arr.addAll(LobbySports.filter(dataSnapshot, f));
                        }
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

    public void loadFilterActivity(View view) {
        Intent intent = new Intent(SportsActivity.this, FilterSportsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}
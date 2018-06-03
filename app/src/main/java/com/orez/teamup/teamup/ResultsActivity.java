package com.orez.teamup.teamup;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Debug;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ResultsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        LobbySports xd = new LobbySports();
        xd.addUser("123");


        LobbySports.readLobbysByFilters(new FilterSports());
    }
}

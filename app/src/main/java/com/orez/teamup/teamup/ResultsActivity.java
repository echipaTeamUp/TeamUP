package com.orez.teamup.teamup;

import android.app.Activity;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;

import java.util.ArrayList;

public class ResultsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        LobbySports xd = new LobbySports();
        xd.addUser("123");
        xd.setFilter(new FilterSports());

        FilterSports filterSports = new FilterSports();

        LobbySports.readLobbysByFilters(filterSports);
        ArrayList<LobbySports> arr = FilterSports.getLastReadLobbys();

        for(LobbySports lb : arr){
            Log.d("PANAMERA", lb.toString());
        }
    }
}

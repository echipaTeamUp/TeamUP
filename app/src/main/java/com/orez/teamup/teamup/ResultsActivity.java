package com.orez.teamup.teamup;

import android.app.Activity;
import android.os.Bundle;

public class ResultsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        LobbySports xd = new LobbySports();
        xd.writeToDB();
    }
}

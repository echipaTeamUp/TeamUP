package com.orez.teamup.teamup;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class Sports extends Activity {

    Button mListBtn;
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports);

        mListBtn.setText(new String("gay"));
        mListView = findViewById(R.id.SportsLst);
        mListView.addView(mListBtn);
    }

    // animeaza cand apesi pe back ca sa te intorci in activitatea trecuta
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }



}

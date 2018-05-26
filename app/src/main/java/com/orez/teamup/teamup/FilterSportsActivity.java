package com.orez.teamup.teamup;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.logging.Filter;

enum skillGroup {
    AMATEUR, EXPERIENCED, PRO, ALL
}

class FilterSports {
    public int minAge;
    public int maxAge;
    public int maxDistance;
    public skillGroup skill;

    public FilterSports(int _minAge, int _maxAge, int _maxDistance, skillGroup _sg) {
        this.minAge = _minAge;
        this.maxAge = _maxAge;
        this.maxDistance = _maxDistance;
        this.skill = _sg;
    }

    public FilterSports() {
        this.minAge = 0;
        this.maxAge = 100;
        this.maxDistance = 20000;
        this.skill = skillGroup.ALL;
    }
}

public class FilterSportsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_sports);
    }
}

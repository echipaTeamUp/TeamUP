package com.orez.teamup.teamup;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.logging.Filter;

enum skillGroup {
    AMATEUR, EXPERIENCED, PRO, ALL
}

class FilterSports {
    private int minAge;
    private int maxAge;
    private int maxDistance;
    private skillGroup skill;

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

    public void setAge(int min, int max){
        this.maxAge = max;
        this.minAge = min;
    }

    public void setMinAge(int minAge){
        this.minAge = minAge;
    }

    public void setMaxAge(int maxAge){
        this.maxDistance = maxAge;
    }

    public void setSkill(skillGroup sg){
        this.skill = sg;
    }

    public void setMaxDistance(int maxDistance){
        this.maxDistance = maxDistance;
    }

    public int getMinAge(){
        return this.minAge;
    }

    public int getMaxAge(){
        return this.maxAge;
    }

    public skillGroup getSkill(){
        return this.skill;
    }

    public int getMaxDistance(){
        return this.maxDistance;
    }
}

public class FilterSportsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_sports);
    }
}

package com.orez.teamup.teamup;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

enum skillGroupSports {
    AMATEUR, EXPERIENCED, PRO, ALL
}

enum sports{
    ANY, BASKETBALL, FOOTBALL, TENNIS, RUGBY, HANDBALL, CYCLING, PINGPONG
}

class FilterSports {
    private int minAge;
    private int maxAge;
    private int maxDistance;
    private skillGroupSports skill;
    private sports sport;

    public FilterSports(int _minAge, int _maxAge, int _maxDistance, skillGroupSports sg, sports sp) {
        this.minAge = _minAge;
        this.maxAge = _maxAge;
        this.maxDistance = _maxDistance;
        this.skill = sg;
        this.sport = sp;
    }

    public FilterSports() {
        this.minAge = -1;
        this.maxAge = -1;
        this.maxDistance = -1;
        this.skill = skillGroupSports.ALL;
        this.sport = sports.ANY;
    }

    public void setAge(int min, int max) {
        this.maxAge = max;
        this.minAge = min;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxDistance = maxAge;
    }

    public void setSkill(skillGroupSports sg) {
        this.skill = sg;
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }

    public void setSport(sports sp){
        this.sport = sp;
    }

    public int getMinAge() {
        return this.minAge;
    }

    public int getMaxAge() {
        return this.maxAge;
    }

    public skillGroupSports getSkill() {
        return this.skill;
    }

    public int getMaxDistance() {
        return this.maxDistance;
    }

    public sports getSport() {return this.sport; }
}

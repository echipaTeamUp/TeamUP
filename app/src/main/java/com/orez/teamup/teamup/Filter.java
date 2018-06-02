package com.orez.teamup.teamup;


import android.util.Log;

import com.google.firebase.database.DataSnapshot;
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

    private static ArrayList<LobbySports> lastReadLobbys = new ArrayList<>();

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

    public static ArrayList<LobbySports> getLastReadLobbys() {
        if (lastReadLobbys.size() == 0){
            Log.d("PANAMERA", "in lastRead nu sunt elemente");
        }
        return lastReadLobbys;
    }

    public static void setLastReadLobbys(DataSnapshot dataSnapshot){
        lastReadLobbys.clear();

        for (DataSnapshot child : dataSnapshot.getChildren()){
            lastReadLobbys.add(child.getValue(LobbySports.class));
        }

        if (lastReadLobbys.size() > 0)
            Log.d("PANAMERA", "last read nu e gol aici");
    }
}

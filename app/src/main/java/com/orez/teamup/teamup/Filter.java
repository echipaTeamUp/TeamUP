package com.orez.teamup.teamup;


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
        this.minAge = 0;
        this.maxAge = 100;
        this.maxDistance = 20000;
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

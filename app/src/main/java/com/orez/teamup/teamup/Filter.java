package com.orez.teamup.teamup;


import java.io.Serializable;

enum skillGroupSports {
    AMATEUR, EXPERIENCED, PRO, ALL
}

enum sports {
    BASKETBALL, FOOTBALL, TENNIS, RUGBY, HANDBALL, CYCLING, PINGPONG, BADMINTON
}

enum esports {
    CSGO, LoL
}

class FilterSports implements Serializable {
    private int age;
    private int maxDistance;
    private skillGroupSports skill;
    private sports sport;
    private double latitude;
    private double longitude;

    public FilterSports(int age, int maxDistance, skillGroupSports sg, sports sp, double longitude, double latitude) {
        this.age = age;
        this.maxDistance = maxDistance;
        this.skill = sg;
        this.sport = sp;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public FilterSports() {
        this.age = -1;
        this.maxDistance = 20;
        this.skill = skillGroupSports.ALL;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setSkill(skillGroupSports sg) {
        this.skill = sg;
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }

    public void setSport(sports sp) {
        this.sport = sp;
    }

    public int getAge() {
        return this.age;
    }

    public skillGroupSports getSkill() {
        return this.skill;
    }

    public int getMaxDistance() {
        return this.maxDistance;
    }

    public sports getSport() {
        return this.sport;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}

class FilterEsports implements Serializable {
    private int maxDistance;
    private esports esport;
    private double latitude;
    private double longitude;

    public FilterEsports(int maxDistance, esports esport, double longitude, double latitude) {
        this.maxDistance = maxDistance;
        this.esport = esport;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public FilterEsports(){}

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }

    public void setSport(esports sp) {
        this.esport = sp;
    }

    public int getMaxDistance() {
        return this.maxDistance;
    }

    public esports getEsport() {
        return this.esport;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
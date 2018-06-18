package com.orez.teamup.teamup;


import java.io.Serializable;

enum skillGroupSports {
    AMATEUR, EXPERIENCED, PRO, ALL
}

enum sports {
    BASKETBALL, FOOTBALL, TENNIS, RUGBY, HANDBALL, CYCLING, PINGPONG, BADMINTON
}
enum CSGOranks{
    Silver1, Silver2, Silver3, Silver4, Silver_Elite,Silver_Elite_Master,
    Gold_Nova1,Gold_Nova2,Gold_Nova3,Gold_Nova_Master,Master_Guardian1,Master_Guardian2,
    Master_Guardian_Elite,Distinguished_Master_Guardian,Legendary_Eagle,Legendary_Eagle_Master,
    Supreme_Master_First_Class,The_Global_Elite
}
enum LoLranks{
    Bronze1,Bronze2,Bronze3,Bronze4,Bronze5,Silver1,Silver2,Silver3,Silver4,Silver5,
    Gold1,Gold2,Gold3,Gold4,Gold5,Platinum1,Platinum2,Platinum3,Platinum4,Platinum5,
    Diamond1,Diamond2,Diamond3,Diamond4,Diamond5,Master,Challenger
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
    private CSGOranks csgOrank;
    private LoLranks loLrank;
    private double latitude;
    private double longitude;

    public FilterEsports(int maxDistance, esports esport, double longitude, double latitude,CSGOranks csgOrank,LoLranks loLrank) {
        this.maxDistance = maxDistance;
        this.esport = esport;
        this.longitude = longitude;
        this.latitude = latitude;
        this.csgOrank=csgOrank;
        this.loLrank=loLrank;
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

    public CSGOranks getCsgOrank() {
        return csgOrank;
    }

    public LoLranks getLoLrank() {
        return loLrank;
    }
}
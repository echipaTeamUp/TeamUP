package com.orez.teamup.teamup;


import java.io.Serializable;

enum skillGroupSports {
    AMATEUR, EXPERIENCED, PRO, ALL
}

enum sports {
    BASKETBALL, FOOTBALL, TENNIS, RUGBY, HANDBALL, CYCLING, PINGPONG, BADMINTON
}
enum CSGOranks{
    Silver1("Silver 1"), Silver2("Silver 2"), Silver3("Silver 3"), Silver4("Silver 4"), Silver_Elite("Silver Elite"),
    Silver_Elite_Master("Silver Elite Master"), Gold_Nova1("Gold Nova 1"),Gold_Nova2("Gold Nova 2"),Gold_Nova3("Gold Nova 3"),
    Gold_Nova_Master("Gold Nova Master"),Master_Guardian1("Master Guardian 1"),Master_Guardian2("Master Guardian 2"),
    Master_Guardian_Elite("Master Guardian Elite"),Distinguished_Master_Guardian("Distinguished Master Guardian"),
    Legendary_Eagle("Legendary Eagle"),Legendary_Eagle_Master("Legendary Eagle Master"),
    Supreme_Master_First_Class("Supreme Master First Class"),The_Global_Elite("The Global Elite");


    private String displayName;
    CSGOranks(String displayName){
        this.displayName=displayName;
    }
    public String displayName() { return displayName; }
    @Override public String toString(){
        return displayName;
    }
}
enum LoLranks{
    Bronze1("Bronze 1"),Bronze2("Bronze 2"),Bronze3("Bronze 3"),Bronze4("Bronze 4"),Bronze5("Bronze 5"),
    Silver1("Silver 1"),Silver2("Silver 2"),Silver3("Silver 3"),Silver4("Silver 4"),Silver5("Silver 5"),
    Gold1("Gold 1"),Gold2("Gold 2"),Gold3("Gold 3"),Gold4("Gold 4"),Gold5("Gold 5"),
    Platinum1("Platinum 1"),Platinum2("Platinum 2"),Platinum3("Platinum 3"),Platinum4("Platinum 4"),Platinum5("Platinum 5"),
    Diamond1("Diamond 1"),Diamond2("Diamond 2"),Diamond3("Diamond 3"),Diamond4("Diamond 4"),Diamond5("Diamond 5"),
    Master("Master"),Challenger("Challenger");
    private String displayName;
    LoLranks(String displayName){
        this.displayName=displayName;
    }
    public String displayName() { return displayName; }
    @Override public String toString(){
        return displayName;
    }
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
    private CSGOranks CSGOrank;
    private LoLranks LoLrank;
    private double latitude;
    private double longitude;

    public FilterEsports(int maxDistance, esports esport, double longitude, double latitude,CSGOranks CSGOrank,LoLranks LoLrank) {
        this.maxDistance = maxDistance;
        this.esport = esport;
        this.longitude = longitude;
        this.latitude = latitude;
        this.CSGOrank=CSGOrank;
        this.LoLrank=LoLrank;
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

    public CSGOranks getCSGOrank() {
        return CSGOrank;
    }

    public LoLranks getLoLrank() {
        return LoLrank;
    }
}
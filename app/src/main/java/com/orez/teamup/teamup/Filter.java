package com.orez.teamup.teamup;


enum skillGroupSports {
    AMATEUR, EXPERIENCED, PRO, ALL
}

enum sports {
    ANY, BASKETBALL, FOOTBALL, TENNIS, RUGBY, HANDBALL, CYCLING, PINGPONG
}

class FilterSports {
    private int age;
    private int maxDistance;
    private skillGroupSports skill;
    private sports sport;

    public FilterSports(int age, int maxDistance, skillGroupSports sg, sports sp) {
        this.age = age;
        this.maxDistance = maxDistance;
        this.skill = sg;
        this.sport = sp;
    }

    public FilterSports() {
        this.age = -1;
        this.maxDistance = -1;
        this.skill = skillGroupSports.ALL;
        this.sport = sports.ANY;
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

    public int getAge(){
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
}

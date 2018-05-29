package com.orez.teamup.teamup;


enum skillGroupSports {
    AMATEUR, EXPERIENCED, PRO, ALL
}

class FilterSports {
    protected int minAge;
    protected int maxAge;
    protected int maxDistance;
    protected skillGroupSports skill;

    public FilterSports(int _minAge, int _maxAge, int _maxDistance, skillGroupSports _sg) {
        this.minAge = _minAge;
        this.maxAge = _maxAge;
        this.maxDistance = _maxDistance;
        this.skill = _sg;
    }

    public FilterSports() {
        this.minAge = 0;
        this.maxAge = 100;
        this.maxDistance = 20000;
        this.skill = skillGroupSports.ALL;
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
}
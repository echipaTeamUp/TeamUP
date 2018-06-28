package com.orez.teamup.teamup;


import android.location.Location;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Lobby implements Serializable {
    protected String id;
    protected ArrayList<String> users = new ArrayList<>();
    protected int maxSize;
    protected String adminId;

    public Lobby(String id, int maxSize) {
        this.id = id;
        this.maxSize = maxSize;
    }

    public Lobby() {
    }

    public String getId() {
        return id;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public int getSize() {
        return users.size();
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public void setId(String id) {
        this.id = id;
    }


    // adds a new user to the lobby
    public void addUser(String userID) {
        if (users.size() == maxSize) {
            // TODO: handle LobbyFullException
            return;
        }

        // check if the user is already in the lobby
        for (String user : users) {
            if (user.equals(userID))
                return;
        }

        users.add(userID);
        FirebaseDatabase.getInstance().getReference().child("id").child(userID).child("Lobby").setValue(this.id);
        writeToDB();
    }

    // removes a user from the lobby
    public void removeUser(String userID) {
        users.remove(userID);
        FirebaseDatabase.getInstance().getReference().child("id").child(userID).child("Lobby").setValue(null);
        if (users.size() == 0)
            delete();
        else {
            adminId = users.get(0);
            writeToDB();
        }
    }

    // deletes this lobby from the DB
    public void delete() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Lobby")
                .child(this.getId());
        ref.removeValue();
    }

    public static String getNewID() {
        // generate random lobby key
        String key = "";
        for (int i = 0; i < 20; ++i) {
            Random r = new Random();
            char rand = (char) (r.nextInt(74) + 48);
            while (rand == 91 || rand == 93) {
                rand = (char) (r.nextInt(74) + 48);
            }
            key += Character.toString(rand);
        }
        return key;
    }

    public void writeToDB() {
    }
}

class LobbySports extends Lobby {

    protected boolean type = false; // false daca e de sports
    protected int minAge;
    protected int maxAge;
    protected sports sport;
    protected skillGroupSports skill;
    protected String locationName;
    protected double latitude;
    protected double longitude;
    protected int month, day, hour, minute;

    LobbySports(String id, int maxSize, int minAge, int maxAge,
                sports sport, skillGroupSports skill, double longitude, double latitude, String adminId,
                String locationName, int month, int day, int hour, int minute) {
        super(id, maxSize);
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.sport = sport;
        this.skill = skill;
        this.locationName = locationName;
        this.longitude = longitude;
        this.latitude = latitude;
        this.adminId = adminId;
        this.addUser(adminId);
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;

    }

    LobbySports() {
    }

    // writes this to the database
    @Override
    public void writeToDB() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("SportsLobby").
                child(this.getId());
        ref.setValue(this);
    }

    @Override
    public void delete() {
        // delete chat
        FirebaseDatabase.getInstance().getReference().child("Chat").child(this.getId()).removeValue();
        // delete lobby
        FirebaseDatabase.getInstance().getReference().child("SportsLobby").child(this.getId()).removeValue();
    }

    public boolean getType(){return type;}

    public int getMaxAge() {
        return maxAge;
    }

    public int getMinAge() {
        return minAge;
    }

    public sports getSport() {
        return sport;
    }

    public skillGroupSports getSkill() {
        return skill;
    }

    public String getLocationName() {
        return locationName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAdminId() {
        return adminId;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public void setSport(sports sport) {
        this.sport = sport;
    }

    public void setSkill(skillGroupSports skill) {
        this.skill = skill;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public static ArrayList<LobbySports> filter(DataSnapshot dataSnapshot, FilterSports filter) {

        ArrayList<LobbySports> arr = new ArrayList<>();

        Log.d("BUGS", "aici incepe sa filtreze");
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            LobbySports curr = ds.getValue(LobbySports.class);
            Log.d("BUGS", curr.getId());

            // age filter
            if (curr.getMaxAge() < filter.getAge() || curr.getMinAge() > filter.getAge())
                continue;

            // sport filter
            if (curr.getSport() != filter.getSport())
                continue;

            // skill filter
            if (curr.getSkill() != filter.getSkill())
                continue;

            // lobby full filter
            if (curr.getSize() == curr.getMaxSize())
                continue;

            // distance filter
            Location mfilterLocation = new Location("filter");
            mfilterLocation.setLatitude(filter.getLatitude());
            mfilterLocation.setLongitude(filter.getLongitude());
            Location mlobbyLocation = new Location("lobby");
            mlobbyLocation.setLongitude(curr.getLongitude());
            mlobbyLocation.setLatitude(curr.getLatitude());
            if (mfilterLocation.distanceTo(mlobbyLocation) / 1000 > 20)
                continue;

            // time filter
            Date currentDate = Calendar.getInstance().getTime();
            int cmonth = currentDate.getMonth(), cday = currentDate.getDay(), chour = currentDate.getHours(), cminute = currentDate.getMinutes();
            if (!verifyDate(cmonth, cday, chour, cminute, curr.getMonth(), curr.getDay(), curr.getHour(), curr.getMinute(), 20))
                continue;
            arr.add(curr);
        }

        return arr;
    }

    static boolean verifyDate(int cmonth, int cday, int chour, int cminute, int month, int day, int hour, int minute, int interval) {
        if(day<cday && month==cmonth)
            return false;
        if (hour < chour && day == cday)
            return false;
        if (hour == chour && day == cday && minute - cminute < interval)
            return false;
        if (chour == 23 && hour == 0 && minute - cminute < interval)
            return false;
        return true;
    }
}

class LobbyEsports extends Lobby {

    protected boolean type = true; //true daca e de esports
    protected esports esport;
    private CSGOranks CSGOrank;
    private LoLranks LoLrank;
    protected int month, day, hour, minute;

    LobbyEsports(String id, int maxSize, esports esport, String adminId,
                int month, int day, int hour, int minute,CSGOranks CSGOrank,LoLranks LoLrank) {
        super(id, maxSize);
        this.esport = esport;
        this.adminId = adminId;
        this.addUser(adminId);
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.CSGOrank=CSGOrank;
        this.LoLrank=LoLrank;
    }

    LobbyEsports() {
    }

    // writes this to the database
    @Override
    public void writeToDB() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("EsportsLobby").
                child(this.getId());
        ref.setValue(this);
    }

    @Override
    public void delete() {
        // delete chat
        FirebaseDatabase.getInstance().getReference().child("Chat").child(this.getId()).removeValue();
        // delete lobby
        FirebaseDatabase.getInstance().getReference().child("EsportsLobby").child(this.getId()).removeValue();
    }

    public boolean getType(){return type;}

    public esports getEsport() {
        return esport;
    }

    public String getAdminId() {
        return adminId;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public CSGOranks getCSGOrank() {
        return this.CSGOrank;
    }

    public LoLranks getLoLrank() {
        return this.LoLrank;
    }

    public void setEsport(esports esport) {
        this.esport = esport;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public void setCSGOrank(CSGOranks CSGOrank) {
        this.CSGOrank = CSGOrank;
    }

    public void setLoLrank(LoLranks loLrank) {
        LoLrank = loLrank;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public static ArrayList<LobbyEsports> filter(DataSnapshot dataSnapshot, FilterEsports filter) {

        ArrayList<LobbyEsports> arr = new ArrayList<>();

        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            LobbyEsports curr = ds.getValue(LobbyEsports.class);

            // esport filter
            if (curr.getEsport() != filter.getEsport())
                continue;

            // lobby full filter
            if (curr.getSize() == curr.getMaxSize())
                continue;

            // time filter
            Date currentDate = Calendar.getInstance().getTime();
            int cmonth = currentDate.getMonth(), cday = currentDate.getDay(), chour = currentDate.getHours(), cminute = currentDate.getMinutes();
            if (!verifyDate(cmonth, cday, chour, cminute, curr.getMonth(), curr.getDay(), curr.getHour(), curr.getMinute(), 5))
                continue;
            //rank filter
            if(filter.getEsport()==esports.CSGO && (curr.getCSGOrank()!=filter.getCSGOrank())){
                Log.v("log","curr: "+curr.getCSGOrank()+" filter: "+filter.getCSGOrank());
                continue;
            }

            if(filter.getEsport()==esports.LoL &&(curr.getLoLrank()!=filter.getLoLrank()))
                continue;
            arr.add(curr);
        }

        return arr;
    }

    static boolean verifyDate(int cmonth, int cday, int chour, int cminute, int month, int day, int hour, int minute, int interval) {
        if (hour < chour && day == cday)
            return false;
        if (hour == chour && day == cday && minute - cminute < interval)
            return false;
        if (chour == 23 && hour == 0 && minute - cminute < interval)
            return false;
        return true;
    }
}

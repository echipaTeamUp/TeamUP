package com.orez.teamup.teamup;


import android.location.Location;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

enum lobbyAvailability {
    ANYONE, FRIENDS, INVITES
}

public class Lobby implements Serializable {
    protected String id;
    protected ArrayList<String> users = new ArrayList<>();
    protected lobbyAvailability availability;
    protected int maxSize;
    protected String hour;
    protected String adminId;

    public Lobby(String id, lobbyAvailability availability, int maxSize) {
        this.id = id;
        this.availability = availability;
        this.maxSize = maxSize;
    }

    public Lobby(String id) {
        this.id = id;
        this.availability = lobbyAvailability.ANYONE;
        this.maxSize = -1;
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

    public lobbyAvailability getAvailability() {
        return availability;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public String getHour() {
        return hour;
    }

    public void setAvailability(lobbyAvailability availability) {
        this.availability = availability;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    // adds a new user to the lobby
    public void addUser(String userID) {
        if (users.size() == maxSize) {
            // TODO: handle LobbyFullException
            return;
        }

        // check if the user is already in the lobby
        for (String user : users){
            if (user.equals(userID))
                return;
        }

        users.add(userID);
        writeToDB();
    }

    // removes a user from the lobby
    public void removeUser(String userID) {
        users.remove(userID);
        if (users.size() == 0)
            delete();
        else
            writeToDB();
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

    protected int minAge;
    protected int maxAge;
    protected sports sport;
    protected skillGroupSports skill;
    protected String locationName;
    protected double latitude;
    protected double longitude;

    LobbySports(String id, lobbyAvailability availability, int maxSize, int minAge, int maxAge,
                sports sport, skillGroupSports skill, double longitude, double latitude, String adminId,
                String locationName, String hour) {
        super(id, availability, maxSize);
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.sport = sport;
        this.skill = skill;
        this.locationName = locationName;
        this.longitude = longitude;
        this.latitude = latitude;
        this.adminId = adminId;
        this.addUser(adminId);
        this.hour = hour;
    }

    LobbySports(String id) {
        super(id);
        this.maxAge = -1;
        this.minAge = -1;
        this.sport = sports.ANY;
        this.skill = skillGroupSports.ALL;
        this.adminId = "da";
        this.longitude = -1;
        this.latitude = -1;
        this.hour = "-1";
    }

    LobbySports() {
    }

    // writes this to the database
    @Override
    public void writeToDB() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("SportsLobby").
                child(this.getId());
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (StackTraceElement s : stackTraceElements) {
            Log.d("BUGS", s.toString());
        }
        ref.setValue(this);
    }

    @Override
    public void delete() {
        // delete chat
        FirebaseDatabase.getInstance().getReference().child("Chat").child(this.getId()).removeValue();
        // delete lobby
        FirebaseDatabase.getInstance().getReference().child("SportsLobby").child(this.getId()).removeValue();
    }

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

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public void setAge(int minAge, int maxAge) {
        this.minAge = minAge;
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

            arr.add(curr);
        }

        return arr;
    }
}

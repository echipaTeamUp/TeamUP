package com.orez.teamup.teamup;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

enum lobbyAvailability {
    ANYONE, FRIENDS, INVITES
}

public class Lobby {
    protected String id;
    protected ArrayList<String> users = new ArrayList<>();
    protected lobbyAvailability availability;
    protected int maxSize;

    public Lobby(String name, lobbyAvailability availability, int maxSize) {
        this.id = Lobby.getNewID();
        this.availability = availability;
        this.maxSize = maxSize;
    }

    public Lobby() {
        this.id = Lobby.getNewID();
        this.availability = lobbyAvailability.ANYONE;
        this.maxSize = 10;
    }

    public String getId() {
        return id;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public lobbyAvailability getAvailability() {
        return availability;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setAvailability(lobbyAvailability availability) {
        this.availability = availability;
        writeToDB();
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
        writeToDB();
    }

    // adds a new user to the lobby
    public void addUser(String userID) {
        if (users.size() == maxSize) {
            // TODO: handle LobbyFullException
            return;
        }
        users.add(userID);
        writeToDB();
    }

    // writes this to the DB
    public void writeToDB() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Lobby")
                .child(this.getId());
        ref.setValue(this);
    }

    private static String getNewID() {
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
}

class LobbySports extends Lobby {

    protected int minAge;
    protected int maxAge;
    protected int maxDistance;
    protected sports sport;
    protected skillGroupSports skill;

    LobbySports(String name, lobbyAvailability availability, int maxSize, FilterSports filter) {
        super(name, availability, maxSize);
        this.setFilter(filter);
    }

    LobbySports() {
        super();
        this.maxAge = -1;
        this.minAge = -1;
        this.maxDistance = -1;
        this.sport = sports.ANY;
        this.skill = skillGroupSports.ALL;
    }

    // writes this to the database
    @Override
    public void writeToDB() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("SportsLobby").
                child(this.getId());
        ref.setValue(this);
    }

    public int getMaxAge() {
        return maxAge;
    }

    public int getMinAge() {
        return minAge;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public sports getSport() {
        return sport;
    }

    public skillGroupSports getSkill() {
        return skill;
    }

    public void setMinAge(int minAge){
        this.minAge = minAge;
        writeToDB();
    }

    public void setMaxAge(int maxAge){
        this.maxAge = maxAge;
        writeToDB();
    }

    public void setAge(int minAge, int maxAge){
        this.minAge = minAge;
        this.maxAge = maxAge;
        writeToDB();
    }

    public void setMaxDistance (int maxDistance){
        this.maxDistance = maxDistance;
        writeToDB();
    }

    public void setSport (sports sport){
        this.sport = sport;
        writeToDB();
    }

    public void setSkill (skillGroupSports skill){
        this.skill = skill;
        writeToDB();
    }

    public void setFilter(FilterSports filter){
        this.maxAge = filter.getMaxAge();
        this.minAge = filter.getMinAge();
        this.maxDistance = filter.getMaxDistance();
        this.sport = filter.getSport();
        this.skill = filter.getSkill();
        writeToDB();
    }

    private static DatabaseReference filterByMinAge(DatabaseReference ref, FilterSports filter) {
        Query query = ref.orderByChild("minAge").equalTo(filter.getMinAge());
        return query.getRef();
    }

    private static DatabaseReference filterByMaxAge(DatabaseReference ref, FilterSports filter) {
        Query query = ref.orderByChild("maxAge").equalTo(filter.getMaxAge());
        return query.getRef();
    }

    private static DatabaseReference filterByDistance(DatabaseReference ref, FilterSports filter) {
        Query query = ref.orderByChild("maxDistance").equalTo(filter.getMaxDistance());
        return query.getRef();
    }

    private static DatabaseReference filterBySkill(DatabaseReference ref, FilterSports filter) {
        Query query = ref.orderByChild("skill").equalTo(filter.getSkill().toString());
        return query.getRef();
    }

    private static DatabaseReference filterBySports(DatabaseReference ref, FilterSports filter) {
        Query query = ref.orderByChild("sport").equalTo(filter.getSport().toString());
        return query.getRef();
    }

    public static void readLobbysByFilters(FilterSports filter) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("SportsLobby");
        ref = LobbySports.filterByMinAge(ref, filter);
        ref = LobbySports.filterByMaxAge(ref, filter);
        ref = LobbySports.filterByDistance(ref, filter);
        ref = LobbySports.filterBySkill(ref, filter);
        ref = LobbySports.filterBySports(ref, filter);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FilterSports.setLastReadLobbys(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO: HANDLE ERROR
            }
        });
    }
}

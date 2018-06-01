package com.orez.teamup.teamup;

import android.provider.ContactsContract;
import android.util.Log;
import java.util.Random;
import java.util.ArrayList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


enum lobbyAvailability{
    ANYONE, FRIENDS, INVITES
}

public class Lobby {
    protected String id;
    protected ArrayList<String> users = new ArrayList<>();
    protected lobbyAvailability availability;
    protected int maxSize;

    public Lobby(String name, lobbyAvailability availability, int maxSize){
        this.id = Lobby.getNewID();
        this.availability = availability;
        this.maxSize = maxSize;
    }

    public Lobby(){
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
    public void addUser(String userID){
        if (users.size() == maxSize) {
            // TODO: handle LobbyFullException
            return;
        }
        users.add(userID);
        writeToDB();
    }

    // writes this to the DB
    public void writeToDB(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Lobby")
                .child(this.getId());
        ref.setValue(this);
    }

    private static String getNewID(){
        // generate random lobby key
        String key = "";
        for (int i = 0; i < 20; ++i){
            Random r = new Random();
            char rand = (char)(r.nextInt(74) + 48);
            while (rand == 91 || rand == 93){
                rand = (char)(r.nextInt(74) + 48);
            }
            key += Character.toString(rand);
        }

        return key;
    }
}

class LobbySports extends Lobby{

    protected FilterSports sportFilter;

    LobbySports(String name, lobbyAvailability availability, int maxSize){
        super(name, availability, maxSize);
    }

    LobbySports(){
        super();
    }

    // writes this to the database
    @Override
    public void writeToDB() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("SportsLobby").
                child(this.getId());
        ref.setValue(this);
    }

    public void setFilter(FilterSports filter) {
        this.sportFilter = filter;
        writeToDB();
    }

    private static DatabaseReference filterByMinAge(DatabaseReference ref, FilterSports filter){
        Query query = ref.child("sportFilter").orderByChild("minAge").equalTo(filter.getMinAge());
        return query.getRef();
    }

    private static DatabaseReference filterByMaxAge(DatabaseReference ref, FilterSports filter){
        Query query = ref.child("sportFilter").orderByChild("maxAge").equalTo(filter.getMaxAge());
        return query.getRef();
    }

    private static DatabaseReference filterByDistance(DatabaseReference ref, FilterSports filter){
        Query query = ref.child("sportFilter").orderByChild("maxDistance").equalTo(filter.getMaxDistance());
        return query.getRef();
    }

    private static DatabaseReference filterBySkill(DatabaseReference ref, FilterSports filter){
        Query query = ref.child("sportFilter").orderByChild("skill").equalTo(filter.getSkill().toString());
        return query.getRef();
    }

    private static DatabaseReference filterBySports(DatabaseReference ref, FilterSports filter){
        Query query = ref.child("sportFilter").orderByChild("minAge").equalTo(filter.getSport().toString());
        return query.getRef();
    }

    /*public static ArrayList<LobbySports> getLobbysByFilter(FilterSports filter){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("SportsLobby");
        ref = LobbySports.filterByMinAge(ref, filter);
        ref = LobbySports.filterByMaxAge(ref, filter);
        ref = LobbySports.filterByDistance(ref, filter);
        ref = LobbySports.filterBySkill(ref, filter);
        ref = LobbySports.filterBySports(ref, filter);

        ArrayList<LobbySports> results = new ArrayList<>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/
}

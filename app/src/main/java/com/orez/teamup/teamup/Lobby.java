package com.orez.teamup.teamup;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
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

    public Lobby(lobbyAvailability availability, int maxSize) {
        this.id = Lobby.getNewID();
        this.availability = availability;
        this.maxSize = maxSize;
    }

    public Lobby() {
        this.id = Lobby.getNewID();
        this.availability = lobbyAvailability.ANYONE;
        this.maxSize = -1;
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

    // removes a user from the lobby
    public void removeUser(String userID){
        users.remove(userID);
        writeToDB();
    }

    // writes this to the DB
    public void writeToDB() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Lobby")
                .child(this.getId());
        if (this.users.size() == 0)
            ref.removeValue();
        else
            ref.setValue(this);
    }

    private static String getNewID() {
        // generate random lobby key
        Log.d("LOLOL", "apelat");
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
    protected sports sport;
    protected skillGroupSports skill;

    LobbySports(lobbyAvailability availability, int maxSize, int minAge, int maxAge,
                sports sport, skillGroupSports skill) {
        super(availability, maxSize);
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.sport = sport;
        this.skill = skill;
        this.writeToDB();
    }

    LobbySports() {
        super();
        this.maxAge = -1;
        this.minAge = -1;
        this.sport = sports.ANY;
        this.skill = skillGroupSports.ALL;
        this.writeToDB();
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

    public void setSport (sports sport){
        this.sport = sport;
        writeToDB();
    }

    public void setSkill (skillGroupSports skill){
        this.skill = skill;
        writeToDB();
    }

    private static ArrayList<LobbySports> filter(DataSnapshot dataSnapshot, FilterSports filter){

        ArrayList<LobbySports> arr = new ArrayList<>();

        // convert snapshot to arraylist
        for (DataSnapshot ds: dataSnapshot.getChildren()){
            LobbySports curr = ds.getValue(LobbySports.class);

            // age filter
            if (curr.getMaxAge() < filter.getAge() || curr.getMinAge() > filter.getAge())
                continue;

            // sport filter
            if (curr.getSport() != filter.getSport())
                continue;

            // skill filter
            if (curr.getSkill() != filter.getSkill())
                continue;

            // TODO: distance filter

            arr.add(curr);

        }

        return arr;
    }

    /*
    * primeste o lista de filtre (un filtru pt fiecare sport)
    * pentru fiecare filtru appendeaza la arr o lista de lobbyuri gasita pt filtrul respectiv
    */
    public static void readLobbysByFilters(final ArrayList<FilterSports> filters) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("SportsLobby");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<LobbySports> arr = new ArrayList<>();
                for (FilterSports f: filters){
                    arr.addAll(LobbySports.filter(dataSnapshot, f));
                }

                // @CIPRIAN: ADAUGA AICI CALL LA FUNCTIA CARE AFISEAZA LOBBYURILE
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO: HANDLE ERROR
            }
        });
    }
}

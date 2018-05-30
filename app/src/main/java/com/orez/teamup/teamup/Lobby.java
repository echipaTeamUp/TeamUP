package com.orez.teamup.teamup;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

enum lobbyAvailability{
    ANYONE, FRIENDS, INVITES
}

public class Lobby {
    protected int id;
    protected ArrayList<String> users = new ArrayList<>();
    protected String name;
    protected lobbyAvailability availability;
    protected int maxSize;

    // used in getNewID function, redundant for the rest of the class
    protected static int _id;

    public Lobby(String name, lobbyAvailability availability, int maxSize){
        this.id = Lobby.getNewID();
        this.name = name;
        this.availability = availability;
        this.maxSize = maxSize;
    }

    public Lobby(){
        this.id = Lobby.getNewID();
        this.name = "lobby".concat(Integer.toString(id));
        this.availability = lobbyAvailability.ANYONE;
        this.maxSize = 10;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public void setName(String name) {
        this.name = name;
        writeToDB();
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
                .child(Integer.toString(this.getId()));
        ref.setValue(this);


        DatabaseReference usersRef = ref.child("users");
        if (users.size() == 0)
            usersRef.setValue("EMPTY LOBBY");
        // TODO: delete lobby on empty
        for (String user : users){
            usersRef.child(users.indexOf(user) + "").setValue(user);
        }
    }

    // gets a new ID for the lobby from the server
    private static int getNewID(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("LobbyID");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                _id = dataSnapshot.getValue(int.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // when getting a new id it increments by 1 for the next id to be unique
        ref.setValue(_id + 1);
        return _id;
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
                child(Integer.toString(this.getId()));
        ref.setValue(this);

        DatabaseReference usersRef = ref.child("users");
        if (users.size() == 0)
            usersRef.setValue("EMPTY LOBBY");
        for (String user : users) {
            usersRef.child(users.indexOf(user) + "").setValue(user);
        }
    }

    public void setFilter(FilterSports filter) {
        this.sportFilter = filter;
        writeToDB();
    }
}

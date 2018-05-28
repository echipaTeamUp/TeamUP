package com.orez.teamup.teamup;

import android.provider.ContactsContract;

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
    private int id;
    private ArrayList<String> users;
    private String name;
    private lobbyAvailability availability;
    private int maxSize;

    public Lobby(String name, lobbyAvailability availability, int maxSize){
        this.id = Lobby.getNewID();
        this.name = name;
        this.availability = availability;
        this.maxSize = maxSize;
        this.users = new ArrayList<>();
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
    }

    public void setAvailability(lobbyAvailability availability) {
        this.availability = availability;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public void addUser(String userID){
        users.add(userID);
    }

    public void writeToDB(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Lobby");
        ref.setValue(this);
    }

    private static int getNewID(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("LobbyID");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
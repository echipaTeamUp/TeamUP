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

    public Lobby(int id, String name, lobbyAvailability availability, int maxSize){
        this.id = id;
        this.name = name;
        this.availability = availability;
        this.maxSize = maxSize;
        this.users = new ArrayList<>();
    }

    public Lobby(int id){
        this.id = id;
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
        ref.child("id").setValue(this.id);
        ref.child("name").setValue(this.name);
        ref.child("availability").setValue(this.availability.toString());
        ref.child("maxSize").setValue(Integer.toString(this.maxSize));

        String users = "";
        for (String i : this.users){
            if (users.length() > 0)
                users.concat(",");
            users.concat(i);
        }
        ref.child("users").setValue(users);
    }

}
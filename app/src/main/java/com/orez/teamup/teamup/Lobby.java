package com.orez.teamup.teamup;

import android.provider.ContactsContract;
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
    protected ArrayList<String> users;
    protected String name;
    protected lobbyAvailability availability;
    protected int maxSize;

    protected static int _id;

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

    public void addUser(String userID){
        users.add(userID);
        writeToDB();
    }

    public void writeToDB(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Lobby")
                .child(Integer.toString(this.getId()));
        ref.setValue(this);

        DatabaseReference usersRef = ref.child("users");
        for (String user : users){
            usersRef.child(users.indexOf(user) + "").setValue(user);
        }
    }

    private static int getNewID(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("LobbyID");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                _id = dataSnapshot.getValue(int.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ref.setValue(_id + 1);
        return _id;
    }
}

class LobbySports extends Lobby{

    protected ArrayList<FilterSports> filterList;

    LobbySports(String name, lobbyAvailability availability, int maxSize){
        super(name, availability, maxSize);
    }

    LobbySports(){
        super();
    }

    @Override
    public void writeToDB(){
        Toast.makeText(ResultsActivity.class, "didiidid", Toast.LENGTH_LONG).show();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("SportsLobby").
                child(Integer.toString(this.getId()));
        ref.setValue(this);

        DatabaseReference usersRef = ref.child("users");
        for (String user : users){
            usersRef.child(users.indexOf(user) + "").setValue(user);
        }
    }
}

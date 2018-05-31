package com.orez.teamup.teamup;

import android.content.Context;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
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
    protected int id;
    protected ArrayList<String> users = new ArrayList<>();
    protected String name;
    protected lobbyAvailability availability;
    protected int maxSize;

    // used in getNewID function, redundant for the rest of the class
    public static int _id;

    public Lobby(String name, lobbyAvailability availability, int maxSize){
        Lobby.getNewID();
        this.id = _id;
        this.name = name;
        this.availability = availability;
        this.maxSize = maxSize;
        this.writeToDB();
    }

    public Lobby(){
        Lobby.getNewID();
        this.id = _id;
        this.name = "lobby".concat(Integer.toString(id));
        this.availability = lobbyAvailability.ANYONE;
        this.maxSize = 10;
        this.writeToDB();
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

    private static void setDBID(int newID){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("LobbyID");
        ref.setValue(newID);
        Lobby._id = newID;
    }

    // gets a new ID for the lobby from the server
    private static void getNewID(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("LobbyID");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                _id = dataSnapshot.getValue(int.class);
                Log.d("AICIICICIICICICICICI ",_id+"");
                Lobby.setDBID(_id + 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

    public static ArrayList<LobbySports> getLobbysByFilter(FilterSports filter){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("SportsLobby");
        ArrayList <LobbySports> results = new ArrayList<>();

        Query query = ref.orderByChild("availability").equalTo("ANYONE");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot xd : dataSnapshot.getChildren()){
                        try{
                            String ls = xd.child("name").getValue(String.class);
                            Log.d("OBJECT", ls);
                        } catch (Exception e){
                            Log.d("ERROR THROWN BY DB", e.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return results;
    }
}

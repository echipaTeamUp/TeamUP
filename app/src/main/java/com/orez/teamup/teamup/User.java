package com.orez.teamup.teamup;

public class User {
    private int ID;
    private String name;
    private String email;
    private String password;

    public User(){
        this.ID = 0;
        this.name = "";
        this.email = "";
        this.password = "";
    }

    public User(User user){
        this.ID = user.ID;
        this.name = user.name;
        this.email = user.email;
    }

    public int getID(){
        return this.ID;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String _name){
        this.name = _name;
    }

    public String getEmail(){
        return this.email;
    }

    public void setEmail(String _email){
        this.email = _email;
    }
}

package com.orez.teamup.teamup;

public class User {
    public int ID;
    private String name;
    private String email;

    public User(){
        this.ID = 0;
        this.name = "";
        this.email = "";
    }

    public User(User user){
        this.ID = user.ID;
        this.name = user.name;
        this.email = user.email;
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

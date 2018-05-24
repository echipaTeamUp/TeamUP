package com.orez.teamup.teamup;

import java.util.ArrayList;
import java.util.List;

public class CSVDataBase {
    private String path;
    private List<User> db = new ArrayList<>();

    public CSVDataBase(String _path){
        this.path = path;
    }

    public void addUser(User _user){
        db.add(_user);
    }
}

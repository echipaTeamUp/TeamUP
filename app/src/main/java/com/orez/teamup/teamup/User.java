package com.orez.teamup.teamup;

import android.net.Uri;

import java.io.Serializable;

public class User implements Serializable {
    private String First_name = "nu a mers:(";
    private String Last_name="null";
    private String Birthday="null";
    private Uri Photo;

    public String getFirst_name() {
        return First_name;
    }

    public String getLast_name() {
        return Last_name;
    }

    public String getBirthday() {
        return Birthday;
    }

    public Uri getPhoto() {
        return Photo;
    }

    public void setFirst_name(String first_name) {
        First_name = first_name;
    }

    public void setLast_name(String last_name) {
        Last_name = last_name;
    }

    public void setBirthday(String birthday) {
        Birthday = birthday;
    }

    public void setPhoto(Uri photo) {
        Photo = photo;
    }

    public User() {

    }

    public User(String First_name, String Last_name, String Birthday,Uri Photo) {
        this.First_name = First_name;
        this.Last_name = Last_name;
        this.Birthday = Birthday;
        this.Photo=Photo;
    }
}

package com.orez.teamup.teamup;

import java.io.Serializable;
import java.util.Calendar;

public class User implements Serializable {
    private String First_name = "nu a mers:(";
    private String Last_name = "null";
    private String Birthday = "null";

    public String getFirst_name() {
        return First_name;
    }

    public String getLast_name() {
        return Last_name;
    }

    public String getBirthday() {
        return Birthday;
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

    public User(){}

    public User(String First_name, String Last_name, String Birthday) {
        this.First_name = First_name;
        this.Last_name = Last_name;
        this.Birthday = Birthday;
    }
    public int getAge(){
        String[] items=this.Birthday.split("-");
        int day=Integer.parseInt(items[0]);
        int month=Integer.parseInt(items[1]);
        int year=Integer.parseInt(items[2]);
        Calendar user_birthday = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        user_birthday.set(year,month-1,day);
        int age = today.get(Calendar.YEAR) - user_birthday.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < user_birthday.get(Calendar.DAY_OF_YEAR))
            age--;
        return age;
    }

}

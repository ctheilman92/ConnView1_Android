package com.example.milkymac.connview_main.models;

/**
 * Created by milkymac on 3/20/17.
 */

public class User {

    /* TODO: salt password */

    private int UID;
    private String Name;
    private String Email;
    private String Password;
//    private Date lastActive;

    public User() {

    }

    public User(int id, String name, String email, String password) {

    }

    public User(String name, String email, String password) {

    }

    public int getUID() { return UID; }
    public void setUID(int i) { this.UID = i; }
    public String getName() { return Name; }
    public void setName(String n) { this.Name = n; }
    public String getEmail() { return Email; }
    public void setEmail(String n) { this.Email = n; }
    public String getPassword() { return Name; }
    public void setPassword(String n) { this.Password = n; }

}

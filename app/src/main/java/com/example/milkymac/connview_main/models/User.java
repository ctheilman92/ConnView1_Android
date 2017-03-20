package com.example.milkymac.connview_main.models;

/**
 * Created by milkymac on 3/20/17.
 */

public class User {

    private int UID;
    private String Name;
    private String Email;
    private String Password;

    public User() {

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

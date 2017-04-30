package com.example.milkymac.connview_main.models;

import java.util.Date;

/**
 * Created by milkymac on 4/25/17.
 */

public class MyNet extends Network{

    //extending Network class to map a user to how many networks it has viewed recently.
    public String UserName;
    public int TimesConnected;


    public MyNet(String un, String ssid, String bssid, int signal, int frequency, String netip, String broadcast, short netmask) {
        UserName = un;
        Frequency = frequency;
        isUP = true;
        BSSID = bssid;
        SSID = ssid;
        NetIP = netip;
        NetMask = netmask;
        Broadcast = broadcast;
        Signal = signal;
    }

    public MyNet(String un) {
        UserName = un;
    }

    public MyNet() {

    }

    //counter only used in database for history updates
    public int getTimesConnected() { return TimesConnected; }
    public void setTimesConnected(int c) { TimesConnected = c; }
    public String getUserName() { return UserName; }
    public void setUserName(String un) { UserName = un; }

}

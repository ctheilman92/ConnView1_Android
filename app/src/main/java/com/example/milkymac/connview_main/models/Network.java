package com.example.milkymac.connview_main.models;



import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by milkymac on 4/8/17.
 */

public class Network {
    private static final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    String SSID;
    String BSSID;
    int Signal;
    String NetIP;
    short NetMask;
    String Broadcast;
    int LinkSpeed;
    int Frequency;
    boolean isUP;
    Date lastConnected;


    public Network() {

    }


    //this used for myNet to copy lastConnected date from object to object (persistence)
    public Network(Date date, Boolean isup, String ssid, String bssid, int signal, int frequency, String netip, String broadcast, short netmask) {
        Frequency = frequency;
        BSSID = bssid;
        SSID = ssid;
        NetIP = netip;
        NetMask = netmask;
        Broadcast = broadcast;
        Signal = signal;

        //excluded from params
        isUP = isup;
        lastConnected = date;
    }


    public Network(String ssid, String bssid, int signal, int frequency, String netip, String broadcast, short netmask) {
        Frequency = frequency;
        BSSID = bssid;
        SSID = ssid;
        NetIP = netip;
        NetMask = netmask;
        Broadcast = broadcast;
        Signal = signal;

        //excluded from params
        isUP = true;
        lastConnected = new Date();
    }

    public Network(boolean isup) {
        if (!isup) {
            isUP = false;
            SSID = "----";
            NetIP = "----";
            NetMask = 0;
            Broadcast = "----";
            Signal = 0;
            BSSID = "----";
            LinkSpeed = 0;
        }
        else isUP = true;
    }

    public void setLastConnected(Date date) { lastConnected = date; }
    public void setFrequency(int f) { Frequency = f; }
    public void setLinkSpeed(int speed) { LinkSpeed = speed; }
    public void setBSSID(String bssid) { BSSID = bssid; }
    public void setSSID(String ssid) { SSID = ssid; }
    public void setSignal(int signal) { Signal = signal; }
    public void setNetIP(String netip) { NetIP = netip; }
    public void setNetMask(short netmask) { NetMask = netmask; }
    public void setBroadcast(String broadcast) { Broadcast = broadcast; }
    public void setState(boolean isup) { isUP = isup; }


    public Date getLastConnectedDate() { return lastConnected; }
    public String getLastConnected() { return sdf.format(lastConnected); }
    public int getLinkSpeed() { return LinkSpeed; }
    public String getBSSID() { return BSSID; }
    public String getSSID() { return SSID; }
    public int getSignal() { return Signal; }
    public String getNetIP() { return NetIP; }
    public short getNetMask() { return NetMask; }
    public String getBroadcast() { return Broadcast; }
    public boolean getState() { return isUP; }
    public int getFrequency() { return Frequency; }
}

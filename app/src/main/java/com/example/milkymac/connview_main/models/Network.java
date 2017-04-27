package com.example.milkymac.connview_main.models;

/**
 * Created by milkymac on 4/8/17.
 */

public class Network {

    String SSID;
    String BSSID;
    int Signal;
    String NetIP;
    short NetMask;
    String Broadcast;
    int LinkSpeed;
    int Frequency;
    boolean isUP;


    public Network() {

    }

    public Network(String ssid, String bssid, int signal, int frequency, String netip, String broadcast, short netmask) {
        this.Frequency = frequency;
        this.isUP = true;
        this.BSSID = bssid;
        this.SSID = ssid;
        this.NetIP = netip;
        this.NetMask = netmask;
        this.Broadcast = broadcast;
        this.Signal = signal;
    }

    public Network(boolean isup) {
        if (!isup) {
            this.isUP = false;
            this.SSID = "----";
            this.NetIP = "----";
            this.NetMask = 0;
            this.Broadcast = "----";
            this.Signal = 0;
            this.BSSID = "----";
            this.LinkSpeed = 0;
        }
        else this.isUP = true;
    }

    public void setFrequency(int f) { this.Frequency = f; }
    public void setLinkSpeed(int speed) { this.LinkSpeed = speed; }
    public void setBSSID(String bssid) { this.BSSID = bssid; }
    public void setSSID(String ssid) { this.SSID = ssid; }
    public void setSignal(int signal) { this.Signal = signal; }
    public void setNetIP(String netip) { this.NetIP = netip; }
    public void setNetMask(short netmask) { this.NetMask = netmask; }
    public void setBroadcast(String broadcast) { this.Broadcast = broadcast; }
    public void setState(boolean isup) { this.isUP = isup; }

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

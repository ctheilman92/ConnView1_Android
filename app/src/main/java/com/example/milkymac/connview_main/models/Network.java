package com.example.milkymac.connview_main.models;

/**
 * Created by milkymac on 4/8/17.
 */

public class Network {

    private String SSID;
    private String BSSID;
    private int Signal;
    private String NetIP;
    private short NetMask;
    private String Broadcast;
    private int LinkSpeed;
    private int Frequency;
    private boolean isUP;


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
            this.SSID = null;
            this.NetIP = null;
            this.NetMask = 0;
            this.Broadcast = null;
            this.Signal = 0;
            this.BSSID = null;
            this.LinkSpeed = 0;
        }
        else this.isUP = true;
    }

    public void getLinkSpeed(int speed) { this.LinkSpeed = speed; }
    public void setBSSID(String bssid) { this.BSSID = bssid; }
    public void setSSID(String ssid) { this.SSID = ssid; }
    public void setSignal(int signal) { this.Signal = signal; }
    public void setNetIP(String netip) { this.NetIP = netip; }
    public void setNetMask(short netmask) { this.NetMask = netmask; }
    public void setBroadcast(String broadcast) { this.Broadcast = broadcast; }

    public int getLinkSpeed() { return LinkSpeed; }
    public String getBSSID() { return BSSID; }
    public String getSSID() { return SSID; }
    public int getSignal() { return Signal; }
    public String getNetIP() { return NetIP; }
    public short getNetMask() { return NetMask; }
    public String getBroadcast() { return Broadcast; }
}

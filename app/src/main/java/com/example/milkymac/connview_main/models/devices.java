package com.example.milkymac.connview_main.models;
import java.io.Serializable;
import java.net.InterfaceAddress;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by milkymac on 3/7/17.
 */

/*

**** PARCELER REQUIRES ALL PUBLIC CLASSES.....EH.
 */

@Parcel
public class devices extends AsyncTask implements Serializable{

    public String devName;
    public String SSID;
    public String Type;
    public String ip;
    public String ipv6;
    public String mac;
    public boolean isUp;   //connected to WiFi


    public devices() {

    }

    public devices(String devname, boolean isV4, String ip, String mac, boolean isup, String type, String SSID) {
        this.devName = devname;
        this.mac = mac;
        this.isUp = isup;
        this.Type = type;
        this.SSID = SSID;

        if (isV4) { this.ip = ip; }
        else { this.ipv6 = ip; }
    }


    //region SETS
    public void setState(boolean status) { isUp = status; }
    public void setDevName(String name) { devName = name; }
    public void setIp(String getip) { ip = getip; }
    public void setIpv6(String getip) { ipv6 = getip; }
    public void setMac(String getmac) { mac = getmac; }
    public void setSSID(String ssid) { SSID = (!isUp) ? null : ssid; }
    //endregion

    //region GETS
    public String getDevName() { return devName; }
    public String getIp() { return ip; }
    public String getIpv6() { return ipv6; }
    public String getMac() { return mac; }
    public String getSSID() { return SSID; }
    public boolean getState() { return isUp; }

    @Override
    protected Object doInBackground(Object[] params) {
        return null;
    }

}

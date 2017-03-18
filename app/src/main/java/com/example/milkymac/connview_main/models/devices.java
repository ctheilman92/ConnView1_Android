package com.example.milkymac.connview_main.models;
import java.net.InterfaceAddress;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import android.net.wifi.WifiManager;
import java.util.List;

/**
 * Created by milkymac on 3/7/17.
 */

public class devices {

    private String devName;
    private String SSID;
    private String Type;
    private String ip;
    private String ipv6;
    private String mac;
    private boolean isUp;   //connected to WiFi


    public devices() {

    }


    //region SETS
    public void setStatus(boolean status) { isUp = status; }
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
    public boolean getStatus() { return isUp; }

}

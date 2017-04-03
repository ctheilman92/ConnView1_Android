package com.example.milkymac.connview_main.models;
import android.content.Context;
import android.net.Network;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;


/**
 * Created by milkymac on 3/17/17.
 */




/*
*   NOTE:
*   --> FOR 3G Net  ::> INTERFACE == rmnet0
*   --> FOR WIFI    ::> INTERFACE == wlan0
*
*/

@org.parceler.Parcel
public class MyDevice extends devices {

    public transient ArrayList<NetworkInterface> interfacesList;
    public transient ArrayList<String> interfacesDisplayNameList;
    public String WIFI_INTERFACE;
    public String CELLULAR_INTERFACE;


    public transient Context myContext;

    //needed for parceler
    public MyDevice() {}

    public MyDevice(Context myContext) {
       this.myContext = myContext;

        //myDevice members
        initMembers();
    }

    public void initMembers() {
        interfacesList = new ArrayList<>();
        interfacesDisplayNameList = new ArrayList<>();
    }

    public void setListNetworkInterfaces() {
        try {
            for(Enumeration<NetworkInterface> list = NetworkInterface.getNetworkInterfaces(); list.hasMoreElements();) {
                NetworkInterface i = list.nextElement();
                if (i != null) {
                    interfacesList.add(i);
                    Log.d("NETWORK INTERFACE-->", i.getDisplayName());

                    WIFI_INTERFACE = (WIFI_INTERFACE == null && !i.isLoopback() && (i.getName().equals("wlan0"))) ? i.getDisplayName() : null;
                    CELLULAR_INTERFACE = (CELLULAR_INTERFACE == null && !i.isLoopback() && (i.getName().equals("rmnet0"))) ? i.getDisplayName() : null;
                }
            }
        }
        catch(Exception ex) {
            Log.d("GETINTERFACES_EXCEPTION", ex.toString());
        }
    }

    public void getSSIDName() {
        WifiManager manager = (WifiManager) myContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = manager.getConnectionInfo();
        Log.d("SSID_STATE", wInfo.getSSID().toString());
        Log.d("SUPPLICANT_STATE", wInfo.getSupplicantState().toString());

        String ssid = (!wInfo.getHiddenSSID()) ? wInfo.getSSID().toString() : "<hidden ssid>";
        String status = wInfo.getSupplicantState().toString();

        setState((status.equals("COMPLETED")));
        setSSID(ssid);
    }

    public void setInterfacesByDisplayName() {
        for (NetworkInterface i : interfacesList) { interfacesDisplayNameList.add(i.getDisplayName()); }
    }

    public void getLocalAddresses(String interfaceName) {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();

                if (!intf.isLoopback() && intf.isUp() && (intf.getName().equals(interfaceName))) {
                    Log.d("INTERFACE_WLAN0_ADDR", intf.getHardwareAddress().toString());


                    //region MAC ASSIGN
                    byte[] macBytes = intf.getHardwareAddress();
                    if (macBytes == null) {
                        setMac("UNDEFINED");
                    }

                    StringBuilder res1 = new StringBuilder();
                    for (byte b : macBytes) {
                        res1.append(Integer.toHexString(b & 0xFF) + ":");
                    }

                    if (res1.length() > 0) {
                        res1.deleteCharAt(res1.length() - 1);
                    }

                    Log.d("FORMATED_MAC_WLAN0", res1.toString());
                    setMac(res1.toString());
                    //endregion



                    //region IPV6 && IPV4 ASSIGN
                    for (Enumeration<InetAddress> ipAddr = intf.getInetAddresses(); ipAddr.hasMoreElements();) {
                        InetAddress inetaddr = ipAddr.nextElement();

                        if (!inetaddr.isLoopbackAddress()) {
                            if (inetaddr instanceof Inet4Address) {
                                Log.d("WLAN0____IP_ADDR", inetaddr.getHostName().toString());
                                setIp(inetaddr.getHostAddress());
                            }
                            else {
                                Log.d("WLAN0____IP_ADDR", inetaddr.getHostName().toString());
                                setIpv6(inetaddr.getHostAddress());
                            }
                        }
                    }
                    //endregion
                }
            }
        } catch (SocketException ex) {
            Log.e("SRM", ex.toString());
        }
    }

}

package com.example.milkymac.connview_main.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import com.example.milkymac.connview_main.models.Devices;
import com.example.milkymac.connview_main.models.MyDevice;
import com.example.milkymac.connview_main.models.Network;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by milkymac on 4/8/17.
 *
 * /**
 * Try to extract a hardware MAC address from a given IP address using the
 * ARP cache (/proc/net/arp).<br>
 * <br>
 * We assume that the file has this structure:<br>
 * <br>
 * IP address       HW type     Flags       HW address            Mask     Device
 * 192.168.18.11    0x1         0x2         00:04:20:06:55:1a     *        eth0
 * 192.168.18.36    0x1         0x2         00:22:43:ab:2a:5b     *        eth0
 *
 * @param "ip"
 * @return the MAC from the ARP cache
 *
 **/

public class NetHelper {

    private ConnectivityManager cm;
    private WifiManager manager;
    private WifiInfo connectionInfo;
    private NetworkInfo activeNetwork;
    private transient Context myContext;

    private static final String TAG = "SNIFF_NET";
    private static String NET_IP;
    private static short NET_PREFIX;
    private static String MYNET_IP;
    private static String IP_LASTOF_PREFIX;
    

    private List<Devices> listDevices;
    private Network myNet;



    public NetHelper() {
        listDevices = new ArrayList<Devices>();
    }


    public NetHelper(Context context) {
        listDevices = new ArrayList<Devices>();
        this.myContext = context;

        cm = (ConnectivityManager) myContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
        manager = (WifiManager) myContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        connectionInfo = manager.getConnectionInfo();
        MYNET_IP = Formatter.formatIpAddress(connectionInfo.getIpAddress());
        IP_LASTOF_PREFIX = MYNET_IP.substring(0, MYNET_IP.lastIndexOf(".") + 1);
        //TODO: CORRECTLY JUSTIFY THIS (ONLY SUPPORTS /24 NETWORKS)
        NET_IP = IP_LASTOF_PREFIX + "0";
    }


    public String getSSID() {
        return (!connectionInfo.getHiddenSSID()) ? connectionInfo.getSSID() : "<hidden ssid>";
    }

    //this sets the subnet variable along with returning broadcast ip...hm
    public InetAddress getSubnetBroadcast() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            if (networkInterface.isLoopback())
                continue;    // Don't want to broadcast to the loopback interface
            for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                InetAddress broadcast = interfaceAddress.getBroadcast();
                if (broadcast == null)
                    continue;
                // Use the address
                else {
                    NET_PREFIX = interfaceAddress.getNetworkPrefixLength();
                    return broadcast;
                }
            }
        }
        return null;
    }

    /* build network obj with nethelper.getNetInfo(); */
    public Network getNetInfo() throws SocketException {
        boolean connected = (connectionInfo.getSupplicantState().equals("COMPLETED")) ? true : false;

        if (!connected) { return new Network(connected); }
        else {
            return new Network(connectionInfo.getSSID(), connectionInfo.getBSSID(), connectionInfo.getRssi(), connectionInfo.getFrequency(), MYNET_IP, getSubnetBroadcast().toString(), NET_PREFIX); }
    }

    public void netSniff() throws IOException {
        Log.d(TAG, "begin sniffing network on network: "+ NET_IP);
        //TODO: find non-deprecated version later. but hey it works.
        Log.d(TAG, "Active Network: " + String.valueOf(activeNetwork));
        Log.d(TAG, "IP_ADDR: " + String.valueOf(MYNET_IP));

        for (int i = 0; i < 255; i++) {
            String testIP = IP_LASTOF_PREFIX + String.valueOf(i);
            InetAddress getAddr = InetAddress.getByName(testIP);
            boolean isReachable = getAddr.isReachable(1000);
            String hostname = getAddr.getHostName();

            if (isReachable && !testIP.equals(MYNET_IP)) {
                Log.d(TAG, "HOST: " + String.valueOf(hostname) + "(" + String.valueOf(testIP) + ") - STATUS: UP");
                String mac = getMacFromArpCache(testIP);
                Log.d(TAG, "MAC_ADDRESS: " + mac);

                boolean isv4 = (getAddr instanceof Inet4Address) ? true : false;
                listDevices.add(new Devices(hostname, isv4, testIP, mac, true, "TYPE", getSSID()));
                Log.d("ADD_2DEVLIST", "adding" + hostname + " to devicesList");
            }
        }
    }

    public List<Devices> getListDevices() {
        return listDevices;
    }


    public String getMacFromArpCache(String ip) throws FileNotFoundException {
        if (ip.isEmpty()) { return null; }

        BufferedReader bfr = null;

        try {
            bfr = new BufferedReader(new FileReader("/proc/net/arp"));
            String res;

            while ((res = bfr.readLine()) != null) {
                String[] splitText = res.split(" +");   //split

                //match ip arg with arp cache line
                if (splitText != null && splitText.length > 4 && ip.equals(splitText[0])) {
                    String mac = splitText[3];
                    if (mac.matches("..:..:..:..:..:..")) { return mac; }
                    else { return null; }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.d("IO_EXCEPTION_THROW", e.toString());
        }
        finally {
            try {
                bfr.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
}

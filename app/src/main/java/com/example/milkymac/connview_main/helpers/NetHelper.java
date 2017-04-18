package com.example.milkymac.connview_main.helpers;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.util.Log;

import com.example.milkymac.connview_main.models.Devices;
import com.example.milkymac.connview_main.models.Network;
import com.google.gson.Gson;

import org.xbill.DNS.Address;
import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Name;
import org.xbill.DNS.PTRRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.ReverseMap;
import org.xbill.DNS.Type;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Created by milkymac on 4/8/17.
 *
 * /**
 * Try to extract a hardware MAC address from a given IP address using the
 * ARP cache (/proc/net/arp).
 *
 * We assume that the file has this structure:<br>
 *
 * IP address       HW type     Flags       HW address            Mask     Device
 * 192.168.18.11    0x1         0x2         00:04:20:06:55:1a     *        eth0
 * 192.168.18.36    0x1         0x2         00:22:43:ab:2a:5b     *        eth0
 *
 * @return the MAC from the ARP cache
 *
 **/

public class NetHelper extends IntentService{

    private ConnectivityManager cm;
    private WifiManager manager;
    private WifiInfo connectionInfo;
    private NetworkInfo activeNetwork;


    public Context myContext;
    public ResultReceiver receiver;
    public Bundle b;

    private static final String TAG = "SNIFF_NET";
    private static final String NET_TAG = "NET_INFO";
    private static String NET_IP;
    private static short NET_PREFIX;
    private static String MYNET_IP;
    private static String IP_LASTOF_PREFIX;
    public static final String BUNDLE_RECEIVER = "receiver";
    public static final String BUNDLE_RECEIVER2 = "netreceiver";


    SharedPreferences netprefs;
    SharedPreferences.Editor editor;


    private String ACTION = "com.example.milkymac.connview_main.helpers.NetHelper";

    private ArrayList<Devices> listDevices;
    private Network myNet;


    //used to name worker thread (DEBUG SPECIFIC)
    public NetHelper() { super("network-helper"); }

    public String getSSID() { return (!connectionInfo.getHiddenSSID()) ? connectionInfo.getSSID() : "<hidden ssid>"; }

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
        Log.d(NET_TAG, "beginning to gather network info");
        boolean connected = (connectionInfo.getSupplicantState().equals("COMPLETED")) ? true : false;
        Log.d(NET_TAG, connectionInfo.getSupplicantState().toString());


        if (!connected) { return new Network(connected); }
        else {
            return new Network(connectionInfo.getSSID(), connectionInfo.getBSSID(), connectionInfo.getRssi(), connectionInfo.getFrequency(), MYNET_IP, getSubnetBroadcast().toString(), NET_PREFIX); }
    }




    public void netSniff() throws IOException {
        Log.d(TAG, "begin sniffing network on network: "+ NET_IP);
        Log.d(TAG, "Active Network: " + String.valueOf(activeNetwork));
        Log.d(TAG, "IP_ADDR: " + String.valueOf(MYNET_IP));


        //if connected to wifi....
        if (!MYNET_IP.equals("0.0.0.0")) {

            for (int i = 0; i < 255; i++) {
                String testIP = IP_LASTOF_PREFIX + String.valueOf(i);
                InetAddress getAddr = InetAddress.getByName(testIP);
                boolean isReachable = getAddr.isReachable(1000);
//                String hostname = getAddr.getCanonicalHostName();
//                String hostname= Address.getHostName(InetAddress.getByName(testIP));

                if (isReachable && !testIP.equals(MYNET_IP)) {
                    String hostname = getHostByIP(testIP);
                    Log.d(TAG, "HOST: " + String.valueOf(hostname) + "(" + String.valueOf(testIP) + ") - STATUS: UP");
                    String mac = getMacFromArpCache(testIP);
                    Log.d(TAG, "MAC_ADDRESS: " + mac);

                    boolean isv4 = (getAddr instanceof Inet4Address) ? true : false;
                    Devices newd = new Devices(hostname, isv4, testIP, mac, true, "DESKTOP", getSSID());
                    listDevices.add(newd);
                    Log.d("ADD_2DEVLIST", "adding" + hostname + " to devicesList");

                    String jsonList = new Gson().toJson(newd);
                    b.putString("DATA_", jsonList);
                    receiver.send(0, b);
                }
            }
        }
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

    //region INTENT-SERVICE IMP. METHODS


    //TODO: figure out WHY THE HELL ReverseMap includes a trailing period at thend end of the arpa zone.
        //should be 5.0.168.192.in-addr.arpa......
    public String getHostByIP(String addr) throws UnknownHostException {
        Name name = ReverseMap.fromAddress(addr);
        Log.d("REVERSE_IP?", name.toString());

        //open DNS
        final String[] openDNS = new String[] {"208.67.222.222", "208.67.220.220"};
        final Resolver resolver = new ExtendedResolver(openDNS);
        final Lookup lookup = new Lookup(name, Type.PTR);

        lookup.setResolver(resolver);
        Record[] records = lookup.run();
        if (records == null) {
            return addr;
        }
        return ((PTRRecord) records[0]).getTarget().toString();

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        //if intent-int == 0: run netsniff
        //if intent-int == 1: run getNetInfo
        int OPR = intent.getIntExtra("OPR", 0); //0 is default value...


        //long running operation is netsniff
        listDevices = new ArrayList<Devices>();
        myContext = getApplicationContext();


        //application based storing of values
        netprefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        editor = netprefs.edit();

        cm = (ConnectivityManager) myContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
        manager = (WifiManager) myContext.getSystemService(Context.WIFI_SERVICE);
        connectionInfo = manager.getConnectionInfo();

        MYNET_IP = Formatter.formatIpAddress(connectionInfo.getIpAddress());
        IP_LASTOF_PREFIX = MYNET_IP.substring(0, MYNET_IP.lastIndexOf(".") + 1);
        //TODO: CORRECTLY JUSTIFY THIS (ONLY SUPPORTS /24 NETWORKS)
        NET_IP = IP_LASTOF_PREFIX + "0";


        //TODO: SEE IF BUNDLE RESULTRECEIVER EVEN WORKS
        if (OPR == 0) {

            //result receiver for callback use
            Bundle params = intent.getExtras();
            receiver = params.getParcelable(NetHelper.BUNDLE_RECEIVER);
            b = new Bundle();

            try {
                netSniff();

            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
            }
        }
        else if (OPR == 1) {

            Bundle params = intent.getExtras();
            receiver = params.getParcelable(NetHelper.BUNDLE_RECEIVER2);
            b = new Bundle();

            try {
                myNet = getNetInfo();
                Gson gson = new Gson();
                String netToJson = gson.toJson(myNet);

                b.putString("DATA_", netToJson);
                receiver.send(0, b);

            }
            catch (SocketException e) {
                e.printStackTrace();
            }
        }

    }

    //endregion
}

package com.example.milkymac.connview_main.helpers;

import android.util.Log;

import com.stealthcopter.networktools.Ping;
import com.stealthcopter.networktools.PortScan;
import com.stealthcopter.networktools.ping.PingResult;
import com.stealthcopter.networktools.ping.PingStats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by milkymac on 3/29/17.
 */

public class Networker {


    public Networker() {

    }

    private String pingErr = null;

    /*
        * using stealthCopter's open Source Library
        * https://github.com/rorist/android-network-discovery
        * attempts to use native ping binary, if fails -> falls back to TCP echo request on port 7
        * TODO: try async methods first,
     */
    public void pinger(String host, int counter) throws UnknownHostException {
        Ping.onAddress(host).setDelayMillis(1000).setTimes(counter).doPing(new Ping.PingListener() {
            @Override
            public void onResult (PingResult pingResult) {

            }

            @Override
            public void onFinished(PingStats pingStats) {

            }
        });
    }

    public void portScan(String host) throws UnknownHostException {
        PortScan.onAddress(host).setTimeOutMillis(1000).setPortsAll().doScan(new PortScan.PortListener() {
            @Override
            public void onResult(int i, boolean b) {

            }

            @Override
            public void onFinished(ArrayList<Integer> arrayList) {

            }
        });
    }

}

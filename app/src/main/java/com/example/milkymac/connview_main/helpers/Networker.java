package com.example.milkymac.connview_main.helpers;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by milkymac on 3/29/17.
 */

public class Networker {


    public Networker() {

    }

    //region PING FUNCTIONS

    private String pingErr = null;


    /*
    RETURNS 0, 1, 2 > std- success, failed, error
    use only 127.0.0.1 for emulators (will fail otherwise)
    */
    public int pinger(String host) throws InterruptedException, IOException {
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec("ping -c 1 " + host);
        proc.waitFor();
        return proc.exitValue();
    }

    public String pingHost(String host) throws IOException, InterruptedException {
        StringBuffer echo = new StringBuffer();
        Runtime runtime = Runtime.getRuntime();

        Process proc = runtime.exec("ping -c 1 " + host);
        proc.waitFor();

        int ret = proc.exitValue();

        //successful ping
        if (ret == 0) {
            InputStreamReader isr = new InputStreamReader(proc.getInputStream());
            BufferedReader buffer = new BufferedReader(isr);
            String in = "";

            while ((in = buffer.readLine()) != null) {
                echo.append(in + '\n');
            }
            String data = getData(echo.toString());
            Log.d("success_ping:::", data);
            return data;
        }
        else if (ret == 1) { //failed ping
            pingErr = "Ping failed, status: 1";
            return null;
        }
        else {
            pingErr = "ping failed, status: 2";
            return null;
        }
    }


    //interpret ping results from command
    public String getData(String s) {
        if (s.contains("0% packet loss")) {
            //TODO: modify later
            int start = s.indexOf("/mdev = ");
            int end = s.indexOf(" ms\n", start);
            s = s.substring(start + 8, end);

            String stats[] = s.split("/");
            return stats[2];
        }
        else if (s.contains("100% packet loss")) {
            pingErr = "100% packet loss";
            return null;
        }
        else if (s.contains("% packet loss")) {
            pingErr = "partial packet loss";
            return null;
        }
        else if (s.contains("unknown host")) {
            pingErr = "unknown host";
            return null;
        }
        else {
            pingErr = "unknown err - data translation";
            return null;
        }
    }

    //endregion
}

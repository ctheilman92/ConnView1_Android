package com.example.milkymac.connview_main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.Object;
import java.lang.Runtime;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InterfaceAddress;


public class PingActivity extends AppCompatActivity {

    //region UI VARS
    TextView tvPinger;
    TextView tvTargetPing;
    TextView tvPacketCount;
    TextView tvPacketSize;
    ImageButton btnPing;
    //endregion

    private String pingErr = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping);

        Toolbar toolbar = (Toolbar) findViewById(R.id.ping_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initVars();

    }


    public void initVars() {
        tvPinger = (TextView) findViewById(R.id.tvPingTitle);
        tvTargetPing = (TextView) findViewById(R.id.tvTargetPing);
        tvPacketCount = (TextView) findViewById(R.id.tvPacketCount);
        tvPacketSize = (TextView) findViewById(R.id.tvPacketSize);
        btnPing = (ImageButton) findViewById(R.id.btnPingTarget);
    }


    /*
    RETURNS 0, 1, 2 > std- success, failed, error
    use only 127.0.0.1 for emulators (will fail otherwise)
    */
    public int pinger(String host) throws IOException, InterruptedException {
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
            return getData(echo.toString());
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


    //region ACTIONBAR
    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //endregion
}

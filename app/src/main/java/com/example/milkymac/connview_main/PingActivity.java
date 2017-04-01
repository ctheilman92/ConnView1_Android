package com.example.milkymac.connview_main;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.stealthcopter.networktools.Ping;
import com.stealthcopter.networktools.ping.PingResult;
import com.stealthcopter.networktools.ping.PingStats;

import java.lang.Object;


public class PingActivity extends AppCompatActivity {

    //region UI VARS
    TextView tvPinger;
    TextView tvPingResult;
    EditText TargetPing;
    EditText PacketCount;
    ImageButton btnPing;
    //endregion


    Networker netHelper;

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
        netHelper = new Networker();
        tvPinger = (TextView) findViewById(R.id.tvPingTitle);
        tvPingResult = (TextView) findViewById(R.id.tvPingResults);
        TargetPing = (EditText) findViewById(R.id.etTarget);
        PacketCount = (EditText) findViewById(R.id.etPingCount);
        btnPing = (ImageButton) findViewById(R.id.btnPingTarget);


        btnPing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new tasker().execute();
            }
        });
    }


    public class tasker extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                pinger();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void appendResults(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvPingResult.append(s + "\n");
            }
        });
    }



    public void pinger() throws Exception {
        String host = TargetPing.getText().toString();
        Log.d("HOST_TAG", host);
        String c = PacketCount.getText().toString();
        int count = Integer.parseInt(c);

        if (TextUtils.isEmpty(host)) {
            appendResults("invalid host!");
        }
        if (count <= 0) {
            appendResults("invalid count specified");
        }

        //synchronous for first time
        PingResult pr = Ping.onAddress(host).setTimeOutMillis(1000).doPing();

        appendResults("pinging "+ pr.getAddress().getHostAddress());
        appendResults("Hostname: "+ pr.getAddress().getHostName());
        appendResults(String.format("%.2f ms", pr.getTimeTaken()));


        //async for running ping
        Ping.onAddress(host).setTimeOutMillis(1000).setTimes(count).doPing(new Ping.PingListener() {
            @Override
            public void onResult(PingResult pingResult) {
                appendResults(String.format("%.2f", pingResult.getTimeTaken()));
            }

            @Override
            public void onFinished(PingStats pingStats) {
                appendResults(String.format("\nPings: %d, Packets lost: %d",
                        pingStats.getNoPings(), pingStats.getPacketsLost()));

                appendResults(String.format("Min/Avg/Max Time Taken: %.2f/%.2f/%.2f ms",
                        pingStats.getMinTimeTaken(),
                        pingStats.getAverageTimeTaken(),
                        pingStats.getMaxTimeTaken()));
            }
        });





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

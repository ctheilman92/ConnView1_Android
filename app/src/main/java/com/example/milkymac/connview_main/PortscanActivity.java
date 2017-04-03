package com.example.milkymac.connview_main;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.stealthcopter.networktools.PortScan;

import java.util.ArrayList;


public class PortscanActivity extends AppCompatActivity {


    //region UI VARS

    private LinearLayout layoutSinglePort;
    private EditText targetIP;
    private EditText targetPort;
    private TextView portScanResults;
    private ImageButton btnScan;
    private Switch scanModeSwitcher;
    //endregion

    private boolean isScanSingleMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portscan);


        Toolbar toolbar = (Toolbar) findViewById(R.id.portscan_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        isScanSingleMode = false;
        initVars();
    }




    public void initVars() {
        targetIP = (EditText) findViewById(R.id.etTarget);
        targetPort = (EditText) findViewById(R.id.etTargetPort);
        portScanResults = (TextView) findViewById(R.id.portScanResults);

        layoutSinglePort = (LinearLayout) findViewById(R.id.SinglePortLayout);
        layoutSinglePort.setVisibility(View.GONE);

        scanModeSwitcher = (Switch) findViewById(R.id.switchPSMode);
        scanModeSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //when checked, mode is single port
                //else multi port
                if (isChecked) {
                    isScanSingleMode = true;
                    layoutSinglePort.setVisibility(View.VISIBLE);
                    portScanResults.setText(R.string.tvPortScanResultsPL2);
                }
                else {
                    isScanSingleMode = false;
                    layoutSinglePort.setVisibility(View.GONE);
                    portScanResults.setText(R.string.tvPortScanResultsPL);
                }
            }
        });

        btnScan = (ImageButton) findViewById(R.id.btnScanPorts);
        btnScan.setOnClickListener(new View.OnClickListener() {
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
                PortScanner();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    public void PortScanner() throws Exception {
        String host = targetIP.getText().toString().trim();

        if (TextUtils.isEmpty(host)) {
            appendResults("\nInvalid Ip Address");
            return;
        }

        appendResults("\n--Scanning IP: "+ host);

        if (isScanSingleMode) {
            int port = Integer.parseInt(targetPort.getText().toString().trim());

            if (port <= 0) {
                appendResults("\nInvalid Port to scan.");
                return;
            }

            ArrayList<Integer> openPorts = PortScan.onAddress(host).setTimeOutMillis(1000).setPort(port).doScan();
            if (openPorts.size() > 0)
                appendResults("PORT " + String.valueOf(port).toString() + ": OPEN");
            else
                appendResults("PORT " + String.valueOf(port).toString() + ": CLOSED");

        }
        else {
            // Perform an asynchronous port scan
            PortScan.onAddress(host).setTimeOutMillis(1000).setPortsAll().doScan(new PortScan.PortListener() {
                @Override
                public void onResult(int portNo, boolean open) {
                    if (open) appendResults("Open: " + portNo);
                }

                @Override
                public void onFinished(ArrayList<Integer> openPorts) {
                    appendResults("Open Ports: " + openPorts.size());
                }
            });
        }
    }


    public void appendResults(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                portScanResults.append(s + "\n");
            }
        });
    }



    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }


    //region ACTIONBAR
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

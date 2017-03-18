package com.example.milkymac.connview_main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

public class PingActivity extends AppCompatActivity {

    //region UI VARS
    TextView tvPinger;
    TextView tvTargetPing;
    TextView tvPacketCount;
    TextView tvPacketSize;
    ImageButton btnPing;
    //endregion


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

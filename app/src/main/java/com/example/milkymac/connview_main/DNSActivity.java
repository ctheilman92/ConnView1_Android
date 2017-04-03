package com.example.milkymac.connview_main;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;
import org.xbill.DNS.*;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;



/*

*
* Using Popular DNSJAVA Library for dns record lookup.
* Possibly creating DNS Server (Cyber Sec future notes)
* http://www.dnsjava.org/
*

 */

public class DNSActivity extends AppCompatActivity {


    //region UI VARS
    EditText targetHostName;
    TextView dnsResults;
    ImageButton btnDnsQuery;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dns);


        Toolbar toolbar = (Toolbar) findViewById(R.id.dns_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initVars();

    }

    private void initVars() {
        targetHostName = (EditText) findViewById(R.id.etTargetDNS);
        dnsResults = (TextView) findViewById(R.id.tvDNSResults);

        btnDnsQuery = (ImageButton) findViewById(R.id.btnQueryDNS);
        btnDnsQuery.setOnClickListener(new View.OnClickListener() {
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
                dnsQuery();
            }
            catch (UnknownHostException e) {
                e.printStackTrace();
            }
            return this;
        }
    }


    public void dnsQuery() throws UnknownHostException {
        String host = targetHostName.getText().toString().trim();

        //get IP associated with name
        InetAddress addr = InetAddress.getByName(host);
        String ip = addr.getHostAddress();
        if (addr instanceof Inet4Address) {
            ip = addr.getHostAddress();
        }
        else {
            ip = addr.getHostAddress();
        }

        appendResults("QUERY FOR " + host + " (" + ip + ")...");
    }


    public void appendResults(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dnsResults.append(s + "\n");
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

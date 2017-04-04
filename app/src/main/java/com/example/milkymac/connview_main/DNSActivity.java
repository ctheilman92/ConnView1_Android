package com.example.milkymac.connview_main;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import org.w3c.dom.Text;
import org.xbill.DNS.*;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;



/*

*
*
* MODEL AFTER THIS PAGE: https://www.ultratools.com/tools/dnsLookupResult
*
*
* Using Popular DNSJAVA Library for dns record lookup.
* Possibly creating DNS Server (Cyber Sec future notes)
* http://www.dnsjava.org/
*
*
* RELEVANT RECORD TYPES
*   A - ipv4
*   AAAA - ipv6
*   MX - mail
*   NS - Nameserver
*   ISDN - isdn address for host
*   PTR - lookup by IP address (reverse dns query)
*
 */


public class DNSActivity extends AppCompatActivity {



    //region UI VARS
    EditText targetHostName;
    EditText dnsRecord;
    TextView dnsResults;
    ImageButton btnDnsQuery;
    //endregion

    private static int RECORD_TYPE;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dns);


        Toolbar toolbar = (Toolbar) findViewById(R.id.dns_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = DNSActivity.this;

        initVars();
        RECORD_TYPE = 0;
        dnsRecord.setText("A RECORD");

    }

    private void initVars() {
        targetHostName = (EditText) findViewById(R.id.etTargetDNS);
        dnsResults = (TextView) findViewById(R.id.tvDNSResults);

        dnsRecord = (EditText) findViewById(R.id.etRecordType);
        dnsRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog TypeDialog;
                final CharSequence[] modalItems = {"A", "TXT", "NS", "MX", "ISDN", "Reverse", "LOC", "A6" };

                //CREATE MODAL DIAG
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setSingleChoiceItems(modalItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch(which)
                        {
                            case 0:
                                RECORD_TYPE = 0;
                                dnsRecord.setText("A RECORD");

                                dialog.dismiss();
                                break;
                            case 1:
                                RECORD_TYPE = 1;
                                dnsRecord.setText("TXT RECORD");

                                dialog.dismiss();
                                break;
                            case 2:
                                RECORD_TYPE = 2;
                                dnsRecord.setText("NS RECORD");

                                dialog.dismiss();
                                break;
                            case 3:
                                RECORD_TYPE = 4;
                                dnsRecord.setText("MX RECORD");

                                dialog.dismiss();
                                break;
                            case 5:
                                RECORD_TYPE = 5;
                                dnsRecord.setText("ISDN RECORD");

                                dialog.dismiss();
                                break;
                            case 6:
                                RECORD_TYPE = 6;
                                dnsRecord.setText("Reverse");

                                dialog.dismiss();
                                break;
                            case 7:
                                RECORD_TYPE = 7;
                                dnsRecord.setText("LOC RECORD");

                                dialog.dismiss();
                                break;
                            case 8:
                                RECORD_TYPE = 8;
                                dnsRecord.setText("A6 RECORD");

                                dialog.dismiss();
                                break;
                        }
                    }
                });
                TypeDialog = builder.create();
                TypeDialog.show();
            }
        });

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
            } catch (TextParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return this;
        }
    }

    public void validateDomain() {
        String d = targetHostName.getText().toString();
        //check for '.' and an upper level domain & lower level domain name [lower-domain].[upper-domain]
    }

    public void getReverse(String ip) throws IOException {
        Record opt = null;
        Resolver res = new ExtendedResolver();

        Name name = ReverseMap.fromAddress(ip);

        //@param Type.ptr = int => record type (reverse lookup)
        //@param Dclass.IN => inbound
        Record rec = Record.newRecord(name, Type.PTR, DClass.IN);

        Message query = Message.newQuery(rec);
        Message response = res.send(query);

        Record[] callbacks = response.getSectionArray(Section.ANSWER);

        if (callbacks.length == 0)
            appendResults("NO ANSWERS FOR: " + ip);
        else
            appendResults(callbacks[0].toString());
    }

    public void getNS() throws TextParseException {
        Record[] records = new Lookup(targetHostName.getText().toString(), Type.NS).run();

        for (int i = 0; i < records.length; i++) {
            NSRecord ns = (NSRecord) records[i];
            appendResults(ns.toString());
        }
    }

    public void getTXT() throws TextParseException {
        Record[] records = new Lookup(targetHostName.getText().toString(), Type.TXT).run();

        for (int i = 0; i < records.length; i++) {
            TXTRecord txt = (TXTRecord) records[i];
            appendResults(txt.toString());
        }
    }

    public void getMX() throws TextParseException {
        Record[] records = new Lookup(targetHostName.getText().toString(), Type.MX).run();

        for (int i = 0; i < records.length; i++) {
            MXRecord mx = (MXRecord) records[i];
            appendResults(mx.toString());
        }
    }

    public void getA() throws TextParseException {
        Record[] records = new Lookup(targetHostName.getText().toString(), Type.A).run();

        for (int i = 0; i < records.length; i++) {
            ARecord a = (ARecord) records[i];
            appendResults("\n"+a.toString());
        }
    }

    public void getA6() throws TextParseException {
        Record[] records = new Lookup(targetHostName.getText().toString(), Type.AAAA).run();

        for (int i = 0; i < records.length; i++) {
            AAAARecord a6 = (AAAARecord) records[i];
            appendResults(a6.toString());
        }
    }

    public void getISDN() throws TextParseException {
        Record[] records = new Lookup(targetHostName.getText().toString(), Type.ISDN).run();

            for (int i = 0; i < records.length; i++) {
                ISDNRecord isdn = (ISDNRecord) records[i];
                appendResults(isdn.toString());
            }
    }

    public void getLOC() throws TextParseException {
        Record[] records = new Lookup(targetHostName.getText().toString(), Type.LOC).run();

            for (int i = 0; i < records.length; i++) {
                LOCRecord loc = (LOCRecord) records[i];
                appendResults(loc.toString());
            }
    }

    public void dnsQuery() throws IOException {
        if (TextUtils.isEmpty(targetHostName.getText().toString())) {
            Toast.makeText(context, "ERR, domain is empty.", Toast.LENGTH_LONG).show();
            return;
        }


        //TODO: WRITE HEADERS FOR OUTPUT -> NAME SERV RECORDS:
        switch (RECORD_TYPE) {
            case (0):
                getA();
                break;
            case (1):
                getTXT();
                break;
            case (2):
                getNS();
                break;
            case (3):
                getMX();
                break;
            case (4):
                getISDN();
                break;
            case (5):
                getReverse("8.8.8.8");
                break;
            case (7):
                getLOC();
            default:
                getA6();
                break;
        }

        appendResults("\n--------------\n");
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

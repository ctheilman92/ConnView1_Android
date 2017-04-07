package com.example.milkymac.connview_main;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.apache.commons.net.whois.WhoisClient;

import java.io.IOException;
import java.net.SocketException;

public class WhoisActivity extends AppCompatActivity {

    //region UI VARS
    EditText whoisDomain;
    EditText serverSelected;
    ImageButton btnWhois;
    TextView whoisResults;

    //endregion

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whois);


        Toolbar toolbar = (Toolbar) findViewById(R.id.traceroute_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = WhoisActivity.this;
        initVars();
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


    public void initVars() {
        whoisDomain = (EditText) findViewById(R.id.etTargetWhois);
        whoisResults = (TextView) findViewById(R.id.tvWhoisResults);

        serverSelected = (EditText) findViewById(R.id.etServer);
        serverSelected.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {

                  //TODO: ADD MORE SERVERS HERE
                  final AlertDialog TypeDialog;
                  final CharSequence[] modalItems = {"internic.net", "something else.net"};

                  AlertDialog.Builder builder = new AlertDialog.Builder(context);
                  builder.setTitle("Select Whois-Server");

                  builder.setItems(modalItems, new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                          switch (which) {
                              case 0:
                                  serverSelected.setText("internic.net");
                                  break;
                              case 1:
                                  serverSelected.setText("something else.net");
                                  break;
                          }
                      }
                  });
                  TypeDialog = builder.create();
                  TypeDialog.show();
              }
        });

        btnWhois =(ImageButton) findViewById(R.id.btnQueryWhois);
        btnWhois.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                new Tasker().execute();
            }
        });

    }

    public class Tasker extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            doWhois();
            return null;
        }

    }

    public void appendResults(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                whoisResults.append(s + "\n");
            }
        });
    }

    public void doWhois() {
        StringBuilder res1 = new StringBuilder("");
        WhoisClient client = new WhoisClient();

        try {
            client.connect(WhoisClient.DEFAULT_HOST);
            String queryData = client.query("="+whoisDomain);

            res1.append(queryData);
            client.disconnect();
        }
        catch (SocketException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        appendResults(res1.toString());
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


package com.example.milkymac.connview_main;

import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
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

import org.apache.commons.net.chargen.CharGenTCPClient;
import org.apache.commons.net.whois.WhoisClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
*
*   (TLD) - TOP LEVEL DOMAINS
*           -> COME IN A VARIETY OF TYPES. HENCE THE NEED FOR DIFFERENT WHOIS SERVERS
*
*           TODO: parse online xml url consisting of list of ALL whois servers
*           TODO: include Information links for commonly used servers in list (redirect to info pages)
*
*           internic.net -> DEFAULT WHOIS SERVER
*           afilias.info (.info, .lgbt, .vote, .pro ETC) -> https://afilias.info/about-us
*           iana.org -> https://www.iana.org/domains
*           enom.com -> https://www.enom.com/whois/default.aspx
*           educause.net -> https://net.educause.edu/edudomain/index.asp
*
*
*
*
 */

public class WhoisActivity extends AppCompatActivity {

    //region UI VARS
    private EditText whoisDomain;
    private EditText serverSelected;
    private ImageButton btnWhois;
    private ImageButton btnServerContext;
    private TextView whoisResults;
    //endregion

    Context context;
    private static int WHOIS_PORT = WhoisClient.DEFAULT_PORT;
    private static String WHOIS_HOST = WhoisClient.DEFAULT_HOST;
    private static Hashtable SERVER_DESC = new Hashtable() {{
        put("whois.internic.net", "DEFAULT Whois Server:\n\nSupports most TLD queries...");
        put("whois.iana.org", "IANA Whois Server:\n\nAccepted Queries: Domain Names, IP's, and AS Numbers..\n\nVisit: https://www.iana.org/domains");
        put("whois.enom.com", "ENOM Whois Server:\n\nLess popular public DN whois lookup.\n\nAccepted Queries: Public Domain Names\n\nVisit: https://www.enom.com/whois/default.aspx");
        put("whois.afilias.info", "AFILIAS Whois Server:\n\ngTLD's for causes and purposes. \n\nAccepted Queries: .INFO, .LGBT, .PRO, .PINK, etc...\nVisit: https://afilias.info/about-us");
        put("whois.educause.net", "EDUCAUSE Whois Server:\n\nAccepted Queries: Educational TLD's (.EDU)\n\nVisit: https://net.educause.edu/edudomain/index.asp");
    }};
    private static final Set<String> SERVER_KEYS = SERVER_DESC.keySet();


    //for regex
    private static Pattern pattern;
    private Matcher matcher;




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


        btnServerContext = (ImageButton) findViewById(R.id.btnServerInfo);
        btnServerContext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog TypeDialog;

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                for (String key : SERVER_KEYS) {
                    if (key.equals(serverSelected.getText().toString())) {
                        builder.setTitle("Info: "+key);
                        builder.setMessage((CharSequence) SERVER_DESC.get(key));
                    }
                }

                builder.setNegativeButton("Thanks", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                TypeDialog = builder.create();
                TypeDialog.show();
            }
        });


        whoisDomain = (EditText) findViewById(R.id.etTargetWhois);
        whoisResults = (TextView) findViewById(R.id.tvWhoisResults);


        serverSelected = (EditText) findViewById(R.id.etServer);
        serverSelected.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {

                  //TODO: ADD MORE SERVERS HERE
                  final AlertDialog TypeDialog;
                  final CharSequence[] modalItems = (CharSequence[]) SERVER_DESC.keySet().toArray(new CharSequence[SERVER_DESC.size()]);



                  AlertDialog.Builder builder = new AlertDialog.Builder(context);
                  builder.setTitle("Select Whois-Server");

                  builder.setItems(modalItems, new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                          switch (which) {
                              case 0:
                                  serverSelected.setText(modalItems[0]);
                                  WHOIS_HOST = WhoisClient.DEFAULT_HOST;
                                  break;
                              case 1:
                                  serverSelected.setText(modalItems[1]);
                                  WHOIS_HOST = modalItems[1].toString();
                                  break;
                              case 2:
                                  serverSelected.setText(modalItems[2]);
                                  WHOIS_HOST = modalItems[2].toString();
                                  break;
                              case 3:
                                  serverSelected.setText(modalItems[3]);
                                  WHOIS_HOST = modalItems[3].toString();
                                  break;
                              case 4:
                                  serverSelected.setText(modalItems[4]);
                                  WHOIS_HOST = modalItems[4].toString();
                          }
                      }
                  });
                  TypeDialog = builder.create();
                  TypeDialog.show();
              }
        });

        btnWhois = (ImageButton) findViewById(R.id.btnQueryWhois);
        btnWhois.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                whoisResults.setText("");
                new Tasker().execute();
            }
        });

    }

    public class Tasker extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {

            //using apache commons with regex
            doWhois();

//            //using sockets
//            try {
//                doSocketWhois();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            return null;
        }

    }

//    public void doSocketWhois() throws IOException {
//        appendResults("beginning socket whois query...\nusing whois.enom.com...\n\n");
//
//        String whoisServ = "whois.internic.net";
//        String whoisServ2 = "whois.enom.com";
//
//        Socket socket = new Socket(whoisServ, WHOIS_PORT);
//
//        InputStreamReader isr = new InputStreamReader(socket.getInputStream());
//        BufferedReader bfr = new BufferedReader(isr);
//
//        PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
//        pw.print("google.com");
//
//        String res = "";
//        while ((res = bfr.readLine()) != null) {
//            appendResults(res);
//        }
//
//    }




    //region WHOIS_METHODS

    //region regex parser
    private static final String WHOIS_SERVER_PATTERN = "Whois Server:\\s(.*)";
    static {
        pattern = Pattern.compile(WHOIS_SERVER_PATTERN);
    }
    //endregion

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
            client.connect(WHOIS_HOST);
            String queryData = client.query(whoisDomain.getText().toString());

            res1.append(queryData);
            client.disconnect();

            //server URL
            String serverURL = getWhoisServer(queryData);
            if (!TextUtils.isEmpty(serverURL)) {
                //whois -h
                String whoisQuery2 = queryWithServer(whoisDomain.getText().toString(), serverURL);
                res1.append(whoisQuery2);
            }
        }
        catch (SocketException e) {
            e.printStackTrace();
            whoisResults.append("QUERY ERROR - SOCKET EXCEPTION: "+ e.toString());
        }
        catch (IOException e) {
            e.printStackTrace();
            whoisResults.append("QUERY ERROR - IO EXCEPTION: "+ e.toString());
        }

        appendResults(res1.toString());
    }

    private String queryWithServer(String domain, String server) {
        String res = "";
        WhoisClient client = new WhoisClient();

        try {
            client.connect(server);
            res = client.query(domain);
            client.disconnect();
        }
        catch (SocketException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }

    public String getWhoisServer(String whois) {
        String res = "";

        matcher = pattern.matcher(whois);

        //get last whois server
        while (matcher.find()) {
            res = matcher.group(1);
        }
        return res;
    }
    //endregion

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


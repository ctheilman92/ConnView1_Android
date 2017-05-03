package com.example.milkymac.connview_main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.milkymac.connview_main.helpers.DatabaseHelper;
import com.example.milkymac.connview_main.helpers.NetHelper;
import com.example.milkymac.connview_main.helpers.myResultReceiver;
import com.example.milkymac.connview_main.models.MyDevice;
import com.example.milkymac.connview_main.models.MyNet;
import com.example.milkymac.connview_main.models.Network;
import com.example.milkymac.connview_main.models.User;
import com.google.gson.Gson;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.xml.datatype.Duration;


public class IPInfoFragment extends Fragment implements Serializable, myResultReceiver.Receiver  {


    //region UI VARS
    TextView tvStatus;
    TextView tvPrivateIP;
    TextView tvPrivateMAC;
    TextView tvConnectionStatus;
    TextView tvSSID;
    TextView tvNetMask;
    TextView tvFrequency;
    TextView tvSignal;
    TextView tvBroadcast;
    TextView tvBSSID;
    TextView tvNetIP;
    //endregion

    public myResultReceiver mReceiver;
    public DatabaseHelper dbhelper;

    public static String PrivateIP;
    public static String PrivateMAC;
    public MyDevice mydev;
    public Network mynet;
    public MyNet personalNet;
    public User currentUser;


    public final String PREFS_NAME = "userPrefs";
    public SharedPreferences myprefs;
    public SharedPreferences.Editor editor;



    //region FRAGMENT STUFF
    private OnFragmentInteractionListener mListener;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String MYDEV_KEY = "MyDevices_Key";

    public IPInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static IPInfoFragment newInstance(int sectionNumber, Parcelable wrappedDev) {
        IPInfoFragment fragment = new IPInfoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putParcelable(MYDEV_KEY, wrappedDev);

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ipinfo, container, false);
        ((MainActivity) getActivity()).setActionBarTitle("IP Info");


        //keep track of current user
        myprefs = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = myprefs.edit();
        currentUser = new User(myprefs.getString("USERNAME_KEY", "---"), myprefs.getString("EMAIL_KEY", "---"), myprefs.getString("USERPASS_KEY", "---"));


        mydev = Parcels.unwrap(getArguments().getParcelable(MYDEV_KEY));
        new MyDeviceWorker().execute();

        launchNetworkSniffer(1);
        ViewPager mViewPager = (ViewPager) v.findViewById(R.id.container);


        PrivateIP = mydev.getIp();
        PrivateMAC = mydev.getMac();

        initVar(v);

        return v;
    }


    public void launchNetworkSniffer(int opr) {
        Intent serviceIntent = new Intent(getActivity(), NetHelper.class);

        //setup resultReceiver for service callbacks
        mReceiver = new myResultReceiver(new android.os.Handler());
        mReceiver.setReceiver(this);


        serviceIntent.putExtra(NetHelper.BUNDLE_RECEIVER2, mReceiver);
        serviceIntent.putExtra("OPR", opr);
        getActivity().startService(serviceIntent);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context; }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //region ASYNCTASK_RUNNERS
    private class MyDeviceWorker extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            mydev.setListNetworkInterfaces();
            mydev.setInterfacesByDisplayName();
            mydev.getSSIDName();
            mydev.getLocalAddresses("wlan0");

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {

            tvPrivateIP.setText(mydev.getIp().toString());
            tvPrivateMAC.setText(mydev.getMac().toString());

        }

    }
    //endregion


    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        Log.d("NET_DATA_RECEIVED", "processing...");

        String newnetjson = resultData.getString("DATA_");
        Log.d("DATA_", newnetjson);


        Gson gson = new Gson();
        mynet = gson.fromJson(newnetjson, Network.class);

        //region DATABASE WORK
        //confusing naming conventions but whatever, It's crunch time.
        personalNet = new MyNet(mynet.getLastConnectedDate(), mynet.getState(),currentUser.getName(), mynet.getSSID(), mynet.getBSSID(), mynet.getSignal(), mynet.getFrequency(), mynet.getNetIP(), mynet.getBroadcast(), mynet.getNetMask());
        new Thread(new Runnable() {
            @Override
            public void run() {
                dbhelper = new DatabaseHelper(getActivity().getApplicationContext());

                //if returns true, a record exists - so just update teh times connected counter
                if (dbhelper.checkNetworkHistory(currentUser.getName(), mynet.getSSID())) {
                    Log.d("DB_IPINFO_OP", "updating user history record for this network");

                    //if personalNet.lastConnected is within an hour of the saved connection. do nothing.
                    //otherwise, update times connected counter.
                    try {

                        //if connection is established
                        if (personalNet.getState()) {
                            MyNet tempnet = dbhelper.getuserNetwork(currentUser.getName(), mynet.getSSID());

                            Date checkDate = tempnet.getLastConnectedDate();
                            Date myNetDate = personalNet.getLastConnectedDate();


                            long diff = checkDate.getTime() - myNetDate.getTime();
                            long seconds = diff / 1000;
                            long minutes = seconds / 60;
                            long hours = minutes / 60;
                            long days = hours / 24;

                            Log.d("DB_INFO_OP_TIMESTAMP", "difference in hours: " + hours);
                            if (hours <= -1) {
                                try {
                                    Log.d("UPDATING_COUNTER_NOW", "well...ya....");
                                    dbhelper.updateNetCounter(personalNet);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            else
                                return;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Log.d("DB_IPINFO_OP", "adding new network to user history");
                    personalNet.setLastConnected(new Date());
                    dbhelper.addUserNetwork(personalNet);
                }

            }
        }).start();



        //region FILL UI
        if (mynet.getState()) { tvConnectionStatus.setText("Connected."); }
        else { tvConnectionStatus.setText("Disconnected"); }

        tvSSID.setText(mynet.getSSID());
        tvBroadcast.setText(mynet.getBroadcast());


        if (mynet.getFrequency() == 0) { tvFrequency.setText("----"); }
        else { tvFrequency.setText(String.valueOf(mynet.getFrequency())); }

        tvBSSID.setText(mynet.getBSSID());
        tvNetIP.setText(mynet.getNetIP());

        if (mynet.getNetMask() == 0)  { tvNetMask.setText("----"); }
        else { tvNetMask.setText(String.valueOf(mynet.getNetMask())); }

        if (mynet.getSignal() == 0) { tvSignal.setText("----"); }
        else { tvSignal.setText(String.valueOf(mynet.getSignal())); }
        //endregion
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String title);
    }
    //endregion


    public void initVar(View v) {
        tvPrivateIP = (TextView) v.findViewById(R.id.tvPersonalIP);
        tvPrivateMAC = (TextView) v.findViewById(R.id.tvPersonalMAC);
        tvConnectionStatus = (TextView) v.findViewById(R.id.lblisConnected);
        tvSSID = (TextView) v.findViewById(R.id.tvSSID);
        tvBSSID = (TextView) v.findViewById(R.id.tvBSSID);
        tvNetMask = (TextView) v.findViewById(R.id.tvNetMask);
        tvFrequency = (TextView) v.findViewById(R.id.tvFrequency);
        tvSignal = (TextView) v.findViewById(R.id.tvSignal);
        tvBroadcast = (TextView) v.findViewById(R.id.tvBroadCast);
        tvNetIP = (TextView) v.findViewById(R.id.tvNetIP);

        String CURRENT_NET = myprefs.getString("CURRENT_NET_KEY", "");
        if (!CURRENT_NET.equals("")) {

        Gson gson = new Gson();
        mynet = gson.fromJson(CURRENT_NET, Network.class);

        //region FILL UI
        if (mynet.getState()) { tvConnectionStatus.setText("Connected."); }
        else { tvConnectionStatus.setText("Disconnected"); }

        tvSSID.setText(mynet.getSSID());
        tvBroadcast.setText(mynet.getBroadcast());

        if (mynet.getFrequency() == 0) { tvFrequency.setText("----"); }
        else { tvFrequency.setText(String.valueOf(mynet.getFrequency())); }

        tvBSSID.setText(mynet.getBSSID());

        tvNetIP.setText(mynet.getNetIP());

        if (mynet.getNetMask() == 0)  { tvNetMask.setText("----"); }
        else { tvNetMask.setText(String.valueOf(mynet.getNetMask())); }

        if (mynet.getSignal() == 0) { tvSignal.setText("----"); }
        else { tvSignal.setText(String.valueOf(mynet.getSignal())); }
        //endregion
        }
    }
}

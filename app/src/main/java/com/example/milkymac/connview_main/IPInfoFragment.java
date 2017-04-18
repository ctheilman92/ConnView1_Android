package com.example.milkymac.connview_main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.milkymac.connview_main.helpers.NetHelper;
import com.example.milkymac.connview_main.helpers.myResultReceiver;
import com.example.milkymac.connview_main.models.MyDevice;
import com.example.milkymac.connview_main.models.Network;
import com.google.gson.Gson;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


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
    //endregion

    public myResultReceiver mReceiver;

    public static String PrivateIP;
    public static String PrivateMAC;
    public MyDevice mydev;
    public Network mynet;



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
        if (container == null) {
            return null;
        }

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ipinfo, container, false);
        ((MainActivity) getActivity()).setActionBarTitle("IP Info");


        launchNetworkSniffer(1);
        ViewPager mViewPager = (ViewPager) v.findViewById(R.id.container);

        mydev = Parcels.unwrap(getArguments().getParcelable(MYDEV_KEY));

        PrivateIP = mydev.getIp();
        PrivateMAC = mydev.getMac();

        initVar(v);

        return v;
    }


    //TODO: USE BROADCAST RECEIVER TO MANAGE WIFI CONNECTIVITY STATUS
    //TODO: SETUP RECEIVER TO RELAY LIST OF DEVICES BACK TO ACTIVITY...
    //TODO: AFTER LIST OF CONNECTED DEVICES IS COMPLETE, SAVE LIST FOR ALL ACTIVITIES
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

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        Log.d("NET_DATA_RECEIVED", "processing...");

        String newnetjson = resultData.getString("DATA_");
        Log.d("DATA_", newnetjson);

        Gson gson = new Gson();
        mynet = gson.fromJson(newnetjson, Network.class);

        if (mynet.getState()) { tvConnectionStatus.setText("Connected."); }
        else { tvConnectionStatus.setText("Disconnected"); }

        tvSSID.setText(mynet.getSSID());
        tvBroadcast.setText(mynet.getBroadcast());

        if (mynet.getFrequency() == 0) { tvFrequency.setText("----"); }
        else { tvFrequency.setText(String.valueOf(mynet.getFrequency())); }

        tvBSSID.setText(mynet.getBSSID());

        if (mynet.getNetMask() == 0)  { tvNetMask.setText("----"); }
        else { tvNetMask.setText(String.valueOf(mynet.getNetMask())); }

        if (mynet.getSignal() == 0) { tvSignal.setText("----"); }
        else { tvSignal.setText(String.valueOf(mynet.getSignal())); }
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

        tvPrivateIP.setText(PrivateIP);
        tvPrivateMAC.setText(PrivateMAC);
    }

    //region INET-STUFF

    //gets ipv6
    public String getLocalIpAddress()
    {
        try
        {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
            {
                NetworkInterface intf = en.nextElement();


                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress())
                    {
                        return inetAddress.getHostAddress().toString();

                    }
                }
            }
        }
        catch (SocketException ex)
        {
            Log.e("SRM", ex.toString());
        }
        return null;
    }
    //endregion
}

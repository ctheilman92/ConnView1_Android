package com.example.milkymac.connview_main;

import android.content.Context;
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

import com.example.milkymac.connview_main.models.MyDevice;

import org.parceler.Parcels;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


public class IPInfoFragment extends Fragment implements Serializable {


    //region UI VARS
    TextView tvStatus;
    TextView tvPrivateIP;
    TextView tvPrivateMAC;
    TextView tvConnectionStatus;
    //endregion

    public static String PrivateIP;
    public static String PrivateMAC;
    public MyDevice mydev;



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
//        args.putSerializable(MYDEV_KEY, thisdev);


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

        ViewPager mViewPager = (ViewPager) v.findViewById(R.id.container);

        mydev = Parcels.unwrap(getArguments().getParcelable(MYDEV_KEY));

        PrivateIP = mydev.getIp();
        PrivateMAC = mydev.getMac();
        initVar(v);

        return v;
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String title);
    }
    //endregion


    public void initVar(View v) {
        tvStatus = (TextView) v.findViewById(R.id.lblisConnected);
        tvPrivateIP = (TextView) v.findViewById(R.id.tvPersonalIP);
        tvPrivateMAC = (TextView) v.findViewById(R.id.tvPersonalMAC);
        tvConnectionStatus = (TextView) v.findViewById(R.id.lblisConnected);

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

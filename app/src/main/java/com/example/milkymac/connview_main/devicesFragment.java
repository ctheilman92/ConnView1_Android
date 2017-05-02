package com.example.milkymac.connview_main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import com.example.milkymac.connview_main.helpers.NetHelper;
import com.example.milkymac.connview_main.helpers.myResultReceiver;
import com.example.milkymac.connview_main.models.Devices;
import com.google.gson.Gson;



public class devicesFragment extends Fragment implements myResultReceiver.Receiver {
    public myResultReceiver mReceiver;
    OnListFragmentInteractionListener mlistener;
    Context context;
    MydevicesRecyclerViewAdapter mydevAdapter;

    Intent serviceIntent;

    private static ArrayList<Devices> devlist;
    SharedPreferences netprefs;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;


    public devicesFragment() {
    }
    // TODO: Customize parameter initialization
    public static devicesFragment newInstance(int columnCount) {
        devicesFragment fragment = new devicesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }


    //TODO: populate devices list with a sharedPreferences list and double check there. also split up subnet into multiple threads to work faster.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_devices_list, container, false);
        ((MainActivity) getActivity()).setActionBarTitle("Net Scan");


        devlist = new ArrayList<>();
        //example device.
        devlist.add(new Devices("TEST_DEVICE", true, "192.168.100.199", "00:00:00:00:00:00", true, "MOBILE", "meeseeks box"));


        launchNetworkSniffer(0);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();


            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            mydevAdapter = new MydevicesRecyclerViewAdapter(devlist, mListener);
            recyclerView.setAdapter(mydevAdapter);
        }
        return view;
    }


    //NetHelper intent service class startup
    //discover network info, and get a list of devices
    public void launchNetworkSniffer(int opr) {
        serviceIntent = new Intent(getActivity(), NetHelper.class);

        //setup resultReceiver for service callbacks
        mReceiver = new myResultReceiver(new android.os.Handler());
        mReceiver.setReceiver(this);

        serviceIntent.putExtra(NetHelper.BUNDLE_RECEIVER, mReceiver);
        serviceIntent.putExtra("OPR", opr);
        getActivity().startService(serviceIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        mReceiver.setReceiver(this);
    }

    //avoid leaks with result receiver

    @Override
    public void onPause() {
        super.onPause();
        mReceiver.setReceiver(null);
        getActivity().stopService(new Intent(getActivity(), NetHelper.class));
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mlistener = (OnListFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    //receiver implemented methods
    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        Log.d("DATA_RECEIVED", "processing...");

        String newdevjson = resultData.getString("DATA_");
        Log.d("DATA_", newdevjson);

        Gson gson = new Gson();
        Devices newd = gson.fromJson(newdevjson, Devices.class);

        if (devlist != null) {
            for (Devices i : devlist) {
                if (i.getIp().equals(newd.getIp())) {
                    return;
                }
            }
            Log.d("ADDING_FRAGDEVICE", newd.devName);
            devlist.add(newd);
            mydevAdapter.notifyItemInserted(devlist.size() -1);
            mydevAdapter.notifyDataSetChanged();
        }
    }


    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Devices device);
    }
}

package com.example.milkymac.connview_main;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v4.net.ConnectivityManagerCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.milkymac.connview_main.dummy.DummyContent;
import com.example.milkymac.connview_main.dummy.DummyContent.DummyItem;

import java.lang.ref.WeakReference;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class devicesFragment extends Fragment {
    OnListFragmentInteractionListener mlistener;
    Context context;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public devicesFragment() {
    }
    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_devices_list, container, false);
        ((MainActivity) getActivity()).setActionBarTitle("Net Scan");


//        new NetworkSniffTask(context).execute();



        // Set the adapter
        if (view instanceof RecyclerView) {

            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MydevicesRecyclerViewAdapter(DummyContent.ITEMS, mListener));
        }
        return view;
    }


//    static class NetworkSniffTask extends AsyncTask {
//
//        Context context;
//        NetworkSniffTask(Context myContext) {
//            this.context = myContext;
//        }
//
//        private final String TAG = "EXEC_NETSNIFF";
//
//        @Override
//        protected Object doInBackground(Object[] params) {
//            {
//                Log.d(TAG, "begin sniffing network... ");
//
//                try {
//                    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//                    WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//                    WifiInfo connectionInfo = manager.getConnectionInfo();
//
//                    String ip = Formatter.formatIpAddress(connectionInfo.getIpAddress());
//
//                    Log.d(TAG, "Active Network: " + String.valueOf(activeNetwork));
//                    Log.d(TAG, "IP_ADDR: " + String.valueOf(ip));
//
//                    //substring for the net prefix
//                    String prefix = ip.substring(0, ip.lastIndexOf(".") + 1);
//                    Log.d(TAG, "prefix: " + prefix);
//
//                    for (int i = 0; i < 255; i++) {
//                        String getIP = prefix + String.valueOf(i);
//
//                        InetAddress getAddr = InetAddress.getByName(getIP);
//                        boolean isReachable = getAddr.isReachable(1000);
//                        String hostname = getAddr.getCanonicalHostName();
//
//                        if (isReachable) {
//                            Log.d(TAG, "HOST: " + String.valueOf(hostname) + "(" + String.valueOf(getIP) + ") - STATUS: UP");
//                        }
//                    }
//
//                } catch (Exception e) {
//                    Log.d("EXEC_ERR", e.toString());
//                }
//                return null;
//            }
//        }
//    }


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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }
}

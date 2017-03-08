package com.example.milkymac.connview_main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * **TODO: implement customeArrayAdapter class for an ArrayList<devices>
 */


public class NetscanFragment extends Fragment {

    private ListView lvDev;
    private  List<String> listDevs;
    private List<devices> listDeviceObjs;
    private ArrayAdapter<String> arrayAdapter;
    private OnFragmentInteractionListener mListener;
    private Context mContext;


    //dummy content replace later
    public String[] dummyDevs = new String[] {
            "DEVICE 1",
            "DEVICE 2",
            "DEVICE 3",
            "DEVICE 4",
            "DEVICE 5",
            "DEVICE 6",
            "DEVICE 7",
            "DEVICE 8",
            "DEVICE 9",
            "DEVICE 10",
            "DEVICE 11",
    };

    public NetscanFragment() {
        // Required empty public constructor

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (container == null) {
            return null;
        }
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_netscan, container, false);

        //databind listview
        lvDev = (ListView) v.findViewById(R.id.lvDevices);
        listDevs = new ArrayList<String>(Arrays.asList(dummyDevs));
//        getDevicetoList();

        getarrayadaptersetup();
        lvDev.setAdapter(arrayAdapter);

        lvDev.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3)
            {
                String value = (String)adapter.getItemAtPosition(position);
                // assuming string and if you want to get the value on click of list item
                // do what you intend to do on click of listview row
                launchDevDetail();
            }
        });

        return v;
    }

    public void launchDevDetail() {
        Intent intent = new Intent(getActivity(), DeviceDetailsActivity.class);
        startActivity(intent);
    }

    public void getDevicetoList() {

        //DUMMY DATA FOR NOW

        listDeviceObjs = new ArrayList<devices>();
        listDeviceObjs.add(new devices(1));
        listDeviceObjs.add(new devices(2));
        listDeviceObjs.add(new devices(3));
        listDeviceObjs.add(new devices(4));
        listDeviceObjs.add(new devices(5));
        listDeviceObjs.add(new devices(6));
        listDeviceObjs.add(new devices(7));
        listDeviceObjs.add(new devices(8));
    }

    public void getarrayObjectAdapterSetup() {
    }

    public void getarrayadaptersetup() {

//        getDevicetoList();
        arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listDevs) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Get the Item from ListView
                View view = super.getView(position, convertView, parent);

                // Initialize a TextView for ListView each Item
                TextView tvNAME = (TextView) view.findViewById(android.R.id.text1);
//                TextView tvIP = (TextView) view.findViewById(android.R.id.text2);



                // Set the text color of TextView (ListView Item)
                tvNAME.setTextColor(getResources().getColor(R.color.textContent));
//                tvIP.setTextColor(getResources().getColor(R.color.textContent));

                // Generate ListView Item using TextView
                return view;
            }
        };
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

package com.example.milkymac.connview_main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class ToolsSelectionFragment extends Fragment {

    //region UI VARS
    Button btnPing;
    Button btnDns;
    Button btnPortScan;
    Button btnTraceRoute;
    //endregion


    private static final String ARG_SECTION_NUMBER = "section_number";
    private OnFragmentInteractionListener mListener;

    public ToolsSelectionFragment() {
        // Required empty public constructor
    }

    public static ToolsSelectionFragment newInstance(int sectionNumber) {
        ToolsSelectionFragment fragment = new ToolsSelectionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tools_selection, container, false);
        ((MainActivity) getActivity()).setActionBarTitle("Utilities");

        initvar(v);
        registerButtonListeners();
        return v;
    }


    public void initvar(View view) {
        btnPing = (Button) view.findViewById(R.id.btnPing);
        btnDns = (Button) view.findViewById(R.id.btnDns);
        btnPortScan = (Button) view.findViewById(R.id.btnPortScan);
        btnTraceRoute = (Button) view.findViewById(R.id.btnTraceRoute);
    }


    public void registerButtonListeners() {
        btnPing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PingActivity.class);
                startActivity(intent);
            }
        });

        btnDns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DNSActivity.class);
                startActivity(intent);
            }
        });

        btnPortScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PortscanActivity.class);
                startActivity(intent);
            }
        });

        btnTraceRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WhoisActivity.class);
                startActivity(intent);
            }
        });
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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

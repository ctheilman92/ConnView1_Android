package com.example.milkymac.connview_main;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.support.v7.app.AppCompatActivity;



public class ToolsFragment extends Fragment {

    //region UI VARS
    Button btnPing;
    Button btnDns;
    Button btnPortScan;
    Button btnTraceRoute;
    //endregion

    private View thisView;
    private ViewPager viewPager;
    private OnFragmentInteractionListener mListener;
    private static final String ARG_SECTION_NUMBER = "section_number";


    public ToolsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /*TODO: HOOK UP BUTTON LISTENERS TO INFLATE SPECIIC PAGE OR FRAGMENT */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tools, container, false);
        thisView = view;
        ((MainActivity) getActivity()).setActionBarTitle("Utilities");

        initvar(view);
        registerButtonListeners();


        return view;
    }


    public void initvar(View view) {
        btnPing = (Button) view.findViewById(R.id.btnPing);
        btnDns = (Button) view.findViewById(R.id.btnDns);
        btnPortScan = (Button) view.findViewById(R.id.btnPortScan);
        btnTraceRoute = (Button) view.findViewById(R.id.btnTraceRoute);
//        ((MainActivity) getActivity()).setActionBarTitle("Utilities");
    }



    public void registerButtonListeners() {
        btnPing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ts = getChildFragmentManager().beginTransaction();
                ts.add(R.id.flToolLauncher, LaunchToolFragment.newInstance(1), "This Tool").commit();
                ts.addToBackStack(null);

                //TODO: NOT WORKING CAN'T FIND id.flToolLauncher
            }
        });

        btnDns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnPortScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnTraceRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public static ToolsFragment newInstance(int sectionNumber) {
            ToolsFragment fragment = new ToolsFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
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

package com.example.milkymac.connview_main;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.milkymac.connview_main.dummy.DummyContent;
import com.example.milkymac.connview_main.helpers.NetHelper;
import com.example.milkymac.connview_main.models.Devices;
import com.example.milkymac.connview_main.models.MyDevice;
import com.example.milkymac.connview_main.models.Network;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements devicesFragment.OnListFragmentInteractionListener,
                    IPInfoFragment.OnFragmentInteractionListener,
                    ToolsSelectionFragment.OnFragmentInteractionListener
{

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private MyDevice mydev;
    private Network myNet;
    private ArrayList<Devices> listDevices;
    public NetHelper nethelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mydev = new MyDevice(this);
        nethelper = new NetHelper(this);
        new MyDeviceWorker().execute();
        new MyNetWorker().execute();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment fors each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }
    

    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }

    //region ACTIONBAR
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
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

        if (id == R.id.action_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
    //endregion

    //region ASYNCTASK_RUNNERS

    //HANDLE BACKGROUND NETWORK OPERATIONS
    private class MyDeviceWorker extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {

            mydev.setListNetworkInterfaces();
            mydev.setInterfacesByDisplayName();
            mydev.getSSIDName();
            mydev.getLocalAddresses("wlan0");
            
            return this;
        }
    }
    
    private class MyNetWorker extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            
            try {
                nethelper.netSniff();
                myNet = nethelper.getNetInfo();
                listDevices = (ArrayList<Devices>) nethelper.getListDevices();
            }
            catch (IOException e) {
                e.printStackTrace();
                Log.d("IO_EXCEPTION", e.toString());
            }
            
            return this;
        }
    }
    
    //endregion


    //region FRAGMENT INTERACTION IMPLEMENTERS
    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }

    @Override
    public void onFragmentInteraction(String title) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    //endregion

    //region PAGE ADAPTER
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    Parcelable wrapped = Parcels.wrap(mydev);
                    return IPInfoFragment.newInstance(position + 1, wrapped);
                case 1:
                    return devicesFragment.newInstance(position);
                case 2:
                    return ToolsSelectionFragment.newInstance(position + 1);
                default:
                    Parcelable wrappeddefault = Parcels.wrap(mydev);
                    return IPInfoFragment.newInstance(position + 1, wrappeddefault);
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getText(R.string.title_fragment_IPINFO);
                case 1:
                    return getText(R.string.title_fragment_NETSCAN);
                case 2:
                    return getText(R.string.title_fragment_UTILITIES);
            }
            return null;
        }
    }
    //endregion
}

package com.example.milkymac.connview_main;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
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
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.milkymac.connview_main.dummy.DummyContent;
import com.example.milkymac.connview_main.models.MyDevice;
import com.example.milkymac.connview_main.models.devices;

import org.parceler.Parcels;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements devicesFragment.OnListFragmentInteractionListener,
                    IPInfoFragment.OnFragmentInteractionListener,
                    ToolsSelectionFragment.OnFragmentInteractionListener
{

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private MyDevice mydev;
    private ArrayList<devices> listDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mydev = new MyDevice(this);
        new NetworkOperation().execute();

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

    //TODO: TEST THOROUGHLY
    public void netSniff() {
        listDevices = new ArrayList<devices>();

        String TAG = "SNIFF_NET";
        Log.d(TAG, "begin sniffing network... ");
        Context context = this;

        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo connectionInfo = manager.getConnectionInfo();

            String ip = Formatter.formatIpAddress(connectionInfo.getIpAddress());

            Log.d(TAG, "Active Network: " + String.valueOf(activeNetwork));
            Log.d(TAG, "IP_ADDR: " + String.valueOf(ip));

            //substring for the net prefix
            String prefix = ip.substring(0, ip.lastIndexOf(".") + 1);
            Log.d(TAG, "prefix: " + prefix);


            //TODO: revise depending on data tests
            for (int i = 0; i < 255; i++) {
                String testIP = prefix + String.valueOf(i);
                InetAddress getAddr = InetAddress.getByName(testIP);
                boolean isReachable = getAddr.isReachable(1000);
                String hostname = getAddr.getCanonicalHostName();


                if (isReachable) {
                    Log.d(TAG, "HOST: " + String.valueOf(hostname) + "(" + String.valueOf(testIP) + ") - STATUS: UP");
                    NetworkInterface netInt = NetworkInterface.getByInetAddress(getAddr);

                    boolean isv4 = (getAddr instanceof Inet4Address) ? true : false;
                    listDevices.add(new devices(hostname, isv4, getAddr.getHostAddress(), assignMAC(netInt), true, "TYPE", "SSID NOT FOUND"));
                    Log.d("ADD_2DEVLIST", "adding"+ hostname + " to devicesList");

                }
            }

        } catch (Exception e) {
            Log.d("EXEC_ERR", e.toString());
        }
    }

    public String assignMAC(NetworkInterface intf) throws SocketException {
        byte[] macAddr = intf.getHardwareAddress();

        if (macAddr == null) {
            return "UNDEFINED";
        }

        StringBuilder res1 = new StringBuilder();
        for (byte b : macAddr) {
            res1.append(Integer.toHexString(b & 0xFF) + ":");
        }

        if (res1.length() > 0) {
            res1.deleteCharAt(res1.length() - 1);
        }

        Log.d("MAC_ADDRESS_FORMATTED", res1.toString());

        return (res1.toString());
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

    //region ASYNCTASK_RUNNER

    //HANDLE BACKGROUND NETWORK OPERATIONS
    private class NetworkOperation extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {

            mydev.setListNetworkInterfaces();
            mydev.setInterfacesByDisplayName();
            mydev.getSSIDName();
            mydev.getLocalAddresses("wlan0");

            netSniff();
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

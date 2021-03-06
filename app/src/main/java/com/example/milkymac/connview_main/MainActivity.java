package com.example.milkymac.connview_main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.milkymac.connview_main.models.Devices;
import com.example.milkymac.connview_main.models.MyDevice;

import org.parceler.Parcels;


public class MainActivity extends AppCompatActivity
        implements devicesFragment.OnListFragmentInteractionListener,
                    IPInfoFragment.OnFragmentInteractionListener,
                    ToolsSelectionFragment.OnFragmentInteractionListener {

    public SectionsPagerAdapter mSectionsPagerAdapter;
    public ViewPager mViewPager;

    private SharedPreferences myprefs;
    private SharedPreferences.Editor editor;
    private final String PREFS_NAME = "userPrefs";

    private MyDevice mydev;


    private final FragmentManager.OnBackStackChangedListener mOnBackStackChangedListener = new FragmentManager.OnBackStackChangedListener() {

        @Override
        public void onBackStackChanged() {
            FragmentManager fm = getSupportFragmentManager();
            Fragment fr = fm.findFragmentById(R.id.container);

            if (fr != null) {
                fr.onResume();
            }
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mydev = new MyDevice(this);
        myprefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = myprefs.edit();



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        FragmentManager fm = getSupportFragmentManager();
        fm.addOnBackStackChangedListener(mOnBackStackChangedListener);
        FragmentTransaction ft = fm.beginTransaction();

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimary, null));
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
            //clear shared-preferences
            editor.clear();
            editor.commit();
            Intent logoutIntent = new Intent(MainActivity.this, LoginActivity.class);
            finish();
            startActivity(logoutIntent);
        }

        if (id == R.id.action_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListFragmentInteraction(Devices device) {
    }
    //endregion


    @Override
    public void onFragmentInteraction(String title) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    //endregion

    //region PAGE ADAPTER
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

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
                    return IPInfoFragment.newInstance(position, wrapped);
                case 1:
                    return devicesFragment.newInstance(position);
                case 2:
                    return ToolsSelectionFragment.newInstance(position);
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

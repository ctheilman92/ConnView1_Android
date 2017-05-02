package com.example.milkymac.connview_main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ListView;
import android.widget.TextView;

import com.example.milkymac.connview_main.helpers.DatabaseHelper;
import com.example.milkymac.connview_main.models.MyNet;
import com.example.milkymac.connview_main.models.User;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.text.ParseException;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {


    SharedPreferences myprefs;
    SharedPreferences.Editor editor;
    public final String PREFS_NAME = "userPrefs";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        myprefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = myprefs.edit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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
            Intent logoutIntent = new Intent(ProfileActivity.this, LoginActivity.class);
            finish();
            startActivity(logoutIntent);
        }

        return super.onOptionsItemSelected(item);
    }


    //region FRAGMENT CLASSES

    /**
     *  embedded fragment classes
     **/

    public static class ProfileMainFragment extends Fragment {


        //region UI VARS
        public TextView profileEmail;
        public TextView topNetwork;
        public ListView lvRecentNetworks;
        //endregion


        private static final String ARG_SECTION_NUMBER = "section_number";
        public final String PREFS_NAME = "userPrefs";
        SharedPreferences myprefs;
        SharedPreferences.Editor editor;

        DatabaseHelper dbhelper;

        MyNet TopNetwork;
        public User currentUser;

        public ProfileMainFragment() {
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_profile, container, false);


            myprefs = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            currentUser = new User(myprefs.getString("USERNAME_KEY", "---"), myprefs.getString("EMAIL_KEY", "---"), myprefs.getString("USERPASS_KEY", "---"));
            initVars(rootView);

            try {
                getTopNetwork();
            }
            catch (ParseException e) {
                e.printStackTrace();
            }

            return rootView;
        }

        private void initVars(View v) {
            profileEmail = (TextView) v.findViewById(R.id.profileEmail);
            topNetwork = (TextView) v.findViewById(R.id.tvTopNet);
            lvRecentNetworks = (ListView) v.findViewById(R.id.lvRecentNet);

            profileEmail.setText(currentUser.getEmail());
        }


        public void getTopNetwork() throws ParseException {
            dbhelper = new DatabaseHelper(getActivity().getApplicationContext());

            List<MyNet> userNetworks = dbhelper.listUserNetworks(currentUser.getName());

            MyNet favoriteNet = userNetworks.get(0);
            int topCount = userNetworks.get(0).getTimesConnected();

            for (MyNet mn : userNetworks) {
                if (mn.getTimesConnected() > topCount) {
                    favoriteNet = mn;
                    topCount = mn.getTimesConnected();
                }
                Log.d("TRAVERSE_USER_NETS", "top Network is: " + mn.getSSID());
            }

            Log.d("USER_FAVE_NET", "top Network is: " + favoriteNet.getSSID());
            topNetwork.setText(favoriteNet.getSSID());
        }


        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
        }

        @Override
        public void onDetach() {
            super.onDetach();
        }
    }









    public static class ConfigsFragment extends Fragment {

        public final String PREFS_NAME = "userPrefs";
        SharedPreferences prefs;
        SharedPreferences.Editor editor;

        private static final String ARG_SECTION_NUMBER = "section_number";


        public ConfigsFragment() {
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_configs, container, false);
            return rootView;
        }


        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
        }

        @Override
        public void onDetach() {
            super.onDetach();
        }
    }


    //endregion

    //region PAGER ADAPTER
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
                    return new ProfileMainFragment();
                case 1:
                    return new ConfigsFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "About You!";
                case 1:
                    return "Configurations & Notes";
            }
            return null;
        }
    }
    //endregion
}

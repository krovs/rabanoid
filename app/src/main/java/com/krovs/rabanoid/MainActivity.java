package com.krovs.rabanoid;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.krovs.rabanoid.app.AppController;
import org.json.JSONArray;

public class MainActivity extends AppCompatActivity
{
    //json array response url
    private String urlJsonArrayCor = "http://feluran.com/rabapi/cordoba";
    private String urlJsonArrayRab = "http://feluran.com/rabapi/rabanales";
    private String urlJsonArrayAlc = "http://feluran.com/rabapi/alcolea";


    private static String TAG = MainActivity.class.getSimpleName();


    ViewPager viewPager;
    MyPagerAdapter myPagerAdapter;

    public Boolean sat = false;

    SharedPreferences sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        AdView mAdView = (AdView)findViewById(R.id.adView);
        AdRequest request = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                 //.addTestDevice("your phone")  // My Nexus5 test phone
                .build();
        mAdView.loadAd(request);


        sharedPref = getSharedPreferences("rabaprefs", Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.pager);
        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());

        if(toolbar!=null)
            setSupportActionBar(toolbar);

        viewPager.setAdapter(myPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);

        firstRun();
    }



    public void firstRun()
    {
        if(sharedPref.getBoolean("firstRun", true))
        {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("firstRun", false);
            editor.apply();

            refreshData();
        }
    }




    //uses the singleton volley class making a request, if there is a response, saves it to sharedpreferences
    public void getJsonApi(final String place, String placeurl, final int fragmentposition)
    {
        JsonArrayRequest req = new JsonArrayRequest(placeurl,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    //if the api has changed, or the sprefs doesnt exists, saves the new data in shared prefs
                    if(sharedPref.getString(place, null) == null || !response.toString().equals(sharedPref.getString(place, null)))
                    {
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(place, response.toString());
                        editor.apply();

                        refreshFragment(fragmentposition);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }


    //refresh data in each list if there is internet access
    public void refreshData()
    {
        if(isConnected(getApplicationContext()))
        {
            Toast.makeText(getApplicationContext(), getString(R.string.toast_updating), Toast.LENGTH_SHORT).show();
            getJsonApi("cordoba", urlJsonArrayCor, 0);
            getJsonApi("rabanales", urlJsonArrayRab, 1);
            getJsonApi("alcolea", urlJsonArrayAlc, 2);
        }
        else
            Toast.makeText(getApplicationContext(), getString(R.string.toast_nointernet), Toast.LENGTH_SHORT).show();
    }


    //takes the fragment created with the viewpager, takes the listadapter, clears it
    //and repopulates it from sharedprefs
    public void refreshFragment(int fragmentposition)
    {
        //we need to notify the changes to the fragments
        String ftag = myPagerAdapter.getFragmentTag(viewPager.getId(), fragmentposition);
        Fragment_Place fp = (Fragment_Place)getSupportFragmentManager().findFragmentByTag(ftag);
        //fragment could be null yet...
        if(fp != null) {
            //for notifydatasetchanged to work, we need to modify the adapter, so we clear it before.
            //then repopulate the arraylist from updated sharedprefs and notify the changes
            fp.myListAdapter.clear();
            fp.getFromShprefs();
            fp.sortRides();
            fp.myListAdapter.notifyDataSetChanged();
        }
    }





    public static class MyPagerAdapter extends android.support.v4.app.FragmentPagerAdapter
    {
        private static int NUM_ITEMS = 3;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return Fragment_Place.newInstance("cordoba");
                case 1:
                    return Fragment_Place.newInstance("rabanales");
                case 2:
                    return Fragment_Place.newInstance("alcolea");
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            switch (position)
            {
                case 0:
                    return "Cordoba";
                case 1:
                    return "Rabanales";
                case 2:
                    return "Alcolea";
                default:
                    return null;
            }

        }

        //we need this method to get the fragment tag
        private String getFragmentTag(int viewPagerId, int fragmentPosition)
        {
            return "android:switcher:" + viewPagerId + ":" + fragmentPosition;
        }
    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            //refresh button for retrieve json data from remote server
            case R.id.action_refresh:
                //Toast.makeText(getApplicationContext(), "Checking API", Toast.LENGTH_SHORT).show();
                refreshData();

                return true;

            case R.id.action_sat:
                //Toast.makeText(getApplicationContext(), "Switching", Toast.LENGTH_SHORT).show();
                if(sat) {
                    sat = false;
                    item.setChecked(false);
                }
                else {
                    sat = true;
                    item.setChecked(true);
                }


                for(int i=0; i<myPagerAdapter.getCount(); i++)
                {
                    refreshFragment(i);
                }

                return true;


            case R.id.action_about:

                showAlertDialog(getString(R.string.menu_about), getString(R.string.text_about));

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void showAlertDialog(String title, String content)
    {
        FragmentManager fm = getSupportFragmentManager();
        Fragment_Dialog fd = new Fragment_Dialog().newInstance(title, content);
        fd.show(fm, "fragment alert");
    }

    public static boolean isConnected(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}

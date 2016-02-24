package com.krovs.rabanoid;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;

public class Fragment_Place extends ListFragment
{
    //place can be cordoba 0, rabanales 1, alcolea 2
    String place;
    ArrayList<Ride> rides = new ArrayList<>();

    public MyListAdapter myListAdapter;

    public static Fragment_Place newInstance(String place)
    {
        Fragment_Place f = new Fragment_Place();
        Bundle args = new Bundle();
        args.putString("place", place);
        f.setArguments(args);
        return f;
    }

    public Fragment_Place(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            place = getArguments().getString("place");
        }

        //get all ride objects and order them
        //if we don't get from sharedprefs one time here,
        //the listview will be empty and uninitialized.
        //better here than oncreateview so its only initialized once
        getFromShprefs();
        sortRides();
    }

    public void sortRides()
    {
        Collections.sort(rides, new Ridetimescomparator());
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_place,container,false);

        ListView listView = (ListView)view.findViewById(android.R.id.list);

        myListAdapter = new MyListAdapter(getActivity(), rides);

        listView.setAdapter(myListAdapter);

        return view;
    }






    //gets and convert to arraylist all ride objects from shared prefs
    public void getFromShprefs()
    {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("rabaprefs", Context.MODE_PRIVATE);
        String prefs = sharedPref.getString(place, null);

        JSONArray response = null;

        if(prefs != null) {
            try {
                response = new JSONArray(prefs);
            } catch (JSONException e) {
                Log.d("error", e.toString());
            }

            try {
                for (int i=0; i<response.length(); i++)
                {
                    JSONObject row = (JSONObject)response.get(i);

                    String time = row.getString("time");
                    String train = row.getString("train");
                    String bus = row.getString("bus");
                    String spec = row.getString("spec");
                    String sat = row.getString("sat");

                    //select only saturdays or weekly
                    if ( !((MainActivity)getActivity()).sat )
                    {
                        if( sat.equals("0"))
                            rides.add(new Ride(time, train, bus, spec, sat));
                    }
                    else if ( ((MainActivity)getActivity()).sat )
                    {
                        if( sat.equals("1"))
                            rides.add(new Ride(time, train, bus, spec, sat));
                    }


                }
            } catch (JSONException e) {
                Log.d("error", e.toString());
            }
        }
    }


    //comparator for time comparing
    public class Ridetimescomparator implements Comparator<Ride>
    {
        public int compare(Ride left, Ride right) {
            return left.time.compareTo(right.time);
        }
    }





}

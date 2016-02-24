package com.krovs.rabanoid;


import android.content.Context;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MyListAdapter extends ArrayAdapter<Ride>
{

    //viewholder pattern
    private static class ViewHolder
    {
        TextView time;
        TextView trans;
    }

    public MyListAdapter(Context context, ArrayList<Ride> rides)
    {
        super(context, -1, rides);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        Ride ride = getItem(position);

        ViewHolder viewHolder;

        if(convertView == null)
        {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listrow, parent, false);
            viewHolder.time = (TextView)convertView.findViewById(R.id.rowtime);
            viewHolder.trans = (TextView)convertView.findViewById(R.id.rowtransp);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.time.setText(ride.time);
        viewHolder.trans.setText(ride.trans);



        return convertView;
    }

}

package com.deadline.core;

import java.util.ArrayList;
import java.util.Calendar;

import com.deadline.data.android.Data;

import android.R.color;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class DeadlineArrayAdapter extends ArrayAdapter<Data> {
	// Some code in this class borrowed from the examples at:
	// http://www.javacodegeeks.com/2013/09/android-listview-with-adapter-example.html
	// Mostly from the ArrayAdapterItem.java example
    Context mContext;
    int layoutResourceId;  

    ArrayList<Data> deadlines = null;
    ArrayList<Data> fitems = null;
    
    DateFormatter dateFormatter = new DateFormatter();
    private final Class STANARD_ROW_CLASS;

    public DeadlineArrayAdapter(Context mContext, int layoutResourceId, ArrayList<Data> data_list, Calendar maxDateTimeToShow) {
    	this(mContext,layoutResourceId,data_list);
    }
    public DeadlineArrayAdapter(Context mContext, int layoutResourceId, ArrayList<Data> data_list) {
    	super(mContext,layoutResourceId,data_list);
    	this.layoutResourceId = layoutResourceId;
    	this.mContext = mContext;
    	this.deadlines = data_list;
    	this.fitems = data_list;
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
		View convertView = inflater.inflate(this.layoutResourceId, null);
		STANARD_ROW_CLASS = convertView.getClass();
    }
    public View getView (int position, View convertView, ViewGroup parent) {
    	View row = convertView;
    	
    	DataHolder holder = null; 
    	if (convertView==null) {
    		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
    		convertView = inflater.inflate(this.layoutResourceId, null);
    		
    		row = convertView;
    		holder = new DataHolder ();
    		holder.imgIcon = (ImageView) row.findViewById(R.id.imgIcon);
    		holder.txtTitle = (TextView) row.findViewById(R.id.row_txtTitle);
    		holder.txtInfo = (TextView) row.findViewById(R.id.row_info_txtView);
    		holder.event = this.deadlines.get(position);
    		
    		row.setTag(holder);
    	} else {
    		holder = (DataHolder)row.getTag();
    	}    	
    	Data dataItem = this.deadlines.get(position);
    	holder.txtTitle.setText(dataItem.name);
    	holder.txtInfo.setText(dateFormatter.formatDate(dataItem.end));
    	
    	Calendar tomorrow = Calendar.getInstance();
    	tomorrow.add(Calendar.DAY_OF_YEAR, 1);
    	Calendar hour1 = Calendar.getInstance();
    	hour1.add(Calendar.HOUR, 1);
    	Calendar hourLimit = Calendar.getInstance();
    	hourLimit.add(Calendar.HOUR_OF_DAY, 49);
    	Calendar week = Calendar.getInstance();
    	week.add(Calendar.DAY_OF_YEAR, 8);
    	Calendar week2 = Calendar.getInstance();
    	week2.add(Calendar.DAY_OF_YEAR, 15);
    	Calendar month = Calendar.getInstance();
    	month.add(Calendar.MONTH, 1);
    	month.add(Calendar.DAY_OF_YEAR, 1);
    	Calendar year = Calendar.getInstance();
    	year.add(Calendar.YEAR, 1);   
    	year.add(Calendar.DAY_OF_YEAR, 1);
		holder.txtTitle.setTextAppearance(mContext, R.style.ListTitleFontLight);
		holder.txtInfo.setTextAppearance(mContext, R.style.ListInfoFontLight);
    	if (dataItem.end.before(hourLimit)) {
    		row.setBackgroundResource(color.holo_red_light);    	
    	}  else if (dataItem.end.before(week)) {
    		row.setBackgroundResource(color.holo_green_light);	    		
    	}  else if (dataItem.end.before(week2)) {
    		row.setBackgroundResource(color.holo_blue_bright);
    	}  else {
    		row.setBackgroundResource(color.background_dark);
    		holder.txtTitle.setTextAppearance(mContext, R.style.ListTitleFontDark);
    		holder.txtInfo.setTextAppearance(mContext, R.style.ListInfoFontDark);
    	}
    	return row;
    }
    public static Data getAttatchedDataItem (View view) {
    		DataHolder holder = (DataHolder) view.getTag();
    		return holder.event;    	   	
    }
    static class DataHolder {
    	Data event;
    	ImageView imgIcon;
    	TextView txtTitle;
    	TextView txtInfo;
    }
}
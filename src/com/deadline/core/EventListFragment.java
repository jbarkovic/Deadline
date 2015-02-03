package com.deadline.core;

import java.util.Calendar;

import com.deadline.data.android.DataManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class EventListFragment extends ListFragment{

	private String BUNDLE_DATAMANAGER_ID = "bundledatamanagerid";
	private Calendar endDate = null;
	boolean started = false;
	private DeadlineArrayAdapter allAdapter;
	protected DataManager deadlines;
	
	protected interface RefreshListListener {
		public void refreshLists (EventListFragment frag);
	}		
	public void setDataManager (DataManager manager) {
		this.deadlines = manager;
	}
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(BUNDLE_DATAMANAGER_ID, this.deadlines);
     }
	public void onListItemSelected(int position) {
		Toast toast;
		try {
			DataParcel.mData = this.deadlines.get(position);			
			DataParcel.dManager = this.deadlines; 

			Intent i = new Intent(getActivity(), DeadlineEditActivity.class);
			i.putExtra("makeNewEvent", false);
			startActivityForResult(i,420);
		} catch (Exception e) {
			toast = Toast.makeText(getActivity(), "Could not select item",Toast.LENGTH_SHORT);
			toast.show();
			e.printStackTrace();
		}
	}
	private void updateArrayList () {
		this.deadlines.removeOld();
		this.deadlines.purgeDeleted();
	}
	private void notifyLists () {
		if (this.allAdapter == null) {System.out.println("Null Adapter - EventListFragment - notifyLists");return;}
		this.allAdapter.notifyDataSetChanged();
	}
	public void refresh () {
		updateArrayList();
		notifyLists ();		
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Toast toast;			
		if (savedInstanceState != null)	{
			deadlines = savedInstanceState.getParcelable(BUNDLE_DATAMANAGER_ID);
		}
		if (deadlines == null) {
			toast = Toast.makeText(getActivity(), "No Data Found",Toast.LENGTH_SHORT);
			toast.show();
		}
		super.onActivityCreated(savedInstanceState);
		if (this.endDate != null) this.endDate.add(Calendar.MINUTE, 1);		
		updateArrayList ();
		this.allAdapter = new DeadlineArrayAdapter(this.getActivity(),
				R.layout.list_row_item_deadlines, this.deadlines);
		ListView listView = getListView();
		setListAdapter(this.allAdapter);
		listView.setBackgroundResource(android.R.color.background_dark);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener () {
			@Override
			public void onItemClick(AdapterView<?> arg0, View row,
					int position, long id) {		
				onListItemSelected(position);
			}
		});
		refresh();
		started = true;
	}	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		refresh ();
	}
}

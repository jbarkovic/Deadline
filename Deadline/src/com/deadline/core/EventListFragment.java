package com.deadline.core;

import java.util.ArrayList;
import java.util.Calendar;

import com.deadline.data.android.Data;
import com.deadline.data.android.DataManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class EventListFragment extends ListFragment{

	private String BUNDLE_DATAMANAGER_ID = "bundledatamanagerid";
	protected interface RefreshListListener {
		public void refreshLists (EventListFragment frag);
	}
		
	protected DataManager deadlines;	

	public void setDataManager (DataManager manager) {
		this.deadlines = manager;
	}
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(BUNDLE_DATAMANAGER_ID, (ArrayList<? extends Parcelable>) this.deadlines);
     }
	public void onListItemSelected(int position) {
		Toast toast;
		try {
			DataParcel.mData = this.data_list.get(position);
			DataParcel.dManager = this.deadlines; 

			Intent i = new Intent(getActivity(), DeadlineEditActivity.class);
			i.putExtra("makeNewEvent", false);
			startActivityForResult(i,420);
		} catch (Exception e) {
			toast = Toast.makeText(getActivity(), "Could not select item",Toast.LENGTH_LONG);
			toast.show();
			e.printStackTrace();
		}
	}
	private Calendar endDate = null;
	boolean started = false;

	private ArrayList<Data> data_list = new ArrayList<Data>();
	private DeadlineArrayAdapter allAdapter;
	
	private void updateArrayList () {
		Calendar current = Calendar.getInstance();
		current.add(Calendar.HOUR_OF_DAY, -1);
		//System.out.println("ListMod BEGIN: ");
		data_list.clear();
		for (int i=0;deadlines != null && i<deadlines.size();i++) {
			Data data = deadlines.get(i); 
			//System.out.println("DATA NAME: " + data.name);			
			if (data.getCompleted()) {
				System.out.println("COMPLETED: " + data.name);
				//data_list.remove(data);
			} else if (data.start.before(data.end)) {
				if (this.endDate == null) {
					if (!data_list.contains(data)) {
						data_list.add(data);							
						//	System.out.println("\tAdding Item NAME: " + data.name + " to List: " + tabName + " in AllCheck");
					}
				} else if (data.end.before(this.endDate)) {
					if (!data_list.contains(data) && !data.getCompleted()) {
						//System.out.println("\tAdding Item NAME: " + data.name + " to List: " + tabName + " in LimitCheck");
						data_list.add(data);
					}
				} else {
					data_list.remove(data);
				}
			}
		}
		//System.out.println("ListMod END: deadlines.size(): " + deadlines.size() + " data_list.size(): " + data_list.size());
	}
	private void notifyLists () {
		if (this.allAdapter == null) {System.out.println("Null Adapter - EventListFragment - notifyLists");return;}
		this.allAdapter.notifyDataSetChanged();
	}
	public void refresh () {
		//if (!started) return;
		updateArrayList();
		notifyLists ();		
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Toast toast;			
		if (savedInstanceState != null)
            deadlines = savedInstanceState.getParcelableArrayList(BUNDLE_DATAMANAGER_ID);
		if (deadlines == null) {
			toast = Toast.makeText(getActivity(), "No data found",Toast.LENGTH_LONG);
			toast.show();
		}
		super.onActivityCreated(savedInstanceState);
		if (this.endDate != null) this.endDate.add(Calendar.MINUTE, 1);		
		updateArrayList ();
		this.allAdapter = new DeadlineArrayAdapter(this.getActivity(),
				R.layout.list_row_item_deadlines, data_list);
		ListView listView = getListView();
		setListAdapter(this.allAdapter);
		listView.setBackgroundResource(android.R.color.background_dark);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener () {
			@Override
			public void onItemClick(AdapterView<?> arg0, View row,
					int position, long id) {
				System.out.println("=========== Click");		
				onListItemSelected(position);
			}
		});
		refresh();
		started = true;
	}	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		refresh ();
//		boolean wasNewEvent = data.getBooleanExtra("isNewEvent", true);		
//		Data newData = DataParcel.mData;
//		Book checkBook = data.getParcelableExtra(MainTabActivity.BOOK_KEY);
//		if (this.ourBook == null || !checkBook.equals(this.ourBook)) return;
//		EventModification recievedInfo = DataParcel.mod;
//		EventModification modInfo = new EventModification();
//		if (recievedInfo != null) modInfo = recievedInfo;
//		else modInfo.modification = (wasNewEvent) ? EventModification.ModType.NEW : EventModification.ModType.CHANGED;
//		Toast toast;
//		if (newData == null) {
//			toast = Toast.makeText(getActivity(), "Could not complete task", Toast.LENGTH_LONG);
//			toast.show();
//			return;
//		} else {
//			switch (modInfo.modification) {		
//			case NEW : {
//				toast = Toast.makeText(getActivity(), "Created Event", Toast.LENGTH_LONG);
//				deadlines.add(newData);	
//				break;
//			} case DELETED :  {
//				toast = Toast.makeText(getActivity(), "Deleted Event" , Toast.LENGTH_LONG);
//				deadlines.purgeDeleted();
//				break;
//			}
//			case CHANGED : {
//				toast = Toast.makeText(getActivity(), "Changed Event" , Toast.LENGTH_LONG);
//				break;
//			}
//			default : { 
//				toast = Toast.makeText(getActivity(), "Aborted", Toast.LENGTH_LONG);
//				toast.show();
//				return;
//			}
//			}			
//			try {
//				deadlines.commit();
//			} catch (DataCommitException e) {
//				e.printStackTrace();
//			}
//		}
//		
//		System.out.println("NAME: " + newData.name);
//				
//		toast.show();
//		refresh ();

	}
}

package com.deadline.core;

import com.deadline.core.EditEventDialogFragment.EditEventDialogListener;
import com.deadline.data.android.Data;
import com.deadline.data.android.DataCommitException;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class DeadlineEditActivity extends ActionBarActivity implements EditEventDialogListener , DateReturnListener {
	private Data ourEvent;
	private boolean createNewEvent = true;
	EditEventDialogFragment editFrag = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deadline_edit);
		Intent recievedIntent = getIntent();
		this.createNewEvent = recievedIntent.getBooleanExtra("makeNewEvent", true);		
		this.ourEvent = (Data) DataParcel.mData;
		DataParcel.mod.modification = null;
		if (this.ourEvent == null) {
			this.ourEvent = new Data();
			this.createNewEvent = true;
		}
		if (this.createNewEvent ) {
			System.out.printf("Creating new Item...");
			this.setTitle("New Event");
		} else {
			System.out.printf("Editing old Item... " + "OLD_NAME :" + this.ourEvent.name);
			this.setTitle("Edit Event");
		}
		this.editFrag = new EditEventDialogFragment (this.ourEvent, createNewEvent);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, this.editFrag).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.deadline_edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_new_deadline,
					container, false);
			return rootView;
		}
	}
	
	@Override
	public void finish() {
	  // Prepare data intent 
		Toast toast;
	  Intent data = new Intent();
	  data.putExtra("isNewEvent", this.createNewEvent);
	  if (DataParcel.bManager != null) {
			try {
				DataParcel.bManager.commit();
			} catch (DataCommitException e) {
				toast = Toast.makeText(this, "Error Saving Data", Toast.LENGTH_LONG);
				toast.show();
				e.printStackTrace();
				return;
			}	 
	  }
	  DataParcel.mod.modification = EventModification.ModType.CANCELED;
	  setResult(RESULT_OK, data);
	  super.finish();
	} 

	@Override
	public void onDeleteEvent(EditEventDialogFragment dialog) {
		if (this.ourEvent != null) this.ourEvent.markForDeletion();
		DataParcel.mod.modification = EventModification.ModType.DELETED;
		this.onDoneEditingEvent(dialog);
	}

	@Override
	public void onDoneEditingEvent(EditEventDialogFragment dialog) {
		if (this.createNewEvent) {
			dialog.ourDManager.add(this.ourEvent);
		}
		finish();
	}

	@Override
	public void onDateReturn() {
		// TODO Auto-generated method stub
		if (this.editFrag != null) this.editFrag.updateNewEventDates ();
	}
}

package com.deadline.core;

import java.text.SimpleDateFormat;

import com.deadline.data.android.Data;
import com.deadline.data.android.DataManager;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import android.widget.CheckBox;

public class EditEventDialogFragment extends DialogFragment implements DateReturnListener, OnItemSelectedListener {
	private EditText fromDateEditText;
	private EditText fromTimeEditText;
	private EditText toDateEditText;
	private EditText toTimeEditText;
	private EditText newNameEditText;
	private EditText newInfoEditText;
	
	private CheckBox completedCheckBox;
	
	protected DataManager ourDManager = null;
	private SeekBar prioritySeekBar;
	private SeekBar difficultySeekBar;
	private SimpleDateFormat newEventFormat_Date = new SimpleDateFormat("MM/dd/yyyy");
	private SimpleDateFormat newEventFormat_Time = new SimpleDateFormat("HH:mm");
	
	private boolean createNewEvent = true;
	private Data ourEvent = new Data ();
	
	protected static int[] newEventDateCompletion = new int[4];
	public EditEventDialogFragment () {
		this.createNewEvent = true;
	}
	public EditEventDialogFragment (Data existingEvent, boolean createNewEvent) {
		ourEvent = existingEvent;
		this.createNewEvent = createNewEvent;
	}
	
    public interface EditEventDialogListener {
        public void onDeleteEvent(EditEventDialogFragment dialog);
        public void onDoneEditingEvent(EditEventDialogFragment dialog);
    }  
    public void cancelCreatingNewEvent () {
    	getActivity().finish();
    }
    
    // Use this instance of the interface to deliver action events
    EditEventDialogListener mEditEventDialogListener;
    
    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mEditEventDialogListener = (EditEventDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }   
    
    protected Data getChangedEvent () {
    	return ourEvent;
    }
    
    public boolean isCreateNewEventDialog () {
    	return this.createNewEvent;
    }
	public void showDatePickerDialogTo(Data event) {
		DialogFragment newFragment = new DatePickerFragment(true, event.end);
	    newFragment.show(getFragmentManager(), "datePickerEnd");
	}
	public void showTimePickerDialogTo(Data event) {
	    DialogFragment newFragment = new TimePickerFragment(true, event.end);
	    newFragment.show(getFragmentManager(), "timePickerEnd");
	}
	public void updateNewEventDates () {
		if (ourEvent.end != null) {
			toDateEditText.setText(newEventFormat_Date.format(ourEvent.end.getTimeInMillis()));
			toTimeEditText.setText(newEventFormat_Time.format(ourEvent.end.getTimeInMillis()));
		}
	}
	public void updateText () {
		if (ourEvent.name != null) {
			newNameEditText.setText(ourEvent.name);
		}
		if (ourEvent.info != null) {
			newInfoEditText.setText(ourEvent.info);
		}
	}
	public void updateSliders () {
		prioritySeekBar.setProgress(Math.max(0, ourEvent.getPriority() % (prioritySeekBar.getMax()+1)));
		difficultySeekBar.setProgress(Math.max(0, ourEvent.getDifficulty() % (difficultySeekBar.getMax()+1)));
	}
	public void finishedCreatingNewEvent (View v) {
		int duration = Toast.LENGTH_LONG;
		Toast toast;

		if (newNameEditText != null && newInfoEditText != null) {
			if (!newNameEditText.getText().toString().equals("")) {
				ourEvent.name = newNameEditText.getText().toString();
				newEventDateCompletion[2] += 1;
			} else {	
				toast = Toast.makeText(getActivity (), "Invalid Name", duration);
				toast.show();
				return;
			}
			ourEvent.info = newInfoEditText.getText().toString();
			newEventDateCompletion[3] += 1;
		}		
		if (newEventDateCompletion[0] !=0 && newEventDateCompletion[1] !=0 && newEventDateCompletion[2] !=0 && newEventDateCompletion[3] !=0) {
			//  --> Successful Completion So far
			ourEvent.setDifficulty(difficultySeekBar.getProgress());
			ourEvent.setPriority(prioritySeekBar.getProgress());
			ourEvent.setCompleted(completedCheckBox.isChecked());
			
			if (this.createNewEvent) toast = Toast.makeText(getActivity (), "Event created", duration);
			else toast = Toast.makeText(getActivity (), "Event changed", duration);
			toast.show();
		} else {
			toast = Toast.makeText(getActivity (), "Error " + newEventDateCompletion[0] + "" + newEventDateCompletion[1] + "" + newEventDateCompletion[2] + "" + newEventDateCompletion[3], duration);
			toast.show();
			return;
		}
		mEditEventDialogListener.onDoneEditingEvent (EditEventDialogFragment.this);
		//dismiss();
	}
	private void confirmDelete () {
		mEditEventDialogListener.onDeleteEvent(this);
	}
	public void onDeleteSelect (View v) {
		 AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	       	builder.setMessage("Are you sure you want to delete this Event?")
	               .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   confirmDelete ();
	                   }
	               })
	               .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   dialog.cancel ();
	                   }
	               });
	        // Create the AlertDialog object and return it
	        AlertDialog al = builder.create();
	        al.show();
	}
	@Override
    public void onItemSelected(AdapterView<?> parent, View view, 
            int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
		this.ourDManager = (DataManager) parent.getItemAtPosition(pos);
    }
	@Override

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_new_deadline, container,
				false);	
		toDateEditText = (EditText) rootView.findViewById(R.id.newValue_toDate);
		toTimeEditText = (EditText) rootView.findViewById(R.id.newValue_toTime);
		newNameEditText = (EditText) rootView.findViewById(R.id.newValue_name);
		newInfoEditText = (EditText) rootView.findViewById(R.id.newValue_description);
		
		prioritySeekBar = (SeekBar) rootView.findViewById(R.id.importanceSeekBar_EventDialog);
		difficultySeekBar = (SeekBar) rootView.findViewById(R.id.difficultySeekBar_EventDialog);
		final Button buttonOk = (Button) rootView.findViewById(R.id.button_newDialog_ok); 
		final Button buttonDelete = (Button) rootView.findViewById(R.id.button_newDialog_delete); 
		final Button buttonCancel = (Button) rootView.findViewById(R.id.button_newDialog_cancel); 
		
		Spinner bookList = (Spinner) rootView.findViewById(R.id.BooksListSpinner);
		completedCheckBox = ((CheckBox) rootView.findViewById(R.id.checkBox_completed));
		
		completedCheckBox.setChecked(ourEvent.getCompleted());
		
		if (this.createNewEvent) {
			completedCheckBox.setVisibility(View.INVISIBLE);
			completedCheckBox.setEnabled(false);
						
			buttonDelete.setEnabled(false);
			buttonDelete.setVisibility(View.GONE);			
			ArrayAdapter<DataManager> adapt = new ArrayAdapter<DataManager>  (getActivity(),android.R.layout.simple_spinner_item,DataParcel.bManager);				
			bookList.setAdapter((SpinnerAdapter) adapt);
			bookList.setOnItemSelectedListener(this);
			this.ourDManager = (DataManager) bookList.getItemAtPosition(0);
		} else {
			bookList.setVisibility(View.INVISIBLE);
			bookList.setEnabled(false);
		}
		buttonOk.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finishedCreatingNewEvent(buttonOk);
				
			}
		});
		buttonDelete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onDeleteSelect(buttonOk);				
			}
		});
		buttonCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {				
				cancelCreatingNewEvent ();
			}
		});
		toDateEditText.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {				
				showDatePickerDialogTo(ourEvent);
			}
		});
		toTimeEditText.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {				
				showTimePickerDialogTo(ourEvent);
			}
		});		
	    updateNewEventDates();
	    updateText ();
	    updateSliders();
	    newEventDateCompletion[0]++;
	    newEventDateCompletion[1]++;
		return rootView;
	}
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setView(inflater.inflate(R.layout.fragment_new_deadline, null));
     
	    return builder.create();
	}
	@Override
	public void onDateReturn() {
	    updateNewEventDates();		
	}
}

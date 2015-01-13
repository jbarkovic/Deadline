package com.deadline.core;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.deadline.data.android.Data;

public class DatePickerFragment extends DialogFragment
implements DatePickerDialog.OnDateSetListener {
	private boolean isStart = true;
	private Calendar cal; 
	DateReturnListener mListener;
	public DatePickerFragment (boolean isStart, Calendar calendar) {
		isStart = true;
		cal = calendar;
	}
	
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (DateReturnListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement DateReturnListener");
        }
    }
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		mListener.onDateReturn();
//		if (ourEvent == null) {
//			ourEvent = new Data();
//		}
//		Calendar date;
//		if (this.getTag().equals("datePickerEnd")) {
//			date = ourEvent.end;
//		} else {
//			date = ourEvent.start;
//		}
//		date.set(Calendar.YEAR, year);
//		date.set(Calendar.MONTH, month);
//		date.set(Calendar.DAY_OF_MONTH, day);
//		newEventDateCompletion[0] += 1;
//		updateNewEventDates ();
	}
}

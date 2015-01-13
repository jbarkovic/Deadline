package com.deadline.core;

import java.util.Calendar;

import com.deadline.core.EditEventDialogFragment.EditEventDialogListener;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment
implements TimePickerDialog.OnTimeSetListener {
	boolean isStart = true;
	Calendar cal;
	DateReturnListener mListener;
	public TimePickerFragment (boolean isStart, Calendar calendar) {
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
		// Use the current time as the default values for the picker
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);

		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, hour, minute,
				DateFormat.is24HourFormat(getActivity()));
	}

	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//		Calendar date;
//		if (this.getTag().equals("timePickerEnd")) {
//			date = ourEvent.end;
//		} else {
//			date = ourEvent.start;
//		}
//		date.set(Calendar.HOUR_OF_DAY, hourOfDay);
//		date.set(Calendar.MINUTE, minute);
//		date.set(Calendar.SECOND, 0);
//		date.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		mListener.onDateReturn();
	}
}
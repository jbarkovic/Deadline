package com.deadline.core.datetime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class DateFormatter {
	
	private final String TEXT_YESTERDAY = "Yesterday";
	private final String TEXT_TOMORROW = "Tomorrow";
	private final String TEXT_TODAY = "Today";
	private final String TEXT_RECENT = "Just now";
	private final String TEXT_NOW = "Now";
	private final String TEXT_AGO = "ago";
	private final String TEXT_AT = "at";
	private final String TEXT_IN = "in";
	private final String TEXT_LAST = "Last";
	private final String TEXT_NEXT = "Next";
	private final String TEXT_THIS = "This";
	private final String TEXT_WAS_DUE = "Was due";
	private final String TEXT_MINUTES_PLURAL = "mins";
	private final String TEXT_MINUTES_SINGULAR = "min";
	private final String TEXT_HOUR_PLURAL = "hrs";
	private final String TEXT_HOUR_SINGULAR = "hr";
	private final String TEXT_DEFAULT = "I can't tell when, sorry.";
	private final Locale DEFAULT_LOCALE = Locale.getDefault();
	
	private SimpleDateFormat textMonthNumberDay = new SimpleDateFormat ("MMMM dd", DEFAULT_LOCALE); // Sept 21
	private SimpleDateFormat textMonthNumberDayYear = new SimpleDateFormat ("MMMM dd, yyyy", DEFAULT_LOCALE); // Sept 21, 2014
	private SimpleDateFormat textDayOfWeek = new SimpleDateFormat ("EEE", DEFAULT_LOCALE); // Sun
	private SimpleDateFormat textMonthDayOfWeekDay = new SimpleDateFormat ("EEE MMM dd", DEFAULT_LOCALE); // Sun Sept 21
	private SimpleDateFormat textSecondsOneDigit = new SimpleDateFormat ("s", DEFAULT_LOCALE); // in 1 second
	private SimpleDateFormat textSecondsTwoDigits = new SimpleDateFormat ("ss", DEFAULT_LOCALE); // in 33 seconds
	private SimpleDateFormat textMinutesOneDigit = new SimpleDateFormat ("m", DEFAULT_LOCALE); // in 1 minute
	private SimpleDateFormat textMinutesTwoDigits = new SimpleDateFormat ("mm", DEFAULT_LOCALE); // in 33 minutes
	private SimpleDateFormat textMinutesSeconds = new SimpleDateFormat ("mm:ss", DEFAULT_LOCALE); // 01:23
	private SimpleDateFormat textHoursMinutes = new SimpleDateFormat ("hh:mm a", DEFAULT_LOCALE); // 01:23

    public String formatDate (Calendar timeInstance) {
    	String out = TEXT_DEFAULT;

    	Calendar instance = Calendar.getInstance();
    	instance.setTime(timeInstance.getTime());
    	DateDifference diff = new DateDifference(instance, Calendar.getInstance());
    	Date date = instance.getTime();
    	

    	// Finally decide what to say
    	if (diff.isFuture) {
    		out = textMonthNumberDayYear.format(date);
    	} else {
    		out = TEXT_WAS_DUE + " " + textMonthNumberDayYear.format(date);
    	}
    	if (diff.totalNumberOf(DateDifference.MONTHS) < 3) { // is within the same Calendar year    
    		if (diff.totalNumberOf(DateDifference.DAYS) > 6 && diff.totalNumberOf(DateDifference.DAYS) < 14) {
    			if (diff.isFuture) out = TEXT_NEXT + " " + textMonthDayOfWeekDay.format(date) + " " + TEXT_AT + " " + textHoursMinutes.format(date);    			
    		} else if (diff.totalNumberOf(DateDifference.DAYS) <= 6 && diff.totalNumberOf(DateDifference.DAYS) > 1) {
    			if (diff.isFuture) out =  TEXT_THIS + " " + textMonthDayOfWeekDay.format(date) + " " + TEXT_AT + " " + textHoursMinutes.format(date);
    			out = TEXT_LAST + " " + textMonthDayOfWeekDay.format(date) + " " + TEXT_AT + " " + textHoursMinutes.format(date);
    		} else if (diff.totalNumberOf(DateDifference.DAYS) <= 1){ // Within 24 hours
    			if (diff.totalNumberOf(DateDifference.DAYS) == 0) out = TEXT_TODAY;
    			else if (diff.isFuture) out = TEXT_TOMORROW;
				else out = TEXT_YESTERDAY;
    			out += (" " + TEXT_AT + " " + textHoursMinutes.format(date) +" (" + TEXT_IN + " " + diff.remainderNumberOf(DateDifference.HOURS) + TEXT_HOUR_PLURAL + ")");
    			if (diff.totalNumberOf(DateDifference.HOURS) <= 2){ // less than 120 minutes							
    				if (diff.totalNumberOf(DateDifference.MINUTES) < 1) {
    					if (diff.isFuture) out = TEXT_NOW;
    					else out = TEXT_RECENT;
    				} else if (diff.totalNumberOf(DateDifference.MINUTES) == 1) {
    					if (diff.isFuture) out = TEXT_IN + " 1 " + TEXT_MINUTES_SINGULAR;
    					else out = " 1 " + TEXT_MINUTES_SINGULAR + " " + TEXT_AGO;
    				} else {
    					String hours   = (diff.totalNumberOf(DateDifference.HOURS) == 1) ? TEXT_HOUR_SINGULAR : TEXT_HOUR_PLURAL;
    					String minutes = (diff.totalNumberOf(DateDifference.MINUTES) == 1) ? TEXT_MINUTES_SINGULAR : TEXT_MINUTES_PLURAL;
    					if (diff.isFuture) out = TEXT_IN + " " + (diff.remainderNumberOf(DateDifference.HOURS) + " " + hours + " " + diff.remainderNumberOf(DateDifference.MINUTES)) + " " + minutes;
    					else out = (diff.remainderNumberOf(DateDifference.HOURS) + " " + hours + " " + diff.remainderNumberOf(DateDifference.MINUTES)) + " " + minutes + " " + TEXT_AGO;
    				}
    			}
    		}
    	}
    	return out;
    }
}

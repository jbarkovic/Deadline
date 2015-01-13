package com.deadline.core;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DateFormatter {
	
	private final String TEXT_YESTERDAY = "Yesterday";
	private final String TEXT_TOMORROW = "Tomorrow";
	private final String TEXT_TODAY = "Today";
	private final String TEXT_RECENT = "Just now";
	private final String TEXT_NOW = "Now";
	private final String TEXT_AGO = "ago";
	private final String TEXT_AT = "at";
	private final String TEXT_IN = "in";
	private final String TEXT_SECONDS_PLURAL = "seconds";
	private final String TEXT_SECONDS_SINGULAR = "second";
	private final String TEXT_MINUTES_PLURAL = "minutes";
	private final String TEXT_MINUTES_SINGULAR = "minute";
	private final String TEXT_HOURS_PLURAL = "hours";
	private final String TEXT_DEFAULT = "Sometime";
	
	private SimpleDateFormat textMonthNumberDay = new SimpleDateFormat ("MMMM dd"); // Sept 21
	private SimpleDateFormat textMonthNumberDayYear = new SimpleDateFormat ("MMMM dd, yyyy"); // Sept 21, 2014
	private SimpleDateFormat textDayOfWeek = new SimpleDateFormat ("EEE"); // Sun
	private SimpleDateFormat textMonthDayOfWeekDay = new SimpleDateFormat ("EEE MMM dd"); // Sun Sept 21
	private SimpleDateFormat textSecondsOneDigit = new SimpleDateFormat ("s"); // in 1 second
	private SimpleDateFormat textSecondsTwoDigits = new SimpleDateFormat ("ss"); // in 33 seconds
	private SimpleDateFormat textMinutesOneDigit = new SimpleDateFormat ("m"); // in 1 minute
	private SimpleDateFormat textMinutesTwoDigits = new SimpleDateFormat ("mm"); // in 33 minutes
	private SimpleDateFormat textMinutesSeconds = new SimpleDateFormat ("mm:ss"); // 01:23
	private SimpleDateFormat textHoursMinutes = new SimpleDateFormat ("hh:mm a"); // 01:23

    public String formatDate (Calendar timeInstance) {
    	String out = TEXT_DEFAULT;

    	Calendar instance = Calendar.getInstance();
    	instance.setTime(timeInstance.getTime());
    	DateDifference diff = compareToPresent (instance);
    	
    	Date date = instance.getTime();
    	
    	// Finally decide what to say
		if (diff.isSameYear) {
			if (diff.months < 3) {
				if (diff.days < 5) { // exactly 7 days ago would be confusing
					if (diff.days < 1) {
						if (diff.hours > 2 || (diff.hours == 1 && diff.minutes > 30)) {	
							if (diff.isSameDay) {
								out = TEXT_TODAY;
							} else {
								if (diff.isFuture) out = TEXT_TOMORROW;
								else out = TEXT_YESTERDAY;
							}
							out += (" " + TEXT_AT + " " + textHoursMinutes.format(date) +"(" + TEXT_IN + " " + diff.hours + ")");
						} else { // less than 120 minutes							
							if (diff.minutes < 1) {
								out = TEXT_NOW;
							} else if (diff.minutes < 2) {
								if (diff.isFuture) out = TEXT_IN + " 1 " + TEXT_MINUTES_SINGULAR;
								else out = " 1 " + TEXT_MINUTES_SINGULAR + " " + TEXT_AGO;
							} else {
								if (diff.isFuture) out = TEXT_IN + " " + (diff.hours*60 + diff.minutes) + " " + TEXT_MINUTES_PLURAL;
								else out = "" + (diff.hours*60 + diff.minutes) + " " + TEXT_MINUTES_PLURAL + " " + TEXT_AGO;
							}
						}
					} else if (diff.isFuture){
						out = textMonthDayOfWeekDay.format(date) + " " + TEXT_AT + " " + textHoursMinutes.format(date);
					} else {
						out = textDayOfWeek.format(date) + " " + TEXT_AT + " " + textHoursMinutes.format(date);
					}
				} else {
					out = textMonthDayOfWeekDay.format(date) + " " + TEXT_AT + " " + textHoursMinutes.format(date);
				}
			} else {
				out = textMonthNumberDay.format(date);
			}
		} else {
			if (diff.years > 10 && diff.isFuture) {
				if (diff.years > 200 && diff.isFuture) {
					out = "In a galaxy far, far away";
				}
				out = "In the distant future";
			} else {
				out = textMonthNumberDayYear.format(date);
			}
		}		
    	return out;
    }
    private DateDifference compareToPresent (Calendar event) {
    		Calendar offset = Calendar.getInstance();
    		offset.setTime(event.getTime());
    		Calendar now = Calendar.getInstance();

    		int hours = 0; int minutes = 0; int days = 0; int months = 0; int years = 0; int seconds = 0;
        	boolean valid = true;
        	final int rollDelta = (offset.before(now)) ? 1 : -1;
        	final int iniCond = offset.compareTo(now);
        	
        	boolean isFuture = event.after(now);
        	boolean isSameYear = event.get(Calendar.YEAR) == now.get(Calendar.YEAR);
        	boolean isSameDay = event.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR);
    
    			// Get YEARS ---------------------------------
				while (offset.compareTo(now) == iniCond) {
					offset.add(Calendar.YEAR, rollDelta);
					years ++;
				} years--;
				offset.add(Calendar.YEAR, -rollDelta);
				
				// Get MONTHS --------------------------------
				while (offset.compareTo(now) == iniCond) {
					offset.add(Calendar.MONTH, rollDelta);
					months ++;
				} months--;
				offset.add(Calendar.MONTH, -rollDelta);
				
				// Get DAYS ----------------------------------
				while (offset.compareTo(now) == iniCond) {
					offset.add(Calendar.DAY_OF_YEAR, rollDelta);
					days ++;
				} days--;
				offset.add(Calendar.DAY_OF_YEAR, -rollDelta);
				
				// Get HOURS ---------------------------------
    			while (offset.compareTo(now) == iniCond) {
    				offset.add(Calendar.HOUR_OF_DAY, rollDelta);
    				hours++;   				
    			} hours --;
    			offset.add(Calendar.HOUR_OF_DAY, -rollDelta);
    			
    			// Get MINUTES --------------------------------
				
				while (offset.compareTo(now) == iniCond) {
					offset.add(Calendar.MINUTE, rollDelta);
					minutes++; 				
				} minutes--;
    			offset.add(Calendar.MINUTE, -rollDelta);
    			
    			// Get SECONDS --------------------------------
				
				while (offset.compareTo(now) == iniCond) {
					offset.add(Calendar.SECOND, rollDelta);
					seconds++; 				
				} seconds--;
    			offset.add(Calendar.SECOND, -rollDelta);
    		
    	DateDifference diff = new DateDifference ();
    	diff.years = Math.abs(years);
    	diff.months = Math.abs(months);
    	diff.days = Math.abs(days);
    	diff.hours = Math.abs(hours);
    	diff.minutes = Math.abs(minutes);
    	diff.seconds = Math.abs(seconds);
    	diff.isValid = valid;
    	diff.isSameYear = isSameYear;
    	diff.isSameDay = isSameDay;
    	diff.isFuture = isFuture;
    	return diff;    	
    }
    private class DateDifference {
    	int years = 0;
    	int months = 0;
    	int days = 0;
    	int hours = 0;
    	int minutes = 0;
    	int seconds = 0;
    	boolean isSameYear = true;
    	boolean isSameDay = true;
    	boolean isValid = true;
    	boolean isFuture = true;
    }
}

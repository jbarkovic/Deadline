package com.deadline.core.datetime;

import java.util.Calendar;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Years;

public class DateDifference {
	/*
	 * Stores the difference between two dates 
	 */
	private int years [] = new int [2]; 	public static final int YEARS = 0;
	private int months [] = new int [2];	public static final int MONTHS = 1;
	private int days [] = new int [2];		public static final int DAYS = 2;
	private int hours [] = new int [2];		public static final int HOURS = 3;
	private int minutes [] = new int [2];	public static final int MINUTES = 4;
	private int seconds [] = new int [2];   public static final int SECONDS = 5;
	private int [][] countedUnits = new int [][] {years,months,days,hours,minutes,seconds};
	boolean isSameYear = true;
	boolean isSameDay = true;
	boolean isFuture = true;
	
	public DateDifference (Calendar eventTime, Calendar referenceTime) {
		this.compareDates(eventTime, referenceTime);
	}
	public boolean isValid () {return true;} // May need in future
    public void compareDates (Calendar eventTime, Calendar referenceTime) { 
		Calendar refHold = (Calendar) referenceTime.clone();
		Calendar offset = (Calendar) eventTime.clone();		
		Calendar decreasingOffset = (Calendar) offset.clone();
    	
    	this.isFuture = eventTime.after(refHold);
    	this.isSameYear = eventTime.get(Calendar.YEAR) == refHold.get(Calendar.YEAR);
    	this.isSameDay = eventTime.get(Calendar.DAY_OF_YEAR) == refHold.get(Calendar.DAY_OF_YEAR);
    	DateTime event = new DateTime(eventTime.getTimeInMillis());
    	DateTime now = new DateTime(refHold.getTimeInMillis());
			// Get YEARS ---------------------------------
    		this.years[0] = Math.abs(Years.yearsBetween(event, now).getYears());
    		this.years[1] = Math.abs(subDivide (decreasingOffset, refHold, Calendar.YEAR));
			
			// Get MONTHS --------------------------------
    		this.months[0] = Math.abs(Months.monthsBetween(event, now).getMonths());
    		this.months[1] = Math.abs(subDivide (decreasingOffset, refHold, Calendar.MONTH));
			
			// Get DAYS ----------------------------------
    		this.days[0] = Math.abs(Days.daysBetween(event, now).getDays());
    		this.days[1] = Math.abs(subDivide (decreasingOffset, refHold, Calendar.DAY_OF_YEAR));
			
			// Get HOURS ---------------------------------
    		this.hours[0] = Math.abs(Hours.hoursBetween(event, now).getHours());
    		this.hours[1] = Math.abs(subDivide (decreasingOffset, refHold, Calendar.HOUR_OF_DAY)); 
			
			// Get MINUTES --------------------------------				
    		this.minutes[0] = Math.abs(Minutes.minutesBetween(event,now).getMinutes());
    		this.minutes[1] = Math.abs(subDivide (decreasingOffset, refHold, Calendar.MINUTE));
    		
    }
    public int totalNumberOf(int TIME_UNIT) {
    	if (TIME_UNIT < 0 || TIME_UNIT >= countedUnits.length) return 0;
    	else return countedUnits[TIME_UNIT][0];
    }
    public int remainderNumberOf(int TIME_UNIT) {
    	if (TIME_UNIT < 0 || TIME_UNIT >= countedUnits.length) return 0;
    	else return countedUnits[TIME_UNIT][1];
    }
    private int subDivide (Calendar dateDiff, Calendar now, int timeUnits) {
    	// Designed to calculate forwards and backwards time differences
    	final int rollDelta = (dateDiff.before(now)) ? 1 : -1; // add or subtract time units
    	final int origComp  = dateDiff.compareTo(now);
    	
    	int count = 0;
    	
		// Get timeUnits COUNT --------------------------------
		while (dateDiff.compareTo(now) == origComp) {			
			dateDiff.add(timeUnits, rollDelta);
			count++; 				
		} count--;
		dateDiff.add(timeUnits, -rollDelta);
    	return count;
    }
}
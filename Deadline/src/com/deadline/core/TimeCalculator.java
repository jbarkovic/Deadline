package com.deadline.core;

import java.util.Calendar;

public class TimeCalculator {
	protected int MIN_BAR_SIZE = 0;
	protected int BAR_ALIGNMENT = 0;
	protected boolean forceAlignment = false; 

	private float getPercentFromStart (Calendar start, Calendar end, Calendar event) {
		float percent = 0;
		long dateBeginning = -1;
		long dateEnd = -1;
		long dateEvent = -1;
		try {
			if (start.before(end)) {
				dateBeginning = start.getTimeInMillis();
				dateEnd = end.getTimeInMillis();		
			} else {
				dateBeginning = end.getTimeInMillis();
				dateEnd = start.getTimeInMillis();
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		dateEvent = event.getTimeInMillis();
		percent = (dateEvent - dateBeginning)/(dateEnd - dateBeginning);
		percent = Math.max(percent, 0);
		percent = Math.min(percent, 1);
		return percent;
	}
	public int [] getTimelinePositionPoints (int size, Calendar timelineStart, Calendar timelineEnd, Calendar eventStart, Calendar eventEnd) {
		int [] result = new int [] {0,0,0,size};
		if (!timelineStart.before(timelineEnd)) return result;
		// Compute bar start and end
		result[1] = (int) (getPercentFromStart (timelineStart,timelineEnd,eventStart) * size);
		result[2] = (int) (getPercentFromStart (timelineStart,timelineEnd,eventEnd) * size);
		// Bar end not before bar start
		result[2] = Math.max(result[2], result[1]);
		// Bar length not shorter than MIN_BAR_SIZE
		if (result[2] == result[1]) result[2] += MIN_BAR_SIZE;
		// Not longer than max size
		result[2] = Math.max(result[2], result[3]);
	
		return result;
	}
}

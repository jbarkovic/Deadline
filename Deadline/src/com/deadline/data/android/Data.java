package com.deadline.data.android;

import java.util.Calendar;

import android.os.Parcelable;

public class Data implements Comparable<Object> , java.io.Serializable, Parcelable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5274349150948995315L;
	public Calendar start;
	public Calendar end;
	public String name;
	public String info;
	public String other;
	boolean completed = false;
	boolean markedForDelete = false;
		
	private int priority = 1;
	private int difficulty = 1;

	public Data () {
		this.name = "";
		this.info = "";
		this.other = "";
		this.start = Calendar.getInstance();
		this.end = Calendar.getInstance();
		this.end.roll(Calendar.DAY_OF_YEAR, 1);
	}
	public Data (Calendar start, Calendar end, String name, String info, String other) {
		this.start = start;
		this.end = end;
		this.name = name;
		this.info = info;
		this.other = other;
	}
	
	public void setCompleted (boolean completed) {
		this.completed = completed;
	}
	
	public boolean getCompleted () {
		return this.completed;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public int getDifficulty() {
		return difficulty;
	}
	
	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}
	
	public int compareTo(Data arg0) { // Compares based on End Date
		return this.end.compareTo(arg0.end);
	}
	public void markForDeletion () {
		this.markedForDelete = true;
	}
	@Override
	public int compareTo(Object arg0) {
		if (arg0 instanceof Data) return this.compareTo((Data) arg0);
		return 0;
	}
	@Override
	public boolean equals(Object arg0) {
		boolean isSame = false;
		isSame = (arg0 instanceof Data);
		try {
			Data compData = (Data) arg0;
			isSame &= this.name.equals(compData.name);
			isSame &= this.info.equals(compData.info);
			isSame &= this.other.equals(compData.other);
			isSame &= this.start.equals(compData.start);
			isSame &= this.end.equals(compData.end);
			isSame &= (this.completed == compData.completed);
			isSame &= (this.markedForDelete == compData.markedForDelete);
		} catch (ClassCastException e) {
			return false;
		}
		return isSame;
	}
}

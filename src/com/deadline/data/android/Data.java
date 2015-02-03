package com.deadline.data.android;

import java.util.Calendar;

import android.os.Parcel;
import android.os.Parcelable;

public class Data implements Comparable<Object> , java.io.Serializable, Parcelable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5274349150948995315L;
	public Calendar end;
	public Calendar created;
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
		this.end = Calendar.getInstance();
		this.end.roll(Calendar.DAY_OF_YEAR, 1);
		this.created = Calendar.getInstance();
	}
	public Data (Calendar end, String name, String info, String other) {
		this (null, end, name, info, other);
	}
	public Data (Calendar start, Calendar end, String name, String info, String other) {
		this.end = end;
		this.name = name;
		this.info = info;
		this.other = other;
		this.created = Calendar.getInstance();
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
			isSame &= this.end.equals(compData.end);
			isSame &= (this.completed == compData.completed);
			isSame &= (this.markedForDelete == compData.markedForDelete);
		} catch (ClassCastException e) {
			return false;
		}
		return isSame;
	}
    public static final Parcelable.Creator<Data> CREATOR = new Parcelable.Creator<Data>() {
        public Data createFromParcel(Parcel in) {
        	Data newData = new Data ();
        	newData.end = Calendar.getInstance();
        	newData.end.setTimeInMillis(in.readLong());
        	newData.created = Calendar.getInstance();
        	newData.created.setTimeInMillis(in.readLong());
        	newData.name = in.readString();
        	newData.info = in.readString();
        	newData.other = in.readString();
        	boolean [] eventFlags = new boolean [2];
        	in.readBooleanArray(eventFlags);
        	newData.completed = eventFlags[0];
        	newData.markedForDelete = eventFlags[1];
        	int [] weights = new int [2];
        	in.readIntArray(weights);
        	newData.priority = weights[0];
        	newData.difficulty = weights[1];
            return newData;
        }

        public Data[] newArray(int size) {
            return new Data[size];
        }
    };
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel arg0, int flags) {
		arg0.writeLong(end.getTimeInMillis());
		arg0.writeLong(created.getTimeInMillis());
		arg0.writeString(name);
		arg0.writeString(info);
		arg0.writeString(other);
		arg0.writeBooleanArray(new boolean [] {completed, markedForDelete});
		arg0.writeIntArray(new int [] {priority, difficulty});		
	}
}

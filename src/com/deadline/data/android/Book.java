package com.deadline.data.android;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable{
	private String name = "Default";
	public Book () {		
	}
	public Book setName(String name) {
		this.name = name;
		return this;
	}
	public String getName() {
		return this.name;
	}
	@Override
	public boolean equals(Object obj) {
		if (!(this instanceof Book)) return false;
		else {
			Book compareTo = (Book) obj;
			if (compareTo.name.equals(this.name)) return true;
		}
		return false;
	}
	@Override
	public String toString() {
		return this.name;		
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeString(name);		
	}
    public Book(Parcel in){  
        this.name = in.readString();
    }
    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
        public Book createFromParcel(Parcel in) {
            return new Book(in); 
        }
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
}

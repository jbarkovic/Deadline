package com.deadline.core;

import android.os.Parcel;
import android.os.Parcelable;

public class EventModification {
	boolean wasCanceled = false;
	ModType modification = null;
	public enum ModType {
		NEW,CHANGED,DELETED,CANCELED;
	}
}

package com.deadline.core;

public class EventModification {
	boolean wasCanceled = false;
	ModType modification = null;
	public enum ModType {
		NEW,CHANGED,DELETED,CANCELED;
	}
}

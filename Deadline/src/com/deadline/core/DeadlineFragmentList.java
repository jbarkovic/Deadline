package com.deadline.core;

import java.util.ArrayList;
import java.util.ListIterator;


public class DeadlineFragmentList extends ArrayList<EventListFragment>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6923330244181034766L;

	public void refresh () {
		ListIterator<EventListFragment> iterator = this.listIterator();
		while (iterator.hasNext()) {
			iterator.next().refresh();
		}
	}
}

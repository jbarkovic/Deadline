package com.deadline.core;

import com.deadline.data.android.BookManager;
import com.deadline.data.android.Data;
import com.deadline.data.android.DataManager;

public class DataParcel {		
   public static Data mData = new Data ();
   public static BookManager bManager;
   public static DataManager dManager;
   public static EventModification mod = new EventModification ();
   static {
	   mod.modification = EventModification.ModType.CANCELED;
   }
}

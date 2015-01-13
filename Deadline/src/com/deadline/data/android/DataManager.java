package com.deadline.data.android;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;

import android.content.Context;

public class DataManager extends ArrayList<Data> {

	private final String COMPLETED = "completed"; 
	private final String NOT_COMPLETED = "not_completed"; 
	/**
	 * 
	 */
	private static final long serialVersionUID = 349517146702606258L;
	public enum DataSource {
		DROP_BOX, FILE_SYSTEM
	}
	private enum WriteMode {
		APPEND, WRITE
	}	
	private static final String LOCAL_FILE_DELIMITER = ";L;";
	private Book book = new Book ();
	private DataSource ourSource = DataSource.FILE_SYSTEM;
	private Context context;
	private static Calendar future = Calendar.getInstance();
	Data[] list_hardcode = new Data[] {new Data (Calendar.getInstance(), future ,"4AA4 Assign.","Written Assignment",""),new Data (Calendar.getInstance(), future ,"4O03 Assign.","Written Assignment","")};
	Data [] error_list = new Data[] {new Data (Calendar.getInstance(),Calendar.getInstance(),"Error","FileError","")};
	
	public DataManager (Context context, Book book) throws DataRetrievalException {	
		super();
		this.context = context;
		this.ourSource = DataSource.FILE_SYSTEM;
		this.book = book;
		this.getAppointments();
	}
	public boolean addDropbox () {
		return false;
	}
	public Book getBook () {
		return this.book;
	}
	public int purgeDeleted() {		
		int count = 0;
		Iterator<Data> it = this.iterator();
		ArrayList<Data> condensed = new ArrayList<Data> ();
		while(it.hasNext())
		{
		    Data obj = it.next();
		    if (!obj.markedForDelete) {condensed.add(obj); count++;}
		}
		if (condensed.size() != this.size()) {
			this.clear();
			this.addAll(condensed);
		}
		return count;
	}
	private int getAppointments () throws DataRetrievalException {
		FileInputStream fis;
		ArrayList<Data> newList = new ArrayList<Data> ();
		try {
			fis = context.openFileInput(this.book.getName());
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			String fileData = "";
			String line;			
			while ((line = br.readLine()) != null) {
				/** Add "\n" to line to account for double
				 line messages **/
				fileData += line + "\n"; 
			}
			System.out.println("READ LINE");
			String [] values = fileData.split(LOCAL_FILE_DELIMITER);
			System.out.println("LENGTH VALUES: " + values.length);
			for (String name : values) {
				System.out.print(name + LOCAL_FILE_DELIMITER);
			}
			System.out.println();
			for (int i=7;i<values.length+1;i+=7) {
				String [] elements = Arrays.copyOfRange(values, i-7, i);
				System.out.println("ITEM");

				if (elements.length < 7) {
					Data error_data = new Data();
					error_data.name = "Data format error.";
					this.add(error_data);
					return -1;
				} else {
					String name = elements[0];
					String info = elements[1];
					long startMillsec = Long.parseLong(elements[2]);
					long endMillsec = Long.parseLong(elements[3]);
					int difficulty = Integer.parseInt(elements[4]);
					int priority = Integer.parseInt(elements[5]);
					boolean completed = elements[6].equals(COMPLETED);

					Calendar start = Calendar.getInstance();
					Calendar end = Calendar.getInstance();
					start.setTimeInMillis(startMillsec);
					end.setTimeInMillis(endMillsec);
					Data new_element = new Data (start,end,name,info,"");
					new_element.setCompleted(completed);
					new_element.setDifficulty(difficulty);
					new_element.setPriority(priority);
					newList.add(new_element);
				}
			}			
			fis.close();
			this.clear();
			for (Data item : newList) {
				this.add(item);
			}									
		} catch (FileNotFoundException e) {
			return 0;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new DataRetrievalException (e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new DataRetrievalException (e.getMessage());
		} finally {

		}
		
		return 0;			
	}
	public void commit () throws DataCommitException {	
		this.purgeDeleted();
		writeFileSystem(WriteMode.WRITE);
	}	
	public void sortByEndDate () {
		Collections.sort( this);
	}
	@Override
	public boolean add (Data inData) {
		boolean result = super.add(inData);
		this.sortByEndDate();
		return result;
	}
	private void writeFileSystem(WriteMode mode) throws DataCommitException {
		String string = "";
		//System.out.println("WRITING TO BOOK: " + this.book);		
		for (int i=0;i<this.size();i++) {
			Data event = this.get(i);
			String completed;
			if (event.getCompleted()) completed = COMPLETED;
			else completed = NOT_COMPLETED;
			string += ("" + event.name + "" + LOCAL_FILE_DELIMITER + "" + event.info + "" + LOCAL_FILE_DELIMITER + "" + event.start.getTimeInMillis() + "" + LOCAL_FILE_DELIMITER + "" + event.end.getTimeInMillis() + "" + LOCAL_FILE_DELIMITER + "" + event.getDifficulty() + "" + LOCAL_FILE_DELIMITER + "" + event.getPriority() + "" + LOCAL_FILE_DELIMITER + "" + completed + LOCAL_FILE_DELIMITER);
		}		
		//System.out.println(string);
		FileOutputStream fos;
		try {
			if (mode == WriteMode.WRITE) {
				fos = context.openFileOutput(book.getName(), Context.MODE_PRIVATE);
				fos.write(string.getBytes());
				fos.close();
			} else {
				fos = context.openFileOutput(book.getName(), Context.MODE_APPEND);
				fos.write(string.getBytes());
				fos.close();
			}
		} catch (IOException e) {
			throw new DataCommitException ("IOException writing filesystem");
		}
	}
	@Override
	public String toString () {
		return this.book.getName();
	}
	@Override
	public boolean equals (Object obj) {
		if (obj instanceof DataManager) {
			DataManager compTo = (DataManager) obj;
			if (compTo.book.getName().equals(this.book.getName()) && compTo.context.equals(this.context)) {
					return true;
			} 
		}
		return false;
	}
}

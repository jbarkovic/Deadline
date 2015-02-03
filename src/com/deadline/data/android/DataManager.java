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
import android.os.Parcel;
import android.os.Parcelable;

public class DataManager extends ArrayList<Data> implements Parcelable {

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
	private Context context;
	
	public DataManager (Context context, Book book) throws DataRetrievalException {	
		super();
		this.context = context;
		this.book = book;
		this.getAppointments();
	}
    public DataManager (Parcel in) {
        readFromParcel(in);
    }
	public boolean addDropbox () {
		return false;
	}
	public Book getBook () {
		return this.book;
	}
	public int removeOld () {
		int count = 0;
		Iterator<Data> it = this.iterator();
		ArrayList<Data> condensed = new ArrayList<Data> ();
		Calendar now = Calendar.getInstance();
		Calendar ago20Min = Calendar.getInstance();
		ago20Min.add(Calendar.MINUTE, -20);
		while(it.hasNext())
		{
		    Data obj = it.next();
		    if (!obj.completed || obj.end.after(now) || obj.end.after(ago20Min)) {condensed.add(obj); count++;}
		}
		if (condensed.size() != this.size()) {
			this.clear();
			this.addAll(condensed);
		}
		return count;
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
			for (int i=6;i<values.length+1;i+=6) {
				String [] elements = Arrays.copyOfRange(values, i-6, i);
				System.out.println("ITEM");

				if (elements.length < 6) {
					Data error_data = new Data();
					error_data.name = "Data format error.";
					this.add(error_data);
					return -1;
				} else {
					String name = elements[0];
					String info = elements[1];
					long endMillsec = Long.parseLong(elements[2]);
					int difficulty = Integer.parseInt(elements[3]);
					int priority = Integer.parseInt(elements[4]);
					boolean completed = elements[5].equals(COMPLETED);

					Calendar end = Calendar.getInstance();
					end.setTimeInMillis(endMillsec);
					Data new_element = new Data (end,name,info,"");
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
			string += ("" + event.name + "" + LOCAL_FILE_DELIMITER + "" + event.info + "" + LOCAL_FILE_DELIMITER + "" + event.end.getTimeInMillis() + "" + LOCAL_FILE_DELIMITER + "" + event.getDifficulty() + "" + LOCAL_FILE_DELIMITER + "" + event.getPriority() + "" + LOCAL_FILE_DELIMITER + "" + completed + LOCAL_FILE_DELIMITER);
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

    private void readFromParcel(Parcel in) {
        this.clear();

        // First we have to read the list size
        int size = in.readInt();
        book = (Book) in.readParcelable (Book.class.getClassLoader());

        for (int i = 0; i < size; i++) {
            Data d = (Data) in.readParcelable(Data.class.getClassLoader());
            this.add(d);
        }
    }

    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<DataManager> CREATOR = new Parcelable.Creator<DataManager>() {
        public DataManager createFromParcel(Parcel in) {
            return new DataManager(in);
        }

        public DataManager[] newArray(int size) {
            return new DataManager[size];
        }
    };

    public void writeToParcel(Parcel dest, int flags) {
        int size = this.size();

        // We have to write the list size, we need him recreating the list
        dest.writeInt(size);

        dest.writeParcelable(book, flags);
        for (int i = 0; i < size; i++) {
            Data d = this.get(i);

            dest.writeParcelable(d, flags);
        }
    }
}

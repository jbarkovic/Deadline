package com.deadline.data.android;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.ListIterator;

import com.deadline.core.R;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastoreManager;
import com.dropbox.sync.android.DbxException;

import android.content.Context;

public class BookManager extends ArrayList<DataManager> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3446909657131708643L;
	
	private static final String BOOK_REPO_NAME = "books";
	private final Context context;
	
	public BookManager (Context context) throws DataRetrievalException {
		this.context = context;
		this.getBooks();
	}
	public DataManager getDataManager (Book book) throws DataRetrievalException {
		return new DataManager (this.context,book);		
	}
	public void commit () throws DataCommitException {
		this.write();
		ListIterator<DataManager> iterator = this.listIterator();
		while (iterator.hasNext()) {
			iterator.next().commit();
		}
	}
	public void add (Book book) throws DataRetrievalException {
		this.add(new DataManager (this.context,book));
	}
	private void write () throws DataCommitException {
		FileOutputStream fos;
		String string = "";
		ListIterator<DataManager> iterator = this.listIterator();
		while (iterator.hasNext()) {
			string += iterator.next().getBook().getName() + "\n";
		}
		try {
				fos = context.openFileOutput(BOOK_REPO_NAME, Context.MODE_PRIVATE);
				fos.write(string.getBytes());
				fos.close();	
		} catch (IOException e){
			throw new DataCommitException ("IO Exception commiting books");
		}			
	}
	private void getBooks () throws DataRetrievalException {
		FileInputStream fis;
		try {
			fis = context.openFileInput(BOOK_REPO_NAME);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			String line;
			while ((line = br.readLine()) != null) {
				this.add(new DataManager(this.context,(new Book()).setName(line)));					
			}	
			fis.close();
		} catch (FileNotFoundException e) {
				return;			
		} catch (NumberFormatException e) {
			throw new DataRetrievalException ("Number Format Exception in getting books");
		} catch (IOException e) {
			throw new DataRetrievalException ("IO Exception in getting books");
		}
	}
}

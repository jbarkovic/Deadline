package com.deadline.core;

import java.util.Locale;

import com.deadline.core.EventListFragment.RefreshListListener;
import com.deadline.data.android.Book;
import com.deadline.data.android.BookManager;
import com.deadline.data.android.Data;
import com.deadline.data.android.DataCommitException;
import com.deadline.data.android.DataRetrievalException;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class MainTabActivity extends ActionBarActivity implements RefreshListListener {
	
	private DeadlineFragmentList fragments = new DeadlineFragmentList ();
	private BookManager bookManager = null;
	protected final static String END_DATE_KEY = "END_DATE";
	protected final static String BOOK_KEY = "DATA_MAN";
	private final static int UPDATE_INTERVAL = 1000 * 58; //2 minutes
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;
	
	private Handler mHandler = new Handler ();
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;	

	private Runnable mHandlerTask = new Runnable()
	{
	     @Override 
	     public void run() {
	          refreshLists();
	          mHandler.postDelayed(mHandlerTask, UPDATE_INTERVAL);
	     }
	};

	private void startRefreshingTask()
	{
	    mHandlerTask.run(); 
	}
	private void stopRefreshingTask()
	{
	    mHandler.removeCallbacks(mHandlerTask);
	}
	public void refreshLists (EventListFragment frag) {
		if (frag != null) frag.refresh();
		if (mViewPager != null) mViewPager.invalidate();
	}
	private void refreshLists () {
		if (this.fragments == null || this.fragments.size() == 0) return;
		this.fragments.refresh();
		if (mViewPager != null) mViewPager.invalidate();
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	            // do nothing, just override
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);	
		Toast toast;
		setContentView(R.layout.activity_main_tab);
	    // Add 3 tabs, specifying the tab's text and TabListener
	    String[] tabLabels = new String[] {"School", "Home", "Work"};
	    try {
			this.bookManager = new BookManager (this);
			DataParcel.bManager = this.bookManager;
			if (this.bookManager.size() != 3) {
				this.bookManager.clear();
				this.bookManager.commit();
				for (int i=0;i<3;i++) {
					this.bookManager.add((new Book ()).setName(tabLabels[i]));
				}
			}
		} catch (DataRetrievalException e) {
			toast = Toast.makeText(this, "Error retrieving data", Toast.LENGTH_LONG);
			toast.show();
			e.printStackTrace();
			return;
		} catch (DataCommitException e) {
			toast = Toast.makeText(this, "Error retrieving data", Toast.LENGTH_LONG);					
			toast.show();
			e.printStackTrace();
			return;
		}
		startRefreshingTask();
		final ActionBar actionBar = getActionBar();	
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());
		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

	    // Specify that tabs should be displayed in the action bar.
	    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

	    // Create a tab listener that is called when the user changes tabs.
	    ActionBar.TabListener tabListener = new ActionBar.TabListener() {
	        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
	            // show the given tab
	            mViewPager.setCurrentItem(tab.getPosition());
	            mViewPager.getCurrentItem();	            
	    	    //refreshLists ();
	            //mViewPager.invalidate();	            
	        }
	        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {	        	
	        	// hide the given tab
	        }
	        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
	            // probably ignore this event
	        }
	    };

	    for (int i = 0; i < 3; i++) {
	        actionBar.addTab(
	                actionBar.newTab()
  	                        .setText(tabLabels[i])
	                        .setTabListener(tabListener));
	    }
	    
	    mViewPager.setOnPageChangeListener(
	            new ViewPager.SimpleOnPageChangeListener() {
	                @Override
	                public void onPageSelected(int position) {

	                    // When swiping between pages, select the
	                    // corresponding tab.
	                    getActionBar().setSelectedNavigationItem(position);
	                }
	            });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_tab, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} else if (id == R.id.action_newEvent) {
			DataParcel.mData = new Data ();
			DataParcel.bManager = this.bookManager;
			Intent i = new Intent(this, DeadlineEditActivity.class);
			i.putExtra("makeNewEvent", true);
			startActivityForResult(i,420);
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			EventListFragment frag;
			if (bookManager.size() > position) {
				frag = new EventListFragment();
				frag.setDataManager(bookManager.get(position));
				fragments.add(position,frag);
				
				return (ListFragment) frag;
			} else {
				return PlaceholderFragment.newInstance(position + 1);
			}			
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return bookManager.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
			refreshLists ();
	}
}

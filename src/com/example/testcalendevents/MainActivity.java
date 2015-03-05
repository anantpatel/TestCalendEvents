package com.example.testcalendevents;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {

	private MyReceiver broadcastreceiver = new MyReceiver();
	IntentFilter filter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		filter = new IntentFilter(CalendarContract.ACTION_EVENT_REMINDER);
		filter.addDataScheme("content");
		registerReceiver(broadcastreceiver, filter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onStop()
	{
		//unregisterReceiver(broadcastreceiver);
		super.onStop();
	}
	
	 // broadcast a custom intent. 
	   public void broadcastIntent(View view)
	   {
		   Log.i("Info","Sedning broadcast");
	      Intent intent = new Intent();
	      intent.setAction("com.example.testcalendevents.CUSTOM_INTENT");
	      sendBroadcast(intent);
	      
	   // get calendar
	      Intent calIntent = new Intent(Intent.ACTION_INSERT); 
	      calIntent.setType("vnd.android.cursor.item/event");    
	      calIntent.putExtra(Events.TITLE, "My House Party"); 
	      calIntent.putExtra(Events.EVENT_LOCATION, "My Beach House"); 
	      calIntent.putExtra(Events.DESCRIPTION, "A Pig Roast on the Beach"); 
	       
	      GregorianCalendar calDate = new GregorianCalendar(2012, 7, 15);
	      calIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true); 
	      calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, 
	           calDate.getTimeInMillis()+ 2*60*1000); // event starts at 11 minutes from 
	      calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, 
	           calDate.getTimeInMillis()+4*60*1000); // ends 60 minutes from now 
	   // make it a recurring Event
	      calIntent.putExtra(Events.RRULE, "FREQ=WEEKLY;COUNT=11;WKST=SU;BYDAY=TU,TH");

	      // Making it private and shown as busy
	      calIntent.putExtra(Events.ACCESS_LEVEL, Events.ACCESS_PRIVATE);
	      calIntent.putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY); 

	      startActivity(calIntent);
	   }
	   
	   private String getCalendarUriBase(Activity act) {

		    String calendarUriBase = null;
		    Uri calendars = Uri.parse("content://calendar/calendars");
		    Cursor managedCursor = null;
		    try {
		        managedCursor = act.managedQuery(calendars, null, null, null, null);
		    } catch (Exception e) {
		    }
		    if (managedCursor != null) {
		        calendarUriBase = "content://calendar/";
		    } else {
		        calendars = Uri.parse("content://com.android.calendar/calendars");
		        try {
		            managedCursor = act.managedQuery(calendars, null, null, null, null);
		        } catch (Exception e) {
		        }
		        if (managedCursor != null) {
		            calendarUriBase = "content://com.android.calendar/";
		        }
		    }
		    return calendarUriBase;
		}
}

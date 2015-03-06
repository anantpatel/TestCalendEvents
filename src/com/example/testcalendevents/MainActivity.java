package com.example.testcalendevents;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
	
	public class MyReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			 if (intent.getAction().equalsIgnoreCase(CalendarContract.ACTION_EVENT_REMINDER)) {
			        //Do Something Here to get EVENT ID
				 
			    }
			 Toast.makeText(context, "onReceive: a=" + intent.getAction() + " " + intent.toString(), Toast.LENGTH_LONG).show();
			 Log.i("Test", "onReceive: a=" + intent.getAction() + " " + intent.toString());
		}

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
	      
	      Intent queryIntent = new Intent("android.intent.action.EVENT_REMINDER");
	     // queryIntent.addDataScheme("content");
	        PackageManager pm = getPackageManager();
	       
	        List<ResolveInfo> re = pm.queryBroadcastReceivers(queryIntent, 0);
	        Log.i("Test1",re.toString());
	        
	        re = pm.queryBroadcastReceivers(new Intent("android.intent.action.AIRPLANE_MODE"), 0);
	        Log.i("Test2",re.toString());
	   }
	   
	   
	   public void broadcastIntentReminder(View view)
	   {
		   //popCalendar();
		   retrieveEvents();
	   }
	   private void retrieveEvents() {
		// TODO Auto-generated method stub
		// determine which fields we want in our events, like before
		   String[] EVENT_PROJECTION = new String[]{CalendarContract.Events.TITLE, CalendarContract.Events.EVENT_LOCATION, CalendarContract.Instances.BEGIN, CalendarContract.Instances.END, CalendarContract.Events.ALL_DAY};

		   // retrieve the ContentResolver
		   ContentResolver resolver = getContentResolver();

		   // use the uri given by Instances, but in a way that we can add information to the URI
		   Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon();

		   // add the begin and end times to the URI to use these to limit the list to events between them
		   GregorianCalendar calDate = new GregorianCalendar(2015, 2, 5);	
		   GregorianCalendar endDate = new GregorianCalendar(2015, 2, 6);
		   ContentUris.appendId(eventsUriBuilder, calDate.getTimeInMillis());
		   ContentUris.appendId(eventsUriBuilder,  endDate.getTimeInMillis());

		   // build the finished URI
		   Uri eventUri = eventsUriBuilder.build();

		   // filter the selection, like before
		   String selection = "((" + Events.CALENDAR_ID + " = ?))";
		   //String[] selectionArgs = new String[]{"" + cal.getCalId()};

		   // resolve the query, this time also including a sort option
		   Cursor eventCursor = resolver.query(eventUri, EVENT_PROJECTION, null, null, CalendarContract.Instances.BEGIN + " ASC");
		   
		   if( eventCursor.moveToFirst())
		   {
			   String l_title;
	    		String l_begin;
	    		String l_end;
			   int l_colTitle = eventCursor.getColumnIndex(EVENT_PROJECTION[0]);
		   		int l_colBegin = eventCursor.getColumnIndex(EVENT_PROJECTION[2]);
		   		int l_colEnd = eventCursor.getColumnIndex(EVENT_PROJECTION[3]);
			   do
			   {
				   l_title = eventCursor.getString(l_colTitle);
	    			l_begin = getDateTimeStr(eventCursor.getString(l_colBegin));
	    			l_end = getDateTimeStr(eventCursor.getString(l_colEnd));
				   Log.i("TEstApp", l_title +" "+ l_begin + " "+ l_end);
			   }while( eventCursor.moveToNext());
		   }
	}

	private void popCalendar() {
		// TODO Auto-generated method stub
		// get calendar
		      Intent calIntent = new Intent(Intent.ACTION_INSERT); 
		      calIntent.setType("vnd.android.cursor.item/event");    
		      calIntent.putExtra(Events.TITLE, "My House Party"); 
		      calIntent.putExtra(Events.EVENT_LOCATION, "My Beach House"); 
		      calIntent.putExtra(Events.DESCRIPTION, "A Pig Roast on the Beach"); 
		       
		      GregorianCalendar calDate = new GregorianCalendar(2015, 3, 5);
		      calIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false); 
		      calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, 
		           calDate.getTimeInMillis()+ 1*60*1000); // event starts at 1 minutes from 
		      calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, 
		           calDate.getTimeInMillis()+4*60*1000); // ends 4 minutes from now 
		   // make it a recurring Event
		      calIntent.putExtra(Events.RRULE, "FREQ=WEEKLY;COUNT=11;WKST=SU;BYDAY=TU,TH");

		      // Making it private and shown as busy
		      calIntent.putExtra(Events.ACCESS_LEVEL, Events.ACCESS_PRIVATE);
		      calIntent.putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY); 
		      
		      
		      //Reminder 
		     // ContentResolver cr = getContentResolver();
		     // ContentValues values = new ContentValues();
		      calIntent.putExtra(Reminders.MINUTES, -1);
		     // calIntent.putExtra(Reminders.EVENT_ID, eventID);
		      calIntent.putExtra(Reminders.METHOD, Reminders.METHOD_ALARM);
		      //Uri uri = cr.insert(Reminders.CONTENT_URI, values);
		      
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
	
	private static final String DATE_TIME_FORMAT = "yyyy MMM dd, HH:mm:ss";
    public static String getDateTimeStr(int p_delay_min) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
		if (p_delay_min == 0) {
			return sdf.format(cal.getTime());
		} else {
			Date l_time = cal.getTime();
			l_time.setMinutes(l_time.getMinutes() + p_delay_min);
			return sdf.format(l_time);
		}
	}
    public static String getDateTimeStr(String p_time_in_millis) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
    	Date l_time = new Date(Long.parseLong(p_time_in_millis));
    	return sdf.format(l_time);
    }
}

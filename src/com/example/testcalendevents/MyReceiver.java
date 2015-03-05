package com.example.testcalendevents;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		 if (intent.getAction().equalsIgnoreCase(CalendarContract.ACTION_EVENT_REMINDER)) {
		        //Do Something Here to get EVENT ID
			 
		    }
		 Toast.makeText(context, "Recived", Toast.LENGTH_LONG);
		 Log.i("Test", "onReceive: a=" + intent.getAction() + " " + intent.toString());
	}

}

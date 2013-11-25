package edu.cmu.ssui.kmmurphy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReminderReceiver extends BroadcastReceiver {

	@Override
    public void onReceive(Context context, Intent intent) {
        // get the step id and description from the intent, and create a notification for it
		
		long id = intent.getLongExtra("id", 0);
        String msg = intent.getStringExtra("msg");
        
        
    }
 
	
}

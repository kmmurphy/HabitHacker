package edu.cmu.ssui.kmmurphy;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import edu.cmu.ssui.kmmurphy.dbAdapter.StepEntry;

public class ReminderReceiver extends BroadcastReceiver {

	@Override
    public void onReceive(Context context, Intent intent) {
        // get the step id and description from the intent, and create a notification for it
		
		int id = intent.getIntExtra(StepEntry._ID, 0);
        String description = intent.getStringExtra(StepEntry.COLUMN_NAME_DESCRIPTION);
        Log.v("MURPHY", "received reminder with description "+description);
        showNotification(context, description);        
    }
	
	private void showNotification(Context context, String description){
		
		PendingIntent pi = PendingIntent.getActivity(context, 0, 
				new Intent(context, MainActivity.class), 0);
		
		NotificationCompat.Builder mBuilder =
			    new NotificationCompat.Builder(context)
			    .setSmallIcon(R.drawable.logo)
			    .setContentTitle("HabitHacker")
			    .setContentText(description);
		
		mBuilder.setContentIntent(pi);
		
		NotificationManager mNotifyMgr = 
				  (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		int mNotificationId = 001;
		mNotifyMgr.notify(mNotificationId, mBuilder.build()); 
	}
 
	
}

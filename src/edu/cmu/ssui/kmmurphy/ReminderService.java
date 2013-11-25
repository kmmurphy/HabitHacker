package edu.cmu.ssui.kmmurphy;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import edu.cmu.ssui.kmmurphy.dbAdapter.StepEntry;

/** 
 * Class to set alarms so a push notification will be sent to the user
 * at the time they selected
 * 
 * @author Kenneth Murphy
 *
 */

public class ReminderService extends IntentService {
	private dbAdapter mDbHelper;
    private IntentFilter matcher;
	
	public static final String CREATE = "CREATE";
    public static final String CANCEL = "CANCEL";
     
    public ReminderService() {
        super("edu.cmu.ssui.kmmurphy");
        matcher = new IntentFilter();
        matcher.addAction(CREATE);
        matcher.addAction(CANCEL);
    }
 
    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
       	Bundle extras = intent.getExtras();
        
       	int stepId = extras.getInt(StepEntry._ID);
       	String description = extras.getString(StepEntry.COLUMN_NAME_DESCRIPTION);
       	String days = extras.getString(StepEntry.COLUMN_NAME_DAYS);
       	String dueDateStr = extras.getString(StepEntry.COLUMN_NAME_DUE_DATE);
       	String reminderTime = extras.getString(StepEntry.COLUMN_NAME_REMINDER_TIME);
         
         
        if (matcher.matchAction(action)) {          
            execute(action, stepId, description, days, dueDateStr, reminderTime);
        }
    }
    
    /**
     * Execute- method to set or cancel a reminder
     * 
     * @param action
     * @param notificationId
     */
    private void execute(String action, int stepId, String days, String description, String dueDateStr, String reminderTime) {
    	
        mDbHelper = new dbAdapter(this);
		mDbHelper.open();
		
		//get the time to set an alarm for 
    	SimpleDateFormat reminderFormater = new SimpleDateFormat("HH:mm", Locale.ROOT);
    	SimpleDateFormat dueDateFormater =  new SimpleDateFormat("MM/dd/yyyy", Locale.ROOT);
    	Date reminderDate = null;
    	Date dueDate = null;
    	
    	try {
			reminderDate = reminderFormater.parse(dueDateStr);
    	} catch (ParseException e) {
			Log.d("MURPHY", "remidner date couldn't be parsed");
		}
    	
    	try {
			dueDate = dueDateFormater.parse(reminderTime);
    	} catch (ParseException e) {
			Log.d("MURPHY", "due date couldn't be parsed");
		}
    	
			
		GregorianCalendar rCalendar = new GregorianCalendar();
		rCalendar.setTime(reminderDate);
		
		int hour = rCalendar.get(Calendar.HOUR_OF_DAY);
		int minutes = rCalendar.get(Calendar.MINUTE);
		Log.v("MURPHY", "hours set to "+Integer.toString(hour)+" minutes set to "+Integer.toString(minutes));
		
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        
			for(int i=0; i<7; i++){
				if(days.charAt(i) == '1'){
					Log.v("MURPHY", "setting an alarm");
					Intent intent = new Intent(this, ReminderReceiver.class);
					intent.putExtra(StepEntry._ID, stepId);
			        intent.putExtra(StepEntry.COLUMN_NAME_DESCRIPTION, description);
			         
			        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 
			                                                        PendingIntent.FLAG_UPDATE_CURRENT);
			        
					
				}
			}
        /* 
        Intent i = new Intent(this, ReminderReceiver.class);
        i.putExtra(StepEntry._ID, c.getLong(c.getColumnIndex(StepEntry._ID)));
        i.putExtra(StepEntry.COLUMN_NAME_DESCRIPTION, c.getString(c.getColumnIndex(StepEntry.COLUMN_NAME_DESCRIPTION)));
         
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 
                                                        PendingIntent.FLAG_UPDATE_CURRENT);
 
        long time = c.getLong(c.getColumnIndex(Notification.COL_DATETIME));
        
        if (CREATE.equals(action)) {
            am.set(AlarmManager.RTC_WAKEUP, time, pi);
             
        } else if (CANCEL.equals(action)) {
            am.cancel(pi);
        }
        */      
    }
}

package edu.cmu.ssui.kmmurphy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReminderSetter extends BroadcastReceiver {

	@Override
    public void onReceive(Context context, Intent intent) {
		// recreates all alarms for when the system reboots
        Intent service = new Intent(context, ReminderService.class);
        service.setAction(ReminderService.CREATE);
        context.startService(service);
    }
	
}

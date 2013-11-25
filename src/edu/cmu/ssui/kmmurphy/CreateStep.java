package edu.cmu.ssui.kmmurphy;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import edu.cmu.ssui.kmmurphy.dbAdapter.StepEntry;

/**
 * Activity to create new steps
 * 
 * @author kmmurphy Kenneth Murphy
 *
 */

public class CreateStep extends Activity {
	private EditText DescriptionText;
	private int[] dayArray = new int[7];
	private int stepId;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.step_edit);
    	
    	DescriptionText = (EditText) findViewById(R.id.descriptionText);

        final Button confirmButton = (Button) findViewById(R.id.confirmButton);
        final Button cancelButton = (Button) findViewById(R.id.cancelButton);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
        	// pre-fill description and days if the step is being edited
        	stepId = extras.getInt(StepEntry._ID);
        	String description = extras.getString(StepEntry.COLUMN_NAME_DESCRIPTION);
        	DescriptionText.setText(description);
        	String days = extras.getString(StepEntry.COLUMN_NAME_DAYS);
        	//set day of week buttons
        	loadDays(days);
        }
        //set click handlers for the confirm and cancel button
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	// should make it so they get a toast if they don't choose any days
            	
		        Bundle stepInfo = new Bundle();
		        String description = DescriptionText.getText().toString();
		        String days = "";
		        for(int i=0; i<7; i++){
		        	days += Integer.toString(dayArray[i]);
		        }
		        
		        //create a reminder to default to noon. Change to time picker!!
		        GregorianCalendar cal = new GregorianCalendar();
		        cal.set(Calendar.HOUR_OF_DAY, 12);
		        cal.set(Calendar.MINUTE, 0);
		        SimpleDateFormat formater = new SimpleDateFormat("HH:mm", Locale.ROOT);
		        String reminderTime = formater.format(cal);
		        Log.v("MURPHY", "setting the reminder to be: "+reminderTime);
		        
		        stepInfo.putInt(StepEntry._ID, stepId);
		        stepInfo.putString(StepEntry.COLUMN_NAME_DESCRIPTION, description);
		        stepInfo.putString(StepEntry.COLUMN_NAME_DAYS, days);
		        stepInfo.putString(StepEntry.COLUMN_NAME_REMINDER_TIME, reminderTime);
		        // on confirm return the new step to the aspiration view
            	Intent mIntent = new Intent();
		        mIntent.putExtras(stepInfo);
            	setResult(RESULT_OK, mIntent);
		        finish();
            }
        });
        
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
		        Intent mIntent = new Intent();
		        setResult(RESULT_CANCELED, mIntent);
		        finish();
            }
        });
    }

    /**
     * Click handler to change the background image of the day button and record that the day has been selected
     * 
     * @param v
     */
    
    public void daySelected(View v){
    	switch(v.getId()){
    	case R.id.sunButton:
    		if(dayArray[0] == 0){
    			dayArray[0] = 1;
    			v.setBackgroundResource(R.drawable.selected_sun);
    		}else {
    			dayArray[0] = 0;
    			v.setBackgroundResource(R.drawable.not_selected_sun);
    		}
    		break;
    	case R.id.monButton:
    		if(dayArray[1] == 0){
    			dayArray[1] = 1;
    			v.setBackgroundResource(R.drawable.selected_mon);
    		}else {
    			dayArray[1] = 0;
    			v.setBackgroundResource(R.drawable.not_selected_mon);
    		}
    		break;
    	case R.id.tuesButton:
    		if(dayArray[2] == 0){
    			dayArray[2] = 1;
    			v.setBackgroundResource(R.drawable.selected_tues);
    		}else {
    			dayArray[2] = 0;
    			v.setBackgroundResource(R.drawable.not_selected_tues);
    		}
    		break;
    	case R.id.wedButton:
    		if(dayArray[3] == 0){
    			dayArray[3] = 1;
    			v.setBackgroundResource(R.drawable.selected_wed);
    		}else {
    			dayArray[3] = 0;
    			v.setBackgroundResource(R.drawable.not_selected_wed);
    		}
    		break;
    	case R.id.thurButton:
    		if(dayArray[4] == 0){
    			dayArray[4] = 1;
    			v.setBackgroundResource(R.drawable.selected_thur);
    		}else {
    			dayArray[4] = 0;
    			v.setBackgroundResource(R.drawable.not_selected_thur);
    		}
    		break;
    	case R.id.friButton:
    		if(dayArray[5] == 0){
    			dayArray[5] = 1;
    			v.setBackgroundResource(R.drawable.selected_fri);
    		}else {
    			dayArray[5] = 0;
    			v.setBackgroundResource(R.drawable.not_selected_fri);
    		}
    		break;
    	case R.id.satButton:
    		if(dayArray[6] == 0){
    			dayArray[6] = 1;
    			v.setBackgroundResource(R.drawable.selected_sat);
    		}else {
    			dayArray[6] = 0;
    			v.setBackgroundResource(R.drawable.not_selected_sat);
    		}
    		break;
    	}
    }
    
    /**
     * Selects days that the user previously entered for when a step is being edited
     * 
     * @param days
     */
    private void loadDays(String days){
    	for(int i=0; i<7; i++){
	    	switch(i){
	    	case 0:
	    		if(days.charAt(i) == 1){
	    			View v = findViewById(R.id.sunButton);
	    			v.setBackgroundResource(R.drawable.selected_sun);
	    		}
	    		break;
	    	case 1:
	    		if(days.charAt(i) == 1){
	    			View v = findViewById(R.id.monButton);
	    			v.setBackgroundResource(R.drawable.selected_mon);
	    		}
	    		break;
	    	case 2:
	    		if(days.charAt(i) == 1){
	    			View v = findViewById(R.id.tuesButton);
	    			v.setBackgroundResource(R.drawable.selected_tues);
	    		}
	    		break;
	    	case 3:
	    		if(days.charAt(i) == 1){
	    			View v = findViewById(R.id.wedButton);
	    			v.setBackgroundResource(R.drawable.selected_wed);
	    		}
	    		break;
	    	case 4:
	    		if(days.charAt(i) == 1){
	    			View v = findViewById(R.id.thurButton);
	    			v.setBackgroundResource(R.drawable.selected_thur);
	    		}
	    		break;
	    	case 5:
	    		if(days.charAt(i) == 1){
	    			View v = findViewById(R.id.friButton);
	    			v.setBackgroundResource(R.drawable.selected_fri);
	    		}
	    		break;
	    	case 6:
	    		if(days.charAt(i) == 1){
	    			View v = findViewById(R.id.satButton);
	    			v.setBackgroundResource(R.drawable.selected_sat);
	    		}
	    		break;
	    	}
    	}
    }
}

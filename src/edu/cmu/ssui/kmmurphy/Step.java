package edu.cmu.ssui.kmmurphy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.util.Log;

/**
 * Class to encapsulate a step
 * 
 * @author kmmurphy Kenneth Murphy
 *
 */
public class Step {
	private int id;
	private String description;
	private int streak;
	private int completed;
	private  Date dueDate;
	private int numDaysDueIn;
	private String days;
	private String reminderTime;
	private int[] dayArray = new int[7];
	
	SimpleDateFormat formater;
	
	public Step(int sId, String sDescription, int sStreak, int sCompleted, String sDueDate, String sDays, String sReminderTime){
		id = sId;
		description = sDescription;
		streak = sStreak;
		completed = sCompleted;
		days = sDays;
		reminderTime = sReminderTime;
		daysToArray();
		// Convert the sDueDate string to a Date object
		formater = new SimpleDateFormat("MM/dd/yyyy", Locale.ROOT);
		// handle case if the due date hasn't been set
		if(sDueDate.equals("")){
			//set the due date to today and recalculate to find the new due date
			dueDate = new Date();
			calculateDueDate();
		}else {
			try {
				dueDate = formater.parse(sDueDate);
			} catch (ParseException e) {
				Log.d("MURPHY", "Date couldn't be parsed");
			}
		}
		calculateDaysDueIn();
	}
	private void daysToArray() {
		if(days.length() != 7){
			Log.e("MURPHY","days not the correct length");
		}
		
		for(int i=0; i<days.length(); i++){
			if(days.charAt(i) == '1'){
				dayArray[i] = 1;
			} else if(days.charAt(i) == '0'){
				dayArray[i] = 0;
			} else {
				Log.e("MURPHY","incorrect character in days string");
			}
		}
	}
	
	
	// getters
	public int getId(){
		return id;
	}
	public String getDescription() {
		return description;
	}
	public int getStreak(){
		return streak;
	}	
	public int getCompleted(){
		return completed;
	}
	public int getNumDaysDueIn(){
		return numDaysDueIn;
	}
	public String getDueDate(){
		if(dueDate == null){
			Log.v("MURPHY", "dueDate null");
			return "";
		}
		String dateString = formater.format(dueDate);
		return dateString;
	}
	public String getDays() {
		String daysStr = "";
		for(int i=0; i<7; i++){
			daysStr+= Integer.toString(dayArray[i]);
		}
		return daysStr;
	}
	public String getReminderTime() {
		return reminderTime;
	}
	// setters
	public void setDueDate(Date d){
		dueDate = d;
	}
	
	//Calculates how many days the step is due in
	private void calculateDaysDueIn(){
		
		Date today = new Date();
		
		long timeOne = today.getTime();
	    long timeTwo = dueDate.getTime();
	    long oneDay = 1000 * 60 * 60 * 24;
	    
	    int delta = (int)((timeTwo-timeOne)/oneDay);
	    
	    if(timeTwo > timeOne){
	    	numDaysDueIn = delta+1;
	    } else {
	    	numDaysDueIn = delta;
	    }
	}
	// Calculates the next due date
	public void calculateDueDate(){
		// this will only be called when a step is created or completed
		// set due date to the next day marked off in days, from today
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(dueDate);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		int daysToAdd = 1;
		//find the next day the goal is due
		for(int i=dayOfWeek; i<7; i++){
			if(dayArray[i] == 0){
				daysToAdd++;
			}else {
				calendar.add(Calendar.DATE, daysToAdd);
				dueDate = calendar.getTime();
				return;
			}
		}
		for(int i = 0; i < dayOfWeek; i++){
			if(dayArray[i] == 0){
				daysToAdd++;
			}else {
				calendar.add(Calendar.DATE, daysToAdd);
				dueDate = calendar.getTime();
				return;
			}
		}
	}
	
	@Override
	public String toString() {
		return description;
	}
	
}

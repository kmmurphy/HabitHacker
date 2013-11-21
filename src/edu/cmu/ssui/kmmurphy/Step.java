package edu.cmu.ssui.kmmurphy;

import android.util.Log;

public class Step {
	private int id;
	private String description;
	private int streak;
	private int completed;
	private String lastComplete;
	private String days;
	private int[] dayArray = new int[7];
	
	public Step(int sId, String sDescription, int sStreak, int sCompleted, String sLastComplete, String sDays){
		id = sId;
		description = sDescription;
		streak = sStreak;
		completed = sCompleted;
		lastComplete = sLastComplete;
		
		days = sDays;
		daysToArray();
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
	public String getLastComplete(){
		return lastComplete;
	}
	public int[] getDays() {
		return dayArray;
	}
	
	@Override
	public String toString() {
		return description;
	}
	
}

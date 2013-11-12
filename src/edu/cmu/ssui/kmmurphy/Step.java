package edu.cmu.ssui.kmmurphy;

public class Step {
	private int id;
	private String description;
	private int streak;
	
	public Step(int sId, String sDescription, int sStreak){
		id = sId;
		description = sDescription;
		streak = sStreak;
	}
	
	public int getId(){
		return id;
	}
	public String getDescription() {
		return description;
	}
	public int getStreak(){
		return streak;
	}	
	@Override
	public String toString() {
		return description;
	}
	
}

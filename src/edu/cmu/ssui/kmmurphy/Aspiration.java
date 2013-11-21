package edu.cmu.ssui.kmmurphy;

public class Aspiration {
	private int id;
	private String description;
	private int stepsInProgress;
	private int stepsCompleted;
	
	
	public Aspiration(int aId, String aDescription, int aStepsInProgress, int aStepsCompleted){
		id = aId;
		description = aDescription;
		stepsInProgress = aStepsInProgress;
		stepsCompleted = aStepsCompleted;
	}
	
	public int getId(){
		return id;
	}
	public String getDescription() {
		return description;
	}
	public int getStepsInProgress(){
		return stepsInProgress;
	}	
	public int getStepsCompleted(){
		return stepsCompleted;
	}
	@Override
	public String toString() {
		return description;
	}
}

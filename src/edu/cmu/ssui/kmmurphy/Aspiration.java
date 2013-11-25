package edu.cmu.ssui.kmmurphy;

/**
 * A class to encapsulate an Aspiration
 * 
 * @author kmmurphy
 *
 */
public class Aspiration {
	private int id;
	private String description;
	private int stepsInProgress;
	private int stepsCompleted;
	
	/**
	 * Constructor to create an Aspiration object
	 * 
	 * @param aId - row id in the database of the aspiration
	 * @param aDescription - description of the aspiration
	 * @param aStepsInProgress - steps in progress within that aspiration
	 * @param aStepsCompleted - steps completed toward that aspiration
	 */
	public Aspiration(int aId, String aDescription, int aStepsInProgress, int aStepsCompleted){
		id = aId;
		description = aDescription;
		stepsInProgress = aStepsInProgress;
		stepsCompleted = aStepsCompleted;
	}
	/*
	 * Various getters for the Aspiration object
	 */
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

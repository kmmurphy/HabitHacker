package edu.cmu.ssui.kmmurphy;

public class Aspiration {
	private int id;
	private String description;
	private int steps;
	
	public Aspiration(int aId, String aDescription, int aSteps){
		id = aId;
		description = aDescription;
		steps = aSteps;
	}
	
	public int getId(){
		return id;
	}
	public String getDescription() {
		return description;
	}
	public int getSteps(){
		return steps;
	}	
	@Override
	public String toString() {
		return description;
	}
}

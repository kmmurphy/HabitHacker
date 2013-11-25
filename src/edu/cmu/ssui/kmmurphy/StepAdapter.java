package edu.cmu.ssui.kmmurphy;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StepAdapter extends ArrayAdapter<Step>{
	private ArrayList<Step> steps;
	
	public StepAdapter(Context context, int textViewResourceId, ArrayList<Step> steps){
		super(context, textViewResourceId, steps);
		this.steps = steps;
	}
	
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;

		// first check to see if the view is null. if so, we have to inflate it.
		// to inflate it basically means to render, or show, the view.
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.step_row, null);
		}
		
		Step s = steps.get(position);
		
		if(s != null){
			TextView description = (TextView)v.findViewById(R.id.stepDescription);
			TextView progress = (TextView)v.findViewById(R.id.stepProgress);
			TextView streak = (TextView)v.findViewById(R.id.stepStreak);
			if(description != null){
				description.setText(s.getDescription());
			}
			if(progress != null){
				if(s.getCompleted() == 1){
					progress.setText("Goal Complete!");
				}else{
					int numDaysDueIn = s.getNumDaysDueIn();
					if(numDaysDueIn < 0){
						if(numDaysDueIn == -1){
							progress.setText("Due 1 Day Ago");
						}else{
							progress.setText("Due "+Integer.toString(Math.abs(numDaysDueIn))+" Days Ago");
						}
					}else if(numDaysDueIn == 0){
						progress.setText("Due Today");
					}else {
						if(numDaysDueIn == 1){
							progress.setText("Due Tomorrow");
						}else{
							progress.setText("Due in "+Integer.toString(numDaysDueIn)+" Days");
						}
					}
				}
			}
			if(streak != null){
				streak.setText(Integer.toString(s.getStreak()));
			}
		}
		
		return v;
	}
	
}

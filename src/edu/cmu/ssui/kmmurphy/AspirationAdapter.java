package edu.cmu.ssui.kmmurphy;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * A custom ArrayAdapter to display the multiple fields of an aspiration within a listview 
 * 
 * @author kmmurphy Kenneth Murphy
 *
 */
public class AspirationAdapter extends ArrayAdapter<Aspiration>{
	private ArrayList<Aspiration> aspirations;
	
	/**
	 * Constructor to create an AspirationAdapter
	 * 
	 * @param context
	 * @param textViewResourceId - resource id to tie the aspirations to
	 * @param aspirations - array of all aspirations
	 */
	public AspirationAdapter(Context context, int textViewResourceId, ArrayList<Aspiration> aspirations){
		super(context, textViewResourceId, aspirations);
		this.aspirations = aspirations;
	}
	
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;

		// first check to see if the view is null. if so, we have to inflate it.
		// to inflate it basically means to render, or show, the view.
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.aspiration_row, null);
		}
		
		Aspiration a = aspirations.get(position);
		
		if(a != null){
			TextView description = (TextView)v.findViewById(R.id.aspDescription);
			TextView streak = (TextView)v.findViewById(R.id.aspStepsInProgress);
			if(description != null){
				description.setText(a.getDescription());
			}
			if(streak != null){
				streak.setText(Integer.toString(a.getStepsInProgress()));
			}
		}
		
		return v;
	}
	
}

package edu.cmu.ssui.kmmurphy;


import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import edu.cmu.ssui.kmmurphy.dbAdapter.AspirationEntry;

public class ShowAspiration extends ListActivity {
	private TextView aDescription;
	private TextView aSteps;
	private int aId;
	
	private dbAdapter mDbHelper;
    private Cursor stepsCursor;
	
    private int stepper = 0;
    
	private List<Step> steps = new ArrayList<Step>();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.show_aspiration);
    	
    	aDescription = (TextView) findViewById(R.id.aspirationDescription);
    	aSteps = (TextView) findViewById(R.id.aspirationSteps);
    	
    	Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	aId = extras.getInt(AspirationEntry._ID);
        	String description = extras.getString(AspirationEntry.COLUMN_NAME_DESCRIPTION);
        	int numSteps = extras.getInt(AspirationEntry.COLUMN_NAME_STEPS_COMPLETED);
        	//set the text shown in the UI
        	aDescription.setText(description);
        	aSteps.setText("Steps Completed: "+Integer.toString(numSteps));
        }
        mDbHelper = new dbAdapter(this);
		mDbHelper.open();
		
        fillSteps();
    }
    
    private void fillSteps(){
    	stepsCursor = mDbHelper.fetchSteps(aId);

    	
    	steps.clear();
        stepsCursor.moveToFirst();
        while (!stepsCursor.isAfterLast()){
        	int id = stepsCursor.getInt(0);
        	String description = stepsCursor.getString(1);
        	int streakLen = stepsCursor.getInt(2);
        	Step newStep = new Step(id, description, streakLen);
        	steps.add(newStep);
        	stepsCursor.moveToNext();
        }
        stepsCursor.close();
        
        ArrayAdapter<Step> adapter = new ArrayAdapter<Step>(this,
                R.layout.aspiration_row, steps);
        setListAdapter(adapter);
    }
    
    public void createStep(View v) {
    	String description = "note number "+stepper;
    	stepper++;
    	
    	mDbHelper.createStep(aId,description);
    	fillSteps();
    }
}

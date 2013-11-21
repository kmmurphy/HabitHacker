package edu.cmu.ssui.kmmurphy;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import edu.cmu.ssui.kmmurphy.dbAdapter.AspirationEntry;
import edu.cmu.ssui.kmmurphy.dbAdapter.StepEntry;

public class ShowAspiration extends ListActivity {
	// related aspiration fields
	private int aId;
	private String aDescription;
	private int aNumStepsInProgress;
	private int aNumStepsCompleted;
	
	private dbAdapter mDbHelper;
    private Cursor stepsCursor;
    private SimpleCursorAdapter sAdapter;

	
    private static final int CREATE_STEP = 0;
	private List<Step> steps = new ArrayList<Step>();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.show_aspiration);
    	
    	TextView aDescriptionField = (TextView) findViewById(R.id.aspirationDescription);
    	TextView aStepsInProgress = (TextView) findViewById(R.id.stepsInProgressNumber);
    	TextView aStepsCompleted = (TextView) findViewById(R.id.stepsCompletedNumber);
    	
    	Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	aId = extras.getInt(AspirationEntry._ID);
        	aDescription = extras.getString(AspirationEntry.COLUMN_NAME_DESCRIPTION);
        	aNumStepsInProgress = extras.getInt(AspirationEntry.COLUMN_NAME_STEPS_IN_PROGRESS);
        	aNumStepsCompleted = extras.getInt(AspirationEntry.COLUMN_NAME_STEPS_COMPLETED);
        	//set the text shown in the UI
        	aDescriptionField.setText(aDescription);
        	aStepsInProgress.setText(Integer.toString(aNumStepsInProgress));
        	aStepsCompleted.setText(Integer.toString(aNumStepsCompleted));
        }
        mDbHelper = new dbAdapter(this);
		mDbHelper.open();
		
        fillSteps();
    }
    
    @SuppressWarnings("deprecation")
	private void fillSteps(){
    	stepsCursor = mDbHelper.fetchSteps(aId);

    	steps.clear();
        stepsCursor.moveToFirst();
        while (!stepsCursor.isAfterLast()){
        	int id = stepsCursor.getInt(0);
        	String description = stepsCursor.getString(1);
        	int streakLen = stepsCursor.getInt(2);
        	int completed = stepsCursor.getInt(3);
        	String lastComplete = stepsCursor.getString(4);
        	String days = stepsCursor.getString(5);
        	Log.v("MURPHY", "days are: "+days);
        	Step newStep = new Step(id, description, streakLen, completed, lastComplete, days);
        	steps.add(newStep);
        	stepsCursor.moveToNext();
        }
        stepsCursor.moveToFirst();
        
        sAdapter = new SimpleCursorAdapter(this,
                R.layout.step_row, 
                stepsCursor,
                new String[]{StepEntry.COLUMN_NAME_DESCRIPTION, StepEntry.COLUMN_NAME_STREAK},
                new int[]{R.id.stepDescription, R.id.stepStreak});
        setListAdapter(sAdapter);
        

        ListView listView = getListView();
        // Create a ListView-specific touch listener. ListViews are given special treatment because
        // by default they handle touches for their list items... i.e. they're in charge of drawing
        // the pressed state (the list selector), handling list item clicks, etc.
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        listView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                    	        	Step s = steps.get(position);
                    	        	mDbHelper.deleteStep(s.getId());
                    	        	String[] ids = TimeZone.getAvailableIDs(-8 * 60 * 60 * 1000);
                    	        	SimpleTimeZone pdt = new SimpleTimeZone(-8 * 60 * 60 * 1000, ids[0]);
                    	        	Calendar calendar = new GregorianCalendar(pdt);
                    	        	
                    	        	
                                }
                                fillSteps();
                            }
                        });
        listView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        listView.setOnScrollListener(touchListener.makeScrollListener());
    }
    
    public void createStep(View v) {
    	Intent i = new Intent(this, CreateStep.class);
        startActivityForResult(i,CREATE_STEP);
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(resultCode == RESULT_CANCELED){
        	Log.v("MURPHY", "activity canceled");
        	return;
        }
        Bundle extras = intent.getExtras();

    	switch(requestCode) {
        	case CREATE_STEP:
                String description = extras.getString(StepEntry.COLUMN_NAME_DESCRIPTION);
                String days = extras.getString(StepEntry.COLUMN_NAME_DAYS);
        		mDbHelper.createStep(aId, description, days);
        		mDbHelper.updateAspiration(aId, aDescription, aNumStepsInProgress++, aNumStepsCompleted);
        		fillSteps();
        }
    }
}

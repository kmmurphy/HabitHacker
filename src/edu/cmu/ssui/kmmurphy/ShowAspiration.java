package edu.cmu.ssui.kmmurphy;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
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
    private StepAdapter sAdapter;
    
    TextView aStepsInProgress;
    TextView aStepsComplete;

    private static final int CREATE_STEP = 0;
	private static final int EDIT_STEP = 1;
	private static final int DELETE_STEP = 2;
	private static final int COMPLETE_STEP = 3;

	private ArrayList<Step> steps = new ArrayList<Step>();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.show_aspiration);
    	
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("Goals to Achieve");
    	
    	TextView aDescriptionField = (TextView) findViewById(R.id.aspirationDescription);
    	aStepsInProgress = (TextView) findViewById(R.id.stepsInProgressNumber);
    	aStepsComplete = (TextView) findViewById(R.id.stepsCompletedNumber);
    	// get and display information about the aspiration 
    	Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	aId = extras.getInt(AspirationEntry._ID);
        	aDescription = extras.getString(AspirationEntry.COLUMN_NAME_DESCRIPTION);
        	aNumStepsInProgress = extras.getInt(AspirationEntry.COLUMN_NAME_STEPS_IN_PROGRESS);
        	aNumStepsCompleted = extras.getInt(AspirationEntry.COLUMN_NAME_STEPS_COMPLETED);
        	//set the text shown in the UI
        	aDescriptionField.setText(aDescription);
        	aStepsInProgress.setText(Integer.toString(aNumStepsInProgress));
        	aStepsComplete.setText(Integer.toString(aNumStepsCompleted));
        }
        mDbHelper = new dbAdapter(this);
		mDbHelper.open();
		
        fillSteps();
		// register each aspiration for the context menu
        registerForContextMenu(getListView());

    }
    
    /**
     * Adds a context menu on long-press of a step so you can edit, delete, or mark a step complete
     */
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    menu.add(0, COMPLETE_STEP, 0, R.string.complete_step);
	    menu.add(0, EDIT_STEP, 0, R.string.edit_step);
	    menu.add(0, DELETE_STEP, 0, R.string.delete_step);
	}

	/**
	 * Handle selection of a context menu
	 */
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        
		switch(item.getItemId()) {
	    case COMPLETE_STEP:
	    	if(info.id >= 0 && info.id < steps.size()){
	    		Step s = steps.get((int) info.id);
	    		mDbHelper.markStepComplete(s.getId());
	    		fillSteps();	    		
        		mDbHelper.updateAspiration(aId, aDescription, aNumStepsInProgress--, aNumStepsCompleted++);
        		aStepsInProgress.setText(Integer.toString(aNumStepsInProgress));
        		aStepsComplete.setText(Integer.toString(aNumStepsCompleted));
	        }
	        return true;
	    case DELETE_STEP:
	    	if(info.id >= 0 && info.id < steps.size()){
	    		Step s = steps.get((int) info.id);
	    		mDbHelper.deleteStep(s.getId());
	    		if(s.getCompleted() == 1){
	        		mDbHelper.updateAspiration(aId, aDescription, aNumStepsInProgress, aNumStepsCompleted--);
	        		aStepsComplete.setText(Integer.toString(aNumStepsCompleted));
	    		} else {
	    			mDbHelper.updateAspiration(aId, aDescription, aNumStepsInProgress--, aNumStepsCompleted);
	        		aStepsInProgress.setText(Integer.toString(aNumStepsInProgress));
	    		}
	    		fillSteps();
	    	}
	        return true;
		case EDIT_STEP:
			Step s = steps.get((int)info.id);
			
	    	Intent i = new Intent(this, CreateStep.class);
	    	i.putExtra(StepEntry._ID, s.getId());
	    	i.putExtra(StepEntry.COLUMN_NAME_DESCRIPTION, s.getDescription());
	    	i.putExtra(StepEntry.COLUMN_NAME_DAYS, s.getDays());
	        startActivityForResult(i,EDIT_STEP);
		}
	    return super.onContextItemSelected(item);
	}
    
    /**
     * Get the steps from the database, sort them, and display them in a listview
     */
	private void fillSteps(){
    	//TODO: change so this is only called on creation. 
		//Can make updates in steps and call notifyDataSetChanged on stepAdapter
		stepsCursor = mDbHelper.fetchSteps(aId);

    	steps.clear();
        stepsCursor.moveToFirst();
        while (!stepsCursor.isAfterLast()){
        	int id = stepsCursor.getInt(0);
        	String description = stepsCursor.getString(1);
        	int streakLen = stepsCursor.getInt(2);
        	int completed = stepsCursor.getInt(3);
        	String dueDate = stepsCursor.getString(4);
        	// set days due in for steps array
        	String days = stepsCursor.getString(5);
        	String reminderTime = stepsCursor.getString(6);

        	Step newStep = new Step(id, description, streakLen, completed, dueDate, days, reminderTime);
        	if(dueDate.equals("")){
	        	mDbHelper.updateStepOnSwipe(id, newStep.getDueDate(), streakLen);
        	}
        	steps.add(newStep);
        	//TODO: change to only change reminders when needed
        	// cancel any old reminder and add new one
        	cancelReminder(newStep);
        	addReminder(newStep);
        	
        	stepsCursor.moveToNext();
        }
        // sort the steps list based on when the step is due
        Collections.sort(steps, new Comparator<Step>(){
        		public int compare(Step s1, Step s2){
        			int daysDueIn1 = s1.getNumDaysDueIn();
        			int daysDueIn2 = s2.getNumDaysDueIn();
        			if(s1.getCompleted() == 1 && s2.getCompleted() == 0){
        				return 1;
        			}
        			if(s1.getCompleted() == 0 && s2.getCompleted() == 1){
        				return -1;
        			}
        			if(s1.getCompleted() == 1 && s2.getCompleted() == 1){
        				return (s1.getStreak() < s2.getStreak()) ? -1: 1;
        			} 
        			return (daysDueIn1 < daysDueIn2) ? -1: 1;
        		}
        });
        
        
        sAdapter = new StepAdapter(this, R.layout.step_row, steps);
        setListAdapter(sAdapter);

        ListView listView = getListView();
        // Create a ListView-specific touch listener.
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
                    	        	// update dueDate in the db
                    	        	s.calculateDueDate();
                    	        	mDbHelper.updateStepOnSwipe(s.getId(), s.getDueDate(), s.getStreak()+1);
                    	        	Context context = getApplicationContext();
                    	        	CharSequence text = "Awesome! You've completed this "+Integer.toString(s.getStreak()+1)+" times";
                    	        	int duration = Toast.LENGTH_SHORT;
                    	        	
                    	        	Toast toast = Toast.makeText(context, text, duration);
                    	        	toast.show();
                                }
                                fillSteps();
                            }
                        });
        listView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        listView.setOnScrollListener(touchListener.makeScrollListener());
    }
    /*
     * Start the activity to create a new step
     */
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
                String reminderTime = extras.getString(StepEntry.COLUMN_NAME_REMINDER_TIME);

                mDbHelper.createStep(aId, description, "", days, reminderTime);
        		mDbHelper.updateAspiration(aId, aDescription, aNumStepsInProgress++, aNumStepsCompleted);
        		fillSteps();
        		aStepsInProgress.setText(Integer.toString(aNumStepsInProgress));      
           		break;
        	case EDIT_STEP:
        		int stepId = extras.getInt(StepEntry._ID);
        		String newDescription = extras.getString(StepEntry.COLUMN_NAME_DESCRIPTION);
                String newDays = extras.getString(StepEntry.COLUMN_NAME_DAYS);
                //update step
                mDbHelper.updateStepOnEdit(stepId, newDescription, newDays);
                fillSteps();
                break;
        }
    }
    
    private void addReminder(Step s){
             	Intent addReminder = new Intent(this, ReminderService.class);
             	addReminder.putExtra(StepEntry._ID, s.getId());
             	addReminder.putExtra(StepEntry.COLUMN_NAME_DESCRIPTION, s.getDescription());
             	addReminder.putExtra(StepEntry.COLUMN_NAME_DAYS, s.getDays());
             	addReminder.putExtra(StepEntry.COLUMN_NAME_DUE_DATE, s.getDueDate());
             	addReminder.putExtra(StepEntry.COLUMN_NAME_REMINDER_TIME, s.getReminderTime());
 
             	addReminder.setAction(ReminderService.CREATE);
                startService(addReminder);
    }
    private void cancelReminder(Step s){
    	Intent cancelReminder = new Intent(this, ReminderService.class);
    	cancelReminder.putExtra(StepEntry._ID, s.getId());
     	cancelReminder.putExtra(StepEntry.COLUMN_NAME_DESCRIPTION, s.getDescription());
     	cancelReminder.putExtra(StepEntry.COLUMN_NAME_DAYS, s.getDays());
     	cancelReminder.putExtra(StepEntry.COLUMN_NAME_DUE_DATE, s.getDueDate());
     	cancelReminder.putExtra(StepEntry.COLUMN_NAME_REMINDER_TIME, s.getReminderTime());

    	
     	cancelReminder.setAction(ReminderService.CANCEL);
        startService(cancelReminder);
    }
}

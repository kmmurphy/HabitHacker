package edu.cmu.ssui.kmmurphy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;



/**
 * This class provides a sqlite database for the app, and all methods to query the database 
 *
 * @author kmmurphy Kenneth Murphy
**/
public class dbAdapter {
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * This class is the schema for a table to hold a user's aspirations
     */
    public static abstract class AspirationEntry implements BaseColumns {
    	public static final String TABLE_NAME = "aspirations";
    	public static final String COLUMN_NAME_DESCRIPTION = "description";
    	public static final String COLUMN_NAME_STEPS_IN_PROGRESS = "stepsInProgress";
    	public static final String COLUMN_NAME_STEPS_COMPLETED = "stepsCompleted";
    }

    /**
     * This class is the schema for a table to hold all the steps to meet aspirations
     */
    public static abstract class StepEntry implements BaseColumns {
    	public static final String TABLE_NAME = "steps";
    	public static final String COLUN_NAME_ASPIRATION_ID = "aspirationId";
    	public static final String COLUMN_NAME_DESCRIPTION = "description";
    	public static final String COLUMN_NAME_STREAK = "streak";
    	public static final String COLUMN_NAME_COMPLETED = "completed";
    	public static final String COLUMN_NAME_DUE_DATE = "dueDate";
    	public static final String COLUMN_NAME_DAYS = "days";
    	public static final String COLUMN_NAME_REMINDER_TIME = "reminderTime";
    }
    
    
    /**
     * Commands to create the two tables
     */
    private static final String CREATE_TABLE_ASPIRATION = 
    		"CREATE TABLE " + AspirationEntry.TABLE_NAME + " (" +
    		        AspirationEntry._ID + " INTEGER PRIMARY KEY, " + 
    		        AspirationEntry.COLUMN_NAME_DESCRIPTION + " TEXT not null, " +
    		        AspirationEntry.COLUMN_NAME_STEPS_IN_PROGRESS + " INTEGER NOT NULL, " +
    		        AspirationEntry.COLUMN_NAME_STEPS_COMPLETED + " INTEGER NOT NULL);";
        
    private static final String CREATE_TABLE_STEP =
    		"CREATE TABLE " + StepEntry.TABLE_NAME + " (" +
    		        StepEntry._ID + " INTEGER PRIMARY KEY, " +
    				StepEntry.COLUN_NAME_ASPIRATION_ID + " INTEGER NOT NULL, " +
    		        StepEntry.COLUMN_NAME_DESCRIPTION + " TEXT NOT NULL, " +
    		        StepEntry.COLUMN_NAME_STREAK + " INTEGER NOT NULL, " + 
    		        StepEntry.COLUMN_NAME_COMPLETED + " INTEGER NOT NULL, " +
    		        StepEntry.COLUMN_NAME_DUE_DATE + " TEXT, " +
    		        StepEntry.COLUMN_NAME_DAYS + " TEXT NOT NULL, " +
    		        StepEntry.COLUMN_NAME_REMINDER_TIME + " TEXT);" ;
    
    
    private final Context mCtx;

    /** 
     * This class provides methods to instantiate and update the database
     *
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "HabitHacker.db";
        private static final int DATABASE_VERSION = 16;

        private static DatabaseHelper sInstance = null;
        
        /**
         * Method to get the instance of the DatabaseHelper
         * 
         * @param context
         * @return the database helper
         */
        public static DatabaseHelper getInstance(Context context) {    
            if (sInstance == null) {
              sInstance = new DatabaseHelper(context.getApplicationContext());
            }
            return sInstance;
        }
        
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_ASPIRATION);
            db.execSQL(CREATE_TABLE_STEP);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+AspirationEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS "+StepEntry.TABLE_NAME);

            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public dbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public dbAdapter open() throws SQLException {
    	mDbHelper = DatabaseHelper.getInstance(mCtx);
    	mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    /**
     * Create a new aspiration. If the aspiration is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     * 
     * @param description - the description of the aspiration
     * @return rowId or -1 if failed
     */
    public long createAspiration(String description) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(AspirationEntry.COLUMN_NAME_DESCRIPTION, description);
        initialValues.put(AspirationEntry.COLUMN_NAME_STEPS_IN_PROGRESS, 0);
        initialValues.put(AspirationEntry.COLUMN_NAME_STEPS_COMPLETED, 0);

        return mDb.insert(AspirationEntry.TABLE_NAME, null, initialValues);
    }

    /**
     * Delete the aspiration with the given rowId
     * 
     * @param rowId id of aspiration to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteAspiration(int rowId) {
    	//first delete all steps related to that aspiration
    	Cursor stepsCursor = mDb.query(StepEntry.TABLE_NAME,
												new String[] { 
											  		StepEntry._ID},
											  	StepEntry.COLUN_NAME_ASPIRATION_ID+"="+Integer.toString(rowId), 
											  	null, null, null, null);
    	
        stepsCursor.moveToFirst();
        while (!stepsCursor.isAfterLast()){
        	int id = stepsCursor.getInt(0);
        	deleteStep(id);
        	stepsCursor.moveToNext();
        }
        stepsCursor.close();
        // now delete that aspiration
        return mDb.delete(AspirationEntry.TABLE_NAME, AspirationEntry._ID + "=" + rowId, null) > 0;
    } 

    /**
     * Return a Cursor over the list of all aspirations in the database
     * 
     * @return Cursor over all aspirations
     */
    public Cursor fetchAllAspirations() {
        return mDb.query(AspirationEntry.TABLE_NAME, 
        		new String[] {AspirationEntry._ID, 
        					  AspirationEntry.COLUMN_NAME_DESCRIPTION,
        					  AspirationEntry.COLUMN_NAME_STEPS_IN_PROGRESS,
        					  AspirationEntry.COLUMN_NAME_STEPS_COMPLETED},
        		null, null, null, null, null);
    }
	
    /**
     * Update the aspiration using the details provided. The aspiration to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     * 
     * @param rowId: id of aspiration to update
     * @param description: value to set aspiration description to
     * @param numSteps: value to set the number of steps to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateAspiration(int rowId, String description, int numStepsInProgress, int numStepsCompleted) {
        ContentValues args = new ContentValues();
        args.put(AspirationEntry.COLUMN_NAME_DESCRIPTION, description);
        args.put(AspirationEntry.COLUMN_NAME_STEPS_IN_PROGRESS, numStepsInProgress);
        args.put(AspirationEntry.COLUMN_NAME_STEPS_COMPLETED, numStepsCompleted);
        
        return mDb.update(AspirationEntry.TABLE_NAME, args, AspirationEntry._ID + "=" + rowId, null) > 0;
    } 
    
    /**
     * Create a new step. If the step is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     * 
     * @param aspirationId - The id of the aspiration to associate this step with
     * @param description - the description of the step
     * @param dueDate - the next date the task should be completed
     * @param days - strings of 1's and 0's indicating whether or not the 
     * 				 task is to be complete on that day. Starts on Sunday 
     * @return rowId or -1 if failed
     */
    public Long createStep(int aspirationId, String sDescription, String dueDate, String days, String reminderTime){
    	// create a string from the due date
    	ContentValues initialValues = new ContentValues();
        initialValues.put(StepEntry.COLUN_NAME_ASPIRATION_ID, aspirationId);
        initialValues.put(StepEntry.COLUMN_NAME_DESCRIPTION, sDescription);
        initialValues.put(StepEntry.COLUMN_NAME_STREAK, 0);
        initialValues.put(StepEntry.COLUMN_NAME_COMPLETED, 0);
        initialValues.put(StepEntry.COLUMN_NAME_DUE_DATE, dueDate);
        initialValues.put(StepEntry.COLUMN_NAME_DAYS, days);
        initialValues.put(StepEntry.COLUMN_NAME_REMINDER_TIME, reminderTime);

        return mDb.insert(StepEntry.TABLE_NAME, null, initialValues);    	
    }
    
    /**
     * Delete the step with the given rowId
     * 
     * @param rowId id of step to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteStep(int rowId) {
        return mDb.delete(StepEntry.TABLE_NAME, StepEntry._ID + "=" + rowId, null) > 0;
    }
    
    /**
     * Return a Cursor over the list of all steps in the database
     * 
     * @return Cursor over all steps
     */
    public Cursor fetchSteps(int aspirationId) {
    	// query the database for steps associated with that aspiration id
    	return mDb.query(StepEntry.TABLE_NAME,
    				new String[] { 
				  		StepEntry._ID,
    					StepEntry.COLUMN_NAME_DESCRIPTION,
				  		StepEntry.COLUMN_NAME_STREAK,
				  		StepEntry.COLUMN_NAME_COMPLETED,
				  		StepEntry.COLUMN_NAME_DUE_DATE,
				  		StepEntry.COLUMN_NAME_DAYS,
				  		StepEntry.COLUMN_NAME_REMINDER_TIME},
				  	StepEntry.COLUN_NAME_ASPIRATION_ID+"=" + Integer.toString(aspirationId), 
				  	null, null, null, null);
    }
    /** 
     * Update a step with the values entered
     * 
     * @param rowId - row id of the step to update
     * @param description - description of the step
     * @param streak - How many times in a row that task has been completed
     * @param completed - whether or not the task is permanently complete
     * @param dueDate - The next date the step is due
     * @param days - the days the step is due on
     * @return true if the step was successfully updated, false otherwise
     */
    public boolean updateStep(int rowId, String description, int streak, int completed, String dueDate, String days) {
        ContentValues args = new ContentValues();
        args.put(StepEntry.COLUMN_NAME_DESCRIPTION, description);
        args.put(StepEntry.COLUMN_NAME_STREAK, streak);
        args.put(StepEntry.COLUMN_NAME_COMPLETED, completed);
        args.put(StepEntry.COLUMN_NAME_DUE_DATE, dueDate);
        args.put(StepEntry.COLUMN_NAME_DAYS, days);
        return mDb.update(StepEntry.TABLE_NAME, args, StepEntry._ID + "=" + rowId, null) > 0;
    }
    
    /**
     *  Method to update the description and days of a step upon editing
     *  
     * @param rowId
     * @param description
     * @param days
     * @return true if the step was successfully updated
     */
    public boolean updateStepOnEdit(int rowId, String description, String days){
    	ContentValues args = new ContentValues();
    	args.put(StepEntry.COLUMN_NAME_DESCRIPTION, description);
    	args.put(StepEntry.COLUMN_NAME_DAYS, days);
        return mDb.update(StepEntry.TABLE_NAME, args, StepEntry._ID + "=" + rowId, null) > 0;
    }
    
    /** 
     * Update the dueDate and streak values of a step upon swipe
     * 
     * @param rowId
     * @param dueDate
     * @param streak
     * @return true if the step was successfully updated
     */
    public boolean updateStepOnSwipe(int rowId, String dueDate, int streak) {
        ContentValues args = new ContentValues();
        args.put(StepEntry.COLUMN_NAME_DUE_DATE, dueDate);
        args.put(StepEntry.COLUMN_NAME_STREAK, streak);
        return mDb.update(StepEntry.TABLE_NAME, args, StepEntry._ID + "=" + rowId, null) > 0;
    }    
    
    /**
     * Update a step to reflect that it's been permanently completed
     * 
     * @param rowId - row id of the step to mark complete
     * @return true is the step is successfully marked complete
     */
    public boolean markStepComplete(int rowId){
    	ContentValues args = new ContentValues();
    	args.put(StepEntry.COLUMN_NAME_COMPLETED, 1);
    	return mDb.update(StepEntry.TABLE_NAME, args, StepEntry._ID + "=" + rowId, null) > 0;
    }
}
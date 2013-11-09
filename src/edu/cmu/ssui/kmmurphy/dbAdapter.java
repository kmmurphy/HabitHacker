package edu.cmu.ssui.kmmurphy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;



/**
 * Simple database access helper class. 
 *
 * modified from adapter in notepad exercise http://developer.android.com/training/notepad/index.html
**/
public class dbAdapter {
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    public static abstract class AspirationEntry implements BaseColumns {
    	public static final String TABLE_NAME = "aspirations";
    	public static final String COLUMN_NAME_DESCRIPTION = "description";
    	public static final String COLUMN_NAME_STEPS_COMPLETED = "stepsCompleted";
    }
    
    public static abstract class StepEntry implements BaseColumns {
    	public static final String TABLE_NAME = "steps";
    	// foreign key to reference the associated aspiration
    	public static final String COLUMN_NAME_ASPIRATION_ID = "aspirationId";
    	public static final String COLUMN_NAME_DESCRIPTION = "description";
    	public static final String COLUMN_NAME_STREAK = "streak";
    }
    
    /**
     * Database creation sql statement
     */
    /*
    private static final String DATABASE_CREATE =
        "BEGIN; " +
        "CREATE TABLE " + AspirationEntry.TABLE_NAME + " (" +
        AspirationEntry._ID + " INTEGER PRIMARY KEY," + 
        AspirationEntry.COLUMN_NAME_DESCRIPTION + " TEXT," +
        AspirationEntry.COLUMN_NAME_STEPS_COMPLETED + " INTEGER NOT NULL" +
        "); " +
        "CREATE TABLE " + StepEntry.TABLE_NAME + " (" +
        StepEntry._ID + " INTEGER PRIMARY KEY," +
        StepEntry.COLUMN_NAME_ASPIRATION_ID + " INTEGER NOT NULL REFERENCES " +
        AspirationEntry.TABLE_NAME + "(" + AspirationEntry._ID + ")," +
        StepEntry.COLUMN_NAME_DESCRIPTION + " TEXT," +
        StepEntry.COLUMN_NAME_STREAK + " INTEGER NOT NULL" +
        "); " + 
        "COMMIT;";
      */
    private static final String DATABASE_CREATE = 
    		"CREATE TABLE " + AspirationEntry.TABLE_NAME + " (" +
    		        AspirationEntry._ID + " INTEGER PRIMARY KEY, " + 
    		        AspirationEntry.COLUMN_NAME_DESCRIPTION + " TEXT not null, " +
    		        AspirationEntry.COLUMN_NAME_STEPS_COMPLETED + " INTEGER NOT NULL);";
        
        
    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "HabitHacker.db";
        private static final int DATABASE_VERSION = 3;


        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS aspirations");
            db.execSQL("DROP TABLE IF EXISTS steps");
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
    	mDbHelper = new DatabaseHelper(mCtx);
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
        initialValues.put(AspirationEntry.COLUMN_NAME_STEPS_COMPLETED, 0);

        return mDb.insert(AspirationEntry.TABLE_NAME, null, initialValues);
    }
    /**
     * Delete the aspiration with the given rowId
     * 
     * @param rowId id of aspiration to delete
     * @return true if deleted, false otherwise
     */
    /*
    public boolean deleteAspiration(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    } */

    /**
     * Return a Cursor over the list of all aspirations in the database
     * 
     * @return Cursor over all aspirations
     */
    
    public Cursor fetchAllAspirations() {
        return mDb.query(AspirationEntry.TABLE_NAME, 
        		new String[] {AspirationEntry._ID, 
        					  AspirationEntry.COLUMN_NAME_DESCRIPTION,
        					  AspirationEntry.COLUMN_NAME_STEPS_COMPLETED},
        		null, null, null, null, null);
    }
	
    /**
     * Return a Cursor positioned at the aspiration that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching aspiration, if found
     * @throws SQLException if aspiration could not be found/retrieved
     */
    /*
    public Cursor fetchNote(long rowId) throws SQLException {

        Cursor mCursor =
            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_ASPIRATION}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    } */

    /**
     * Update the aspiration using the details provided. The aspiration to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     * 
     * @param rowId id of aspiration to update
     * @param title value to set aspiration title to
     * @return true if the note was successfully updated, false otherwise
     */
    /*
    public boolean updateNote(long rowId, String title) {
        ContentValues args = new ContentValues();
        args.put(KEY_ASPIRATION, title);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    } */
}
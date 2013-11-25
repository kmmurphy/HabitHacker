package edu.cmu.ssui.kmmurphy;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import edu.cmu.ssui.kmmurphy.dbAdapter.AspirationEntry;

public class MainActivity extends ListActivity {
	private dbAdapter mDbHelper;
    private Cursor mAspirationsCursor;
    AspirationAdapter mAdapter;

	private ArrayList<Aspiration> aspirations = new ArrayList<Aspiration>();
	
	private static final int ASPIRATION_CREATE = 0;
	private static final int ASPIRATION_EDIT = 1;
	private static final int ASPIRATION_DELETE = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
				
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("Your Aspirations");
		
		mDbHelper = new dbAdapter(this);
		mDbHelper.open();
		// populate the aspirations listview
		fillData();
		// register each aspiration for the context menu
        registerForContextMenu(getListView());

        Button addAspiration = (Button) findViewById(R.id.addAspiration);
        addAspiration.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Create Aspiration on Click
            	editAspiration(ASPIRATION_CREATE,0);
            }
        });
	}
	
	/**
	 * Creates context menu on long press of an aspiration to edit or delete an aspiration
	 */
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    menu.add(0, ASPIRATION_EDIT, 0, R.string.edit_aspiration);
	    menu.add(0, ASPIRATION_DELETE, 0, R.string.delete_aspiration);
	}
	
	/**
	 * Delete or edit an aspiration on selection in the context menu
	 */
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        
		switch(item.getItemId()) {
	    case ASPIRATION_EDIT:
	    	if(info.id >= 0 && info.id < aspirations.size()){
	        	editAspiration(ASPIRATION_EDIT, (int)info.id);
	        }
	        Log.v("MURPHY","editing aspiration number "+Long.toString(info.id));
	        return true;
	    case ASPIRATION_DELETE:
	        mDbHelper.deleteAspiration((int) info.id);
	        Log.v("MURPHY","deleting aspiration number "+Long.toString(info.id));
	        fillData();
	        return true;
	    }
	    return super.onContextItemSelected(item);
	}
    
	/**
	 * Creates a dialog to edit or create an aspiration
	 * 
	 * @param ACTION_ID
	 * @param aspirationIndex
	 */
    public void editAspiration(int ACTION_ID, int aspirationIndex) {
    	// create an aspiration through a dialog modal
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    // Get the layout inflater
	    LayoutInflater inflater = this.getLayoutInflater();
	    
	    View dialogView = inflater.inflate(R.layout.aspiration_edit, null);
	    final EditText editDescriptionInput = (EditText) dialogView.findViewById(R.id.aspiration_description);
		
	    switch (ACTION_ID){
	    case ASPIRATION_EDIT:
	    	final Aspiration a = aspirations.get(aspirationIndex);
	    	editDescriptionInput.setText(a.getDescription());
	    	// Inflate and set the layout for the dialog
		    // Pass null as the parent view because its going in the dialog layout
		    builder.setView(dialogView)
		    	.setTitle("Edit this Aspiration")
		    	// Add action buttons
		    	.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// create new aspiration
						String newAspirationDescription = editDescriptionInput.getText().toString();
						mDbHelper.updateAspiration(a.getId(), newAspirationDescription, a.getStepsInProgress(), a.getStepsCompleted());
						fillData();
						dialog.cancel();
					}
		    	})
		    	.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		    		public void onClick(DialogInterface dialog, int id) {
		    			dialog.cancel();
		            }	
		        });
		    	break;
	    case ASPIRATION_CREATE:
		    // Inflate and set the layout for the dialog
		    // Pass null as the parent view because its going in the dialog layout
		    builder.setView(dialogView)
		    	.setTitle("Create an Aspiration")
		    	// Add action buttons
		    	.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// create new aspiration
						String newAspiration = editDescriptionInput.getText().toString();
						mDbHelper.createAspiration(newAspiration);
						fillData();
						dialog.cancel();
					}
		    	})
		    	.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		    		public void onClick(DialogInterface dialog, int id) {
		    			dialog.cancel();
		            }	
		        });
	    }	    
	    AlertDialog newAspirationDialog = builder.create();
	    newAspirationDialog.show();
    }    
	
    /**
     * Fetches all aspirations from the database and populate a listView with the data
     */
    private void fillData() {
        aspirations.clear();
        // Get all of the notes from the database and create the item list
        mAspirationsCursor = mDbHelper.fetchAllAspirations();        
        mAspirationsCursor.moveToFirst();
        
        while (!mAspirationsCursor.isAfterLast()){
        	int id = mAspirationsCursor.getInt(0);
        	String description = mAspirationsCursor.getString(1);
        	int numStepsInProgress = mAspirationsCursor.getInt(2);
        	int numStepsCompleted = mAspirationsCursor.getInt(3);
        	Log.v("MURPHY", "Aspiration: "+Integer.toString(id)+" "+description);
        	Aspiration newAsp = new Aspiration(id, description, numStepsInProgress, numStepsCompleted);
        	aspirations.add(newAsp);
        	mAspirationsCursor.moveToNext();
        }
        
        mAdapter = new AspirationAdapter(this, R.layout.aspiration_row, aspirations);
        setListAdapter(mAdapter);
    }
    
    /**
     * On click of an aspiration start a new activity to view the aspiration and its subgoals
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        
        Aspiration a = aspirations.get(position);
        if(a == null){
        	return;
        }
    	
        Intent i = new Intent(this, ShowAspiration.class);
        i.putExtra(AspirationEntry._ID, a.getId());
        i.putExtra(AspirationEntry.COLUMN_NAME_DESCRIPTION, a.getDescription());
        i.putExtra(AspirationEntry.COLUMN_NAME_STEPS_IN_PROGRESS, a.getStepsInProgress());
        i.putExtra(AspirationEntry.COLUMN_NAME_STEPS_COMPLETED, a.getStepsCompleted());
        startActivity(i);
    }
}

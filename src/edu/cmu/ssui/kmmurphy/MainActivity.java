package edu.cmu.ssui.kmmurphy;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import edu.cmu.ssui.kmmurphy.dbAdapter.AspirationEntry;

public class MainActivity extends ListActivity {
	private dbAdapter mDbHelper;
    private Cursor mAspirationsCursor;

	private List<Aspiration> aspirations = new ArrayList<Aspiration>();
	
	private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;

    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mDbHelper = new dbAdapter(this);
		mDbHelper.open();
		// populate the aspirations listview
		fillData();
		// register each aspiration for the context menu
        registerForContextMenu(getListView());
	}
	
	//adding a context menu on long press of an aspiration
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}
	
	public boolean onContextItemSelected(MenuItem item) {
	    switch(item.getItemId()) {
	    case DELETE_ID:
	        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	        if(info.id >= 0 && info.id < aspirations.size()){
	        	Aspiration a = aspirations.get(((int) info.id));
	        	mDbHelper.deleteAspiration(a.getId());
	        }
	        fillData();
	        return true;
	    }
	    return super.onContextItemSelected(item);
	}
    
    public void createAspiration(View v) {
    	/*
    	Intent i = new Intent(this, AspirationEdit.class);
    	startActivityForResult(i, ACTIVITY_CREATE);
    	*/
    	// create an aspiration through a dialog modal
    	
    	
    	
    }    
	
    private void fillData() {
        // Get all of the notes from the database and create the item list
        mAspirationsCursor = mDbHelper.fetchAllAspirations();
        aspirations.clear();
        
        mAspirationsCursor.moveToFirst();
        while (!mAspirationsCursor.isAfterLast()){
        	int id = mAspirationsCursor.getInt(0);
        	String description = mAspirationsCursor.getString(1);
        	int numSteps = mAspirationsCursor.getInt(2);
        	Aspiration newAsp = new Aspiration(id, description, numSteps);
        	aspirations.add(newAsp);
        	mAspirationsCursor.moveToNext();
        }
        mAspirationsCursor.close();
        
        ArrayAdapter<Aspiration> adapter = new ArrayAdapter<Aspiration>(this,
                R.layout.aspiration_row, aspirations);
        setListAdapter(adapter);
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Aspiration a = aspirations.get(((int) id));
    	
        Intent i = new Intent(this, AspirationEdit.class);
        i.putExtra(AspirationEntry._ID, a.getId());
        i.putExtra(AspirationEntry.COLUMN_NAME_DESCRIPTION, a.getDescription());
        i.putExtra(AspirationEntry.COLUMN_NAME_STEPS_COMPLETED, a.getSteps());
        startActivityForResult(i, ACTIVITY_EDIT);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Bundle extras = intent.getExtras();
        switch(requestCode) {
            case ACTIVITY_CREATE:
                String description = extras.getString(AspirationEntry.COLUMN_NAME_DESCRIPTION);
                mDbHelper.createAspiration(description);
                fillData();
                break;
        }
    }


}

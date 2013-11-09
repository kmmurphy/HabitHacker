package edu.cmu.ssui.kmmurphy;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

public class MainActivity extends ListActivity {
	private dbAdapter mDbHelper;
	
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
		// register each apspiration for the context menu
        registerForContextMenu(getListView());
	}

	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_insert);
        return result;
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
        	case INSERT_ID:
        		createAspiration();
        		return true;
        }
      
    	return super.onOptionsItemSelected(item);
    }
    
    private void createAspiration() {
        String noteName = "Note " + mAspirationNumber++;
        mDbHelper.createAspiration(noteName);
        fillData();
    }    
	
    private void fillData() {
        // Get all of the notes from the database and create the item list
        Cursor c = mDbHelper.fetchAllAspirations();
        List<Aspiration> aspirations = new ArrayList<Aspiration>();

        c.moveToFirst();
        while (!c.isAfterLast()){
        	int id = c.getInt(0);
        	String description = c.getString(1);
        	int numSteps = c.getInt(2);
        	Aspiration newAsp = new Aspiration(id, description, numSteps);
        	aspirations.add(newAsp);
        	c.moveToNext();
        }
        c.close();
        
        ArrayAdapter<Aspiration> adapter = new ArrayAdapter<Aspiration>(this,
                R.layout.aspiration_row, aspirations);
        setListAdapter(adapter);
    }

}

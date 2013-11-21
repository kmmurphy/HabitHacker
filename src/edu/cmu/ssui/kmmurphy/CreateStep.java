package edu.cmu.ssui.kmmurphy;


import edu.cmu.ssui.kmmurphy.dbAdapter.StepEntry;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class CreateStep extends Activity {
	private EditText DescriptionText;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.step_edit);
    	
    	DescriptionText = (EditText) findViewById(R.id.descriptionText);

        final Button confirmButton = (Button) findViewById(R.id.confirmButton);
        final Button cancelButton = (Button) findViewById(R.id.cancelButton);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
        	// prefill description and days if the step is being editted
        }
        
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
		        Bundle stepInfo = new Bundle();
		        String description = DescriptionText.getText().toString();
		        String days = "";
		        //create days string
		        if(((CheckBox) findViewById(R.id.checkBox1)).isChecked()){
		        	days += "1";
		        }else {
		        	days += "0";
		        }
		        
		        if(((CheckBox) findViewById(R.id.checkBox2)).isChecked()){
		        	days += "1";
		        }else {
		        	days += "0";
		        }
		        
		        if(((CheckBox) findViewById(R.id.checkBox3)).isChecked()){
		        	days += "1";
		        }else {
		        	days += "0";
		        }
		        
		        if(((CheckBox) findViewById(R.id.checkBox4)).isChecked()){
		        	days += "1";
		        }else {
		        	days += "0";
		        }
		        
		        if(((CheckBox) findViewById(R.id.checkBox5)).isChecked()){
		        	days += "1";
		        }else {
		        	days += "0";
		        }
		        
		        if(((CheckBox) findViewById(R.id.checkBox6)).isChecked()){
		        	days += "1";
		        }else {
		        	days += "0";
		        }
		        
		        if(((CheckBox) findViewById(R.id.checkBox7)).isChecked()){
		        	days += "1";
		        }else {
		        	days += "0";
		        }
		        stepInfo.putString(StepEntry.COLUMN_NAME_DESCRIPTION, description);
		        stepInfo.putString(StepEntry.COLUMN_NAME_DAYS, days);
		        
            	Intent mIntent = new Intent();
		        mIntent.putExtras(stepInfo);
            	setResult(RESULT_OK, mIntent);
		        finish();
            }
        });
        
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
		        Intent mIntent = new Intent();
		        setResult(RESULT_CANCELED, mIntent);
		        finish();
            }
        });
    }
}

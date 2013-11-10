package edu.cmu.ssui.kmmurphy;

import edu.cmu.ssui.kmmurphy.dbAdapter.AspirationEntry;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class AspirationEdit extends Activity{

    private EditText descriptionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aspiration_edit);
        setTitle(R.string.edit_aspiration);

        descriptionText = (EditText) findViewById(R.id.description);

        Button confirmButton = (Button) findViewById(R.id.confirm);


        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Bundle bundle = new Bundle();

                bundle.putString(AspirationEntry.COLUMN_NAME_DESCRIPTION, descriptionText.getText().toString());
                
                Intent mIntent = new Intent();
                mIntent.putExtras(bundle);
                setResult(RESULT_OK, mIntent);
                finish();
            }

        });
    }
}

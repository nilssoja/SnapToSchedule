package com.example.snaptoschedule;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ClassListTest extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_class_list_test);
		
		//Up action bar
		getActionBar().setDisplayHomeAsUpEnabled(true);

		ListView customListView = (ListView) findViewById(R.id.mainListView);
		// This doesnt seem to be working
		customListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		customListView.setItemsCanFocus(true);

		// Define The Data
		// Eventually this will pull from actual schedule using OCR
		String[][] ClassAndTime = { { "Computer Programming", "11am MWF" },
				{ "Calculus", "1pm MWF" }, { "Spanish", "3pm TR" },
				{ "Management in IT", "4pm TR" },
				{ "Rocket Science", "6pm R" }, { "Biology", "8pm MWF" },
				{ "Psychology", "9pm MWF" }, { "Cooking", "8am TR" } };

		// Load ArrayList with Data and Keys via HashMaps
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		HashMap<String, String> item;
		for (int i = 0; i < ClassAndTime.length; i++) {
			item = new HashMap<String, String>();
			item.put("line1", ClassAndTime[i][0]);
			item.put("line2", ClassAndTime[i][1]);
			list.add(item);
		}

		// Create Simple Adapter and set it
		// using created two_lines_layout instead of SimpleListItemActivated2
		SimpleAdapter sa;
		sa = new SimpleAdapter(this, list,
				R.layout.simple_list_item, new String[] {
						"line1", "line2" }, new int[] { android.R.id.text1,
						android.R.id.text2 });
		customListView.setAdapter(sa);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.class_list_test, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
        case R.id.action_confirm:
        //    openSearch();
        	confirmAction();
            return true;
        case R.id.action_settings:
        //    openSettings();
            return true;
        default:
            return super.onOptionsItemSelected(item);
    }
	}

	@Override
	public void onClick(View v) {
		// onClick stuff here
	}
	
	public void confirmAction()
	{
		CharSequence text = "In the future this is where your schedule will be pushed to your calendar.";
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(this, text, duration);
		toast.show();
	}

}

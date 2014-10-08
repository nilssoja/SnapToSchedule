package com.example.snaptoschedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ClassListTest extends Activity {
	
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		System.out.print("Step1");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_class_list_test);
		
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// From http://windrealm.org/tutorials/android/android-listview.php
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// Find the ListView resource.
		
	    ListView scheduleListView = (ListView) findViewById( R.id.mainListView );
	    scheduleListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);   
	    scheduleListView.setItemsCanFocus(false);
	
	    List<Map<String, String>> data = new ArrayList<Map<String, String>>();
	    
	    Map<String, String> datum = new HashMap<String, String>(2);
        datum.put("First Line", "First line of text");
        datum.put("Second Line","Second line of text");
        data.add(datum);
        
        SimpleAdapter adapter = new SimpleAdapter(this, data,
                android.R.layout.simple_list_item_activated_2, 
                new String[] {"First Line", "Second Line" }, 
                new int[] {android.R.id.text1, android.R.id.text2 });
        
	    scheduleListView.setAdapter( adapter );
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
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

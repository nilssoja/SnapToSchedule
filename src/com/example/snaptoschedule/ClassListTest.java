package com.example.snaptoschedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ClassListTest extends Activity {
	
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		System.out.print("Step1");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_class_list_test);
		
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// http://stackoverflow.com/questions/4837834/2-lines-in-a-listview-item
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		
	    ListView scheduleListView = (ListView) findViewById( R.id.mainListView );
	    scheduleListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);   
	    scheduleListView.setItemsCanFocus(false);
	
	    List<Map<String, String>> data = new ArrayList<Map<String, String>>();
	    
	    Map<String, String> datum = new HashMap<String, String>(2);
        datum.put("First Line", "Computer Programming");
        datum.put("Second Line","Room: 223 MWF 9am");
        data.add(datum);
        Map<String, String> datum2 = new HashMap<String, String>(2);
        datum2.put("First Line", "Management in IT");
        datum2.put("Second Line","Room: 333 MWF 11am");
        data.add(datum2);
        Map<String, String> datum3 = new HashMap<String, String>(2);
        datum3.put("First Line", "Calculus");
        datum3.put("Second Line","Room: 12 TR 11am");
        data.add(datum3);
        Map<String, String> datum4 = new HashMap<String, String>(2);
        datum4.put("First Line", "Spanish");
        datum4.put("Second Line","Room: 55 TR 2pm");
        data.add(datum4);
        
        SimpleAdapter adapter = new SimpleAdapter(this, data,
                android.R.layout.simple_list_item_activated_2, 
                new String[] {"First Line", "Second Line"}, 
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

package com.example.snaptoschedule;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends Activity {
	
	//start running m8s i swear onn me mum

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();
        switch (item.getItemId()) {        
        case R.id.action_settings:
            return true;
        default:
            return super.onOptionsItemSelected(item);
    }
    }
    
    public void startClassListTest(View v) {
    	
    	//Place holder code.
    	//TODO: Add code for handling Content Provider data between Gallery and Camera Applications.
    	Context context = getApplicationContext();
    	CharSequence text = "In the future these buttons will take you to either the phones camera or gallery view.";
    	int duration = Toast.LENGTH_LONG;

    	Toast toast = Toast.makeText(context, text, duration);
    	toast.show();
    	
        Intent intent = new Intent(MainActivity.this, ClassListTest.class);
        startActivity(intent);
    }
}

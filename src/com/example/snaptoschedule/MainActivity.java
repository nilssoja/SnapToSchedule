package com.example.snaptoschedule;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.googlecode.tesseract.android.TessBaseAPI;

public class MainActivity extends Activity {
	
	public static final String PACKAGE_NAME = "com.example.snaptoschedule";
	public static final String DATA_PATH = Environment
			.getExternalStorageDirectory().toString() + "/SnapToSchedule/";
	public static final String lang = "eng";
	private static final String TAG = "MainActivity.java";

	// start running m8s i swear onn me mum

	private static final int RESULT_LOAD_IMAGE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

		for (String path : paths) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
					return;
				} else {
					Log.v(TAG, "Created directory " + path + " on sdcard");
				}
			}

		}
		
		// lang.traineddata file with the app (in assets folder)
		// You can get them at:
		// http://code.google.com/p/tesseract-ocr/downloads/list
		// This area needs work and optimization
		if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
			try {

				AssetManager assetManager = getAssets();
				InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
				//GZIPInputStream gin = new GZIPInputStream(in);
				OutputStream out = new FileOutputStream(DATA_PATH
						+ "tessdata/" + lang + ".traineddata");

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				//while ((lenf = gin.read(buff)) > 0) {
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				//gin.close();
				out.close();
				
				Log.v(TAG, "Copied " + lang + " traineddata");
			} catch (IOException e) {
				Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
			}
		}
		
		
		// http://viralpatel.net/blogs/pick-image-from-galary-android-app/
		Button existingPhoto = (Button) findViewById(R.id.existing_button);
		existingPhoto.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, RESULT_LOAD_IMAGE);
				
			}
		});
		
		Button newPhoto = (Button) findViewById(R.id.new_button);
		newPhoto.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			        startActivityForResult(takePictureIntent, RESULT_LOAD_IMAGE);
			    }
				
			}
		});
		
		
		
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
		// int id = item.getItemId();
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void startClassListTest(View v) {

		// Place holder code.
		// TODO: Add code for handling Content Provider data between Gallery and
		// Camera Applications.
		Context context = getApplicationContext();
		CharSequence text = "In the future these buttons will take you to either the phones camera or gallery view.";
		int duration = Toast.LENGTH_LONG;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();

		Intent intent = new Intent(MainActivity.this, ClassListTest.class);
		startActivity(intent);
	}
	
	public void addToCalendar(ScheduleItem SI){
		
		//set beggining and end time beginTime.set takes 5 ints so this might mean breaking down the regex even more
				Calendar beginTime = Calendar.getInstance();
				//this is a begin time of january 8th at 730am
				beginTime.set(SI.getYear(), SI.getMonth(), SI.getDay(), SI.getStartHour(), SI.getStartMinute());
				Calendar endTime = Calendar.getInstance();
				endTime.set(SI.getYear(), SI.getMonth(), SI.getDay(), SI.getEndHour(), SI.getEndMinute());
				
				
				Intent addItemIntent = new Intent(Intent.ACTION_INSERT)
				        .setData(Events.CONTENT_URI)
				        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
				        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
				        .putExtra(Events.TITLE, SI.getTitle())
				        .putExtra(Events.DESCRIPTION, SI.getDescription())
				        .putExtra(Events.EVENT_LOCATION, SI.getLocation())
						.putExtra(Events.RRULE, SI.getRrule());
				startActivity(addItemIntent);

		
		
	}

	// http://viralpatel.net/blogs/pick-image-from-galary-android-app/
	//possibly redo using http://stackoverflow.com/questions/2507898/how-to-pick-an-image-from-gallery-sd-card-for-my-app
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
				&& null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
			bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
			cursor.close();
			
			TessBaseAPI baseApi = new TessBaseAPI();
			baseApi.setDebug(true);
			baseApi.init(DATA_PATH, "eng");
			baseApi.setImage(bitmap);
			
			String recognizedText = baseApi.getUTF8Text();
			
			recognizedText = recognizedText.replace("\n", " ");
			
			Log.v(TAG, recognizedText);
			
			Context context = getApplicationContext();
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(context, recognizedText, duration);
			toast.show();

			baseApi.end();
			parse(recognizedText);
			ImageView imageView = (ImageView) findViewById(R.id.imageView1);
			imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

		}

	}

	protected void parse(String text){
		text = text + " ";
		String new_schedule = text.replaceAll("(WEST|UCBA|C[I1l]|N|WB|EAST|CLER) ", "$1\t");
//		System.out.println("Schedule with tab characters: ");
//		System.out.println(new_schedule);
//		System.out.println("\n");
		
		// Various patterns for matching
		Pattern pattern_building = Pattern.compile("(WEST|UCBA|EAST|CLER)$");
		Pattern pattern_cname = Pattern.compile("N$");   // Begin new schedule item  when found; end other if required 
		
		// MWF 10:10a 11:05a CI
		// group(1) = Days? of week
		// group(2) = Start hour
		// group(3) = Start minute
		// group(4) = start am/pm
		// group(5) = End hour
		// group(6) = End minute
		// group(7) = End am/pm
		// Pattern pattern_time = Pattern.compile("([MTWRF]+)\\s+(\\d+)[:](\\d+)([ap])\\s+(\\d+)[:](\\d+)([ap])\\s+CI$");
		//Pattern pattern_time = Pattern.compile("([MTWRF][MTWRF]*)\\s*(\\d\\d*)[:](\\d\\d*)([ap])\\s*(\\d\\d*)[:](\\d\\d*)([ap])\\s*CI$");
		//Pattern pattern_time = Pattern.compile("^([MTWRF]+)");
		Pattern pattern_time = Pattern.compile("([MTWRF]+)\\s+(\\d+):(\\d+)([ap])\\s+(\\d+):(\\d+)([ap])\\s+C[l1I]$");
		
		// How are we handling online classes with no set time? 
		Pattern pattern_wb = Pattern.compile("WB$");
		
		
		// Now split the above schedule on TAB characters.  Parse the individual strings with the 
		// above patterns. 
		
		ArrayList<ScheduleItem> scheduleList = new ArrayList<ScheduleItem>();
		//ScheduleItem scheduleItemTest = new ScheduleItem();
		//scheduleList.add(scheduleItemTest);
		//scheduleList.get(scheduleList.size() -1).setEndHour(4);
		//System.out.println(scheduleList.get(scheduleList.size()-1).endHour);
		for (String token: new_schedule.split("\t")) {
			Matcher m = pattern_cname.matcher(token);
			if (m.find()) {
				ScheduleItem newItem = new ScheduleItem();
				scheduleList.add(newItem);
				scheduleList.get(scheduleList.size() -1).setTitle(token.substring(0,token.length()-2));
				// Add this value as the course name without the " N"
				
				continue;
			}
			m = pattern_building.matcher(token);
			if (m.find()) {
				scheduleList.get(scheduleList.size() -1).setLocation(token);
				// Add this value as a building
				continue;
			}
			
			// recurrance rule: calIntent.putExtra(Events.RRULE, “FREQ=WEEKLY;COUNT=10;WKST=SU;BYDAY=TU,TH”);
			// http://code.tutsplus.com/tutorials/android-essentials-adding-events-to-the-users-calendar--mobile-8363
			m = pattern_time.matcher(token);
			if (m.find()) {
				String rule = "FREQ=WEEKLY;COUNT=2;WKST=SU;BYDAY=";
				// Add the groups of this value as times
				String days = m.group(1);
				if(m.group(1).contains("M")){
					rule += "MO,";
				} if(m.group(1).contains("T")){
					rule += "TU,";
				} if(m.group(1).contains("W")){
					rule += "WE,";
				} if(m.group(1).contains("R")){
					rule += "TH,";
				} if(m.group(1).contains("F")){
					rule += "FR,";
				}
				rule = rule.substring(0,rule.length()-1);
				scheduleList.get(scheduleList.size()-1).setRrule(rule);
				//Create time variables
				int start_hour = Integer.parseInt(m.group(2));
				int start_min = Integer.parseInt(m.group(3));
				if (m.group(4).equals("p")) {
					start_hour = start_hour + 12;
				}
				// same code with end hour
				int end_hour = Integer.parseInt(m.group(5));
				int end_min = Integer.parseInt(m.group(6));
				if (m.group(7).equals("p")) {
					end_hour += 12;
				}
				
				// set dates/times to scheduleList items
				scheduleList.get(scheduleList.size()-1).setStartHour(start_hour);
				scheduleList.get(scheduleList.size()-1).setStartMinute(start_min);
				scheduleList.get(scheduleList.size()-1).setEndHour(end_hour);
				scheduleList.get(scheduleList.size()-1).setEndMinute(end_min);
				//NOTE: DAY SET IS A PLACEHOLDER FOR NOW
				scheduleList.get(scheduleList.size()-1).setDay(1);
				//NOTE: MONTH SET IS A PLACEHOLDER FOR NOW
				scheduleList.get(scheduleList.size()-1).setMonth(0);
				//NOTE: YEAR SET IS A PLACEHOLDER FOR NOW
				scheduleList.get(scheduleList.size()-1).setYear(2015);
				continue;
			}
			m = pattern_wb.matcher(token);
			if (m.find()) {
				// Handle TBA WB times
				continue;
			}
		}
		for(int i = 0; i < scheduleList.size(); i++){
			addToCalendar(scheduleList.get(i));
		}
	}
}

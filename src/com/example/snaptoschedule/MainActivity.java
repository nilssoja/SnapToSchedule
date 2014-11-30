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
import android.graphics.Matrix;
import android.media.ExifInterface;
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
	private static final String TAG = "MainActivity.java";

	//Path variable used to designate taken picture location.
	protected String _path;
	private static final int RESULT_LOAD_IMAGE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Create tessdata path on SD card. Necessary for OCR usage.
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
		
		// Create traineddata file in tessdata directory (in this case, english).
		if (!(new File(DATA_PATH + "tessdata/" + "eng" + ".traineddata")).exists()) {
			try {

				AssetManager assetManager = getAssets();
				InputStream in = assetManager.open("tessdata/" + "eng" + ".traineddata");
				//GZIPInputStream gin = new GZIPInputStream(in);
				OutputStream out = new FileOutputStream(DATA_PATH
						+ "tessdata/" + "eng" + ".traineddata");

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
				
				Log.v(TAG, "Copied " + "eng" + " traineddata");
			} catch (IOException e) {
				Log.e(TAG, "Was unable to copy " + "eng" + " traineddata " + e.toString());
			}
		}
		
		
		// Create listener for gallery button
		Button existingPhoto = (Button) findViewById(R.id.existing_button);
		existingPhoto.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, RESULT_LOAD_IMAGE);
				
			}
		});
		
		_path = DATA_PATH + "/ocr.jpg";
		Button newPhoto = (Button) findViewById(R.id.new_button);
		newPhoto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				File file = new File(_path);
				Uri outputFileUri = Uri.fromFile(file);
				Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
			    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			        startActivityForResult(takePictureIntent, 0);
			    }
				
			}
		});
		
		
		
	}

	/**
	 * Handles the creation and inflation of the options menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * 
	 */
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


	/**
	 * Method to handle data passed back to Activity from intents. In this case, 
	 * taking a picture from the camera or gallery and passing it through the OCR.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Run if user selects gallery
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
		// Run if user selects "Take Photo"
		else if (requestCode == 0){
			// Set sample size to decode taken image
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 2;
			Bitmap bitmap = BitmapFactory.decodeFile(_path, options); // Create bitmap from taken image
			
			try {
				//Get orientation of taken image and rotate if necessary
				ExifInterface exif = new ExifInterface(_path);
				int exifOrientation = exif.getAttributeInt(
						ExifInterface.TAG_ORIENTATION,
						ExifInterface.ORIENTATION_NORMAL);

				Log.v(TAG, "Orient: " + exifOrientation);

				int rotate = 0;

				switch (exifOrientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					rotate = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					rotate = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					rotate = 270;
					break;
				}

				Log.v(TAG, "Rotation: " + rotate);

				if (rotate != 0) {

					int w = bitmap.getWidth();
					int h = bitmap.getHeight();

					// Setting pre rotate
					Matrix mtx = new Matrix();
					mtx.preRotate(rotate);

					// Rotating Bitmap
					bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
				}

				// Convert to ARGB_8888, required by tess
				bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

			} catch (IOException e) {
				Log.e(TAG, "Couldn't correct orientation: " + e.toString());
			}
			
			Log.v(TAG, "Before baseApi");

			TessBaseAPI baseApi = new TessBaseAPI(); // Create instance of Tesseract
			baseApi.setDebug(true); 
			baseApi.init(DATA_PATH, "eng"); // Set Tesseract Language
			baseApi.setImage(bitmap); // Pass image to Tesseract
			
			String recognizedText = baseApi.getUTF8Text(); // Decode text from image and store
			recognizedText = recognizedText.replace("\n", " "); // Replace all newline characters with a space
			
			Log.v(TAG, recognizedText);
			
			Context context = getApplicationContext();
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(context, recognizedText, duration);
			toast.show();

			baseApi.end();
			parse(recognizedText);
			ImageView imageView = (ImageView) findViewById(R.id.imageView1);
			imageView.setImageBitmap(bitmap);
		}
		

	}

	/**
	 * Used to parse the string generated from the OCR. Through the use of RegEx, tokenize and group the string
	 * into parameters to create schedule items.
	 * @param text - Text data generated from the OCR based on detected text in the user provided image.
	 */
	protected void parse(String text){
		// Place space at end of string for parsing
		text = text + " ";
		// Delimit string via specified delimiters in Regex. replace with tab character
		String new_schedule = text.replaceAll(" (WEST|UCBA|C[I1l]|N|WB|EAST|CLER) ", " $1\t");

		// RegEx used to determine building locations
		Pattern pattern_building = Pattern.compile("(WEST|UCBA|EAST|CLER)\\s*$");
		// RegEx used to locate class title.
		Pattern pattern_cname = Pattern.compile("N$"); 
		
		
		/* Divide string token of time into groups based on the following RegEx
		group(1) = Day(s) of week
		group(2) = Start hour
		group(3) = Start minute
		group(4) = start am/pm
		group(5) = End hour
		group(6) = End minute
		group(7) = End am/pm
		Pattern pattern_time = Pattern.compile("([MTWRF]+)\\s+(\\d+):(\\d+)([AP3])\\s+(\\d+):(\\d+)([AP3])\\s+C[L1I]$");*/	
		Pattern pattern_time = Pattern.compile("([MTWRF]+)\\s+(\\d{1,2}):(\\d{1,2})(.)\\s+(\\d{1,2}):(\\d{1,2})(.)\\s+C.$"); // using . in attempt to get inside time loop
		
		// RegEx used to locate online classes
		Pattern pattern_wb = Pattern.compile("WB$");
		
		// whitespace
		Pattern pattern_white = Pattern.compile("^\\s*$");
		
		// Now split the above schedule on TAB characters.  Parse the individual strings with the 
		// above patterns. 
		ArrayList<ScheduleItem> scheduleList = new ArrayList<ScheduleItem>();
		for (String token: new_schedule.split("\t")) {
			String token_upper = token.toUpperCase();
			Log.v(TAG, "'"+token_upper+"'");
			
			Matcher m = pattern_cname.matcher(token_upper);
			if (m.find()) {
				ScheduleItem newItem = new ScheduleItem();
				scheduleList.add(newItem);
				scheduleList.get(scheduleList.size() -1).setTitle(token.substring(0,token.length()-2));
				// Add this value as the course name without the " N"
				
				continue;
			}
			m = pattern_building.matcher(token_upper);
			if (m.find()) {
				scheduleList.get(scheduleList.size() -1).setLocation(token);
				// Add this value as a building
				continue;
			}
			
			m = pattern_time.matcher(token_upper);
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
				int start_hour = Integer.parseInt(m.group(2)) % 12;
				int start_min = Integer.parseInt(m.group(3));
				if (m.group(4).matches("[0124-9P]")) {
					Log.v(TAG, "PM FOUND: " + token_upper);
					start_hour += 12;
				}
				// same code with end hour
				int end_hour = Integer.parseInt(m.group(5)) % 12;
				int end_min = Integer.parseInt(m.group(6));
				Log.v(TAG, Integer.toString(end_min));
				if (m.group(7).matches("[0124-9P]")) {
					Log.v(TAG, "PM FOUND: " + token_upper);
					end_hour += 12;
				}
				
				// set dates/times to scheduleList items
				scheduleList.get(scheduleList.size()-1).setStartHour(start_hour);
				scheduleList.get(scheduleList.size()-1).setStartMinute(start_min);
				scheduleList.get(scheduleList.size()-1).setEndHour(end_hour);
				scheduleList.get(scheduleList.size()-1).setEndMinute(end_min);
				//NOTE: DAY SET IS A PLACEHOLDER FOR NOW
				scheduleList.get(scheduleList.size()-1).setDay(5);
				//NOTE: MONTH SET IS A PLACEHOLDER FOR NOW
				scheduleList.get(scheduleList.size()-1).setMonth(1);
				//NOTE: YEAR SET IS A PLACEHOLDER FOR NOW
				scheduleList.get(scheduleList.size()-1).setYear(2015);
				continue;
			}
			m = pattern_wb.matcher(token_upper);
			if (m.find()) {
				scheduleList.remove(scheduleList.size()-1);
				continue;
			}
		}
		for(int i = 0; i < scheduleList.size(); i++){
			addToCalendar(scheduleList.get(i));
		}
	}
}

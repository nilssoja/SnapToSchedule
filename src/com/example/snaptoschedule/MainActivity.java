package com.example.snaptoschedule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

	
	public void startGalleryView() {
		
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
			
			Log.v(TAG, recognizedText);
			
			Context context = getApplicationContext();
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(context, recognizedText, duration);
			toast.show();

			baseApi.end();

			ImageView imageView = (ImageView) findViewById(R.id.imageView1);
			imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

		}

	}

}

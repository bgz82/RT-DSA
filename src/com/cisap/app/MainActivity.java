package com.cisap.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MainActivity extends Activity {

	Intent intent = null;
	Spinner array = null;
	Context myContext = null;
	File mediaFile = null;
	OutputStream outputStream = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		array = (Spinner) findViewById(R.id.event);
		myContext = this;
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
			new GetEvents(myContext).execute();
		} else {
			File mediaStorageDir = new File("/sdcard/", "ocisa");
			if (!mediaStorageDir.exists()) {
				// if you cannot make this folder return
				if (!mediaStorageDir.mkdirs()) {
					// If you cannot make this directory
				}
			}
			File eventFile = new File(mediaStorageDir.getPath()
					+ File.separator + "events.txt");
			if (!eventFile.exists()) {
				Log.d("Main Activity", "EventFile doesnt exist");
				try {
					eventFile.createNewFile();
					outputStream = new FileOutputStream(eventFile);
					String event = "sample";
					outputStream.write(event.getBytes());
					outputStream.close();
					ArrayAdapter<String> events;
					List<String> getEvents = new ArrayList<String>();
					getEvents.add(event);
					events = new ArrayAdapter<String>(getApplicationContext(),
							R.layout.spinner_layout, getEvents);
					events.setDropDownViewResource(R.layout.spinner_down);
					array.setAdapter(events);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				FileInputStream fileInputStream = null;
				try {
					fileInputStream = new FileInputStream(eventFile);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(fileInputStream));
				StringBuilder sb = new StringBuilder();
				String line = null;
				try {
					while ((line = reader.readLine()) != null) {
						sb.append(line).append("\n");
					}
					String fileData = sb.toString();
					reader.close();
					fileInputStream.close();
					String[] events = fileData.split(";");
					ArrayAdapter<String> adapter;
					List<String> list;
					list = new ArrayList<String>();
					for (int i = 0; i < events.length; i++) {
						list.add(events[i]);
					}
					adapter = new ArrayAdapter<String>(getApplicationContext(),
							R.layout.spinner_layout, list);
					adapter.setDropDownViewResource(R.layout.spinner_down);
					array.setAdapter(adapter);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}

		}
	}

	public void takePictureClick(View view) {
		intent = new Intent(this, TakePicture.class);
		Spinner spinner_event = (Spinner) findViewById(R.id.event);
		String event = spinner_event.getSelectedItem().toString();
		intent.putExtra("event", event);
		startActivity(intent);

	}

	public void dispPictureClick(View view) {
		intent = new Intent(this, DisplayPictures.class);
		startActivity(intent);

	}

	public void getMap(View view) {

		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {

			intent = new Intent(this, MapPlaces.class);
			startActivity(intent);
		} else {
			AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
			String dispText = "Internet is not available. Google Map cannot be displayed at this time.";
			dlgAlert.setMessage(dispText);
			dlgAlert.setTitle("Network Error");
			dlgAlert.setPositiveButton("OK", null);
			dlgAlert.setCancelable(true);
			dlgAlert.create().show();
		}

	}

	public void uploadData(View view) {

		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
			intent = new Intent(this, UpdateData.class);
			startActivity(intent);
		} else {
			AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
			String dispText = "Internet is not available. Data cannot be uploaded at this time.";
			dlgAlert.setMessage(dispText);
			dlgAlert.setTitle("Network Error");
			dlgAlert.setPositiveButton("OK", null);
			dlgAlert.setCancelable(true);
			dlgAlert.create().show();
		}

	}

	class GetEvents extends AsyncTask {
		Context pContext = null;
		String line = "";
		BufferedReader in = null;
		ArrayList<String> eventValues = null;

		public GetEvents(Context context) {
			pContext = context;
		}

		@Override
		protected Object doInBackground(Object... params) {
			eventValues = new ArrayList<String>();
			// TODO Auto-generated method stub
			// TODO Auto-generated method stub
			try {
				HttpClient httpclient = new DefaultHttpClient();

				HttpGet request = new HttpGet();
				URI website = new URI(
						"http://lasir.umkc.edu:8080/cisaservice/webresources/ocisa/events");
				request.setURI(website);
				HttpResponse response = httpclient.execute(request);
				in = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));

				line = in.readLine();

			} catch (Exception e) {
				Log.d("AddEventException", e.toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object object) {
			if (line != null || !line.isEmpty()) {
				String[] events = line.split(";");
				ArrayAdapter<String> adapter;
				List<String> list;
				list = new ArrayList<String>();
				for (int i = 0; i < events.length; i++) {
					list.add(events[i]);
				}
				adapter = new ArrayAdapter<String>(getApplicationContext(),
						R.layout.spinner_layout, list);
				adapter.setDropDownViewResource(R.layout.spinner_down);
				array.setAdapter(adapter);
				File mediaStorageDir = new File("/sdcard/", "ocisa");
				if (!mediaStorageDir.exists()) {
					// if you cannot make this folder return
					if (!mediaStorageDir.mkdirs()) {
						// If you cannot make this directory
					}
				}
				File eventFile = new File(mediaStorageDir.getPath()
						+ File.separator + "events.txt");
				if (!eventFile.exists()) {
					Log.d("Main Activity", "EventFile doesnt exist");
					try {
						eventFile.createNewFile();
						outputStream = new FileOutputStream(eventFile);
						outputStream.write(line.getBytes());
						outputStream.close();

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						outputStream = new FileOutputStream(eventFile);
						outputStream.write(line.getBytes());
						outputStream.close();

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

		}

	}

}
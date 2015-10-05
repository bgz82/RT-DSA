package com.cisap.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.cisap.app.utils.Image;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cisap.app.utils.GPSTracker;;

public class TakePicture extends Activity {

	int gpsenabled = 0;
	private double lattitude;
	private double longitude;
	private LocationManager locationManager;
	private CameraPreview cameraPreview = null;
	private com.cisap.app.ImagePreview imagePreview = null;
	private Context myContext = null;
	private String imgPath = null;
	private File mediaFile = null;
	private EditText desc = null;
	private Date date;
	String event = null;
	Intent intent = null;
	Fragment currentFragment=null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.takepicture);
		myContext = this;
		cameraPreview = new CameraPreview();
		imagePreview = new ImagePreview();
		desc = (EditText) findViewById(R.id.description);
		Log.d("Image Data", imagePreview.toString());
		getFragmentManager().beginTransaction()
				.add(R.id.linearFrag, cameraPreview).commit();
		Log.d("After Fragment", "Fragment Addedd");
		intent = getIntent();
		event = intent.getStringExtra("event");
		Log.d("TakePicture : Event : ", event);
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			gpsenabled = 1;
			Toast.makeText(this, "GPS is Enabled on your device",
					Toast.LENGTH_SHORT).show();
			getLocation();
		} else {
			showGPSDisabledAlertToUser();
		}
	}

	public void getLocation() {
		GPSTracker gps = new GPSTracker(this);
		lattitude = gps.getLatitude();

		Toast.makeText(this, "Lattitude : " + lattitude, Toast.LENGTH_SHORT)
				.show();
		longitude = gps.getLongitude();
	}

	public void showGPSDisabledAlertToUser() {
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);
			alertDialogBuilder
					.setMessage(
							"GPS is disabled in your device. Please Enable it")
					.setCancelable(false)
					.setPositiveButton("Goto Settings Page To Enable GPS",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									Intent callGPSSettingIntent = new Intent(
											android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
									startActivity(callGPSSettingIntent);
								}
							});
			AlertDialog alert = alertDialogBuilder.create();
			alert.show();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		/*locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Toast.makeText(this, "GPS is Enabled on your device",
					Toast.LENGTH_SHORT).show();
			getLocation();
		} else {
			showGPSDisabledAlertToUser();
		}
*/		if(currentFragment instanceof CameraPreview)
		{
			Log.d("Activit Resume", "create fragment");
			cameraPreview =new CameraPreview();
			getFragmentManager().beginTransaction()
			.add(R.id.linearFrag, cameraPreview).commit();
		}

	}
	
	public void onPause()
	{
		super.onPause();
		currentFragment = getFragmentManager().findFragmentById(R.id.linearFrag);
		
	}


	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	public void saveImage(View v) throws JSONException {
		try {
			createJSONFile();
			changeImageFragment("save");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// resetClickListener

	public void resetImage(View v) {
		// TODO Auto-generated method stub

		changeImageFragment("reset");
	}

	@SuppressWarnings("deprecation")
	public void changeCameraFragment(String filename) {
		date = new Date();
		Log.d("TimeStamp : ", date.toString());
		Bundle bundle = new Bundle();
		bundle.putString("filename", filename);
		bundle.putString("lattitude", lattitude + "");
		bundle.putString("longitude", longitude + "");
		bundle.putString("timestamp", date.toGMTString());
		imagePreview.setArguments(bundle);
		getFragmentManager().beginTransaction()
				.replace(R.id.linearFrag, imagePreview).commit();
	}

	public void changeImageFragment(String event) {

		if (event.equals("reset"))
		{
			File imgDelete = new File(imgPath);
			if (imgDelete.exists()) {
				if (imgDelete.delete()) {
					Log.d("RESET", "Image Deleted");
				} else {
					Log.d("RESET", "Image Deletion Failed");
				}
			}
			System.gc();
		}
		getFragmentManager().beginTransaction()
				.replace(R.id.linearFrag, cameraPreview).commit();
	}

	public void storeImage(byte[] data) {
		try {
			File mediaStorageDir = new File("/sdcard/", "ocisa");
			if (!mediaStorageDir.exists()) {
				// if you cannot make this folder return
				if (!mediaStorageDir.mkdirs()) {
					// If you cannot make this directory
				}
			}

			// take the current timeStamp
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
					.format(new Date());
			// and make a media file:
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");

			if (mediaFile != null) {

				try {
					// write the file
					FileOutputStream fos = new FileOutputStream(mediaFile);
					fos.write(data);
					fos.close();
					Toast toast = Toast.makeText(myContext, "Picture saved: "
							+ mediaFile.getName(), Toast.LENGTH_LONG);
					toast.show();
					imgPath = mediaFile.getAbsolutePath();
				} catch (FileNotFoundException e) {
				} catch (IOException e) {
				}
			}
		} finally {
		}
		changeCameraFragment(mediaFile.getAbsolutePath());
	}

	public void createXML() throws SAXException, IOException,
			ParserConfigurationException {
		String description = "Description Not Available";
		if (desc != null) {
			description = desc.getText().toString();
		}
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory
				.newDocumentBuilder();
		// Log.d("Media File", mediaFile.toString());
		if (mediaFile != null) {
			File mediaStorageDir = new File("/sdcard/", "ocisa");
			if (!mediaStorageDir.exists()) {
				// if you cannot make this folder return
				if (!mediaStorageDir.mkdirs()) {
					// If you cannot make this directory
				}
			}
			File file = new File(mediaStorageDir.getPath() + File.separator
					+ "updateImages.xml");
			if (!file.exists()) {
				Log.d("In new File", "New File");
				if (file.createNewFile()) {
					Document document = documentBuilder.newDocument();
					date = new Date();
					Image addImage = new Image(
							mediaFile.getAbsolutePath().toString(),
							description, date.toString(), lattitude + "",
							longitude + "", event, "No");
					Element root = document.createElement("Images");
					document.appendChild(root);
					Element newImage = document.createElement("Image");
					root.appendChild(newImage);
					Element ImageURL = document.createElement("ImageURL");
					ImageURL.appendChild(document.createTextNode(addImage
							.getImageURL()));
					newImage.appendChild(ImageURL);

					Element Description = document.createElement("Description");
					ImageURL.appendChild(document.createTextNode(addImage
							.getDescripiton()));
					newImage.appendChild(Description);

					Element TimeStamp = document.createElement("TimeStamp");
					ImageURL.appendChild(document.createTextNode(addImage
							.getTimeStamp()));
					newImage.appendChild(TimeStamp);

					Element Lattitude = document.createElement("Lattitude");
					ImageURL.appendChild(document.createTextNode(addImage
							.getLattitude()));
					newImage.appendChild(Lattitude);

					Element Longitude = document.createElement("Longitude");
					ImageURL.appendChild(document.createTextNode(addImage
							.getLongitude()));
					newImage.appendChild(Longitude);

					root.appendChild(newImage);

					DOMSource source = new DOMSource(document);

					TransformerFactory transformerFactory = TransformerFactory
							.newInstance();
					Transformer transformer;
					try {
						transformer = transformerFactory.newTransformer();
						StreamResult result = new StreamResult(
								file.getAbsolutePath());
						transformer.transform(source, result);
					} catch (TransformerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Toast toast = Toast.makeText(myContext, "XML File Updated "
							+ mediaFile.getName(), Toast.LENGTH_LONG);
					toast.show();
				} else {
					Log.d("XML File Creation", "Failed");
				}

			} else {
				Log.d("In old File", "Old File");
				Image addImage = new Image(
						mediaFile.getAbsolutePath().toString(), description,
						new Date().toString(), lattitude + "", longitude + "", event, "No");
				Document document = documentBuilder.parse(file
						.getAbsolutePath());
				Element root = document.getDocumentElement();
				Element newImage = document.createElement("Image");
				root.appendChild(newImage);
				Element ImageURL = document.createElement("ImageURL");
				ImageURL.appendChild(document.createTextNode(addImage
						.getImageURL()));
				newImage.appendChild(ImageURL);

				Element Description = document.createElement("Description");
				ImageURL.appendChild(document.createTextNode(addImage
						.getDescripiton()));
				newImage.appendChild(Description);

				Element TimeStamp = document.createElement("TimeStamp");
				ImageURL.appendChild(document.createTextNode(addImage
						.getTimeStamp()));
				newImage.appendChild(TimeStamp);

				Element Lattitude = document.createElement("Lattitude");
				ImageURL.appendChild(document.createTextNode(addImage
						.getLattitude()));
				newImage.appendChild(Lattitude);

				Element Longitude = document.createElement("Longitude");
				ImageURL.appendChild(document.createTextNode(addImage
						.getLongitude()));
				newImage.appendChild(Longitude);

				root.appendChild(newImage);

				DOMSource source = new DOMSource(document);

				TransformerFactory transformerFactory = TransformerFactory
						.newInstance();
				Transformer transformer;
				try {
					transformer = transformerFactory.newTransformer();
					StreamResult result = new StreamResult(
							file.getAbsolutePath());
					transformer.transform(source, result);
				} catch (TransformerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Toast toast = Toast.makeText(myContext, "XML File Updated "
						+ mediaFile.getName(), Toast.LENGTH_LONG);
				toast.show();

			}

		}

	}

	public void createJSONFile() throws JSONException, IOException {
		String description = "Description Not Available";
		desc = (EditText)findViewById(R.id.description);
		Log.d("Description", desc.getText().toString());
		FileOutputStream outputStream;
		String imgName="";
		if (desc != null) {
			description = desc.getText().toString();
		}
		if (mediaFile != null) {
			File mediaStorageDir = new File("/sdcard/", "ocisa");
			if (!mediaStorageDir.exists()) {
				// if you cannot make this folder return
				if (!mediaStorageDir.mkdirs()) {
					// If you cannot make this directory
				}
			}
			File logFile = new File(mediaStorageDir.getPath()
					+ File.separator + "updateImages.log");
			if (!logFile.exists()) {
				Log.d("Update Images", "LogFile not found");
				try {
					logFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			File file = new File(mediaStorageDir.getPath() + File.separator
					+ "updateImages.json");
			if (!file.exists()) {
				Log.d("In new File", "File" + file.getAbsolutePath());
				if (file.createNewFile()) {
					Image addImage = new Image(
							mediaFile.getAbsolutePath().toString(),
							description, date.toString(), lattitude + "",
							longitude + "", event, "No");
					JSONObject imageData = new JSONObject();
					imageData.put("ImageURL", addImage.getImageURL());
					imageData.put("Description", addImage.getDescripiton());
					imageData.put("TimeStamp", addImage.getTimeStamp());
					imageData.put("Lattitude", addImage.getLattitude());
					imageData.put("Longitude", addImage.getLongitude());
					imageData.put("Event", addImage.getEvent());
					imageData.put("Updated", addImage.getUpdated());
					imgName = addImage.getImageURL();
					Log.d("JSONObjetc", imageData.toString());
					try {
						Log.d("Absolute Path", file.getAbsolutePath());
						outputStream = new FileOutputStream(file);
						outputStream.write(imageData.toString().getBytes());
						outputStream.close();
						Toast toast = Toast.makeText(myContext,
								"JSON File Created :  " + mediaFile.getName(),
								Toast.LENGTH_LONG);
						toast.show();
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {
					Log.d("JSON File Creation", "Failed");
				}

			} else {
				Log.d("In old File", "Old File");
				Image addImage = new Image(
						mediaFile.getAbsolutePath().toString(), description,
						date.toString(), lattitude + "", longitude + "", event, "No");
				JSONObject imageData = new JSONObject();
				imageData.put("ImageURL", addImage.getImageURL());
				imageData.put("Description", addImage.getDescripiton());
				imageData.put("TimeStamp", addImage.getTimeStamp());
				imageData.put("Lattitude", addImage.getLattitude());
				imageData.put("Longitude", addImage.getLongitude());
				imageData.put("Event", addImage.getEvent());
				imageData.put("Updated", addImage.getUpdated());
				imgName = addImage.getImageURL();
				FileInputStream fileInputStream = new FileInputStream(file);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(fileInputStream));
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line).append("\n");
				}
				String fileData = sb.toString();
				reader.close();
				fileInputStream.close();
				JSONObject jsonObject = new JSONObject(fileData);
				JSONObject resultData = new JSONObject();
				JSONArray jsonArray = jsonObject.optJSONArray("Images"); 
				if(jsonArray != null)
				{
					jsonArray.put(imageData);
					resultData.put("Images", jsonArray);
				}
				else
				{
					jsonArray = new JSONArray();
					jsonArray.put(jsonObject);
					jsonArray.put(imageData);
					resultData.put("Images", jsonArray);
				}
				Log.d("JSON Content", resultData.toString());
				try {
					outputStream = new FileOutputStream(file, false);
					outputStream.write(resultData.toString().getBytes());
					outputStream.close();
					Toast toast = Toast.makeText(myContext,
							"JSON File Updated :  " + mediaFile.getName(),
							Toast.LENGTH_LONG);
					toast.show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try{
				outputStream = new FileOutputStream(logFile,
						false);
				String log = new Date().toGMTString().toString() + "  : " + "Event : " + event + " :  InsertImages :" + "  " + imgName + "\n";
				outputStream.write(log.getBytes());
				outputStream.close();
				
			}
			catch(Exception e){}
		}		
		else {
			Log.d("JSON File Creation", "Failed");
		}
	}


}

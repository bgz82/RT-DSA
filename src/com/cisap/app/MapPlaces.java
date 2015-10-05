package com.cisap.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cisap.app.utils.Image;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class MapPlaces extends Activity implements OnMapReadyCallback {

	Context myContext = null;
	GoogleMap mMap;
	MapView mapView = null;
	LatLng latLng = null;
	ArrayList<Image> data = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapfragment);
		MapFragment mapFragment = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}

	@Override
	public void onMapReady(GoogleMap map) {

		map.setMyLocationEnabled(true);
		ArrayList<Image> data = null;
		try {
			Log.d("Entering jsonfile", "load JSON");
			data = loadDataFromJSON();
			if (data != null) {
				Log.d("Entering jsonfile", "JSON Data ");
				for (int i = 0; i < data.size(); i++) {
					Log.d("In Markers", i+"");
					latLng = new LatLng(Double.parseDouble(data.get(i)
							.getLattitude()), Double.parseDouble(data.get(i)
							.getLongitude()));
					map.addMarker(new MarkerOptions().position(latLng).title(
							data.get(i).getDescripiton()));
				    }
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(
						new LatLng(Double.parseDouble(data.get(0)
								.getLattitude()), Double.parseDouble(data
								.get(0).getLongitude())), 13));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	private ArrayList<Image> loadDataFromJSON() throws IOException,
			JSONException {
		ArrayList<Image> Data = null;
		File mediaStorageDir = new File("/sdcard/", "ocisa");
		if (!mediaStorageDir.exists()) {
			// if you cannot make this folder return
			if (!mediaStorageDir.mkdirs()) {
				// If you cannot make this directory
				return null;
			}
		}
		File file = new File(mediaStorageDir.getPath() + File.separator
				+ "updateImages.json");
		if (!file.exists()) {
			Log.d("AdapterView", "No files Found");
			return null;

		} else {
			Data = new ArrayList<Image>();
			int i = 0;
			FileInputStream fileInputStream = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					fileInputStream));
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
			if (jsonArray != null) {
				while (i < jsonArray.length()) {
					JSONObject temp = new JSONObject();
					temp = jsonArray.optJSONObject(i);
					if (temp != null) {
						Image imageTemp = new Image(temp.getString("ImageURL"),
								temp.getString("Description"),
								temp.getString("TimeStamp"),
								temp.getString("Lattitude"),
								temp.getString("Longitude"),
								temp.getString("Event"),
								temp.getString("Updated"));
						Log.d("In JSON Array", temp.getString("ImageURL"));
						Data.add(imageTemp);
					}

					i++;
				}
			} else {
				if (jsonObject != null) {
					Image imageTemp = new Image(
							jsonObject.getString("ImageURL"),
							jsonObject.getString("Description"),
							jsonObject.getString("TimeStamp"),
							jsonObject.getString("Lattitude"),
							jsonObject.getString("Longitude"),
							jsonObject.getString("Event"),
							jsonObject.getString("Updated"));
					Log.d("In JSON Object", jsonObject.getString("ImageURL"));
					Data.add(imageTemp);
				}
			}

		}
		return Data;

	}

}

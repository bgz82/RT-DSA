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

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;



public class DisplayPictures extends Activity {
	final static String TAG = "DisplayPictures";
	ArrayList<Image> sceneList;
	SceneListAdapter dataAdapter = null;
	ListView listView = null;
	Activity context = null;
	int imageHeight;
	int imageWidth;
	String imageType;
	BitmapFactory.Options options;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pictureslist);
		options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(getResources(), R.id.imageicon, options);
		imageHeight = options.outHeight;
		imageWidth = options.outWidth;
		imageType = options.outMimeType;
		context = this;
		listView = (ListView) findViewById(R.id.imageList);
		sceneList = new ArrayList<Image>();
		dataAdapter = new SceneListAdapter(this, R.layout.imageadapter_layout,sceneList);
		try {
			loadDataFromJSON();
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loadDataFromJSON() throws IOException, JSONException {
		File mediaStorageDir = new File("/sdcard/", "ocisa");
		if (!mediaStorageDir.exists()) {
			// if you cannot make this folder return
			if (!mediaStorageDir.mkdirs()) {
				// If you cannot make this directory
			}
		}
		File file = new File(mediaStorageDir.getPath() + File.separator
				+ "updateImages.json");
		if (!file.exists()) {
			Log.d("AdapterView","No files Found");
		}
		else
		{
			int i=0;
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
				while(i < jsonArray.length())
				{
					JSONObject temp = new JSONObject();
					temp = jsonArray.optJSONObject(i);
					if(temp != null)
					{
						Image imageTemp = new Image(temp.getString("ImageURL"), temp.getString("Description"), temp.getString("TimeStamp"), temp.getString("Lattitude"), temp.getString("Longitude"), temp.getString("Event"), temp.getString("Updated"));
						Log.d("In JSON Array", temp.getString("ImageURL"));
						sceneList.add(imageTemp);
						dataAdapter.add(imageTemp);
					}
					i++;
				}
			}
			else
			{
				if(jsonObject != null)
				{
					Image imageTemp = new Image(jsonObject.getString("ImageURL"), jsonObject.getString("Description"), jsonObject.getString("TimeStamp"), jsonObject.getString("Lattitude"), jsonObject.getString("Longitude"), jsonObject.getString("Event"), jsonObject.getString("Updated"));
					Log.d("In JSON Object", jsonObject.getString("ImageURL"));
					sceneList.add(imageTemp);
					dataAdapter.add(imageTemp);
				}
			}
			listView.setAdapter(dataAdapter);

		}
		
	}

	class SceneListAdapter extends ArrayAdapter<Image> {

		private class ViewHolder {
			ImageView image;
			TextView lattitude;
			TextView longitude;
			TextView description;
			TextView timestamp;
			TextView updated;
		}

		private ArrayList<Image> sceneList;

		public SceneListAdapter(Context listHolderFragment, int itemInfo,
				ArrayList<Image> sceneList) {
			super(listHolderFragment, itemInfo, sceneList);
			Log.d("Adapter   ", "Object Created");
			this.sceneList = new ArrayList<Image>();
			this.sceneList.addAll(sceneList);
		}

		public void add(Image scene) {
			Log.d(TAG, "scenelist size: " + this.sceneList.size());
			this.sceneList.add(scene);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			DisplayPictures disp = new DisplayPictures();
			Log.d("Adapter", "In View");
			ViewHolder holder = null;
			View view;
			LayoutInflater vi = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				view = vi.inflate(R.layout.imageadapter_layout, parent, false);
			} else
				view = convertView;
			holder = new ViewHolder();
			holder.image = (ImageView) view.findViewById(R.id.imageicon);
			holder.lattitude = (TextView) view.findViewById(R.id.latt);
			holder.description = (TextView) view.findViewById(R.id.description);
			holder.longitude = (TextView) view.findViewById(R.id.longg);
			holder.timestamp = (TextView) view.findViewById(R.id.time);
			holder.updated = (TextView) view.findViewById(R.id.updated);
			Image currentScene = sceneList.get(position);
			Bitmap myBitmap = DisplayPictures.decodeSampledBitmapFromResource(currentScene.getImageURL(),200,200);
			Matrix matrix = new Matrix();
			matrix.postRotate(90);
			Bitmap resultBitmap = Bitmap
					.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(),
							myBitmap.getHeight(), matrix, true );
			holder.image.setImageBitmap(resultBitmap);	
			holder.lattitude.setText("Lat: " + currentScene.getLattitude());
			holder.longitude.setText("Lon: " + currentScene.getLongitude());
			holder.description.setText("Description: "
					+ currentScene.getDescripiton());
			holder.updated.setText("Updated : " + currentScene.getUpdated());
			holder.timestamp.setText("Time: " + currentScene.getTimeStamp());
            holder.lattitude.setTextColor(Color.parseColor("#0066FF"));
            holder.longitude.setTextColor(Color.parseColor("#0066FF"));
            holder.description.setTextColor(Color.parseColor("#0066FF"));
            holder.timestamp.setTextColor(Color.parseColor("#0066FF"));
            holder.updated.setTextColor(Color.parseColor("#0066FF"));
			return view;
		}

	}
	
	public static Bitmap decodeSampledBitmapFromResource(String url,int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(url, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(url, options);
	}
	
	public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

        final int halfHeight = height / 2;
        final int halfWidth = width / 2;

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while ((halfHeight / inSampleSize) > reqHeight
                && (halfWidth / inSampleSize) > reqWidth) {
            inSampleSize *= 2;
        }
    }

    return inSampleSize;
}
	
}

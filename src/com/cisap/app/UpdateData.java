package com.cisap.app;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cisap.app.utils.Base64;
import com.cisap.app.utils.Image;
import com.cisap.app.utils.SendImage;
import com.google.gson.Gson;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateData extends Activity {

	final static String TAG = "DisplayPictures";
	ArrayList<Image> sceneList;
	SceneListAdapter dataAdapter = null;
	ListView listView = null;
	Activity context = null;
	int imageHeight;
	int imageWidth;
	String imageType;
	BitmapFactory.Options options;
	ArrayList<Image> data = null;
	JSONArray jsonArray = null;
	JSONObject jsonObject = null;
	Context myContext = null;
	int pCount = 0;
	ProgressBar pBar = null;
	TextView hideText = null;
	TextView status = null;
    //ProgressDialog progress = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.updatepictureslist);
		pBar = (ProgressBar) findViewById(R.id.pbar);
		myContext = this;
		hideText = (TextView) findViewById(R.id.hideText);
		status = (TextView) findViewById(R.id.status);
		sceneList = new ArrayList<Image>();
		options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(getResources(), R.id.imageicon, options);
		imageHeight = options.outHeight;
		imageWidth = options.outWidth;
		imageType = options.outMimeType;
		context = this;
		listView = (ListView) findViewById(R.id.imageList1);
		sceneList = new ArrayList<Image>();
		try {
			loadDataFromJSON();
			if (sceneList.size() > 0) {
				Log.d("After PCOUNT++", pCount+"");
				DataUpdate dataUpdate = new DataUpdate(sceneList, jsonObject,
						myContext, pBar);
				dataUpdate.execute();
			}
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
			Log.d("AdapterView", "No files Found");
		} else {
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
			jsonObject = new JSONObject(fileData);
			jsonArray = jsonObject.optJSONArray("Images");
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
						Log.d("UpdateImages", temp.getString("Updated"));
						sceneList.add(imageTemp);
						if(imageTemp.getUpdated().equals("No"))
						{
							pCount++;
						}
                      
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
					Log.d("UpdateImages", jsonObject.getString("Updated"));
					if(imageTemp.getUpdated().equals("No"))
					{
						pCount++;
					}
					sceneList.add(imageTemp);

				}
			}

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
			Log.d("Data Adapter List", "Posiiton : " + position);
			Image currentScene = this.sceneList.get(position);
			Bitmap myBitmap = DisplayPictures.decodeSampledBitmapFromResource(
					currentScene.getImageURL(), 200, 200);
			Matrix matrix = new Matrix();
			matrix.postRotate(90);
			Bitmap resultBitmap = Bitmap.createBitmap(myBitmap, 0, 0,
					myBitmap.getWidth(), myBitmap.getHeight(), matrix, true);
			holder.image.setImageBitmap(resultBitmap);
			holder.lattitude.setText("Lat: " + currentScene.getLattitude());
			holder.longitude.setText("Lon: " + currentScene.getLongitude());
			holder.description.setText("Description: "
					+ currentScene.getDescripiton());
			holder.timestamp.setText("Time: " + currentScene.getTimeStamp());
			//holder.updated.setText("Updated : " + currentScene.getUpdated());
			holder.lattitude.setTextColor(Color.parseColor("#0066FF"));
			holder.longitude.setTextColor(Color.parseColor("#0066FF"));
			holder.description.setTextColor(Color.parseColor("#0066FF"));
			holder.timestamp.setTextColor(Color.parseColor("#0066FF"));
			holder.updated.setTextColor(Color.parseColor("#0066FF"));
			return view;
		}

	}

	public static Bitmap decodeSampledBitmapFromResource(String url,
			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(url, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(url, options);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	public class DataUpdate extends AsyncTask<Object, Integer, Object> {

		ArrayList<Image> dataList = null;
		HttpClient httpclient = null;
		HttpPost httpPost = null;
		StringEntity se = null;
		HttpResponse httpResponse = null;
		String result = "";
		InputStream inputStream = null;
		SendImage sendImage = null;
		String imageData = null;
		JSONObject tempJSON = null;
		Gson gson = null;
		int bytesRead = -1;
		byte[] buffer = null;
		JSONObject jobj = null;
		ByteArrayOutputStream byteArrayOutputStream = null;
		Context myContext = null;
		OutputStream outputStream;
		ArrayList<Image> imageList;
		ProgressDialog progress=null;
		ProgressBar pBar = null;
		RelativeLayout relativeLayout = null;
		int update;
		ContentLoadingProgressBar clpb = null;
		String Url = "http://lasir.umkc.edu:8080/cisaservice/webresources/ocisa/imagingdata?event=oevents";
		public DataUpdate(ArrayList<Image> data, JSONObject jObject,
				Context context, ProgressBar p) {
			dataList = data;
			imageList = new ArrayList<Image>();
			jobj = jObject;
			myContext = context;
			tempJSON = new JSONObject();
			httpclient = new DefaultHttpClient();
			gson = new Gson();
			//clpb = (ContentLoadingProgressBar)p;
		    pBar = p;
		    pBar.setMax(100);
		    progress = new ProgressDialog(myContext);
		}
		
		@Override
		protected void onPreExecute()
		{
			update = 0;
			progress.setMessage("Uploading Images to cloud. Please wait ...");
			progress.show();
			status.setText("0%");
		}

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			File mediaStorageDir = new File("/sdcard/", "ocisa");
			if (!mediaStorageDir.exists()) {
				// if you cannot make this folder return
				return null;
			}
			File file = new File(mediaStorageDir.getPath() + File.separator
					+ "updateImages.json");
			if (!file.exists()) {
				Log.d("AdapterView", "No files Found");
				return null;
			} else {
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
				jsonArray = jobj.optJSONArray("Images");
				if (jsonArray != null) {
					int i = 0;
					progress.dismiss();
					pBar.setVisibility(View.VISIBLE);
					pBar.setProgress(0);
					//clpb.setVisibility(View.VISIBLE);
					//clpb.setProgress(0);
					Log.d("Data List Size ", dataList.size() + "");
					while (i < dataList.size()) {
						try {
							progress.dismiss();
							if (dataList.get(i).getUpdated().equals("No")) {
								Log.d("AsyncTask ", "Post Image");
								imageData = Base64.encodeFromFile(dataList.get(
										i).getImageURL());
								sendImage = new SendImage(imageData, dataList
										.get(i).getDescripiton(), dataList.get(
										i).getTimeStamp(), dataList.get(i)
										.getLattitude(), dataList.get(i)
										.getLongitude(), dataList.get(i)
										.getEvent());
								httpPost = new HttpPost(Url);
								se = new StringEntity(gson.toJson(sendImage));
								httpPost.setEntity(se);
								httpPost.setHeader("Accept", "application/json");
								httpPost.setHeader("Content-type",
										"application/json");
								httpResponse = httpclient.execute(httpPost);
								Log.d("AsyncTask", httpResponse.toString());
								BufferedReader reader = new BufferedReader(
										new InputStreamReader(httpResponse
												.getEntity().getContent(),
												"UTF-8"));
								result = reader.readLine();
								Log.d("AsyncTask", result);
								if (result.equals("0")) {
									update++;
									Log.d("AsyncTask", "In updateFile");
									jsonArray.getJSONObject(i).put("Updated",
											"yes");
									tempJSON.put("Images", jsonArray);
									try {
										outputStream = new FileOutputStream(
												file, false);
										outputStream.write(tempJSON.toString()
												.getBytes());
										outputStream.close();
										outputStream = new FileOutputStream(
												logFile, false);
										String log = new Date().toGMTString()
												.toString()
												+ "  : "
												+ "UpdateImages :"
												+ "  "
												+ dataList.get(i).getImageURL()
												+ "  Event:  "
												+ dataList.get(i).getEvent()
												+ "\n";
										outputStream.write(log.getBytes());
										outputStream.close();
										imageList.add(dataList.get(i));
										Log.d("PCOUNT", pCount+"");
										Log.d("u Value", update+"");
										publishProgress(Math.round((update /(float) pCount ) * 100));
										Log.d("ProgressDialog", Math.round((update /(float) pCount ) * 100) + "");
									} catch (Exception e) {
										e.printStackTrace();
									}

								}
							}
						} catch (Exception e) {
						}
						i++;
					}
				} else {
					if (jobj != null) {
						int i = 0;
						Image imageTemp = null;
						try {
							progress.dismiss();
							if (jobj.getString("Updated").equals("No")) {
								imageTemp = new Image(
										jobj.getString("ImageURL"),
										jobj.getString("Description"),
										jobj.getString("TimeStamp"),
										jobj.getString("Lattitude"),
										jobj.getString("Longitude"),
										jobj.getString("Event"),
										jobj.getString("Updated"));
								Log.d("In JSON Object",
										jobj.getString("ImageURL"));
								Log.d("AsyncTask ", "Post Image");
								imageData = Base64.encodeFromFile(dataList.get(
										i).getImageURL());
								sendImage = new SendImage(imageData, dataList
										.get(i).getDescripiton(), dataList.get(
										i).getTimeStamp(), dataList.get(i)
										.getLattitude(), dataList.get(i)
										.getLongitude(), dataList.get(i)
										.getEvent());
								httpPost = new HttpPost(Url);
								se = new StringEntity(gson.toJson(sendImage));
								httpPost.setEntity(se);
								httpPost.setHeader("Accept", "application/json");
								httpPost.setHeader("Content-type",
										"application/json");
								httpResponse = httpclient.execute(httpPost);
								Log.d("AsyncTask", httpResponse.toString());
								BufferedReader reader = new BufferedReader(
										new InputStreamReader(httpResponse
												.getEntity().getContent(),
												"UTF-8"));
								result = reader.readLine();
								Log.d("AsyncTask", result);
								if (result.equals("0")) {
									update++;
									Log.d("AsyncTask", "In updateFile");
									jobj.put("Updated", "yes");
									publishProgress(100);
									// tempJSON.put("Images", jsonArray);
									try {
										outputStream = new FileOutputStream(
												file, false);
										outputStream.write(jobj.toString()
												.getBytes());
										outputStream.close();
										outputStream = new FileOutputStream(
												logFile, false);
										String log = new Date().toGMTString()
												.toString()
											
												+ "  : "
												+ "UpdateImages :"
												+ "  "
												+ dataList.get(i).getImageURL()
												+ "  Event:  "
												+ dataList.get(i).getEvent()
												+ "\n";
										outputStream.write(log.getBytes());
										outputStream.close();
									/*	if (i == 0) {
											listView.setAdapter(dataAdapter);
										}
									*/	imageList.add(dataList.get(i));
										Log.d("AsyncTask", "Before Toast");

									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						} catch (JSONException | IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// dataAdapter.add(imageTemp);
					}
				}
			}
			return null;
		}
		@Override
		protected void onProgressUpdate(Integer... progress) {
	        Log.d("UpdateProgress", progress[0]+"");		
			pBar.setProgress(progress[0]);
			status.setText(progress[0] + " %");
	        //clpb.setProgress(progress[0]);
	
	     }


		@Override
		protected void onPostExecute(Object obj) {
			pBar.setVisibility(View.GONE);
			hideText.setVisibility(View.GONE);
			dataAdapter = new SceneListAdapter(myContext, R.layout.imageadapter_layout,
					imageList);
			Log.d("PostExecute", dataAdapter.sceneList.size()+"");
			listView.setAdapter(dataAdapter);
			dataAdapter.notifyDataSetChanged();
		}

	}

}

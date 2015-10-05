package com.cisap.app;

import java.io.File;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ImagePreview extends Fragment {

	private Activity myContext = null;
	private ImageView imageView = null;
	private Bitmap myBitmap = null;
	private View v = null;
	private String filename = null;
	private TextView timestamp = null;
	private TextView lattitude = null;
	private TextView longitude = null;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		myContext = activity;
		Log.d("In Attach", "Balu");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("In on create bundle", " Picture Preview Created ");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		System.gc();
		// takePicture = (TakePicture) this.getActivity();
		if (this.getArguments() != null) {
			filename = this.getArguments().getString("filename");
			Log.d("FileName", filename);
			Log.d("PV", " Picture Preview creating view");
			Log.d("PV", " Inflating testlayout ");
			v = inflater.inflate(R.layout.viewimage, container, false);
			imageView = (ImageView) v.findViewById(R.id.image);
			timestamp = (TextView) v.findViewById(R.id.TimeStamp);
			lattitude = (TextView) v.findViewById(R.id.Lattitude);
			longitude = (TextView) v.findViewById(R.id.Longitude);
			timestamp.setText(this.getArguments().getString("timestamp"));
			lattitude.setText(this.getArguments().getString("lattitude"));
			longitude.setText(this.getArguments().getString("longitude"));
			Display display = myContext.getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			int height = size.y;
			height = (int) (height - height * 0.2);
			imageView.getLayoutParams().height = height;
			File imgFile = new File(filename);
			if (imgFile.exists()) {
				myBitmap = DisplayPictures.decodeSampledBitmapFromResource(imgFile.getAbsolutePath(), 300, 300);
				//myBitmap.
				if(CameraPreview.portrait.equals("landscape"))
				{
				Matrix matrix = new Matrix();
				matrix.postRotate(90);
				Bitmap resultBitmap = Bitmap
						.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(),
								myBitmap.getHeight(), matrix, true );
				imageView.setImageBitmap(resultBitmap);
				}
				else
				{
				imageView.setImageBitmap(myBitmap);
				}
				//imageView.setImageBitmap(myBitmap);
				//imageView.setRotation(90);
			}
			Log.d("Before view return", "About to return");
			//System.gc();

		}
		return v;
	}

}

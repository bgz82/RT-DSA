package com.cisap.app;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.widget.TextView;

public class CameraPreview extends Fragment implements SurfaceHolder.Callback {

	TextView testView;
	Context myContext = null;
	TakePicture takePicture = null;
	Camera camera;
	SurfaceView surfaceView;
	SurfaceHolder surfaceHolder;
	File mediaFile;
	PictureCallback rawCallback;
	ShutterCallback shutterCallback;
	PictureCallback jpegCallback;
	View v = null;
	static String portrait = "landscape";
	int id;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		myContext = activity;

		if (camera == null)
			Log.d("PV", "Camera instance is null");
		else
			Log.d("PV", "Camera instance is not null");

		Log.d("PV", " Picture Preview attached ");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d("PV", " Picture Preview Created ");
	}

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		takePicture = (TakePicture) this.getActivity();
		super.onCreateView(inflater, container, savedInstanceState);
		Log.d("PV", " Picture Preview creating view");
		Log.d("PV", " Inflating testlayout ");
		v = inflater.inflate(R.layout.camerapreview, container, false);
		surfaceView = (SurfaceView) v.findViewById(R.id.surfaceView);
		Log.d("PV", " Inflated testlayout ");
		surfaceView.setOnLongClickListener(cameraListener);
		Log.d("PV", " returning view");
		surfaceHolder = surfaceView.getHolder();

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		surfaceHolder.addCallback(this);

		// deprecated setting, but required on Android versions prior to 3.0
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		jpegCallback = new PictureCallback() {
			public void onPictureTaken(byte[] data, Camera camera) {
				takePicture.storeImage(data);
			}
		};
		takePicture.getLocation();

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
	}

	public OnLongClickListener cameraListener = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View arg0) {
			// TODO Auto-generated method stub
			if (camera != null) {
				camera.takePicture(null, null, jpegCallback);
			} else {
				id = CameraInfo.CAMERA_FACING_BACK;
				camera = Camera.open(id);
				camera.takePicture(null, null, jpegCallback);
			}
			return false;
		}

	};

	public void captureImage(View v) throws IOException {
		// take the picture
		camera.takePicture(null, null, jpegCallback);
	}

	public void refreshCamera() {
		if (surfaceHolder.getSurface() == null) {
			// preview surface does not exist
			Log.d("SurfaceView", "NULL in refreshCamera");
			return;
		}

		// stop preview before making changes
		try {
			if (camera != null) {
				camera.stopPreview();
			}
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}

		try {

			android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
			android.hardware.Camera.getCameraInfo(
					Camera.CameraInfo.CAMERA_FACING_BACK, info);
			int rotation = takePicture.getWindowManager().getDefaultDisplay()
					.getRotation();
			int degrees = 0;
			switch (rotation) {
			case Surface.ROTATION_0:
				degrees = 0;
				break;
			case Surface.ROTATION_90:
				degrees = 90;
				break;
			case Surface.ROTATION_180:
				degrees = 180;
				break;
			case Surface.ROTATION_270:
				degrees = 270;
				break;
			}

			int result;
			if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				result = (info.orientation + degrees) % 360;
				result = (360 - result) % 360; // compensate the mirror
			} else { // back-facing
				result = (info.orientation - degrees + 360) % 360;
				if(degrees == 270 || degrees == 90)
				{
					CameraPreview.portrait = "potrait";
				}
				else
				{
					CameraPreview.portrait = "landscape";
				}
			}
			Log.d("After Facing refresh camera", "before camrea preview");
			if (camera != null) {
				camera.setDisplayOrientation(result);
				camera.setPreviewDisplay(surfaceHolder);
				camera.startPreview();
			}
		} catch (Exception e) {

		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// Now that the size is known, set up the camera parameters and begin
		// the preview.
		refreshCamera();
	}

	public void surfaceCreated(SurfaceHolder holder) {
		try {
			// open the camera
			id = CameraInfo.CAMERA_FACING_BACK;
			camera = Camera.open(id);
			camera.setPreviewDisplay(surfaceHolder);
			camera.startPreview();
		} catch (RuntimeException | IOException e) {
			// check for exceptions
			Log.d("SurfaceChanged", "Camera open failed");
			System.err.println(e);
			return;
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// stop preview and release camera
		camera.stopPreview();
		camera.release();
		camera = null;
	}

/*	public void onResume() {
		super.onResume();
		Log.d("In Resume", "onResume function of Camera Preview");
		try{
		camera = Camera.open(id);
		camera.setPreviewDisplay(surfaceHolder);
		camera.startPreview();
		}
		catch(Exception e)
		{
			Log.d("OnResume", "Unable to open camera");
			Log.d("Error onResume", e.toString());
		}

	}*/

}

package com.asus;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Size;

import java.io.IOException;

import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.widget.ImageButton;

public class MainActivity extends ActionBarActivity {
	private final String TAG = this.getClass().getSimpleName();
	protected TextureView mTextureView;
	private ImageButton takePictureButton;
	private Size imageDimension;
	Framelayout frlayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "Byron onCreate");
		setContentView(R.layout.activity_main);
		//mTextureView.setSurfaceTextureListener(textureListener);
		//mTextureView.setBackgroundColor(Color.BLUE);*/
		//takePictureButton = (ImageButton)findViewById(R.id.imagebtn1);
		//CameraClient = new CameraClient(this);
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		Log.d(TAG, "Byron onStart");
		//CameraClient CameraClient = new CameraClient();
		//CameraClient.openCamera(0);
		
	}

	@Override
	protected void onResume(){
		super.onResume();
		Log.d(TAG, "Byron onResume");
		mTextureView = (TextureView) findViewById(R.id.textureView1);
		try {
			mCameraClient = new CameraClient(this);
			mCameraClient.setUpCamera();
			mCameraClient.openCamera();
		} catch (CameraAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(mCameraClient.mCameraDevice!=null){
			//mPreview = new CameraPreview(this, mCameraClient.mCameraDevice);
		}
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		Log.d(TAG, "Byron onPause");

	}
	@Override
	protected void onStop(){
		super.onStop();
		Log.d(TAG, "Byron onStop");
		mCameraClient.closeCamera();
		mCameraClient = null;
		mTextureView = null;

	}
	@Override
	protected void onDestroy(){
		super.onDestroy();
		Log.d(TAG, "Byron onDestroy");

	}
	
	@Override
	protected void onRestart(){
		super.onRestart();
		Log.d(TAG, "Byron onRestart");

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
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
		
}

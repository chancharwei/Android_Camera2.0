package com.asus;

import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.*;
import android.hardware.camera2.CameraCaptureSession.CaptureCallback;
import android.hardware.camera2.CameraCaptureSession.StateCallback;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Toast;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;


public class CameraClient {
	private final String TAG = this.getClass().getSimpleName();
	private static int MASTER_CAMERA = 1, FRONT_CAMERA = 0, ALL_CAMERAS = 2;
	private int numCamera = -1;
	MainActivity mActivity;
	private CameraDevice.StateCallback mCameraCallBack;
	private CameraManager mCameraManager = null;
	protected CameraDevice mCameraDevice = null;
    HandlerThread previewBackgroundThread = null;
    Handler previewBackgroundHandler = null;
    CaptureRequest.Builder previewRequestBuilder = null;
    SurfaceTexture mSurface = null;
    //CameraCaptureSession.StateCallback previewCaptureStateCallBack = null;
	//private TextureView mTextureView = null;
	private String[] camIDs;
	String cameraId = null;
    Size mPreviewSize = null;
    ImageReader mImageReader = null;
	public CameraClient(MainActivity activity){
		mActivity = activity;
		mCameraManager = (CameraManager)activity.getSystemService(Context.CAMERA_SERVICE);
		assertNotNull("Can't connect to camera manager!", mCameraManager);
		numCamera = getNumCamera();
		if(numCamera>0){
			startBackgroundThread();
		}
		Log.d(TAG, "Byron Num of Camera = "+numCamera);
	}

    public int getNumCamera(){
		try {
			camIDs = mCameraManager.getCameraIdList();
			return camIDs.length;
		} catch (CameraAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		
	}
    
	public void setUpCamera() throws CameraAccessException{
		for(String cameraId : camIDs){
			CameraCharacteristics cameraCharacteristics =
					mCameraManager.getCameraCharacteristics(cameraId);
			if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) ==
					MASTER_CAMERA) {
                StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                mPreviewSize = streamConfigurationMap.getOutputSizes(SurfaceTexture.class)[0];
                this.cameraId = cameraId;
                Size[] sizes = streamConfigurationMap.getOutputSizes(ImageFormat.JPEG);
                Size outputSize = sizes[5];
                mImageReader = ImageReader.newInstance(outputSize.getWidth(), outputSize.getHeight(), android.graphics.ImageFormat.JPEG, 2);
                mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, null);
            }
		}
	}
	
    private ImageReader.OnImageAvailableListener mOnImageAvailableListener = new OnImageAvailableListener() {
        @Override
        public void onImageAvailable(final ImageReader reader) {
            final File outputFile;
        }
    };
	public void openCamera() throws CameraAccessException{
		if(numCamera>0 && cameraId!=null){
			mCameraManager.openCamera(cameraId, cameraDeviceStateCallback, null);
		}	
	}
	

	


	private CameraDevice.StateCallback cameraDeviceStateCallback = new CameraDevice.StateCallback() {
	    @Override
	    public void onOpened(CameraDevice camera) {
	        Log.d(TAG,"DeviceStateCallback:camera was opend.");
	        mCameraDevice = camera;
	        mActivity.mTextureView.setSurfaceTextureListener(texturePreviewListener);

	        /*try {
	            createCameraCaptureSession();
	        } catch (CameraAccessException e) {
	            e.printStackTrace();
	        }*/
	    }

		@Override
		public void onDisconnected(CameraDevice camera) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onError(CameraDevice camera, int error) {
			// TODO Auto-generated method stub
			
		}
	};
	

	
	public void startPreview(SurfaceTexture surfaceTexture) throws CameraAccessException{
		if (previewRequestBuilder == null){
				previewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
		}
		surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        Surface mSurface = new Surface(surfaceTexture);
        previewRequestBuilder.addTarget(mSurface);
        // setting AE and AF MODE
        previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
        previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);

        mCameraDevice.createCaptureSession(Arrays.asList(mSurface, mImageReader.getSurface()), previewCaptureStateCallBack, previewBackgroundHandler);
	
	}
    private void startBackgroundThread() {
        previewBackgroundThread = new HandlerThread("PreviewBackgoundThread");
        previewBackgroundThread.start();
        previewBackgroundHandler = new Handler(previewBackgroundThread.getLooper());
    }
    /*
    private void startBackgroundThread2() {
        previewBackgroundThread = new HandlerThread();
        previewBackgroundThread.start();
        previewBackgroundHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                // process incoming messages here
                // this will run in the thread, which instantiates it
            }
        };
    }*/
    
    private void stopBackgorundThread() {
        try {
            previewBackgroundHandler.removeCallbacks(previewBackgroundThread);
            previewBackgroundThread.quitSafely();
            previewBackgroundThread.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        previewBackgroundThread = null;
        previewBackgroundHandler = null;
    }
    
    private CameraCaptureSession.StateCallback previewCaptureStateCallBack = new StateCallback() {
        @Override
        public void onConfigured(CameraCaptureSession session) {
        	Log.d(TAG, "createSession onConfigured");
            try {

            	session.setRepeatingRequest(previewRequestBuilder.build(), listener, previewBackgroundHandler);

            } catch (CameraAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        @Override
        public void onConfigureFailed(CameraCaptureSession session) {
            Log.d(TAG, "[Camera ERROR] Create Preview CaptureSession Fail");
        }
    };
    
    private CaptureCallback listener = new CaptureCallback() {
    	@Override
    	public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
    	
    	}
    };

	
	TextureView.SurfaceTextureListener texturePreviewListener = new TextureView.SurfaceTextureListener() {
	    @Override
	    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
	    	Log.d(TAG,"onSurfaceTextureAvailable width"+width+" height"+height);
	    	assertNotNull("get surface null", surface);
	    	mSurface = surface;
	    	try {
				startPreview(surface);
			} catch (CameraAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 	
	    }
	    @Override
	    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
	        // Transform you image captured size according to the surface width and height
	    	Log.d(TAG,"onSurfaceTextureSizeChanged");
	    }
	    @Override
	    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
	    	Log.d(TAG,"onSurfaceTextureDestroyed");
	        return false;
	    }
	    @Override
	    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
	    	Log.d(TAG,"onSurfaceTextureUpdated");
	    }
	};
	
    public void closeCamera() {
        if (mCameraDevice!= null) {
        	mCameraDevice.close();
        	mCameraDevice = null;
        }
        if (mImageReader != null) {
            mImageReader.close();
            mImageReader = null;
        }
        /*
        if(mSurface!=null){
        	mSurface.release();
        	mSurface = null;
        }*/
        texturePreviewListener = null;
        stopBackgorundThread();
    }
	

}






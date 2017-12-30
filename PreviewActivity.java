package com.connect4.thad.connect4_aicv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.FileObserver;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.glass.content.Intents;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

import java.io.File;
import java.io.IOException;

public class PreviewActivity extends Activity {
    private SurfaceView mPreview;
    private SurfaceHolder mPreviewHolder;
    private android.hardware.Camera mCamera;
    private boolean mInPreview = false;
    private boolean mCameraConfigured = false;
    private GestureDetector mGestureDetector;

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }catch(Exception e) {
            System.out.println("Camera is not available");
        }
        return c;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mPreview = (SurfaceView)findViewById(R.id.preview);
        mPreviewHolder = mPreview.getHolder();
        mPreviewHolder.addCallback(surfaceCallback);
        mCamera = Camera.open();
        if (mCamera != null)
            startPreview();
        mGestureDetector = createGestureDetector(this);
    }

    private void configPreview(int width, int height) {
        if ( mCamera != null && mPreviewHolder.getSurface() != null) {
            try {
                mCamera.setPreviewDisplay(mPreviewHolder);
            }catch(IOException e) {
                Toast.makeText(PreviewActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            if(!mCameraConfigured) {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setPreviewFpsRange(30000, 30000);
                parameters.setPreviewSize(640, 360);
                mCamera.setParameters(parameters);
                mCameraConfigured = true;
            }
        }
    }

    private void startPreview() {
        if ( mCameraConfigured && mCamera != null ) {
            mCamera.startPreview();
            mInPreview = true;
        }
    }

    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        public void surfaceCreated(SurfaceHolder holder) {
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            configPreview(width, height);
            startPreview();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            if(mCamera != null) {
                mCamera.release();
                mCamera = null;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        // Re-acquire the camera and start the preview.
        if(mCamera == null) {
            mCamera = getCameraInstance();
            if(mCamera != null) {
                configPreview(640, 360);
                startPreview();
            }
        }
    }

    @Override
    public void onPause() {
        if ( mInPreview ) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            mInPreview = false;
        }
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // see note 2 after the code for explanation
        if (keyCode == KeyEvent.KEYCODE_CAMERA) {
            if ( mInPreview ) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
                mInPreview = false;
            }
            return false;
        } else return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
    }

    private GestureDetector createGestureDetector(Context context) {
        GestureDetector gestureDetector = new GestureDetector(context);

        gestureDetector.setBaseListener( new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                if (gesture == Gesture.TAP) {
                    takePicture();
                    return true;
                } else if (gesture == Gesture.TWO_TAP) {
                    return true;
                } else if (gesture == Gesture.SWIPE_RIGHT) {
                    return true;
                } else if (gesture == Gesture.SWIPE_LEFT) {
                    return true;
                } else if (gesture == Gesture.SWIPE_DOWN){
                    finish();
                }
                return false;
            }
        });

        return gestureDetector;
    }

    private void takePicture() {
        Intent intent = new Intent(this, PictureActivity.class);
        startActivity(intent);

        /*Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("bestmove", 1234);
        startActivity(intent);*/
    }

}
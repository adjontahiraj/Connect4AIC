package com.connect4.thad.connect4_aicv;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.FileObserver;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.connect4.thad.connect4_aicv.UploadActivity;
import com.google.android.glass.content.Intents;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import java.io.File;

public class PictureActivity extends Activity {

    private Boolean TakenPicture;
    private GestureDetector mGestureDetector;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_picture);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mGestureDetector = createGestureDetector(this);
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Storing image...");
        TakenPicture = false;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus && !TakenPicture) {
            takePicture();
            TakenPicture = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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

        //Create a base listener for generic gestures
        gestureDetector.setBaseListener( new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                if (gesture == Gesture.TAP) {
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

    private static final int TAKE_PICTURE_REQUEST = 1;

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_PICTURE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PICTURE_REQUEST && resultCode == RESULT_OK) {
            String thumbnailPath = data.getStringExtra(Intents.EXTRA_THUMBNAIL_FILE_PATH);
            String picturePath = data.getStringExtra(Intents.EXTRA_PICTURE_FILE_PATH);

            processPictureWhenReady(picturePath);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void processPictureWhenReady(final String picturePath) {
        progress.show();
        final File pictureFile = new File(picturePath);

        if (pictureFile.exists()) {
            Intent intent = new Intent(this, UploadActivity.class);
            intent.putExtra("picture", picturePath);
            startActivity(intent);
            progress.dismiss();
            finish();
        } else {
            final File parentDirectory = pictureFile.getParentFile();
            FileObserver observer = new FileObserver(parentDirectory.getPath(),
                    FileObserver.CLOSE_WRITE | FileObserver.MOVED_TO) {
                // Protect against additional pending events after CLOSE_WRITE
                // or MOVED_TO is handled.
                private boolean isFileWritten;

                @Override
                public void onEvent(int event, String path) {
                    if (!isFileWritten) {
                        File affectedFile = new File(parentDirectory, path);
                        isFileWritten = affectedFile.equals(pictureFile);

                        if (isFileWritten) {
                            stopWatching();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    processPictureWhenReady(picturePath);
                                }
                            });
                        }
                    }
                }
            };
            observer.startWatching();
        }
    }

}
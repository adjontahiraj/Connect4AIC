package com.connect4.thad.connect4_aicv;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.view.WindowUtils;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class UploadActivity extends Activity {
    private GestureDetector mGestureDetector;

    private String URL = "http://54.191.117.213/Connect4Server_v3.8/ProcessImage";
    private int compressionRate = 75; //0 - MAX Compression; 100 - NO Compression

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_upload);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mGestureDetector = createGestureDetector(this);
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Server is processing move...");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus) {
            progress.show();
            SendHttpRequestTask t = new SendHttpRequestTask();
            String[] params = new String[]{URL, getIntent().getExtras().getString("picture")};
            t.execute(params);
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

    private class SendHttpRequestTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            String url = params[0];
            String imgPath = params[1];

            File imgFile = new  File(imgPath);
            Bitmap b;
            if(imgFile.exists()) {
                b = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            }else {
                return -5;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.JPEG, compressionRate, baos);

            try {
                HttpClient client = new HttpClient(url);
                client.connectForMultipart();
                client.addFilePart("file", "logo1.jpeg", baos.toByteArray());
                client.finishMultipart();
                int res = client.getResponse();
                return res;
            } catch(Throwable t) {
                t.printStackTrace();
            }

            return -4;
        }

        @Override
        protected void onPostExecute(Integer data) {
            displayResult(data);
        }
    }

    private void displayResult(int bestMove) {
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("bestmove", bestMove);
        startActivity(intent);
        progress.dismiss();
        finish();
    }
}
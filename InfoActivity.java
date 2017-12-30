package com.connect4.thad.connect4_aicv;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

public class InfoActivity extends Activity {

    private GestureDetector mGestureDetector;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_info);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mGestureDetector = createGestureDetector(this);
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
        if(mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
    }

    private GestureDetector createGestureDetector(Context context) {
        GestureDetector gestureDetector = new GestureDetector(context);

        gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                if(gesture == Gesture.TAP) {
                    return true;
                }else if(gesture == Gesture.TWO_TAP) {
                    return true;
                }else if(gesture == Gesture.SWIPE_RIGHT) {
                    return true;
                }else if(gesture == Gesture.SWIPE_LEFT) {
                    return true;
                }else if(gesture == Gesture.SWIPE_DOWN) {
                    finish();
                }
                return false;
            }
        });

        return gestureDetector;
    }
}
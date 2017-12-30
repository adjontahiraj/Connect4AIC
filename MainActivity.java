package com.connect4.thad.connect4_aicv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.view.WindowUtils;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

public class MainActivity extends Activity {
    private CardScrollView mCardScroller;
    private View mView;
    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        //Permissions, we need to request to use them
        //FLAG_KEEP_SCREEN_ON is always requested
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //Requested for voice commands
        getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);


        mView = buildView();

        mCardScroller = new CardScrollView(this);
        mCardScroller.setAdapter(new CardScrollAdapter() {
            @Override
            public int getCount() {
                return 1;
            }

            @Override
            public Object getItem(int position) {
                return mView;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return mView;
            }

            @Override
            public int getPosition(Object item) {
                if (mView.equals(item)) {
                    return 0;
                }
                return AdapterView.INVALID_POSITION;
            }
        });

        mGestureDetector = createGestureDetector(this);

        setContentView(mCardScroller);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCardScroller.activate();
    }

    @Override
    protected void onPause() {
        mCardScroller.deactivate();
        super.onPause();
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS || featureId == Window.FEATURE_OPTIONS_PANEL) {
            getMenuInflater().inflate(R.menu.mainactivitymenu, menu);
            return true;
        }
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS || featureId ==  Window.FEATURE_OPTIONS_PANEL) {
            switch (item.getItemId()) {
                case R.id.get_move:
                    picture_preview();
                    break;
                case R.id.app_info:
                    displayInfo();
                    break;
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    //This is the same every time
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
    }

    //This handles all the taps and swipes
    //From here, we can say what to do when button is pressed etc.
    private GestureDetector createGestureDetector(Context context) {
        GestureDetector gestureDetector = new GestureDetector(context);

        //Create a base listener for generic gestures
        gestureDetector.setBaseListener( new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                if (gesture == Gesture.TAP) {
                    openOptionsMenu();
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

    //This creates what the screen looks like
    private View buildView() {
        CardBuilder card = new CardBuilder(this, CardBuilder.Layout.TEXT);

        card.setText("Welcome to Connect 4!");
        return card.getView();
    }

    //This is a function that calls another activity: PictureActivity
    public void picture_preview() {
        Intent resultsIntent = new Intent(this, PreviewActivity.class);
        startActivity(resultsIntent);
    }

    //This is a function that calls another activity: InfoActivity
    public void displayInfo() {
        Intent resultsIntent = new Intent(this, InfoActivity.class);
        startActivity(resultsIntent);
    }
}
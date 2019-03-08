package com.displayfort.dfortusbtouch.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by pc on 08/03/2019 13:32.
 * Dfortusbtouch
 */
public class SwippeableRelativeLayout extends RelativeLayout {
    private GestureDetector mGestureDetector;

    public SwippeableRelativeLayout(Context context) {
        super(context);
    }

    public SwippeableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    public void setGestureDetector(GestureDetector gestureDetector) {
        mGestureDetector = gestureDetector;
    }
}
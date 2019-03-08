package com.displayfort.dfortusbtouch.widgets;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by pc on 08/03/2019 13:27.
 * Dfortusbtouch
 */
public abstract class CustomGestureListener extends GestureDetector.SimpleOnGestureListener {

    private final View mView;

    public CustomGestureListener(View view) {
        mView = view;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        mView.onTouchEvent(e);
        return super.onSingleTapConfirmed(e);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        onTouch();
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1.getX() < e2.getX()) {
            return onSwipeRight();
        }

        if (e1.getX() > e2.getX()) {
            return onSwipeLeft();
        }

        return onTouch();
    }

    public abstract boolean onSwipeRight();

    public abstract boolean onSwipeLeft();

    public abstract boolean onTouch();
}
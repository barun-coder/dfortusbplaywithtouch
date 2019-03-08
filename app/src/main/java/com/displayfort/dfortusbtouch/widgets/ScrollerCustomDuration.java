package com.displayfort.dfortusbtouch.widgets;


import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

class ScrollerCustomDuration extends Scroller {

    private double scrollFactor = 1;

    ScrollerCustomDuration(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    void setScrollDurationFactor(double scrollFactor) {
        this.scrollFactor = scrollFactor;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, (int) (duration * scrollFactor));
    }
}
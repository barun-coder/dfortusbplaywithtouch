package com.displayfort.dfortusbtouch.widgets;

import android.support.v4.view.ViewPager;

/**
 * Created by pc on 07/03/2019 17:32.
 * Dfortusbtouch
 */
public class CircularViewPagerHandler implements ViewPager.OnPageChangeListener {
    private ViewPager customViewPager;
    private int         mCurrentPosition;
    private int         mScrollState;

    public CircularViewPagerHandler(final ViewPager viewPager) {
        customViewPager = viewPager;
    }

    @Override
    public void onPageSelected(final int position) {
        mCurrentPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(final int state) {
        handleScrollState(state);
        mScrollState = state;
    }

    private void handleScrollState(final int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            setNextItemIfNeeded();
        }
    }

    private void setNextItemIfNeeded() {
        if (!isScrollStateSettling()) {
            handleSetNextItem();
        }
    }

    private boolean isScrollStateSettling() {
        return mScrollState == ViewPager.SCROLL_STATE_SETTLING;
    }

    private void handleSetNextItem() {
        final int lastPosition = customViewPager.getAdapter().getCount() - 1;
        if(mCurrentPosition == 0) {
            customViewPager.setCurrentItem(lastPosition, false);
        } else if(mCurrentPosition == lastPosition) {
            customViewPager.setCurrentItem(0, false);
        }
    }

    @Override
    public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
    }
}

package com.displayfort.dfortusbtouch.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.displayfort.dfortusbtouch.Fragments.ImageFragment;
import com.displayfort.dfortusbtouch.Fragments.VideoFragment;

import java.io.File;
import java.net.URLConnection;

/**
 * Created by pc on 07/03/2019 13:29.
 * Dfortusbtouch
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    private int COUNT = 3;
    private File[] completFileList;
    private Context context;

    public ViewPagerAdapter(FragmentManager fm, File[] completFileList, Context context) {
        super(fm);
        this.completFileList = completFileList;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        File files = completFileList[position];
        String type = URLConnection.guessContentTypeFromName(files.getName());
        if (type != null && type.toLowerCase().contains("video")) {
            fragment = new VideoFragment().newInstance(context, files);
        } else {
            fragment = new ImageFragment().newInstance(context, files);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return (completFileList != null) ? completFileList.length : 0;
    }

//    @Override
//    public CharSequence getPageTitle(int position) {
//        return "Tab " + (position + 1);
//    }
}
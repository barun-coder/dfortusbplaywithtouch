package com.displayfort.dfortusbtouch;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import com.displayfort.dfortusbtouch.Adapters.ViewPagerAdapter;
import com.displayfort.dfortusbtouch.receiver.StartService;
import com.displayfort.dfortusbtouch.widgets.CustomViewPager;
import com.tompee.circularviewpager.CircularViewPager;

import java.io.File;
import java.util.Arrays;


/**
 * Created by pc on 09/01/2019 10:59.
 * MyApplication
 */
public class ViewPagerAdsFromUsbActivity extends BaseSupportActivity {
    private String TAG = "USBCatch";
    private File[] completFileList;
    private int Orientation = ExifInterface.ORIENTATION_UNDEFINED;
    private CircularViewPager viewPager;
    private Fragment CurrentFragment;
    private int count = 1;
    private CustomViewPager customViewPager;
    private int mCurrentPosition;
    private int mScrollState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_main);

        init();
        SHowMNT();
    }

    private void init() {
        viewPager = findViewById(R.id.viewPager);
        customViewPager = findViewById(R.id.custom_viewpager);

    }

    ///data/user/0/displayfort.nirmit.com.myapplication/files
    private void SHowMNT() {
        showLog("FILEPATH", "\n\n");
        File[] fileList;
        File file = new File("mnt");
        if (file.exists()) {
            showLog("FILEPATH-MNT", "mnt exist");
            fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                showLog("FILEPATH-MNT", i + ":-" + fileList[i].getAbsolutePath());
            }
            showLog("FILEPATH", "MNT over \n");
            File file1 = new File(getApplicationContext().getFilesDir().getPath());
            if (!isNotMobile()) {
                if (Orientation == ExifInterface.ORIENTATION_UNDEFINED) {
                    file1 = new File(Environment.getExternalStorageDirectory() + File.separator + "adsTouch");
                } else {
                    file1 = new File(getApplicationContext().getFilesDir().getPath().replace("files", "img"));
                }
                if (file1.exists()) {
                    showLog("FILEPATH-MNT", file1 + " mnt/usb exist");
                    startFIlePlay(file1);
                }
            } else {
                file1 = new File(file.getAbsoluteFile() + File.separator + "usb");
                fileList = file1.listFiles();
                if (fileList != null && fileList.length > 0) {
                    for (int i = 0; i < fileList.length; i++) {
                        showLog("FILEPATH-MNT", i + ":--" + fileList[i].getAbsolutePath());
                        if (fileList[i].exists()) {
                            startFIlePlay(fileList[i]);
                            return;
                        }
                    }

                } else {
                    file1 = new File("storage");
                    fileList = file1.listFiles();
                    if (fileList != null && fileList.length > 0) {
                        for (int i = 0; i < fileList.length; i++) {
                            if (!fileList[i].getAbsolutePath().contains("emulated") && !fileList[i].getAbsolutePath().contains("self")) {
                                if (fileList[i].exists()) {
                                    startFIlePlay(fileList[i]);
                                    return;
                                }
                            }
                        }
                    }
                }
            }

        }
    }


    ///storage/emulated/0/ads
    private void startFIlePlay(final File file) {
        count = 1;
        filterName(file);
        completFileList = file.listFiles();
        if (completFileList != null && completFileList.length > 0) {
            Arrays.sort(completFileList);
            for (int i = 0; i < completFileList.length; i++) {
                showLog("FILEPATH", completFileList[i] + "");
            }
            setViewpagerAdaprter();


            Intent intent = new Intent(this, StartService.class);
            intent.putExtra("FILEPATH", file.getAbsolutePath());
            startService(intent);
            showLog("FILEPATH", completFileList.length + " completFileList \n");
        }
    }

    private void setViewpagerAdaprter() {
        customViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), completFileList, ViewPagerAdsFromUsbActivity.this));
//        customViewPager.addOnPageChangeListener(new CircularViewPagerHandler(customViewPager));
        customViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                showLog("onPageSelected", mCurrentPosition + "");
                mCurrentPosition = position;
                showLog("onPageSelected", position + "");
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                handleScrollState(state);
                mScrollState = state;
            }
        });
        customViewPager.setOffscreenPageLimit(1);
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
        if (mCurrentPosition == 0) {
            customViewPager.setCurrentItem(lastPosition, false);
        } else if (mCurrentPosition == lastPosition) {
            customViewPager.setCurrentItem(0, false);
        }
    }

    private void filterName(File file) {
        completFileList = file.listFiles();
        if (completFileList != null && completFileList.length > 0) {
            for (File FronFile : completFileList) {
                if (FronFile.getAbsolutePath().contains(" ")) {
                    File from = new File(file, FronFile.getName());
                    File to = new File(file, FronFile.getName().replace(" ", "_"));
                    boolean isRename = from.renameTo(to);
                    showLog("FILEPATH", to.getAbsolutePath() + ":" + isRename);
                }
            }
        }
    }

    private boolean isNotMobile() {
        Display display = ((Activity) this).getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        float widthInches = metrics.widthPixels / metrics.xdpi;
        float heightInches = metrics.heightPixels / metrics.ydpi;
        double diagonalInches = Math.sqrt(Math.pow(widthInches, 2) + Math.pow(heightInches, 2));
        return diagonalInches >= 7.0;
    }

    private void showLog(String tag, String deviceName) {
        Log.d(TAG, tag + ":" + deviceName);
    }

    @Override
    public void onPause() {
        onFinishAPP();
        super.onPause();


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void onFinishAPP() {

    }

    @Override
    public void onStop() {
        onFinishAPP();
        super.onStop();

    }

    @Override
    public void onDestroy() {
        onFinishAPP();
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        finish();
    }


    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.intent.action.MEDIA_MOUNTED")) {
                SHowMNT();
            }
        }
    }

    private void dummyText() {

//            if (viewPager != null) {
//                viewPager.setPageCount(completFileList.length - 1);
//                viewPager.setCurrentItem(0);
//                viewPager.setFragmentPagerAdapter(getSupportFragmentManager(),
//                        new CircularViewPager.GetFragmentItemListener() {
//                            @Override
//                            public Fragment getFragment(int position) {
//                                Fragment fragment = null;
//                                File files = completFileList[position];
//                                showLog("FILEs", files.getAbsolutePath());
//                                String type = URLConnection.guessContentTypeFromName(files.getName());
//                                if (type != null && type.toLowerCase().contains("video")) {
//                                    fragment = new VideoFragment().newInstance(ViewPagerAdsFromUsbActivity.this, files);
//                                } else {
//                                    fragment = new ImageFragment().newInstance(ViewPagerAdsFromUsbActivity.this, files);
//                                }
//                                Log.d("VideoFragment", "Fragment Call ");
//                                CurrentFragment = fragment;
//                                Log.d("VideoFragment", " JUST CALLED " + CurrentFragment.getClass().getName());
//                                return fragment;
//                            }
//                        });
//            }
//            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//                @Override
//                public void onPageScrolled(int i, float v, int i1) {
//                }
//
//                @Override
//                public void onPageSelected(int i) {
//                    Log.d("VideoFragment", "onPageSelected " + i);
//                    count = i;
//                    if ((i - 1) >= 0) {
//                    }
//                    if (CurrentFragment instanceof VideoFragment) {
//                        ((VideoFragment) CurrentFragment).onPause();
//                        Log.d("VideoFragment", CurrentFragment.getClass().getName());
//                    } else {
//                        Log.d("VideoFragment", CurrentFragment.getClass().getName());
//                    }
//                }
//
//                @Override
//                public void onPageScrollStateChanged(int i) {
//
//                }
//            });
    }

}

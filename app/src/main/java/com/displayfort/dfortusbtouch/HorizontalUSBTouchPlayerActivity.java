package com.displayfort.dfortusbtouch;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.usb.UsbManager;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.displayfort.dfortusbtouch.Constant.Constants;
import com.displayfort.dfortusbtouch.widgets.CustomGestureListener;
import com.displayfort.dfortusbtouch.widgets.SwippeableRelativeLayout;
import com.universalvideoview.UniversalVideoView;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;


/**
 * Created by pc on 09/01/2019 10:59.
 * MyApplication
 */
public class HorizontalUSBTouchPlayerActivity extends BaseSupportActivity {
    private String TAG = "USBCatch";
    private ArrayList<File> completFileList;
    private int Orientation = ExifInterface.ORIENTATION_UNDEFINED;
    private CountDownTimer countDownTimer;
    private int count = 1;
    private MyReceiver myReceiver;
    private ImageView mDefaultIV;
    private ImageView displayImageView;
    private boolean isPause = false;
    private UniversalVideoView videoView;
    private RelativeLayout mUvVideoRl;
    private SwippeableRelativeLayout gestures_rl;
    private int currentAdvertisementNo = 0;
    private File currentFile, oldFilePath;
    private View bckview;
    private long timeRemaining;
    private String Model = null;
    private File selectedFile = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontal_player);

        RegisterUpdateReceiver();
        init();
        setConstant();

        /**/

        SHowMNT();
//
        /**/
    }

    private void CallFile(String stringPath) {
        showLog("FILE", "PAth" + stringPath);
        File file1 = new File(stringPath);
        File[] fileList = file1.listFiles();
        CallFileList(file1, fileList);
    }

    private void CallFileList(File file1, File[] fileList) {
        if (fileList != null && fileList.length > 0) {
            for (int i = 0; i < fileList.length; i++) {
                try {
//                    showLog("FILE", fileList[i].getAbsolutePath());
                    File file = fileList[i];
                    if ((file.getAbsolutePath().contains("salsain"))) {
                        selectedFile = file1;
                        showLog("FILE", "-------------" + selectedFile.getAbsolutePath());
                    }
                    if (file.getAbsolutePath().contains("udisk")) {
                        CallFileList(file, file.listFiles());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setConstant() {
        gestures_rl.setVisibility((Constants.isTouchEnable) ? View.VISIBLE : View.GONE);
//        VideoView NewVideo = findViewById(R.id.new_videoView);
//        NewVideo.setVideoURI(Uri.parse(" /storage/emulated/0/adsTouch/Mykonos_5.mov"));
//        NewVideo.start();
    }

    private void init() {

        textview = findViewById(R.id.textview);
        gestures_rl = findViewById(R.id.gestures_rl);
        gestures_rl.setSelected(false);
        bckview = findViewById(R.id.view);
        bckview.setEnabled(false);
        mUvVideoRl = (RelativeLayout) findViewById(R.id.uv_video_rl);
        mUvVideoRl.setVisibility(View.GONE);
        mDefaultIV = (ImageView) findViewById(R.id.default_iv);
        displayImageView = (ImageView) findViewById(R.id.imageView2);
        videoView = findViewById(R.id.videoView);
        videoView.setEnabled(false);

        final GestureDetector mGestureDetector = new GestureDetector(this, new CustomGestureListener(gestures_rl) {
            private int stopPosition;

            @Override
            public boolean onSwipeRight() {
                if (!gestures_rl.isSelected()) {
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                    //   Toast.makeText(HorizontalUSBTouchPlayerActivity.this, "right", Toast.LENGTH_SHORT).show();
                    if (currentAdvertisementNo == 0) {
                        currentAdvertisementNo = (completFileList.size() - 2);
                    } else if (currentAdvertisementNo > 0) {
                        currentAdvertisementNo = currentAdvertisementNo - 2;
                        if (currentAdvertisementNo == -1) {
                            currentAdvertisementNo = (completFileList.size() - 1);
                        }
                    } else {
                        currentAdvertisementNo = (completFileList.size() - 1);
                    }
                    showCurrentAd();
                }
                return false;
            }

            @Override
            public boolean onSwipeLeft() {
                if (!gestures_rl.isSelected()) {
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                    //  Toast.makeText(HorizontalUSBTouchPlayerActivity.this, "left", Toast.LENGTH_SHORT).show();
                    showCurrentAd();
                }
                return false;
            }

            @Override
            public boolean onTouch() {
                //  Toast.makeText(HorizontalUSBTouchPlayerActivity.this, "ON", Toast.LENGTH_SHORT).show();
                showLog("\nONFLINGS", "before Vlue gestures_rl " + gestures_rl.isSelected());
                if (gestures_rl.isSelected()) {
                    bckview.setBackgroundColor(Color.TRANSPARENT);
                    if (videoView != null) {
                        videoView.seekTo(stopPosition);
                        videoView.start();
                        showLog("ONFLINGS", "Video status Playing " + videoView.isPlaying());
                    }
                    isPause = false;
                    countDownTimer = new CountDownTimer(timeRemaining, 1000) {

                        public void onTick(long millisUntilFinished) {
                            if (isPause) {
                                countDownTimer.cancel();
                            } else {
                                timeRemaining = millisUntilFinished;
                            }
                            Log.d("MILISECONDS", "REMAING Swipe: " + millisUntilFinished);
                        }

                        public void onFinish() {

                            Log.d("handleMessage", "currentFile  :  " + currentFile);
                            Log.d("handleMessage", "oldFilePath  :  " + oldFilePath);
                            if (currentFile != null && oldFilePath != null && currentFile.getAbsolutePath().equalsIgnoreCase(oldFilePath.getAbsolutePath())) {
                                showCurrentAd();
                            }
                        }

                    }.start();
                } else {
                    isPause = true;
                    bckview.setBackgroundColor(Color.WHITE);
                    if (videoView != null && videoView.isPlaying()) {
                        stopPosition = videoView.getCurrentPosition();
                        videoView.pause();
                        showLog("ONFLINGS", "Video status Playing " + videoView.isPlaying());
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (gestures_rl.isSelected()) {
                                onTouch();
                            }

                        }
                    }, 5000);

                }
                gestures_rl.setSelected(!gestures_rl.isSelected());
                showLog("ONFLINGS", "final Vlue gestures_rl " + gestures_rl.isSelected());

                return false;
            }
        });

//container click listener
        gestures_rl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return !mGestureDetector.onTouchEvent(event);
            }
        });
        gestures_rl.setGestureDetector(mGestureDetector);

    }

    ///data/user/0/displayfort.nirmit.com.myapplication/files
    private void SHowMNT() {
        selectedFile = null;
        CallFile("/storage");
        mDefaultIV.setVisibility(View.VISIBLE);
        mUvVideoRl.setVisibility(View.INVISIBLE);
        displayImageView.setVisibility(View.INVISIBLE);
        if (selectedFile != null) {
            startFIlePlay(selectedFile);
            return;
        } else {
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
                if (!isNotMobile() && isIMEIAvailable()) {
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
                        } else {
                            file1 = new File(file.getAbsoluteFile() + File.separator + "sdcard");
                            fileList = file1.listFiles();
                            if (fileList != null && fileList.length > 0) {
//                            for (int i = 0; i < fileList.length; i++) {
                                startFIlePlay(file1);
                                return;

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
        RemoveWhiteSpace(file);
        completFileList = FilterFiles(file.listFiles());
        if (completFileList != null && completFileList.size() > 0) {
            mDefaultIV.setVisibility(View.GONE);
            try {
                Collections.sort(completFileList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (int i = 0; i < completFileList.size(); i++) {
                showLog("FILEPATH", completFileList.get(i) + "");
            }
            currentAdvertisementNo = 0;
            showCurrentAdvertisements();
        }
    }

    private ArrayList<File> FilterFiles(File[] completFileList) {
        ArrayList<File> newFileList = new ArrayList<>();
        int j = 0;
        if (completFileList != null) {
            for (int i = 0; i < completFileList.length; i++) {
                String type = URLConnection.guessContentTypeFromName(completFileList[i].getName());
                if (type != null && (type.toLowerCase().contains("gif") || type.toLowerCase().contains("image") || type.toLowerCase().contains("video"))) {
                    newFileList.add(completFileList[i]);
                }
            }
        }
        return newFileList;
    }

    private void showCurrentAdvertisements() {
        showCurrentAd();

    }

    private void showCurrentAd() {
        int interval = -1;
        Log.d("handleMessage", "currentAdvertisementNo  :  " + currentAdvertisementNo);
        setTheme(R.style.AppTheme);
        if (completFileList == null && completFileList.size() <= 0) {
            SHowMNT();
            return;
        }
        final File file = completFileList.get(currentAdvertisementNo);
        if (file.exists()) {
            try {
                currentFile = file;
                if (videoView != null && videoView.isPlaying()) {
                    showLog(TAG, "Onstop Video ");
                    videoView.stopPlayback();
                }
                showLog("FILEPATH", "Run File " + file.getAbsolutePath() + "\n");
                String type = URLConnection.guessContentTypeFromName(file.getName());
                if (type != null) {
                    if (type.toLowerCase().contains("gif")) {
                        mUvVideoRl.setVisibility(View.INVISIBLE);
                        displayImageView.setVisibility(View.VISIBLE);
                        String photoPath = file.getAbsolutePath();
                        Glide.with(this).load(photoPath).into(displayImageView);
                        Log.d("ADVERTISEMENT", photoPath.toString() + "");
                        interval = 5000;
                    } else if (type.toLowerCase().contains("image")) {
                        mUvVideoRl.setVisibility(View.INVISIBLE);
                        displayImageView.setVisibility(View.VISIBLE);
                        String photoPath = file.getAbsolutePath();
                        Glide.with(this).load(file).into(displayImageView);
                        System.gc();
                        Log.d("ADVERTISEMENT", photoPath.toString() + "");
                        interval = 5000;
                    } else if (type.toLowerCase().contains("video")) {
                        mUvVideoRl.setVisibility(View.VISIBLE);
                        videoView.setVisibility(View.GONE);
                        videoView.setVisibility(View.VISIBLE);
                        displayImageView.setVisibility(View.INVISIBLE);
                        File newfile = file;
                        boolean isFileFound = newfile.exists();
                        if (!isFileFound) {
                            Log.d(" VIDEPATH", "isFileFound" + isFileFound);
                            interval = -1; // 1 Second
                        } else {
                            Log.d("VIDEPATH", "isFileFound" + isFileFound);
                            String photoPath = newfile.getAbsolutePath();
                            interval = getMiliseconds(newfile);
                            if (interval != 0) {
                                Log.i("PostActivity", "Video List is " + getFilesDir().getPath());
                                try {
                                    videoView.setVideoURI(Uri.parse(photoPath));
                                    videoView.start();
                                    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            Log.d("VIDEPATH", "onCompletion");
                                        }
                                    });
                                    videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                                        @Override
                                        public boolean onError(MediaPlayer mp, int what, int extra) {
                                            Log.d("VIDEPATH", "onError");
                                            videoView.stopPlayback();
                                            if (currentAdvertisementNo < (completFileList.size() - 1)) {
                                                currentAdvertisementNo++;
                                            } else {
                                                currentAdvertisementNo = 0;
                                            }
                                            showCurrentAd();
                                            return false;
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else {
                                interval = -1;
                            }

                        }
                        // 1 Second
                    }
                }
                if (interval != 0) {
                    Log.d("interval", "interval  :  " + interval + " " + file.getName());
                    Handler handler = new Handler() {
                        @Override
                        public void handleMessage(final Message msg) {
                            final Bundle bundle = msg.getData();
                            oldFilePath = new File(bundle.getString("FILE"));
//                        String oldFilePath = bundle.getString("FILE");
//                        Log.d("handleMessage", "currentFile  :  " + currentFile);
//                        Log.d("handleMessage", "oldFilePath  :  " + oldFilePath);
//                        if (currentFile.getAbsolutePath().equalsIgnoreCase(oldFilePath)) {
//                            showCurrentAd();
//                        }


                            countDownTimer = new CountDownTimer(bundle.getLong("INTERVAL"), 1000) {

                                public void onTick(long millisUntilFinished) {
                                    if (isPause) {
                                        countDownTimer.cancel();
                                    } else {
                                        timeRemaining = millisUntilFinished - 1000;
                                    }
                                    Log.d("MILISECONDS", "REMAING: " + millisUntilFinished);
                                }

                                public void onFinish() {


                                    Log.d("handleMessage", "currentFile  :  " + currentFile);
                                    Log.d("handleMessage", "oldFilePath  :  " + oldFilePath);
                                    if (oldFilePath == null || currentFile.getAbsolutePath().equalsIgnoreCase(oldFilePath.getAbsolutePath())) {
                                        showCurrentAd();
                                    }
                                }

                            }.start();

                        }
                    };

                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("FILE", currentFile.getAbsolutePath());
                    bundle.putLong("INTERVAL", interval);
                    msg.setData(bundle);
//                handler.sendMessageDelayed(msg, interval);
                    handler.sendMessage(msg);
                    if (currentAdvertisementNo < (completFileList.size() - 1)) {
                        currentAdvertisementNo++;
                    } else {
                        currentAdvertisementNo = 0;
                    }
                }
            } catch (RuntimeException e) {
                if (currentAdvertisementNo < (completFileList.size() - 1)) {
                    currentAdvertisementNo++;
                } else {
                    currentAdvertisementNo = 0;
                }
                showCurrentAd();
            } catch (Exception e) {
                mUvVideoRl.setVisibility(View.INVISIBLE);
                displayImageView.setVisibility(View.VISIBLE);


            }
        } else {
            SHowMNT();
        }
    }


    private int getMiliseconds(File file) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//use one of overloaded setDataSource() functions to set your data source
        retriever.setDataSource(this, Uri.fromFile(file));
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMillisec = Long.parseLong(time);
        return (int) timeInMillisec;
    }

    private void RemoveWhiteSpace(File file) {
        File[] completFileLists = file.listFiles();
        if (completFileLists != null && completFileLists.length > 0) {
            for (File FronFile : completFileLists) {
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

    private boolean isIMEIAvailable() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return true;
        }
        if (telephonyManager.getDeviceId() != null) {
            return true;
        }
        return false;
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
        try {
            unregisterReceiver(myReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (videoView != null && videoView.isPlaying()) {
                showLog(TAG, "Onstop Video ");
                videoView.stopPlayback();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            showLog("", "onReceive");
            SHowMNT();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                showLog("", "attached");


            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                showLog("", "deattached");
            }

        }

    }

    private void RegisterUpdateReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.MEDIA_MOUNTED");
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        intentFilter.addDataScheme("file");
        intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        myReceiver = new MyReceiver();
        this.registerReceiver(myReceiver, intentFilter);
    }

}

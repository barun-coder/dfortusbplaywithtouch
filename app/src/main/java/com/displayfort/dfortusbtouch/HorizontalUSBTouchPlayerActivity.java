package com.displayfort.dfortusbtouch;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.displayfort.dfortusbtouch.widgets.CustomGestureListener;
import com.displayfort.dfortusbtouch.widgets.SwippeableRelativeLayout;
import com.tompee.circularviewpager.CircularViewPager;
import com.universalvideoview.UniversalVideoView;

import java.io.File;
import java.net.URLConnection;
import java.util.Arrays;


/**
 * Created by pc on 09/01/2019 10:59.
 * MyApplication
 */
public class HorizontalUSBTouchPlayerActivity extends BaseSupportActivity {
    private String TAG = "USBCatch";
    private File[] completFileList;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontal_player);
        RegisterUpdateReceiver();
        init();
        SHowMNT();
    }

    private void init() {
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
                    countDownTimer.cancel();
                    Toast.makeText(HorizontalUSBTouchPlayerActivity.this, "right", Toast.LENGTH_SHORT).show();
                    if (currentAdvertisementNo > 0) {
                        currentAdvertisementNo = currentAdvertisementNo - 2;
                        if (currentAdvertisementNo == -1) {
                            currentAdvertisementNo = (completFileList.length - 1);
                        }
                    } else {
                        currentAdvertisementNo = (completFileList.length - 1);
                    }
                    showCurrentAd();
                }
                return false;
            }

            @Override
            public boolean onSwipeLeft() {
                if (!gestures_rl.isSelected()) {
                    countDownTimer.cancel();
                    Toast.makeText(HorizontalUSBTouchPlayerActivity.this, "left", Toast.LENGTH_SHORT).show();
                    showCurrentAd();
                }
                return false;
            }

            @Override
            public boolean onTouch() {
                Toast.makeText(HorizontalUSBTouchPlayerActivity.this, "ON", Toast.LENGTH_SHORT).show();
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
                            if (currentFile.getAbsolutePath().equalsIgnoreCase(oldFilePath.getAbsolutePath())) {
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
            currentAdvertisementNo = 0;
            showCurrentAdvertisements();
        }
    }

    private void showCurrentAdvertisements() {
        showCurrentAd();

    }

    private void showCurrentAd() {
        int interval = -1;
        try {
            Log.d("handleMessage", "currentAdvertisementNo  :  " + currentAdvertisementNo);
            setTheme(R.style.AppTheme);
            final File file = completFileList[currentAdvertisementNo];
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
                    Glide.with(this).load(BitmapFactory.decodeFile(photoPath)).into(displayImageView);
                    Log.d("ADVERTISEMENT", photoPath.toString() + "");
                    interval = 5000;
                } else if (type.toLowerCase().contains("image")) {
                    mUvVideoRl.setVisibility(View.INVISIBLE);
                    displayImageView.setVisibility(View.VISIBLE);
                    //displayImageView
                    String photoPath = file.getAbsolutePath();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    final Bitmap b = BitmapFactory.decodeFile(photoPath);// BitmapFactory.decodeFile(photoPath, options);
                    displayImageView.setImageBitmap(b);
                    Log.d("ADVERTISEMENT", photoPath.toString() + "");
                    interval = 5000;
                } else if (type.toLowerCase().contains("video")) {
                    mUvVideoRl.setVisibility(View.VISIBLE);
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

                                oldFilePath = new File(bundle.getString("FILE"));

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
                if (currentAdvertisementNo < (completFileList.length - 1)) {
                    currentAdvertisementNo++;
                } else {
                    currentAdvertisementNo = 0;
                }
            }
        } catch (RuntimeException e) {
            if (currentAdvertisementNo < (completFileList.length - 1)) {
                currentAdvertisementNo++;
            } else {
                currentAdvertisementNo = 0;
            }
            showCurrentAd();
        } catch (Exception e) {
            mUvVideoRl.setVisibility(View.INVISIBLE);
            displayImageView.setVisibility(View.VISIBLE);


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
            if (action.equals("android.intent.action.MEDIA_MOUNTED")) {
                SHowMNT();
            }
        }
    }

    private void RegisterUpdateReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.MEDIA_MOUNTED");
        intentFilter.addDataScheme("file");
        myReceiver = new MyReceiver();
        this.registerReceiver(myReceiver, intentFilter);
    }

    //        gestures_rl.setOnTouchListener(new OnSwipeTouchListener(this) {
//            public void onSwipeON() {
//                Toast.makeText(HorizontalUSBTouchPlayerActivity.this, "ON", Toast.LENGTH_SHORT).show();
//                if (gestures_rl.isSelected()) {
//                    findViewById(R.id.bckview).setBackgroundColor(Color.TRANSPARENT);
//                } else {
//                    findViewById(R.id.bckview).setBackgroundColor(Color.WHITE);
//                }
//                gestures_rl.setSelected(!gestures_rl.isSelected());
//            }
//
//            public void onSwipeTop() {
//                Toast.makeText(HorizontalUSBTouchPlayerActivity.this, "top", Toast.LENGTH_SHORT).show();
//            }
//
//            public void onSwipeRight() {
//                Toast.makeText(HorizontalUSBTouchPlayerActivity.this, "right", Toast.LENGTH_SHORT).show();
//                if (currentAdvertisementNo > 0) {
//                    currentAdvertisementNo = currentAdvertisementNo - 2;
//                    if (currentAdvertisementNo == -1) {
//                        currentAdvertisementNo = (completFileList.length - 1);
//                    }
//                } else {
//                    currentAdvertisementNo = (completFileList.length - 1);
//                }
//                showCurrentAd();
//            }
//
//            public void onSwipeLeft() {
//                Toast.makeText(HorizontalUSBTouchPlayerActivity.this, "left", Toast.LENGTH_SHORT).show();
//                showCurrentAd();
//            }
//
//            public void onSwipeBottom() {
//                Toast.makeText(HorizontalUSBTouchPlayerActivity.this, "bottom", Toast.LENGTH_SHORT).show();
//            }
//
//        });
}

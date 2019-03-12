package com.displayfort.dfortusbtouch.receiver;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.displayfort.dfortusbtouch.BaseSupportActivity;
import com.displayfort.dfortusbtouch.R;
//import com.netcompss.ffmpeg4android.GeneralUtils;
//import com.netcompss.ffmpeg4android.Prefs;
//import com.netcompss.ffmpeg4android.ProgressCalculator;
//import com.netcompss.loader.LoadJNI;

import java.io.File;
import java.net.URLConnection;

/**
 * Created by Husain on 14-03-2016.
 */
public class StartService extends Service {
    private String workFolder = null;
    private String demoVideoFolder = null;
    private String demoVideoPath = null;
    private String vkLogPath = null;
    private Context context;
    private File[] completFileList;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = this;
//        demoVideoFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + Prefs.FOLDER;
//        demoVideoPath = demoVideoFolder + "in.mp4";
//
//        Log.i(Prefs.TAG, getString(R.string.app_name) + " version: " + GeneralUtils.getVersionName(getApplicationContext()));
//        workFolder = getApplicationContext().getFilesDir().getAbsolutePath() + "/";
//        //Log.i(Prefs.TAG, "workFolder: " + workFolder);
//        vkLogPath = workFolder + "vk.log";
//        int rc = GeneralUtils.isLicenseValid(getApplicationContext(), workFolder);
//        Log.i(Prefs.TAG, "License check RC: " + rc);

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
//        if (intent != null) {
//            String FilePath = intent.getStringExtra("FILEPATH");
//            File mainFile = new File(FilePath);
//            File[] completFileLists = mainFile.listFiles();
//            int j = 0;
//            if (completFileLists != null && completFileLists.length > 0) {
//                completFileList = new File[completFileLists.length];
//                Log.d(Prefs.TAG, "completFileLists List " + completFileLists.length);
//                for (int i = 0; i < completFileLists.length; i++) {
//                    String type = URLConnection.guessContentTypeFromName(completFileLists[i].getName());
//                    if (type != null && type.toLowerCase().contains("video")) {
//                        completFileList[j++] = completFileLists[i];
//                    }
//                }
//                if (completFileList != null && completFileList.length > 0) {
//                    Log.d(Prefs.TAG, "After Filter completFileList List " + completFileList.length);
//                    new TranscdingBackground().execute();
//                }
//            }
//        }
        return super.onStartCommand(intent, flags, startId);
    }


    public class TranscdingBackground extends AsyncTask<String, Integer, Integer> {
        File files;


        @Override
        protected void onPreExecute() {


        }

        protected Integer doInBackground(String... paths) {
//            for (int i = 0; i < completFileList.length; i++) {
//                files = completFileList[i];
//                if (files != null) {
//                    File transposeFile = new File(demoVideoFolder + files.getName());
//                    try {
//                        Log.d(Prefs.TAG, files.getName());
//                        String type = URLConnection.guessContentTypeFromName(files.getName());
//                        if (type != null && type.toLowerCase().contains("video") && !transposeFile.exists()) {
//                            PowerManager powerManager = (PowerManager) getSystemService(Activity.POWER_SERVICE);
//                            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "USB:VK_LOCK");
//                            Log.d(Prefs.TAG, "Acquire wake lock");
//                            wakeLock.acquire();
//                            BaseSupportActivity.hashSet.add(transposeFile.getAbsolutePath());
//                            Log.i(Prefs.TAG, files.getAbsolutePath() + "\n" + transposeFile.getAbsolutePath());
//                            String commandStr8 = "ffmpeg -y -i " + files.getAbsolutePath().replace(" ", "%20") + " -strict experimental -vf transpose=2 -aspect 16:9 " + transposeFile.getAbsolutePath().replace(" ", "%20"); //-s frame rate
//                            LoadJNI vk = new LoadJNI();
//                            try {
//                                /*  /data/user/0/com.displayfort.dfortusb/blue_ball_v.mp4*/
//                                Log.i(Prefs.TAG, "=======running eight command=========");
//                                vk.run(GeneralUtils.utilConvertToComplex(commandStr8), workFolder, getApplicationContext());
//                                GeneralUtils.copyFileToFolder(vkLogPath, demoVideoFolder);
//                                Log.i(Prefs.TAG, "=======running FINISH=========");
////                            try {
////                                runThreadForProgress();
////                            } catch (Exception e) {
////                                e.printStackTrace();
////                            }
//                            } catch (Throwable e) {
//                                Log.e(Prefs.TAG, "vk run exeption.", e);
//                                if (transposeFile.exists()) {
//                                    transposeFile.delete();
//                                }
//                            } finally {
//                                if (wakeLock.isHeld())
//                                    wakeLock.release();
//                                else {
//                                    Log.i(Prefs.TAG, "Wake lock is already released, doing nothing");
//                                }
//                            }
//                            BaseSupportActivity.hashSet.remove(transposeFile.getAbsolutePath());
//                            Log.i(Prefs.TAG, "doInBackground finished");
//                        } else {
//                            Log.i(Prefs.TAG, "File Exist " + transposeFile.getName());
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
            return Integer.valueOf(0);
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        @Override
        protected void onCancelled() {
//            Log.i(Prefs.TAG, "onCancelled");
            //progressDialog.dismiss();
            super.onCancelled();
        }


        @Override
        protected void onPostExecute(Integer result) {
//            Log.i(Prefs.TAG, "onPostExecute");
//            super.onPostExecute(result);
//            // finished Toast
//            String rc = GeneralUtils.getReturnCodeFromLog(vkLogPath);
//            final String status = rc;
//            Log.i(Prefs.TAG, " onPostExecute  rc" + rc);


        }

    }

//    private void runThreadForProgress() {
//        new Thread() {
//            ProgressCalculator pc = new ProgressCalculator(vkLogPath);
//
//            public void run() {
//                Log.d(Prefs.TAG, "Progress update started");
//                int progress = -1;
//                try {
//                    while (true) {
//                        sleep(300);
//                        progress = pc.calcProgress();
//                        if (progress != 0 && progress < 100) {
//                            Log.d(Prefs.TAG, "PROGRESS:" + (progress));
//
//                            Log.i(Prefs.TAG, "setting progress notification: " + progress);
//                        } else if (progress == 100) {
//                            Log.i(Prefs.TAG, "==== progress is 100, exiting Progress update thread");
//                            pc.initCalcParamsForNextInter();
//                            break;
//                        }
//                    }
//
//                } catch (Exception e) {
//                    Log.e("threadmessage", e.getMessage());
//                }
//            }
//        }.start();
//    }

}
package com.displayfort.dfortusbtouch.Fragments;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.displayfort.dfortusbtouch.BaseSupportActivity;
import com.displayfort.dfortusbtouch.R;
import com.netcompss.ffmpeg4android.Prefs;
import com.universalvideoview.UniversalVideoView;

import java.io.File;

/**
 * Created by pc on 07/03/2019 13:30.
 * DFortUSB
 */
public class VideoFragment extends Fragment {
    private File files;
    private static Context mContext;
    private UniversalVideoView videoView;

    public VideoFragment() {
    }

    public static VideoFragment newInstance(Context context, File files) {
        mContext = context;
        VideoFragment oneFragment = new VideoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("files", files.getAbsolutePath());
        oneFragment.setArguments(bundle);
        return oneFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        videoView = view.findViewById(R.id.videoView);
        try {
            ((TextView) view.findViewById(R.id.textviews)).setText(files.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bundle bundle = getArguments();
        files = new File(bundle.getString("files"));
        setVideoPlay(videoView, files);
    }

    private void setVideoPlay(final UniversalVideoView videoView, File file) {
        int interval = -1;
        File newfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + Prefs.FOLDER + file.getName());// new File(getApplicationInfo().dataDir + File.separator + file.getName());
//        boolean isFileFound = newfile.exists();
//        if (!isFileFound) {
            newfile = file;
//        }
        boolean isFileFound = newfile.exists();
        if (!isFileFound) {
            Log.d(" VIDEPATH", "isFileFound" + isFileFound);
            interval = -1; // 1 Second
        } else {
            Log.d("VIDEPATH", "isFileFound" + isFileFound);
            if (!BaseSupportActivity.hashSet.contains(newfile.getAbsolutePath())) {
                Log.d("VIDEPATH", "ViewPagerAdsFromUsbActivity.hashSet" + true);
                String photoPath = newfile.getAbsolutePath();
                interval = 1;//getMiliseconds(newfile);
                if (interval != 0) {
                    Log.i("PostActivity", "Video List is " + file.getPath());
                    Uri myUri = Uri.parse(photoPath); // initialize Uri here
                    String url = photoPath;
                    try {
                        videoView.setVideoURI(Uri.parse(photoPath));
                        videoView.start();
                        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                Log.d("VIDEPATH", "onCompletion");
                                videoView.start();
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
            } else {
                Log.d("VIDEPATH", "ViewPagerAdsFromUsbActivity.hashSet" + false);
                interval = -1;
            }
        }
    }

    private int getMiliseconds(File file) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//use one of overloaded setDataSource() functions to set your data source
        retriever.setDataSource(getActivity(), Uri.fromFile(file));
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMillisec = Long.parseLong(time);
        return (int) timeInMillisec;
    }

    @Override
    public void onStop() {
        onFinishAPP();
        Log.d("VideoFragment", "onStop " + files.getAbsolutePath());
        super.onStop();

    }

    @Override
    public void onPause() {
        onFinishAPP();
        Log.d("VideoFragment", "onPause " + files.getAbsolutePath());
        super.onPause();


    }

    private void onFinishAPP() {
        try {
            if (videoView != null && videoView.isPlaying()) {
                videoView.stopPlayback();
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        Log.d("VideoFragment", "onStart " + files.getAbsolutePath());
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.d("VideoFragment", "onHiddenChanged " + files.getAbsolutePath() + " " + hidden);
        super.onHiddenChanged(hidden);
    }
}

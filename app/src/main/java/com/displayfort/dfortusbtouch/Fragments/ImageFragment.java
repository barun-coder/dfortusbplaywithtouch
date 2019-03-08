package com.displayfort.dfortusbtouch.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.displayfort.dfortusbtouch.R;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by pc on 07/03/2019 13:30.
 * DFortUSB
 */
public class ImageFragment extends Fragment {


    private File files;
    private static Context mContext;

    public ImageFragment() {

    }

    public static ImageFragment newInstance(Context context, File files) {
        mContext = context;
        ImageFragment oneFragment = new ImageFragment();
        Bundle bundle = new Bundle();
        bundle.putString("files", files.getAbsolutePath());
        oneFragment.setArguments(bundle);
        return oneFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView displayImageView = (ImageView) view.findViewById(R.id.imageView2);
        Bundle bundle = getArguments();
        files = new File(bundle.getString("files"));
        ((TextView) view.findViewById(R.id.textview)).setText(files.getName());
        String photoPath = files.getAbsolutePath();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        final Bitmap b = BitmapFactory.decodeFile(photoPath);// BitmapFactory.decodeFile(photoPath, options);
        displayImageView.setImageBitmap(b);
        Log.d("ADVERTISEMENT", photoPath.toString() + "");

    }

    public Bitmap changeOrientation(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream outputStream = null;
        int orientation = ExifInterface.ORIENTATION_ROTATE_270;
        Bitmap imageRotate = rotateBitmap(bitmap, orientation);

        return imageRotate;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }
}

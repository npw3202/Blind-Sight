package com.lab.blindsight;

import android.content.Context;

import android.graphics.ImageFormat;
import android.hardware.Camera;

import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

/**
 * Created by lab on 11/12/16.
 */

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder holder;

    private Camera camera;

    private static final String TAG = "CameraView";

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV not loaded");
        } else {
            Log.d(TAG, "OpenCV loaded");
        }
    }

    public CameraView(Context context, Camera camera) {
        super(context);

        this.camera = camera;
        this.camera.setDisplayOrientation(90);
        //get the holder and set this class as the callback, so we can get camera data here
        this.holder = getHolder();
        this.holder.addCallback(this);
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        //when the surface is created, we can set the camera to draw images in this surface holder
        try {
            camera.setPreviewDisplay(surfaceHolder);

//            Camera.Size setSize = camera.getParameters().getPreviewSize();

//            int bufferSize = setSize.width * setSize.height
//                    * ImageFormat.getBitsPerPixel(
//                    camera.getParameters().getPreviewFormat()) / 8;

            camera.startPreview();

        } catch (IOException e) {
            Log.d("ERROR", "Camera error on surfaceCreated " + e.getMessage());
        }
    }


    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        if (this.holder.getSurface() == null)//check if the surface is ready to receive camera data
            return;
        try {
            this.camera.stopPreview();
        } catch (Exception e) {
            //this will happen when you are trying the camera if it's not running

        }

        //now, recreate the camera preview
        try {
            this.camera.setPreviewDisplay(this.holder);
            this.camera.startPreview();
        } catch (IOException e) {
            Log.d("ERROR", "Camera error on surfaceChanged " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();

    }

}

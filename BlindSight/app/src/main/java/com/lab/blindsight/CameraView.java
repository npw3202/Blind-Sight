package com.lab.blindsight;

import android.content.Context;

import android.hardware.Camera;

import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by lab on 11/12/16.
 */

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder holder;

    private Camera camera;

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
        //when the surface is created, we can set the camera to draw images in this surfaceholder
        try {
            camera.setPreviewDisplay(surfaceHolder);
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
        this.camera.stopPreview();
        this.camera.release();
    }

}

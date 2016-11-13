package com.lab.blindsight;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

import com.lab.blindsight.Audio.AudioThread;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class Stream extends AppCompatActivity implements CvCameraViewListener2 {
    private static final String TAG = "StreamView";

    private CameraBridgeViewBase mOpenCvCameraView;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    AudioThread at = new AudioThread();

    @Override
    protected void onStart() {
        super.onStart();
    }

    Mat cannyMat;

    boolean edgeMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = this.getWindow();

        View decorView = window.getDecorView();

        int systemStyleFlag =
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_FULLSCREEN;

        decorView.setSystemUiVisibility(systemStyleFlag);

        setContentView(R.layout.activity_stream);
        mOpenCvCameraView = (JavaCameraView) findViewById(R.id.OpenCVCam);
        at.start();
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);

        //btn to close the application
        ImageButton imgClose = (ImageButton) findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });

        Switch switchCvMode = (Switch) findViewById(R.id.switchCvMode);
        switchCvMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                edgeMode = isChecked;
            }
        });

    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();

                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();

                    display.getSize(size);

                    int width = size.x;

                    int height = size.y;

                    mOpenCvCameraView.setMinimumHeight(height);
                    mOpenCvCameraView.setMinimumWidth(width);
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
//        mRgba = new Mat(height, width, CvType.CV_8UC4);
//        mRgbaF = new Mat(height, width, CvType.CV_8UC4);
//        mRgbaT = new Mat(width, width, CvType.CV_8UC4);
    }

    public void onCameraViewStopped() {
    }

    int threshold1 = 70;

    int threshold2 = 100;

    final int cannySize = 8;

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        Mat inputGrayscale = inputFrame.gray();
        // Rotate mRgba 90 degrees
        if (cannyMat == null) {
            cannyMat = new Mat();
        }

        Imgproc.Canny(inputGrayscale, cannyMat, threshold1, threshold2);

        Message msg = Message.obtain();

        msg.obj = cannyMat;
        msg.arg1 = cannySize;

        at.mHandler.sendMessage(msg);

        if (!edgeMode) {
            return inputFrame.rgba();
        } else {
            return cannyMat;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this,
                mLoaderCallback);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}

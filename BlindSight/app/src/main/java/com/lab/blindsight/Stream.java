package com.lab.blindsight;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

import com.lab.blindsight.Audio.AudioPlayer;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class Stream extends AppCompatActivity implements CvCameraViewListener2 {
    private static final String TAG = "StreamView";

    private CameraBridgeViewBase mOpenCvCameraView;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    Mat mRgba;
    Mat cannyMat;
    Mat mRgbaF;
    Mat mRgbaT;

    boolean edgeMode = false;

    private Handler audioHandler;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState");
    }

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
        double[][] t = new double[][]{{0, 0, 128, 0}, {128, 0, 0, 0}, {0, 0, 191, 0}, {0, 0, 0, 0}};

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

        audioHandler = new Handler();

        startRepeatingTask();
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
        stopRepeatingTask();
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

    final int cannySize = 4;

    double result[][];

    // 5 seconds by default, can be changed later
    private int mInterval = 500;

    Runnable audioThread = new Runnable() {
        @Override
        public void run() {
            try {
                if (result != null)
                    AudioPlayer.play(result, 1000);
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                audioHandler.postDelayed(audioThread, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        audioThread.run();
    }

    void stopRepeatingTask() {
        audioHandler.removeCallbacks(audioThread);
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        Mat inputGrayscale = inputFrame.gray();
        // Rotate mRgba 90 degrees
        if (cannyMat == null) {
            cannyMat = new Mat();
        }

        Imgproc.Canny(inputGrayscale, cannyMat, threshold1, threshold2);

        Mat processingMat = new Mat();

//        Mat processingMat = cannyMat;

        Imgproc.resize(cannyMat, processingMat, new Size(cannySize, cannySize));

        result = new double[cannySize][cannySize];

        for (int r = 0; r < cannySize; r++) {
            for (int c = 0; c < cannySize; c++) {
                double data[] = processingMat.get(r, c);
                result[r][c] = data[0];
            }
        }

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

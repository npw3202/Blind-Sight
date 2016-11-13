package com.lab.blindsight.Audio;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Created by Nicholas on 11/12/2016.
 */

public class AudioThread extends Thread {
    public Handler mHandler;

    double lastExecuteTime = System.currentTimeMillis();

    @Override
    public void run() {
        Looper.prepare();
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (System.currentTimeMillis() - lastExecuteTime >= 300) {
                    // Act on the message
//                    double[][] input = (double[][]) msg.obj;

                    Mat cannyMat = (Mat) msg.obj;

                    int cannySize = msg.arg1;

                    Mat processingMat = new Mat();

                    Imgproc.resize(cannyMat, processingMat, new Size(cannySize, cannySize));

                    double result[][] = new double[cannySize][cannySize];
                    for (int r = 0; r < cannySize; r++) {
                        for (int c = 0; c < cannySize; c++) {
                            double data[] = processingMat.get(r, c);
                            result[r][c] = data[0];
                        }
                    }

                    AudioPlayer.play(result, 1000);

                    lastExecuteTime = System.currentTimeMillis();
                }
            }
        };
        Looper.loop();
    }
}


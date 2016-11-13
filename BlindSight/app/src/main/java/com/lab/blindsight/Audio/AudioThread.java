package com.lab.blindsight.Audio;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.Timer;

/**
 * Created by Nicholas on 11/12/2016.
 */

public class AudioThread extends Thread{
    public Handler mHandler;
    Timer t = new Timer();
    double lastExecuteTime = System.currentTimeMillis();
    @Override
    public void run(){
        Looper.prepare();
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                if(System.currentTimeMillis() - lastExecuteTime >= 300) {
                    // Act on the message
                    double[][] input = (double[][]) msg.obj;
                    AudioPlayer.play(input, 1000);
                    lastExecuteTime = System.currentTimeMillis();
                }
            }
        };
        Looper.loop();
    }
}


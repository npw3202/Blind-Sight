package com.lab.blindsight.Audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.util.ArrayList;

/**
 * Created by Nicholas on 11/12/2016.
 */

public class AudioPlayer {
    public static final int SAMPLE_RATE = 16000;
    public static void play(double[][] data, double duration){
        AudioTrack at;
        int bufsizbytes = (int) (duration * SAMPLE_RATE / 1000);
        int bufsizsamps = bufsizbytes / 2;
        short buffer[] = fillbuf(bufsizsamps, data);
        try
        {
            at = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, bufsizbytes,
                    AudioTrack.MODE_STATIC);
            at.setStereoVolume(1.0f, 1.0f);
            at.write(buffer, 0, bufsizsamps);
            at.play();
            Thread.sleep(2000);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static short[] fillbuf(int bufsizsamps, double[][] data)
    {
        ArrayList<Double> dataEncoded = HilbertCurve.generateWalk(data);

        double t, omega;
        double dt = 1.0 / SAMPLE_RATE;
        t = 0.0;
        omega = (float) (2.0 * Math.PI * 1000.0f);

        short buffer[] = new short[bufsizsamps];
        for (int i = 0; i < bufsizsamps; i++)
        {
            buffer[i] = (short)(Short.MAX_VALUE * FourierTransform.evaluate(dataEncoded,t));
            t += dt;
        }
        return buffer;
    }
}

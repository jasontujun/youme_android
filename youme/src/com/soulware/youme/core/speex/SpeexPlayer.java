/**
 *
 */
package com.soulware.youme.core.speex;

import com.morln.app.utils.XLog;
import com.soulware.youme.core.speex.core.SpeexDecoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @author Gauss
 *
 */
public class SpeexPlayer {

    private static final String TAG = "Speex";

    private String fileName;
    private PlayThread playThread;


    public void startPlay(String fileName) {
        this.fileName = fileName;
        if (playThread != null) {
            playThread.stopPlay();
            playThread = null;
        }
        playThread = new PlayThread();
        playThread.start();
    }

    public void stopPlay() {
        if (playThread != null)
            playThread.stopPlay();

    }

    private SpeexDecoder.DecodeProgressListener listener
            = new SpeexDecoder.DecodeProgressListener() {
        @Override
        public void decoded(long size) {
            XLog.d(TAG, "decode progress:" + size);
        }
    };

    class PlayThread extends Thread {

        private SpeexDecoder speexdec;

        public PlayThread() {
            speexdec = new SpeexDecoder();
        }

        public void stopPlay() {
            speexdec.stopDecode();
        }

        public void run() {
            File file = new File(fileName);
            try {
                InputStream inputStream = new FileInputStream(file);
                speexdec.startDecode(inputStream, listener);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                XLog.d(TAG, "sound file not exist!");
            }
        }
    };
}

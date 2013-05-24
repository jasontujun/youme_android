/**
 *
 */
package com.soulware.youme.core.speech;

import com.soulware.youme.core.speech.speex.SpeexDecoder;

/**
 * @author Gauss
 *
 */
public class SpeechPlayer {

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

    class PlayThread extends Thread {

        private SpeexDecoder speexdec;

        public PlayThread() {
            speexdec = new SpeexDecoder();
        }

        public void stopPlay() {
            speexdec.stopDecode();
        }

        public void run() {
            speexdec.startDecode(fileName, null);
        }
    };
}

package com.soulware.youme.core.speech;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import com.soulware.youme.core.speech.speex.SpeexEncoder;
import com.soulware.youme.core.speech.speex.SpeexEncoderListener;
import com.soulware.youme.core.speech.speex.SpeexFrame;
import com.xengine.android.utils.XLog;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class SpeechRecorder {

    private static final String TAG = "Speex";

	private static final int SAMPLE_RATE_IN_HZ = 8000;

    private RecordThread recordThread;

    public void startRecord(String filePath, SpeexEncoderListener listener) {
        if (recordThread != null) {
            recordThread.stopRecord();
            recordThread = null;
        }
        recordThread = new RecordThread(filePath, listener);
        recordThread.start();
    }

    public void stopRecord() {
        if (recordThread != null) {
            recordThread.stopRecord();
            recordThread = null;
        }
    }

    private class RecordThread extends Thread {
        private boolean isRecording = false;

        private String filePath;

        SpeexEncoderListener listener;

        private RecordThread(String filePath, SpeexEncoderListener listener) {
            super();
            this.filePath = filePath;
            this.listener = listener;
        }

        public void stopRecord() {
            XLog.d(TAG, "set stopRecord.");
            isRecording = false;
        }

        @Override
        public void run() {
            isRecording = true;

            OutputStream out;
            try {
                out = new FileOutputStream(filePath);
            } catch (FileNotFoundException e) {
                XLog.d(TAG, "record file error.#$#$#$");
                e.printStackTrace();
                return;
            }

            int bufferSize = AudioRecord.getMinBufferSize(
                    SAMPLE_RATE_IN_HZ,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            XLog.d(TAG, "bufferSize: " + bufferSize);
            AudioRecord recorder = new AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE_IN_HZ,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize);
            SpeexEncoder encoder = new SpeexEncoder(SAMPLE_RATE_IN_HZ, 1, true);
            encoder.startEncode(out, listener);
            recorder.startRecording();
            XLog.d(TAG, "start to recording.........");

            int bufferRead = 0;
            short[] tempBuffer = new short[SpeexFrame.FRAME_SIZE];
            while (this.isRecording) {
                bufferRead = recorder.read(tempBuffer, 0, SpeexFrame.FRAME_SIZE);
                XLog.d(TAG, "bufferRead:" + bufferRead);
                if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
                    XLog.d(TAG, "recorder read error....ERROR_INVALID_OPERATION");
                    throw new IllegalStateException("read() returned AudioRecord.ERROR_INVALID_OPERATION");
                } else if (bufferRead == AudioRecord.ERROR_BAD_VALUE) {
                    XLog.d(TAG, "recorder read error....ERROR_BAD_VALUE");
                    throw new IllegalStateException("read() returned AudioRecord.ERROR_BAD_VALUE");
                }

                encoder.offerFrame(tempBuffer, bufferRead);
            }
            XLog.d(TAG, "is stopRecord.");

            recorder.stop();
            encoder.stopEncode();
        }
    }
}

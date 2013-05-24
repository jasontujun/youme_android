package com.soulware.youme.core.speech.speex;


import com.xengine.android.utils.XLog;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by 赵之韵.
 * Email: ttxzmorln@163.com
 * Date: 12-7-11
 * Time: 下午4:23
 */
public class SpeexEncoder {

    private static final String TAG = "Speex";
    private static final int ENCODE_BUFFER_SIZE = 1024;
    private static Speex speex;

    private byte[] encodedBuf = new byte[ENCODE_BUFFER_SIZE];
    private BlockingQueue<SpeexFrame> tobeEncoded = new ArrayBlockingQueue<SpeexFrame>(1024);
    private EncodeThread encodeThread;
    private OggSpeexWriter outWriter;
    private SpeexEncoderListener listener;

    static {
        speex = new Speex();
        speex.loadLib();
    }

    public SpeexEncoder(int sampleRate, int channels, boolean vbr) {
        super();
        outWriter = new OggSpeexWriter(
                OggSpeexWriter.NB,
                sampleRate,
                channels,
                1,
                vbr);
    }

    public boolean offerFrame(short[] data, int size) {
        return tobeEncoded.offer(new SpeexFrame(data, size));
    }

    public void startEncode(OutputStream out, SpeexEncoderListener listener) {
        outWriter.setOutputStream(out);
        if(encodeThread != null) {
            encodeThread.stopEncode();
            encodeThread = null;
        }
        encodeThread = new EncodeThread(listener);
        encodeThread.start();
    }

    public void stopEncode() {
        if(encodeThread != null) {
            encodeThread.stopEncode();
            encodeThread = null;
        }
    }

    private void clearBuff() {
        for (int i = 0; i < ENCODE_BUFFER_SIZE; i++)
            encodedBuf[i] = 0;
    }

    private class EncodeThread extends Thread {
        private boolean isEncoding = false;

        private SpeexEncoderListener listener;

        private EncodeThread(SpeexEncoderListener listener) {
            super();
            this.listener = listener;
        }

        public void stopEncode() {
            XLog.d(TAG, "set stopEncode.");
            isEncoding = false;
            offerFrame(null, -1);// -1 表示插入最后一帧,防止BlockingQueue阻塞而无法停止
        }

        @Override
        public void run() {
            isEncoding = true;
            int packetCount = 0;
            int encodeSize = 0;

            try {
                speex.open();
                outWriter.writeHeader("Speex Encode");
                XLog.d(TAG, "Start encoding.");
                while(isEncoding) {
                    SpeexFrame frame = tobeEncoded.take();
                    if (frame.getSize() != -1) {
                        int readSize = speex.encode(frame.getData(), 0, encodedBuf, frame.getSize());
                        XLog.d(TAG, "encode readSize:" + readSize);
                        outWriter.writePacket(encodedBuf, 0, readSize);
                        clearBuff();
                        XLog.d(TAG, "encode count:" + packetCount);
                        packetCount++;
                        encodeSize =+ readSize;

                        if (listener != null)
                            listener.encodeProgress(encodeSize);
                    }
                }
                XLog.d(TAG, "encode finish.");
                outWriter.close();
            } catch (InterruptedException e) {
                XLog.e(TAG, "Encoding is interrupted.");
                e.printStackTrace();
            } catch (IOException e) {
                XLog.e(TAG, "Encoding IOException.");
                e.printStackTrace();
            } finally {
                XLog.d(TAG, "Stop encoding, packet count: " + packetCount);
                speex.close();
                if (listener != null)
                    listener.encodeFinish();
            }
        }
    }

}

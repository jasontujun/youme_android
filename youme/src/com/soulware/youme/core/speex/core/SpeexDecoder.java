
package com.soulware.youme.core.speex.core;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import com.xengine.android.utils.XLog;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by 赵之韵.
 * Email: ttxzmorln@163.com
 * Date: 12-7-11
 * Time: 下午4:23
 */
public class SpeexDecoder {

    private static final String TAG = "Speex";
    private static Speex speex;

    private boolean isDecoding = false;
    private AudioTrack audioTrack;

    static {
        speex = new Speex();
        speex.loadLib();
    }

    public void stopDecode() {
        isDecoding = false;
    }

    public void startDecode(String filePath, SpeexDecoderListener listener) {
        byte[] header = new byte[2048];
        byte[] payload = new byte[65536];
        final int OGG_HEADER_SIZE = 27;
        final int OGG_SEG_OFFSET = 26;
        final String OGG_ID = "OggS";
        int segments = 0;
        int curSeg = 0;
        int bodyBytes = 0;
        int decSize = 0;
        int packetNo = 0;

        speex.open();
        isDecoding = true;
        long decodedSize = 0;
        try {
            XLog.d(TAG, "Start decode.");
            RandomAccessFile input = new RandomAccessFile(filePath, "r");// 注意，用InputStream会有bug
            int originChecksum;
            int checksum;
            // read until we get to EOF
            while (isDecoding) {
                // 读取ogg的header
                input.readFully(header, 0, OGG_HEADER_SIZE);
                originChecksum = readInt(header, 22);
                readLong(header, 6);
                header[22] = 0;
                header[23] = 0;
                header[24] = 0;
                header[25] = 0;
                checksum = OggCrc.checksum(0, header, 0, OGG_HEADER_SIZE);

                // make sure its a OGG header
                if (!OGG_ID.equals(new String(header, 0, 4))) {
                    XLog.e(TAG, "Illegal ogg file format.");
                    return;
                }

                /* how many segments are there? */
                segments = header[OGG_SEG_OFFSET] & 0xFF;
                //XLog.d(TAG, "Segments count: " + segments);
                input.readFully(header, OGG_HEADER_SIZE, segments);
                checksum = OggCrc.checksum(checksum, header, OGG_HEADER_SIZE, segments);

                /* decode each segment, writing output to wav */
                for (curSeg = 0; curSeg < segments; curSeg++) {
                    /* get the number of bytes in the segment */
                    bodyBytes = header[OGG_HEADER_SIZE + curSeg] & 0xFF;
                    if (bodyBytes == 255) {
                        XLog.e(TAG, "sorry, don't handle 255 sizes!");
                        return;
                    }

                    input.readFully(payload, 0, bodyBytes);
                    checksum = OggCrc.checksum(checksum, payload, 0, bodyBytes);

                    /* decode the segment */
                    /* if first packet, read the Speex header */
                    if (packetNo == 0) {
                        if (readSpeexHeader(payload, 0, bodyBytes))
                            audioTrack.play();
                        else
                            throw new IllegalStateException("read Speex header error!");
                        packetNo++;
                    } else if (packetNo == 1) { // Ogg Comment packet
                        packetNo++;
                    } else {
                        /* get the amount of decoded data */
                        short[] decoded = new short[160];
                        if ((decSize = speex.decode(payload, decoded, 160)) > 0) {
                            audioTrack.write(decoded, 0, decSize);
                            decodedSize += decSize;

                            if(listener != null)
                                listener.decodedProgress(decodedSize);
                        }
                        packetNo++;
                    }
                }
                XLog.d(TAG, "decode, originCheckSum:" + originChecksum + ", checksum:" + checksum);
                if (checksum != originChecksum) {
                    throw new IOException("Ogg CheckSums do not match");
                }
            }

            input.close();
            if(listener != null)
                listener.decodedFinish(true);
        } catch (EOFException e) {
            // RandomAccessFile.readFully()会抛出EOFException,靠这个exception来结束的
            XLog.d(TAG, "Reach file end!");
            if(listener != null)
                listener.decodedFinish(true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            XLog.d(TAG, "speex file not found!");
            if(listener != null)
                listener.decodedFinish(false);
        } catch (IOException e) {
            e.printStackTrace();
            if(listener != null)
                listener.decodedFinish(false);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            if(listener != null)
                listener.decodedFinish(false);
        } finally {
            XLog.d(TAG, "Decoded packet count: " + (packetNo - 2));
            XLog.d(TAG, "Stop track.");
            speex.close();
            if (audioTrack != null) {
                audioTrack.stop();
                audioTrack.release();
            }
        }
    }

    private boolean initializeAndroidAudio(int sampleRate) {
        int minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        if (minBufferSize < 0)
            return false;

        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize,
                AudioTrack.MODE_STREAM);
        return true;
    }

    /**
     * Reads the header packet.
     *
     * <pre>
     *  0 -  7: speex_string: "Speex   "
     *  8 - 27: speex_version: "speex-1.0"
     * 28 - 31: speex_version_id: 1
     * 32 - 35: header_size: 80
     * 36 - 39: rate
     * 40 - 43: mode: 0=narrowband, 1=wb, 2=uwb
     * 44 - 47: mode_bitstream_version: 4
     * 48 - 51: nb_channels
     * 52 - 55: bitrate: -1
     * 56 - 59: frame_size: 160
     * 60 - 63: vbr
     * 64 - 67: frames_per_packet
     * 68 - 71: extra_headers: 0
     * 72 - 75: reserved1
     * 76 - 79: reserved2
     * </pre>
     *
     * @param packet
     * @param offset
     * @param bytes
     * @return
     * @throws Exception
     */
    private boolean readSpeexHeader(final byte[] packet, final int offset, final int bytes) {
        if (bytes != 80) {
            return false;
        }
        if (!"Speex   ".equals(new String(packet, offset, 8))) {
            return false;
        }
        int mode = packet[40 + offset] & 0xFF;
        int sampleRate = readInt(packet, offset + 36);
        int channels = readInt(packet, offset + 48);
        int framesPerPacket = readInt(packet, offset + 64);
        int frameSize = readInt(packet, offset + 56);
        XLog.d(TAG, " + sampleRate: " + sampleRate + "...........");
        return initializeAndroidAudio(sampleRate);
    }

    protected static int readInt(final byte[] data, final int offset) {
        // no 0xff on the last one to keep the sign
        return (data[offset] & 0xff) | ((data[offset + 1] & 0xff) << 8) | ((data[offset + 2] & 0xff) << 16) | (data[offset + 3] << 24);
    }

    protected static long readLong(final byte[] data, final int offset) {
        // no 0xff on the last one to keep the sign
        return (data[offset] & 0xff) | ((data[offset + 1] & 0xff) << 8) | ((data[offset + 2] & 0xff) << 16)
                | ((data[offset + 3] & 0xff) << 24) | ((data[offset + 4] & 0xff) << 32) | ((data[offset + 5] & 0xff) << 40)
                | ((data[offset + 6] & 0xff) << 48) | (data[offset + 7] << 56);
    }

    protected static int readShort(final byte[] data, final int offset) {
        // no 0xff on the last one to keep the sign
        return (data[offset] & 0xff) | (data[offset + 1] << 8);
    }

}

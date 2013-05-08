
package com.soulware.youme.core.speex.core;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import com.morln.app.utils.XLog;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 赵之韵.
 * Email: ttxzmorln@163.com
 * Date: 12-7-11
 * Time: 下午4:23
 */
public class SpeexDecoder {

    private static final String TAG = "Speex";
    private static final int SAMPLE_RATE_IN_HZ = 8000;
    private static Speex speex;

    private boolean isDecoding = false;


    static {
        speex = new Speex();
        speex.loadLib();
    }

    public void stopDecode() {
        isDecoding = false;
    }

    public void startDecode(InputStream input, DecodeProgressListener listener) {

        int minBuf = AudioTrack.getMinBufferSize(
                SAMPLE_RATE_IN_HZ,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        AudioTrack audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                SAMPLE_RATE_IN_HZ,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, minBuf,
                AudioTrack.MODE_STREAM);

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

            int originChecksum;
            int checksum;

            while (isDecoding) {
                // 读取ogg的header
                input.read(header, 0, OGG_HEADER_SIZE);
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
                input.read(header, OGG_HEADER_SIZE, segments);
                checksum = OggCrc.checksum(checksum, header, OGG_HEADER_SIZE, segments);

                audioTrack.play();

                /* decode each segment, writing output to wav */
                for (curSeg = 0; curSeg < segments; curSeg++) {
                    /* get the number of bytes in the segment */
                    bodyBytes = header[OGG_HEADER_SIZE + curSeg] & 0xFF;
                    if (bodyBytes == 255) {
                        XLog.e(TAG, "sorry, don't handle 255 sizes!");
                        return;
                    }

                    input.read(payload, 0, bodyBytes);
                    checksum = OggCrc.checksum(checksum, payload, 0, bodyBytes);

                    /* decode the segment */
                    /* if first packet, read the Speex header */
                    if (packetNo == 0) {
                        readSpeexHeader(payload, 0, bodyBytes, true);
                        packetNo++;
                    } else if (packetNo == 1) { // Ogg Comment packet
                        packetNo++;
                    } else {
                        /* get the amount of decoded data */
                        short[] decoded = new short[160];
                        if ((decSize = speex.decode(payload, decoded, 160)) > 0) {
                            audioTrack.write(decoded, 0, decSize);
                            decodedSize += decSize;
                            if(listener != null) {
                                listener.decoded(decodedSize);
                            }
                        }
                        packetNo++;
                    }
                }

                if (checksum != originChecksum) {
                    throw new IOException("Ogg CheckSums do not match");
                }
            }

            input.close();
            audioTrack.stop();
        } catch (EOFException e) {
            // 好吧，在这里是靠exception来结束的
            XLog.d(TAG, "Reach file end!");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } finally {
            XLog.d(TAG, "Decoded packet count: " + (packetNo - 2));
            XLog.d(TAG, "Stop track.");
            speex.close();
            audioTrack.release();
        }

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
    private boolean readSpeexHeader(final byte[] packet, final int offset, final int bytes, boolean init) {
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

        return true;
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

    public interface DecodeProgressListener {
        void decoded(long size);
    }

}

package com.soulware.youme.core.speex.core;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * Ogg Speex Writer
 * @author 赵之韵
 */
public class OggSpeexWriter {

    /** Number of packets in an Ogg page (must be less than 255) */
    public static final int PACKETS_PER_OGG_PAGE = 250;

    /** The OutputStream */
    private OutputStream out;

    public static final int NB = 0;
    public static final int WB = 1;
    public static final int UWB = 2;

    /** Defines the encoder mode (0=NB, 1=WB and 2-UWB). */
    private int mode;

    /** Defines the sampling rate of the audio input. */
    private int sampleRate;

    /** Defines the number of channels of the audio input (1=mono, 2=stereo). */
    private int channels;

    /** Defines the number of frames per speex packet. */
    private int framesPerPacket;

    /** Defines whether or not to use VBR (Variable Bit Rate). */
    private boolean vbr;

    /** Ogg Stream Serial Number */
    private int streamSerialNumber;

    /** Data buffer */
    private byte[] dataBuffer;

    /** Pointer within the Data buffer */
    private int dataBufferPtr;

    /** Header buffer */
    private byte[] headerBuffer;

    /** Pointer within the Header buffer */
    private int headerBufferPtr;

    /** Ogg Page count */
    private int pageCount;

    /** Speex packet count within an Ogg Page */
    private int packetCount;

    /**
     * Absolute granule position (the number of audio samples from beginning of
     * file to end of Ogg Packet).
     */
    private long granulePos;

    public OggSpeexWriter() {
        if (streamSerialNumber == 0)
            streamSerialNumber = new Random().nextInt();
        // ogg格式每一页的最大数据容量为65025
        dataBuffer = new byte[65565];
        dataBufferPtr = 0;
        headerBuffer = new byte[255];
        headerBufferPtr = 0;
        pageCount = 0;
        packetCount = 0;
        granulePos = 0;
    }

    /**
     * 构造函数。
     * @param mode 编码模式 (0=NB, 1=WB, 2=UWB)
     * @param sampleRate 采样率。
     * @param channels 声道数量 (1=mono, 2=stereo, ...)。
     * @param framesPerPacket 每个数据包中包含多少帧数据。
     * @param vbr 是否动态码率。
     */
    public OggSpeexWriter(final int mode, final int sampleRate, final int channels, final int framesPerPacket, final boolean vbr) {
        this();
        setFormat(mode, sampleRate, channels, framesPerPacket, vbr);
    }

    /**
     * 设置输出格式，必须在WriteHeader()方法之前调用。
     * @param mode 编码模式 (0=NB, 1=WB, 2=UWB)
     * @param sampleRate 采样率。
     * @param channels 声道数量 (1=mono, 2=stereo, ...)。
     * @param framesPerPacket 每个数据包中包含多少帧数据。
     * @param vbr 是否动态码率。
     */
    private void setFormat(final int mode, final int sampleRate, final int channels, final int framesPerPacket, boolean vbr) {
        this.mode = mode;
        this.sampleRate = sampleRate;
        this.channels = channels;
        this.framesPerPacket = framesPerPacket;
        this.vbr = vbr;
    }

    /**
     * Sets the Stream Serial Number. Must not be changed mid stream.
     *
     * @param serialNumber
     */
    public void setSerialNumber(final int serialNumber) {
        this.streamSerialNumber = serialNumber;
    }

    /**
     * Closes the output file.
     *
     * @exception java.io.IOException
     *                if there was an exception closing the Audio Writer.
     */
    public void close() throws IOException {
        flush(true);
        out.close();
    }

    /**
     * 设置输出流
     */
    public void setOutputStream(OutputStream out) {
        this.out = out;
    }

    /**
     * Writes the header pages that start the Ogg Speex file. Prepares file for
     * data to be written.
     *
     * @param comment
     *            description to be included in the header.
     * @exception java.io.IOException
     */
    public void writeHeader(final String comment) throws IOException {
        int checksum;
        byte[] header;
        byte[] data;
        /* writes the OGG header page */
        header = buildOggPageHeader(2, 0, streamSerialNumber, pageCount++, 1, new byte[]{80});
        data = buildSpeexHeader(sampleRate, mode, channels, vbr, framesPerPacket);
        checksum = OggCrc.checksum(0, header, 0, header.length);
        checksum = OggCrc.checksum(checksum, data, 0, data.length);
        LittleEndian.writeInt(header, 22, checksum);
        out.write(header);
        out.write(data);
        /* writes the OGG comment page */
        header = buildOggPageHeader(0, 0, streamSerialNumber, pageCount++, 1, new byte[] { (byte) (comment.length() + 8) });
        data = buildSpeexComment(comment);
        checksum = OggCrc.checksum(0, header, 0, header.length);
        checksum = OggCrc.checksum(checksum, data, 0, data.length);
        LittleEndian.writeInt(header, 22, checksum);
        out.write(header);
        out.write(data);
    }

    /**
     * Writes a packet of audio.
     *
     * @param data
     *            - audio data.
     * @param offset
     *            - the offset from which to start reading the data.
     * @param len
     *            - the length of data to read.
     * @exception java.io.IOException
     */
    public void writePacket(final byte[] data, final int offset, final int len) throws IOException {
        if (len <= 0) { // nothing to write
            return;
        }
        if (packetCount > PACKETS_PER_OGG_PAGE) {
            flush(false);
        }
        System.arraycopy(data, offset, dataBuffer, dataBufferPtr, len);
        dataBufferPtr += len;
        headerBuffer[headerBufferPtr++] = (byte) len;
        packetCount++;
        granulePos += framesPerPacket * (mode == 2 ? 640 : (mode == 1 ? 320 : 160));
    }

    /**
     * Flush the Ogg page out of the buffers into the file.
     *
     * @param eos
     *            - end of stream
     * @exception java.io.IOException
     */
    private void flush(final boolean eos) throws IOException {
        int chksum;
        byte[] header;
        /* writes the OGG header page */
        header = buildOggPageHeader((eos ? 4 : 0), granulePos, streamSerialNumber, pageCount++, packetCount, headerBuffer);
        chksum = OggCrc.checksum(0, header, 0, header.length);
        chksum = OggCrc.checksum(chksum, dataBuffer, 0, dataBufferPtr);
        LittleEndian.writeInt(header, 22, chksum);
        out.write(header);
        out.write(dataBuffer, 0, dataBufferPtr);
        dataBufferPtr = 0;
        headerBufferPtr = 0;
        packetCount = 0;
    }

    /**
     * Writes an Ogg Page Header to the given byte array.
     * @param buf     the buffer to write to.
     * @param offset  the from which to start writing.
     * @param headerType the header type flag
     *          (0=normal, 2=bos: beginning of stream, 4=eos: end of stream).
     * @param granulePos the absolute granule position.
     * @param streamSerialNumber
     * @param pageCount
     * @param packetCount
     * @param packetSizes
     * @return the amount of data written to the buffer.
     */
    public static int writeOggPageHeader(
            byte[] buf, int offset, int headerType,
            long granulePos, int streamSerialNumber,
            int pageCount, int packetCount,
            byte[] packetSizes) {
        LittleEndian.writeString(buf, offset, "OggS");             //  0 -  3: capture_pattern
        buf[offset+4] = 0;                            //       4: stream_structure_version
        buf[offset+5] = (byte) headerType;            //       5: header_type_flag
        LittleEndian.writeLong(buf, offset+6, granulePos);         //  6 - 13: absolute granule position
        LittleEndian.writeInt(buf, offset+14, streamSerialNumber); // 14 - 17: stream serial number
        LittleEndian.writeInt(buf, offset+18, pageCount);          // 18 - 21: page sequence no
        LittleEndian.writeInt(buf, offset+22, 0);                  // 22 - 25: page checksum
        buf[offset+26] = (byte) packetCount;          //      26: page_segments
        System.arraycopy(packetSizes, 0,              // 27 -  x: segment_table
                buf, offset+27, packetCount);
        return packetCount+27;
    }

    /**
     * Builds and returns an Ogg Page Header.
     * @param headerType the header type flag
     *          (0=normal, 2=bos: beginning of stream, 4=eos: end of stream).
     * @param granulePos the absolute granule position.
     * @param streamSerialNumber
     * @param pageCount
     * @param packetCount
     * @param packetSizes
     * @return an Ogg Page Header.
     */
    public static byte[] buildOggPageHeader(
            int headerType, long granulePos,
            int streamSerialNumber, int pageCount,
            int packetCount, byte[] packetSizes) {
        byte[] data = new byte[packetCount+27];
        writeOggPageHeader(data, 0, headerType, granulePos, streamSerialNumber,
                pageCount, packetCount, packetSizes);
        return data;
    }

    /**
     * Writes a Speex Header to the given byte array.
     * @param buf     the buffer to write to.
     * @param offset  the from which to start writing.
     * @param sampleRate
     * @param mode
     * @param channels
     * @param vbr
     * @param framesPerPacket
     * @return the amount of data written to the buffer.
     */
    public static int writeSpeexHeader(
            byte[] buf, int offset, int sampleRate,
            int mode, int channels, boolean vbr,
            int framesPerPacket) {
        LittleEndian.writeString(buf, offset, "Speex   ");    //  0 - 7: speex_string
        LittleEndian.writeString(buf, offset+8, "speex-1.2rc"); //  8 - 27: speex_version
        System.arraycopy(new byte[11], 0, buf, offset+17, 11); // : speex_version (fill in up to 20 bytes)
        LittleEndian.writeInt(buf, offset+28, 1);           // 28 - 31: speex_version_id
        LittleEndian.writeInt(buf, offset+32, 80);          // 32 - 35: header_size
        LittleEndian.writeInt(buf, offset+36, sampleRate);  // 36 - 39: rate
        LittleEndian.writeInt(buf, offset+40, mode);        // 40 - 43: mode (0=NB, 1=WB, 2=UWB)
        LittleEndian.writeInt(buf, offset+44, 4);           // 44 - 47: mode_bitstream_version
        LittleEndian.writeInt(buf, offset+48, channels);    // 48 - 51: nb_channels
        LittleEndian.writeInt(buf, offset+52, -1);          // 52 - 55: bitrate
        LittleEndian.writeInt(buf, offset+56, 160 << mode); // 56 - 59: frame_size (NB=160, WB=320, UWB=640)
        LittleEndian.writeInt(buf, offset+60, vbr?1:0);     // 60 - 63: vbr
        LittleEndian.writeInt(buf, offset+64, framesPerPacket);     // 64 - 67: frames_per_packet
        LittleEndian.writeInt(buf, offset+68, 0);           // 68 - 71: extra_headers
        LittleEndian.writeInt(buf, offset+72, 0);           // 72 - 75: reserved1
        LittleEndian.writeInt(buf, offset+76, 0);           // 76 - 79: reserved2
        return 80;
    }

    /**
     * Builds a Speex Header.
     * @param sampleRate
     * @param mode
     * @param channels
     * @param vbr
     * @param framesPerPacket
     * @return a Speex Header.
     */
    public static byte[] buildSpeexHeader(
            int sampleRate, int mode, int channels,
            boolean vbr, int framesPerPacket) {
        byte[] data = new byte[80];
        writeSpeexHeader(data, 0, sampleRate, mode, channels, vbr, framesPerPacket);
        return data;
    }

    /**
     * Writes a Speex Comment to the given byte array.
     * @param buf     the buffer to write to.
     * @param offset  the from which to start writing.
     * @param comment the comment.
     * @return the amount of data written to the buffer.
     */
    public static int writeSpeexComment(byte[] buf, int offset, String comment) {
        int length = comment.length();
        LittleEndian.writeInt(buf, offset, length);       // vendor comment size
        LittleEndian.writeString(buf, offset+4, comment); // vendor comment
        LittleEndian.writeInt(buf, offset+length+4, 0);   // user comment list length
        return length+8;
    }

    /**
     * Builds and returns a Speex Comment.
     * @param comment the comment.
     * @return a Speex Comment.
     */
    public static byte[] buildSpeexComment(String comment) {
        byte[] data = new byte[comment.length()+8];
        writeSpeexComment(data, 0, comment);
        return data;
    }
}

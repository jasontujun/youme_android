package com.soulware.youme.core.speex.core;

/**
 * Created by 赵之韵.
 * Date: 12-5-5
 * Time: 下午9:05
 */
public class Speex {
    private static final String TAG = "Speex";

    public static final int COMPRESSION_4KBPS = 1;

    public static final int COMPRESSION_6KBPS = 2;

    public static final int COMPRESSION_8KBPS = 4;

    public static final int COMPRESSION_11KBPS = 6;

    public static final int COMPRESSION_15KBPS = 8;

    /**
     * 载入speex库
     */
    public boolean loadLib() {
        try {
            System.loadLibrary("speex");
            return true;
        }catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
            return false;
        }
    }

    public int open() {
        return open(COMPRESSION_8KBPS);
    }

    /**
     * @param compression 压缩率，也就是比特率，每秒的数据量。
     */
    public native int open(int compression);

    public native void close();

    public native int getEncodeFrameSize();

    public native int getDecodeFrameSize();

    public native int decode(byte encoded[], short lin[], int size);

    public native int encode(short lin[], int offset, byte encoded[], int size);
}

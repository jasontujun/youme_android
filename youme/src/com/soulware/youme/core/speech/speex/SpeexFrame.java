package com.soulware.youme.core.speech.speex;

/**
 * Created by 赵之韵.
 * Email: ttxzmorln@163.com
 * Date: 12-5-15
 * Time: 下午2:53
 */
public class SpeexFrame {

    public static final int FRAME_SIZE = 160;
    private short[] data;
    private int size;

    public SpeexFrame(short[] data, int size) {
        this.size = size;
        this.data = new short[FRAME_SIZE];
        if (data != null && size >=0)
            System.arraycopy(data, 0, this.data, 0, size);
    }

    public short[] getData() {
        return data;
    }

    public int getSize() {
        return size;
    }

}

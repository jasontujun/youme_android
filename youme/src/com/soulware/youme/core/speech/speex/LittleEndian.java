package com.soulware.youme.core.speech.speex;

import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Little-Endian工具。
 * Created by 赵之韵.
 * Email: ttxzmorln@163.com
 * Date: 12-7-11
 * Time: 下午4:23
 */
public class LittleEndian {
    /**
     * Writes a Little-Endian short.
     * @param out the data output to write to.
     * @param v value to write.
     * @exception java.io.IOException
     */
    public static void writeShort(DataOutput out, short v) throws IOException {
        out.writeByte((0xff & v));
        out.writeByte((0xff & (v >>> 8)));
    }

    /**
     * Writes a Little-Endian int.
     * @param out the data output to write to.
     * @param v value to write.
     * @exception java.io.IOException
     */
    public static void writeInt(DataOutput out, int v) throws IOException {
        out.writeByte(0xff & v);
        out.writeByte(0xff & (v >>>  8));
        out.writeByte(0xff & (v >>> 16));
        out.writeByte(0xff & (v >>> 24));
    }

    /**
     * Writes a Little-Endian short.
     * @param os - the output stream to write to.
     * @param v - the value to write.
     * @exception java.io.IOException
     */
    public static void writeShort(OutputStream os, short v) throws IOException {
        os.write((0xff & v));
        os.write((0xff & (v >>> 8)));
    }

    /**
     * Writes a Little-Endian int.
     * @param os - the output stream to write to.
     * @param v - the value to write.
     * @exception java.io.IOException
     */
    public static void writeInt(OutputStream os, int v) throws IOException {
        os.write(0xff & v);
        os.write(0xff & (v >>>  8));
        os.write(0xff & (v >>> 16));
        os.write(0xff & (v >>> 24));
    }

    /**
     * Writes a Little-Endian long.
     * @param os - the output stream to write to.
     * @param v - the value to write.
     * @exception java.io.IOException
     */
    public static void writeLong(OutputStream os, long v) throws IOException {
        os.write((int)(0xff & v));
        os.write((int)(0xff & (v >>>  8)));
        os.write((int)(0xff & (v >>> 16)));
        os.write((int)(0xff & (v >>> 24)));
        os.write((int)(0xff & (v >>> 32)));
        os.write((int)(0xff & (v >>> 40)));
        os.write((int)(0xff & (v >>> 48)));
        os.write((int)(0xff & (v >>> 56)));
    }

    /**
     * Writes a Little-Endian short.
     * @param data   the array into which the data should be written.
     * @param offset the offset from which to start writing in the array.
     * @param v      the value to write.
     */
    public static void writeShort(byte[] data, int offset, int v) {
        data[offset]   = (byte) (0xff & v);
        data[offset+1] = (byte) (0xff & (v >>>  8));
    }

    /**
     * Writes a Little-Endian int.
     * @param data   the array into which the data should be written.
     * @param offset the offset from which to start writing in the array.
     * @param v      the value to write.
     */
    public static void writeInt(byte[] data, int offset, int v) {
        data[offset]   = (byte) (0xff & v);
        data[offset+1] = (byte) (0xff & (v >>>  8));
        data[offset+2] = (byte) (0xff & (v >>> 16));
        data[offset+3] = (byte) (0xff & (v >>> 24));
    }

    /**
     * Writes a Little-Endian long.
     * @param data   the array into which the data should be written.
     * @param offset the offset from which to start writing in the array.
     * @param v      the value to write.
     */
    public static void writeLong(byte[] data, int offset, long v) {
        data[offset]   = (byte) (0xff & v);
        data[offset+1] = (byte) (0xff & (v >>>  8));
        data[offset+2] = (byte) (0xff & (v >>> 16));
        data[offset+3] = (byte) (0xff & (v >>> 24));
        data[offset+4] = (byte) (0xff & (v >>> 32));
        data[offset+5] = (byte) (0xff & (v >>> 40));
        data[offset+6] = (byte) (0xff & (v >>> 48));
        data[offset+7] = (byte) (0xff & (v >>> 56));
    }

    /**
     * Writes a String.
     * @param data   the array into which the data should be written.
     * @param offset the offset from which to start writing in the array.
     * @param v      the value to write.
     */
    public static void writeString(byte[] data, int offset, String v) {
        byte[] str = v.getBytes();
        System.arraycopy(str, 0, data, offset, str.length);
    }
}

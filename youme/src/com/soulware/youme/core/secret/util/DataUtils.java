package com.soulware.youme.core.secret.util;

import java.io.UnsupportedEncodingException;


/**
 * <pre>
 * 位运算符：
 *  >>  : 右移                        b   =   a   >>   2
 *        当a为正数时，最高位补0，而a为负数时，符号位为1，
 *        最高位是补0或是补1取决于编译系统的规定。Turbo C和很多系统规定为补1。
 *  >>> : 右移，左边空出的位以0填充     b   =   a   >>>   2
 *  <<  : 左移                        b   =   a   <<   1
 *
 *  byte:
 *  Java中byte是做为最小的数字来处理的，
 *  因此它的值域被定义为-128~127，也就是signed byte。
 *
 *  unicode:
 *  java默认编码为unicode，而不是ascii码，因为java中的char类型是16位的（无符号），
 *  也就是0~65535，所以才支持中文字符，例如：'中' 。
 *  unicode码为了支持国际化，他的部分编码与ascii码是一致的。
 *  </pre>
 */
public class DataUtils {

    /**
     * argb数组 -> pixel
     * @param argb
     * @return
     */
    public static int argb2pixel(byte[] argb) {
        return argb2pixel(argb[0], argb[1], argb[2], argb[3]);
    }

    /**
     * argb -> pixel
     * @param alpha
     * @param red
     * @param green
     * @param blue
     * @return
     */
    public static int argb2pixel(byte alpha, byte red, byte green, byte blue) {
        int a1 = signedByte2unsignedByte(alpha) << 24;
        int r1 = signedByte2unsignedByte(red) << 16;
        int g1 = signedByte2unsignedByte(green) << 8;
        int b1 = signedByte2unsignedByte(blue);
        return a1 | r1 | g1 | b1;
    }

    /**
     * pixel -> argb数组
     * @param pixel
     * @return
     */
    public static byte[] pixel2argb(int pixel) {
        byte[] result = new byte[4];
        result[0] = (byte) ((pixel >> 24)& 0x000000ff);// alpha
        result[1] = (byte) ((pixel >> 16) & 0x000000ff);// red
        result[2] = (byte) ((pixel >> 8) & 0x000000ff);// green
        result[3] = (byte) (pixel & 0x000000ff);// blue
        return result;
    }


    //=======================================单个类型转换==============================================//

    /**
     * 将有符号byte(-128~127) -> 无符号byte(0~255)
     * @param signedByte
     * @return
     */
    public static int signedByte2unsignedByte(byte signedByte) {
        return signedByte >= 0 ? signedByte : signedByte + 256;
    }

    /**
     * 将无符号byte(0~255) -> 有符号byte(-128~127)
     * @param unsignedByte
     * @return
     */
    public static byte unsignedByte2signedByte(int unsignedByte) {
        byte byteValue=0;
        int temp = unsignedByte % 256;
        if ( unsignedByte < 0) {
            byteValue =  (byte)(temp < -128 ? 256 + temp : temp);
        }
        else {
            byteValue =  (byte)(temp > 127 ? temp - 256 : temp);
        }
        return byteValue;
//        return (byte) ((unsignedByte - 256) % 256);
//        return (byte) (unsignedByte < 128 ? unsignedByte : unsignedByte - 256);
    }


    /**
     * 2进制数组 -> 单个byte
     * @param binaryArray 长度为8的二进制数组（index0为最高位）
     * @return 返回signed byte
     */
    public static byte binary2byte(byte[] binaryArray, int offset) {
        int value = 0;
        for (int i = 0; i < 8; i++) {
            int shift = 8 - 1 - i;
            value += (binaryArray[offset + i] & 0x00000001) << shift;
        }
        // 转为signed byte
        if (binaryArray[0] == 1)
            value = -256 + value;

        return (byte) value;
    }

    /**
     * int -> byte(4)
     * @param value
     * @return signed byte
     */
    public static byte[] int2byte(int value) {
        final int intLength = 4;
        byte[] b = new byte[intLength];
        for (int i = 0; i < intLength; i++) {
            int offset = (intLength - 1 - i) * 8;
            b[i] = (byte) ((value >>> offset) & 0x000000FF);
        }
        return b;
    }

    /**
     * long -> byte(8)
     * @param value
     * @return signed byte
     */
    public static byte[] long2byte(long value) {
        final int longLength = 8;
        byte[] b = new byte[longLength];
        for (int i = 0; i < longLength; i++) {
            int offset = (longLength - 1 - i) * 8;
            b[i] = (byte) ((value >>> offset) & 0x000000FF);
        }
        return b;
    }

    /**
     * 字符串 -> byte
     * @param str
     * @param charSet 字符集名称
     * @return signed byte
     */
    public static byte[] string2byte(String str, String charSet) {
        try {
            return str.getBytes(charSet);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 单个byte -> 2进制
     * @param b
     * @return
     */
    public static byte[] byte2binary(byte b) {
        byte[] binary = new byte[8];
        for (int i = 0; i < 8; i++) {
            byte c = b;
            b = (byte) (b >> 1);//每移一位如同将10进制数除以2并去掉余数。
            b = (byte) (b << 1);
            if (b == c) {
                binary[8 - 1 -i] = 0;
            } else {
                binary[8 - 1 -i] = 1;
            }
            b = (byte) (b >> 1);
        }
        return binary;

    }

    /**
     * byte -> int
     * @param b	index0为最高位
     * @return
     */
    public static int byte2int(byte[] b, int offset) {
        final int intLength = 4;
        int value = 0;
        for (int i = 0; i < intLength; i++) {
            int shift = (intLength - 1 - i) * 8;
            int tmpB = b[offset + i] & 0x000000FF;// TIP 默认也是转化为int
            value += tmpB << shift;
        }
        return value;
    }


    /**
     * byte -> long
     * @param b index0为最高位
     * @return
     */
    public static long byte2long(byte[] b, int offset) {
        final int longLength = 8;
        long value = 0;
        for (int i = 0; i < longLength; i++) {
            int shift = (longLength - 1 - i) * 8;
            long tmpB = b[offset + i] & 0x000000FF;// TIP 一定要先转化为long，再左移。否则只能左移到第31位
            value += (tmpB << shift);
        }
        return value;
    }

    /**
     * byte -> 字符串
     * @param b
     * @param offset The index of the first byte to decode
     * @param length The number of bytes to decode
     * @param charSet 字符集名称
     * @return
     */
    public static String byte2string(byte[] b, int offset,
                                     int length, String charSet) {
        try {
            return new String(b, offset, length, charSet);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }


    //=======================================数组类型转换==============================================//

    /**
     * 将有符号byte(-128~127) -> 无符号byte(0~255)
     * @param signedByte
     * @return
     */
    public static int[] signedByteArray2unsignedByteArray(byte[] signedByte) {
        final int length = signedByte.length;
        int[] result = new int[length];
        for (int i = 0; i < length; i++) {
            result[i] = signedByte2unsignedByte(signedByte[i]);
        }
        return result;
    }

    /**
     * 将无符号byte(0~255) -> 有符号byte(-128~127)
     * @param unsignedByte
     * @return
     */
    public static byte[] unsignedByteArray2signedByteArray(int[] unsignedByte) {
        final int length = unsignedByte.length;
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = unsignedByte2signedByte(unsignedByte[i]);
        }
        return result;
    }

    /**
     * 2进制数组 -> byte数组
     * @param binaryArray
     * @return
     */
    public static byte[] binaryArray2byteArray(byte[] binaryArray, int offset,
                                               int binaryLength) {
        final int length = binaryLength / 8;
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = binary2byte(binaryArray, offset + (8 * i));
        }
        return result;
    }

    /**
     * int数组 -> byte数组
     * @param intArray
     * @return
     */
    public static byte[] intArray2byteArray(int[] intArray, int offset,
                                            int intLength) {
        byte[] result = new byte[intLength * 4];
        for (int i = 0; i < intLength; i++) {
            for (int j = 0; j < 4; j++) {
                int shift = (4 - 1 - j) * 8;
                result[i * 4 + j] = (byte) ((intArray[offset + i]
                        >>> shift) & 0x000000FF);
            }
        }
        return result;
    }

    /**
     * byte数组 -> int数组
     * @param byteArray
     * @return
     */
    public static int[] byteArray2intArray (byte[] byteArray, int offset,
                                            int byteLength) {
        final int intLength = byteLength / 4;
        int[] result = new int[intLength];
        for (int i = 0; i < intLength; i++) {
            result[i] = byte2int(byteArray, offset + (i * 4));
        }
        return result;
    }


    /**
     * byte数组 -> 2进制数组
     * @param byteArray
     * @return
     */
    public static byte[] byteArray2binaryArray(byte[] byteArray, int offset,
                                               int byteLength) {
        final int binaryLength = byteLength * 8;
        byte[] binary = new byte[binaryLength];
        for (int i = 0; i < byteLength; i++) {
            byte b = byteArray[offset + i];
            for (int j = 0; j < 8; j++) {
                byte c = b;
                b = (byte) (b >> 1);//每移一位如同将10进制数除以2并去掉余数。
                b = (byte) (b << 1);
                if (b == c) {
                    binary[8 * (i + 1) - 1 -j] = 0;
                } else {
                    binary[8 * (i + 1) - 1 -j] = 1;
                }
                b = (byte) (b >> 1);
            }
        }
        return binary;
    }


    /**
     * 1维数组转为2维数组
     * @param i1D
     * @param l
     * @return
     */
    public static int[][] _1D_to2D(int i1D[], int l) {
        int[][] i2D = new int[l][l];
        for (int i = 0; i < l; i++) {
            for (int j = 0; j < l; j++) {
                i2D[i][j] = i1D[i*l+j];
            }
        }
        return i2D;
    }

    /**
     * 2维数组转为1维数组
     * @param i2D
     * @param l
     * @return
     */
    public static int[] _2D_to1D(int i2D[][], int l) {
        int[] i1D = new int[l * l];
        for (int i = 0; i < l; i++) {
            for (int j = 0; j < l; j++) {
                i1D[i*l+j] = i2D[i][j];
            }
        }
        return i1D;
    }
}

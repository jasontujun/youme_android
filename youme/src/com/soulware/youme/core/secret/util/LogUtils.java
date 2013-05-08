package com.soulware.youme.core.secret.util;

/**
 * Created by jasontujun.
 * Date: 13-3-9
 * Time: 上午11:33
 */
public class LogUtils {    private static boolean enable = true;

    public static void setEnable(boolean enable1) {
        enable = enable1;
    }

    public static void logArray(String tag, byte[] data, int length, boolean parity) {
        if (!enable)
            return;

        if (data == null) {
            System.out.println(tag + " is null");
            return;
        }

        System.out.println();
        System.out.println(tag + ":");
        final long dataLength = data.length;
        for (int i = 0; i<dataLength; i++) {
            if ((i + 1) % length == 1) // 本行第一个，缩进
                System.out.print("      ");
            if (parity) {
                int p = data[i] & 0x01;
                System.out.print(p+ " ");// 转为奇偶
            } else {
                System.out.print(Integer.toHexString(data[i]) + "   ");// 转为16进制
            }
            if ((i + 1) % length == 0) // 本行末尾，换行
                System.out.println();
        }
    }

    public static void logArray(String tag, int[] data, int length, boolean parity) {
        if (!enable)
            return;

        if (data == null) {
            System.out.println(tag + " is null");
            return;
        }

        System.out.println();
        System.out.println(tag + ":");
        final long dataLength = data.length;
        for (int i = 0; i<dataLength; i++) {
            if ((i + 1) % length == 1) // 本行第一个，缩进
                System.out.print("      ");
            if (parity) {
                int p = data[i] & 0x01;
                System.out.print(p+ " ");// 转为奇偶
            } else {
                System.out.print(Integer.toHexString(data[i]) + "   ");// 转为16进制
            }
            if ((i + 1) % length == 0) // 本行末尾，换行
                System.out.println();
        }
    }

    public static void logArray(String tag, String[] data, int length) {
        if (!enable)
            return;

        if (data == null) {
            System.out.println(tag + " is null");
            return;
        }

        System.out.println();
        System.out.println(tag + ":");
        final long dataLength = data.length;
        for (int i = 0; i<dataLength; i++) {
            if ((i + 1) % length == 1) // 本行第一个，缩进
                System.out.print("      ");
            System.out.print(data[i] + "   ");
            if ((i + 1) % length == 0) // 本行末尾，换行
                System.out.println();
        }
    }
}

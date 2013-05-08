package com.soulware.youme.core.secret.message;

/**
 * Created by jasontujun.
 * Date: 13-2-27
 * Time: 下午2:55
 */
public interface MessageHider {

    /**
     * 获取该编码方式的id号
     * @return
     */
    int getEncodingVersion();

    /**
     * 根据原始数据大小，返回可容纳信息的大小
     * @param dataSize 原始数据大小（个位单位）
     * @return 返回可容纳信息的大小（bit为单位）
     */
    long getCapacity(int dataSize);

    /**
     * 加载信息
     * @param data 原始数据，载体
     * @param offset 起始位置
     * @param message 信息的字节码
     * @return
     */
    boolean hideMessage(int[] data, int offset, byte[] message);

    /**
     * 提取信息
     * @param pixels 原始数据，载体
     * @param offset 起始位置
     * @param messageLength 信息长度（字节为单位）
     * @return 返回信息的字节码
     */
    byte[] pickMessage(int[] pixels, int offset, int messageLength);
}

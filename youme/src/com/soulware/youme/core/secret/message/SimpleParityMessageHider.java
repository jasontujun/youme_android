package com.soulware.youme.core.secret.message;


import com.soulware.youme.core.secret.message.type.EncodingVersion;
import com.soulware.youme.core.secret.util.DataUtils;

/**
 * <pre>
 * 简单奇偶编码法.
 * 每一个int数据的值为奇数的时候，表示一个bit信息1，否则表示0；
 * 隐藏的信息按照奇偶编码法修改像素值进而编码。
 * 像素修改的注意点：1）同时应该尽量使每一个数据的值向0靠拢，比如:值为118，要修改为奇数，
 *                 应修改为117；值为-108且要修改为奇数，应修改为-107（防止溢出）
 * </pre>
 * Created by jasontujun.
 * Date: 13-2-27
 * Time: 下午3:14
 */
public class SimpleParityMessageHider implements MessageHider {

    /**
     * Singleton
     */
    private static SimpleParityMessageHider instance;

    public synchronized static SimpleParityMessageHider getInstance() {
        if (instance == null)
            instance = new SimpleParityMessageHider();
        return instance;
    }

    private SimpleParityMessageHider() {}


    @Override
    public int getEncodingVersion() {
        return EncodingVersion.SIMPLE_PARITY_VERSION;
    }

    @Override
    public long getCapacity(int dataSize) {
        return dataSize;
    }


    @Override
    public boolean hideMessage(int[] data, int offset, byte[] message) {
        if (data == null || message == null)
            return false;

        // 检查图片是否过小
        byte[] bitArray = DataUtils.byteArray2binaryArray(message, 0, message.length);
        final int infoLength = bitArray.length;
        final int pixelsAvailable = data.length - offset;
        if (pixelsAvailable < infoLength)
            return false;

        // 简单奇偶水印算法
        for (int i = 0; i < infoLength; i++) {
            int bit = bitArray[i];
            int pixel = data[i + offset];
            if (bit == 0) {// 如果此位置的bit为0，则此位置的像素值为偶数
                if ((pixel & 0x01) != 0) {// 如果像素不为偶数，则增或减1使其变为偶数
                    if (pixel < -1) {
                        pixel = pixel + 1;
                    } else {
                        pixel = pixel - 1;
                    }
                }
            } else {// 如果此位置的bit为1，则此位置的像素值为奇数
                if ((pixel & 0x01) != 1) {// 如果像素不为奇数，则增或减1使其变为奇数
                    if (pixel <= 0) {
                        pixel = pixel + 1;
                    } else {
                        pixel = pixel - 1;
                    }
                }
            }
            data[i + offset] = pixel;// 将变换后的像素写回原像素矩阵中
        }
        return true;
    }


    @Override
    public byte[] pickMessage(int[] pixels, int offset, int messageLength) {
        if (pixels == null)
            return null;

        if (messageLength <= 0)
            return null;

        // 检查图片是否过小
        System.out.println("提取信息的长度(字节数)：" + messageLength);
        final int bitLength = messageLength * 8;
        final int pixelsAvailable = pixels.length - offset;
        if (pixelsAvailable < bitLength)
            return null;

        // 根据简单奇偶编码法，提取二进制流
        byte[] bitArray = new byte[bitLength];
        for(int i = 0; i < bitLength; i++)
            bitArray[i] = (byte) (pixels[i + offset] & 0x01);

        return DataUtils.binaryArray2byteArray(bitArray, 0, bitLength);
    }
}

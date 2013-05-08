package com.soulware.youme.core.secret.message;


import com.soulware.youme.core.secret.message.type.EncodingVersion;
import com.soulware.youme.core.secret.util.DataUtils;

/**
 * <pre>
 * 多通道奇偶编码法.
 * 每一个32位int像素中（R、G、B）的值为奇数的时候，表示一个bit信息1，否则表示0；
 * B为第一层信息通道[0x000000BB]，channel = 3
 * G为第二层信息通道[0x0000GG00]，channel = 2
 * R为第三层信息通道[0x00RR0000]，channel = 1
 * A为第四层信息通道[0xAA000000]，channel = 0
 * 隐藏的信息按照奇偶编码法修改像素值进而编码。
 * 像素修改的顺序是：先将信息存入第一层信息通道，即修改图片所有像素的R值的奇偶。
 *                 当第一层通道装满时，再写入第二层信息通道；
 *                 以此类推，直到最后一层信息通道。
 * 像素修改的注意点：1）由于采用signed byte作为表示单位，对于每一个RGB的值得修改，
 *                 只偏移至多1的值（减少图片失真性）；
 *                 2）同时应该尽量使RGB的值向0靠拢，比如:R的值为118，要修改为奇数，
 *                 应修改为117；R的值为-108且要修改为奇数，应修改为-107（防止溢出）
 * </pre>
 * Created by jasontujun.
 * Date: 13-2-27
 * Time: 下午3:14
 */
public class MultiChannelParityMessageHider implements MessageHider {

    /**
     * Singleton
     */
    private static MultiChannelParityMessageHider instance;

    public synchronized static MultiChannelParityMessageHider getInstance() {
        if (instance == null)
            instance = new MultiChannelParityMessageHider();
        return instance;
    }

    private MultiChannelParityMessageHider() {}

    public static final int CHANNEL_NUMBER = 4;// 可存储信息的层数


    @Override
    public int getEncodingVersion() {
        return EncodingVersion.MULTI_CHANNEL_PARITY_VERSION;
    }

    @Override
    public long getCapacity(int dataSize) {
        long ds = dataSize;
        long result = ds * CHANNEL_NUMBER;
        return result;
    }


    @Override
    public boolean hideMessage(int[] data, int offset, byte[] message) {
        if (data == null || message == null)
            return false;

        // 检查图片是否过小
        byte[] bitArray = DataUtils.byteArray2binaryArray(message, 0, message.length);
        final int bitLength = bitArray.length;
        final int pixelsAvailable = data.length - offset;
        if (pixelsAvailable * CHANNEL_NUMBER < bitLength)
            return false;

        final int channelNeed = (bitLength / (pixelsAvailable + 1)) + 1;// 使用的层数
        // 多通道奇偶水印算法
        int curMsgBit = 0;
        for (int c = 0; c < channelNeed; c++) {
            int channel = CHANNEL_NUMBER - 1 - c;
            for (int i = 0; i < pixelsAvailable; i++) {
                if (curMsgBit >= bitLength)
                    break;

                int bit = bitArray[curMsgBit];
                int pixel = data[i + offset];
                byte[] argb = DataUtils.pixel2argb(pixel);
                if (bit == 0) {// 如果此位置的bit为0，则此位置的像素值为偶数
                    if ((argb[channel] & 0x01) != 0) {// 如果像素不为偶数，则增或减1使其变为偶数
                        if (argb[channel] < -1) {
                            argb[channel] = (byte) (argb[channel] + 1);
                        } else {
                            argb[channel] = (byte) (argb[channel] - 1);
                        }
                    }
                } else {// 如果此位置的bit为1，则此位置的像素值为奇数
                    if ((argb[channel] & 0x01) != 1) {// 如果像素不为奇数，则增或减1使其变为奇数
                        if (argb[channel] <= 0) {
                            argb[channel] = (byte) (argb[channel] + 1);
                        } else {
                            argb[channel] = (byte) (argb[channel] - 1);
                        }
                    }
                }
                data[i + offset] = DataUtils.argb2pixel(argb);// 将变换后的像素写回原像素矩阵中

                curMsgBit++;
            }
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

        final int channelNeed = (bitLength / (pixelsAvailable + 1)) + 1;// 使用的层数
        // 根据多通道奇偶编码法，提取二进制流
        byte[] bitArray = new byte[bitLength];
        int curMsgBit = 0;
        for (int c = 0; c < channelNeed; c++) {
            int channel = CHANNEL_NUMBER - 1 - c;
            for (int i = 0; i < pixelsAvailable; i++) {
                if (curMsgBit >= bitLength)
                    break;

                byte[] argb = DataUtils.pixel2argb(pixels[i + offset]);
                bitArray[curMsgBit] = (byte) (argb[channel] & 0x01);

                curMsgBit++;
            }
        }

        return DataUtils.binaryArray2byteArray(bitArray, 0, bitLength);
    }
}

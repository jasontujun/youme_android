package com.soulware.youme.core.secret.media.image;


import com.soulware.youme.core.secret.media.image.model.SecretImage;

/**
 * Created by jasontujun.
 * Date: 13-2-27
 * Time: 下午9:46
 */
public interface ImageHandler {

    static final int HEADER_LENGTH_BYTE = 40; // 单位：byte
    static final int AUTHOR_MAX_LENGTH = 16; // 单位：byte
    static final String STRING_CHAR_SET = "UTF-8"; // 单位：byte


    /**
     * 保存图片
     * @param image
     * @param type 图片格式
     * @param path
     * @return
     */
    boolean saveImage(SecretImage image, String type, String path);

    /**
     * 获取图片
     * @param path
     * @return
     */
    SecretImage loadImage(String path);

    /**
     * 获取图片像素
     * @param img
     * @return
     */
    int[] getImagePixels(SecretImage img);

    /**
     * 将像素写进图片中
     * @param srcImage 源图片（像素被设置）
     * @param pixels
     * @return
     */
    boolean setImagePixels(int[] pixels, SecretImage srcImage, String desPath);

    /**
     * 检测是否有信息隐藏。（检测EncodingVersion）
     * 通过检测数据流的最开始的4*8个数据，检测是否有信息隐藏其中。
     * @param pixels
     * @return 如果有信息隐藏，true。如果没有，则返回false
     */
    boolean isSecretHidden(int[] pixels);


    /**
     * <pre>
     * 隐藏信息进图片
     * 头部: 编码方式[4b], 时间[8b], 作者[16b], message类型[4b],
     *       message字节长度[4b], password[4b]
     * 内容: 奇偶编码法
     * </pre>
     * @param srcImagePath 图片的绝对路径
     * @param author
     * @param secretType 信息类型（eg:文字，语音）
     * @param secret
     * @param password 密码（经过MD5加密后的口令）
     * @param desImagePath
     * @return 返回SecretImage对象。如果为null，或者里面的secret为null，则表示失败
     * @see #HEADER_LENGTH_BYTE 头部信息长度
     */
    SecretImage hideSecret(String srcImagePath, String author, int secretType,
                           byte[] secret, int password, String desImagePath);

    /**
     * <pre>
     * 隐藏信息进图片
     * 头部: 编码方式[4b], 时间[8b], 作者[16b],message类型[4b],
     *       message字节长度[4b], password[4b]
     * 内容: 奇偶编码法
     * </pre>
     * @param srcImage
     * @param author
     * @param secretType 信息类型（eg:文字，语音）
     * @param secret
     * @param password 密码（经过MD5加密后的口令）
     * @param desImagePath
     * @return
     * @see #HEADER_LENGTH_BYTE 头部信息长度
     */
    boolean hideSecret(SecretImage srcImage, String author, int secretType,
                       byte[] secret, int password, String desImagePath);

    /**
     * 提取信息
     * @param srcImagePath 图片的绝对路径
     * @return 返回SecretImage对象。如果为null，或者里面的secret为null，则表示失败
     */
    SecretImage pickSecret(String srcImagePath);


    /**
     * 提取信息
     * @param srcImage
     * @return
     */
    boolean pickSecret(SecretImage srcImage);
}

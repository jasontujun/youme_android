package com.soulware.youme.core.secret.media.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import com.soulware.youme.core.secret.media.image.jni.ImageHandlerJNI;
import com.soulware.youme.core.secret.media.image.model.SecretImage;
import com.soulware.youme.core.secret.message.MessageHider;
import com.soulware.youme.core.secret.message.MultiChannelParityMessageHider;
import com.soulware.youme.core.secret.message.type.EncodingVersion;
import com.soulware.youme.core.secret.util.DataUtils;
import com.xengine.android.utils.XLog;
import com.xengine.android.utils.XStringUtil;

import java.io.*;

/**
 * Created by jasontujun.
 * Date: 13-2-27
 * Time: 下午9:48
 */
public class ImageHandlerAndroidImpl implements ImageHandler {

    /**
     * Singleton
     */
    private static ImageHandlerAndroidImpl instance;

    public synchronized static ImageHandlerAndroidImpl getInstance() {
        if (instance == null)
            instance = new ImageHandlerAndroidImpl();
        return instance;
    }


    private static final String TAG = ImageHandlerAndroidImpl.class.getSimpleName();
    private MessageHider mMessageHider;
    private ImageHandlerJNI mImageHandlerJNI;

    private ImageHandlerAndroidImpl() {
//        mMessageHider = SimpleParityMessageHider.getInstance();
        mMessageHider = MultiChannelParityMessageHider.getInstance();
        mImageHandlerJNI = ImageHandlerJNI.getInstance();
    }


    @Override
    public boolean saveImage(SecretImage image, String type, String path) {
        try {
            XLog.d(TAG, "saveImage type:" + type);
            XLog.d(TAG, "saveImage path:" + path);
            File desFile = new File(path);
            int separatorIndex = desFile.getAbsolutePath().lastIndexOf(File.separator);
            if (separatorIndex != -1) {
                String dirPath = path.substring(0, separatorIndex);
                File dirFile = new File(dirPath);
                if (!dirFile.exists()) {
                    dirFile.mkdirs();
                }
            }
            // 查看目标文件是否会覆盖已有文件，如果有则重命名目标文件
            for (int i = 2; desFile.exists(); i++) {
                int j = path.lastIndexOf(".");
                String newPath = path.substring(0, j) + "(" + i + ")" + path.substring(j);
                desFile = new File(newPath);
            }
            if (!desFile.exists()) {
                desFile.createNewFile();
            }
            Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
            if (type.toLowerCase().equals("png"))
                format = Bitmap.CompressFormat.PNG;
            FileOutputStream fos = new FileOutputStream(desFile);
            image.getDisplayImage().compress(format, 100, fos);
            fos.flush();
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }



    @Override
    public SecretImage loadImage(String path) {
        try {
            Bitmap srcImg = null;
            Bitmap displayImg = null;
            File imgFile = new File(path);
            if(imgFile.exists()) {
                // 加载原始的图像（不会在加载的时候压缩）
                InputStream is = new FileInputStream(imgFile);
                Rect outPadding = new Rect(-1, -1, -1, -1);// 完整的图像
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inScaled = false;
                opts.inDensity = 160;
                opts.inTargetDensity = 160;
                opts.inScreenDensity = 160;
                srcImg = BitmapFactory.decodeStream(is, outPadding, opts);
                is.close();
                // 加载用于显示的图像（缩放后的）
                InputStream is2 = new FileInputStream(imgFile);
                displayImg = BitmapFactory.decodeStream(is2);
                is2.close();
            } else {
                XLog.d(TAG, "图片不存在！" + imgFile);
            }
            if (srcImg == null)
                return null;
            if (displayImg == null)
                return null;

            SecretImage result = new SecretImage();
            result.setWidth(srcImg.getWidth());
            result.setHeight(srcImg.getHeight());
            result.setDisplayImage(displayImg);
            result.setSrcPath(path);

            XLog.d(TAG, "TYPE_ALPHA_8:" + Bitmap.Config.ALPHA_8.ordinal());
            XLog.d(TAG, "TYPE_ARGB_4444:" + Bitmap.Config.ARGB_4444.ordinal());
            XLog.d(TAG, "TYPE_ARGB_8888:" + Bitmap.Config.ARGB_8888.ordinal());
            XLog.d(TAG, "TYPE_RGB_565:" + Bitmap.Config.RGB_565.ordinal());
            XLog.d(TAG, "loadImage: imageType = " + srcImg.getConfig().ordinal()
                    + ",hasAlpha=" + srcImg.hasAlpha());
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public int[] getImagePixels(SecretImage sImage) {
        if (sImage == null || XStringUtil.isNullOrEmpty(sImage.getSrcPath()))
            return null;

        return mImageHandlerJNI.getPixelsFromImage(sImage.getSrcPath());
    }

    @Override
    public boolean setImagePixels(int[] pixels, SecretImage srcImage, String desPath) {
        if (pixels == null || srcImage == null ||
                XStringUtil.isNullOrEmpty(srcImage.getSrcPath()) ||
                XStringUtil.isNullOrEmpty(desPath))
            return false;

        return mImageHandlerJNI.setPixelsToImage(srcImage.getSrcPath(), desPath, pixels);
    }

    @Override
    public boolean isSecretHidden(int[] pixels) {
        if (pixels == null)
            return false;

        // 检查图片是否过小
        if (mMessageHider.getCapacity(pixels.length) < HEADER_LENGTH_BYTE * 8)
            return false;

        // 提取前4b的encodingVersion信息进行验证
        byte[] byteEV = mMessageHider.pickMessage(pixels, 0, 4);
        if (byteEV == null)
            return false;
        int encodingVersion = DataUtils.byte2int(byteEV, 0);
        return EncodingVersion.verify(encodingVersion);
    }


    @Override
    public SecretImage hideSecret(String srcImagePath, String author, int secretType,
                                  byte[] secret, int password, String desImagePath) {
        SecretImage secretImage = loadImage(srcImagePath);
        hideSecret(secretImage, author, secretType, secret, password, desImagePath);
        return secretImage;
    }

    @Override
    public boolean hideSecret(SecretImage srcImage, String author, int secretType,
                              byte[] secret, int password, String desImagePath) {
        // null验证
        if (srcImage == null || secret == null)
            return false;

        // 作者名字过长
        if (author.getBytes().length > AUTHOR_MAX_LENGTH)
            return false;

        // 获取像素矩阵
        int[] pixels = getImagePixels(srcImage);
        if (pixels == null)
            return false;

        // 容量验证
        final int messageBitLength = secret.length * 8;
        final int headerBitLength = HEADER_LENGTH_BYTE * 8;
        final int messageTotalBitLength = headerBitLength + messageBitLength;
        if (mMessageHider.getCapacity(pixels.length) < messageTotalBitLength)
            return false;

        // 写入头部信息
        byte[] versionByte = DataUtils.int2byte(EncodingVersion.SIMPLE_PARITY_VERSION);
        final long createdTime = System.currentTimeMillis();
        byte[] timeByte = DataUtils.long2byte(createdTime);
        byte[] authorByte = DataUtils.string2byte(author, STRING_CHAR_SET);
        byte[] messageTypeByte = DataUtils.int2byte(secretType);
        byte[] messageLengthByte = DataUtils.int2byte(secret.length);
        byte[] passwordByte = DataUtils.int2byte(password);
        mMessageHider.hideMessage(pixels, 0, versionByte);
        mMessageHider.hideMessage(pixels, 4 * 8, timeByte);
        mMessageHider.hideMessage(pixels, 12 * 8, authorByte);
        mMessageHider.hideMessage(pixels, 28 * 8, messageTypeByte);
        mMessageHider.hideMessage(pixels, 32 * 8, messageLengthByte);
        mMessageHider.hideMessage(pixels, 36 * 8, passwordByte);

        // 写入内容信息
        if (!mMessageHider.hideMessage(pixels, headerBitLength, secret))
            return false;

        // 设置新的图片像素,并保存
        if (!setImagePixels(pixels, srcImage, desImagePath))
            return false;

        srcImage.setVersionCode(mMessageHider.getEncodingVersion());
        srcImage.setTime(createdTime);
        srcImage.setAuthor(author);
        srcImage.setSecret(secret);
        srcImage.setSecretType(secretType);
        srcImage.setPassword(password);

        return true;
    }


    @Override
    public SecretImage pickSecret(String srcImagePath) {
        SecretImage secretImage = loadImage(srcImagePath);
        pickSecret(secretImage);
        return secretImage;
    }

    @Override
    public boolean pickSecret(SecretImage srcImage) {
        // null验证
        if (srcImage == null)
            return false;

        // 如果已经解开并存储过的，则直接返回
        if (srcImage.getSecret() != null)
            return false;

        // 获取像素矩阵
        int[] pixels = getImagePixels(srcImage);
        if (pixels == null)
            return false;

        // 检查图片是否过小
        if (mMessageHider.getCapacity(pixels.length)  < HEADER_LENGTH_BYTE * 8)
            return false;

        // 检测是否被隐藏过信息
        if (!isSecretHidden(pixels))
            return false;
        srcImage.setVersionCode(mMessageHider.getEncodingVersion());

        // 提取头部信息[40B]
        byte[] header = mMessageHider.pickMessage(pixels, 0, HEADER_LENGTH_BYTE);
        if (header == null)
            return false;
        final int versionLength = 4;
        final int timeLength = 8;
        final int authorLength = 16;
        final int messageTypeLength = 4;
        final int messageLength = 4;
        final int passwordLength = 4;
        long time = DataUtils.byte2long(header, versionLength);
        String author = DataUtils.byte2string(header,
                versionLength + timeLength, authorLength, "UTF-8");
        int messageType = DataUtils.byte2int(header,
                versionLength + timeLength + authorLength);
        int messageL = DataUtils.byte2int(header,
                versionLength + timeLength + authorLength + messageTypeLength);
        XLog.d(TAG, "pick secret: message length=" + messageL);
        int password = DataUtils.byte2int(header,
                versionLength + timeLength + authorLength + messageTypeLength + messageLength);
        srcImage.setTime(time);
        srcImage.setAuthor(author);
        srcImage.setSecretType(messageType);
        srcImage.setPassword(password);

        // 再提取内容信息
        byte[] secret = mMessageHider.pickMessage(pixels,
                HEADER_LENGTH_BYTE * 8, messageL);
        if (secret == null)
            return false;
        srcImage.setSecret(secret);

        return true;
    }

}

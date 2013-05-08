package com.soulware.youme.core.secret.media.image.jni;

/**
 * Created by jasontujun.
 * Date: 13-4-16
 * Time: 下午8:04
 */
public class ImageHandlerJNI {

    static {
        System.loadLibrary("ymImage");
    }

    public native int[] getPixelsFromImage(String imagePath);

    public native boolean setPixelsToImage(String srcImagePath,
                                           String desImagePath, int[] pixels);


    private static ImageHandlerJNI instance;

    private ImageHandlerJNI(){}

    public synchronized static ImageHandlerJNI getInstance() {
        if (instance == null)
            instance = new ImageHandlerJNI();
        return instance;
    }
}

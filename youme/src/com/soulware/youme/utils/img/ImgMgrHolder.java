package com.soulware.youme.utils.img;
import com.xengine.android.media.image.XSerialDownloadMgr;

/**
 * 持有一个图片下载管理器对象和一个线性下载对象
 * Created by jasontujun.
 * Date: 12-11-1
 * Time: 下午10:54
 */
public class ImgMgrHolder {

    private static XSerialDownloadMgr serialDownloadMgrInstance;

    private static ImageDownloadMgr imageDownloadMgrInstance;


    private static int screenWidth;

    private static int screenHeight;

    public static void init(int sWidth, int sHeight) {
        screenWidth = sWidth;
        screenHeight = sHeight;
    }

    public static synchronized XSerialDownloadMgr getSerialDownloadMgr() {
        if(serialDownloadMgrInstance == null) {
            serialDownloadMgrInstance = new XSerialDownloadMgr(getImgDownloadMgr());
        }
        return serialDownloadMgrInstance;
    }

    public static synchronized ImageDownloadMgr getImgDownloadMgr() {
        if(serialDownloadMgrInstance == null) {
            imageDownloadMgrInstance = new ImageDownloadMgr(screenWidth, screenHeight);
        }
        return imageDownloadMgrInstance;
    }
}

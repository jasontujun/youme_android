package com.soulware.youme.logic;

import com.soulware.youme.core.secret.media.image.ImageHandler;
import com.soulware.youme.core.secret.media.image.ImageHandlerAndroidImpl;
import com.soulware.youme.core.secret.media.image.model.SecretImage;
import com.soulware.youme.core.secret.message.type.MessageType;

/**
 * Created with IntelliJ IDEA.
 * User: jasontujun
 * Date: 13-5-23
 * Time: 下午3:54
 */
public class SecretMgr {
    private static SecretMgr instance;

    public synchronized static SecretMgr getInstance() {
        if (instance == null)
            instance = new SecretMgr();
        return instance;
    }

    private ImageHandler mImageHandler;

    private SecretMgr() {
        // 初始化图片处理器
        mImageHandler = ImageHandlerAndroidImpl.getInstance();
    }

    public boolean hideSecret(String srcPath, byte[] message, String desPath) {
        SecretImage oldPic = mImageHandler.loadImage(srcPath);
        return mImageHandler.hideSecret(oldPic, "jasontujun", MessageType.SOUND,
                message, 0, desPath);
    }
}

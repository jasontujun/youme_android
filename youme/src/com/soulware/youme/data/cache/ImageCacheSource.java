package com.soulware.youme.data.cache;

import com.xengine.android.data.cache.XDataSource;
import com.xengine.android.utils.XStringUtil;

import java.util.HashMap;

/**
 * Created by jasontujun.
 * Date: 12-10-5
 * Time: 下午8:44
 */
public class ImageCacheSource implements XDataSource {

    /**
     * 映射 <imageUrl网络地址, localImageFile本地缓存的文件名>
     */
    private HashMap<String, String> images;

    public ImageCacheSource() {
        images = new HashMap<String, String>();
    }

    @Override
    public String getSourceName() {
        return SourceName.IMAGE_CHACHE;
    }

    public boolean containsLocalImage(String imageUrl) {
        if (XStringUtil.isNullOrEmpty(imageUrl)) {
            return false;
        }

        if (!images.containsKey(imageUrl)) {
            return false;
        }

        String localImageFile = images.get(imageUrl);
        return XStringUtil.isNullOrEmpty(localImageFile);
    }

    public String getLocalImage(String imageUrl) {
        return images.get(imageUrl);
    }


    public void remove(String imageUrl) {
        images.remove(imageUrl);
    }

    public void putImage(String imageUrl, String localImageFile) {
        images.put(imageUrl, localImageFile);
    }
}

package com.soulware.youme.data.cache;

import com.soulware.youme.data.db.ImageTable;
import com.soulware.youme.data.model.Image;
import com.xengine.android.data.cache.XBaseAdapterDBDataSource;
import com.xengine.android.data.db.XDBTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jasontujun
 * Date: 13-5-27
 * Time: 下午3:27
 */
public class ImageSource extends XBaseAdapterDBDataSource<Image> {
    private XDBTable<Image> mTable = new ImageTable();

    @Override
    public String getSourceName() {
        return SourceName.IMAGE;
    }

    @Override
    public XDBTable<Image> getDatabaseTable() {
        return mTable;
    }

    public List<Image> getByStoryId(String storyId) {
        List<Image> result = new ArrayList<Image>();
        for (int i = 0; i < size(); i++) {
            Image image = get(i);
            if (storyId.equals(image.getStoryId()))
                result.add(image);
        }
        return result;
    }
}

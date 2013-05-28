package com.soulware.youme.data.cache;

import com.soulware.youme.data.db.StoryTable;
import com.soulware.youme.data.model.Story;
import com.xengine.android.data.cache.XBaseAdapterIdDBDataSource;
import com.xengine.android.data.db.XDBTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jasontujun
 * Date: 13-5-26
 * Time: 下午2:02
 */
public class StorySource extends XBaseAdapterIdDBDataSource<Story> {
    private XDBTable<Story> mTable = new StoryTable();

    @Override
    public String getSourceName() {
        return SourceName.STORY;
    }

    @Override
    public XDBTable<Story> getDatabaseTable() {
        return mTable;
    }

    public List<Story> getByTime(long startTime, long endTime) {
        List<Story> result = new ArrayList<Story>();
        for (int i = 0; i<size(); i++) {
            Story s = get(i);
            if (startTime <= s.getStoryTime() && s.getStoryTime() < endTime)
                result.add(s);
        }
        Collections.sort(result, storyComparator);
        return result;
    }

    @Override
    public String getId(Story story) {
        return story.getId();
    }

    private Comparator<Story> storyComparator = new Comparator<Story>() {
        @Override
        public int compare(Story story1, Story story2) {
            // 时间越后的前面显示
            if (story2.getStoryTime() < story1.getStoryTime())
                return -1;
            else if (story2.getStoryTime() == story1.getStoryTime())
                return 0;
            else
                return 1;
        }
    };
}

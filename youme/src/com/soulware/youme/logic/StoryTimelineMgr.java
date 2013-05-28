package com.soulware.youme.logic;

import com.soulware.youme.data.cache.*;
import com.soulware.youme.data.model.Image;
import com.soulware.youme.data.model.Story;
import com.soulware.youme.data.model.User;
import com.xengine.android.utils.XLog;

/**
 * Created with IntelliJ IDEA.
 * User: jasontujun
 * Date: 13-5-23
 * Time: 下午3:54
 */
public class StoryTimelineMgr {

    private static StoryTimelineMgr instance;

    public synchronized static StoryTimelineMgr getInstance() {
        if (instance == null)
            instance = new StoryTimelineMgr();
        return instance;
    }

    private GlobalStateSource mGlobalSource;
    private UserSource mUserSource;
    private StorySource mStorySource;
    private ImageSource mImageSource;
    private ImageCacheSource mImageCacheSource;

    private StoryTimelineMgr() {
        DataRepo repo = DataRepo.getInstance();
        mImageCacheSource = (ImageCacheSource) repo.getSource(SourceName.IMAGE_CHACHE);
        mGlobalSource = (GlobalStateSource) repo.getSource(SourceName.GLOBAL_STATE);
        mUserSource = (UserSource) repo.getSource(SourceName.USER);
        mStorySource = (StorySource) repo.getSource(SourceName.STORY);
        mImageSource = (ImageSource) repo.getSource(SourceName.IMAGE);
    }

    public String addStory(String storyName, long storyTime) {
        String username = mGlobalSource.getCurrentUserName();
        User user = mUserSource.getByUserName(username);
        String storyId = "u" + ((int) Math.random() * 96 + 100);
        Story story = new Story(storyId, user.getId(), username,
                user.getId(), username, storyName, storyTime, null);
        mStorySource.add(story);
        return storyId;
    }

    public String addImage(String storyId, String imagePath) {
        if (mStorySource.getById(storyId) == null)
            return null;

        long currentTime = System.currentTimeMillis();
        String imageId = "i" + currentTime;
        XLog.d("FK", "new imageid:" + imageId);
        Image image = new Image(imageId, storyId, currentTime, null,
                imagePath, null, false, 0);
        mImageCacheSource.putImage(imageId, imagePath);
        mImageSource.add(image);
        return imageId;
    }
}

package com.soulware.youme.data.model;

import com.xengine.android.utils.XStringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jasontujun
 * Date: 13-5-24
 * Time: 下午2:45
 */
public class Image {
    private String id;
    private String storyId;
    private long imageTime;
    private List<String> friendTags;

    // 本地数据
    private String localImagePath;// 本地图片的存放路径
    private String localSpeechPath;// 语音文件的存放路径
    private boolean hasSecret;
    private int secretSize;// 语音长度

    public Image() {
    }

    public Image(String id, String storyId, long imageTime, List<String> friendTags,
                 String localImagePath, String localSpeechPath, boolean hasSecret, int secretSize) {
        this.id = id;
        this.storyId = storyId;
        this.imageTime = imageTime;
        this.friendTags = friendTags;
        this.localImagePath = localImagePath;
        this.localSpeechPath = localSpeechPath;
        this.hasSecret = hasSecret;
        this.secretSize = secretSize;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public long getImageTime() {
        return imageTime;
    }

    public void setImageTime(long imageTime) {
        this.imageTime = imageTime;
    }

    public boolean isHasSecret() {
        return hasSecret;
    }

    public void setHasSecret(boolean hasSecret) {
        this.hasSecret = hasSecret;
    }

    public int getSecretSize() {
        return secretSize;
    }

    public void setSecretSize(int secretSize) {
        this.secretSize = secretSize;
    }

    public List<String> getFriendTags() {
        return friendTags;
    }

    public void setFriendTags(List<String> friendTags) {
        this.friendTags = friendTags;
    }

    public String getLocalImagePath() {
        return localImagePath;
    }

    public void setLocalImagePath(String localImagePath) {
        this.localImagePath = localImagePath;
    }

    public String getLocalSpeechPath() {
        return localSpeechPath;
    }

    public void setLocalSpeechPath(String localSpeechPath) {
        this.localSpeechPath = localSpeechPath;
    }

    // 适配数据库存储需要
    public String getFriendTagsToString() {
        if (friendTags == null || friendTags.size() == 0)
            return "";

        StringBuilder sb = new StringBuilder(friendTags.get(0));
        for (int i = 0; i < friendTags.size() - 1; i++)
            sb.append(friendTags.get(i)).append("-");
        sb.append(friendTags.get(friendTags.size() - 1));
        return sb.toString();
    }

    public void setFriendTagsFromString(String friendStrings) {
        this.friendTags = new ArrayList<String>();
        if (XStringUtil.isNullOrEmpty(friendStrings))
            return;

        String[] friends = friendStrings.split("-");
        for (int i = 0; i<friends.length; i++)
            this.friendTags.add(friends[i]);
    }
}

package com.soulware.youme.data.model;

/**
 * Created with IntelliJ IDEA.
 * User: jasontujun
 * Date: 13-5-24
 * Time: 下午2:44
 */
public class Story {
    private String id;
    private String ownerId;// 拥有者
    private String ownerName;
    private String authorId;// 作者
    private String authorName;
    private String name;
    private long storyTime;// 故事发生时间
    private boolean isGift;
    private boolean isOpen;
    private long presentTime;// 赠送时间
    private String coverImageId;// 封面图片Id

    public Story() {
    }

    public Story(String id, String ownerId, String ownerName, String authorId,
                 String authorName, String name, long storyTime, String coverImageId) {
        this.id = id;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.authorId = authorId;
        this.authorName = authorName;
        this.name = name;
        this.storyTime = storyTime;
        this.coverImageId = coverImageId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getStoryTime() {
        return storyTime;
    }

    public void setStoryTime(long storyTime) {
        this.storyTime = storyTime;
    }

    public boolean isGift() {
        return isGift;
    }

    public void setGift(boolean gift) {
        isGift = gift;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public long getPresentTime() {
        return presentTime;
    }

    public void setPresentTime(long presentTime) {
        this.presentTime = presentTime;
    }

    public String getCoverImageId() {
        return coverImageId;
    }

    public void setCoverImageId(String coverImageId) {
        this.coverImageId = coverImageId;
    }
}

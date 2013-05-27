package com.soulware.youme.data.db;

import android.content.ContentValues;
import android.database.Cursor;
import com.soulware.youme.data.model.Story;
import com.xengine.android.data.db.XBaseDBTable;
import com.xengine.android.data.db.XSQLiteDataType;

/**
 * Created with IntelliJ IDEA.
 * User: jasontujun
 * Date: 13-5-24
 * Time: 下午2:44
 */
public class StoryTable extends XBaseDBTable<Story> {
    @Override
    public void initiateColumns() {
        addColumn("story_id", XSQLiteDataType.TEXT, null);
        addColumn("owner_id", XSQLiteDataType.TEXT, null);
        addColumn("owner_name", XSQLiteDataType.TEXT, null);
        addColumn("author_id", XSQLiteDataType.TEXT, null);
        addColumn("author_name", XSQLiteDataType.TEXT, null);
        addColumn("story_name", XSQLiteDataType.TEXT, null);
        addColumn("story_time", XSQLiteDataType.LONG, null);
        addColumn("is_gift", XSQLiteDataType.INTEGER, null);
        addColumn("is_open", XSQLiteDataType.INTEGER, null);
        addColumn("present_time", XSQLiteDataType.LONG, null);
        addColumn("cover_image_id", XSQLiteDataType.LONG, null);
    }

    @Override
    public String getName() {
        return "story_table";
    }

    @Override
    public ContentValues getContentValues(Story instance) {
        ContentValues values = new ContentValues();
        values.put("story_id", instance.getId());
        values.put("owner_id", instance.getOwnerId());
        values.put("owner_name", instance.getOwnerName());
        values.put("author_id", instance.getAuthorId());
        values.put("author_name", instance.getAuthorName());
        values.put("story_name", instance.getName());
        values.put("story_time", instance.getStoryTime());
        values.put("is_gift",  instance.isGift() ? 1 : 0);
        values.put("is_open", instance.isOpen() ? 1 : 0);
        values.put("present_time", instance.getPresentTime());
        values.put("cover_image_id", instance.getCoverImageId());
        return values;
    }

    @Override
    public Story getFilledInstance(Cursor cursor) {
        Story instance = new Story();
        instance.setId(cursor.getString(cursor.getColumnIndex("story_id")));
        instance.setOwnerId(cursor.getString(cursor.getColumnIndex("owner_id")));
        instance.setOwnerName(cursor.getString(cursor.getColumnIndex("owner_name")));
        instance.setAuthorId(cursor.getString(cursor.getColumnIndex("author_id")));
        instance.setAuthorName(cursor.getString(cursor.getColumnIndex("author_name")));
        instance.setName(cursor.getString(cursor.getColumnIndex("story_name")));
        instance.setStoryTime(cursor.getLong(cursor.getColumnIndex("story_time")));
        instance.setGift(cursor.getInt(cursor.getColumnIndex("is_gift")) == 1);
        instance.setOpen(cursor.getInt(cursor.getColumnIndex("is_open")) == 1);
        instance.setPresentTime(cursor.getLong(cursor.getColumnIndex("present_time")));
        instance.setCoverImageId(cursor.getString(cursor.getColumnIndex("cover_image_id")));
        return instance;
    }
}

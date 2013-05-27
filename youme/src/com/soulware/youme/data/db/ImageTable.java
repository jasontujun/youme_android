package com.soulware.youme.data.db;

import android.content.ContentValues;
import android.database.Cursor;
import com.soulware.youme.data.model.Image;
import com.xengine.android.data.db.XBaseDBTable;
import com.xengine.android.data.db.XSQLiteDataType;

/**
 * Created with IntelliJ IDEA.
 * User: jasontujun
 * Date: 13-5-24
 * Time: 下午2:44
 */
public class ImageTable extends XBaseDBTable<Image> {
    @Override
    public void initiateColumns() {
        addColumn("image_id", XSQLiteDataType.TEXT, null);
        addColumn("story_id", XSQLiteDataType.TEXT, null);
        addColumn("local_image_path", XSQLiteDataType.TEXT, null);
        addColumn("local_speech_path", XSQLiteDataType.TEXT, null);
        addColumn("image_time", XSQLiteDataType.LONG, null);
        addColumn("has_secret", XSQLiteDataType.INTEGER, null);
        addColumn("secret_size", XSQLiteDataType.INTEGER, null);
        addColumn("friend_tags", XSQLiteDataType.TEXT, null);
    }

    @Override
    public String getName() {
        return "image_table";
    }

    @Override
    public ContentValues getContentValues(Image instance) {
        ContentValues values = new ContentValues();
        values.put("image_id", instance.getId());
        values.put("story_id", instance.getStoryId());
        values.put("local_image_path", instance.getLocalImagePath());
        values.put("local_speech_path", instance.getLocalSpeechPath());
        values.put("image_time", instance.getImageTime());
        values.put("has_secret", instance.isHasSecret() ? 1 : 0);
        values.put("secret_size", instance.getSecretSize());
        values.put("friend_tags", instance.getFriendTagsToString());
        return values;
    }

    @Override
    public Image getFilledInstance(Cursor cursor) {
        Image instance = new Image();
        instance.setId(cursor.getString(cursor.getColumnIndex("image_id")));
        instance.setStoryId(cursor.getString(cursor.getColumnIndex("story_id")));
        instance.setLocalImagePath(cursor.getString(cursor.getColumnIndex("local_image_path")));
        instance.setLocalImagePath(cursor.getString(cursor.getColumnIndex("local_speech_path")));
        instance.setImageTime(cursor.getLong(cursor.getColumnIndex("image_time")));
        instance.setHasSecret(cursor.getInt(cursor.getColumnIndex("has_secret")) == 1);
        instance.setSecretSize(cursor.getInt(cursor.getColumnIndex("secret_size")));
        instance.setFriendTagsFromString(cursor.getString(cursor.getColumnIndex("friend_tags")));
        return instance;
    }
}

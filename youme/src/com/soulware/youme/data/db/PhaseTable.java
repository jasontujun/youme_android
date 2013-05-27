package com.soulware.youme.data.db;

import android.content.ContentValues;
import android.database.Cursor;
import com.soulware.youme.data.model.Phase;
import com.xengine.android.data.db.XBaseDBTable;
import com.xengine.android.data.db.XSQLiteDataType;

/**
 * Created with IntelliJ IDEA.
 * User: jasontujun
 * Date: 13-5-26
 * Time: 下午1:12
 */
public class PhaseTable extends XBaseDBTable<Phase> {
    @Override
    public void initiateColumns() {
        addColumn("phase_id", XSQLiteDataType.TEXT, null);
        addColumn("user_id", XSQLiteDataType.TEXT, null);
        addColumn("phase_name", XSQLiteDataType.TEXT, null);
        addColumn("start_time", XSQLiteDataType.LONG, null);
        addColumn("end_time", XSQLiteDataType.LONG, null);
    }

    @Override
    public String getName() {
        return "phase_table";
    }

    @Override
    public ContentValues getContentValues(Phase instance) {
        ContentValues values = new ContentValues();
        values.put("phase_id", instance.getId());
        values.put("user_id", instance.getUserId());
        values.put("phase_name", instance.getName());
        values.put("start_time", instance.getStartTime());
        values.put("end_time", instance.getEndTime());
        return values;
    }

    @Override
    public Phase getFilledInstance(Cursor cursor) {
        Phase instance = new Phase();
        instance.setId(cursor.getString(cursor.getColumnIndex("phase_id")));
        instance.setUserId(cursor.getString(cursor.getColumnIndex("user_id")));
        instance.setName(cursor.getString(cursor.getColumnIndex("phase_name")));
        instance.setStartTime(cursor.getLong(cursor.getColumnIndex("start_time")));
        instance.setEndTime(cursor.getLong(cursor.getColumnIndex("end_time")));
        return instance;
    }
}

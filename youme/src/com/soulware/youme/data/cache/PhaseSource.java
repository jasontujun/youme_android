package com.soulware.youme.data.cache;

import com.soulware.youme.data.db.PhaseTable;
import com.soulware.youme.data.model.Phase;
import com.xengine.android.data.cache.XBaseAdapterDBDataSource;
import com.xengine.android.data.db.XDBTable;

/**
 * Created with IntelliJ IDEA.
 * User: jasontujun
 * Date: 13-5-26
 * Time: 下午1:07
 */
public class PhaseSource extends XBaseAdapterDBDataSource<Phase> {
    private XDBTable<Phase> mTable = new PhaseTable();

    @Override
    public String getSourceName() {
        return SourceName.PHASE;
    }

    @Override
    public XDBTable<Phase> getDatabaseTable() {
        return mTable;
    }
}

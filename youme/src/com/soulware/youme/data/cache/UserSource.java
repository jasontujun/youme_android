package com.soulware.youme.data.cache;

import com.soulware.youme.data.model.User;
import com.xengine.android.data.cache.XBaseAdapterIdDataSource;

/**
 * Created with IntelliJ IDEA.
 * User: jasontujun
 * Date: 13-5-28
 * Time: 下午7:05
 */
public class UserSource extends XBaseAdapterIdDataSource<User> {
    @Override
    public String getSourceName() {
        return SourceName.USER;
    }

    @Override
    public String getId(User user) {
        return user.getId();
    }

    public User getByUserName(String username) {
        for (int i = 0; i<size(); i++) {
            if (get(i).getUsername().equals(username))
                return get(i);
        }
        return null;
    }
}

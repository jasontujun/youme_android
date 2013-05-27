package com.soulware.youme.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import com.xengine.android.system.ui.XBackType;
import com.xengine.android.system.ui.XBaseFrame;
import com.xengine.android.system.ui.XUILayer;
import com.xengine.android.utils.XLog;

/**
 * Created with IntelliJ IDEA.
 * User: jasontujun
 * Date: 13-5-27
 * Time: 下午12:26
 */
public class FStory extends XBaseFrame {
    private static final String TAG = FStory.class.getSimpleName();

    private LStoryDetail mStoryDetailLayer;

    private String storyId;

    @Override
    public boolean isFullScreen() {
        return true;
    }

    @Override
    public boolean isBackKeyDisabled() {
        XLog.d("BACK", "点击back按钮！");

        XUILayer layer = getTopLayer();
        if (layer != null) {
            int result = layer.back();// 先调用顶部图层的back()函数
            if (result == XBackType.SELF_BACK) {
                removeLayer(layer);// 退出这一图层
            } else if (result == XBackType.NOTHING_TO_BACK) {
                back();// 没有可以退出的。调用自身的back()函数
            }
        } else {
            back();// 再调用自身的back()函数
        }
        return true;
    }

    /**
     * 两次back键退出程序
     * @return
     */
    @Override
    public int back() {
        exit();
        return XBackType.SELF_BACK;
    }

    @Override
    public boolean isKeyMenuDisable() {
        XUILayer layer = getTopLayer();
        if (layer != null) {
            return layer.onMenu();
        }
        return true;
    }

    @Override
    public void preInit(Context context) {
        // 获取storyId
        storyId = getIntent().getStringExtra("storyId");
        XLog.d(TAG, "storyId:" + storyId);
    }

    @Override
    public void init(Context context) {
        if (mStoryDetailLayer == null)
            mStoryDetailLayer = new LStoryDetail(this, storyId);
        addLayer(mStoryDetailLayer);
    }

    @Override
    public String getName() {
        return "FStory";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Handler getFrameHandler() {
        return mFrameHandler;
    }

    public static final int BACK = 1;
    private Handler mFrameHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BACK:
                    isBackKeyDisabled();
                    break;
                default:
                    break;
            }
        }
    };
}

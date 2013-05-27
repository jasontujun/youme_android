package com.soulware.youme.ui;

import android.os.Handler;
import android.view.View;
import android.widget.Button;
import com.soulware.youme.R;
import com.soulware.youme.ui.controls.XHorizontalListView;
import com.xengine.android.system.ui.XBackType;
import com.xengine.android.system.ui.XBaseLayer;
import com.xengine.android.system.ui.XUIFrame;

/**
 * Created with IntelliJ IDEA.
 * User: jasontujun
 * Date: 13-5-24
 * Time: 下午2:48
 */
public class LStoryDetail extends XBaseLayer {

    private Button mBackBtn;
    private XHorizontalListView mImageListView;
    private AStory mStoryAdapter;

    private String storyId;

    public LStoryDetail(XUIFrame uiFrame, String storyId) {
        super(uiFrame);
        this.storyId = storyId;

        setContentView(R.layout.story_detail);
        mBackBtn = (Button) findViewById(R.id.top_btn);
        mImageListView = (XHorizontalListView) findViewById(R.id.image_list);

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFrameHandler().sendEmptyMessage(FStory.BACK);
            }
        });
        mStoryAdapter = new AStory(getContext(), storyId);
        mImageListView.setAdapter(mStoryAdapter);
    }

    @Override
    public Handler getLayerHandler() {
        return null;
    }

    @Override
    public int back() {
        return XBackType.NOTHING_TO_BACK;
    }
}

package com.soulware.youme.ui;

import android.os.Handler;
import android.view.View;
import android.widget.Button;
import com.soulware.youme.R;
import com.soulware.youme.ui.controls.viewflow.TitleFlowIndicator;
import com.soulware.youme.ui.controls.viewflow.ViewFlow;
import com.xengine.android.system.ui.XBackType;
import com.xengine.android.system.ui.XBaseLayer;
import com.xengine.android.system.ui.XUIFrame;

/**
 * Created with IntelliJ IDEA.
 * User: jasontujun
 * Date: 13-5-24
 * Time: 下午2:47
 */
public class LTimeline extends XBaseLayer{

    private Button mTopBtn;
    private ViewFlow mViewFlow;

    public LTimeline(XUIFrame uiFrame) {
        super(uiFrame);

        setContentView(R.layout.timeline_frame);
        mTopBtn = (Button) findViewById(R.id.top_btn);
        mViewFlow = (ViewFlow) findViewById(R.id.story_frame);
        ATimeline phaseAdapter = new ATimeline(getContext());
        mViewFlow.setAdapter(phaseAdapter);
        TitleFlowIndicator indicator = (TitleFlowIndicator) findViewById(R.id.timeline_indic);
        indicator.setTitleProvider(phaseAdapter);
        mViewFlow.setFlowIndicator(indicator);

        mTopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFrameHandler().sendEmptyMessage(FMain.GO_TO_ADD_STORY);
            }
        });
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

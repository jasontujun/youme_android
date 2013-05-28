package com.soulware.youme.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.soulware.youme.R;
import com.soulware.youme.data.cache.DataRepo;
import com.soulware.youme.data.cache.PhaseSource;
import com.soulware.youme.data.cache.SourceName;
import com.soulware.youme.data.model.Phase;
import com.soulware.youme.ui.controls.XListView;
import com.soulware.youme.ui.controls.viewflow.TitleProvider;

/**
 * Timeline界面上viewflow的适配器，用来显示多个Phase
 * Created with IntelliJ IDEA.
 * User: jasontujun
 * Date: 13-5-25
 * Time: 下午8:41
 */
public class ATimeline extends BaseAdapter implements TitleProvider {


    private PhaseSource mPhaseSource;
    private Context context;

    public ATimeline(Context context) {
        this.context = context;
        mPhaseSource = (PhaseSource) DataRepo.getInstance().getSource(SourceName.PHASE);
    }

    @Override
    public int getCount() {
        return mPhaseSource.size();
    }

    @Override
    public Object getItem(int position) {
        return mPhaseSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        public XListView storyListView;
        public View tipFrame;
        public TextView tipView;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null) {
            convertView = View.inflate(context, R.layout.timeline_phase, null);
            viewHolder = new ViewHolder();
            viewHolder.tipFrame = convertView.findViewById(R.id.tip_view_frame);
            viewHolder.tipView = (TextView) convertView.findViewById(R.id.tip_view);
            viewHolder.storyListView = (XListView) convertView.findViewById(R.id.story_list);
            viewHolder.storyListView.setRefreshable(true);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Phase phase = (Phase) getItem(position);
        APhase storyListAdapter;
        if (viewHolder.storyListView.getAdapter() == null) {
            storyListAdapter = new APhase(context);
            viewHolder.storyListView.setAdapter(storyListAdapter);
        } else {
            storyListAdapter = (APhase) viewHolder.storyListView.getAdapter();
        }
        storyListAdapter.refresh(phase.getStartTime(), phase.getEndTime());

        if (storyListAdapter.getCount() == 0) {
            viewHolder.storyListView.setVisibility(View.GONE);
            viewHolder.tipFrame.setVisibility(View.VISIBLE);
            viewHolder.tipView.setText(phase.getName() + ",");
        } else {
            viewHolder.storyListView.setVisibility(View.VISIBLE);
            viewHolder.tipFrame.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public String getTitle(int position) {
        return ((Phase)getItem(position)).getName();
    }
}

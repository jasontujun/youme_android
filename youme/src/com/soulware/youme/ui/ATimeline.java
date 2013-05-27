package com.soulware.youme.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import com.soulware.youme.R;
import com.soulware.youme.data.cache.DataRepo;
import com.soulware.youme.data.cache.PhaseSource;
import com.soulware.youme.data.cache.SourceName;
import com.soulware.youme.data.model.Phase;
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
        public ListView storyListView;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null) {
            convertView = View.inflate(context, R.layout.timeline_phase, null);
            viewHolder = new ViewHolder();
            viewHolder.storyListView = (ListView) convertView.findViewById(R.id.story_list);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Phase phase = (Phase) getItem(position);
        APhase storyListAdapter = new APhase(context, phase.getStartTime(), phase.getEndTime());
        viewHolder.storyListView.setAdapter(storyListAdapter);

        return convertView;
    }

    @Override
    public String getTitle(int position) {
        return ((Phase)getItem(position)).getName();
    }
}

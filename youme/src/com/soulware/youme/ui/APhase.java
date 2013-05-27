package com.soulware.youme.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.soulware.youme.R;
import com.soulware.youme.data.cache.DataRepo;
import com.soulware.youme.data.cache.SourceName;
import com.soulware.youme.data.cache.StorySource;
import com.soulware.youme.data.model.Story;
import com.soulware.youme.utils.img.ImageLoader;
import com.xengine.android.media.image.XImageLocalMgr;
import com.xengine.android.utils.XStringUtil;

import java.util.List;

/**
 * 一个Phase的适配器，用来显示其中的Story List
 * Created with IntelliJ IDEA.
 * User: jasontujun
 * Date: 13-5-26
 * Time: 下午2:23
 */
public class APhase extends BaseAdapter {

    private Context context;

    private long mStartTime;
    private long mEndTime;
    private List<Story> mStoryList;

    public APhase(Context context, long startTime, long endTime) {
        this.context = context;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
        refresh();
    }

    public void refresh() {
        StorySource storySource = (StorySource) DataRepo.getInstance().getSource(SourceName.STORY);
        mStoryList = storySource.getByTime(mStartTime, mEndTime);
    }

    @Override
    public int getCount() {
        return mStoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return mStoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        public ImageView coverView;
        public TextView titleView;
        public TextView timeView;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(convertView == null) {
            convertView = View.inflate(context, R.layout.timeline_story_item, null);
            viewHolder = new ViewHolder();
            viewHolder.coverView = (ImageView) convertView.findViewById(R.id.cover);
            viewHolder.titleView = (TextView) convertView.findViewById(R.id.title_view);
            viewHolder.timeView = (TextView) convertView.findViewById(R.id.time_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Story story = (Story) getItem(position);
        viewHolder.titleView.setText(story.getName());
        viewHolder.timeView.setText(XStringUtil.date2str(story.getStoryTime()));
        // TODO 封面
        if (XStringUtil.isNullOrEmpty(story.getCoverImageId())) {
            viewHolder.coverView.setImageResource(android.R.color.transparent);
        } else {
            ImageLoader.getInstance().asyncLoadBitmap(
                    context, story.getCoverImageId(), viewHolder.coverView,
                    XImageLocalMgr.ImageSize.SCREEN);
        }

        final String storyId = story.getId();
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FStory.class);
                intent.putExtra("storyId", storyId);
                context.startActivity(intent);
            }
        });

        return convertView;
    }
}

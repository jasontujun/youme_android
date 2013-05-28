package com.soulware.youme.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.soulware.youme.R;
import com.soulware.youme.data.cache.DataRepo;
import com.soulware.youme.data.cache.ImageSource;
import com.soulware.youme.data.cache.SourceName;
import com.soulware.youme.data.model.Image;
import com.soulware.youme.utils.img.ImageLoader;
import com.xengine.android.data.cache.XDataChangeListener;
import com.xengine.android.media.image.XImageLocalMgr;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jasontujun
 * Date: 13-5-27
 * Time: 下午3:22
 */
public class AStory extends BaseAdapter implements XDataChangeListener<Image> {

    private Context context;
    private String mStoryId;
    private List<Image> mImageList;
    private boolean mIsEditMode = false;

    public AStory(Context context) {
        this.context = context;
        ImageSource imageSource = (ImageSource) DataRepo.getInstance().getSource(SourceName.IMAGE);
        imageSource.registerDataChangeListener(this);
        mImageList = new ArrayList<Image>();
    }

    public void refresh(String storyId) {
        ImageSource imageSource = (ImageSource) DataRepo.getInstance().getSource(SourceName.IMAGE);
        mStoryId = storyId;
        mImageList = imageSource.getByStoryId(storyId);
        notifyDataSetChanged();
    }

    public void setEditMode(boolean isEdit) {
        mIsEditMode = isEdit;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mImageList.size();
    }

    @Override
    public Object getItem(int i) {
        return mImageList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    private class ViewHolder {
        public ImageView imageView;
        public TextView timeView;
        public View btnFrame;
        public Button recordBtn;
        public Button deleteBtn;
        public Button infoBtn;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(convertView == null) {
            convertView = View.inflate(context, R.layout.story_image_item, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
            viewHolder.timeView = (TextView) convertView.findViewById(R.id.time_view);
            viewHolder.btnFrame = convertView.findViewById(R.id.btn_frame);
            viewHolder.recordBtn = (Button) convertView.findViewById(R.id.btn_record);
            viewHolder.deleteBtn = (Button) convertView.findViewById(R.id.btn_delete);
            viewHolder.infoBtn = (Button) convertView.findViewById(R.id.btn_info);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Image image = (Image) getItem(position);
        if (!image.isHasSecret())
            viewHolder.timeView.setVisibility(View.GONE);
        else {
            viewHolder.timeView.setVisibility(View.VISIBLE);
            viewHolder.timeView.setText(image.getSecretSize() + "''");
        }
        // 异步加载图片
        ImageLoader.getInstance().asyncLoadBitmap(
                context, image.getId(), viewHolder.imageView,
                XImageLocalMgr.ImageSize.SCREEN);
        // 按钮栏
        if (!mIsEditMode) {
            viewHolder.btnFrame.setVisibility(View.GONE);
        } else {
            viewHolder.btnFrame.setVisibility(View.VISIBLE);
            // 录音按钮
            viewHolder.recordBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //To change body of implemented methods use File | Settings | File Templates.
                }
            });
            // 删除按钮
            viewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //To change body of implemented methods use File | Settings | File Templates.
                }
            });
            // 信息按钮
            viewHolder.infoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //To change body of implemented methods use File | Settings | File Templates.
                }
            });
        }

        return convertView;
    }



    @Override
    public void onChange() {
        postNotifyDataChange();
    }

    @Override
    public void onAdd(Image image) {
        postNotifyDataChange();
    }

    @Override
    public void onAddAll(List<Image> images) {
        postNotifyDataChange();
    }

    @Override
    public void onDelete(Image image) {
        postNotifyDataChange();
    }

    @Override
    public void onDeleteAll(List<Image> images) {
        postNotifyDataChange();
    }


    private void postNotifyDataChange() {
        changeHandler.sendEmptyMessage(0);
    }

    private Handler changeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0) {
                refresh(mStoryId);
            }
        }
    };
}

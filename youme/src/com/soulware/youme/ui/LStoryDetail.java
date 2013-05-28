package com.soulware.youme.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.View;
import android.widget.*;
import com.soulware.youme.R;
import com.soulware.youme.data.cache.DataRepo;
import com.soulware.youme.data.cache.ImageSource;
import com.soulware.youme.data.cache.SourceName;
import com.soulware.youme.data.cache.StorySource;
import com.soulware.youme.data.model.Image;
import com.soulware.youme.data.model.Story;
import com.soulware.youme.logic.StoryTimelineMgr;
import com.xengine.android.system.mobile.XPhotoListener;
import com.xengine.android.system.ui.XBackType;
import com.xengine.android.system.ui.XBaseLayer;
import com.xengine.android.system.ui.XUIFrame;
import com.xengine.android.utils.XLog;
import com.xengine.android.utils.XStringUtil;

import java.io.File;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jasontujun
 * Date: 13-5-24
 * Time: 下午2:48
 */
public class LStoryDetail extends XBaseLayer {

    private TextView mTitleView;
    private TextView mTimeView;
    private Button mBackBtn;
    private View mTopBtnFrame;
    private Button mEditBtn;
    private Button mAddBtn;
    private TextView mGiftView;
    //    private XHorizontalListView mImageListView;
    private Gallery mImageListView;
    private AStory mStoryAdapter;
    private FrameLayout mImageDetailFrame;
    private Dialog mAddImageDialog;

    private CImageDetail mImageDetailComponent;
    private boolean mShowImageDetail;// 是否显示图片详情

    private Story mStory;
    private boolean mIsEditMode = false;// 是否进入编辑模式

    public LStoryDetail(XUIFrame uiFrame, String storyId) {
        super(uiFrame);
        DataRepo repo = DataRepo.getInstance();
        StorySource storySource = (StorySource) repo.getSource(SourceName.STORY);
        ImageSource imageSource = (ImageSource) repo.getSource(SourceName.IMAGE);
        mStory = storySource.getById(storyId);

        setContentView(R.layout.story_detail);
        mImageDetailFrame = (FrameLayout) findViewById(R.id.image_detail_frame);
        mTitleView = (TextView) findViewById(R.id.title_view);
        mTimeView = (TextView) findViewById(R.id.time_view);
        mBackBtn = (Button) findViewById(R.id.top_btn);
        mTopBtnFrame = findViewById(R.id.top_btn_frame);
        mEditBtn = (Button) findViewById(R.id.edit_btn);
        mAddBtn = (Button) findViewById(R.id.add_btn);
        mGiftView = (TextView) findViewById(R.id.gift_view);
//        mImageListView = (XHorizontalListView) findViewById(R.id.image_list);
        mImageListView = (Gallery) findViewById(R.id.image_list);

        mTitleView.setText(mStory.getName());
        mTimeView.setText(XStringUtil.date2calendarStr(new Date(mStory.getStoryTime())));
        // 监听
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFrameHandler().sendEmptyMessage(FStory.BACK);
            }
        });
        mEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 进入编辑模式
                enterEditMode();
            }
        });
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 添加图片
                showAddImageDialog();
            }
        });

        // 列表
        mStoryAdapter = new AStory(getContext());
        mImageListView.setAdapter(mStoryAdapter);
        mImageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (mIsEditMode || mShowImageDetail)
                    return;

                Image image = (Image) mStoryAdapter.getItem(position);
                if (mImageDetailComponent == null)
                    mImageDetailComponent = new CImageDetail(LStoryDetail.this);

                mImageDetailComponent.showImage(image.getId());
                mImageDetailFrame.addView(mImageDetailComponent.getContent());
                mShowImageDetail = true;
            }
        });
        mStoryAdapter.refresh(storyId);

        enterNormalMode();
        // 尽量左对齐显示
        if (imageSource.getByStoryId(storyId).size() > 1) {
            mImageListView.setSelection(1);
        }
    }

    private void enterEditMode() {
        if (mIsEditMode)
            return;

        mIsEditMode = true;
        mTimeView.setVisibility(View.GONE);
        mTitleView.setText("编辑");
        mTopBtnFrame.setVisibility(View.GONE);
        mStoryAdapter.setEditMode(true);
    }

    private void enterNormalMode() {
        if (!mIsEditMode)
            return;

        mIsEditMode = false;
        mTimeView.setVisibility(View.VISIBLE);
        mTimeView.setText(XStringUtil.date2calendarStr(new Date(mStory.getStoryTime())));
        mTitleView.setText(mStory.getName());
        mStoryAdapter.setEditMode(false);

        if (!mStory.getAuthorId().equals(mStory.getOwnerId())) {
            mTopBtnFrame.setVisibility(View.GONE);
            mGiftView.setVisibility(View.VISIBLE);
            mGiftView.setText("来自" + mStory.getAuthorName() + "的礼物");
        } else {
            mTopBtnFrame.setVisibility(View.VISIBLE);
            mGiftView.setVisibility(View.GONE);
        }
    }


    @Override
    public void onLayerUnCovered() {
        super.onLayerUnCovered();

        if (mImageDetailComponent != null)
            mImageDetailComponent.onLayerUnCovered();
    }

    @Override
    public void onLayerCovered() {
        super.onLayerCovered();

        if (mImageDetailComponent != null)
            mImageDetailComponent.onLayerCovered();
    }

    @Override
    public Handler getLayerHandler() {
        return null;
    }

    @Override
    public int back() {
        if (mIsEditMode) {
            enterNormalMode();
            return XBackType.CHILD_BACK;
        } else {
            if (mShowImageDetail) {
                int result = mImageDetailComponent.back();
                if (result == XBackType.CHILD_BACK) {
                    mShowImageDetail = false;
                    mImageDetailFrame.removeView(mImageDetailComponent.getContent());
                }
                return result;
            }
            return XBackType.NOTHING_TO_BACK;
        }
    }


    private void showAddImageDialog() {
        if (mAddImageDialog == null) {
            final String[] ways = new String[] { "拍一张照", "从本地导入"};
            mAddImageDialog = new AlertDialog.Builder(getContext()).
                    setTitle("添加图片，请选择方式")
                    .setItems(ways, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    mobileMgr().doTakePhoto(photoListener);
                                    break;
                                case 1:
                                    mobileMgr().doPickPhotoFromGallery(photoListener);
                                    break;
                            }
                            Toast.makeText(getContext(), ways[which], Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAddImageDialog.dismiss();
                        }
                    }).create();
        }
        mAddImageDialog.show();
    }

    // 获取图片的监听
    private XPhotoListener photoListener = new XPhotoListener() {
        @Override
        public void onSuccess(File file) {
            if (file != null) {
                XLog.d("PHOTO", "photo file:" + file.getAbsolutePath());
                StoryTimelineMgr.getInstance().addImage(mStory.getId(), file.getAbsolutePath());
            }else {
                Toast.makeText(getContext(), "拍照失败~", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void onFail() {
            Toast.makeText(getContext(), "拍照失败~", Toast.LENGTH_SHORT).show();
        }
    };
}

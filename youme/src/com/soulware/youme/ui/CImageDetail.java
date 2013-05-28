package com.soulware.youme.ui;

import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.soulware.youme.R;
import com.soulware.youme.data.cache.DataRepo;
import com.soulware.youme.data.cache.ImageSource;
import com.soulware.youme.data.cache.SourceName;
import com.soulware.youme.data.model.Image;
import com.xengine.android.media.image.XAndroidImageLocalMgr;
import com.xengine.android.media.image.XImageLocalMgr;
import com.xengine.android.system.ui.XBackType;
import com.xengine.android.system.ui.XBaseComponent;
import com.xengine.android.system.ui.XUILayer;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: jasontujun
 * Date: 13-5-28
 * Time: 上午11:39
 */
public class CImageDetail extends XBaseComponent {

    private ImageView mImageView;
    private TextView mTimeView;

    private Image mImage;

    public CImageDetail(XUILayer parent) {
        super(parent);

        setContentView(R.layout.image_detail);
        mImageView = (ImageView) findViewById(R.id.image_view);
        mTimeView = (TextView) findViewById(R.id.time_view);

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFrameHandler().sendEmptyMessage(FStory.BACK);
            }
        });
        mTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO 播放语音
                Toast.makeText(getContext(), "播放语音", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showImage(String imageId) {
        ImageSource imageSource = (ImageSource) DataRepo.getInstance().getSource(SourceName.IMAGE);
        mImage = imageSource.getById(imageId);
        // 显示图片
        try {
            mImageView.setImageDrawable(new BitmapDrawable(XAndroidImageLocalMgr.getInstance().
                    getLocalImage(mImage.getLocalImagePath(), XImageLocalMgr.ImageSize.SCREEN)));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "加载图片失败~", Toast.LENGTH_SHORT).show();
        }
        if (mImage.isHasSecret()) {
            mTimeView.setVisibility(View.VISIBLE);
        } else {
            mTimeView.setVisibility(View.GONE);
        }
    }

    @Override
    public int back() {
        return XBackType.CHILD_BACK;
    }
}

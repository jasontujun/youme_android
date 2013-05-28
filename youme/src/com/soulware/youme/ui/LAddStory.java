package com.soulware.youme.ui;

import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import com.soulware.youme.R;
import com.soulware.youme.logic.StoryTimelineMgr;
import com.soulware.youme.utils.AnimationUtil;
import com.xengine.android.system.ui.XBackType;
import com.xengine.android.system.ui.XBaseLayer;
import com.xengine.android.system.ui.XUIFrame;
import com.xengine.android.utils.XLog;
import com.xengine.android.utils.XStringUtil;

import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: jasontujun
 * Date: 13-5-28
 * Time: 下午5:04
 */
public class LAddStory extends XBaseLayer {
    private Button mBackBtn;
    private EditText mStoryNameInput;
    private DatePicker mStoryTimeInput;
    private Button mAddStoryBtn;

    private int selectedYear, selectedMonth, selectedDay;// 真实的年月日
    private Calendar today;

    public LAddStory(XUIFrame uiFrame) {
        super(uiFrame);

        setContentView(R.layout.story_add);
        mBackBtn = (Button) findViewById(R.id.top_btn);
        mStoryNameInput = (EditText) findViewById(R.id.story_name_input);
        mStoryTimeInput = (DatePicker) findViewById(R.id.story_time_picker);
        mAddStoryBtn = (Button) findViewById(R.id.add_story_button);

        today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY,23);
        today.set(Calendar.MINUTE,59);
        today.set(Calendar.SECOND,59);
        selectedYear = 2013;
        selectedMonth = 5;
        selectedDay = 29;
        // DatePicker
        mStoryTimeInput.init(selectedYear, selectedMonth - 1, selectedDay,
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
                        selectedYear = year;
                        selectedMonth = month + 1;
                        selectedDay = day;
                        XLog.d("AddStory", "on date change:" + selectedYear +
                                "." + selectedMonth + "." + selectedDay);
                    }
                });

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFrameHandler().sendEmptyMessage(FMain.BACK);
            }
        });

        mAddStoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String storyName = mStoryNameInput.getText().toString();
                if (XStringUtil.isNullOrEmpty(storyName)) {
                    AnimationUtil.startShakeAnimation(mStoryNameInput, getContext());
                    Toast.makeText(getContext(), "请起个故事名字...", Toast.LENGTH_SHORT).show();
                    return;
                }

                Calendar selectDate = Calendar.getInstance();
                selectDate.set(selectedYear, selectedMonth - 1, selectedDay);
                if(selectDate.after(today)) {
                    AnimationUtil.startShakeAnimation(mStoryTimeInput, getContext());
                    Toast.makeText(getContext(), "不能超过今天...", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 添加故事
                if (StoryTimelineMgr.getInstance().addStory(storyName, selectDate.getTimeInMillis()) == null) {
                    Toast.makeText(getContext(), "创建Story成功！", Toast.LENGTH_SHORT).show();
                    getFrameHandler().sendEmptyMessage(FMain.BACK);
                }
            }
        });
    }

    @Override
    public Handler getLayerHandler() {
        return null;
    }

    @Override
    public int back() {
        return XBackType.SELF_BACK;
    }
}

package com.soulware.youme.ui;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import com.soulware.youme.R;
import com.soulware.youme.data.cache.DataRepo;
import com.soulware.youme.data.cache.GlobalStateSource;
import com.soulware.youme.data.cache.SourceName;
import com.soulware.youme.logic.SystemMgr;
import com.xengine.android.system.ui.XBackType;
import com.xengine.android.system.ui.XBaseLayer;
import com.xengine.android.system.ui.XUIFrame;
import com.xengine.android.utils.XLog;
import com.xengine.android.utils.XStringUtil;

/**
 * Created with IntelliJ IDEA.
 * User: jasontujun
 * Date: 13-5-24
 * Time: 下午2:46
 */
public class LLogin extends XBaseLayer {

    private static final int LOGO_LENGTH = 2500;// logo最短时长
    private Runnable initTask;// UI线程中执行的初始化任务
    private long startTime, endTime;

    private View mLoginFrame;
    private EditText mUserNameInput;
    private EditText mDescriptionInput;
    private EditText mPasswordInput;
    private Button mLoginButton;

    /**
     * 构造函数，记得调用setContentView()哦
     *
     * @param uiFrame
     */
    public LLogin(XUIFrame uiFrame, Runnable initTask) {
        super(uiFrame);
        this.initTask = initTask;

        // UI thing..
        setContentView(R.layout.login);
        mLoginFrame = findViewById(R.id.login_frame);
        mUserNameInput = (EditText) findViewById(R.id.username_input);
        mDescriptionInput = (EditText) findViewById(R.id.description_input);
        mPasswordInput = (EditText) findViewById(R.id.password_input);
        mLoginButton = (Button) findViewById(R.id.login_button);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO 登陆，不存在用户则注册
                getFrameHandler().sendEmptyMessage(FMain.GO_TO_TIMELINE);
            }
        });
        // 隐藏登陆框
        mLoginFrame.setVisibility(View.INVISIBLE);
    }

    private void showLoginFrame() {
        GlobalStateSource globalStateSource = (GlobalStateSource)
                DataRepo.getInstance().getSource(SourceName.GLOBAL_STATE);
        String userName = globalStateSource.getLastUserName();// 上一次用户名
        String password = globalStateSource.getLastUserPassword();// 上一次密码
        String description = globalStateSource.getLastUserDescription();// 上一次描述

        if (!XStringUtil.isNullOrEmpty(userName))
            mUserNameInput.setText(userName);
        if (!XStringUtil.isNullOrEmpty(password))
            mPasswordInput.setText(password);
        if (!XStringUtil.isNullOrEmpty(description))
            mDescriptionInput.setText(description);

        Animation fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade);
        fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                XLog.d("FK", "onAnimationStart");
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                XLog.d("FK", "onAnimationEnd");
                mLoginFrame.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
                XLog.d("FK", "onAnimationRepeat");
            }
        });
        mLoginFrame.setVisibility(View.VISIBLE);
        mLoginFrame.startAnimation(fadeInAnimation);
    }


    /**
     * 开始动画计时
     */
    private void startTiming() {
        startTime = System.currentTimeMillis();
    }

    /**
     * 结束动画计时
     */
    private void endTiming() {
        endTime = System.currentTimeMillis();
        // 计算差值
        long delta = endTime - startTime;
        XLog.d("FK", "LOGO的时间间隔：" + delta);
        if (delta < LOGO_LENGTH) {
            long remain = LOGO_LENGTH - delta;
            try {
                Thread.sleep(remain);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLayerAddedToFrame() {
        // 启动异步线程加载数据
        new AsyncTask<Void, Long, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                startTiming();

                // 初始化数据源、数据库等
                SystemMgr.initSystem(getContext());
                return null;
            }
            @Override
            protected void onPostExecute(Void result) {
                // 外部传入的初始化任务
                if (initTask != null)
                    initTask.run();

                // 显示登录界面
                endTiming();
                getLayerHandler().sendEmptyMessage(0);
            }
        }.execute(null);
    }

    private Handler mLayerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: // 显示登陆框
                    showLoginFrame();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public Handler getLayerHandler() {
        return mLayerHandler;
    }

    @Override
    public int back() {
        return XBackType.NOTHING_TO_BACK;
    }
}

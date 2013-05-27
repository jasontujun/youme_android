package com.soulware.youme.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import com.soulware.youme.data.cache.DataRepo;
import com.soulware.youme.data.cache.GlobalStateSource;
import com.soulware.youme.data.cache.SourceName;
import com.soulware.youme.logic.SystemMgr;
import com.soulware.youme.session.HttpClientHolder;
import com.soulware.youme.utils.AnimationUtil;
import com.soulware.youme.utils.img.ImgMgrHolder;
import com.xengine.android.session.http.XNetworkUtil;
import com.xengine.android.system.file.XAndroidFileMgr;
import com.xengine.android.system.heartbeat.XAndroidHBM;
import com.xengine.android.system.ui.XBackType;
import com.xengine.android.system.ui.XBaseFrame;
import com.xengine.android.system.ui.XUILayer;
import com.xengine.android.utils.XLog;

/**
 * Created with IntelliJ IDEA.
 * User: jasontujun
 * Date: 13-5-24
 * Time: 下午3:08
 */
public class FMain extends XBaseFrame {
    private long lastBackTime;// 上一次back键的时间
    private static final int PRESS_BACK_INTERVAL = 1500; // back按键间隔，单位：毫秒

    // 界面
    private LLogin mLoginLayer;
    private LTimeline mTimelineLayer;

    @Override
    public void preInit(Context context) {
        // 禁掉XLog
//        XLog.setDebugEnabled(false);
//        XLog.setErrorEnabled(false);
//        XLog.setInfoEnabled(false);

        XNetworkUtil.init(getApplicationContext());
        HttpClientHolder.init(getApplicationContext());
        AnimationUtil.init(getApplicationContext());
        // 初始化文件管理器根目录
        XAndroidFileMgr.getInstance().setRootName("youme");
    }

    @Override
    public void init(Context context) {
        // 初始化图片下载管理器
        int screenWidth = screen().getScreenWidth();
        int screenHeight = screen().getScreenHeight();
        ImgMgrHolder.init(screenWidth, screenHeight);

        // 注册心跳
        getSystemStateManager().registerSystemStateListener(XAndroidHBM.getInstance());

        // 启动logo界面
        if (mLoginLayer == null) {
            mLoginLayer = new LLogin(this, new Runnable() {
                @Override
                public void run() {
                    // 预加载主界面，为了提高整体启动速度
                    mTimelineLayer = new LTimeline(FMain.this);
                }
            });
        }
        addLayer(mLoginLayer);

        // 检测版本
//        UmengUpdateAgent.update(context);
//        UmengUpdateAgent.setUpdateOnlyWifi(false);
//        UmengUpdateAgent.setUpdateAutoPopup(true);
//        MobclickAgent.onError(context);
    }

    @Override
    public void onFrameDisplay() {
        // 检测网络和GPS的状态
//        if (!XNetworkUtil.isNetworkAvailable())
// TODO            DialogUtil.createWarningDialog(this).show("有点小问题~", "您的手机网络没打开哦~");
    }

    @Override
    public Handler getFrameHandler() {
        return mainFrameHandler;
    }

    @Override
    public boolean isFullScreen() {
        return true;
    }

    @Override
    public boolean isBackKeyDisabled() {
        XLog.d("BACK", "点击back按钮！");

        XUILayer layer = getTopLayer();
        if (layer != null) {
            int result = layer.back();// 先调用顶部图层的back()函数
            if (result == XBackType.SELF_BACK) {
                removeLayer(layer);// 退出这一图层
                lastBackTime = 0;
            } else if (result == XBackType.NOTHING_TO_BACK) {
                back();// 没有可以退出的。调用自身的back()函数
            } else {
                lastBackTime = 0;// 如果图层back键操作成功，则重置退出程序的标识
            }
        } else {
            back();// 再调用自身的back()函数
        }
        return true;
    }

    @Override
    public boolean isKeyMenuDisable() {
        XUILayer layer = getTopLayer();
        if (layer != null) {
            return layer.onMenu();
        }
        return true;
    }

    @Override
    public String getName() {
        return "FMain";
    }

    /**
     * 两次back键退出程序
     * @return
     */
    @Override
    public int back() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBackTime <= PRESS_BACK_INTERVAL) {
            // 退出程序自动注销
            GlobalStateSource globalStateSource = (GlobalStateSource) DataRepo.
                    getInstance().getSource(SourceName.GLOBAL_STATE);
            if (globalStateSource.isLogin()) {
                globalStateSource.setCurrentUser("", "");
            }
            // 退出前清空整个系统，如：临时文件，管理器等
            SystemMgr.clearSystem();
            exit();
        } else {
            lastBackTime = currentTime;
            Toast.makeText(getContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
        }
        return XBackType.SELF_BACK;
    }


    public static final int BACK = 1;
    public static final int GO_TO_TIMELINE = 2;
    private Handler mainFrameHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BACK:
                    isBackKeyDisabled();
                    break;
                // 进入主界面
                case GO_TO_TIMELINE:
                    if(mTimelineLayer == null) {
                        mTimelineLayer = new LTimeline(FMain.this);
                    }
                    addLayer(mTimelineLayer);
                    break;
                default:
                    break;
            }
        }
    };
}

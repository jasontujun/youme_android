package com.soulware.youme.logic;

import android.content.Context;
import com.soulware.youme.data.cache.*;
import com.soulware.youme.utils.img.ImageLoader;
import com.soulware.youme.utils.img.ImgMgrHolder;
import com.xengine.android.data.db.XSQLiteHelper;
import com.xengine.android.system.file.XAndroidFileMgr;
import com.xengine.android.system.file.XFileMgr;

import java.io.File;

/**
 * Created by jasontujun.
 * Date: 12-4-27
 * Time: 下午3:08
 */
public class SystemMgr {
    private static SystemMgr instance;

    public synchronized static SystemMgr getInstance() {
        if(instance == null) {
            instance = new SystemMgr();
        }
        return instance;
    }

    private SystemMgr() {}


    public static final int DIR_DATA_IMG = 20;
    public static final int DIR_DATA_SPEECH = 21;

    /**
     * 初始化系统
     * @param context
     */
    public static void initSystem(Context context) {
        clearSystem();
        initFileMgr();
        initDB(context);
        initDataSources(context);
        FakeMgr.fake();// TODO 添加测试数据
    }

    private static void initFileMgr() {
        // 文件夹管理器
        XFileMgr fileMgr = XAndroidFileMgr.getInstance();
        fileMgr.setDir(DIR_DATA_IMG, "data" + File.separator + "image", false);
        fileMgr.setDir(DIR_DATA_SPEECH, "data" + File.separator + "speech", false);
        // 图片下载管理器
        ImgMgrHolder.getImgDownloadMgr().setDownloadDirectory(
                fileMgr.getDir(XFileMgr.FILE_TYPE_TMP).getAbsolutePath());
    }

    /**
     * 初始化数据库
     * @param context
     */
    private static void initDB(Context context) {
        // 初始化数据库
        XSQLiteHelper.initiate(context, "morln_bhw_db", 1);
    }

    /**
     * 初始化非公共数据源。
     * 一部分是空数据源。
     * 一部分从sharePreference导入。
     * 一部分从SQLite导入。
     */
    private static void initDataSources(Context context) {
        DataRepo repo = DataRepo.getInstance();
        // 公用数据源
        GlobalStateSource globalStateSource = new GlobalStateSource(context);
        globalStateSource.setLoginStatus(GlobalStateSource.LOGIN_STATUS_NO_LOGIN);
        repo.registerDataSource(globalStateSource);
        repo.registerDataSource(new SystemSettingSource(context));
        repo.registerDataSource(new ImageCacheSource());
        repo.registerDataSource(new ImageSource());
        repo.registerDataSource(new StorySource());
        repo.registerDataSource(new PhaseSource());
        // ...
    }

    public static void clearSystem() {
        // clear image cache
        ImageLoader.getInstance().clearImageCache();

        // clear tmp file
        XAndroidFileMgr.getInstance().clearDir(XFileMgr.FILE_TYPE_TMP);
        XAndroidFileMgr.getInstance().clearDir(XFileMgr.FILE_TYPE_PHOTO);

        // clear Mgr
//        LoginMgr.clearInstance();
//        BbsArticleMgr.clearInstance();
//        BbsBoardMgr.clearInstance();
//        BbsMailMgr.clearInstance();
//        BbsPersonMgr.clearInstance();

        // clear DataSource
        DataRepo.clearInstance();
    }
}

package com.soulware.youme.data.cache;

import android.content.Context;
import android.content.SharedPreferences;
import com.xengine.android.data.cache.XDataSource;

/**
 * 系统配置数据源。
 * 目前系统的配置只包含音效和背景音乐，还有已经登陆过的用户及其密码。
 * 本数据源中的数据都保存在SharedPreferences里面。
 * Created by 赵之韵.
 * Date: 11-12-10
 * Time: 上午11:19
 */
public class SystemSettingSource implements XDataSource {

    private static final String PREF_NAME = "lbstask.systemSetting";

    private static final String REMEMBER = "remember";// 记住密码

    private static final String AUTO_LOGIN = "autoLogin";// 自动登录

    private static final String AUTO_LOGOUT = "autoLogout";// 退出程序时自动注销

    private static final String AUTO_DOWNLOAD_IMG = "autoDownloadImg";// 自动下载图片
    public static final int AUTO_DOWNLOAD_IMG_CLOSE = 0;// 关
    public static final int AUTO_DOWNLOAD_IMG_WIFI = 1;// wifi时候开
    public static final int AUTO_DOWNLOAD_IMG_ALWAYS = 2;// 任何时候开

    
    private SharedPreferences pref;

    public SystemSettingSource(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setRememberPassword(boolean enable) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(REMEMBER, enable);
        editor.commit();
    }

    public boolean isRememberPassword() {
        return pref.getBoolean(REMEMBER, true);
    }

    public void setAutoLogin(boolean enable) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(AUTO_LOGIN, enable);
        editor.commit();
    }

    public boolean isAutoLogin() {
        return pref.getBoolean(AUTO_LOGIN, false);
    }

    public void setAutoLogout(boolean enable) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(AUTO_LOGOUT, enable);
        editor.commit();
    }

    public boolean isAutoLogout() {
        return pref.getBoolean(AUTO_LOGOUT, true);
    }

    public void setAutoDownloadImg(int downloadImgChoice) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(AUTO_DOWNLOAD_IMG, downloadImgChoice);
        editor.commit();
    }

    public int getAutoDownloadImg() {
        return pref.getInt(AUTO_DOWNLOAD_IMG, AUTO_DOWNLOAD_IMG_WIFI);
    }

    @Override
    public String getSourceName() {
        return SourceName.SYSTEM_SETTING;
    }
}

package com.soulware.youme.data.cache;

import android.content.Context;
import android.content.SharedPreferences;
import com.xengine.android.data.cache.XDataSource;

/**
 * 记录全局都要用到的状态。
 * 存储在SharedPreferences中。
 * 包括当前登陆的用户名、密码和通信token。
 * Created by jasontujun.
 * Date: 12-2-23
 * Time: 下午3:34
 */
public class GlobalStateSource implements XDataSource {

    private static final String PREF_NAME = "lbstask.globalState";

    private static final String USERNAME = "username";

    private static final String PASSWORD = "password";

    private static final String DESCRIPTION = "description";

    private static final String LAST_USERNAME = "lastUsername";

    private static final String LAST_PASSWORD = "lastPassword";

    private static final String LAST_DESCRIPTION = "lastDescription";


    /**
     * 登录状态。共三种。
     * 未登录=0，只有BBS登录=1，全登录=2
     */
    private static final String LOGIN_STATUS = "loginStatus";
    public static final int LOGIN_STATUS_NO_LOGIN = 0;
    public static final int LOGIN_STATUS_LOGIN = 1;


    private SharedPreferences pref;

    private String token ;// 和服务器通信的token

    /**
     * 全局状态数据源
     * @param context 请使用由getApplicationContext()获得的context
     */
    public GlobalStateSource(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    /**
     * 返回当前登陆的用户的用户名
     */
    public String getCurrentUserName() {
        return pref.getString(USERNAME, null);
    }

    /**
     * 返回当前用户使用的密码
     */
    public String getCurrentUserPassword() {
        return pref.getString(PASSWORD, null);
    }

    /**
     * 返回当前用户的描述
     */
    public String getCurrentUserDescription() {
        return pref.getString(DESCRIPTION, null);
    }

    /**
     * 返回上次登陆的用户名
     */
    public String getLastUserName() {
        return pref.getString(LAST_USERNAME, null);
    }

    /**
     * 返回上次登录的密码
     */
    public String getLastUserPassword() {
        return pref.getString(LAST_PASSWORD, null);
    }

    /**
     * 返回上次登录的描述
     */
    public String getLastUserDescription() {
        return pref.getString(LAST_DESCRIPTION, null);
    }

    /**
     * 设置token
     * @param token 通信token
     */
    public void setToken(String token){
        this.token = token;
    }

    /**
     * 返回当前的通信token
     */
    public String getToken() {
        return token;
    }

    /**
     * 当心跳超时或者注销的时候清除当前用户通信使用的token字符串
     */
    public void clearToken() {
        token = null;
    }

    /**
     * 设置当前登陆的用户名、密码
     * @param userName 用户名
     * @param password 密码
     */
    public void setCurrentUser(String userName, String password, String description) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(USERNAME, userName);
        editor.putString(PASSWORD, password);
        editor.putString(DESCRIPTION, description);
        editor.commit();
    }

    /**
     * 设置上次登陆的用户名、密码
     * @param userName 用户名
     * @param password 密码
     */
    public void setLastUser(String userName, String password, String description) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(LAST_USERNAME, userName);
        editor.putString(LAST_PASSWORD, password);
        editor.putString(LAST_DESCRIPTION, description);
        editor.commit();
    }

    /**
     * REVISED 注销时情况当前登录的用户信息
     */
    public void removeCurrentUser() {
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(USERNAME);
        editor.remove(PASSWORD);
        editor.commit();
        clearToken();
    }

    public boolean isLogin() {
        int loginStatus = pref.getInt(LOGIN_STATUS, LOGIN_STATUS_NO_LOGIN);
        if(loginStatus == LOGIN_STATUS_NO_LOGIN) {
            return false;
        }else {
            return true;
        }
    }
    
    public int getLoginStatus() {
        return pref.getInt(LOGIN_STATUS, LOGIN_STATUS_NO_LOGIN);
    }

    public void setLoginStatus(int loginStatus) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(LOGIN_STATUS, loginStatus);
        editor.commit();
    }

    @Override
    public String getSourceName() {
        return SourceName.GLOBAL_STATE;
    }
}

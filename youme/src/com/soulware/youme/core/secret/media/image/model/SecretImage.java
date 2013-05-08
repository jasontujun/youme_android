package com.soulware.youme.core.secret.media.image.model;

import android.graphics.Bitmap;


/**
 * Created by jasontujun.
 * Date: 13-2-27
 * Time: 下午9:45
 */
public class SecretImage {
    private int width, height;// 图片原始的长宽
    private String srcPath;// 源图片地址
    private Bitmap displayImage;// 显示图片(经过压缩等处理的图片数据，用于显示)
    private int[] pixels;// 图片像素(图片像素)
    private byte[] secret;// 隐藏信息
    private int secretType;// 信息类型
    private String author;// 作者
    private long time;// 时间
    private int password;// MD5加密过的(图片头包含匹配验证)
    private String passwordSrc;// 原始密码(用户输入：用于解码提取的信息)
    private int versionCode;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Bitmap getDisplayImage() {
        return displayImage;
    }

    public void setDisplayImage(Bitmap displayImage) {
        this.displayImage = displayImage;
    }

    public byte[] getSecret() {
        return secret;
    }

    public void setSecret(byte[] secret) {
        this.secret = secret;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getSecretType() {
        return secretType;
    }

    public void setSecretType(int secretType) {
        this.secretType = secretType;
    }

    public int getPassword() {
        return password;
    }

    public void setPassword(int password) {
        this.password = password;
    }

    public String getPasswordSrc() {
        return passwordSrc;
    }

    public void setPasswordSrc(String passwordSrc) {
        this.passwordSrc = passwordSrc;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getSrcPath() {
        return srcPath;
    }

    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    public int[] getPixels() {
        return pixels;
    }

    public void setPixels(int[] pixels) {
        this.pixels = pixels;
    }
}

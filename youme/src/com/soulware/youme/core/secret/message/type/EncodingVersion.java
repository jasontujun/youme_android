package com.soulware.youme.core.secret.message.type;

/**
 * SecretImage格式编码头。
 * 作者出身年份(4) + 身份证后四位(4)
 * Created by jasontujun.
 * Date: 13-2-28
 * Time: 下午9:32
 */
public class EncodingVersion {
    public static final int SIMPLE_PARITY_VERSION = 1990273601;
    public static final int MULTI_CHANNEL_PARITY_VERSION = 1990273602;

    public static boolean verify(int versionCode) {
        switch (versionCode) {
            case SIMPLE_PARITY_VERSION:
                return true;
            case MULTI_CHANNEL_PARITY_VERSION:
                return true;
        }
        return false;
    }
}

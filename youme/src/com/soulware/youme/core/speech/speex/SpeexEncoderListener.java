package com.soulware.youme.core.speech.speex;

/**
 * Created with IntelliJ IDEA.
 * User: jasontujun
 * Date: 13-5-23
 * Time: 下午12:06
 */
public interface SpeexEncoderListener {
    void encodeProgress(int encodeSize);

    void encodeFinish();
}

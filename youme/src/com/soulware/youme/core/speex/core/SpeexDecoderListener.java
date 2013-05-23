package com.soulware.youme.core.speex.core;

/**
 * Created with IntelliJ IDEA.
 * User: jasontujun
 * Date: 13-5-23
 * Time: 上午11:58
 */
public interface SpeexDecoderListener {
    void decodedProgress(long decodeSize);

    void decodedFinish(boolean success);
}

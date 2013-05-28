package com.soulware.youme.logic;

import com.soulware.youme.core.speech.SpeechPlayer;
import com.soulware.youme.core.speech.SpeechRecorder;
import com.soulware.youme.core.speech.speex.SpeexEncoderListener;
import com.soulware.youme.data.cache.DataRepo;
import com.soulware.youme.data.cache.ImageSource;
import com.soulware.youme.data.cache.SourceName;
import com.soulware.youme.data.model.Image;
import com.xengine.android.system.file.XAndroidFileMgr;
import com.xengine.android.system.file.XFileMgr;
import com.xengine.android.utils.XLog;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: jasontujun
 * Date: 13-5-23
 * Time: 下午3:54
 */
public class SpeechMgr {
    private static SpeechMgr instance;

    public synchronized static SpeechMgr getInstance() {
        if (instance == null)
            instance = new SpeechMgr();
        return instance;
    }

    private ImageSource mImageSource;
    private SpeechRecorder mRecorder;
    private SpeechPlayer mPlayer;

    private SpeechMgr() {
        DataRepo repo = DataRepo.getInstance();
        mImageSource = (ImageSource) repo.getSource(SourceName.IMAGE);

        mRecorder = new SpeechRecorder();
        mPlayer = new SpeechPlayer();
    }


    public void startRecord(String imageId, String speechPath) {
        final Image image = mImageSource.getById(imageId);
        if (image == null)
            return;

        final File speechFile = new File(speechPath);

        mRecorder.startRecord(
                speechPath,
                new SpeexEncoderListener() {
                    @Override
                    public void encodeProgress(int encodeSize) {
                    }
                    @Override
                    public void encodeFinish() {
                        // TODO 融入进图片中
                        try {
                            File tmpDir = XAndroidFileMgr.getInstance().getDir(XFileMgr.FILE_TYPE_TMP);
                            String newImagePath = tmpDir + File.separator +
                                    "IMG_" + System.currentTimeMillis() + ".png";
                            byte[] message = XAndroidFileMgr.getInstance().file2byte(speechFile);
                            if (SecretMgr.getInstance().hideSecret
                                    (image.getLocalImagePath(), message, newImagePath)) {
                                XLog.d("Speex", "语音信息融入文件成功！");
                                image.setHasSecret(true);
                                image.setSecretSize(10);
                                image.setLocalImagePath(newImagePath);
                                image.setLocalSpeechPath(speechFile.getAbsolutePath());
                            }
                        } catch (IOException e) {
                            XLog.d("Speex", "语音信息融入文件出错！");
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void stopRecord() {

    }

    public boolean playSpeech(String imageId) {
        final Image image = mImageSource.getById(imageId);
        if (image == null)
            return false;

        return true;
    }
}

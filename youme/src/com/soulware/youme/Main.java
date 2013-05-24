package com.soulware.youme;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.soulware.youme.core.secret.media.image.ImageHandler;
import com.soulware.youme.core.secret.media.image.ImageHandlerAndroidImpl;
import com.soulware.youme.core.secret.media.image.model.SecretImage;
import com.soulware.youme.core.secret.message.type.MessageType;
import com.soulware.youme.core.secret.util.DataUtils;
import com.soulware.youme.core.speech.SpeechPlayer;
import com.soulware.youme.core.speech.SpeechRecorder;
import com.soulware.youme.core.speech.speex.SpeexEncoderListener;
import com.soulware.youme.mgr.FileMgr;
import com.xengine.android.media.graphics.XAndroidScreen;
import com.xengine.android.media.graphics.XScreen;
import com.xengine.android.media.image.XAndroidImageLocalMgr;
import com.xengine.android.media.image.XImageLocalMgr;
import com.xengine.android.system.file.XAndroidFileMgr;
import com.xengine.android.system.file.XFileMgr;
import com.xengine.android.system.mobile.XAndroidMobileMgr;
import com.xengine.android.system.mobile.XPhotoListener;
import com.xengine.android.utils.XLog;

import java.io.File;
import java.io.IOException;

/**
 * Created by jasontujun.
 * Date: 13-3-22
 * Time: 下午12:41
 */
public class Main extends Activity {

    private XAndroidMobileMgr mobileMgr;
    private SpeechRecorder mRecorder;
    private SpeechPlayer mPlayer;
    private ImageHandler mImageHandler;

    private Button takePhoto;
    private ImageView imageView;
    private File imageFile;
    private File soundFile;
    private File newImageFile;
    private File decodeSoundFile;
    private SecretImage oldPic;
    private SecretImage newPic;
    //    private String testImg = "/mnt/sdcard/b.png";
    private String testImg = "/mnt/sdcard/front2.png";

    private boolean addedSound = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 初始化图片管理器
        XFileMgr fileMgr = XAndroidFileMgr.getInstance();
        fileMgr.setRootName("demoandroid");
        fileMgr.setDir(XFileMgr.FILE_TYPE_TMP, "tmp", true);
        fileMgr.setDir(XFileMgr.FILE_TYPE_PHOTO, "photo", true);
        File tmpDir = fileMgr.getDir(XFileMgr.FILE_TYPE_TMP);
        soundFile = new File(tmpDir, "sound.spx");
        newImageFile = new File(tmpDir, "NEW_IMG_" + System.currentTimeMillis() + ".png");
        decodeSoundFile = new File(tmpDir, "DECODE_SOUND_" + System.currentTimeMillis() + ".spx");
        // 初始化手机功能管理器
        XScreen screen = new XAndroidScreen(this);
        mobileMgr = new XAndroidMobileMgr(this, screen.getScreenWidth(), screen.getScreenHeight());
        // 初始化录音器
        mRecorder = new SpeechRecorder();
        mPlayer = new SpeechPlayer();
        // 初始化图片处理器
        mImageHandler = ImageHandlerAndroidImpl.getInstance();

        setContentView(R.layout.main);
        takePhoto = (Button) findViewById(R.id.take_photo_btn);
        imageView = (ImageView) findViewById(R.id.image_view);

        takePhoto.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mobileMgr.doTakePhoto(photoListener);
//                mobileMgr.doPickPhotoFromGallery(photoListener);
                imageFile = new File(testImg);
                oldPic = mImageHandler.loadImage(imageFile.getAbsolutePath());
                if (oldPic != null) {
                    imageView.setImageBitmap(oldPic.getDisplayImage());
                    takePhoto.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(Main.this, "加载图片失败！", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        imageView.setOnTouchListener(getWordListener);
//        imageView.setOnTouchListener(hideWordListener);
        imageView.setOnTouchListener(hideSoundListener);
    }

    private void showSecret(int secretType, byte[] secret) {
        if (secretType == MessageType.SOUND) {
            if (FileMgr.byte2file(secret, decodeSoundFile)) {
                Toast.makeText(Main.this, "语音信息提取成功！", Toast.LENGTH_SHORT).show();
                mPlayer.startPlay(decodeSoundFile.getAbsolutePath());
            } else {
                Toast.makeText(Main.this, "语音信息无法还原成文件...", Toast.LENGTH_SHORT).show();
            }
        } else {
            String word = DataUtils.byte2string(secret, 0,
                    secret.length, ImageHandler.STRING_CHAR_SET);
            Toast.makeText(Main.this, "文字信息提取成功：" + word, Toast.LENGTH_SHORT).show();
        }
    }

    private View.OnTouchListener getWordListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                SecretImage testedPic = mImageHandler.pickSecret(testImg);
                if (testedPic == null)
                    return true;

                mImageHandler.pickSecret(testedPic);
                byte[] secret = testedPic.getSecret();
                if (secret == null) {
                    Toast.makeText(Main.this, "只是一张普通图片", Toast.LENGTH_SHORT).show();
                } else {
                    int messageType = testedPic.getSecretType();
                    showSecret(messageType, secret);
                }
            }
            return true;
        }
    };

    private View.OnTouchListener hideWordListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (!addedSound) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    addedSound = true;
                    String secret = "aa";
                    byte[] wordByteArray = DataUtils.string2byte(secret, ImageHandler.STRING_CHAR_SET);
                    Toast.makeText(Main.this, "融入文字:" + secret, Toast.LENGTH_SHORT).show();
                    if (mImageHandler.hideSecret(oldPic, "jasontujun", MessageType.WORD,
                            wordByteArray, 0, newImageFile.getAbsolutePath())) {
                        Toast.makeText(Main.this, "文字信息融入成功！", Toast.LENGTH_SHORT).show();
                        newPic = mImageHandler.loadImage(newImageFile.getAbsolutePath());
                        imageView.setImageBitmap(newPic.getDisplayImage());
                    } else {
                        Toast.makeText(Main.this, "文字信息融入失败！", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    if (newPic == null)
                        return true;

                    mImageHandler.pickSecret(newPic);
                    byte[] secret = newPic.getSecret();
                    if (secret == null) {
                        Toast.makeText(Main.this, "只是一张普通图片", Toast.LENGTH_SHORT).show();
                    } else {
                        int messageType = newPic.getSecretType();
                        showSecret(messageType, secret);
                    }
                }
            }
            return true;
        }
    };

    private View.OnTouchListener hideSoundListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (!addedSound) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    Toast.makeText(Main.this, "开始录音", Toast.LENGTH_SHORT).show();
                    mRecorder.startRecord(
                            soundFile.getAbsolutePath(),
                            new SpeexEncoderListener() {
                                @Override
                                public void encodeProgress(int encodeSize) {
                                }
                                @Override
                                public void encodeFinish() {
                                    // TODO 融入进图片中
                                    try {
                                        oldPic = mImageHandler.loadImage(imageFile.getAbsolutePath());
                                        byte[] soundByteArray = FileMgr.file2byte(soundFile);
                                        if (mImageHandler.hideSecret(oldPic, "jasontujun", MessageType.SOUND,
                                                soundByteArray, 0, newImageFile.getAbsolutePath())) {
                                            XLog.d("Speex", "语音信息融入成功！");
                                        } else {
                                            XLog.d("Speex", "语音信息融入失败！");
                                        }
                                    } catch (IOException e) {
                                        XLog.d("Speex", "语音信息融入文件出错！");
                                        e.printStackTrace();
                                    }
                                }
                            });
                } else if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                    addedSound = true;
                    mRecorder.stopRecord();
                    Toast.makeText(Main.this, "结束录音", Toast.LENGTH_SHORT).show();
                    // 回放
//                    mPlayer.startPlay(soundFile.getAbsolutePath());
                }
            } else {
//                mPlayer.startPlay(soundFile.getAbsolutePath());
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    if (newPic == null) {
                        newPic = mImageHandler.loadImage(newImageFile.getAbsolutePath());
                        imageView.setImageBitmap(newPic.getDisplayImage());
                    }

                    mImageHandler.pickSecret(newPic);
                    byte[] secret = newPic.getSecret();
                    if (secret == null) {
                        Toast.makeText(Main.this, "只是一张普通图片", Toast.LENGTH_SHORT).show();
                    } else {
                        int messageType = newPic.getSecretType();
                        showSecret(messageType, secret);
                    }
                }
            }
            return true;
        }
    };


    // 获取图片的监听
    private XPhotoListener photoListener = new XPhotoListener() {
        @Override
        public void onSuccess(File file) {
            if(file != null) {
                XLog.d("PHOTO", "photo file:" + file.getAbsolutePath());
                try {
                    takePhoto.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);

                    imageFile = file;
                    Bitmap bitmap = XAndroidImageLocalMgr.getInstance()
                            .getLocalImage(imageFile.getName(), XImageLocalMgr.ImageSize.SCREEN);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e1) {
                    Toast.makeText(Main.this, "拍照失败~", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(Main.this, "拍照失败~", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void onFail() {
            Toast.makeText(Main.this, "拍照失败~", Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        XLog.d("MAIN", "onConfigurationChanged!!@@@&&");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mobileMgr.onInvokeResult(this, requestCode, resultCode, data);
    }
}
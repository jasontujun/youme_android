package com.soulware.youme.logic;

import com.soulware.youme.data.cache.*;
import com.soulware.youme.data.model.Image;
import com.soulware.youme.data.model.Phase;
import com.soulware.youme.data.model.Story;
import com.xengine.android.system.file.XAndroidFileMgr;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jasontujun
 * Date: 13-5-26
 * Time: 下午4:12
 */
public class FakeMgr {
    private static final String TAG = FakeMgr.class.getSimpleName();

    public static void fake() {
        fakePhase();
        fakeStory();
        fakeImage();
    }

    private static void fakePhase() {
        PhaseSource phaseSource = (PhaseSource) DataRepo.getInstance().getSource(SourceName.PHASE);
        phaseSource.add(new Phase("p000", "user000", "小时候", 0, new Date(109, 8, 1).getTime()));
        phaseSource.add(new Phase("p001", "user000", "我的大学", new Date(109, 8, 1).getTime(), new Date(1013, 6, 1).getTime()));
        phaseSource.add(new Phase("p002", "user000", "未来", new Date(113, 6, 1).getTime(), Long.MAX_VALUE));
    }

    private static void fakeStory() {
        StorySource storySource = (StorySource) DataRepo.getInstance().getSource(SourceName.STORY);
        storySource.add(new Story("s000", "u000", "tujun", "u000", "tujun", "我的天使", new Date(108, 4, 20).getTime(), null));
        storySource.add(new Story("s001", "u000", "tujun", "u000", "tujun", "那些手绘海报", new Date(110, 8, 1).getTime(), "i006"));
        storySource.add(new Story("s002", "u000", "tujun", "u000", "tujun", "犯二", new Date(112, 5, 1).getTime(), null));
        storySource.add(new Story("s003", "u000", "tujun", "u000", "tujun", "毕业了", new Date(113, 5, 1).getTime(), null));
    }

    private static void fakeImage() {
        String dirPath = XAndroidFileMgr.getInstance().getDir(SystemMgr.DIR_DATA_IMG).getAbsolutePath();
        DataRepo repo = DataRepo.getInstance();
        ImageSource imageSource = (ImageSource) repo.getSource(SourceName.IMAGE);
        ImageCacheSource imageCacheSource = (ImageCacheSource) repo.getSource(SourceName.IMAGE_CHACHE);
        imageSource.add(new Image("i000", "s001", new Date().getTime(), new ArrayList<String>(),
                dirPath + File.separator + "20091109001.jpg", null, false, 0));
        imageSource.add(new Image("i001", "s001", new Date().getTime(), new ArrayList<String>(),
                dirPath + File.separator + "20091109005.jpg", null, false, 0));
        imageSource.add(new Image("i002", "s001", new Date().getTime(), new ArrayList<String>(),
                dirPath + File.separator + "20091110007.jpg", null, false, 0));
        imageSource.add(new Image("i003", "s001", new Date().getTime(), new ArrayList<String>(),
                dirPath + File.separator + "20091110008.jpg", null, false, 0));
        imageSource.add(new Image("i004", "s001", new Date().getTime(), new ArrayList<String>(),
                dirPath + File.separator + "20091113009.jpg", null, false, 0));
        imageSource.add(new Image("i005", "s001", new Date().getTime(), new ArrayList<String>(),
                dirPath + File.separator + "20091113010.jpg", null, false, 0));
        imageSource.add(new Image("i006", "s001", new Date().getTime(), new ArrayList<String>(),
                dirPath + File.separator + "20091117020.jpg", null, false, 0));
        imageSource.add(new Image("i007", "s001", new Date().getTime(), new ArrayList<String>(),
                dirPath + File.separator + "20091118021.jpg", null, false, 0));
        imageSource.add(new Image("i008", "s001", new Date().getTime(), new ArrayList<String>(),
                dirPath + File.separator + "20091118022.jpg", null, false, 0));
        imageSource.add(new Image("i009", "s001", new Date().getTime(), new ArrayList<String>(),
                dirPath + File.separator + "20091120058.jpg", null, false, 0));
        imageSource.add(new Image("i0010", "s001", new Date().getTime(), new ArrayList<String>(),
                dirPath + File.separator + "20091129063.jpg", null, false, 0));
        imageSource.add(new Image("i0011", "s001", new Date().getTime(), new ArrayList<String>(),
                dirPath + File.separator + "20091205075.jpg", null, false, 0));
        imageSource.add(new Image("i0012", "s001", new Date().getTime(), new ArrayList<String>(),
                dirPath + File.separator + "20091206076.jpg", null, false, 0));
        imageSource.add(new Image("i0013", "s001", new Date().getTime(), new ArrayList<String>(),
                dirPath + File.separator + "20091206077.jpg", null, false, 0));

        for (int i = 0; i<imageSource.size(); i++) {
            Image image = imageSource.get(i);
            imageCacheSource.putImage(image.getId(), image.getLocalImagePath());
        }
    }
}

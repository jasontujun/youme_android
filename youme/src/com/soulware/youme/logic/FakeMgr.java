package com.soulware.youme.logic;

import com.soulware.youme.data.cache.*;
import com.soulware.youme.data.model.Image;
import com.soulware.youme.data.model.Phase;
import com.soulware.youme.data.model.Story;
import com.soulware.youme.data.model.User;
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
        fakeGlobale();
        fakeUser();
        fakePhase();
        fakeStory();
        fakeImage();
    }

    private static void fakeGlobale() {
        GlobalStateSource globalStateSource = (GlobalStateSource) DataRepo.getInstance().getSource(SourceName.GLOBAL_STATE);
        globalStateSource.setCurrentUser("涂俊", "123456", "Follow your heart");
        globalStateSource.setLastUser("涂俊", "123456", "Follow your heart");
    }

    private static void fakeUser() {
        UserSource userSource = (UserSource) DataRepo.getInstance().getSource(SourceName.USER);
        userSource.add(new User("u000", "涂俊", "123456", new String[]{"Follow your heart"},
                "南京大学", new Date().getTime()));
        userSource.add(new User("u001", "陆怡平", "123456", new String[]{"Follow your heart"},
                "南京大学", new Date().getTime()));
    }

    private static void fakePhase() {
        PhaseSource phaseSource = (PhaseSource) DataRepo.getInstance().getSource(SourceName.PHASE);
        phaseSource.add(new Phase("p000", "u000", "小时候", 0, new Date(109, 8, 1).getTime()));
        phaseSource.add(new Phase("p001", "u000", "我的大学", new Date(109, 8, 1).getTime(), new Date(1013, 6, 1).getTime()));
        phaseSource.add(new Phase("p002", "u000", "未来", new Date(113, 6, 1).getTime(), Long.MAX_VALUE));
    }

    private static void fakeStory() {
        StorySource storySource = (StorySource) DataRepo.getInstance().getSource(SourceName.STORY);
        storySource.add(new Story("s000", "u000", "涂俊", "u000", "涂俊", "我的天使", new Date(108, 4, 20).getTime(), "i040"));
        storySource.add(new Story("s001", "u000", "涂俊", "u000", "涂俊", "那些手绘海报", new Date(110, 8, 1).getTime(), "i006"));
        storySource.add(new Story("s002", "u000", "涂俊", "u000", "涂俊", "美丽的南大", new Date(112, 8, 9).getTime(), "i020"));
        storySource.add(new Story("s003", "u000", "涂俊", "u000", "涂俊", "毕业了", new Date(113, 5, 1).getTime(), "i030"));
    }

    private static void fakeImage() {
        String dirPath = XAndroidFileMgr.getInstance().getDir(SystemMgr.DIR_DATA_IMG).getAbsolutePath();
        DataRepo repo = DataRepo.getInstance();
        ImageSource imageSource = (ImageSource) repo.getSource(SourceName.IMAGE);
        ImageCacheSource imageCacheSource = (ImageCacheSource) repo.getSource(SourceName.IMAGE_CHACHE);
        imageSource.add(new Image("i000", "s001", new Date().getTime(), new ArrayList<String>(),
                dirPath + File.separator + "20091107001.png", null, false, 0));
        imageSource.add(new Image("i001", "s001", new Date().getTime(), new ArrayList<String>(),
                dirPath + File.separator + "20091109001.png", null, false, 0));
        imageSource.add(new Image("i003", "s001", new Date().getTime(), new ArrayList<String>(),
                dirPath + File.separator + "20091110007.png", null, false, 0));
        imageSource.add(new Image("i004", "s001", new Date().getTime(), new ArrayList<String>(),
                dirPath + File.separator + "20091117020.png", null, false, 0));
        imageSource.add(new Image("i005", "s001", new Date().getTime(), new ArrayList<String>(),
                dirPath + File.separator + "20091205075.png", null, false, 0));
        imageSource.add(new Image("i006", "s001", new Date().getTime(), new ArrayList<String>(),
                dirPath + File.separator + "20091208078.png", null, false, 0));
        imageSource.add(new Image("i007", "s001", new Date().getTime(), new ArrayList<String>(),
                dirPath + File.separator + "20091215087.png", null, false, 0));
        imageSource.add(new Image("i009", "s001", new Date().getTime(), new ArrayList<String>(),
                dirPath + File.separator + "20091221092.png", null, false, 0));
        imageSource.add(new Image("i010", "s001", new Date().getTime(), new ArrayList<String>(),
                dirPath + File.separator + "20100322140.png", null, false, 0));

        imageSource.add(new Image("i020", "s002", new Date().getTime(), new ArrayList<String>(),
                dirPath + File.separator + "C360_2012-03-31-11-02-30.png", null, false, 0));
        imageSource.add(new Image("i021", "s002", new Date().getTime(), new ArrayList<String>(),
                dirPath + File.separator + "C360_2012-11-24-15-12-54.png", null, false, 0));
        imageSource.add(new Image("i022", "s002", new Date().getTime(), new ArrayList<String>(),
                dirPath + File.separator + "C360_2012-11-24-15-17-45.png", null, false, 0));

        imageSource.add(new Image("i030", "s003", new Date().getTime(), new ArrayList<String>(),
                dirPath + File.separator + "graduate.png", null, false, 0));

        imageSource.add(new Image("i040", "s000", new Date().getTime(), new ArrayList<String>(),
                dirPath + File.separator + "a1.png", null, false, 0));
        imageSource.add(new Image("i041", "s000", new Date().getTime(), new ArrayList<String>(),
                dirPath + File.separator + "a2.png", null, false, 0));

        for (int i = 0; i<imageSource.size(); i++) {
            Image image = imageSource.get(i);
            imageCacheSource.putImage(image.getId(), image.getLocalImagePath());
        }
    }
}

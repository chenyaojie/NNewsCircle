package com.wetter.nnewscircle;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import cn.bmob.newim.BmobIM;

/**
 * Created by Wetter on 2016/7/7.
 *
 */
public class MyApplication extends Application {

    // 友盟社会化组件密钥
    static {
        PlatformConfig.setWeixin("wxcbda238d018ab045", "3458777b875c9ae1ac949235f6e04be3");
        PlatformConfig.setQQZone("1105508928", "fZNhjlcgbCVPdVof");
        PlatformConfig.setSinaWeibo("306593860","110b8aff15c98bb365e13e404b549cf8");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Fresco初始化
        Fresco.initialize(this);
        // 友盟初始化
        UMShareAPI.get(this);

        // 只有主进程运行的时候才需要初始化
        if (getApplicationInfo().packageName.equals(getMyProcessName())){
            //im初始化
            BmobIM.init(this);
            //注册消息接收器
            BmobIM.registerDefaultMessageHandler(new MyMessageHandler(this));
        }
    }

    /**
     * 获取当前运行的进程名
     * @return 当前运行的进程名
     */
    public static String getMyProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

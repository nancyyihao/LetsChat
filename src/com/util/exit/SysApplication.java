
package com.util.exit;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;


/**
 * 类功能描述：完全退出程序</br>
 *
 * @author 王明献
 * @version 1.0
 * </p>
 * 修改时间：</br>
 * 修改备注：</br>
 */
public class SysApplication extends Application {

    /**
     * 存放所有启动的Activity
     */
    private List<Activity> mActivityList = new LinkedList<Activity>();

    /**
     * 声音池，用来播放发送或接收消息时的短声音
     */
    private SoundPool mSoundPool; // 播放短声音
    
    private static SysApplication mInstance;

    public Context mContext;

    private SysApplication() {
        mSoundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
    }

    public void setContext(Context context) {

        this.mContext = context;
    }

    public SoundPool getSoundResource() {

        return this.mSoundPool;
    }

    public synchronized static SysApplication getInstance() {

        if (null == mInstance) {
            mInstance = new SysApplication();
        }
        return mInstance;
    }

    public void addActivity(Activity act) {
        mActivityList.add(act);
    }

    /**
     * (完全退出) 
     */
    public void exit() {
        try {
            for (Activity act : mActivityList) {
                if (null != act) {
                    act.finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    public void onLowMemory() {

        super.onLowMemory();
        System.gc();
    }
}

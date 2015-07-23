
package com.util.exit;

import java.util.LinkedList;
import java.util.List;

import com.main.letschat.R;

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

    private List<Activity> aList = new LinkedList<Activity>();

    private static SysApplication instance;

    private SoundPool soundPool; // 播放短声音

    public Context context;

    private SysApplication() {
        soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
    }

    public void setContext(Context context) {

        this.context = context;
    }

    public SoundPool getSoundResource() {

        return this.soundPool;
    }

    public synchronized static SysApplication getInstance() {

        if (null == instance) {
            instance = new SysApplication();
        }
        return instance;
    }

    public void addActivity(Activity act) {
        aList.add(act);
    }

    public void exit() {
        try {
            for (Activity act : aList) {
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

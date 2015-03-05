package com.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**用来监听消息
 * @author jumper
 *
 */
public class MsgLisenService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		Log.v("onBind", "Service Start!");
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		Log.v("onStartCommand", "Service Start!");
		return START_STICKY;
	}

}

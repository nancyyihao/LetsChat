package com.model;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import android.os.Handler;
import android.util.Log;

import com.util.connect.Connect;
import com.util.exit.SysApplication;
import com.util.timer.Timer;

/**聊天管理
 * @author jumper
 *
 */
public class MyChatManagerListener implements ChatManagerListener {

	private String userName ;

	public MyChatManagerListener(){
		
	}
	
	public MyChatManagerListener(String userName){
		this.userName = userName ;
	}
	
	@Override
	public void chatCreated(Chat chat, boolean arg1) {
		chat.addMessageListener(new myMessageListener()) ;
	}
	
	private class myMessageListener implements MessageListener{

		@Override
		public void processMessage(org.jivesoftware.smack.Chat chat0,
				Message msg) {
			
			if (msg.getFrom().contains(userName)){
				
				//��ǰ��¼���û�����Ҫ����Ϣ���͸����û�����Ϣ��ʱ�䡢IN			
				String[] args = new String[]{Connect.getInstance().USERNAME,userName,Timer.getDate(),msg.getBody(),"IN"} ;
				android.os.Message OSmsg = handler.obtainMessage() ;					//����һ����Ϣ.
				OSmsg.what = 1 ;
				OSmsg.obj = args ;
				OSmsg.sendToTarget() ;		
			}
			Log.v( "chatType", Message.Type.chat.toString() ) ;
		}
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what){
				case 1:
						String[] args = (String[]) msg.obj; 						//ȡ��һ����Ϣ
						if(!args[3].equals("")){									//����һ������Ϣ
							oneMsg chatMsg = new oneMsg(args[0], args[1], args[2], args[3],args[4]) ;
							//msglist.add( chatMsg ) ;						
							//openHelper.addOneMsg( chatMsg ) ;					
							//adapter.notifyDataSetChanged() ;							//ˢ��������																			//��������
							SysApplication.getInstance().getSoundResource().play(1,1, 1, 0, 0, 1);
						}
						
					break ;
				case 2:
					
					break ;
			}
		}
	} ;

}

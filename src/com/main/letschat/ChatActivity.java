package com.main.letschat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import com.model.oneMsg;
import com.util.connect.Connect;
import com.util.database.myDBHelper;
import com.util.exit.SysApplication;
import com.util.timer.Timer;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.telephony.gsm.SmsMessage.MessageClass;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

/**聊天界面
 * @author jumper
 *
 */
public class ChatActivity extends Activity {
	
	private EditText eText ;
	private ListView listView ;
	private List<oneMsg> msglist  ;
	private BaseAdapter adapter ;
	private String userJID ;
	private String userName ;
	private org.jivesoftware.smack.Chat chat ;
	private myDBHelper openHelper;
	
	private class myAdapter extends BaseAdapter{
		
		private Context cxt ;
		private LayoutInflater inflater ;
		
		myAdapter(ChatActivity chat){
			this.cxt = chat ;
		}
		
		@Override
		public int getCount() {
			return msglist.size();
		}

		@Override
		public Object getItem(int position) {
			return msglist.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//显示消息的布局：内容、背景、用户、时间
			this.inflater = (LayoutInflater) this.cxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			//判断当前的消息是发出去还是接收的，根据不同场景用不同的背景图
			if(msglist.get(position).getDir().equals("IN"))
			{
				convertView = this.inflater.inflate(R.layout.msg_in, null);
			}
			else
			{
				convertView = this.inflater.inflate(R.layout.msg_out, null);
			}
			
			TextView useridView = (TextView) convertView.findViewById(R.id.formclient_row_userid);
			TextView dateView = (TextView) convertView.findViewById(R.id.formclient_row_date);
			TextView msgView = (TextView) convertView.findViewById(R.id.formclient_row_msg);
			
			useridView.setText( msglist.get(position).getUid() );
			dateView.setText( msglist.get(position).getDate() );
			msgView.setText( msglist.get(position).getMsg() );
			
			return convertView;
		}
		
	}
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_chat);	
		SysApplication.getInstance().addActivity(this); 
		SysApplication.getInstance().getSoundResource().load(this,R.raw.recv,1);
		
		findViewById(R.id.chat_reback_btn).setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ChatActivity.this,FriendlistActivity.class) ;
				startActivity(intent) ;
			}
		}) ;
			
		findViewById(R.id.send).setOnClickListener(new OnClickListener() {	
			
			@Override
			public void onClick(View v) {
				
				eText = (EditText) findViewById(R.id.editText);
				String content = eText.getText().toString() ;
				if (""==content) {
					Toast.makeText(ChatActivity.this,
							"请输入要发的消息", Toast.LENGTH_SHORT).show() ;
					return ;
				}
							
				try {					
					Message m = new Message() ;
					m.setBody(content);		
					chat.sendMessage(m) ;
					eText.setText("") ;	
					
					//发送一条消息
					String[] args = new String[] { Connect.getInstance().USERNAME,userName,
							Timer.getDate(),m.getBody(),"OUT" } ;
					android.os.Message OSmsg = handler.obtainMessage() ;
					OSmsg.what = 1 ;
					OSmsg.obj = args ;
					OSmsg.sendToTarget() ;		
					
				} catch (XMPPException e) {
					e.printStackTrace();
				}
			}
		});
		
		listView = (ListView) findViewById(R.id.msgList) ;	
		listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		
		msglist = new ArrayList<oneMsg>() ;
		
		this.adapter = new myAdapter(this) ;
		listView.setAdapter(adapter) ;
				
		userJID = getIntent().getStringExtra("USERJID") ;
		userName = getIntent().getStringExtra("USERNAME") ;
		Toast.makeText(ChatActivity.this, userName, Toast.LENGTH_SHORT).show() ;
		
		//新建一个聊天	
		ChatManager chatManager = Connect.getInstance().getConnection().getChatManager() ;
		chat = chatManager.createChat(userJID, null);
		chatManager.addChatListener(new myChatManagerListener()) ; 					
		
		//初始化数据库.
		initDatabase() ;					
		
		//加载两个人以前的对话记录
		LoadChatRecord(Connect.getInstance().USERNAME,userName) ;
	}		
	
	private class myChatManagerListener implements ChatManagerListener {

		@Override
		public void chatCreated(org.jivesoftware.smack.Chat chat, boolean arg1) {
				chat.addMessageListener(new myMessageListener()) ;
		}
		
	}
	
	//消息监听器
	private class myMessageListener implements MessageListener {
		
		@Override
		public void processMessage(org.jivesoftware.smack.Chat chat0,
				Message msg) {
			
			if (msg.getFrom().contains(userName)) {
				
				String[] args = new String[]{userName,Connect.getInstance().USERNAME,Timer.getDate(),msg.getBody(),"IN"} ;
				android.os.Message OSmsg = handler.obtainMessage() ;					//生成一条消息.
				OSmsg.what = 1 ;
				OSmsg.obj = args ;
				OSmsg.sendToTarget() ;		
			}
			Log.v( "chatType", Message.Type.chat.toString() ) ;
		}
	}
	
	public void initDatabase() {
		
		String dbName=Environment.getExternalStorageDirectory().getAbsolutePath()+"/chatHistory.db";
		openHelper =  new myDBHelper(this, dbName, null, 1) ;
	}
	
	public void LoadChatRecord(String from,String to) {
		
		msglist.clear() ;
		msglist = openHelper.getChatRecord(from, to) ;  //从数据库中调出两个人以前的对话
		adapter.notifyDataSetChanged() ;
	}
	
	private Handler handler = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
				case 1:
						String[] args = (String[]) msg.obj; 						//取出一条消息
						if (!args[3].equals("")) {									//不是一条空消息
							oneMsg chatMsg = new oneMsg(args[0], args[1], 
									args[2], args[3],args[4]) ;
							
							msglist.add( chatMsg ) ;						
							openHelper.addOneMsg( chatMsg ) ;						//将消息存入数据库				
							adapter.notifyDataSetChanged() ;							//刷新适配器
							
							SysApplication.getInstance().getSoundResource()
									.play(1,1, 1, 0, 0, 1); //播放声音
						}
						
					break ;
					
				default:

					break ;
			}
		}
	} ;
	
}

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
public class Chat extends Activity {
	
	private Button button ;
	private EditText eText ;
	private ListView listView ;
	private List<oneMsg> msglist  ;
	private BaseAdapter adapter ;
	String userJID ;
	String userName ;
	org.jivesoftware.smack.Chat chat ;
	myDBHelper openHelper;
	
	private Context context ;
	
	private class myAdapter extends BaseAdapter{
		
		private Context cxt ;
		private LayoutInflater inflater ;
		
		myAdapter(Chat chat){
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
			
			////IN,OUT的图片
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
				Intent intent = new Intent(Chat.this,Friendlist.class) ;
				startActivity(intent) ;
			}
		}) ;
		
		button = (Button) findViewById(R.id.send);
		listView = (ListView) findViewById(R.id.msgList) ;	
		listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		
		msglist = new ArrayList<oneMsg>() ;
		
		this.adapter = new myAdapter(this) ;
		listView.setAdapter(adapter) ;
		
		context = this ;
		
		userJID = getIntent().getStringExtra("USERJID") ;
		userName = getIntent().getStringExtra("USERNAME") ;
		Toast.makeText(Chat.this, userName, Toast.LENGTH_SHORT).show() ;
		
		ChatManager chatManager = Connect.getInstance().getConnection().getChatManager() ;
		chat = chatManager.createChat(userJID, null);
		chatManager.addChatListener(new myChatManagerListener()) ; 					//新建一个聊天	
		
		initDatabase() ;					//初始化数据库.
		
		button.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				eText = (EditText) findViewById(R.id.editText);
				String content = eText.getText().toString() ;
				if (""==content){
					Toast.makeText(Chat.this, "请输入要发的消息", Toast.LENGTH_SHORT).show() ;
					return ;
				}
							
				try {					
					Message m = new Message() ;
					m.setBody(content);		
					chat.sendMessage(m) ;
					eText.setText("") ;	
					
					////发送一条消息
					String[] args = new String[]{Connect.getInstance().USERNAME,userName,Timer.getDate(),m.getBody(),"OUT"} ;
					android.os.Message OSmsg = handler.obtainMessage() ;
					OSmsg.what = 1 ;
					OSmsg.obj = args ;
					OSmsg.sendToTarget() ;		
					
				
				} catch (XMPPException e) {
					e.printStackTrace();
				}
			}
		});
		
		LoadChatRecord(Connect.getInstance().USERNAME,userName) ;
	}		
	
	private class myChatManagerListener implements ChatManagerListener {

		@Override
		public void chatCreated(org.jivesoftware.smack.Chat chat, boolean arg1) {
				chat.addMessageListener(new myMessageListener()) ;
		}
		
	}
	
	private class myMessageListener implements MessageListener{

		@Override
		public void processMessage(org.jivesoftware.smack.Chat chat0,
				Message msg) {
			
			//if (null == msg.getType() ){     //若为空消息则忽略	
			//	return  ;
			//}
				
			//if (msg.getType().equals(Message.Type.chat)){ 		//消息类型为短消息
			//	Toast.makeText(Chat.this, "chat", Toast.LENGTH_SHORT).show() ;
			//}
			if (msg.getFrom().contains(userName)){
				
							
				String[] args = new String[]{userName,Connect.getInstance().USERNAME,Timer.getDate(),msg.getBody(),"IN"} ;
				android.os.Message OSmsg = handler.obtainMessage() ;					//生成一条消息.
				OSmsg.what = 1 ;
				OSmsg.obj = args ;
				OSmsg.sendToTarget() ;		
			}
			Log.v( "chatType", Message.Type.chat.toString() ) ;
		}
	}
	
	public void initDatabase(){
		
		String dbName=Environment.getExternalStorageDirectory().getAbsolutePath()+"/chatHistory.db";
		openHelper =  new myDBHelper(this, dbName, null, 1) ;
	}
	
	public void LoadChatRecord(String from,String to){
		msglist.clear() ;
		msglist = openHelper.getChatRecord(from, to) ;
		adapter.notifyDataSetChanged() ;
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what){
				case 1:
						String[] args = (String[]) msg.obj; 						//取出一条消息
						if(!args[3].equals("")){									//不是一条空消息
							oneMsg chatMsg = new oneMsg(args[0], args[1], args[2], args[3],args[4]) ;
							msglist.add( chatMsg ) ;						
							openHelper.addOneMsg( chatMsg ) ;					
							adapter.notifyDataSetChanged() ;							//刷新适配器																			//锟斤拷锟斤拷锟斤拷锟斤拷
							SysApplication.getInstance().getSoundResource().play(1,1, 1, 0, 0, 1);
						}
						
					break ;
				case 2:
					
					break ;
			}
		}
	} ;
	
}

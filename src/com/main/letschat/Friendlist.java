package com.main.letschat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;

import com.main.letschat.R.layout;
import com.model.MyChatManagerListener;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
import com.util.connect.Connect;
import com.util.database.myDBHelper;
import com.util.exit.SysApplication;

import dalvik.system.BaseDexClassLoader;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

/**朋友列表，用户登陆成功后就会跳转到这里
 * @author jumper
 *
 */
public class Friendlist extends Activity {

	private List<Map<String,Object>> data =  new ArrayList<Map<String,Object>>();
	private List<RosterEntry> userList = new ArrayList<RosterEntry>() ;  
	private ListView mlist ;
	private ListView menulist ;
	private ListView firendslist  ;
	private BaseAdapter friendAdapter ;
	private org.jivesoftware.smack.Chat chat ;
	
	private static String[] words = new String[]{"哥只是个传说.",
												"哥只是个传说.",
												"哥只是个传说.",
												"哥只是个传说.",
												"哥只是个传说.",
												"哥只是个传说.",
												"哥只是个传说.",
												"哥只是个传说."} ;
	
	public class friendListAdapter extends BaseAdapter {

		private Context cxt ;
		private LayoutInflater inflater ;
		
		friendListAdapter(Friendlist friendlist){
			this.cxt = friendlist ;
		}
		
		@Override
		public int getCount() {
			return userList.size();
		}

		@Override
		public Object getItem(int position) {
			return userList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//显示消息的布局：内容、背景、用户、时间
			this.inflater = (LayoutInflater) this.cxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			//IN,OUT的图片
			convertView = this.inflater.inflate(R.layout.friendlist_baseadapter, null);
			
			ImageView imageView =  (ImageView) convertView.findViewById(R.id.image);
			TextView nameView = (TextView) convertView.findViewById(R.id.friend_name);
			TextView signatureView = (TextView) convertView.findViewById(R.id.signature);
			
			
			Random random = new Random();
			int t = Math.abs( random.nextInt() ) % 5 ;
			String imageName = "pic" + t ;
			
			if ( t > 4 ){
				imageName = "default_pic" ;
			}
			imageView.setImageResource( getResource(imageName) ) ;
			
			Log.v("POSITION", userList.get(position) + "   "+position) ;
			nameView.setText( userList.get(position).getName().toString() );
			
			Log.v("POSITION",  words[Math.abs( random.nextInt() ) % 8] ) ;
			signatureView.setText( words[Math.abs( random.nextInt() ) % 8] );
			return convertView;
		}
		
	} 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_friendlist);
		SysApplication.getInstance().addActivity(this); 
		SysApplication.getInstance().getSoundResource().load(this,R.raw.recv,1);
		
		
		/*													//采用SimpleAdapter来作为适配器
		mlist =(ListView)findViewById(R.id.list);
		mlist.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		
		SimpleAdapter adapter = new SimpleAdapter(this,getData(),
				R.layout.frienditem,
				new String[]{"title","info","img"},
				new int[]{R.id.title,R.id.info,R.id.img});
		
		mlist.setAdapter(adapter);
		
		mlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent intent = new Intent(Friendlist.this,Chat.class) ;
				intent.putExtra("USERJID", Connect.getInstance().getUsers().get(position).getUser());
				intent.putExtra("USERNAME", Connect.getInstance().getUsers().get(position).getName());
				startActivity(intent) ;
			}
		});

		*/
		
		
		
		getData() ;
		friendAdapter = new friendListAdapter(this) ;
		firendslist = (ListView) findViewById(R.id.list) ;
		firendslist.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		firendslist.setAdapter(friendAdapter) ;
		firendslist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent intent = new Intent(Friendlist.this,Chat.class) ;
				intent.putExtra("USERJID", Connect.getInstance().getUsers().get(position).getUser());
				intent.putExtra("USERNAME", Connect.getInstance().getUsers().get(position).getName());
				startActivity(intent) ;
			}
		});
			
		// 添加侧滑按钮菜单
		com.slidingmenu.lib.SlidingMenu menu = new com.slidingmenu.lib.SlidingMenu( this);
	     menu.setMode(com.slidingmenu.lib.SlidingMenu.LEFT);
	     menu.setTouchModeAbove(com.slidingmenu.lib.SlidingMenu.TOUCHMODE_FULLSCREEN );
	     menu.setShadowWidthRes(R.dimen.shadow_width);        // 1
	     menu.setShadowDrawable(R.drawable.shadow);           // 2
	     menu.setBehindOffsetRes(R.dimen.slidingmenu_offset ); // 3
	     menu.setFadeDegree(0.35f);
	     menu.attachToActivity( this, com.slidingmenu.lib.SlidingMenu.SLIDING_CONTENT );
	     menu.setMenu(R.layout.menu_list); 						// 4
	     
	     menulist = (ListView) findViewById(R.id.menu) ;
		 String[] data = {"个人信息","管理好友","删除记录","意见反馈","登出","退出"};
		 menulist.setAdapter(new ArrayAdapter(this,android.R.layout.simple_list_item_1,data) ) ;
		 menulist.setOnItemClickListener(new OnItemClickListener() {
			 
			 //TO-DO 				添加相应的功能.
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
					switch(position){
						case 0:
							Toast.makeText(getApplicationContext(), "空空如也!", Toast.LENGTH_SHORT).show() ;
							break ;
						case 1:
								Intent intent = new Intent(Friendlist.this,ModifyFriendsActivity.class) ;;
								startActivity(intent) ;
							break ;
						case 2:
								new AlertDialog.Builder(Friendlist.this).setTitle("确定要删除所有聊天记录？").setMessage("确定？").setPositiveButton("是", new OnClickListener() {		
								@Override
								public void onClick(DialogInterface dialog, int which) {
									String dbName=Environment.getExternalStorageDirectory().getAbsolutePath()+"/chatHistory.db" ;
									myDBHelper mHelper = new myDBHelper(getApplicationContext(),dbName,null,1) ;
									mHelper.deleteRecord("message_table") ;
								}
							}).setNegativeButton("否", null).show() ;	
							break ;
						case 3:
							Toast.makeText(getApplicationContext(), "该功能尚未开发，请等待!", Toast.LENGTH_SHORT).show() ;
							break;
						case 4:							
							Intent intent1 = new Intent(Friendlist.this,LogoutAct.class) ;
							startActivity(intent1) ;
							break ;
						case 5:
							Intent intent2 = new Intent(Friendlist.this,Exit.class) ;;
							startActivity(intent2) ;
							break ;
						default:
							break ;
					}
			}
		}) ;
		 
		 
		 //ChatManager chatManager = Connect.getInstance().getConnection().getChatManager() ;
		 //chat = chatManager.createChat(userJID, null);
		 //chatManager.addChatListener(new MyChatManagerListener()) ; 	 
	}
	
	private List<Map<String,Object>> getData(){
		
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		Map<String, Object> map ;
		ArrayList<RosterEntry> users = Connect.getInstance().getUsers() ;
		for (int i=0 ; i<users.size() ; i++) {
			map = new HashMap<String, Object>();
			map.put("title", users.get(i).getName() );
			map.put("info", "哥只是个传说.");
			String imageName = "pic" + (i+1) ;
			if ( i > 1 ){
				imageName = "default_pic" ;
			}
			map.put("img", getResource(imageName));
			userList.add(users.get(i)) ;
			list.add(map);
		}
		return list ;
	}
	
	public int getResource(String imageName){  

	      Context ctx=getBaseContext();   
	      int resId = getResources().getIdentifier(imageName, "drawable" , ctx.getPackageName());   
	      return resId ;
	} 
}


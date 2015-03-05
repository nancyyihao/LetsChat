package com.main.letschat;

import com.util.connect.Connect;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Build;

/**管理朋友
 * @author jumper
 *
 */
public class ModifyFriendsActivity extends Activity {

	private Button add_btn,del_btn ;
	private EditText edit ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modify_friends);
		add_btn = (Button) findViewById(R.id.addfriends_btn) ;
		del_btn = (Button) findViewById(R.id.delfriends_btn) ;
		
		add_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				edit = (EditText) findViewById(R.id.modify_edit) ;
				String user = edit.getText().toString() ;
				if(Connect.getInstance().addUser(Connect.getInstance().getConnection().getRoster(), user+"@newburst", user) )
				{
					Toast.makeText(getApplicationContext(), "请求已发送，请等待验证!", Toast.LENGTH_SHORT).show() ;
					Log.v("添加朋友", "成功！") ;
				}else
				{
					Toast.makeText(getApplicationContext(), "添加失败，请稍候再试!", Toast.LENGTH_SHORT).show() ;
					Log.v("添加朋友", "失败") ;
				}
				
				finish() ;
				Intent intent = new Intent(ModifyFriendsActivity.this,Friendlist.class);
				startActivity(intent);  
				
			}
		}) ;
		
		del_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				edit = (EditText) findViewById(R.id.modify_edit) ;
				String user = edit.getText().toString() ;
				
				if(Connect.getInstance().removeUser(Connect.getInstance().getConnection().getRoster(), user) )
				{
					Toast.makeText(getApplicationContext(), "删除成功！", Toast.LENGTH_SHORT).show() ;
					Log.e("删除好友", "成功") ;
				}else
				{
					Toast.makeText(getApplicationContext(), "服务器给你提了个问题，请稍候再来!", Toast.LENGTH_SHORT).show() ;
					Log.e("删除好友", "失败") ;
				}
				
				finish() ;
				Intent intent = new Intent(ModifyFriendsActivity.this,Friendlist.class);
				startActivity(intent);  
			}
		}) ;
	
	}
	
	public void modify_back(View v){
		finish() ;
		Intent intent = new Intent(ModifyFriendsActivity.this,Friendlist.class);
		startActivity(intent);  
	}
}

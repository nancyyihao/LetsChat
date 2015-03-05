package com.main.letschat;

import com.util.connect.Connect;
import com.util.exit.SysApplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**登陆界面
 * @author jumper
 *
 */
public class Login extends Activity {

	private String mUser ;
	private String mPassword ;
	private LinearLayout loginStatus ;
	private RelativeLayout loginForm ;
	private EditText textUser ;
	private EditText textPassword ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		SysApplication.getInstance().addActivity(this); 
			
		loginStatus = (LinearLayout)findViewById(R.id.login_status);
		loginForm = (RelativeLayout) findViewById(R.id.login_form);
		
		findViewById(R.id.login_login_btn).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						mUser = ((EditText)findViewById(R.id.login_user_edit)).getText().toString();
						mPassword = ((EditText)findViewById(R.id.login_passwd_edit)).getText().toString(); 
						new Thread(doLogin).start();
					}
				});
		
		findViewById(R.id.forget_passwd).setOnClickListener( 
				new OnClickListener() {
			
					@Override
					public void onClick(View v) {
						Toast.makeText(getApplicationContext(), "该功能尚未开发，请等待!", Toast.LENGTH_SHORT).show() ;
						//finish() ;
					}
		}) ;
		
		findViewById(R.id.login_reback_btn).setOnClickListener( 
				new OnClickListener() {
			
					@Override
					public void onClick(View v) {
						//Toast.makeText(getApplicationContext(), "该功能尚未开发，请等待!", Toast.LENGTH_SHORT).show() ;
						finish() ;
					}
		}) ;
	}

	Handler handler = new Handler() {  
	    @Override  
	    public void handleMessage(Message msg) {  
	    	switch (msg.what) {
			case 1:
					loginForm.setVisibility(View.GONE);
					loginStatus.setVisibility(View.VISIBLE);
				break;
			case 2:													//login fail
					loginForm.setVisibility(View.VISIBLE);
					loginStatus.setVisibility(View.GONE);	
					Toast.makeText(Login.this, "正在登陆...", Toast.LENGTH_LONG).show();
	        	break;
			case 3:													//login success
					Intent intent = new Intent(Login.this,Friendlist.class);
					startActivity(intent);  
					finish();
				break;
			default:
				break;
			}
	    }  
	};  
	Runnable doLogin = new Runnable() {
		
		@Override
		public void run() {
			handler.sendEmptyMessage(1);
			if ( Connect.getInstance().connectServer() ){
				if (Connect.getInstance().Login(mUser, mPassword))
					handler.sendEmptyMessage(3);
				else	
					handler.sendEmptyMessage(2);
			}else{
				handler.sendEmptyMessage(2);
			}
		}
	};
	
}


package com.main.letschat;

import java.util.ArrayList;
import java.util.List;


import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import com.model.oneMsg;
import com.util.connect.Connect;
import com.util.database.myDBHelper;
import com.util.exit.SysApplication;
import com.util.timer.Timer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 类功能描述：管理聊天界面的类</br>
 * 
 * @author 王明献
 * @version 1.0 </p> 
 * 修改时间：</br> 
 * 修改备注：</br>
 */
public class ChatActivity extends Activity {

    
    /**
     * 装载消息的List
     */
    private List<oneMsg> mMsgList;   

    /**
     * 用户标志
     */
    private String mUserJID;         

    /**
     * 用户昵称
     */
    private String mUserName;      

    /**
     * 数据库封装类
     */
    private myDBHelper mOpenHelper;  
    
    private EditText mText;

    private ListView mListView;

    private BaseAdapter mAdapter;
    
    private org.jivesoftware.smack.Chat mChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.chat_activity);

        // 接收从上个activity传过来的参数，包括用户的ID和用户昵称
        mUserJID = getIntent().getStringExtra("USERJID");
        mUserName = getIntent().getStringExtra("USERNAME");

        if (mUserJID == "" || mUserName == "") { // 用户无效，退出聊天

            finish();
        }

        SysApplication.getInstance().addActivity(this);

        // 加载声频
        SysApplication.getInstance().getSoundResource().load(this, R.raw.recv, 1);

        findViewById(R.id.chat_reback_btn).setOnClickListener(mOnBackBtnClickListener);        
        findViewById(R.id.send).setOnClickListener(mOnSendBtnClickListener);

        mListView = (ListView)findViewById(R.id.msgList);
        mListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mMsgList = new ArrayList<oneMsg>();
        this.mAdapter = new myAdapter(this);
        mListView.setAdapter(mAdapter);

        // 新建一个聊天
        ChatManager chatManager = Connect.getInstance().getConnection().getChatManager();
        mChat = chatManager.createChat(mUserJID, null);
        chatManager.addChatListener(new myChatManagerListener());

        initDatabase();
        LoadChatRecord(Connect.USERNAME, mUserName);
    }
    
    /**
     * 类功能描述：数据适配器</br>
     *
     * @author 王明献
     * @version 1.0
     * </p>
     * 修改时间：</br>
     * 修改备注：</br>
     */
    private class myAdapter extends BaseAdapter {

        private Context context;

        private LayoutInflater inflater;

        myAdapter(ChatActivity chat) {
            this.context = chat;
        }

        @Override
        public int getCount() {
            return mMsgList.size();
        }

        @Override
        public Object getItem(int position) {
            return mMsgList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // 显示消息的布局：内容、背景、用户、时间
            this.inflater = (LayoutInflater)this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // 判断当前的消息是发出去还是接收的，根据不同场景用不同的背景图
            if (mMsgList.get(position).getDir().equals("IN")) {
                convertView = this.inflater.inflate(R.layout.chat_msg_in, parent);
            } else {
                convertView = this.inflater.inflate(R.layout.chat_msg_out, parent);
            }

            TextView useridView = (TextView)convertView.findViewById(R.id.formclient_row_userid);
            TextView dateView = (TextView)convertView.findViewById(R.id.formclient_row_date);
            TextView msgView = (TextView)convertView.findViewById(R.id.formclient_row_msg);

            useridView.setText(mMsgList.get(position).getUid());
            dateView.setText(mMsgList.get(position).getDate());
            msgView.setText(mMsgList.get(position).getMsg());

            return convertView;
        }

    }
    
    /**
     * 点击按钮返回
     */
    private OnClickListener mOnBackBtnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ChatActivity.this, FriendlistActivity.class);
            startActivity(intent);
        }
    } ; 
    
    /**
     *  点击按钮发送消息
     */
    private OnClickListener mOnSendBtnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            mText = (EditText)findViewById(R.id.editText);
            String content = mText.getText().toString();
            if ("" == content) {
                Toast.makeText(ChatActivity.this, "请输入要发的消息", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                Message m = new Message();
                m.setBody(content);
                mChat.sendMessage(m); // 发送消息给服务器
                mText.setText("");

                // 把消息显示到UI中
                String[] args = new String[] {
                        Connect.USERNAME, mUserName, Timer.getDate(), m.getBody(),
                        "OUT"
                };

                android.os.Message OSmsg = handler.obtainMessage();
                OSmsg.what = 1;
                OSmsg.obj = args;
                OSmsg.sendToTarget();

            } catch (XMPPException e) {
                e.printStackTrace();
            }
        }
    } ;
    
    /**
     * 类功能描述：消息管理器</br>
     *
     * @author 王明献
     * @version 1.0
     * </p>
     * 修改时间：</br>
     * 修改备注：</br>
     */
    private class myChatManagerListener implements ChatManagerListener {

        @Override
        public void chatCreated(org.jivesoftware.smack.Chat chat, boolean arg1) {
            chat.addMessageListener(new myMessageListener());
        }

    }

    /**
     * 类功能描述：消息监听器</br>
     *
     * @author 王明献
     * @version 1.0
     * </p>
     * 修改时间：</br>
     * 修改备注：</br>
     */
    private class myMessageListener implements MessageListener {

        @Override
        public void processMessage(org.jivesoftware.smack.Chat chat0, Message msg) {

            if (msg.getFrom().contains(mUserName)) { // 是否是发给userName的消息

                String[] args = new String[] {
                        mUserName, Connect.USERNAME, Timer.getDate(), msg.getBody(),
                        "IN"
                };

                android.os.Message OSmsg = handler.obtainMessage(); // 生成一条消息.
                OSmsg.what = 1;
                OSmsg.obj = args;
                OSmsg.sendToTarget();
            }
        }
    }

    /**
     * (初始化数据库.) 
     */
    public void initDatabase() {

        String dbName = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/chatHistory.db";
        mOpenHelper = new myDBHelper(this, dbName, null, 1);
    }

    /**
     * (加载两个人以前的对话记录) 
     * @param from  发送者
     * @param to    接收者
     */
    public void LoadChatRecord(String from, String to) {

        mMsgList.clear();
        mMsgList = mOpenHelper.getChatRecord(from, to); // 从数据库中调出两个人以前的对话
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 把消息存入数据库，并显示在UI中
     */
    @SuppressLint("HandlerLeak") 
    private Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {

                case 1: // 处理消息
                    String[] args = (String[])msg.obj; // 取出一条消息
                    if (!args[3].equals("")) { // 不是一条空消息
                        oneMsg chatMsg = new oneMsg(args[0], args[1], args[2], args[3], args[4]);

                        mMsgList.add(chatMsg);
                        mOpenHelper.addOneMsg(chatMsg); // 将消息存入数据库
                        mAdapter.notifyDataSetChanged(); // 刷新适配器

                        // 播放声音
                        SysApplication.getInstance().getSoundResource().play(1, 1, 1, 0, 0, 1);
                    }

                    break;

                default:

                    break;
            }
        }
    };

}

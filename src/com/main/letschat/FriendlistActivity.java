
package com.main.letschat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jivesoftware.smack.RosterEntry;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.util.connect.Connect;
import com.util.database.myDBHelper;
import com.util.exit.SysApplication;

/**
 * 类功能描述：朋友列表，用户登陆成功后就会跳转到这里</br>
 * 
 * @author 王明献
 * @version 1.0 </p> 
 * 修改时间：</br> 
 * 修改备注：</br>
 */
public class FriendlistActivity extends Activity {

    /**
     * 保存所有好友信息
     */
    private List<RosterEntry> mUserList = new ArrayList<RosterEntry>(); 

    /**
     * 侧滑菜单ListView
     */
    private ListView mMenuList;  

    /**
     * 朋友列表的ListView
     */
    private ListView mFirendsList; 

    /**
     * 朋友列表的数据适配器
     */
    private BaseAdapter mFriendAdapter; 

    /**
     * 每个好友的个性签名
     */
    private String[] words  ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.friendlist_activity);
        SysApplication.getInstance().addActivity(this);
        SysApplication.getInstance().getSoundResource().load(this, R.raw.recv, 1);
        
        //获取好友个性签名
        words = getResources().getStringArray(R.array.friends_words) ;

        getData(); // 获取好友列表
        mFriendAdapter = new friendListAdapter(this);
        mFirendsList = (ListView)findViewById(R.id.list);
        mFirendsList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mFirendsList.setAdapter(mFriendAdapter);

        // 设置监听，点击后就跳转聊天界面和该好友聊天
        mFirendsList.setOnItemClickListener(mOnFriendListItemClickListener);

        // 添加侧滑按钮菜单，并设置相应属性
        com.slidingmenu.lib.SlidingMenu menu = new com.slidingmenu.lib.SlidingMenu(this);
        menu.setMode(com.slidingmenu.lib.SlidingMenu.LEFT);
        menu.setTouchModeAbove(com.slidingmenu.lib.SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, com.slidingmenu.lib.SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.slidemenu_menu_list);

        mMenuList = (ListView)findViewById(R.id.menu);

        String[] items = getResources().getStringArray(R.array.slidemenulist_items) ;
        mMenuList.setAdapter(new ArrayAdapter<Object>(this, android.R.layout.simple_list_item_1, items));
        mMenuList.setOnItemClickListener(mOnMenuListItemClickListener);

    }
    
    private OnItemClickListener mOnFriendListItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Intent intent = new Intent(FriendlistActivity.this, ChatActivity.class);
            intent.putExtra("USERJID", Connect.getInstance().getUsers().get(position).getUser());
            intent.putExtra("USERNAME", Connect.getInstance().getUsers().get(position)
                    .getName());
            startActivity(intent);
        }
    } ;
    
    /**
     * 点击按钮，执行对应的操作
     */
    private OnItemClickListener mOnMenuListItemClickListener = new OnItemClickListener() {

        
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    Toast.makeText(getApplicationContext(), getString(R.string.nothing), Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(getApplicationContext(), getString(R.string.unsupport), Toast.LENGTH_SHORT)
                            .show();
                    break;
                case 2:
                    // 删除聊天记录的对话框
                    new AlertDialog.Builder(FriendlistActivity.this).
                            setTitle(getString(R.string.delete_record_title)) // 对话框的标题
                            .setMessage(getString(R.string.delete_record_message)) // 对话框的消息
                            .setPositiveButton(getString(R.string.delete_record_positive_button), 
                                    new OnClickListener() {     // 为"是"按钮添加监听事件
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            String dbPath = Environment
                                                    .getExternalStorageDirectory()
                                                    .getAbsolutePath()
                                                    + "/chatHistory.db";
                                            myDBHelper mHelper = new myDBHelper(
                                                    getApplicationContext(), dbPath, null, 1);
                                            mHelper.deleteRecord("message_table");
                                        }
                                    }).setNegativeButton(getString(R.string.delete_record_negative_button), null)
                                    .show();
                    break;
                case 3:
                    Toast.makeText(getApplicationContext(), getString(R.string.unsupport), Toast.LENGTH_SHORT)
                            .show();
                    break;
                case 4:
                    Intent intent1 = new Intent(FriendlistActivity.this, LogoutActivity.class);
                    startActivity(intent1);
                    break;
                case 5:
                    Intent intent2 = new Intent(FriendlistActivity.this, ExitActivity.class);
                    ;
                    startActivity(intent2);
                    break;
                default:
                    break;
            }
        }
    } ;
    
    /**
     * 类功能描述：数据适配器</br>
     *
     * @author 王明献
     * @version 1.0
     * </p>
     * 修改时间：</br>
     * 修改备注：</br>
     */
    private class friendListAdapter extends BaseAdapter {

        private Context mContext;

        private LayoutInflater mInflater;

        friendListAdapter(FriendlistActivity friendlist) {
            this.mContext = friendlist;
        }

        @Override
        public int getCount() {
            return mUserList.size();
        }

        @Override
        public Object getItem(int position) {
            return mUserList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("ViewHolder") @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            
            // 显示消息的布局：内容、背景、用户、时间
            this.mInflater = (LayoutInflater)this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = this.mInflater.inflate(R.layout.friendlist_item, parent);

            ImageView imageView = (ImageView)convertView.findViewById(R.id.image);
            TextView nameView = (TextView)convertView.findViewById(R.id.friend_name);
            TextView signatureView = (TextView)convertView.findViewById(R.id.signature);

            // 图片是存在本地的，随机给好友分一张图片
            Random random = new Random();
            int t = Math.abs(random.nextInt()) % 5;
            String imageName = "friendlist_ic_pic" + t;

            if (t > 4) {
                imageName = "friendlist_ic_pic0";
            }

            imageView.setImageResource(getResource(imageName));
            nameView.setText(mUserList.get(position).getName().toString());
            signatureView.setText(words[Math.abs(random.nextInt()) % 8]);

            return convertView;
        }

    }

    /**
     * 取得所有的好友数据
     * 
     * @return
     */
    private List<Map<String, Object>> getData() {

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        ArrayList<RosterEntry> users = Connect.getInstance().getUsers();
        Map<String, Object> map;

        for (int i = 0; i < users.size(); i++) {

            map = new HashMap<String, Object>();
            map.put("title", users.get(i).getName()); // 好友的名字
            map.put("info", "哥只是个传说."); // 好友的个性签名
            String imageName = "friendlist_ic_pic" + (i + 1); // 好友的图片

            if (i > 2) {
                imageName = "friendlist_ic_pic0";
            }
            map.put("img", getResource(imageName));
            mUserList.add(users.get(i));
            list.add(map);
        }
        return list;
    }

    /**
     * 根据图片名返回资源的id
     * 
     * @param imageName 图片名
     * @return 返回资源id
     */
    public int getResource(String imageName) {

        Context context = getBaseContext();
        int resId = getResources().getIdentifier(imageName, "drawable", context.getPackageName());
        return resId;
    }
}

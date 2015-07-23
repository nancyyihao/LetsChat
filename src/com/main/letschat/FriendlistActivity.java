
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

/**
 * 类功能描述：朋友列表，用户登陆成功后就会跳转到这里</br>
 * 
 * @author 王明献
 * @version 1.0 </p> 
 * 修改时间：</br> 
 * 修改备注：</br>
 */
public class FriendlistActivity extends Activity {

    private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>(); // 填充朋友的ListView的数据源

    private List<RosterEntry> userList = new ArrayList<RosterEntry>(); // 保存所有好友信息

    private ListView menulist; // 侧滑菜单ListView

    private ListView firendsList; // 朋友列表的ListView

    private BaseAdapter friendAdapter; // 朋友列表的数据适配器

    private org.jivesoftware.smack.Chat chat; // 聊天管理

    private static String[] words = new String[] {
            "哥只是个传说.", "哥只是个传说.", "哥只是个传说.", "哥只是个传说.", "哥只是个传说.", "哥只是个传说.", "哥只是个传说.", "哥只是个传说."
    };

    private class friendListAdapter extends BaseAdapter {

        private Context context;

        private LayoutInflater inflater;

        friendListAdapter(FriendlistActivity friendlist) {
            this.context = friendlist;
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
            // 显示消息的布局：内容、背景、用户、时间
            this.inflater = (LayoutInflater)this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = this.inflater.inflate(R.layout.friendlist_item, null);

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
            nameView.setText(userList.get(position).getName().toString());
            signatureView.setText(words[Math.abs(random.nextInt()) % 8]);

            return convertView;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.friendlist_activity);
        SysApplication.getInstance().addActivity(this);
        SysApplication.getInstance().getSoundResource().load(this, R.raw.recv, 1);

        getData(); // 获取好友列表
        friendAdapter = new friendListAdapter(this);
        firendsList = (ListView)findViewById(R.id.list);
        firendsList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        firendsList.setAdapter(friendAdapter);

        // 设置监听，点击后就跳转聊天界面和该好友聊天
        firendsList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(FriendlistActivity.this, ChatActivity.class);
                intent.putExtra("USERJID", Connect.getInstance().getUsers().get(position).getUser());
                intent.putExtra("USERNAME", Connect.getInstance().getUsers().get(position)
                        .getName());
                startActivity(intent);
            }
        });

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

        menulist = (ListView)findViewById(R.id.menu);

        String[] data = {
                "个人信息", "管理好友", "删除记录", "意见反馈", "登出", "退出"
        };
        menulist.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, data));
        menulist.setOnItemClickListener(new OnItemClickListener() {

            // 这里的0,1,2...是和data[]数组相对应的
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Toast.makeText(getApplicationContext(), "空空如也!", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(getApplicationContext(), "该功能尚未实现，敬请期待!", Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case 2:
                        // 删除聊天记录的对话框
                        new AlertDialog.Builder(FriendlistActivity.this).setTitle("确定要删除所有聊天记录？") // 对话框的标题
                                .setMessage("确定？") // 对话框的消息
                                .setPositiveButton("是", new OnClickListener() { // 为"是"按钮添加监听事件

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
                                        }).setNegativeButton("否", null).show();
                        break;
                    case 3:
                        Toast.makeText(getApplicationContext(), "该功能尚未开发，请等待!", Toast.LENGTH_SHORT)
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
        });

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
            userList.add(users.get(i));
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

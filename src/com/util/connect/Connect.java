
package com.util.connect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Registration;

import android.util.Log;

/**
 * 类功能描述：主要用来和服务器的交互，如登录，获取好友信息等</br>
 * 
 * @author 王明献
 * @version 1.0 </p> 
 * 修改时间：</br> 
 * 修改备注：</br>
 */
public class Connect {

    /**
     * 服务器IP
     */
    private static String SERVER_HOST = "172.18.216.6"; 

    /**
     * 服务器连接端口
     */
    private static int SERVER_PORT = 5222;  

    /**
     * 负责和服务器连接的connection
     */
    private  XMPPConnection connection = null;

    public static Connect uniqueInstance = null;   

    /**
     * 使用该用户名和服务器连接
     */
    public static String USERNAME = "";

    /**
     * 使用该密码和服务器连接
     */
    public static String PASSWORD = "";

    private Connect() {
    }

    public static Connect getInstance() {

        if (null == uniqueInstance) {
            uniqueInstance = new Connect();
        }
        return uniqueInstance;
    }

    public  XMPPConnection getConnection() {
        return connection;
    }

    /**
     * (连接服务器)
     * 
     * @return
     */
    public boolean connectServer() {

        // 新建一个连接配置
        ConnectionConfiguration config = new ConnectionConfiguration(SERVER_HOST, SERVER_PORT); 

        config.setSASLAuthenticationEnabled(false);
        config.setSecurityMode(SecurityMode.disabled);

        connection = new XMPPConnection(config); // 新建一个和服务器的连接

        try {
            connection.connect();
        } catch (XMPPException e) {
            e.printStackTrace();
            return false;
        }
        
        // 设置为接受所有消息
        connection.getRoster().setSubscriptionMode(SubscriptionMode.accept_all);
        return true;
    }

    /**
     * 登陆
     * 
     * @param user
     * @param password
     * @return
     */
    public boolean Login(String user, String password) {

        if (user == "" || password == "") {
            return false;
        }

        if (null == connection)
            return false;
        try {
            connection.login(user, password);
            USERNAME = user;
            PASSWORD = password;
        } catch (XMPPException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean Logout() {
        connection.disconnect();
        return true;
    }

    /**
     * (注册新用户)
     * 
     * @param account
     * @param password
     * @return SUCCESS 成功 FAIL 失败 USER_ALREADY_EXISTS 用户已存在 ERROR 有错误
     */
    public String regist(String account, String password) {

        if (account == "" || password == "") {

            return "FAIL";
        }

        Registration reg = new Registration();
        reg.setType(IQ.Type.SET);
        reg.setTo(connection.getServiceName());
        reg.setUsername(account);
        reg.setPassword(password);

        reg.addAttribute("android", "geolo_createUser_android");

        PacketFilter filter = new AndFilter(new PacketIDFilter(reg.getPacketID()),
                new PacketTypeFilter(IQ.class));

        PacketCollector collector = connection.createPacketCollector(filter);

        // 向服务器发送注册请求
        connection.sendPacket(reg);

        // 从服务器读结果
        IQ result = (IQ)collector.nextResult(SmackConfiguration.getPacketReplyTimeout());

        // Stop queuing results
        collector.cancel();
        if (result == null) {
            return "FAIL";
        }
        ;
        if (result.getType() == IQ.Type.RESULT) {

            return "SUCCESS";
        }
        if (result.getType() == IQ.Type.ERROR) {

            if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {
                return "USER_ALREADY_EXISTS";
            } else {
                return "ERROR";
            }
        }
        ;
        return "ERROR";
    }

    /**
     * (新增一个分组)
     * 
     * @param roster GROUPNAME:组名
     * @param userName username:uid+@+服务器名
     * @param name name:uid
     * @return
     */
    public boolean addUser(Roster roster, String userName, String name) {

        if (roster == null || userName == "" || name == "") {

            return false;
        }

        try {

            roster.createEntry(userName, name, new String[] {
                "Friends"
            });
            return true;

        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除用户
     * 
     * @param roster 用户的roster,可以通过connetion.getRoster()来获取
     * @param userName 用户名
     * @return
     */
    public boolean removeUser(Roster roster, String userName) {

        if (roster == null || userName == "") {

            return false;
        }

        try {
            if (userName.contains("@")) {
                userName = userName.split("@")[0];
            }

            RosterEntry entry = roster.getEntry(userName);

            if (null != entry) {

                roster.removeEntry(entry);
            } else {

                return false;
            }

            return true;
        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }

    /**
     * 用来修改密码
     * 
     * @param newPassword
     * @return
     */
    public boolean changePassword(String newPassword) {

        if (newPassword == "") {
            return false;
        }

        try {
            connection.getAccountManager().changePassword(newPassword);

        } catch (XMPPException e) {

            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 用来获取所有好友
     * 
     * @return ArrayList<RosterEntry>
     */
    public ArrayList<RosterEntry> getUsers() {

        ArrayList<RosterEntry> userList = new ArrayList<RosterEntry>();
        if (null != connection) {

            Roster roster = connection.getRoster();
            Collection<RosterGroup> entriesGroup = roster.getGroups(); // 总共有多少个组

            for (RosterGroup group : entriesGroup) { // 从所有组中取出所有好友

                Collection<RosterEntry> entries = group.getEntries();
                for (RosterEntry entry : entries) {

                    userList.add(entry); // 将好友都添加到userList中
                }
            }
            return userList;
        }
        return null;
    }
}

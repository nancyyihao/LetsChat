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



/**用来管理连接的类
 * @author jumper
 *
 */
public class Connect {
	
	private static String SERVER_HOST = "10.205.18.181" ;       //服务器IP
	private static int SERVER_PORT = 5222 ;						//服务器连接端口
	private static XMPPConnection connection = null ;
	public static Connect uniqueInstance = null ;
	public static String USERNAME = "" ;
	public static String PASSWORD = "" ;
	
	private Connect(){
	}
	
	public static Connect getInstance() {
		
		if (null == uniqueInstance) {
			uniqueInstance = new Connect();
		}
		return uniqueInstance ;
	}
	
	public static XMPPConnection getConnection() {
		return connection ;
	}
	
	public boolean connectServer() {
		
		ConnectionConfiguration config = 
				new ConnectionConfiguration(SERVER_HOST,SERVER_PORT) ;      
		
		config.setSASLAuthenticationEnabled(false);		//false 
		config.setSecurityMode(SecurityMode.disabled);	//disabled 
		connection = new XMPPConnection(config);  
		
		try {
			connection.connect() ;
		} catch (XMPPException e) {
			e.printStackTrace();
			Log.e("conserver", "连接失败"+e.toString());
			return false ;
		}
		Log.e("conserver", "连接成功");
		connection.getRoster().setSubscriptionMode( SubscriptionMode.accept_all ) ;
		return true ;
	}
	
	
	/** 登陆
	 * @param user
	 * @param password
	 * @return
	 */
	public boolean Login(String user,String password) {
		
		if (null == connection)
			return false ;
		try {
			connection.login(user, password);
			this.USERNAME = user ;
			this.PASSWORD = password ;
		} catch (XMPPException e) {
			e.printStackTrace();
			return false ;
		}		
		return true;
	}
	
	public boolean Logout(){
		connection.disconnect();
		return true;
	}
	
	/**
	 * 
	 * 
	 * @param account 
	 * @param password 
	 * @return 1  成功 0 失败 2 用户已存在 3 有错误
	 */
	public String regist(String account, String password) {
		
		Registration reg = new Registration();
        reg.setType(IQ.Type.SET);
        reg.setTo(connection.getServiceName());
        reg.setUsername(account);
        reg.setPassword(password);
        
        reg.addAttribute("android", "geolo_createUser_android");
        System.out.println("reg:" + reg);
        
        PacketFilter filter = new AndFilter(new PacketIDFilter(reg
                .getPacketID()), new PacketTypeFilter(IQ.class));
        
        PacketCollector collector = connection
                .createPacketCollector(filter);
        connection.sendPacket(reg);
        
        IQ result = (IQ) collector.nextResult(SmackConfiguration
                .getPacketReplyTimeout());
        
        // Stop queuing results
        collector.cancel();
         if (result == null) {                                 
        	 return "0";
         } ;
         if (result.getType() == IQ.Type.RESULT) { 				
        	 
        	return "1";
        }
         if (result.getType() == IQ.Type.ERROR) {		
            if (result.getError().toString().
            		equalsIgnoreCase("conflict(409)")) { 
            	return "2";
            } else {	
            	return "3";
            }
        } ; 
       return "3";
	}
	
	//username:uid+@+服务器名
	//name:uid
	//GROUPNAME:组名
	public boolean addUser(Roster roster, String userName, String name) {  
        try {  
            roster.createEntry(userName, name, new String[]{"Friends"});  
            return true;  
        } catch (Exception e) {  
            e.printStackTrace();  
            return false;  
        }  
    }  
	
	/** 删除用户
	 * @param roster    用户的roster,可以通过connetion.getRoster()来获取
	 * @param userName  用户名
	 * @return
	 */
	public boolean removeUser(Roster roster, String userName) {
		try {
			if (userName.contains("@")) {
				userName = userName.split("@")[0];
			}

			
			RosterEntry entry = roster.getEntry(userName);
			Log.e("roster", roster.toString() +"roster")  ;

			if(null !=entry ){
				roster.removeEntry(entry);
			}else{
				Log.e("roster",userName+ "   entry is null")  ;
				return false ;
			} 
						

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/** 用来修改密码
	 * @param newPassword
	 * @return
	 */
	public boolean changePassword(String newPassword) {
		
		try {
			connection.getAccountManager().changePassword(newPassword);
		} catch (XMPPException e) {
			e.printStackTrace();
			return false ;
		}
		return true ;
	}
	
	/** 用来获取所有好友
	 * @return ArrayList<RosterEntry>
	 */
	public ArrayList<RosterEntry> getUsers() {
		
		ArrayList<RosterEntry> userList = new ArrayList<RosterEntry>(); 
		if ( null != connection ) {
			
			Roster roster = connection.getRoster() ;
			Collection<RosterGroup> entriesGroup = roster.getGroups() ; 
			for ( RosterGroup group:entriesGroup ) {
				
				Collection<RosterEntry> entries = group.getEntries() ;
				for (RosterEntry entry : entries) {
					
					userList.add( entry ) ;
				}
			}
			return userList;
		}
		return null ;
	}
}

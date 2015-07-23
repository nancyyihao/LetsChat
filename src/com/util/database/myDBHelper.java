package com.util.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.model.oneMsg;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;


/**
 * 类功能描述：DBO,数据库管理</br>
 *
 * @author 王明献
 * @version 1.0
 * </p>
 * 修改时间：</br>
 * 修改备注：</br>
 */
public class myDBHelper extends SQLiteOpenHelper {
	
	private List<oneMsg> list = new ArrayList<oneMsg>(); //存放聊天记录
	public static final String DataBaseName ="chatHistory" ;

	public myDBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = " CREATE TABLE if not exists message_table" +
				"(chatFrom text,chatTo text,chatDate text,chatContent text,chatDir text)" ;
		db.execSQL(sql) ;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	
	}
	
	/**删除聊天记录
	 * @param tableName 把tableName表清空 
	 */
	public void deleteRecord(String tableName ) {
	    
	    if (tableName == null) {
	        return ;
	    }
		
		SQLiteDatabase db = this.getWritableDatabase();
        String sql = "delete from " + tableName;
        
        try {
            db.execSQL(sql);
        } catch (SQLException e) {
        	e.printStackTrace() ;
        }
	}
	
	/**返回两个人的聊天记录
	 * @param from  发送者
	 * @param to	接收者
	 * @return
	 */
	public List<oneMsg> getChatRecord(String from,String to) {
		
		list.clear() ;
		if (from=="" || to=="")
		    return list ;
		
	    String date="";
	    String content="";
	    String dir = "" ;
	        
		SQLiteDatabase db=this.getReadableDatabase();
		
		//调出两个人的对话记录
		String sql="select chatFrom,chatTo,chatDate,chatContent,chatDir from message_table" +
				" where (chatTo=? and chatFrom=?) or (chatFrom=? and chatTo=?) order by chatDate";
		Cursor cursor = db.rawQuery(sql,new String[]{from,to,from,to});
		
		while (cursor.moveToNext()) {
			
			date = cursor.getString(2);
			content = cursor.getString(3);
			dir = cursor.getString(4) ;

			list.add( new oneMsg(from,to, date, content, dir) ) ;
		}	
		db.close() ;
		return list ;
	}
	
	/**加入一条消息到数据库中
	 * @param chatMsg
	 */
	public void addOneMsg(oneMsg chatMsg) {
		
	    if (chatMsg == null) {
	        
	        return  ;
	    }
		SQLiteDatabase db = this.getWritableDatabase() ;
		
		String sql1="insert into message_table" +
				"(chatFrom,chatTo,chatDate,chatContent,chatDir) values(?,?,?,?,?)";
		
		//执行SQL语句
		db.execSQL(sql1, new Object[]{chatMsg.getUid(),chatMsg.getToWhom(),
		        chatMsg.getDate(),chatMsg.getMsg(),chatMsg.getDir()}); 
	}
	
}

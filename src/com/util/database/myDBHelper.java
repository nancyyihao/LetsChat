package com.util.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
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

/** 用来管理数据库连接的类
 * @author jumper
 *
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
		String user="";
		String toWhom = "" ;
		String date="";
		String content="";
		String dir = "" ;
		
		SQLiteDatabase db=this.getReadableDatabase();
		String sql="select chatFrom,chatTo,chatDate,chatContent,chatDir from message_table where " +
				"(chatFrom=? and chatTo=?) or (chatFrom=? and chatTo=?) order by chatDate";
		Cursor cursor = db.rawQuery(sql,new String[]{from,to});
		
		while (cursor.moveToNext()) {
			
			user=cursor.getString(0);
			toWhom = cursor.getString(1) ;
			date=cursor.getString(2);
			content=cursor.getString(3);
			dir = cursor.getString(4) ;

			list.add( new oneMsg(from,to, date, content, dir) ) ;
		}
		return list ;
	}
	
	/**加入一条消息到数据库中
	 * @param chatMsg
	 */
	public void addOneMsg(oneMsg chatMsg) {
		
		SQLiteDatabase db=this.getWritableDatabase() ;
		String sql1="insert into message_table(chatFrom,chatTo,chatDate,chatContent,chatDir) values(?,?,?,?,?)"; 
		db.execSQL(sql1, new Object[]{chatMsg.getUid(),chatMsg.getToWhom(),chatMsg.getDate(),chatMsg.getMsg(),chatMsg.getDir()}); 
	}
	
	/**判断表是否存在
	 * @param tableName
	 * @return
	 */
	public boolean tabbleIsExist(String tableName){
		
        boolean result = false;
        
        if (tableName == null) {
        	
                return false;
        }
        
        SQLiteDatabase db = null;
        Cursor cursor = null;
        
        try {
                db = this.getReadableDatabase();
                String sql = "select count(*) as c from "+DataBaseName+" where type ='table' and name ='"+tableName.trim()+"' ";
                cursor = db.rawQuery(sql, null);
                if (cursor.moveToNext()) {
                        int count = cursor.getInt(0);
                        if (count>0) {
                                result = true;
                        }
                }
                
        } catch (Exception e) {
                
        }               
        return result;
	}
	
}

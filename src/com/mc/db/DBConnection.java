package com.mc.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBConnection extends SQLiteOpenHelper {

	static SQLiteDatabase sqLiteDatabase;
	// 保存课程和教室的表
	public interface UserSchema {
		String TABLE_NAME = "Users";// 表的名字，表中有八个数据

		String ID = "_id";

		String USERNAME = "username";
		String PASSWORD = "password";
	}

	private static final String DATABASE_NAME = "users";// 数据库
	private static final int DATABASE_VERSION = 1;

	public DBConnection(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	// 创建表
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = "CREATE TABLE "
				+ UserSchema.TABLE_NAME
				+ " ("
				+ UserSchema.ID
				+ " INTEGER primary key autoincrement, " // 主键
				+ UserSchema.USERNAME + " text not null, "
				+ UserSchema.PASSWORD + " text not null " + ");";

/*		String[] sqlStrings = { sql, sqll };
		for (int i = 0; i < sqlStrings.length; i++) {
			db.execSQL(sqlStrings[i]);

		}*/
		 db.execSQL(sql);
		System.out.println("****************创建成功");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	public static String getPassword(String username,Context context){
		String password = "";
		sqLiteDatabase = new DBConnection(context).getWritableDatabase();
		String[] USERSFROM = { UserSchema.ID,
				UserSchema.USERNAME, UserSchema.PASSWORD,
		};
		Cursor c = sqLiteDatabase
				.query(UserSchema.TABLE_NAME, USERSFROM,
						"username='"+username+"'", null, null,
						null, null);
		c.moveToFirst();
		if (c!=null) {
			password = c.getString(2);//获取密码
		}
		
		return password;
	}

}

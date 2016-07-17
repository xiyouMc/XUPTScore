package com.mc.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBConnection extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "users";// ��ݿ�
    private static final int DATABASE_VERSION = 2;
    static SQLiteDatabase sqLiteDatabase;

    public DBConnection(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }

    /**
     * ��ȡ����
     */
    public static String getPassword(String username, Context context) {
        String password = "";
        sqLiteDatabase = new DBConnection(context).getWritableDatabase();
        String[] USERSFROM = {UserSchema.ID, UserSchema.USERNAME,
                UserSchema.PASSWORD,};
        Cursor c = sqLiteDatabase.query(UserSchema.TABLE_NAME, USERSFROM,
                "username='" + username + "'", null, null, null, null);
        if (c.moveToFirst() != false) {
            password = c.getString(2);// ��ȡ����
        }
        c.close();
        return password;
    }

    /**
     * ��ȡͷ��
     */
    public static String getPhotoName(String username, Context context) {
        String photoname = "";
        sqLiteDatabase = new DBConnection(context).getWritableDatabase();
        String[] USERSFROM = {UserSchema.ID, UserSchema.USERNAME,
                UserSchema.PHOTONAME};
        Cursor c = sqLiteDatabase.query(UserSchema.USERPHOTO_TABLE, USERSFROM,
                "username='" + username + "'", null, null, null, null);
        if (c.moveToFirst() != false) {
            photoname = c.getString(2);// ��ȡͷ��
        }
        c.close();
        return photoname;
    }

    /**
     * ɾ���û�
     */
    public static void deleteUser(String username, Context context) {
        String password = "";
        sqLiteDatabase = new DBConnection(context).getWritableDatabase();
        String[] USERSFROM = {UserSchema.ID, UserSchema.USERNAME,
                UserSchema.PASSWORD,};
        sqLiteDatabase.delete(UserSchema.TABLE_NAME, "username='" + username
                + "'", null);
    }

    /**
     * �����û�����Ϊ��
     */
    public static void updateUser(String username, Context context) {
        String password = "";
        sqLiteDatabase = new DBConnection(context).getWritableDatabase();
        String[] USERSFROM = {UserSchema.ID, UserSchema.USERNAME,
                UserSchema.PASSWORD,};
        ContentValues values = new ContentValues();
        values.put("password", "");
        sqLiteDatabase.update(UserSchema.TABLE_NAME, values, "username='"
                + username + "'", null);
    }

    /**
     * �����û�ͷ��
     */
    public static void insertPhotoname(String username, String photoname,
                                       Context context) {
        sqLiteDatabase = new DBConnection(context).getWritableDatabase();
        String[] USERSFROM = {UserSchema.ID, UserSchema.USERNAME,
                UserSchema.PHOTONAME};
        ContentValues values = new ContentValues();
        values.put(USERSFROM[2], photoname);
        Cursor c = sqLiteDatabase.query(UserSchema.USERPHOTO_TABLE, USERSFROM,
                "username='" + username + "'", null, null, null, null);
        if (c.moveToFirst() == false) {
            values.put(USERSFROM[1], username);
            sqLiteDatabase.insert(UserSchema.USERPHOTO_TABLE, null, values);// ����
        } else
            sqLiteDatabase.update(UserSchema.USERPHOTO_TABLE, values,
                    "username='" + username + "'", null);
    }

    // ������
    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        String sql = "CREATE TABLE if not exists " + UserSchema.TABLE_NAME
                + " (" + UserSchema.ID
                + " INTEGER primary key autoincrement, " // ����
                + UserSchema.USERNAME + " text not null, "
                + UserSchema.PASSWORD + " text not null " + ");";
        db.execSQL(sql);
        db.execSQL("CREATE TABLE if not exists " + UserSchema.USERPHOTO_TABLE
                + " (" + UserSchema.ID + " INTEGER primary key autoincrement, " // ����
                + UserSchema.USERNAME + " text not null, "
                + UserSchema.PHOTONAME + " text not null " + ");");
        System.out.println("****************�����ɹ�");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        onCreate(db);
    }

    // ����γ̺ͽ��ҵı�
    public interface UserSchema {
        String USERPHOTO_TABLE = "userphoto";
        String TABLE_NAME = "Users";// ������֣������а˸����

        String ID = "_id";

        String USERNAME = "username";
        String PASSWORD = "password";
        String PHOTONAME = "photoname";
    }

    public interface LanguageSchema {
        String Language = "language";
    }
}

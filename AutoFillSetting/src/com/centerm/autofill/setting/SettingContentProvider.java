package com.centerm.autofill.setting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class SettingContentProvider extends ContentProvider {
	//Content provider URI
	public static final Uri CONTENT_URI = 
				Uri.parse( "content://com.centerm.autofill.settingprovider/settings");
	
	//�б������
	public static final String KEY_ID = "_id";		//������id
	public static final String KEY_NAME = "name";	//��������
	public static final String KEY_VALUE = "value";	//���õ�ֵ
	
	//�����ݿ�����
	private class SettingSQLiteOpenHelper extends SQLiteOpenHelper{
		
		//���ݿ����
		private static final String DATABASE_NAME = "autofillDatabase.db";	//���ݿ��ļ�
		private static final int DATABASE_VERSION = 1;						//�汾��
		private static final String DATABASE_TABLE = "Setting";				//���õı������
		
		//�������ݿ��
		private static final String SQL_DATABASE_CREATE = "create table " +
				DATABASE_TABLE + " ( " + KEY_ID + " integer primary key autoincrement, " +
				KEY_NAME + " text not null, " + KEY_VALUE + " text);";
		
		//private static final String SQL_INSERT_INTO = "insert into " +
		//		DATABASE_TABLE + " ( " + KEY_NAME + ", " + KEY_VALUE + " ) VALUES ("
		public SettingSQLiteOpenHelper(Context context, String name,
				CursorFactory factory, int version,
				DatabaseErrorHandler errorHandler) {
			super(context, name, factory, version, errorHandler);
		}

		public SettingSQLiteOpenHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			
			Log.e("con","cn is SQLiteDatabase");
			//�������ݿ��
			db.execSQL( SQL_DATABASE_CREATE );
			
			//����ԭʼ����
			HashMap<String, String> defConf = DefaultConfig.getDefConfig();
			Set<String> keys = defConf.keySet();
			Iterator<String> it = keys.iterator();
			//��������
			while( it.hasNext()){
				String key = it.next();
				
				//����
				ContentValues values = new ContentValues();
				values.put( KEY_NAME, key );
				values.put( KEY_VALUE, defConf.get( key ) );
				db.insert( DATABASE_TABLE, null, values);
			}			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL( "DROP TABLE IF EXISTS " + DATABASE_TABLE );
			onCreate( db );
		}
		
	}
	
	//type
	private static final int ALLROWS = 1;		//������
	private static final int SINGLE_ROW = 2;	//����
	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher( UriMatcher.NO_MATCH );
		uriMatcher.addURI( "com.centerm.autofill.settingprovider", "settings", ALLROWS );
		uriMatcher.addURI( "com.centerm.autofill.settingprovider", "settings/#", SINGLE_ROW );
	}
	
	//Provider���
	private SettingSQLiteOpenHelper sqliteHelper;	//���ݿ����	
	
	@Override
	public boolean onCreate() {
		sqliteHelper = new SettingSQLiteOpenHelper( getContext(), 
				SettingSQLiteOpenHelper.DATABASE_NAME, null, SettingSQLiteOpenHelper.DATABASE_VERSION);
		return true;
	}
	
	

	@Override
	public String getType(Uri uri) {
		switch( uriMatcher.match( uri )){
		case ALLROWS:return "vnd.android.cursor.dir/vnd.centerm.setting";
		case SINGLE_ROW:return "vnd.android.cursor.item/vnd.centerm.setting";
		default: throw new IllegalArgumentException( "Unsupported URI:" + uri );
		}
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, 
			String[] selectionArgs,	String sortOrder ) {
		//��ѯ
		SQLiteDatabase db = sqliteHelper.getWritableDatabase();
		
		String groupBy = null;
		String having = null;
		
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables( SettingSQLiteOpenHelper.DATABASE_TABLE );
		
		switch( uriMatcher.match( uri )){
		case SINGLE_ROW:
			String rowID = uri.getPathSegments().get(1);
			queryBuilder.appendWhere( KEY_ID + "=" + rowID );
			break;
		default:
			break;
		}
		Cursor cursor = queryBuilder.query( db, projection, selection, 
				selectionArgs, groupBy, having, sortOrder );
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = sqliteHelper.getWritableDatabase();
		
		//���ǵ��У��ͼ���idƥ��
		switch( uriMatcher.match( uri )){
		case SINGLE_ROW:
			String rowID = uri.getPathSegments().get(1);
			selection = KEY_ID + "=" + rowID + (!TextUtils.isEmpty( selection) ?
					" AND (" + selection + ')' : "");
			break;
		default:
			break;
		}
		//update��֪ͨ���м����������Ѹı�
		int updateCount = db.update( SettingSQLiteOpenHelper.DATABASE_TABLE, 
				values, selection , selectionArgs );
		getContext().getContentResolver().notifyChange( uri, null );
		return updateCount;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = sqliteHelper.getWritableDatabase();
		String nullColumnHack = null;
		
		//����
		long id = db.insert( SettingSQLiteOpenHelper.DATABASE_TABLE, 
				nullColumnHack, values );
		
		//����uri����
		if( id > -1 ){
			Uri insertedId = ContentUris.withAppendedId(CONTENT_URI, id);
			getContext().getContentResolver().notifyChange( insertedId, null );
			return insertedId;
		}
		else
			return null;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = sqliteHelper.getWritableDatabase();
		
		//���ǵ��У��ͼ���idƥ��
		switch( uriMatcher.match( uri )){
		case SINGLE_ROW:
			String rowID = uri.getPathSegments().get(1);
			selection = KEY_ID + "=" + rowID + (!TextUtils.isEmpty( selection) ?
					" AND (" + selection + ')' : "");
			break;
		default:
			break;
		}
		
		if( selection == null )
			selection = "1";
		
		int deletecnt = db.delete( SettingSQLiteOpenHelper.DATABASE_TABLE, 
				selection, selectionArgs );
		
		getContext().getContentResolver().notifyChange( uri, null );
		return deletecnt;
	}
}

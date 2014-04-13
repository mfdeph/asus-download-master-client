package com.insolence.admclient.storage;

import java.util.ArrayList;
import java.util.List;

import com.insolence.admclient.StaticContextApp;
import com.insolence.admclient.entity.DownloadItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DownloadItemStorage {

	private static DownloadItemStorage _instance;
	
	public static DownloadItemStorage getInstance(){
		return getInstance(StaticContextApp.getContext());
	}
	
	public static DownloadItemStorage getInstance(Context context){
		if (_instance == null)
			_instance = new DownloadItemStorage(context);
		return _instance;
	}
	
	private DownloadItemStorageOpenHelper dbHelper;
	
	private DownloadItemStorage(Context context){
		dbHelper = new DownloadItemStorageOpenHelper(context);
	}
	
	public DownloadItemStorage(Context context, boolean clearStorage){
		this(context);
		if (clearStorage)
			clearStorage();
	}
	
	public List<DownloadItem> getDownloadItems(){
		List<DownloadItem> items = new ArrayList<DownloadItem>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(
				DownloadItemStorageOpenHelper.DOWNLOAD_ITEMS_TABLE_NAME, 
				dbHelper.EVENTS_ALL_COLUMNS, 
				null, null, null, null,
				DownloadItemStorageOpenHelper.DOWNLOAD_ITEM_ID);
		while (cursor.moveToNext()){
			DownloadItem item = new DownloadItem(
					cursor.getString(cursor.getColumnIndex(DownloadItemStorageOpenHelper.DOWNLOAD_ITEM_ID)),
					cursor.getString(cursor.getColumnIndex(DownloadItemStorageOpenHelper.DOWNLOAD_ITEM_NAME)),
					cursor.getFloat(cursor.getColumnIndex(DownloadItemStorageOpenHelper.DOWNLOAD_ITEM_PERCENTAGE)),
					cursor.getString(cursor.getColumnIndex(DownloadItemStorageOpenHelper.DOWNLOAD_ITEM_VOLUME)),
					cursor.getString(cursor.getColumnIndex(DownloadItemStorageOpenHelper.DOWNLOAD_ITEM_STATUS)),
					cursor.getString(cursor.getColumnIndex(DownloadItemStorageOpenHelper.DOWNLOAD_ITEM_TYPE)),
					cursor.getString(cursor.getColumnIndex(DownloadItemStorageOpenHelper.DOWNLOAD_ITEM_UPTIME)),
					cursor.getString(cursor.getColumnIndex(DownloadItemStorageOpenHelper.DOWNLOAD_ITEM_UPLOAD_SPEED)),
					cursor.getString(cursor.getColumnIndex(DownloadItemStorageOpenHelper.DOWNLOAD_ITEM_DOWNLOAD_SPEED)),
					cursor.getString(cursor.getColumnIndex(DownloadItemStorageOpenHelper.DOWNLOAD_ITEM_SEEDS)),
					cursor.getString(cursor.getColumnIndex(DownloadItemStorageOpenHelper.DOWNLOAD_ITEM_ADDITIONAL_INFO))
			);
			items.add(item);			
		}
        db.close();
        return items;
	}
	
	public void saveDownloadItem(DownloadItem item){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        
        cv.put(DownloadItemStorageOpenHelper.DOWNLOAD_ITEM_ID, item.getId());
        cv.put(DownloadItemStorageOpenHelper.DOWNLOAD_ITEM_NAME, item.getName());
        cv.put(DownloadItemStorageOpenHelper.DOWNLOAD_ITEM_PERCENTAGE, item.getPercentage());
        cv.put(DownloadItemStorageOpenHelper.DOWNLOAD_ITEM_VOLUME, item.getVolume());
        cv.put(DownloadItemStorageOpenHelper.DOWNLOAD_ITEM_STATUS, item.getStatus());
        cv.put(DownloadItemStorageOpenHelper.DOWNLOAD_ITEM_TYPE, item.getType());
        cv.put(DownloadItemStorageOpenHelper.DOWNLOAD_ITEM_UPTIME, item.getTimeOnLine());
        cv.put(DownloadItemStorageOpenHelper.DOWNLOAD_ITEM_UPLOAD_SPEED, item.getUpSpeed());
        cv.put(DownloadItemStorageOpenHelper.DOWNLOAD_ITEM_DOWNLOAD_SPEED, item.getDownSpeed());
        cv.put(DownloadItemStorageOpenHelper.DOWNLOAD_ITEM_SEEDS, item.getSeeds());
        cv.put(DownloadItemStorageOpenHelper.DOWNLOAD_ITEM_ADDITIONAL_INFO, item.getAddInfo());
        
        db.replace(DownloadItemStorageOpenHelper.DOWNLOAD_ITEMS_TABLE_NAME, null, cv);
        
        db.close();
	}
	
	private String getSavedStatus(DownloadItem item){		
		String result = null;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(
				DownloadItemStorageOpenHelper.DOWNLOAD_ITEMS_TABLE_NAME, 
				new String[]{DownloadItemStorageOpenHelper.DOWNLOAD_ITEM_STATUS}, 
				DownloadItemStorageOpenHelper.DOWNLOAD_ITEM_ID + " = ? AND " + DownloadItemStorageOpenHelper.DOWNLOAD_ITEM_NAME + " = ?", 
				new String[] {item.getId(), item.getName()}, null, null,
				DownloadItemStorageOpenHelper.DOWNLOAD_ITEM_ID, 
				"1");
		
		if (cursor.moveToNext()){
			result = cursor.getString(cursor.getColumnIndex(DownloadItemStorageOpenHelper.DOWNLOAD_ITEM_STATUS));
		}
		db.close();		
		return result;
	}
	
	public void saveDownloadItems(List<DownloadItem> downloadItems, IOnDownloadStatusChangedListener listener){
		if (listener != null){
			for(DownloadItem item : downloadItems){
				String savedStatus = getSavedStatus(item);
				if (!item.getStatus().equals(savedStatus))
					listener.onDownloadStatusChanged(item, savedStatus);
			}
		}
		clearStorage();
		for (DownloadItem item : downloadItems)
			saveDownloadItem(item);
	}
	
	public void clearStorage(){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete(DownloadItemStorageOpenHelper.DOWNLOAD_ITEMS_TABLE_NAME, null, null);
		db.close();
	}
	
	public interface IOnDownloadStatusChangedListener{
		void onDownloadStatusChanged(DownloadItem item, String previousStatus);
	}
	
	private class DownloadItemStorageOpenHelper extends SQLiteOpenHelper{

		  private static final int DB_VERSION = 1;
		  
		  private static final String DB_NAME = "download_items";

		  public static final String DOWNLOAD_ITEMS_TABLE_NAME = "download_items";
		  
		  public static final String DOWNLOAD_ITEM_ID = "id";
		  public static final String DOWNLOAD_ITEM_NAME = "name";
		  public static final String DOWNLOAD_ITEM_PERCENTAGE = "percentage";
		  public static final String DOWNLOAD_ITEM_VOLUME = "volume";
		  public static final String DOWNLOAD_ITEM_STATUS = "status";
		  public static final String DOWNLOAD_ITEM_TYPE = "type";
		  public static final String DOWNLOAD_ITEM_UPTIME = "uptime";
		  public static final String DOWNLOAD_ITEM_UPLOAD_SPEED = "upload_speed";
		  public static final String DOWNLOAD_ITEM_DOWNLOAD_SPEED = "download_speed";
		  public static final String DOWNLOAD_ITEM_SEEDS = "seeds";
		  public static final String DOWNLOAD_ITEM_ADDITIONAL_INFO = "additional_info";
		  
		  public final String[] EVENTS_ALL_COLUMNS = {
				  DOWNLOAD_ITEM_ID, 
				  DOWNLOAD_ITEM_NAME, 
				  DOWNLOAD_ITEM_PERCENTAGE, 
				  DOWNLOAD_ITEM_VOLUME, 
				  DOWNLOAD_ITEM_STATUS, 
				  DOWNLOAD_ITEM_TYPE, 
				  DOWNLOAD_ITEM_UPTIME,
				  DOWNLOAD_ITEM_UPLOAD_SPEED,
				  DOWNLOAD_ITEM_DOWNLOAD_SPEED,
				  DOWNLOAD_ITEM_SEEDS,
				  DOWNLOAD_ITEM_ADDITIONAL_INFO
		  };

		  private static final String CREATE_TABLE = 
			 "create table " + DOWNLOAD_ITEMS_TABLE_NAME + " ( "+ 
					 DOWNLOAD_ITEM_ID +" INTEGER, " +
					 DOWNLOAD_ITEM_NAME + " TEXT, " +
					 DOWNLOAD_ITEM_PERCENTAGE + " REAL, " +
					 DOWNLOAD_ITEM_VOLUME + " TEXT, " +
					 DOWNLOAD_ITEM_STATUS + " TEXT, " + 
					 DOWNLOAD_ITEM_TYPE + " TEXT, " +
					 DOWNLOAD_ITEM_UPTIME + " TEXT, " +
					 DOWNLOAD_ITEM_UPLOAD_SPEED + " TEXT, " +
					 DOWNLOAD_ITEM_DOWNLOAD_SPEED + " TEXT, " +
					 DOWNLOAD_ITEM_SEEDS + " TEXT, " +
					 DOWNLOAD_ITEM_ADDITIONAL_INFO + " TEXT, " +
					 "primary key (" + DOWNLOAD_ITEM_ID + ", " + DOWNLOAD_ITEM_NAME + ")" +
			")";

		  public DownloadItemStorageOpenHelper(Context context) {
		    super(context, DB_NAME, null, DB_VERSION);
		  }

		  @Override
		  public void onCreate(SQLiteDatabase sqLiteDatabase) {
		    sqLiteDatabase.execSQL(CREATE_TABLE);
		  }

		  @Override
		  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
		  }
	}
	
}

package com.insolence.admclient.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class UriUtil {
	
	private Context _context;
	public UriUtil(Context context){
		_context = context;
	}
	
	public String getUriFileName(Uri uri){
		String scheme = uri.getScheme();
		if (scheme.equals("content")) {
		    String[] proj = { MediaStore.Images.Media.TITLE };
		    Cursor cursor = _context.getContentResolver().query(uri, proj, null, null, null);
		    if (cursor != null && cursor.getCount() != 0) {
		        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
		        cursor.moveToFirst();
		        return cursor.getString(columnIndex);
		    }
		}
		return uri.getLastPathSegment();
	}
	
}

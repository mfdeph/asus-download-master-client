package com.insolence.admclient.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class FriendlyNameUtil {
	
	private Context _context;
	public FriendlyNameUtil(Context context){
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
		        String result = cursor.getString(columnIndex);
		        if (result != null)
		        	return result;
		    }
		}
		String result =  uri.getLastPathSegment();
		if (!result.contains("."))
			return new RandomGuid().toString(16) + ".torrent";
		return result;
	}
	
	public static String GetNativeFileNameFromMagnetLink(String magnetLink){
		try {
			String[] splitted = magnetLink.split("&dn=");
			if (splitted.length < 2)
				return "";
			return "\"" + URLDecoder.decode(splitted[1].split("&")[0], "UTF-8") + "\"";
		} 
		catch (UnsupportedEncodingException e) {
			
		}
		return "";
	}
	
}

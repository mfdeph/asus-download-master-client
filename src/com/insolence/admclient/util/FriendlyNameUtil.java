package com.insolence.admclient.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

public class FriendlyNameUtil {
	
	private Context _context;
	public FriendlyNameUtil(Context context){
		_context = context;
	}
	
	public String getUriFileName(Uri uri){
		
		String result = null;
		
		try{
			String scheme = uri.getScheme();
			if (scheme.equals("content")) {
			    String[] proj = { MediaStore.Images.Media.TITLE };
			    Cursor cursor = _context.getContentResolver().query(uri, proj, null, null, null);
			    if (cursor != null && cursor.getCount() != 0) {
			        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
			        cursor.moveToFirst();
			        result = cursor.getString(columnIndex);
			    }
			}
		}catch(Exception e){
			
		}
		//if there's no file name in context title try to parse url string
		if (result == null || result.isEmpty())
			result = uri.getLastPathSegment();
		//if there's still nothing generate random guid
		if (result == null || result.isEmpty())
			result = new RandomGuid().toString(16);
		//if there's no valid extension in file name try to resolve extension manually
		if (!(result.toLowerCase().endsWith(".torrent") || result.toLowerCase().endsWith(".nzb"))){
			String mimeType = _context.getContentResolver().getType(uri);
			String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
			if (extension == null)
				extension = mimeType.contains("-") ? mimeType.split("-")[1] : "bin";
			if (!result.toLowerCase().endsWith(extension.toLowerCase()))
				result = result + "." + extension;
		}
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

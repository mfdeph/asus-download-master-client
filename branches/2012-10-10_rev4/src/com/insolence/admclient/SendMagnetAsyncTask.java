package com.insolence.admclient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;

import com.insolence.admclient.util.RandomGuid;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.Toast;

public class SendMagnetAsyncTask extends AsyncTask<Void, Void, Void>{
	protected ListActivity _listActivity;
	protected String _magnetLink;
	
	public SendMagnetAsyncTask(ListActivity listActivity, String magnetLink){
		_listActivity = listActivity;
		_magnetLink = magnetLink;
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
	
	
	private String getTorrentIdFromMagnetLink(String magnetLink){
		return magnetLink.split("urn:btih:")[1].split("&")[0].toUpperCase();
	}
	
	public static String GetValidFileName(){
		return new RandomGuid().toString() + ".torrent";
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		File torrent = GetTorrentFileFromMagnetLink(_magnetLink);
		if (torrent != null){
			DownloadItemListManager.getInstance().SendFile(torrent);
			DownloadItemListManager.getInstance().getDownloadItems(true);
		}else{
			Toast.makeText(
					_listActivity,
 				   "Failed to get Magnet link info", Toast.LENGTH_SHORT).show();
		}
		return null;
	}

	
	private static List<String> _torrentCahceProviders;
	
	private static List<String> getTorrentCacheProviders(){
		if (_torrentCahceProviders == null){
			_torrentCahceProviders = new ArrayList<String>();
			_torrentCahceProviders.add("http://torcache.net/torrent/%s.torrent");
			_torrentCahceProviders.add("http://torrage.com/torrent/%s.torrent");
		}
		return _torrentCahceProviders;
	}
	
	
	private File TryGetTorrent(String requestPath){
		
		BufferedOutputStream bos = null;
		File torrentFile = null;
		
		try{
			
			torrentFile = new File(_listActivity.getCacheDir(), GetValidFileName());
			if (torrentFile.exists()) {
				torrentFile.delete();
			}
			
			URL url = new URL(requestPath);
		    URLConnection con = (HttpURLConnection) url.openConnection();	    
		    InputStream input = con.getInputStream();
		    
    		if (input != null) {			
    			torrentFile.createNewFile();		
				bos = new BufferedOutputStream(new FileOutputStream(torrentFile));			
	            byte buf[] = new byte[1024];
	            int len = 0;
	            
	            boolean isFirstPart = true;
	            
	            while ((len = input.read(buf)) != -1) {
	            	
	            	if (isFirstPart){
	            		String data = EncodingUtils.getString(buf, "UTF-8");
	            		if (!data.startsWith("d8:")){
	            			bos.close();
	            			torrentFile.delete();
	            			return null;
	            		}
	            	}
	            	
	                bos.write(buf, 0, len);
	                isFirstPart = false;
	            }
	            bos.flush();
			}
		} catch (Exception e) {
			torrentFile.delete();
			return null;
		}	
		
		return torrentFile;
	}


	private File GetTorrentFileFromMagnetLink(String magnetLink){
		File torrentFile = null;
		
		for (String provider : getTorrentCacheProviders()) {
			torrentFile = TryGetTorrent(String.format(provider, getTorrentIdFromMagnetLink(magnetLink)));
			if (torrentFile != null)
				return torrentFile;
		}
		
		return null;
	}	

	
}
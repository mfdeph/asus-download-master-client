package com.insolence.admclient.asynctasks;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.util.EncodingUtils;

import android.content.Context;

import com.insolence.admclient.util.RandomGuid;

@Deprecated
public class SendMagnetTask extends SendFileTaskBase {

	String _magnetLink;
	File _cacheDirectory;
	
	public SendMagnetTask(Context context, String magnetLink, File cacheDirectory) {
		super(context);
		_magnetLink = magnetLink;
		_cacheDirectory = cacheDirectory;
		fileIsNullExceptionText = "Failed to get magnet link data";
	}

	@Override
	protected File getFile() {
		return GetTorrentFileFromMagnetLink(_magnetLink);
	}
	
	private String getTorrentIdFromMagnetLink(String magnetLink){
		return magnetLink.split("urn:btih:")[1].split("&")[0].toUpperCase();
	}
	
	public static String GetValidFileName(){
		return new RandomGuid().toString() + ".torrent";
	}
	
	private static List<String> _torrentCahceProviders;
	
	private static List<String> getTorrentCacheProviders(){
		if (_torrentCahceProviders == null){
			_torrentCahceProviders = new ArrayList<String>();
			_torrentCahceProviders.add("http://torcache.net/torrent/%s.torrent");
			_torrentCahceProviders.add("http://torrage.com/torrent/%s.torrent");
			_torrentCahceProviders.add("http://zoink.it/torrent/%s.torrent");
			_torrentCahceProviders.add("http://torrage.ws/torrent/%s.torrent");
		}
		return _torrentCahceProviders;
	}
	
	private File TryGetTorrent(String requestPath){
		
		BufferedOutputStream bos = null;
		File torrentFile = null;
		
		try{
			
			torrentFile = new File(_cacheDirectory, GetValidFileName());
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

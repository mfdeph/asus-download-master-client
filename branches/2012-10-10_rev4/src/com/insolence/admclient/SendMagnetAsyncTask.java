package com.insolence.admclient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
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

import android.app.ListActivity;
import android.os.AsyncTask;

public class SendMagnetAsyncTask extends AsyncTask<Void, Void, Void>{
	protected ListActivity _listActivity;
	protected String _magnetLink;
	
	public SendMagnetAsyncTask(ListActivity listActivity, String magnetLink){
		_listActivity = listActivity;
		_magnetLink = magnetLink;
	}
	
	public static String GetFileNameFromMagnetLink(String magnetLink){
		try {
			return URLDecoder.decode(magnetLink.split("&dn=")[1].split("&")[0], "UTF-8");
		} 
		catch (UnsupportedEncodingException e) {
			
		}
		return "";
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		File torrent = GetTorrentFileFromMagnetLink(_magnetLink);
		if (torrent != null)
			DownloadItemListManager.getInstance().SendFile(torrent);
		DownloadItemListManager.getInstance().getDownloadItems(true);
		return null;
	}

	private static final String MAGNET_CONVERSION_URL = "http://magnet2torrent.com/upload/";
	
	private File GetTorrentFileFromMagnetLink(String magnetLink){
			File file = null;
			HttpClient httpclient = null;
			try {
				file = new File(_listActivity.getCacheDir(), GetFileNameFromMagnetLink(magnetLink));
				if (file.exists()) {
					file.delete();
				}		
				String charset = "UTF-8";
				httpclient = new DefaultHttpClient();
			    HttpPost httppost = new HttpPost(MAGNET_CONVERSION_URL);
			    BufferedInputStream bis = null;
			    BufferedOutputStream bos = null;
			    try {
			        // Add your data
			        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			        nameValuePairs.add(new BasicNameValuePair("magnet", magnetLink));
			        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, charset));

			        // Execute HTTP Post Request
			        HttpResponse response = httpclient.execute(httppost);
			        HttpEntity entity = response.getEntity();
			        bis = new BufferedInputStream(entity.getContent());
		    		if (bis != null) {
						file.createNewFile();
						
						bos = new BufferedOutputStream(new FileOutputStream(file));
						
			            byte buf[] = new byte[1024];
			            int len = 0;
			            while ((len = bis.read(buf)) != -1) {
			                bos.write(buf, 0, len);
			            }
			            bos.flush();
					}
			    }
			    finally {
			    	if (bos != null) {
			    		bos.close();
			    	}
			    	if (bis != null) {
			    		bis.close();
			    	}
				}			
			}
			catch (Exception e) {
				file = null;
			}
			finally {
				if (httpclient != null) {
					httpclient.getConnectionManager().shutdown();
				}
			}
			
			return file;
	}
	
	
	
}
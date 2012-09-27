package com.insolence.admclient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.insolence.admclient.util.RandomGuid;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

public class SendFileAsyncTask extends AsyncTask<Bundle, Void, Boolean> {
	
	private static final String MAGNET_CONVERSION_URL = "http://magnet2torrent.com/upload/";
	protected SherlockFragmentActivity _listActivity;
	
	public SendFileAsyncTask(SherlockFragmentActivity listActivity) {
		_listActivity = listActivity;
	}
	
	private String uploadFileUrlString() {
		return "http://" + DownloadItemListActivity._connectionString + "/dm_uploadbt.cgi";
	}
	
	private File downloadTorrentFile(String magnetLink, String fileName) {
		File file = null;
		HttpClient httpclient = null;
		try {
			file = new File(_listActivity.getCacheDir(), fileName);
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
	
	private boolean sendFile(File file) {
		int respCode = 0;
		HttpURLConnection con = null;
		try {		
			String newLine = "\r\n";
			String boundary = "---------------------------" + new RandomGuid().toString(13);
		    
		    int bufferSize;
		    int maxBufferSize = 4096;
		    int bytesRead;
		    
		    con = (HttpURLConnection) new URL(uploadFileUrlString()).openConnection();
		    con.setConnectTimeout(30000);
		    con.setDoInput(true);
		    con.setDoOutput(true);
		    con.setUseCaches(false);
		    con.setRequestMethod("POST");
		    con.setRequestProperty("Connection", "Keep-Alive");
		    con.setRequestProperty("Authorization", "Basic " + Base64.encodeToString((DownloadItemListActivity._userName + ":" + DownloadItemListActivity._password).getBytes(), Base64.DEFAULT).trim());
		    con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
		    DataOutputStream dos = new DataOutputStream(con.getOutputStream());

		    dos.writeBytes("--" + boundary + newLine);
		    dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""
		        + file.getName() + "\"" + newLine);
		    dos.writeBytes("Content-Type: application/x-bittorrent" + newLine + newLine);
		    bufferSize = maxBufferSize;
		    byte[] buffer = new byte[bufferSize];
		    FileInputStream fis = new FileInputStream(file.getAbsolutePath());
		    
		    while ((bytesRead = fis.read(buffer)) > -1)
		        dos.write(buffer, 0, bytesRead);
		    
		    dos.writeBytes(newLine);
		    dos.writeBytes("--" + boundary + "--" + newLine);

		    dos.flush();
		    dos.close();
		    fis.close();
		    
		    respCode = con.getResponseCode(); 
		}
		catch (Exception e) {
			respCode = -1;
		}
		finally {
			if (con != null) {
				con.disconnect();
			}
		}
		
		if (respCode == HttpURLConnection.HTTP_OK) {
			return true;
		}
		else {
			return false;
		}
	}
	
	@Override
	protected Boolean doInBackground(Bundle... args) {
		
		if (args[0].getString(ConfirmDialogFragment.SCHEME).equals("magnet")) {
			File file = downloadTorrentFile(args[0].getString(ConfirmDialogFragment.MAGNET_LINK), args[0].getString(ConfirmDialogFragment.FILE_NAME));
			if (file != null) {
				return sendFile(file);
			}
			else {
				return false;
			}
		}
		else {
			Uri uri = Uri.parse(args[0].getString(ConfirmDialogFragment.URI_STRING));
			File file = new File(uri.getPath());
			return sendFile(file);
		}	
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (result && _listActivity != null) {
			((DownloadItemListActivity) _listActivity).recreateDownloadItemLoader();	
		}
		else if (_listActivity != null) {
			Toast.makeText(_listActivity, "Command failed!", Toast.LENGTH_LONG).show();
		}
	}
}
package com.insolence.admclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.os.AsyncTask;
import android.util.Base64;
import android.widget.Toast;

public class SendCommandAsyncTask extends AsyncTask<String, Void, Boolean> {

	protected SherlockFragmentActivity _listActivity;
	
	public SendCommandAsyncTask(SherlockFragmentActivity listActivity) {
		_listActivity = listActivity;
	}
	
	private String sendGroupCommandUrlString() {
		return "http://" + DownloadItemListActivity._connectionString + "/dm_apply.cgi?action_mode=DM_CTRL&dm_ctrl=%s&download_type=ALL";
	}
	
	private boolean sendGroupCommand(String command) {
		Exception sendException = null;
		String result = "";
		try {			
			URL url = new URL(String.format(sendGroupCommandUrlString(), command));
			URLConnection con = (HttpURLConnection) url.openConnection();
			con.setConnectTimeout(30000);
		    con.addRequestProperty("Authorization", "Basic " + Base64.encodeToString((DownloadItemListActivity._userName + ":" + DownloadItemListActivity._password).getBytes(), Base64.DEFAULT).trim());
		    InputStream stream = con.getInputStream();
		    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
			String temp;
			
			while ((temp = bufferedReader.readLine()) != null) {
				result += temp;
			}
			bufferedReader.close();
			stream.close();
		} 
		catch (MalformedURLException e) {
			sendException = e;
		}
		catch (SocketTimeoutException e) {
			sendException = e;
		}
		catch (IOException e) {
			sendException = e;
		}
		
		if (sendException == null && result.contains("ACK_SUCESS")) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private String sendCommandUrlString() {
		return "http://" + DownloadItemListActivity._connectionString + "/dm_apply.cgi?action_mode=DM_CTRL&dm_ctrl=%s&task_id=%s&download_type=BT";
	}
	
	private boolean sendCommand(String command, String id) {
		Exception sendException = null;
		String result = "";
		try{	
			URL url = new URL(String.format(sendCommandUrlString(), command, id));
			URLConnection con = (HttpURLConnection) url.openConnection();
			con.setConnectTimeout(30000);
		    con.addRequestProperty("Authorization", "Basic " + Base64.encodeToString((DownloadItemListActivity._userName + ":" + DownloadItemListActivity._password).getBytes(), Base64.DEFAULT).trim());
		    InputStream stream = con.getInputStream();
		    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
			String temp;
			
			while ((temp = bufferedReader.readLine()) != null) {
				result += temp;
			}
			bufferedReader.close();
			stream.close();
		}
		catch (MalformedURLException e) {
			sendException = e;
		}
		catch (SocketTimeoutException e) {
			sendException = e;
		}
		catch (IOException e) {
			sendException = e;
		}
		
		if (sendException == null && result.contains("ACK_SUCESS")) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	protected Boolean doInBackground(String... params) {
		String _id = params[0];
		String _command = params[1];
						
		if (_id == null) {
			return sendGroupCommand(_command);
		}
		else {
			return sendCommand(_command, _id);
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
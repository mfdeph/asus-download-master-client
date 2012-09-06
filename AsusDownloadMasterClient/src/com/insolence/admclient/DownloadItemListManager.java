package com.insolence.admclient;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.insolence.admclient.util.RandomGuid;

import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Base64;

public class DownloadItemListManager {
	
	//http://192.168.1.1:8081/dm_print_status.cgi?action_mode=All
	
	public static DownloadItemListManager _instance;
	
	public static DownloadItemListManager getInstance(){
		if (_instance == null)
			_instance = new DownloadItemListManager();
		return _instance;
	}
	
	private DownloadItemListManager(){
	}
	
	
	public static void SetPrefs(SharedPreferences prefs){
		_connectionString = prefs.getString("webServerAddrPref", "192.168.1.1") + ":" + prefs.getString("webServerPortPref", "8081");
		_userName = prefs.getString("loginPref", "admin");
		_password = prefs.getString("passwordPref", "admin");
		
	}
	private static String _connectionString;
	private static String _userName;
	private static String _password;
	
	
	private String getListUrlString(){
		return "http://" + _connectionString + "/dm_print_status.cgi?action_mode=All";
	}
	
	private String sendCommandtUrlString(){
		return "http://" + _connectionString + "/dm_apply.cgi?action_mode=DM_CTRL&dm_ctrl=%s&task_id=%s&download_type=BT";
	}
	private String uploadFileUrlString(){
		return "http://" + _connectionString + "/dm_uploadbt.cgi";
	}
	 
	    
	private void loadDownloadItemList(){
		

		
		if (!_isUpdateAvailable)
			return;
		_isUpdateAvailable = false;
		
		try{
		
			URL url = new URL(getListUrlString());
		    URLConnection con = (HttpURLConnection) url.openConnection();	    
		    con.addRequestProperty("Authorization", "Basic " + Base64.encodeToString((_userName + ":" + _password).getBytes(), Base64.DEFAULT).trim());		
			InputStream stream = con.getInputStream();
		    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
		    String temp;
		    String result = "";
		    while ((temp = bufferedReader.readLine()) != null)
		    	result += temp;
		    	
		    FillDownloadItems(result);
        
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			setDelayForNextUpdate();
		}	
		

	}
	
	private ArrayList<DownloadItem> downloadItems;
	
	public ArrayList<DownloadItem> getDownloadItems(boolean forceLoad){
		if (downloadItems == null || forceLoad)
			loadDownloadItemList();
		if (downloadItems == null)
			downloadItems = new ArrayList<DownloadItem>();
		return downloadItems;		
	}

	
	public void SendCommand(String command, String id){
		try{
			
			URL url = new URL(String.format(sendCommandtUrlString(), command, id));
		    URLConnection con = (HttpURLConnection) url.openConnection();	    
		    con.addRequestProperty("Authorization", "Basic " + Base64.encodeToString((_userName + ":" + _password).getBytes(), Base64.DEFAULT).trim());
			InputStream stream = con.getInputStream();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
		}			
	}
	

	
	public void SendFile(File file){
		try {		
			String newLine = "\r\n";
			String boundary = "---------------------------" + new RandomGuid().toString(13);
		    
		    int bufferSize;
		    int maxBufferSize = 4096;
		    int bytesRead;
		    
		    HttpURLConnection con = (HttpURLConnection) new URL(uploadFileUrlString()).openConnection();
		    con.setDoInput(true);
		    con.setDoOutput(true);
		    con.setUseCaches(false);
		    con.setRequestMethod("POST");
		    con.setRequestProperty("Connection", "Keep-Alive");
		    con.setRequestProperty("Authorization", "Basic " + Base64.encodeToString((_userName + ":" + _password).getBytes(), Base64.DEFAULT).trim());
		    con.setRequestProperty("Content-Type",
		        "multipart/form-data;boundary=" + boundary);
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
		    int respCode = con.getResponseCode(); 
		    if (respCode == 1)
		    	return;

		} catch (Exception e) {
		    e.printStackTrace();
		}

	}
	
	private void FillDownloadItems(String data){
		
		ArrayList<DownloadItem> result = new ArrayList<DownloadItem>();
		
		Matcher m = Pattern.compile("\\[([^\\[]*)\\]").matcher(data);
    	while (m.find()){
    	    String res = m.toMatchResult().group(1);
    	    Matcher m2 = Pattern.compile("\"([^\"]*)\"").matcher(res);
    	    
    	    int counter = 0;
    	    DownloadItem item = new DownloadItem();
    	    
    	    while (m2.find()){
    	    	String res2 = m2.toMatchResult().group(1);
    	    	
    	    	switch (counter){
    	    		case 0:
    	    			item.setId(res2);
    	    			break;
	    	    	case 1:
	    	    		item.setName(res2);
	    	    		break;
	    	    	case 2:
	    	    		if (res2.trim().equalsIgnoreCase(""))
	    	    			item.setPercentage(0);
	    	    		else
	    	    			item.setPercentage(Float.parseFloat(res2));
	    	    		break;
	    	    	case 3:
	    	    		item.setVolume(res2);
	    	    		break;
	    	    	case 4:
	    	    		item.setStatus(res2);
	    	    		break;
	    	    	case 5:
	    	    		item.setType(res2);
	    	    		break;
	    	    	case 6:
	    	    		item.setTimeOnLine(res2);
	    	    		break;
	    	    	case 7:
	    	    		item.setUpSpeed(res2);
	    	    		break;
	    	    	case 8:
	    	    		item.setDownSpeed(res2);
	    	    		break;
	    	    	case 9:
	    	    		item.setSeeds(res2);
	    	    		break;
	    	    	case 10:
	    	    		item.setAddInfo(res2);
	    	    		break;
    	    	}
    	    	counter ++;
    	    }
    	    result.add(item);	
    	}
    	
    	if (result.size() > 0){
    		downloadItems = result;
    	}
    	
	}
	
	//механизм, позволяющий странице перерисовываться не чаще чем раз в 5 секунд
	private boolean _isUpdateAvailable = true;
	
	private void setDelayForNextUpdate(){
		_isUpdateAvailable = true;
		/*new Handler().postDelayed(new Runnable() {
			   public void run() {
				   _isUpdateAvailable = true;
			   }
			}, 5000);*/
	}
	
}

package com.insolence.admclient.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.insolence.admclient.entity.DownloadItem;
import com.insolence.admclient.storage.PreferenceAccessor;
import com.insolence.admclient.util.Holder;
import com.insolence.admclient.util.RandomGuid;

import android.content.Context;
import android.util.Base64;

public class DownloadMasterNetworkDalc {
	
	private final PreferenceAccessor _preferences;
	
	public DownloadMasterNetworkDalc(PreferenceAccessor preferences){
		_preferences = preferences;
	}
	
	public DownloadMasterNetworkDalc(Context context){
		_preferences = PreferenceAccessor.getInstance(context);
	}
	
	private String _connectionString;
	protected String getConnectionString(){
		if (_connectionString == null){
			_connectionString = _preferences.getWebServerAddress() + ":" + _preferences.getWebServerPort();
			if (_preferences.isPathPostfixEnabled())
				_connectionString = _connectionString + "/downloadmaster";
		}
		return _connectionString;
	}
	
	private String _authorizationString;
	protected String getAuthorizationString(){
		if (_authorizationString == null){
			_authorizationString = "Basic " + Base64.encodeToString((_preferences.getLogin() + ":" + _preferences.getPassword()).getBytes(), Base64.DEFAULT).trim();
		}
		return _authorizationString;
	}
		
	private String getListUrlString(){
		return "http://" + getConnectionString() + "/dm_print_status.cgi?action_mode=All";
	}
	
	private String sendCommandUrlString(){
		return "http://" + getConnectionString() + "/dm_apply.cgi?action_mode=DM_CTRL&dm_ctrl=%s&task_id=%s&download_type=BT";
	}
	
	private String sendGroupCommandUrlString(){
		return "http://" + getConnectionString() + "/dm_apply.cgi?action_mode=DM_CTRL&dm_ctrl=%s&download_type=ALL";
	}
	
	private String uploadFileUrlString(){
		return "http://" + getConnectionString() + "/dm_uploadbt.cgi";
	}

	
	protected boolean tryGetItemList(Holder<String> result){
		
		/*final Random rand = new Random();
		
		try{ Thread.sleep(rand.nextInt(3000)); }catch(InterruptedException e){ }
		
		result.value = 
				"[" + 
				"[\"1\",\"Hunger_Games'_BDRIP\",\"" + String.format("%.2f", rand.nextFloat()).replace(',', '.') + "\",\"100GB\",\"" + (rand.nextBoolean() ? "Downloading" : "Seeding") + "\",\"\",\"100500 hrs\",\"100 mbps\",\"200 mbps\",\"10\",\"11\"]" +
				"[\"2\",\"HOT_CHICK_ASSES_IN_IBIZA_5'_BDRIP.avi\",\"" + String.format("%.2f", rand.nextFloat()).replace(',', '.') + "\",\"100GB\",\"" + (rand.nextBoolean() ? "Downloading" : "Seeding") + "\",\"\",\"100500 hrs\",\"100 mbps\",\"200 mbps\",\"10\",\"11\"]" +
				"[\"3\",\"Revolu'tion\",\"" + String.format("%.2f", rand.nextFloat()).replace(',', '.') + "\",\"100GB\",\"" + (rand.nextBoolean() ? "Downloading" : "Seeding") + "\",\"\",\"100500 hrs\",\"100 mbps\",\"200 mbps\",\"10\",\"11\"]"+
				"[\"4\",\"охоеж! 'DVDRIP\",\"" + String.format("%.2f", rand.nextFloat()).replace(',', '.') + "\",\"100GB\",\"" + (rand.nextBoolean() ? "Downloading" : "Seeding") + "\",\"\",\"100500 hrs\",\"100 mbps\",\"200 mbps\",\"10\",\"11\"]" + 
				"[\"5\",\"Hunger_gam'es_catching_fire_720p_dvdrip.avi\",\"" + String.format("%.2f", rand.nextFloat()).replace(',', '.') + "\",\"100GB\",\"" + (rand.nextBoolean() ? "Downloading" : "Seeding") + "\",\"\",\"100500 hrs\",\"100 mbps\",\"200 mbps\",\"10\",\"11\"]" + 
				"]";
		return true;*/
		
		try{
			
			URL url = new URL(getListUrlString());
		    URLConnection con = (HttpURLConnection) url.openConnection();	    
		    con.addRequestProperty("Authorization", getAuthorizationString());		
			InputStream stream = con.getInputStream();
		    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
		    String temp;
		    while ((temp = bufferedReader.readLine()) != null)
		    	result.value += temp;
		    return true;
	        
		} catch (Exception e) {
			return false;
		}finally{
			
		}
	}
	
	public ArrayList<DownloadItem> getDownloadItems(){
		Holder<String> itemListString = new Holder<String>("");
		if (!tryGetItemList(itemListString))
			return null;
		return fillDownloadItems(itemListString.value);
	}

	public boolean sendGroupCommand(String command){
		try{
			
			URL url = new URL(String.format(sendGroupCommandUrlString(), command));
		    URLConnection con = (HttpURLConnection) url.openConnection();	    
		    con.addRequestProperty("Authorization", getAuthorizationString());
		    con.getInputStream();
			return true;
		} catch (Exception e) {
			return false;
		}		
	}	
	
	public boolean sendCommand(String command, String id){
		try{
			
			URL url = new URL(String.format(sendCommandUrlString(), command, id));
		    URLConnection con = (HttpURLConnection) url.openConnection();	    
		    con.addRequestProperty("Authorization", getAuthorizationString());
			con.getInputStream();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	

	
	public boolean sendFile(File file){
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
		    con.setRequestProperty("Authorization", getAuthorizationString());
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
		    if (respCode == 200)
		    	return true;

		} catch (Exception e) {
		    return false;
		}
		return false;

	}
	
	private static ArrayList<DownloadItem> fillDownloadItems(String data){
		
		ArrayList<DownloadItem> result = new ArrayList<DownloadItem>();
		
		Matcher m = Pattern.compile("\\[((\"[^,^\"]*\"),)*(\"[^,^\"]*\")\\]").matcher(data);
    	while (m.find()){
    	    String res = m.toMatchResult().group(0);
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
    	
    	return result;
    	
	}
	
}

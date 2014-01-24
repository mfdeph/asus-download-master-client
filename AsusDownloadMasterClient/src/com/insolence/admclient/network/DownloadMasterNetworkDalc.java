package com.insolence.admclient.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.insolence.admclient.entity.DownloadItem;
import com.insolence.admclient.storage.PreferenceAccessor;
import com.insolence.admclient.util.Holder;
import com.insolence.admclient.util.RandomGuid;
import com.insolence.admclient.util.FriendlyNameUtil;

import android.content.Context;
import android.net.Uri;
import android.util.Base64;

public class DownloadMasterNetworkDalc {
	
	private final PreferenceAccessor _preferences;	
	private final Context _context;
	
	public DownloadMasterNetworkDalc(Context context){
		_context = context;
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
	
	private String sendLinkUrlString(){
		return "http://" + getConnectionString() + "/dm_apply.cgi?action_mode=DM_ADD&usb_dm_url=%s&download_type=5&again=no";
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
		String groupCommandUrlPath = String.format(sendGroupCommandUrlString(), command);
		return sendGetRequest(groupCommandUrlPath);
	}	
	
	public boolean sendCommand(String command, String id){
		String commandUrlPath = String.format(sendCommandUrlString(), command, id);
		return sendGetRequest(commandUrlPath);
	}
	
	public boolean sendLink(String link){
		String commandUrlPath = String.format(sendLinkUrlString(), URLEncoder.encode(link));
		return sendGetRequest(commandUrlPath);
	}
	
	private boolean sendGetRequest(String urlPath){
		try{
			
			URL url = new URL(urlPath);
		    URLConnection con = (HttpURLConnection) url.openConnection();	    
		    con.addRequestProperty("Authorization", getAuthorizationString());
			con.getInputStream();
			return true;
		} catch (Exception e) {
			return false;
		}		
	}
	

	@Deprecated
	public boolean sendFile(File file){
		try{
			FileInputStream fis = new FileInputStream(file.getAbsolutePath());
			return sendFilePostRequest(file.getName(), fis);
		}catch(Exception e){
			return false;
		}
	}
	
	public boolean sendFile(Uri uri, String fileName){
		try{
			InputStream is = _context.getContentResolver().openInputStream(uri);
			return sendFilePostRequest(fileName, is);
		}catch(Exception e){
			return false;
		}
	}
	
	private static String newLine = "\r\n";
	private static int maxBufferSize = 4096;
	
	private boolean sendFilePostRequest(String fileName, InputStream bodyInputStream) throws MalformedURLException, IOException {
		
		String boundary = "---------------------------" + new RandomGuid().toString(13);	    
		    
		HttpURLConnection con = (HttpURLConnection) new URL(uploadFileUrlString()).openConnection();
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setUseCaches(false);
		con.setRequestMethod("POST");
		con.setRequestProperty("Connection", "Keep-Alive");
		con.setRequestProperty("Authorization", getAuthorizationString());
		con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
		    
		DataOutputStream dos = new DataOutputStream(con.getOutputStream());

		dos.writeBytes("--" + boundary + newLine);
		dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + fileName + "\"" + newLine);
		dos.writeBytes("Content-Type: application/x-bittorrent" + newLine + newLine);
		    
		int bytesRead;
		byte[] buffer = new byte[maxBufferSize];
		    
		while ((bytesRead = bodyInputStream.read(buffer)) > -1)
		    dos.write(buffer, 0, bytesRead);
		    
		dos.writeBytes(newLine);
		dos.writeBytes("--" + boundary + "--" + newLine);
		dos.flush();
		    
		int respCode = con.getResponseCode(); 
		
		return (respCode == 200);	
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

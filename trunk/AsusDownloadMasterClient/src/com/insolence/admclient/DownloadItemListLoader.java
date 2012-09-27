package com.insolence.admclient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Base64;


public class DownloadItemListLoader extends AsyncTaskLoader<ArrayList<DownloadItem>> {
	
	private Context _context;
	private ArrayList<DownloadItem> downloadItems;
	private String errorMsg = null;
	
	public DownloadItemListLoader(Context context) {
		super(context);
		_context = context;
	}
	
	private String getListUrlString(){
		return "http://" + DownloadItemListActivity._connectionString + "/dm_print_status.cgi?action_mode=All";
	}
	
	@Override
	public ArrayList<DownloadItem> loadInBackground() {
		errorMsg = null;
		ArrayList<DownloadItem> items = null;
		if (!DownloadItemListActivity._password.equals("password")) {
			try {
				URL url = new URL(getListUrlString());
				URLConnection con = (HttpURLConnection) url.openConnection();
				con.setConnectTimeout(30000);
				con.addRequestProperty("Authorization", "Basic " + Base64.encodeToString((DownloadItemListActivity._userName + ":" + DownloadItemListActivity._password).getBytes(), Base64.DEFAULT).trim());				
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(con.getInputStream())));
				String temp;
				String result = "";
				while ((temp = bufferedReader.readLine()) != null) {
					result += temp;
				}
				bufferedReader.close();
				
				if (!result.equals("")) {
					String[] itemSplit = result.split("\"\\],\\[\"");
					
					items = new ArrayList<DownloadItem>();
					DownloadItem item = null;
					for (int i=0; i<itemSplit.length; i++) {
						
						item = new DownloadItem();
						
						String[] fieldSplit  = itemSplit[i].split("\",\"");
						
						for (int j=0; j<fieldSplit.length; j++) {
							
							String splitChar = fieldSplit[j];
							
							switch (j) {
							case 0:
								String[] first = splitChar.split("\\[\"");
								if (first.length == 2) {
									item.setId(first[1]);
								}
								else {
									first = splitChar.split("\"");
									item.setId(first[0]);
								}
								break;
							case 1:
								item.setName(splitChar);
								break;
							case 2:
								if (splitChar.trim().equalsIgnoreCase(""))
									item.setPercentage(0);
								else
									item.setPercentage(Float.parseFloat(splitChar));
								break;
							case 3:
								item.setVolume(splitChar);
								break;
							case 4:
								item.setStatus(splitChar);
								break;
							case 5:
								item.setType(splitChar);
								break;
							case 6:
								item.setTimeOnLine(splitChar);
								break;
							case 7:
								item.setUpSpeed(splitChar);
								break;
							case 8:
								item.setDownSpeed(splitChar);
								break;
							case 9:
								item.setSeeds(splitChar);
								break;
							case 10:
								item.setAddInfo(splitChar);
								break;
							}
						}
						items.add(item);
					}
				}
			}
			catch (SocketTimeoutException e) {
				errorMsg = "Connection timeout. Verify your login credentials.";
			}
			catch (MalformedURLException e) {
				
			} 
			catch (IOException e) {
				errorMsg = "Connection error. Verify your network connectivity.";
			}
		}
		else {
			errorMsg = "Not logged in.";
		}	
		return items;
	}

	@Override 
	public void deliverResult(ArrayList<DownloadItem> items) {
		
		((DownloadItemListActivity) _context).setEmptyMsg(errorMsg);
		
		downloadItems = items;

		if (isStarted()) {
			// If the Loader is currently started, we can immediately
			// deliver its results.
			super.deliverResult(items);
		}

		// At this point we can release the resources associated with
		// 'oldApps' if needed; now that the new result is delivered we
		// know that it is no longer in use.
	}

	@Override 
	protected void onStartLoading() {
		if (downloadItems != null) {
			// If we currently have a result available, deliver it
			// immediately.
			deliverResult(downloadItems);
		}

		// Has something interesting in the configuration changed since we
		// last built the app list?

		if (takeContentChanged() || downloadItems == null) {
			// If the data has changed since the last time it was loaded
			// or is not currently available, start a load.
			forceLoad();
		}
	}

	@Override 
	protected void onStopLoading() {
		// Attempt to cancel the current load task if possible.
		cancelLoad();
	}

	@Override 
	public void onCanceled(ArrayList<DownloadItem> items) {
		super.onCanceled(items);

		// At this point we can release the resources associated with 'items'
		// if needed.
	}

	@Override 
	protected void onReset() {
		super.onReset();

		// Ensure the loader is stopped
		onStopLoading();

		// At this point we can release the resources associated with 'apps'
		// if needed.
		if (downloadItems != null) {
			downloadItems = null;
		}
	}
}
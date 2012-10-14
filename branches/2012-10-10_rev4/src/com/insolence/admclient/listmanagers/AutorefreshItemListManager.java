package com.insolence.admclient.listmanagers;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Handler;

import com.insolence.admclient.DownloadItem;
import com.insolence.admclient.ItemListRequestController;
import com.insolence.admclient.asynctasks.GetItemListAsyncTask;
import com.insolence.admclient.asynctasks.GetItemListResult;

public class AutorefreshItemListManager extends DownloadItemListManagerBase {

	private int _refreshInterval = 10;
	
	private IDisabler _disabler;
	
	public void SetDisabler(IDisabler disabler){
		_disabler = disabler;
	}
	
	public AutorefreshItemListManager(IProcessResultConsumer processResultConsumer, int refreshInterval){
		super(processResultConsumer);
		_refreshInterval = refreshInterval;
		h.post(myRunnable);
	}

	private Handler h = new Handler();
	
	IDownloadItemListManager manager = this;

	private Runnable myRunnable = new Runnable() {
	   public void run() {
		if (_disabler == null || _disabler.IsEnabled())
			new GetItemListAsyncTask(manager).execute();
		h.postDelayed(myRunnable, _refreshInterval * 1000);
	   }
	};


}

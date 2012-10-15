package com.insolence.admclient.listmanagers;

import android.os.Handler;


public class AutoRefreshItemListManager extends DownloadItemListManagerBase {

	private int _refreshInterval = 10;
	
	private IDisabler _disabler;
	
	public AutoRefreshItemListManager setDisabler(IDisabler disabler){
		_disabler = disabler;
		return this;
	}
	
	public AutoRefreshItemListManager(IProcessResultConsumer processResultConsumer, int refreshInterval){
		super(processResultConsumer);
		_refreshInterval = refreshInterval;
		h.post(myRunnable);
	}
	
	@Override
	protected void dispose(){
		super.dispose();
		h = null;
	}
	
	private Handler h = new Handler();
	
	DownloadItemListManagerBase manager = this;

	private Runnable myRunnable = new Runnable() {
	   public void run() {
		   if (h == null)
			   return;
		   if (_disabler == null || _disabler.IsEnabled())
			   manager.ExecuteItemListRequest();
			h.postDelayed(myRunnable, _refreshInterval * 1000);
	   }
	};


}

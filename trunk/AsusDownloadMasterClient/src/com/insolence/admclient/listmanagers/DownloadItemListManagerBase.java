/*package com.insolence.admclient.listmanagers;

import java.util.ArrayList;

import com.insolence.admclient.asynctasks.GetItemListTask;
import com.insolence.admclient.asynctasks.GetItemListResult;
import com.insolence.admclient.entity.DownloadItem;
import com.insolence.admclient.storage.DownloadItemStorage;

public abstract class DownloadItemListManagerBase implements IDownloadItemListManager{

	protected ArrayList<DownloadItem> DownloadItems;

	protected IProcessResultConsumer ProcessResultConsumer;
	
	protected DownloadItemListManagerBase(IProcessResultConsumer processResultConsumer){
		Actualize(processResultConsumer);
	}
	
	@Override
	public void Actualize(IProcessResultConsumer processResultConsumer){
		ProcessResultConsumer = processResultConsumer;
	}
	
	private boolean _locked = false;
	
	
	@Override
	public void postProcessResult(GetItemListResult result) {
		_locked = false;
		if (ProcessResultConsumer == null)
			return;
		if (result.isSucceed()){
			DownloadItems = result.getDownloadItems();
			DownloadItemStorage.getInstance().saveDownloadItems(result.getDownloadItems(), null);
			ProcessResultConsumer.showResult(DownloadItems);
		}else{
			ProcessResultConsumer.showErrorMessage(result.getMessage());
		}
		
	}
	
	public ArrayList<DownloadItem> getDownloadItems() {
		if (DownloadItems == null){			
			DownloadItems = new ArrayList<DownloadItem>();
			ExecuteItemListRequest();
		}
		return DownloadItems;
	}

	protected void dispose(){
		if (DownloadItems != null)
			DownloadItems.clear();
		DownloadItems = null;
		ProcessResultConsumer = null;
	}
	
	protected void ExecuteItemListRequest(){
		if (!_locked){
			_locked = true;
			new GetItemListTask(this).execute();
		}
	}
	
	public IDownloadItemListManager switchToNext(IDownloadItemListManager nextManager){
		((DownloadItemListManagerBase)nextManager).DownloadItems = new ArrayList<DownloadItem>(DownloadItems);
		dispose();
		return nextManager;
	}

}*/

